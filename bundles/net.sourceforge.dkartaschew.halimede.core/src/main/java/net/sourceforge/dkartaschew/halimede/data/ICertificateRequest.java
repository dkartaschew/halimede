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

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * Interface for all Certificate Request Implementations.
 */
public interface ICertificateRequest {

	/**
	 * Default extension for PKCS10 requests.
	 */
	public static String DEFAULT_EXTENSION = ".csr";
	
	/**
	 * Get the subject of the request
	 * 
	 * @return The subject
	 */
	X500Name getSubject();

	/**
	 * Get the public key material
	 * 
	 * @return The public key
	 */
	SubjectPublicKeyInfo getSubjectPublicKeyInfo();

	/**
	 * Get the key usage.
	 * 
	 * @return The defined key usage
	 */
	KeyUsage getKeyUsage();

	/**
	 * Is this request for an Intermediate CA
	 * 
	 * @return TRUE if this request is for an Intermediate CA
	 */
	boolean isCARequest();

	/**
	 * Get any extended key usage
	 * 
	 * @return The extended keu usage. (Typically a ASN1EncodableVector of KeyPurposeId).
	 */
	ASN1Encodable getExtendedKeyUsage();

	/**
	 * Get any Subject Alt Names.
	 * 
	 * @return Subject Alternate Names. (Typically GeneralNames containing GeneralName).
	 */
	ASN1Encodable getSubjectAlternativeName();

	/**
	 * Get any defined certificate policies
	 * 
	 * @return Any defined certificate policies.
	 */
	ASN1Encodable getCertificatePolicies();

	/**
	 * Get any defined certificate CRL locations.
	 * <P>
	 * This MUST be a CRLDistPoint object. 
	 * 
	 * @return Any defined certificate CRL locations.
	 */
	ASN1Encodable getCRLDistributionPoint();
	
}