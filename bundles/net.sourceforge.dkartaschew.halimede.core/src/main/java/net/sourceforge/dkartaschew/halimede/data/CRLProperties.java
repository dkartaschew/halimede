/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2019 Darran Kartaschew 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 */

package net.sourceforge.dkartaschew.halimede.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509CRL;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;

import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CRLHolder;

import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

/**
 * CRL Properties holder.
 */
public class CRLProperties implements Comparable<CRLProperties>, Comparator<CRLProperties> {

	/**
	 * Create and populate a CRLProperties instance.
	 * 
	 * @param ca The Certificate Authorithy
	 * @param file The File to load
	 * @return A CRLProperties instance
	 * @throws IOException If reading the file failed.
	 */
	public static CRLProperties create(CertificateAuthority ca, Path file) throws IOException {
		CRLProperties p = new CRLProperties(ca);
		try (FileInputStream in = new FileInputStream(file.toFile())) {
			p.load(in);
		}
		return p;
	}

	/**
	 * The CA which this certificate is tied to.
	 */
	private final CertificateAuthority ca;
	/**
	 * The issued crl.
	 */
	private X509CRL crl;

	/**
	 * The default extension of the properties.
	 */
	public final static String DEFAULT_EXTENSION = ".crlprop";

	/**
	 * Default Keys for the property list.
	 */
	public enum Key {
		/**
		 * The issuer X500 Name
		 */
		issuer,
		/**
		 * The Filename of the property file.
		 */
		filename,
		/**
		 * The Filename of the CRL datastore.
		 */
		crlFilename,
		/**
		 * The CRL issued date.
		 */
		issueDate,
		/**
		 * The next expected CRL date
		 */
		nextExpectedDate,
		/**
		 * CRL Serial Number
		 */
		crlSerialNumber,
		/**
		 * Comments
		 */
		comments
	}

	/**
	 * The properties store
	 */
	private final Properties properties;

	/**
	 * Create an empty properties store.
	 * 
	 * @param ca The CA that this issued certificate is tied to.
	 */
	public CRLProperties(CertificateAuthority ca) {
		this.ca = ca;
		properties = new Properties();
	}

	/**
	 * Create an empty properties store.
	 * 
	 * @param ca The CA that this CRL is tied to.
	 * @param crl Preloaded CRL.
	 */
	public CRLProperties(CertificateAuthority ca, X509CRL crl) {
		this.ca = ca;
		this.crl = crl;
		properties = new Properties();
		
		if (ca == null) {
			setProperty(Key.issuer, crl.getIssuerX500Principal().getName());
			setProperty(Key.issueDate, DateTimeUtil.toString(crl.getThisUpdate()));
			setProperty(Key.nextExpectedDate, DateTimeUtil.toString(crl.getNextUpdate()));
			try {
				X509CRLHolder holder = new X509CRLHolder(crl.getEncoded());
				BigInteger serial = BigInteger.ZERO;
				if (holder.hasExtensions()) {
					Extension ext = holder.getExtension(Extension.cRLNumber);
					if (ext != null) {
						CRLNumber serialn = CRLNumber.getInstance(ext.getParsedValue());
						serial = serialn.getCRLNumber();
					}
				}
				setProperty(Key.crlSerialNumber, serial.toString());
			} catch (Throwable e) {
				setProperty(Key.crlSerialNumber, BigInteger.ZERO.toString());
			}
		}
	}

	/**
	 * Get the given value for the key
	 * 
	 * @param key The key to enquire
	 * @return The set value, or NULL if not present/set.
	 */
	public String getProperty(Key key) {
		return properties.getProperty(key.name());
	}

	/**
	 * Set the given property
	 * 
	 * @param key The key to set
	 * @param value The value to set. (NULL to remove)
	 * @return The prior value.
	 */
	public Object setProperty(Key key, String value) {
		if(value == null) {
			Object oldValue = getProperty(key);
			properties.remove(key.name());
			return oldValue;
		}
		return properties.setProperty(key.name(), value);
	}

	/**
	 * Load the properties from the given stream
	 * 
	 * @param stream The stream to read the properties from
	 * @throws IOException If the load failed.
	 */
	public synchronized void load(InputStream stream) throws IOException {
		clearCRL();
		properties.clear();
		properties.load(stream);
	}

	/**
	 * Store the properties for the given stream
	 * 
	 * @param stream The stream to write to
	 * @throws IOException If the store failed.
	 */
	public synchronized void store(OutputStream stream) throws IOException {
		properties.store(stream, "CRLProperties");
	}

	/**
	 * Reset any loaded X509CRL.
	 */
	public synchronized void clearCRL() {
		crl = null;
	}

	/**
	 * Have we loaded the X509CRL?
	 * 
	 * @return TRUE if we have an instance of the X509CRL.
	 */
	public synchronized boolean hasCRL() {
		return crl != null;
	}

	/**
	 * Load the defined X509CRL.
	 * 
	 * @return The loaded X509CRL Instance
	 * @throws IOException The X509CRL file doesn't exist or can't be read.
	 */
	public synchronized X509CRL getCRL() throws IOException {
		// If already loaded, just return
		if (crl != null) {
			return crl;
		}
		// Locate and load. (The value store in the property field is the plain filename, not the full path).
		if (getProperty(Key.crlFilename) != null) {
			Path filename = findFile(Paths.get(getProperty(Key.crlFilename)));
			crl = X509CRLEncoder.open(filename);
		}
		return crl;
	}

	/**
	 * Attempt to locate the file within the CAs domain.
	 * 
	 * @param filename The filename of the X509CRL file.
	 * @return The absolute filename for the X509CRL file.
	 */
	private Path findFile(Path filename) {
		// Locate the PKCS12 file. (What is stored here is relative not absolute).
		Iterator<Path> searchPaths = ca.getSearchPaths();
		while (searchPaths.hasNext()) {
			Path file = searchPaths.next().resolve(filename);
			if (Files.exists(file) && Files.isReadable(file) && Files.isRegularFile(file)) {
				return file;
			}
		}
		// Not found? return the input.
		return filename;
	}

	/**
	 * Get the CA this certificate is tied to.
	 * 
	 * @return The CA this certificate is tied to.
	 */
	public CertificateAuthority getCertificateAuthority() {
		return ca;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CRLProperties other = (CRLProperties) obj;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String v = getProperty(Key.crlSerialNumber);
		if (v != null) {
			return "CRL: " + v;
		}
		return "CRL: " + hashCode();
	}

	@Override
	public int compare(CRLProperties o1, CRLProperties o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 == null) {
			return -1;
		}
		if (o2 == null) {
			return 1;
		}
		return o1.compareTo(o2);
	}

	@Override
	public int compareTo(CRLProperties o) {
		if (o == null) {
			return -1;
		}
		int res = 0;
		if (o.getProperty(Key.crlSerialNumber) != null && this.getProperty(Key.crlSerialNumber) != null) {
			res = new BigInteger(this.getProperty(Key.crlSerialNumber))
					.compareTo(new BigInteger(o.getProperty(Key.crlSerialNumber)));
			if (res != 0) {
				return res;
			}
		}
		if (o.getProperty(Key.issueDate) != null && this.getProperty(Key.issueDate) != null) {
			ZonedDateTime tS = DateTimeUtil.toZonedDateTime(this.getProperty(Key.issueDate));
			ZonedDateTime oS = DateTimeUtil.toZonedDateTime(o.getProperty(Key.issueDate));
			res = tS.compareTo(oS);
			if (res != 0) {
				return res;
			}
		}
		if (o.getProperty(Key.nextExpectedDate) != null && this.getProperty(Key.nextExpectedDate) != null) {
			ZonedDateTime tS = DateTimeUtil.toZonedDateTime(this.getProperty(Key.nextExpectedDate));
			ZonedDateTime oS = DateTimeUtil.toZonedDateTime(o.getProperty(Key.nextExpectedDate));
			res = tS.compareTo(oS);
		}
		return res;
	}
}
