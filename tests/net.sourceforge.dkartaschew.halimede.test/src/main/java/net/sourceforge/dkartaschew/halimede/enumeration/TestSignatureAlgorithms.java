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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.gmss.BCGMSSPublicKey;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;

public class TestSignatureAlgorithms {

	@Test
	public void testSignatureForAlgID() {
		for (SignatureAlgorithm s : SignatureAlgorithm.values()) {
			assertEquals(s, SignatureAlgorithm.forAlgID(s.getAlgID()));
		}
	}

	@Test(expected=NullPointerException.class)
	public void testSignatureForAlgID_NULL() {
		assertEquals(null, SignatureAlgorithm.forAlgID(null));
	}

	@Test(expected=NoSuchElementException.class)
	public void testSignatureForAlgID_Unknown() {
		assertEquals(null, SignatureAlgorithm.forAlgID("1.1.1"));
	}

	@Test(expected=NullPointerException.class)
	public void testSignatureForNULL() {
		assertEquals(null, SignatureAlgorithm.getDefaultSignature(null));
	}

	@Test
	public void testSignatureForOID() {
		for (SignatureAlgorithm s : SignatureAlgorithm.values()) {
			assertEquals(s, SignatureAlgorithm.forOID(s.getOID()));
		}
	}

	@Test(expected=NullPointerException.class)
	public void testSignatureForOID_NULL() {
		assertEquals(null, SignatureAlgorithm.forOID(null));
	}

	@Test(expected=NoSuchElementException.class)
	public void testSignatureForOID_Unknown() {
		assertEquals(null, SignatureAlgorithm.forOID(X9ObjectIdentifiers.ansi_X9_42));
	}

	@Test
	public void testSignatureDefaults()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		assertEquals(SignatureAlgorithm.SHA256withRSA,
				SignatureAlgorithm.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.RSA_512).getPublic()));

		assertEquals(SignatureAlgorithm.SHA1withDSA,
				SignatureAlgorithm.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.DSA_512).getPublic()));

		assertEquals(SignatureAlgorithm.SHA512withECDSA, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.EC_prime192v1).getPublic()));

		assertEquals(SignatureAlgorithm.GOST3411withGOST3410, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.GOST_3410_94_A).getPublic()));

		assertEquals(SignatureAlgorithm.GOST3411withECGOST3410, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.GOST_3410_2001_A).getPublic()));

		assertEquals(SignatureAlgorithm.GOST3411withECGOST3410_2012_256, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.GOST_3410_2012_256_A).getPublic()));

		assertEquals(SignatureAlgorithm.GOST3411withECGOST3410_2012_512, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.GOST_3410_2012_512_A).getPublic()));
		
		assertEquals(SignatureAlgorithm.GOST3411withDSTU4145, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.DSTU4145_0).getPublic()));
		
		assertEquals(SignatureAlgorithm.ED25519, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.ED25519).getPublic()));
		
		assertEquals(SignatureAlgorithm.ED448, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.ED448).getPublic()));
		
		assertEquals(SignatureAlgorithm.RAINBOWwithSHA512, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.Rainbow).getPublic()));
		
		assertEquals(SignatureAlgorithm.XMSSwithSHA512, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.XMSS_SHA2_10_256).getPublic()));
		
		assertEquals(SignatureAlgorithm.XMSSMTwithSHA512, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.XMSSMT_SHA2_20_4_256).getPublic()));
		
		assertEquals(SignatureAlgorithm.SPHINCS256withSHA3_512, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.SPHINCS_SHA3_256).getPublic()));
		
		assertEquals(SignatureAlgorithm.SPHICS256withSHA512, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.SPHINCS_SHA512_256).getPublic()));

	}

	@Test
	public void testUnhandledPublicKeySpec() {
		/*
		 * THIS KEY IS NON-FUNCTIONAL
		 */
		BCGMSSPublicKey publicKey = new BCGMSSPublicKey(null, null);
		assertEquals(null, SignatureAlgorithm.getDefaultSignature(publicKey));
	}
	
	@Test(expected=NullPointerException.class)
	public void testUnhandledPublicKeyNull() {
		assertEquals(null, SignatureAlgorithm.getDefaultSignature(null));
	}

	@Test
	public void testUnhandledKeyType() {
		for (KeyType k : KeyType.values()) {
			Collection<SignatureAlgorithm> c = SignatureAlgorithm.forType(k);
			assertFalse(c.isEmpty());
		}
	}

	@Test(expected=NullPointerException.class)
	public void testSignatureForKey_NULL() {
		SignatureAlgorithm.forType((KeyType) null);
	}

	@Test
	public void testSignatureForKey_DSA() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.DSA_512);
		assertTrue(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForKey_RSA_512() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.RSA_512);
		assertTrue(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForKey_RSA_16384() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.RSA_16384);
		assertTrue(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertTrue(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForKey_EC_NIST() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.EC_prime192v1);
		assertTrue(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForKey_GOST94() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.GOST_3410_94_A);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForKey_ECGOST() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.GOST_3410_2001_A);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForKey_ECGOST_256() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.GOST_3410_2012_256_A);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_512));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForKey_ECGOST_512() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.GOST_3410_2012_512_A);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_512));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}
	
	@Test
	public void testSignatureForKeyDSTU4145() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(KeyType.DSTU4145_0);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
	}

	@Test(expected=NullPointerException.class)
	public void testSignatureForSig_NULL() {
		SignatureAlgorithm.forType((SignatureAlgorithm) null);
	}

	@Test
	public void testSignatureForSig_DSA() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.SHA1withDSA);
		assertTrue(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForSig_RSA_512() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.SHA224withRSA);
		assertTrue(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertTrue(alg.contains(SignatureAlgorithm.SHA224withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForSig_EC_NIST() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.SHA1withECDSA);
		assertTrue(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertTrue(alg.contains(SignatureAlgorithm.SHA512withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForSig_GOST94() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.GOST3411withGOST3410);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForSig_ECGOST() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.GOST3411withECGOST3410);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForSig_ECGOST_256() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm
				.forType(SignatureAlgorithm.GOST3411withECGOST3410_2012_256);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_512));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testSignatureForSig_ECGOST_512() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm
				.forType(SignatureAlgorithm.GOST3411withECGOST3410_2012_512);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_512));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
	}

	@Test
	public void testEnum() {
		for (SignatureAlgorithm t : SignatureAlgorithm.values()) {
			assertEquals(t, SignatureAlgorithm.valueOf(t.name()));
			assertEquals(0, t.compareTo(t));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
		}
	}
}
