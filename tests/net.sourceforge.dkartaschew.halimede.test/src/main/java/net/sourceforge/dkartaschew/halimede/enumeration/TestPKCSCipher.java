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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.junit.Test;

public class TestPKCSCipher {

	/*
	 * PKCS 12
	 */
	
	@Test
	public void testPKCS12_DES() {
		assertEquals(null, PKCS12Cipher.DES3.getID());
	}
	
	@Test
	public void testPKCS12_AES128() {
		assertEquals(NISTObjectIdentifiers.id_aes128_CBC, PKCS12Cipher.AES128.getID());
	}
	
	@Test
	public void testPKCS12_AES192() {
		assertEquals(NISTObjectIdentifiers.id_aes192_CBC, PKCS12Cipher.AES192.getID());
	}
	
	@Test
	public void testPKCS12_AES256() {
		assertEquals(NISTObjectIdentifiers.id_aes256_CBC, PKCS12Cipher.AES256.getID());
	}
	
	/*
	 * PKCS 8
	 */
	
	@Test
	public void testPKCS8_DES() {
		assertEquals(PKCSObjectIdentifiers.des_EDE3_CBC, PKCS8Cipher.DES3_CBC.getID());
	}
	
	@Test
	public void testPKCS8_AES_128_CBC() {
		assertEquals(NISTObjectIdentifiers.id_aes128_CBC, PKCS8Cipher.AES_128_CBC.getID());
	}
	
	@Test
	public void testPKCS8_AES_192_CBC() {
		assertEquals(NISTObjectIdentifiers.id_aes192_CBC, PKCS8Cipher.AES_192_CBC.getID());
	}
	
	@Test
	public void testPKCS8_AES_256_CBC() {
		assertEquals(NISTObjectIdentifiers.id_aes256_CBC, PKCS8Cipher.AES_256_CBC.getID());
	}


	@Test
	public void testPKCS8_PBE_SHA1_RC4_128() {
		assertEquals(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC4, PKCS8Cipher.PBE_SHA1_RC4_128.getID());
	}
	
	@Test
	public void testPKCS8_PBE_SHA1_RC4_40() {
		assertEquals(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC4, PKCS8Cipher.PBE_SHA1_RC4_40.getID());
	}
	
	@Test
	public void testPKCS8_PBE_SHA1_3DES() {
		assertEquals(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, PKCS8Cipher.PBE_SHA1_3DES.getID());
	}
	
	@Test
	public void testPKCS8_PBE_SHA1_2DES() {
		assertEquals(PKCSObjectIdentifiers.pbeWithSHAAnd2_KeyTripleDES_CBC, PKCS8Cipher.PBE_SHA1_2DES.getID());
	}
	
	@Test
	public void testPKCS8_PBE_SHA1_RC2_128() {
		assertEquals(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC, PKCS8Cipher.PBE_SHA1_RC2_128.getID());
	}
	
	@Test
	public void testPKCS8_PBE_SHA1_RC2_40() {
		assertEquals(PKCSObjectIdentifiers.pbeWithSHAAnd40BitRC2_CBC, PKCS8Cipher.PBE_SHA1_RC2_40.getID());
	}
	
	@Test
	public void testEnumPKCS12() {
		for(PKCS12Cipher t : PKCS12Cipher.values()) {
			assertEquals(t, PKCS12Cipher.valueOf(t.name()));
			assertEquals(0, t.compareTo(t));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
			System.out.println(t.getID());
		}
	}
	
	@Test
	public void testEnumPKCS8() {
		for(PKCS8Cipher t : PKCS8Cipher.values()) {
			assertEquals(t, PKCS8Cipher.valueOf(t.name()));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
			assertEquals(t.getStringID(), t.getID().toString());
		}
	}

}
