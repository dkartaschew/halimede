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

package net.sourceforge.dkartaschew.halimede.enumeration;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;

public enum PKCS12Cipher {

	/**
	 * Default cipher for KeyStore data type (3DES)
	 */
	DES3(null), //
	/**
	 * AES 128-CBC
	 */
	AES128(NISTObjectIdentifiers.id_aes128_CBC), //
	/**
	 * AES 192-CBC
	 */
	AES192(NISTObjectIdentifiers.id_aes192_CBC), //
	/**
	 * AES 256-CBC
	 */
	AES256(NISTObjectIdentifiers.id_aes256_CBC);//
//	/**
//	 * GOST 28147
//	 */
//	GOST28147_GCFB(CryptoProObjectIdentifiers.gostR28147_gcfb);

	/**
	 * The ASN1 ID
	 */
	private final ASN1ObjectIdentifier id;

	/**
	 * Construct the enum with the given ID
	 * 
	 * @param id The id to use
	 */
	private PKCS12Cipher(ASN1ObjectIdentifier id) {
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
}
