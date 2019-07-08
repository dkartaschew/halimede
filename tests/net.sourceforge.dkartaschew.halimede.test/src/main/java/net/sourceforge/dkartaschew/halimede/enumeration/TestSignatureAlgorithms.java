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
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
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
		
		assertEquals(SignatureAlgorithm.SM3withSM2, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.EC_sm2p256v1).getPublic()));
		
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
		
		assertEquals(SignatureAlgorithm.qTESLA_I, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.qTESLA_I).getPublic()));
		
		assertEquals(SignatureAlgorithm.qTESLA_III_size, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.qTESLA_III_size).getPublic()));
		
		assertEquals(SignatureAlgorithm.qTESLA_III_speed, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.qTESLA_III_speed).getPublic()));
		
		assertEquals(SignatureAlgorithm.qTESLA_P_I, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.qTESLA_P_I).getPublic()));
		
		assertEquals(SignatureAlgorithm.qTESLA_P_III, SignatureAlgorithm
				.getDefaultSignature(KeyPairFactory.generateKeyPair(KeyType.qTESLA_P_III).getPublic()));
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
	public void testSignatureForSig_DSTU() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm
				.forType(SignatureAlgorithm.GOST3411withDSTU4145);
		assertTrue(alg.contains(SignatureAlgorithm.GOST3411withDSTU4145));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withDSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA512withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withRSA));
		assertFalse(alg.contains(SignatureAlgorithm.SHA1withECDSA));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410));
		assertFalse(alg.contains(SignatureAlgorithm.GOST3411withECGOST3410_2012_256));
	}
	
	@Test
	public void testSignatureForSig_SM2() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm
				.forType(SignatureAlgorithm.SM3withSM2);
		assertTrue(alg.contains(SignatureAlgorithm.SM3withSM2));
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
	public void testSignatureForSig_ED25519() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.ED25519);
		assertTrue(alg.contains(SignatureAlgorithm.ED25519));
		assertEquals(1, alg.size());
	}
	
	@Test
	public void testSignatureForSig_ED448() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.ED448);
		assertTrue(alg.contains(SignatureAlgorithm.ED448));
		assertEquals(1, alg.size());
	}
	
	@Test
	public void testSignatureForSig_Rainbow() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.RAINBOWwithSHA512);
		assertTrue(alg.contains(SignatureAlgorithm.RAINBOWwithSHA224));
		assertTrue(alg.contains(SignatureAlgorithm.RAINBOWwithSHA256));
		assertTrue(alg.contains(SignatureAlgorithm.RAINBOWwithSHA384));
		assertTrue(alg.contains(SignatureAlgorithm.RAINBOWwithSHA512));
		assertEquals(4, alg.size());
	}
	
	@Test
	public void testSignatureForSig_SPHINCS() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.SPHICS256withSHA512);
		assertTrue(alg.contains(SignatureAlgorithm.SPHICS256withSHA512));
		assertEquals(1, alg.size());
		
		alg = SignatureAlgorithm.forType(SignatureAlgorithm.SPHINCS256withSHA3_512);
		assertTrue(alg.contains(SignatureAlgorithm.SPHINCS256withSHA3_512));
		assertEquals(1, alg.size());
	}
	
	@Test
	public void testSignatureForSig_XMSS() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.XMSSwithSHA256);
		assertTrue(alg.contains(SignatureAlgorithm.XMSSwithSHA256));
		assertTrue(alg.contains(SignatureAlgorithm.XMSSwithSHA512));
		assertTrue(alg.contains(SignatureAlgorithm.XMSSwithSHAKE128));
		assertTrue(alg.contains(SignatureAlgorithm.XMSSwithSHAKE256));
		assertEquals(4, alg.size());
	}
	
	@Test
	public void testSignatureForSig_XMSSMT() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.XMSSMTwithSHA256);
		assertTrue(alg.contains(SignatureAlgorithm.XMSSMTwithSHA256));
		assertTrue(alg.contains(SignatureAlgorithm.XMSSMTwithSHA512));
		assertTrue(alg.contains(SignatureAlgorithm.XMSSMTwithSHAKE128));
		assertTrue(alg.contains(SignatureAlgorithm.XMSSMTwithSHAKE256));
		assertEquals(4, alg.size());
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
	
	@Test
	public void testSignatureForSig_qTELSA() {
		Collection<SignatureAlgorithm> alg = SignatureAlgorithm.forType(SignatureAlgorithm.qTESLA_I);
		assertTrue(alg.contains(SignatureAlgorithm.qTESLA_I));
		assertEquals(1, alg.size());
		
		alg = SignatureAlgorithm.forType(SignatureAlgorithm.qTESLA_III_size);
		assertTrue(alg.contains(SignatureAlgorithm.qTESLA_III_size));
		assertEquals(1, alg.size());
		
		alg = SignatureAlgorithm.forType(SignatureAlgorithm.qTESLA_III_speed);
		assertTrue(alg.contains(SignatureAlgorithm.qTESLA_III_speed));
		assertEquals(1, alg.size());
		
		alg = SignatureAlgorithm.forType(SignatureAlgorithm.qTESLA_P_I);
		assertTrue(alg.contains(SignatureAlgorithm.qTESLA_P_I));
		assertEquals(1, alg.size());
		
		alg = SignatureAlgorithm.forType(SignatureAlgorithm.qTESLA_P_III);
		assertTrue(alg.contains(SignatureAlgorithm.qTESLA_P_III));
		assertEquals(1, alg.size());
	}
	
	@Test
	public void testProvider() {
		assertEquals(BouncyCastleProvider.PROVIDER_NAME, SignatureAlgorithm.MD5withRSA.getProvider());
		assertEquals(BouncyCastleProvider.PROVIDER_NAME, SignatureAlgorithm.SHA1withDSA.getProvider());
		assertEquals(BouncyCastleProvider.PROVIDER_NAME, SignatureAlgorithm.SHA3_512withECDSA.getProvider());
		assertEquals(BouncyCastleProvider.PROVIDER_NAME, SignatureAlgorithm.GOST3411withDSTU4145.getProvider());
		assertEquals(BouncyCastleProvider.PROVIDER_NAME, SignatureAlgorithm.SM3withSM2.getProvider());
		assertEquals(BouncyCastleProvider.PROVIDER_NAME, SignatureAlgorithm.GOST3411withECGOST3410.getProvider());
		assertEquals(BouncyCastlePQCProvider.PROVIDER_NAME, SignatureAlgorithm.XMSSMTwithSHA256.getProvider());
		assertEquals(BouncyCastlePQCProvider.PROVIDER_NAME, SignatureAlgorithm.XMSSwithSHA256.getProvider());
		assertEquals(BouncyCastlePQCProvider.PROVIDER_NAME, SignatureAlgorithm.RAINBOWwithSHA224.getProvider());
		assertEquals(BouncyCastlePQCProvider.PROVIDER_NAME, SignatureAlgorithm.qTESLA_I.getProvider());
		assertEquals(BouncyCastlePQCProvider.PROVIDER_NAME, SignatureAlgorithm.SPHICS256withSHA512.getProvider());
	}
	
	@Test
	public void testInDirectory() {
		assertTrue(SignatureAlgorithm.SHA1withRSA.isInBCCentralDirectory());
		assertTrue(SignatureAlgorithm.qTESLA_P_III.isInBCCentralDirectory());
		assertFalse(SignatureAlgorithm.ED25519.isInBCCentralDirectory());
		assertFalse(SignatureAlgorithm.GOST3411withDSTU4145.isInBCCentralDirectory());
		assertFalse(SignatureAlgorithm.RAINBOWwithSHA224.isInBCCentralDirectory());
	}
}
