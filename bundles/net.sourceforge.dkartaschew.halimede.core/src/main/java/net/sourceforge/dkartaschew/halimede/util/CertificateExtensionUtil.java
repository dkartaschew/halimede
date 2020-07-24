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

package net.sourceforge.dkartaschew.halimede.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x509.Extension;

public class CertificateExtensionUtil {

	private final static Map<ASN1ObjectIdentifier, String> map;

	static {
		Map<ASN1ObjectIdentifier, String> m = new HashMap<>();
		m.put(Extension.subjectDirectoryAttributes, "Subject Directory Attributes");
		m.put(Extension.subjectKeyIdentifier, "Subject Key Identifier");
		m.put(Extension.keyUsage, "Key Usage");
		m.put(Extension.privateKeyUsagePeriod, "Private Key Usage Period");
		m.put(Extension.subjectAlternativeName, "Subject Alternative Name");
		m.put(Extension.issuerAlternativeName, "Issuer Alternative Name");
		m.put(Extension.basicConstraints, "Basic Constraints");
		m.put(Extension.cRLNumber, "CRL Number");
		m.put(Extension.reasonCode, "Reason code");
		m.put(Extension.instructionCode, "Hold Instruction Code");
		m.put(Extension.invalidityDate, "Invalidity Date");
		m.put(Extension.deltaCRLIndicator, "Delta CRL indicator");
		m.put(Extension.issuingDistributionPoint, "Issuing Distribution Point");
		m.put(Extension.certificateIssuer, "Certificate Issuer");
		m.put(Extension.nameConstraints, "Name Constraints");
		m.put(Extension.cRLDistributionPoints, "CRL Distribution Points");
		m.put(Extension.certificatePolicies, "Certificate Policies");
		m.put(Extension.policyMappings, "Policy Mappings");
		m.put(Extension.authorityKeyIdentifier, "Authority Key Identifier");
		m.put(Extension.policyConstraints, "Policy Constraints");
		m.put(Extension.extendedKeyUsage, "Extended Key Usage");
		m.put(Extension.freshestCRL, "Freshest CRL");
		m.put(Extension.inhibitAnyPolicy, "Inhibit Any Policy");
		m.put(Extension.authorityInfoAccess, "Authority Info Access");
		m.put(Extension.subjectInfoAccess, "Subject Info Access");
		m.put(Extension.logoType, "Logo Type");
		m.put(Extension.biometricInfo, "BiometricInfo");
		m.put(Extension.qCStatements, "QCStatements");
		m.put(Extension.auditIdentity, "Audit identity");
		m.put(Extension.noRevAvail, "NoRevAvail");
		m.put(Extension.targetInformation, "TargetInformation");
		m.put(Extension.expiredCertsOnCRL, "Expired Certificates on CRL");
		m.put(MiscObjectIdentifiers.netscapeCertComment, "Netscape Comment");
		map = Collections.unmodifiableMap(m);
	}

	/**
	 * Get the extension description for the given extension ASN1 object id.
	 * 
	 * @param ext The ASN1 Object ID
	 * @return The plain text description, or NULL if unknown.
	 */
	public static String getDescription(ASN1ObjectIdentifier ext) {
		if (ext == null) {
			return null;
		}
		return map.get(ext);
	}
}
