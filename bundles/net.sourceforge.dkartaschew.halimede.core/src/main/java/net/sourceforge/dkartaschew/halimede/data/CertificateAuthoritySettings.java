/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
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
import java.math.BigInteger;
import java.nio.file.Path;
import java.util.UUID;

import net.sourceforge.dkartaschew.halimede.data.persistence.BigIntegerPersistanceDelegate;
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
			encoder.setPersistenceDelegate(BigInteger.class, new BigIntegerPersistanceDelegate());
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
	private BigInteger serial;
	/**
	 * Incremental Serial denotes if we are using incremental serial (true) or timestamp (false) as the serial.
	 * <p>
	 * Default is TRUE.
	 */
	private boolean incrementalSerial = true;
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
	private BigInteger crlSerial;
	/**
	 * Enable the log.
	 */
	private boolean enableLog;

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
		this.crlSerial = BigInteger.ONE;
		this.serial = BigInteger.ONE;
		this.enableLog = true;
		this.incrementalSerial = true;
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
	public BigInteger getSerial() {
		return serial;
	}

	/**
	 * Set the current serial. (next in line)
	 * 
	 * @param serial The next serial number to use.
	 */
	public void setSerial(long serial) {
		setSerial(BigInteger.valueOf(serial));
	}

	/**
	 * Set the current serial. (next in line)
	 * 
	 * @param serial The next serial number to use.
	 */
	public void setSerial(BigInteger serial) {
		if (serial == null) {
			return;
		}
		if (this.serial != null) {
			// confirm the new serial is greater than current.
			if (this.serial.compareTo(serial) > 0) {
				return; // new serial is less than current, so ignore.
			}
		} else if (serial.compareTo(BigInteger.ZERO) < 0) {
			// The new serial is negative?
			serial = BigInteger.ONE;
		}
		this.serial = serial;
	}

	/**
	 * Get the current crl serial. (next in line)
	 * 
	 * @return the serial
	 */
	public BigInteger getCRLSerial() {
		/*
		 * Do not add in code to return a valid value, allow return of null to allow proper XML serialise as JavaBean.
		 */
		return crlSerial;
	}

	/**
	 * Set the current crl serial. (next in line)
	 * 
	 * @param serial The next serial number to use.
	 */
	public void setCRLSerial(BigInteger serial) {
		if (serial == null) {
			return;
		}
		if (crlSerial != null) {
			// confirm the new serial is greater than current.
			if (crlSerial.compareTo(serial) > 0) {
				return; // new serial is less than current, so ignore.
			}
		} else if (serial.compareTo(BigInteger.ZERO) < 0) {
			// The new serial is negative?
			serial = BigInteger.ONE;
		}
		this.crlSerial = serial;
	}

	/**
	 * Set the current crl serial. (next in line)
	 * 
	 * @param serial The next serial number to use.
	 */
	public void setCRLSerial(long serial) {
		setCRLSerial(BigInteger.valueOf(serial));
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
	public BigInteger getAndIncrementSerial() {
		if (serial == null) {
			serial = BigInteger.ONE;
		}
		if (incrementalSerial) {
			// Perform simple increment.
			BigInteger oldSerial = serial;
			serial = serial.add(BigInteger.ONE);
			return oldSerial;
		} else {
			/*
			 * Get the current timestamp.
			 */
			BigInteger time = BigInteger.valueOf(System.currentTimeMillis());
			BigInteger current = serial;
			/*
			 * If the current timestamp is less than or equal the current serial, offer a 1 increment of current serial,
			 * otherwise offer timestamp. This should guard against clock drift/changes, especially if the clock is
			 * rewound. This will also guard against cases when System.currentTimeMillis() returns a negative value.
			 * This basically ensures the serial is only ever incremented in value. (we should never generate a value
			 * that is less than a value that has already been offered).
			 */
			if (time.compareTo(current) <= 0) {
				BigInteger oldSerial = serial;
				serial = serial.add(BigInteger.ONE);
				return oldSerial;
			} else {
				serial = time;
				return serial;
			}
		}
	}

	/**
	 * Get the CRL serial number to utilise.
	 * 
	 * @return The CRL serial number to use for signing.
	 */
	public BigInteger getAndIncrementCRLSerial() {
		if (crlSerial == null) {
			crlSerial = BigInteger.ONE;
		}
		BigInteger oldSerial = crlSerial;
		crlSerial = crlSerial.add(BigInteger.ONE);
		return oldSerial;
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

	/**
	 * Is Incremental serial number generation set.
	 * 
	 * @return TRUE if incremental serial number generation is set.
	 */
	public boolean isIncrementalSerial() {
		return incrementalSerial;
	}

	/**
	 * Set incremental serial number generation
	 * 
	 * @param incrementalSerial TRUE to enable incremental serial numbers or FALSE for serial number to be timestamp.
	 */
	public void setIncrementalSerial(boolean incrementalSerial) {
		this.incrementalSerial = incrementalSerial;
	}
	
	/**
	 * Is the log for the CA enabled.
	 * @return TRUE if the log for the CA is enabled.
	 */
	public boolean isEnableLog() {
		return enableLog;
	}

	/**
	 * Set the log for the CA to be enabled/disabled.
	 * @param enable TRUE to enable the CA log.
	 */
	public void setEnableLog(boolean enable) {
		this.enableLog = enable;
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
		if (description == null && uuid == null) {
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
