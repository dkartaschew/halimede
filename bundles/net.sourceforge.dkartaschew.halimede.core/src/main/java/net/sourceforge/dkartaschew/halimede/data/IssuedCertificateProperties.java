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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Properties;

import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

/**
 * Holder for Issued Certificate Properties
 */
public class IssuedCertificateProperties
		implements Comparable<IssuedCertificateProperties>, Comparator<IssuedCertificateProperties> {

	/**
	 * Create and populate a IssuedCertificateProperties instance.
	 * 
	 * @param ca The Certificate Authority
	 * @param file The File to load
	 * @return A IssuedCertificateProperties instance
	 * @throws IOException If reading the file failed.
	 */
	public static IssuedCertificateProperties create(CertificateAuthority ca, Path file) throws IOException {
		IssuedCertificateProperties p = new IssuedCertificateProperties(ca);
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
	 * The issued certificate.
	 */
	private IIssuedCertificate issuedCertificate;



	/**
	 * The default extension of the properties.
	 */
	public final static String DEFAULT_EXTENSION = ".prop";

	/**
	 * Default Keys for the property list.
	 */
	public enum Key {
		/**
		 * The description of the stored information
		 */
		description,
		/**
		 * The Filename of the property file.
		 */
		filename,
		/**
		 * The Filename of the PKCS12 datastore. (Certificate chain + keys)
		 */
		pkcs12store,
		/**
		 * The Filename of the PKCS7 datastore. (Certificate chain only)
		 */
		pkcs7store,
		/**
		 * The subject of the certificate
		 */
		subject,
		/**
		 * The certificate start date.
		 */
		startDate,
		/**
		 * The certificate end date
		 */
		endDate,
		/**
		 * The keying material type.
		 */
		keyType,
		/**
		 * General comments.
		 */
		comments,
		/**
		 * Revoke date
		 */
		revokeDate,
		/**
		 * The creation date.
		 */
		creationDate, 
		/**
		 * The reason for revocation.
		 */
		revokeCode,
		/**
		 * Certificate Serial Number
		 */
		certificateSerialNumber
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
	public IssuedCertificateProperties(CertificateAuthority ca) {
		this.ca = ca;
		properties = new Properties();
	}

	/**
	 * Create an empty properties store.
	 * 
	 * @param ca The CA that this issued certificate is tied to.
	 * @param issuedCertificate Preloaded certificate.
	 */
	public IssuedCertificateProperties(CertificateAuthority ca, IIssuedCertificate issuedCertificate) {
		this.ca = ca;
		this.issuedCertificate = issuedCertificate;
		properties = new Properties();
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
		clearIssuedCertificate();
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
		properties.store(stream, "IssuedCertificateProperties");
	}

	/**
	 * Load the defined certificate.
	 * 
	 * @param password The password required to open the certificate.
	 * @return The loaded Issued Certificate Instance
	 * @throws IOException The PKCS12/PKCS7 file doesn't exist or can't be read.
	 * @throws InvalidPasswordException The password provided is invalid.
	 * @throws KeyStoreException The PKCS12 container failed to load.
	 */
	public synchronized IIssuedCertificate loadIssuedCertificate(String password)
			throws KeyStoreException, InvalidPasswordException, IOException {
		// If already loaded, just return
		if (issuedCertificate != null) {
			return issuedCertificate;
		}
		// Locate and load. (The value store in the property field is the plain filename, not the full path).
		if (getProperty(Key.pkcs12store) != null) {
			Path filename = findFile(Paths.get(getProperty(Key.pkcs12store)));
			issuedCertificate = IssuedCertificate.openPKCS12(filename, password);
		} else {
			Path filename = findFile(Paths.get(getProperty(Key.pkcs7store)));
			issuedCertificate = IssuedCertificate.openPKCS7(filename);
		}
		return issuedCertificate;
	}

	/**
	 * Attempt to locate the file within the CAs domain.
	 * 
	 * @param filename The filename of the PKCS12 file.
	 * @return The absolute filename for the PKCS12 file.
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
	 * Reset any loaded issued certificate.
	 */
	public synchronized void clearIssuedCertificate() {
		issuedCertificate = null;
	}

	/**
	 * Have we loaded the issued certificate?
	 * 
	 * @return TRUE if we have an instance of the issued certificate.
	 */
	public synchronized boolean hasIssuedCertificate() {
		return issuedCertificate != null;
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
		IssuedCertificateProperties other = (IssuedCertificateProperties) obj;
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
		String v = getProperty(Key.description);
		if (v != null) {
			return v;
		}
		return "Issued Certificate Properties: " + hashCode();
	}

	@Override
	public int compare(IssuedCertificateProperties o1, IssuedCertificateProperties o2) {
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
	public int compareTo(IssuedCertificateProperties o) {
		if (o == null) {
			return 1;
		}
		int res = 0;
		if (o.getProperty(Key.description) != null && this.getProperty(Key.description) != null) {
			res = this.getProperty(Key.description).compareTo(o.getProperty(Key.description));
			if (res != 0) {
				return res;
			}
		}
		if (o.getProperty(Key.subject) != null && this.getProperty(Key.subject) != null) {
			res = this.getProperty(Key.subject).compareTo(o.getProperty(Key.subject));
			if (res != 0) {
				return res;
			}
		}
		if (o.getProperty(Key.startDate) != null && this.getProperty(Key.startDate) != null) {
			ZonedDateTime tS = DateTimeUtil.toZonedDateTime(this.getProperty(Key.startDate));
			ZonedDateTime oS = DateTimeUtil.toZonedDateTime(o.getProperty(Key.startDate));
			res = tS.compareTo(oS);
			if (res != 0) {
				return res;
			}
		}
		if (o.getProperty(Key.endDate) != null && this.getProperty(Key.endDate) != null) {
			ZonedDateTime tS = DateTimeUtil.toZonedDateTime(this.getProperty(Key.endDate));
			ZonedDateTime oS = DateTimeUtil.toZonedDateTime(o.getProperty(Key.endDate));
			res = tS.compareTo(oS);
		}
		return res;
	}
}
