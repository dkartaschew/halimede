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

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;

public enum PKCS8Cipher {
	/**
	 * Default cipher for KeyStore data type (3DES)
	 */
	DES3_CBC(PKCSObjectIdentifiers.des_EDE3_CBC), //
	/**
	 * AES 128-CBC
	 */
	AES_128_CBC(NISTObjectIdentifiers.id_aes128_CBC), //
	/**
	 * AES 192-CBC
	 */
	AES_192_CBC(NISTObjectIdentifiers.id_aes192_CBC), //
	/**
	 * AES 256-CBC
	 */
	AES_256_CBC(NISTObjectIdentifiers.id_aes256_CBC), //

	/**
	 * RC4-128
	 */
	PBE_SHA1_RC4_128(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4), //
	/**
	 * RC4-40
	 */
	PBE_SHA1_RC4_40(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4), //
	/**
	 * 3Key 3DES
	 */
	PBE_SHA1_3DES(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC), //
	/**
	 * 2Key 3DES
	 */
	PBE_SHA1_2DES(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC), //
	/**
	 * RC2-128
	 */
	PBE_SHA1_RC2_128(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC), //
	/**
	 * RC2-40
	 */
	PBE_SHA1_RC2_40(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC);

	/**
	 * The ASN1 ID
	 */
	private final ASN1ObjectIdentifier id;

	/**
	 * Construct the enum with the given ID
	 * 
	 * @param id The id to use
	 */
	private PKCS8Cipher(ASN1ObjectIdentifier id) {
		this.id = id;
	}

	/**
	 * Get the ASN1 ID of the cipher
	 * 
	 * @return The ASN1 ID
	 */
	public ASN1ObjectIdentifier getID() {
		return id;
	}

	/**
	 * Get the ASN1 ID of the cipher as a String.
	 * 
	 * @return The ASN1 ID
	 */
	public String getStringID() {
		return id.getId();
	}

}
