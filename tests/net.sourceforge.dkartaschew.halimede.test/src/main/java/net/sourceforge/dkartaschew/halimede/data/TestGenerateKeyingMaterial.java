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

package net.sourceforge.dkartaschew.halimede.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.interfaces.ECPrivateKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestGenerateKeyingMaterial {

	/*
	 * TRUE to only run those ciphers that are quick to generate.
	 */
	public final static boolean SHORT = true;
	
	@BeforeClass
	public static void setup() throws NoSuchAlgorithmException {
		CryptoServicesRegistrar.setSecureRandom(SecureRandom.getInstance("SHA1PRNG"));
	}
	
	@AfterClass
	public static void teardown() {
		CryptoServicesRegistrar.setSecureRandom(null);
	}
	
	@Parameters(name = "{0}")
	public static Collection<KeyType> data() {
		return Collections.singletonList(KeyType.qTESLA_I);
		
		// Always do all key type here.
//		if(!SHORT)
//			return Arrays.stream(KeyType.values()).collect(Collectors.toList());
//
//		// Only do for keying material of 2048 bits or less.
//		KeyTypeWarningValidator v = new KeyTypeWarningValidator();
//		Collection<KeyType> data = Arrays.stream(KeyType.values())//
//				.filter(key -> (v.validate(key) == ValidationStatus.ok()))//
//				.collect(Collectors.toList());
//		return data;
	}

	private final String PASSWORD = "changeme";
	private final Path fn = Paths.get(TestUtilities.TMP, "key.p8");
	private final Path fn2 = Paths.get(TestUtilities.TMP, "key.pub");
	private final KeyType type;
	private static KeyPair key;
	private static KeyType ktype;

	public TestGenerateKeyingMaterial(KeyType type)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		this.type = type;
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testKeyGenerateDER() throws Exception {
		generateKeyPair();
		IssuedCertificate ic = new IssuedCertificate(key, new Certificate[] { new MockCertificate("Mock") }, null, null, null);
		ic.createPKCS8(fn, PASSWORD, EncodingType.DER, PKCS8Cipher.AES_256_CBC);
		reloadAndComparePrivate(key, fn);
		ic.createPublicKey(fn2, EncodingType.DER);
		reloadAndComparePublic(key, fn2);
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testKeyGenerateDERPlain() throws Exception {
		generateKeyPair();
		IssuedCertificate ic = new IssuedCertificate(key, new Certificate[] { new MockCertificate("Mock") }, null, null, null);
		ic.createPKCS8(fn, null, EncodingType.DER, PKCS8Cipher.AES_256_CBC);
		reloadAndComparePrivate(key, fn);
		ic.createPublicKey(fn2, EncodingType.DER);
		reloadAndComparePublic(key, fn2);
	}
	
	/*
	 * Key Generation in PEM (no alias defined).
	 */
	@Test
	public void testKeyGeneratePEM() throws Exception {
		generateKeyPair();
		IssuedCertificate ic = new IssuedCertificate(key, new Certificate[] { new MockCertificate("Mock") }, null, null, null);
		ic.createPKCS8(fn, PASSWORD, EncodingType.PEM, PKCS8Cipher.AES_256_CBC);
		reloadAndComparePrivate(key, fn);
		ic.createPublicKey(fn2, EncodingType.PEM);
		reloadAndComparePublic(key, fn2);
	}
	
	/*
	 * Key Generation in PEM (no alias defined).
	 */
	@Test
	public void testKeyGeneratePEMPLain() throws Exception {
		generateKeyPair();
		IssuedCertificate ic = new IssuedCertificate(key, new Certificate[] { new MockCertificate("Mock") }, null, null, null);
		ic.createPKCS8(fn, null, EncodingType.PEM, PKCS8Cipher.AES_256_CBC);
		reloadAndComparePrivate(key, fn);
		ic.createPublicKey(fn2, EncodingType.PEM);
		reloadAndComparePublic(key, fn2);
	}

	/*
	 * Ensure guessed key length is ok...
	 */
	@Test
	public void testKeyLength() throws Exception {
		generateKeyPair();
		int length = KeyPairFactory.getKeyLength(key);
		assertEquals(type.getBitLength(), length);
	}

	/*
	 * Test pretty print...
	 */
	@Test
	public void testPublicKeyPrint() throws Exception {
		generateKeyPair();
		TestUtilities.displayKeys(key);
		String s = Strings.prettyPrint(key.getPublic());
		assertNotEquals("", s);
		System.out.println(s);
	}

	/**
	 * Reload and compare the keying material
	 * 
	 * @param key The key to save/restore
	 * @param fn The filename to use
	 */
	private void reloadAndComparePrivate(KeyPair key, Path fn) throws Exception {
		// reload
		try {
			PKCS8Decoder pkcs8 = PKCS8Decoder.open(fn, PASSWORD);
			PrivateKey key2 = pkcs8.getKeyPair().getPrivate();
			if (key.getPrivate() instanceof ECPrivateKey) {
				ECPrivateKey k = (ECPrivateKey) key.getPrivate();
				assertEquals(k.getS(), ((ECPrivateKey) key2).getS());
			} else {
				assertEquals(key.getPrivate(), key2);
			}
		} finally {
			TestUtilities.delete(fn);
		}
	}

	/**
	 * Reload and compare the keying material
	 * 
	 * @param key The key to save/restore
	 * @param fn The filename to use
	 */
	private void reloadAndComparePublic(KeyPair key, Path fn) throws Exception {
		// reload
		try {
			PublicKey pkey = PublicKeyDecoder.open(fn);
			assertEquals(key.getPublic(), pkey);
		} finally {
			TestUtilities.delete(fn);
		}
	}

	/**
	 * Generate key pair if needed.
	 * 
	 * @throws NoSuchAlgorithmException The keytype is invalid
	 * @throws NoSuchProviderException BC is not loaded?
	 * @throws InvalidAlgorithmParameterException The parameters for the key type are invalid.
	 */
	private void generateKeyPair()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		if (ktype == null || type != ktype) {
			ktype = type;
			key = KeyPairFactory.generateKeyPair(type);
		}
	}

}
