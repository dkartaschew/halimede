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

package net.sourceforge.dkartaschew.halimede.enumeration;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.asn1.x509.CRLReason;

/**
 * Reason Code enumeration for certificate revocation.
 */
public enum RevokeReasonCode {

	UNSPECIFIED(0, "Unspecified"), //
	KEY_COMPROMISE(1, "Key Compromise"), //
	CA_COMPROMISE(2, "CA Compromise"), //
	AFFILIATION_CHANGED(3, "Affiliation Changed"), //
	SUPERSEDED(4, "Superseded"), //
	CESSATION_OF_OPERATION(5, "Cessation Of Operation"), //
	CERTIFICATE_HOLD(6, "Certificate Hold"), //
	UNKNOWN(7, "UNKNOWN"), //
	REMOVE_FROM_CRL(8, "Remove From CRL"), //
	PRIVILEGE_WITHDRAWN(9, "Privilege Withdrawn"), //
	AA_COMPROMISE(10, "AA Compromise");

	/**
	 * The CRL Code enumeration
	 */
	private final int code;
	/**
	 * Plain text description
	 */
	private final String description;

	/**
	 * Create a new revoke reason code enumeration
	 * 
	 * @param code The internal code (MUST match those in CRLReason).
	 * @param description The plain text description.
	 */
	private RevokeReasonCode(int code, String description) {
		this.code = code;
		this.description = description;
	}

	/**
	 * Get the reason code.
	 * 
	 * @return The reason code.
	 */
	public int getCode() {
		return code;
	}

	/**
	 * Get the plain text description
	 * 
	 * @return The plain text description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The the revoke code as a CRL Reason.
	 * 
	 * @return The CRL Reason.
	 */
	public CRLReason getCRLReason() {
		return CRLReason.lookup(code);
	}

	@Override
	public String toString() {
		return description;
	}

	/**
	 * Get the revoke reason code for the given CRL Reason.
	 * 
	 * @param reason The CRLReason
	 * @return The reason code enumeration or Unspecified if not known.
	 */
	public static RevokeReasonCode forCRLReason(CRLReason reason) {
		if(reason == null) {
			return UNSPECIFIED;
		}
		final int value = reason.getValue().intValue();
		return Arrays.stream(RevokeReasonCode.values()).filter(i -> i.code == value).findFirst().orElse(UNSPECIFIED);
	}

	/**
	 * Get the revoke reason code for the given CRL Reason.
	 * 
	 * @param reason The CRLReason
	 * @return The reason code enumeration or Unspecified if not known.
	 */
	public static RevokeReasonCode forCRLReason(java.security.cert.CRLReason reason) {
		if(reason == null) {
			return UNSPECIFIED;
		}
		final int value = reason.ordinal();
		return Arrays.stream(RevokeReasonCode.values()).filter(i -> i.code == value).findFirst().orElse(UNSPECIFIED);
	}

	/**
	 * Get the RevokeReasonCode based on the given description
	 * 
	 * @param value The description obtained from the RevokeReasonCode
	 * @return The RevokeReasonCode based on the given description.
	 * @throws NoSuchElementException The description given doesn't match a known element.
	 * @throws NullPointerException The description was null.
	 */
	public static RevokeReasonCode forDescription(String value) {
		Objects.requireNonNull(value, "Description was null");
		return Arrays.stream(RevokeReasonCode.values()).filter(i -> i.description.equals(value)).findFirst().get();
	}

	/**
	 * Get the valid items for this enumeration suitable for using a UI for selection.
	 * 
	 * @return A list of valid codes for revocation.
	 */
	public static List<Object> getValidList() {
		// DO NOT INCLUDE UNKNOWN (7).
		return Arrays.asList(UNSPECIFIED, //
				KEY_COMPROMISE, //
				CA_COMPROMISE, //
				AFFILIATION_CHANGED, //
				SUPERSEDED, //
				CESSATION_OF_OPERATION, //
				CERTIFICATE_HOLD, //
				REMOVE_FROM_CRL, //
				PRIVILEGE_WITHDRAWN, //
				AA_COMPROMISE);
	}
}
