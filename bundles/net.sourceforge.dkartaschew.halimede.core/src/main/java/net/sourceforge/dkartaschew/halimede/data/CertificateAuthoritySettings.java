/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2018 Darran Kartaschew 
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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import net.sourceforge.dkartaschew.halimede.data.persistence.UUIDPersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

/**
 * The settings store for the given CA
 * 
 * @author darran
 *
 */
public class CertificateAuthoritySettings {

	/**
	 * The default name to use.
	 */
	public static final String DEFAULT_NAME = "configuration.xml";

	/**
	 * Store the certificate authority settings.
	 * 
	 * @param settings The instance to store
	 * @param filename The filename to store the settings to.
	 * @throws Exception If writing the information failed.
	 */
	public static void write(CertificateAuthoritySettings settings, Path filename) throws Exception {
		try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename.toFile())))) {
			// Add a specialised persistence delegate for UUIDs
			encoder.setPersistenceDelegate(UUID.class, new UUIDPersistenceDelegate());
			encoder.writeObject(settings);
		}
	}

	/**
	 * Read the certificate authority settings
	 * 
	 * @param filename The filename to read the settings from
	 * @return An instance of the settings.
	 * @throws Exception If reading/decoding the settings failed.
	 */
	public static CertificateAuthoritySettings read(Path filename) throws Exception {
		try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename.toFile())))) {
			return (CertificateAuthoritySettings) decoder.readObject();
		}
	}

	/**
	 * The description to utilise for the CA
	 */
	private String description;
	/**
	 * The backing PKCS12 file with all the keying material
	 */
	private String pkcs12Filename;
	/**
	 * The serial number for signing.
	 */
	private final AtomicLong serial = new AtomicLong(0);
	/**
	 * A unique ID for this node.
	 */
	private UUID uuid;
	/**
	 * The signature algorithm to use.
	 */
	private SignatureAlgorithm signatureAlg;
	/**
	 * The number of expiry Days.
	 */
	private int expiryDays;
	/**
	 * The CRL serial number.
	 */
	private AtomicLong crlSerial = new AtomicLong(1);

	/**
	 * Default constructor for java beans.
	 */
	public CertificateAuthoritySettings() {
	}

	/**
	 * Create a new ca settings with provided ID
	 * 
	 * @param uuid The ID.
	 */
	public CertificateAuthoritySettings(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Get the plain text description
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the CA's description
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the CA PKCS12 backing store file
	 * 
	 * @return The filename of the PKCS12 keying material file.
	 */
	public String getPkcs12Filename() {
		return pkcs12Filename;
	}

	/**
	 * Set the CA PKCS12 backing store file.
	 * 
	 * @param pkcs12Filename The filename of the PKCS12 keying material file.
	 */
	public void setPkcs12Filename(String pkcs12Filename) {
		this.pkcs12Filename = pkcs12Filename;
	}

	/**
	 * Get the current serial. (next in line)
	 * 
	 * @return the serial
	 */
	public long getSerial() {
		return serial.get();
	}

	/**
	 * Set the current serial. (next in line)
	 * 
	 * @param serial The next serial number to use.
	 */
	public void setSerial(long serial) {
		this.serial.set(serial);
	}

	/**
	 * Get the current crl serial. (next in line)
	 * 
	 * @return the serial
	 */
	public long getCRLSerial() {
		return crlSerial.get();
	}

	/**
	 * Set the current crl serial. (next in line)
	 * 
	 * @param serial The next serial number to use.
	 */
	public void setCRLSerial(long serial) {
		this.crlSerial.set(serial);
	}
	
	/**
	 * Get this CA's UUID
	 * 
	 * @return the CA UUID
	 */
	public UUID getUuid() {
		return uuid;
	}

	/**
	 * Set the CA UUID.
	 * 
	 * @param uuid the uuid to set
	 */
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	/**
	 * Get the signature algorithm
	 * 
	 * @return The signature algorithm
	 */
	public SignatureAlgorithm getSignatureAlgorithm() {
		return signatureAlg;
	}

	/**
	 * Set the signature algorithm
	 * 
	 * @param signatureAlg The signature algorithm
	 */
	public void setSignatureAlgorithm(SignatureAlgorithm signatureAlg) {
		this.signatureAlg = signatureAlg;
	}

	/**
	 * Get the serial number to utilise.
	 * 
	 * @return The serial number to use for signing.
	 */
	public long getAndIncrementSerial() {
		return serial.getAndIncrement();
	}

	/**
	 * Get the CRL serial number to utilise.
	 * 
	 * @return The CRL serial number to use for signing.
	 */
	public long getAndIncrementCRLSerial() {
		return crlSerial.getAndIncrement();
	}
	
	/**
	 * Get the default days for expiry from start date
	 * 
	 * @return The default expiry
	 */
	public int getExpiryDays() {
		return expiryDays;
	}

	/**
	 * Get the default days for expiry from start date
	 * 
	 * @param expiryDays The default expiry
	 */
	public void setExpiryDays(int expiryDays) {
		this.expiryDays = expiryDays;
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
		// Don't include description, sigAlg and expiry days in hashcode.
		// (these can change in the life of the object instance).
		result = prime * result + ((pkcs12Filename == null) ? 0 : pkcs12Filename.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		
		// Don't include description, sigAlg and expiry days in equals 
		// (these can change in the life of the object instance).
		CertificateAuthoritySettings other = (CertificateAuthoritySettings) obj;
		if (pkcs12Filename == null) {
			if (other.pkcs12Filename != null)
				return false;
		} else if (!pkcs12Filename.equals(other.pkcs12Filename))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
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
		if(description == null && uuid == null) {
			return super.toString();
		}
		if (description == null) {
			return "[" + uuid.toString() + "]";
		}
		if (uuid != null) {
			return description + " [" + uuid.toString() + "]";
		}
		return description;
	}

}
