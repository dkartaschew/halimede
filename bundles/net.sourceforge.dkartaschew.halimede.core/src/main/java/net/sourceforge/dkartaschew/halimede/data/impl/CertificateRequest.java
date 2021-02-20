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

import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

/**
 * Basic Certificate Request Holder.
 * <p>
 * Note: this comparator imposes orderings that are inconsistent with equals.
 */
public class CertificateRequest implements ICertificateRequest, ICertificateKeyPairTemplate,
		Comparable<CertificateRequest>, Comparator<CertificateRequest> {

	/**
	 * The request subject name
	 */
	private X500Name subject;

	/**
	 * The request keypair
	 */
	private KeyPair keyPair;

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
	private ASN1EncodableVector extendedKeyUsage;

	/**
	 * The defined subject alternate names.
	 */
	private GeneralNames subjectAltNames;

	/**
	 * Any defined certificate policies.
	 */
	private ASN1Encodable certificatePolicies;

	/**
	 * Flag to indicate if this is a CA request.
	 */
	private boolean cARequest = false;

	/**
	 * Certificate request description.
	 */
	private String description;

	/**
	 * The creation date of the request.
	 */
	private ZonedDateTime creationDate;
	
	/**
	 * CRL Location
	 */
	private String crlLocation;
	
	/**
	 * CRL Issuer
	 */
	private X500Name crlIssuer;

	/**
	 * Create an empty CA request object. (All elements are empty).
	 */
	public CertificateRequest() {
			};

	@Override
	public X500Name getSubject() {
		return subject;
	}

	@Override
	public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
		try {
			getKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			// Ignore.
		}
		if (keyPair == null) {
			return null;
		}
		return SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()));
	}

	@Override
	public KeyUsage getKeyUsage() {
		return keyUsage;
	}

	@Override
	public boolean isCARequest() {
		return this.cARequest;
	}

	@Override
	public ASN1Encodable getExtendedKeyUsage() {
		if (extendedKeyUsage == null) {
			return null;
		}
		return new DERSequence(extendedKeyUsage);
	}

	@Override
	public ASN1Encodable getSubjectAlternativeName() {
		return subjectAltNames;
	}

	@Override
	public ASN1Encodable getCertificatePolicies() {
		return certificatePolicies;
	}

	@Override
	public ASN1Encodable getCRLDistributionPoint() {
		try {
			// Don't use if we are not a Intermediate CA ourselves.
			if (!cARequest) {
				return null;
			}
			if (crlLocation != null && !crlLocation.isEmpty()) {
				X500Name cIssuer = crlIssuer != null ? crlIssuer : subject;
				if (cIssuer == null) {
					return null;
				}
				DistributionPointName name = new DistributionPointName(DistributionPointName.FULL_NAME,
						new GeneralNames(GeneralNameTag.uniformResourceIdentifier.asGeneralName(crlLocation)));
				DistributionPoint point = new DistributionPoint(name, null, new GeneralNames(new GeneralName(cIssuer)));
				return new CRLDistPoint(new DistributionPoint[] { point });
			}
		} catch (Throwable e) {
			// Ignore
		}
		return null;
	}
	
	/**
	 * Get the defined KeyPair,
	 * <p>
	 * If the keypair is undefined, but the key type has been defined, calling this method generate a new key pair
	 * instance.
	 * 
	 * @return The defined KeyPair
	 * @throws InvalidAlgorithmParameterException Auto generation of keying material failed. The algorithm parameters is
	 *             invalid.
	 * @throws NoSuchProviderException The Cryptoprovider is missing?
	 * @throws NoSuchAlgorithmException Auto generation of keying material failed. The algorithm is not known.
	 */
	public KeyPair getKeyPair()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		/*
		 * If we have no key material but a defined key type, generate the key pair.
		 */
		if (keyPair == null && keyType != null) {
			keyPair = KeyPairFactory.generateKeyPair(keyType);
		}
		return keyPair;
	}

	/**
	 * Set the key material pair
	 * 
	 * @param keyPair The key material pair. Use NULL to unset.
	 * <P>
	 * If keypair supplied, it MUST contain a public key. (NULL pointer exception if missing).
	 */
	public void setKeyPair(KeyPair keyPair) {
		if(keyPair != null) {
			Objects.requireNonNull(keyPair.getPublic(), "Public Key is missing from KeyPair");
		}
		this.keyPair = keyPair;
	}

	/**
	 * Set the subject alternate names
	 * 
	 * @param subjectAltNames The subject alternate names. Use NULL to unset.
	 */
	public void setSubjectAlternativeName(GeneralNames subjectAltNames) {
		this.subjectAltNames = subjectAltNames;
	}

	/**
	 * Set the subject
	 * 
	 * @param subject The certificate subject.
	 */
	public void setSubject(X500Name subject) {
		this.subject = subject;
	}

	/**
	 * Set the defined key usage.
	 * 
	 * @param keyUsage The key usage to set. Use NULL to unset.
	 */
	public void setKeyUsage(KeyUsage keyUsage) {
		this.keyUsage = keyUsage;
	}

	/**
	 * Set the extended key usage
	 * 
	 * @param extendedKeyUsage The extended key usage. Use NULL to unset.
	 */
	public void setExtendedKeyUsage(ASN1EncodableVector extendedKeyUsage) {
		this.extendedKeyUsage = extendedKeyUsage;
	}

	/**
	 * Get the extended key usage
	 * 
	 * @return The extended key usage as an vector.
	 */
	public ASN1EncodableVector getExtendedKeyUsageVector() {
		return extendedKeyUsage;
	}

	/**
	 * Set any defined certificate policies
	 * 
	 * @param certificatePolicies The certificates policies to set. Use NULL to unset.
	 */
	public void setCertificatePolicies(ASN1Encodable certificatePolicies) {
		this.certificatePolicies = certificatePolicies;
	}

	/**
	 * Is this a CA Request
	 * @return TRUE if this is a CA Request.
	 */
	public boolean iscARequest() {
		return cARequest;
	}
	/**
	 * Set if this request is for an Intermediate CA.
	 * <p>
	 * Setting this to TRUE does NOT modify KeyUsage or ExtendedKeyUsage.
	 * 
	 * @param cARequest TRUE to set this is for an Intermediate CA.
	 */
	public void setcARequest(boolean cARequest) {
		this.cARequest = cARequest;
	}

	/**
	 * Get the defined key type.
	 * 
	 * @return The defined key type.
	 */
	public KeyType getKeyType() {
		return keyType;
	}

	/**
	 * Set the defined key type
	 * 
	 * @param keyType The key material type to set.
	 */
	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	/**
	 * Get the description of this request.
	 * 
	 * @return the description of this request.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The description of this request.
	 * 
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * The date time that this request was initially created.
	 * 
	 * @return The initial creation date.
	 */
	public ZonedDateTime getCreationDate() {
		return creationDate;
	}

	/**
	 * Set the date time that this request was initially created.
	 * 
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
	public ICertificateRequest asCertificateRequest() {
		return this;
	}

	/**
	 * Get this object as a Certificate Key Pair Template.
	 * 
	 * @return This object represented as a Certificate Key Pair Template.
	 */
	public ICertificateKeyPairTemplate asTemplate() {
		return this;
	}

	@Override
	public void store(Path filename) throws Exception {
		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		template.setSubject(subject);
		template.setKeyType(keyType);
		template.setDescription(description);
		template.setKeyUsage(keyUsage);
		if(extendedKeyUsage != null) {
			Collection<KeyPurposeId> values = new ArrayList<>();
			for(int i = 0 ; i < extendedKeyUsage.size(); i++) {
				values.add((KeyPurposeId)extendedKeyUsage.get(i));
			}
			template.setExtendedKeyUsage(values);
		}
		template.setSubjectAltNames(subjectAltNames);
		template.setCARequest(cARequest);
		template.setCrlLocation(crlLocation);
		template.setCrlIssuer(crlIssuer);
		template.setCreationDate(creationDate);
		CertificateKeyPairTemplate.write(template, filename);
	}

	@Override
	public String toString() {
		if (subject != null) {
			return subject.toString();
		}
		if (description != null) {
			return description;
		}
		return "Empty Certificate Request";
	}

	@Override
	public int compareTo(CertificateRequest o) {
		if (o == null) {
			return -1;
		}
		if (o.description != null && this.description != null) {
			return this.description.compareTo(o.description);
		}
		if (o.subject != null && this.subject != null) {
			return this.subject.toString().compareTo(o.subject.toString());
		}
		return 0;
	}

	@Override
	public int compare(CertificateRequest o1, CertificateRequest o2) {
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		CertificateRequest other = (CertificateRequest) obj;
		if (creationDate == null) {
			if (other.creationDate != null)
				return false;
		} else if (!creationDate.equals(other.creationDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}
	
	
}
