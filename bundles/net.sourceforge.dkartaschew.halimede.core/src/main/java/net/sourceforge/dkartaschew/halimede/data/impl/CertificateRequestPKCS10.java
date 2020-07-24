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

package net.sourceforge.dkartaschew.halimede.data.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.PKCS10Decoder;
import net.sourceforge.dkartaschew.halimede.ui.labelproviders.GeneralNameLabelProvider;

/**
 * Certificate Request backed by a PKCS10 file.
 */
public class CertificateRequestPKCS10 implements ICertificateRequest {

	private final PKCS10CertificationRequest pkcs10Request;

	/**
	 * Create a new Certificate Request Instance
	 * 
	 * @param filename The filename with the CSR
	 * @return A Certificate Request
	 * @throws IOException Unable to read or decode the PKCS10 CSR.
	 */
	public static ICertificateRequest create(Path filename) throws IOException {
		return PKCS10Decoder.open(filename);
	}

	/**
	 * Create a new certificate request
	 * 
	 * @param pkcs10Request The BC PKCS10 request. (MUST not be null).
	 */
	public CertificateRequestPKCS10(PKCS10CertificationRequest pkcs10Request) {
		Objects.requireNonNull(pkcs10Request, "Request is NULL");
		this.pkcs10Request = pkcs10Request;
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
		result = prime * result + pkcs10Request.hashCode();
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
		CertificateRequestPKCS10 other = (CertificateRequestPKCS10) obj;
		return pkcs10Request.equals(other.pkcs10Request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CertificateRequest [" + pkcs10Request.getSubject() + ", "
				+ Arrays.toString(pkcs10Request.getSignature()) + "]";
	}

	@Override
	public X500Name getSubject() {
		return pkcs10Request.getSubject();
	}

	@Override
	public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
		return pkcs10Request.getSubjectPublicKeyInfo();
	}

	/**
	 * Get the X509v3 Extensions object from the PKCS10 CSR
	 * 
	 * @return The X509v3 Extensions if present. (NULL if not present).
	 */
	public Extensions getExtensions() {
		Attribute[] attrs = pkcs10Request.getAttributes();
		for (Attribute attr : attrs) {
			if (attr.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_extensionRequest)) {
				ASN1Set values = attr.getAttrValues();
				return Extensions.getInstance((ASN1Sequence) values.getObjectAt(0));
			}
		}
		return null;
	}

	@Override
	public KeyUsage getKeyUsage() {
		Extensions exts = getExtensions();
		if (exts != null) {
			return KeyUsage.fromExtensions(exts);
		}
		return null;
	}

	@Override
	public boolean isCARequest() {
		// Check for basic Constraints
		Extensions exts = getExtensions();
		if (exts != null) {
			BasicConstraints constraints = BasicConstraints.fromExtensions(exts);
			if (constraints != null) {
				return constraints.isCA();
			}
		}

		KeyUsage usage = getKeyUsage();
		if (usage != null) {
			return usage.hasUsages(KeyUsage.keyCertSign);
		}
		return false;
	}

	@Override
	public ASN1Encodable getExtendedKeyUsage() {
		return getExtension0(Extension.extendedKeyUsage);
	}

	@Override
	public ASN1Encodable getSubjectAlternativeName() {
		return getExtension0(Extension.subjectAlternativeName);
	}

	/**
	 * Get the Subject Alternative Names as a GeneralName
	 * 
	 * @return The Subject Alternative Names
	 */
	public GeneralNames getSubjectAlternativeNames() {
		ASN1Encodable element = getSubjectAlternativeName();
		if (element != null) {
			return GeneralNames.getInstance(element);
		}
		return null;
	}

	@Override
	public ASN1Encodable getCertificatePolicies() {
		return getExtension0(Extension.certificatePolicies);
	}

	@Override
	public ASN1Encodable getCRLDistributionPoint() {
		return getExtension0(Extension.cRLDistributionPoints);
	}

	public ASN1EncodableVector getExtendedKeyUsageVector() {
		ASN1Encodable extKeyUsage = getExtendedKeyUsage();
		if(extKeyUsage == null) {
			return null;
		}
		if (extKeyUsage instanceof ASN1EncodableVector) {
			return (ASN1EncodableVector) extKeyUsage;
		}
		ASN1EncodableVector v = new ASN1EncodableVector();
		if (extKeyUsage instanceof ASN1Sequence) {
			((ASN1Sequence) extKeyUsage).forEach(o -> v.add(KeyPurposeId.getInstance(o)));
		} else if (extKeyUsage instanceof ASN1Set) {
			((ASN1Set) extKeyUsage).forEach(o -> v.add(KeyPurposeId.getInstance(o)));
		}
		return v;
	}

	/**
	 * Extract the CRL Issuer (if this is a CSR for a CA).
	 * 
	 * @return The CRL Issuer, null if not set/present.
	 */
	public X500Name getCrlIssuer() {
		CRLDistPoint points = CRLDistPoint.getInstance(getCRLDistributionPoint());
		if ((points != null && points.getDistributionPoints() != null)) {
			for (DistributionPoint point : points.getDistributionPoints()) {
				GeneralNames names = point.getCRLIssuer();
				if (names != null && names.getNames() != null && names.getNames().length > 0) {
					GeneralName name = names.getNames()[0];
					GeneralNameLabelProvider l = new GeneralNameLabelProvider();
					return new X500Name(l.getValue(name));
				}
			}
		}
		return null;
	}

	/**
	 * Extract the CRL Location (if this is a CSR for a CA).
	 * 
	 * @return The CRL Location, null if not set/present.
	 */
	public String getCrlLocation() {
		CRLDistPoint points = CRLDistPoint.getInstance(getCRLDistributionPoint());
		if ((points != null && points.getDistributionPoints() != null)) {
			for (DistributionPoint point : points.getDistributionPoints()) {
				DistributionPointName distPoint = point.getDistributionPoint();
				ASN1Encodable dPointsEnc = distPoint.getName();
				if (dPointsEnc instanceof GeneralNames) {
					GeneralNames names = (GeneralNames) dPointsEnc;
					if (names.getNames() != null && names.getNames().length > 0) {
						GeneralName name = names.getNames()[0];
						GeneralNameLabelProvider l = new GeneralNameLabelProvider();
						return l.getValue(name);
					}
				}

			}
		}
		return null;
	}

	/**
	 * Return the details of the signature algorithm used to create this request.
	 *
	 * @return the AlgorithmIdentifier describing the signature algorithm used to create this request.
	 */
	public AlgorithmIdentifier getSignatureAlgorithm() {
		return pkcs10Request.getSignatureAlgorithm();
	}

	/**
	 * Return the bytes making up the signature associated with this request.
	 *
	 * @return the request signature bytes.
	 */
	public byte[] getSignature() {
		return pkcs10Request.getSignature();
	}

	/**
	 * Get the byte DER encoded CSR
	 * 
	 * @return The DER encoded CSR
	 * @throws IOException If encoding failed.
	 */
	public byte[] getEncoded() throws IOException {
		return pkcs10Request.getEncoded();
	}

	/**
	 * Get the ASN1 object for the given extension
	 * 
	 * @param extension The extension to enquire
	 * @return The extension or null if not found.
	 */
	private ASN1Encodable getExtension0(ASN1ObjectIdentifier extension) {
		Extensions exts = getExtensions();
		if (exts != null) {
			Extension e = exts.getExtension(extension);
			if (e != null) {
				return e.getParsedValue();
			}
		}
		return null;
	}

	/**
	 * Get the ASN1 object for the given extension
	 * 
	 * @param extension The extension to enquire
	 * @return The extension or null if not found.
	 */
	public Extension getExtension(ASN1ObjectIdentifier extension) {
		Extensions exts = getExtensions();
		if (exts != null) {
			return exts.getExtension(extension);
		}
		return null;
	}

}
