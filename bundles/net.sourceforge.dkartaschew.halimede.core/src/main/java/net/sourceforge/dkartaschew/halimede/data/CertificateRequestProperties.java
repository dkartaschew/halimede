/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2021 Darran Kartaschew 
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;

import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

/**
 * Certificate Request Properties holder.
 */
public class CertificateRequestProperties implements Comparable<CertificateRequestProperties>, Comparator<CertificateRequestProperties> {

	/**
	 * Create and populate a CRLProperties instance.
	 * 
	 * @param ca The Certificate Authorithy
	 * @param file The File to load
	 * @return A CRLProperties instance
	 * @throws IOException If reading the file failed.
	 */
	public static CertificateRequestProperties create(CertificateAuthority ca, Path file) throws IOException {
		CertificateRequestProperties p = new CertificateRequestProperties(ca);
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
	 * The request.
	 */
	private ICertificateRequest request;

	/**
	 * The default extension of the properties.
	 */
	public final static String DEFAULT_EXTENSION = ".csrprop";

	/**
	 * Default Keys for the property list.
	 */
	public enum Key {
		/**
		 * The Filename of the property file.
		 */
		filename,
		/**
		 * The Filename of the CSR datastore.
		 */
		csrFilename,
		/**
		 * The certificate request subject.
		 */
		subject,
		/**
		 * The import date
		 */
		importDate,
		/**
		 * Comments
		 */
		comments, 
		/**
		 * The PKI key type.
		 */
		keyType
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
	public CertificateRequestProperties(CertificateAuthority ca) {
		this.ca = ca;
		properties = new Properties();
	}

	/**
	 * Create an empty properties store.
	 * 
	 * @param ca The CA that this CRL is tied to.
	 * @param request The preloaded request.
	 */
	public CertificateRequestProperties(CertificateAuthority ca, ICertificateRequest request) {
		this.ca = ca;
		this.request = request;
		properties = new Properties();
		if(ca == null) {
			setProperty(CertificateRequestProperties.Key.subject, //
					request.getSubject().toString());
			setProperty(CertificateRequestProperties.Key.importDate, //
					DateTimeUtil.toString(ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE)));
			try {
				setProperty(CertificateRequestProperties.Key.keyType, //
						KeyPairFactory.forKeyInformation(request.getSubjectPublicKeyInfo()).name());
			} catch (IOException e) {
				// ignore for foreign type that are not native for us.
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
		clearRequest();
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
		properties.store(stream, "CertificateRequestProperties");
	}

	/**
	 * Reset any loaded CSR.
	 */
	public synchronized void clearRequest() {
		request = null;
	}

	/**
	 * Have we loaded the request?
	 * 
	 * @return TRUE if we have an instance of the CSR.
	 */
	public synchronized boolean hasRequest() {
		return request != null;
	}

	/**
	 * Load the defined X509CRL.
	 * 
	 * @return The loaded X509CRL Instance
	 * @throws IOException The X509CRL file doesn't exist or can't be read.
	 */
	public synchronized ICertificateRequest getCertificateRequest() throws IOException {
		// If already loaded, just return
		if (request != null) {
			return request;
		}
		// Locate and load. (The value store in the property field is the plain filename, not the full path).
		if (getProperty(Key.csrFilename) != null) {
			Path filename = findFile(Paths.get(getProperty(Key.csrFilename)));
			request = CertificateRequestPKCS10.create(filename);
		}
		return request;
	}

	/**
	 * Attempt to locate the file within the CAs domain.
	 * 
	 * @param filename The filename of the CSR file.
	 * @return The absolute filename for the CSR file.
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
		CertificateRequestProperties other = (CertificateRequestProperties) obj;
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
		String v = getProperty(Key.subject);
		if (v != null) {
			return "CSR: " + v;
		}
		return "CSR: " + hashCode();
	}

	@Override
	public int compare(CertificateRequestProperties o1, CertificateRequestProperties o2) {
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
	public int compareTo(CertificateRequestProperties o) {
		if (o == null) {
			return -1;
		}
		int res = 0;
		if (o.getProperty(Key.subject) != null && this.getProperty(Key.subject) != null) {
			res = this.getProperty(Key.subject).compareTo(o.getProperty(Key.subject));
			if (res != 0) {
				return res;
			}
		}
		if (o.getProperty(Key.importDate) != null && this.getProperty(Key.importDate) != null) {
			ZonedDateTime tS = DateTimeUtil.toZonedDateTime(this.getProperty(Key.importDate));
			ZonedDateTime oS = DateTimeUtil.toZonedDateTime(o.getProperty(Key.importDate));
			res = tS.compareTo(oS);
		}
		return res;
	}
}
