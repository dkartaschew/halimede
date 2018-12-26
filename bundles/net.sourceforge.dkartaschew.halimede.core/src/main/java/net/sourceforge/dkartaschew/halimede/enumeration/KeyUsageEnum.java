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

package net.sourceforge.dkartaschew.halimede.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.asn1.x509.KeyUsage;

/**
 * Basic Enumeration of all KeyUsage. (RFC 5280)
 */
public enum KeyUsageEnum {

	digitalSignature("Digital Signature", KeyUsage.digitalSignature), //
	nonRepudiation("Non Repudiation", KeyUsage.nonRepudiation), //
	keyEncipherment("Key Encipherment", KeyUsage.keyEncipherment), //
	dataEncipherment("Data Encipherment", KeyUsage.dataEncipherment), //
	keyAgreement("Key Agreement", KeyUsage.keyAgreement), //
	keyCertSign("Certificate Signing", KeyUsage.keyCertSign), //
	cRLSign("CRL Signing", KeyUsage.cRLSign), //
	encipherOnly("Data Enchipher Only", KeyUsage.encipherOnly), //
	decipherOnly("Data Dechipher Only", KeyUsage.decipherOnly);

	/**
	 * Description
	 */
	private final String description;
	/**
	 * Usage
	 */
	private final int usage;

	/**
	 * Create a new Enum base KeyUsage
	 * 
	 * @param description The textual description
	 * @param usage Key Usage bit value.
	 */
	private KeyUsageEnum(String description, int usage) {
		this.description = description;
		this.usage = usage;
	}

	/**
	 * Get the int type.
	 * 
	 * @return bit type.
	 */
	public int getKeyUsage() {
		return usage;
	}

	@Override
	public String toString() {
		return description;
	}

	/**
	 * Get the single value as a Key Usage.
	 * 
	 * @return A Key Usage.
	 */
	public KeyUsage asKeyUsage() {
		return new KeyUsage(usage);
	}

	/**
	 * Convert the collection to a KeyUsage instance
	 * 
	 * @param usage The collection of usage
	 * @return A KeyUsage instance
	 */
	public static KeyUsage asKeyUsage(Collection<KeyUsageEnum> usage) {
		if (usage == null || usage.isEmpty()) {
			return new KeyUsage(0);
		}
		return new KeyUsage(usage.stream().mapToInt(i -> i != null ? i.usage : 0).sum());
	}

	/**
	 * Convert the collection to a KeyUsage instance
	 * 
	 * @param usage The collection of usage
	 * @return A KeyUsage instance
	 */
	public static KeyUsage asKeyUsage(KeyUsageEnum[] usage) {
		if (usage == null || usage.length == 0) {
			return new KeyUsage(0);
		}
		return new KeyUsage(Arrays.stream(usage).mapToInt(i -> i != null ? i.usage : 0).sum());
	}

	/**
	 * Convert the KeyUsage instance to a collection of enums
	 * 
	 * @param usage The key usage instance
	 * @return Collections of enums that make up the key usage
	 */
	public static Collection<KeyUsageEnum> asKeyUsageEnum(KeyUsage usage) {
		if(usage == null) {
			return new ArrayList<KeyUsageEnum>();
		}
		List<KeyUsageEnum> values = new ArrayList<>();
		for (KeyUsageEnum e : KeyUsageEnum.values()) {
			if (usage.hasUsages(e.usage)) {
				values.add(e);
			}
		}
		return values;
	}
}
