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

package net.sourceforge.dkartaschew.halimede.data.impl;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Comparator;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;

import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.persistence.ByteArrayPersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.data.persistence.ExtendedKeyUsagePersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.data.persistence.GeneralNamesPersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.data.persistence.KeyUsagePersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.data.persistence.X500NamePersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.data.persistence.ZonedDateTimePersistenceDelegate;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

public class CertificateKeyPairTemplate implements ICertificateKeyPairTemplate, Comparable<CertificateKeyPairTemplate>,
		Comparator<CertificateKeyPairTemplate> {

	/**
	 * Store the certificate authority settings.
	 * 
	 * @param settings The instance to store
	 * @param filename The filename to store the settings to.
	 * @throws Exception If writing the information failed.
	 */
	public static void write(CertificateKeyPairTemplate settings, Path filename) throws Exception {
		try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filename.toFile())))) {

			// Add a specialised persistence delegate for ZonedDateTime, etc
			encoder.setPersistenceDelegate(ZonedDateTime.class, new ZonedDateTimePersistenceDelegate());
			encoder.setPersistenceDelegate(X500Name.class, new X500NamePersistenceDelegate());
			encoder.setPersistenceDelegate(KeyUsage.class, new KeyUsagePersistenceDelegate());
			encoder.setPersistenceDelegate(KeyPurposeId.class, new ExtendedKeyUsagePersistenceDelegate());
			encoder.setPersistenceDelegate(GeneralNames.class, new GeneralNamesPersistenceDelegate());

			// Use a specialised byte[] encoder. (encode as Base64 string).
			encoder.setPersistenceDelegate(byte[].class, new ByteArrayPersistenceDelegate());
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
	public static CertificateKeyPairTemplate read(Path filename) throws Exception {
		try (XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename.toFile())))) {
			return (CertificateKeyPairTemplate) decoder.readObject();
		}
	}

	/**
	 * Comparator for comparison of templates.
	 */
	private final static Comparator<CertificateKeyPairTemplate> comparator = Comparator//
			.comparing(CertificateKeyPairTemplate::getDescription, //
					Comparator.nullsLast(String::compareTo))//
			.thenComparing(CertificateKeyPairTemplate::getSubject, //
					Comparator.nullsLast((o1, o2) -> o1.toString().compareTo(o2.toString())));
	
	/**
	 * The request subject name
	 */
	private X500Name subject;

	/**
	 * The requested Key Type to generate.
	 */
	private KeyType keyType;

	/**
	 * The defined key usage.
	 */
	private KeyUsage keyUsage;

	/**
	 * The defined extended key usage.
	 */
	private Collection<KeyPurposeId> extendedKeyUsage;

	/**
	 * The defined subject alternate names.
	 */
	private GeneralNames subjectAltNames;

	/**
	 * Flag to indicate if this is a CA request.
	 */
	private boolean isCARequest = false;

	/**
	 * CRL Location
	 */
	private String crlLocation;

	/**
	 * CRL Issuer
	 */
	private X500Name crlIssuer;

	/**
	 * Certificate request description.
	 */
	private String description;

	/**
	 * The creation date of the request.
	 */
	private ZonedDateTime creationDate;

	/**
	 * Create a new template.
	 */
	public CertificateKeyPairTemplate() {
	}

	/**
	 * Get the X500 Subject Name
	 * 
	 * @return the subject
	 */
	public X500Name getSubject() {
		return subject;
	}

	/**
	 * Set the X500 Subject Name
	 * 
	 * @param subject the subject to set
	 */
	public void setSubject(X500Name subject) {
		this.subject = subject;
	}

	/**
	 * Get the KeyType
	 * 
	 * @return the keyType
	 */
	public KeyType getKeyType() {
		return keyType;
	}

	/**
	 * Set the KeyType
	 * 
	 * @param keyType the keyType to set
	 */
	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	/**
	 * @return the keyUsage
	 */
	public KeyUsage getKeyUsage() {
		return keyUsage;
	}

	/**
	 * @param keyUsage the keyUsage to set
	 */
	public void setKeyUsage(KeyUsage keyUsage) {
		this.keyUsage = keyUsage;
	}

	/**
	 * @return the extendedKeyUsage
	 */
	public Collection<KeyPurposeId> getExtendedKeyUsage() {
		return extendedKeyUsage;
	}

	/**
	 * Get the extended key usage as ASN1 vector
	 * 
	 * @return The exteneded keyusage as a ASN1 encodeded vector.
	 */
	public ASN1EncodableVector getExtendedKeyUsageVector() {
		if(extendedKeyUsage == null) {
			return null;
		}
		ASN1EncodableVector v = new ASN1EncodableVector();
		extendedKeyUsage.stream().forEach(v::add);
		return v;
	}

	/**
	 * @param extendedKeyUsage the extendedKeyUsage to set
	 */
	public void setExtendedKeyUsage(Collection<KeyPurposeId> extendedKeyUsage) {
		this.extendedKeyUsage = extendedKeyUsage;
	}

	/**
	 * @return the subjectAltNames
	 */
	public GeneralNames getSubjectAltNames() {
		return subjectAltNames;
	}

	/**
	 * @param subjectAltNames the subjectAltNames to set
	 */
	public void setSubjectAltNames(GeneralNames subjectAltNames) {
		this.subjectAltNames = subjectAltNames;
	}

	/**
	 * @return the certificatePolicies
	 */
	public ASN1Encodable getCertificatePolicies() {
		return null; // Not supported
	}

	/**
	 * @return the isCARequest
	 */
	public boolean isCARequest() {
		return isCARequest;
	}

	/**
	 * @param isCARequest the isCARequest to set
	 */
	public void setCARequest(boolean isCARequest) {
		this.isCARequest = isCARequest;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the creationDate
	 */
	public ZonedDateTime getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(ZonedDateTime creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Get the CRL distribution point.
	 * 
	 * @return The CRL distribution point.
	 */
	public String getCrlLocation() {
		return crlLocation;
	}

	/**
	 * Set the CRL distribution point.
	 * 
	 * @param crlLocation The CRL distribution point.
	 */
	public void setCrlLocation(String crlLocation) {
		this.crlLocation = crlLocation;
	}

	/**
	 * Get the CRL distribution point.
	 * 
	 * @return The CRL distribution point.
	 */
	public X500Name getCrlIssuer() {
		return crlIssuer;
	}

	/**
	 * Set the CRL distribution point issuer
	 * 
	 * @param crlIssuer The CRL distribution point issuer.
	 */
	public void setCrlIssuer(X500Name crlIssuer) {
		this.crlIssuer = crlIssuer;
	}

	@Override
	public String toString() {
		if (description != null) {
			return description;
		}
		if (subject != null) {
			return subject.toString();
		}
		return "Empty Certificate Template";
	}

	@Override
	public int compareTo(CertificateKeyPairTemplate o) {
		if (o == null) {
			return 1;
		}
		return comparator.compare(this, o);
	}

	@Override
	public int compare(CertificateKeyPairTemplate o1, CertificateKeyPairTemplate o2) {
		if (o1 == null && o2 == null) {
			return 0;
		}
		if (o1 == null) {
			return -1;
		}
		return o1.compareTo(o2);
	}

	@Override
	public void store(Path filename) throws Exception {
		write(this, filename);
	}

	@Override
	public ICertificateRequest asCertificateRequest() {
		CertificateRequest request = new CertificateRequest();
		request.setSubject(subject);
		request.setKeyType(keyType);
		request.setDescription(description);
		request.setKeyUsage(keyUsage);
		ASN1EncodableVector vector = new ASN1EncodableVector();
		if (extendedKeyUsage != null) {
			for (KeyPurposeId id : extendedKeyUsage) {
				vector.add(id);
			}
		}
		request.setExtendedKeyUsage(vector);
		request.setSubjectAlternativeName(subjectAltNames);
		request.setcARequest(isCARequest);
		request.setCreationDate(creationDate);
		request.setCrlLocation(crlLocation);
		request.setCrlIssuer(crlIssuer);
		return request;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((crlIssuer == null) ? 0 : crlIssuer.hashCode());
		result = prime * result + ((crlLocation == null) ? 0 : crlLocation.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((extendedKeyUsage == null) ? 0 : extendedKeyUsage.hashCode());
		result = prime * result + (isCARequest ? 1231 : 1237);
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((keyUsage == null) ? 0 : keyUsage.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((subjectAltNames == null) ? 0 : subjectAltNames.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CertificateKeyPairTemplate other = (CertificateKeyPairTemplate) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (crlIssuer == null) {
			if (other.crlIssuer != null)
				return false;
		} else if (!crlIssuer.equals(other.crlIssuer))
			return false;
		if (crlLocation == null) {
			if (other.crlLocation != null)
				return false;
		} else if (!crlLocation.equals(other.crlLocation))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (extendedKeyUsage == null) {
			if (other.extendedKeyUsage != null)
				return false;
		} else if (!extendedKeyUsage.equals(other.extendedKeyUsage))
			return false;
		if (isCARequest != other.isCARequest)
			return false;
		if (keyType != other.keyType)
			return false;
		if (keyUsage == null) {
			if (other.keyUsage != null)
				return false;
		} else if (!keyUsage.equals(other.keyUsage))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (subjectAltNames == null) {
			if (other.subjectAltNames != null)
				return false;
		} else if (!subjectAltNames.equals(other.subjectAltNames))
			return false;
		return true;
	}

}
