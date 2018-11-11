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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.exceptions.UnknownKeyTypeException;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;

public class TestKeyType {

	@After
	public void tearDown() {
		System.clearProperty(KeyType.ALLOWED);
		System.clearProperty(KeyType.DEFAULT);
	}

	@Before
	public void setup() {
		System.clearProperty(KeyType.ALLOWED);
		System.clearProperty(KeyType.DEFAULT);
	}

	/*
	 * Test generate key pair, and get key type from the pair.
	 */
	@Test
	public void testKeyToKeyType()
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException {
		KeyTypeWarningValidator v = new KeyTypeWarningValidator();

		// Only do for keying material of 2048 bits or less.
		Collection<KeyType> data = Arrays.stream(KeyType.values())//
				.filter(key -> (v.validate(key) == ValidationStatus.ok()))//
				.collect(Collectors.toList());

		for (KeyType type : data) {
			KeyPair key = KeyPairFactory.generateKeyPair(type);
			PublicKey pubKey = key.getPublic();
			try (ASN1InputStream is = new ASN1InputStream(new ByteArrayInputStream(pubKey.getEncoded()));) {
				ASN1Sequence seq = (ASN1Sequence) is.readObject();
				SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(seq);

				KeyType keyType = KeyPairFactory.forKeyInformation(info);
				if (keyType != type) {
					assertTrue(isEquivalent(type, keyType));
					// System.out.println(String.format("if(type == KeyType.%s && keyType ==
					// KeyType.%s) return true;", type.toString(), keyType.toString()));
				} else {
					assertEquals(type, keyType);
				}
			}
		}
	}

	private boolean isEquivalent(KeyType type, KeyType keyType) {
		if (type == KeyType.EC_secp192r1 && keyType == KeyType.EC_prime192v1)
			return true;
		if (type == KeyType.EC_secp256r1 && keyType == KeyType.EC_prime256v1)
			return true;
		if (type == KeyType.EC_B163 && keyType == KeyType.EC_sect163r2)
			return true;
		if (type == KeyType.EC_B233 && keyType == KeyType.EC_sect233r1)
			return true;
		if (type == KeyType.EC_B283 && keyType == KeyType.EC_sect283r1)
			return true;
		if (type == KeyType.EC_B409 && keyType == KeyType.EC_sect409r1)
			return true;
		if (type == KeyType.EC_B571 && keyType == KeyType.EC_sect571r1)
			return true;
		if (type == KeyType.EC_K163 && keyType == KeyType.EC_sect163k1)
			return true;
		if (type == KeyType.EC_K233 && keyType == KeyType.EC_sect233k1)
			return true;
		if (type == KeyType.EC_K283 && keyType == KeyType.EC_sect283k1)
			return true;
		if (type == KeyType.EC_K409 && keyType == KeyType.EC_sect409k1)
			return true;
		if (type == KeyType.EC_K571 && keyType == KeyType.EC_sect571k1)
			return true;
		if (type == KeyType.EC_P192 && keyType == KeyType.EC_prime192v1)
			return true;
		if (type == KeyType.EC_P224 && keyType == KeyType.EC_secp224r1)
			return true;
		if (type == KeyType.EC_P256 && keyType == KeyType.EC_prime256v1)
			return true;
		if (type == KeyType.EC_P384 && keyType == KeyType.EC_secp384r1)
			return true;
		if (type == KeyType.EC_P521 && keyType == KeyType.EC_secp521r1)
			return true;
		return false;
	}

	@Test
	public void testEnum() {
		for (KeyType t : KeyType.values()) {
			assertEquals(t, KeyType.valueOf(t.name()));
			assertEquals(0, t.compareTo(t));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
			System.out.println(t.getDescription());

			assertEquals(t.getDescription(), KeyType.getKeyTypeDescription(t.name()));
			assertEquals(t, KeyType.forDescription(t.getDescription()));
			assertEquals(-1, t.compare(null));
			assertEquals(0, t.compare(t));
			for (KeyType u : KeyType.values()) {
				u.compare(t);
			}
		}
	}

	@Test
	public void testEnumIndex() {
		assertEquals(0, KeyType.getIndex(null));
		int index = 0;
		for (KeyType t : KeyType.values()) {
			assertEquals(index, KeyType.getIndex(t));
			assertEquals(t.ordinal(), KeyType.getIndex(t));
			index++;
		}
	}

	@Test(expected = NullPointerException.class)
	public void testEnumTypegetDescriptionNull() {
		assertEquals(null, KeyType.getKeyTypeDescription(null));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEnumTypegetDescriptionEmpty() {
		assertEquals(null, KeyType.getKeyTypeDescription(""));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEnumTypegetDescriptionUnknown() {
		assertEquals(null, KeyType.getKeyTypeDescription("Unknown"));
	}

	@Test(expected = NullPointerException.class)
	public void testForDescriptionNull() {
		assertEquals(null, KeyType.forDescription(null));
	}

	@Test(expected = NoSuchElementException.class)
	public void testForDescriptionEmpty() {
		assertEquals(null, KeyType.forDescription(""));
	}

	@Test(expected = NoSuchElementException.class)
	public void testForDescriptionUnknown() {
		assertEquals(null, KeyType.forDescription("Unknown"));
	}

	@Test(expected = NullPointerException.class)
	public void testForKeyNull() throws UnknownKeyTypeException {
		assertEquals(null, KeyType.forKey(null));
	}

	@Test(expected = UnknownKeyTypeException.class)
	public void testForKeyUnknown() throws UnknownKeyTypeException {
		PublicKey k = mock(PublicKey.class);
		when(k.getAlgorithm()).thenReturn("Unknown");
		assertEquals(null, KeyType.forKey(k));
	}

	@Test
	public void testDefaultAllowedKeys() {
		Set<KeyType> available = new HashSet<KeyType>(Arrays.asList(KeyType.values()));
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertEquals(available, allowed);
	}

	@Test
	public void testNoAllowedKeys_Empty() {
		System.setProperty(KeyType.ALLOWED, "");
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.size() == 1);
		assertTrue(allowed.contains(KeyType.getDefaultKeyType()));
	}

	@Test
	public void testNoAllowedKeys_Whitespace() {
		System.setProperty(KeyType.ALLOWED, "  ");
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.size() == 1);
		assertTrue(allowed.contains(KeyType.getDefaultKeyType()));
	}

	@Test
	public void testDefaultAllowedKeys_NoMatch() {
		System.setProperty(KeyType.ALLOWED, "UNKNOWN");
		Set<KeyType> available = new HashSet<KeyType>(Arrays.asList(KeyType.values()));
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertEquals(available, allowed);
	}

	@Test
	public void testRSAAllowed() {
		testAllowedByType("RSA");
	}

	@Test
	public void testECDSAAllowed() {
		testAllowedByType("ECDSA");
	}

	@Test
	public void testDSAAllowed() {
		testAllowedByType("DSA");
	}

	@Test
	public void testGOST3410Allowed() {
		testAllowedByType("GOST3410");
	}

	@Test
	public void testECGOST3410Allowed() {
		testAllowedByType("ECGOST3410");
	}

	@Test
	public void testECGOST34102012Allowed() {
		testAllowedByType("ECGOST3410-2012");
	}

	@Test
	public void testDSTU4145Allowed() {
		testAllowedByType("DSTU4145");
	}

	@Test
	public void testRainbowAllowed() {
		testAllowedByType("Rainbow");
	}

	@Test
	public void testSPHINCS256Allowed() {
		testAllowedByType("SPHINCS256");
	}

	@Test
	public void testXMSSAllowed() {
		testAllowedByType("XMSS");
	}

	@Test
	public void testXMSSMTAllowed() {
		testAllowedByType("XMSSMT");
	}
	
	@Test
	public void testRSADSAAllowed() {
		testAllowedByType("RSA" , "DSA");
	}

	@Test
	public void testGOSTECGOSTAllowed() {
		testAllowedByType("GOST3410" , "ECGOST3410");
	}
	
	@Test
	public void testRSASubsetAllowed() {
		System.setProperty(KeyType.ALLOWED, "RSA -RSA_512 -RSA_1024");
		System.setProperty(KeyType.DEFAULT, "RSA_16384");
		assertEquals(4, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.RSA_2048));
		assertTrue(allowed.contains(KeyType.RSA_4096));
		assertTrue(allowed.contains(KeyType.RSA_8192));
		assertTrue(allowed.contains(KeyType.RSA_16384));
		assertEquals(KeyType.RSA_16384, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testRSASubsetAllowedWithTypo() {
		System.setProperty(KeyType.ALLOWED, "RSA -RSA_512 -RAS_1024");
		System.setProperty(KeyType.DEFAULT, "RSA_16384");
		assertEquals(5, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.RSA_512));
		assertTrue(allowed.contains(KeyType.RSA_2048));
		assertTrue(allowed.contains(KeyType.RSA_4096));
		assertTrue(allowed.contains(KeyType.RSA_8192));
		assertTrue(allowed.contains(KeyType.RSA_16384));
		assertEquals(KeyType.RSA_16384, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testRainbow() {
		System.setProperty(KeyType.ALLOWED, "Rainbow");
		System.setProperty(KeyType.DEFAULT, "Rainbow");
		assertEquals(1, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.Rainbow));
		assertEquals(KeyType.Rainbow, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testRainbowNoneAllowed() {
		System.setProperty(KeyType.ALLOWED, "Rainbow -Rainbow");
		System.setProperty(KeyType.DEFAULT, "Rainbow");
		assertEquals(1, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.EC_secp521r1));
		assertEquals(KeyType.EC_secp521r1, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testAllECGOST() {
		System.setProperty(KeyType.ALLOWED, "ECGOST3410*");
		System.setProperty(KeyType.DEFAULT, "GOST_3410_2012_256_A");
		assertEquals(9, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_C));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_XA));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_XB));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_256_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_C));
		assertEquals(KeyType.GOST_3410_2012_256_A, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testAllECGOSTByName() {
		System.setProperty(KeyType.ALLOWED, "GOST_3410_20*");
		System.setProperty(KeyType.DEFAULT, "GOST_3410_2012_256_A");
		assertEquals(9, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_C));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_XA));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_XB));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_256_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_C));
		assertEquals(KeyType.GOST_3410_2012_256_A, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testAllGOSTR3410() {
		System.setProperty(KeyType.ALLOWED, "ECGOST3410* GOST3410*");
		System.setProperty(KeyType.DEFAULT, "GOST_3410_2012_256_A");
		assertEquals(12, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_XA));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_C));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_XA));
		assertTrue(allowed.contains(KeyType.GOST_3410_2001_XB));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_256_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_2012_512_C));
		assertEquals(KeyType.GOST_3410_2012_256_A, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testAllGOSTR3410_94() {
		System.setProperty(KeyType.ALLOWED, "GOST3410*");
		System.setProperty(KeyType.DEFAULT, "GOST_3410_94_XA");
		assertEquals(3, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_XA));
		assertEquals(KeyType.GOST_3410_94_XA, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testAllGOSTR3410_94_BadDefault() {
		System.setProperty(KeyType.ALLOWED, "GOST3410*");
		System.setProperty(KeyType.DEFAULT, "GOST_3410_94_XC");
		assertEquals(3, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_XA));
		assertEquals(KeyType.GOST_3410_94_A, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testAllGOSTR3410_94_BadDefault2() {
		System.setProperty(KeyType.ALLOWED, "GOST3410*");
		System.setProperty(KeyType.DEFAULT, "GOST_3410_2001_XB");
		assertEquals(3, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_A));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_B));
		assertTrue(allowed.contains(KeyType.GOST_3410_94_XA));
		assertEquals(KeyType.GOST_3410_94_A, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDSA_RSA_Removed() {
		System.setProperty(KeyType.ALLOWED, "RSA DSA -RSA");
		System.setProperty(KeyType.DEFAULT, "DSA_1024");
		assertEquals(4, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.contains(KeyType.DSA_512));
		assertTrue(allowed.contains(KeyType.DSA_1024));
		assertTrue(allowed.contains(KeyType.DSA_2048));
		assertTrue(allowed.contains(KeyType.DSA_3072));
		assertEquals(KeyType.DSA_1024, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_Empty() {
		System.setProperty(KeyType.DEFAULT, "");
		assertEquals(KeyType.EC_secp521r1, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_Whitespace() {
		System.setProperty(KeyType.DEFAULT, "   ");
		assertEquals(KeyType.EC_secp521r1, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_NoMatch() {
		System.setProperty(KeyType.DEFAULT, "UNKNOWN");
		assertEquals(KeyType.EC_secp521r1, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_RSAOnly() {
		System.setProperty(KeyType.ALLOWED, "RSA");
		assertEquals(KeyType.RSA_512, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_RSA() {
		System.setProperty(KeyType.ALLOWED, "RSA");
		System.setProperty(KeyType.DEFAULT, "RSA_4096");
		assertEquals(KeyType.RSA_4096, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_RSAOnly_NoDefaultInSet() {
		System.setProperty(KeyType.ALLOWED, "RSA");
		System.setProperty(KeyType.DEFAULT, "DSA_1024");
		assertEquals(KeyType.RSA_512, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_XMMSOnly() {
		System.setProperty(KeyType.ALLOWED, "XMSS");
		assertEquals(KeyType.XMSS_SHA2_10_256, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_XMMS() {
		System.setProperty(KeyType.ALLOWED, "XMSS");
		System.setProperty(KeyType.DEFAULT, "XMSS_SHAKE_16_256");
		assertEquals(KeyType.XMSS_SHAKE_16_256, KeyType.getDefaultKeyType());
	}
	
	@Test
	public void testDefaultKey_XMMSOnly_NoDefaultInSet() {
		System.setProperty(KeyType.ALLOWED, "XMSS");
		System.setProperty(KeyType.DEFAULT, "XMSSMT_SHA2_20_4_256");
		assertEquals(KeyType.XMSS_SHA2_10_256, KeyType.getDefaultKeyType());
	}
	
	private void testAllowedByType(String t) {
		// Set the type to a single element...
		System.setProperty(KeyType.ALLOWED, t);
		long count = Arrays.stream(KeyType.values()).filter(k -> k.getType().equals(t)).count();
		assertEquals(count, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.stream().allMatch(k -> k.getType().equals(t)));
	}

	private void testAllowedByType(String t1, String t2) {
		// Set the type to a single element...
		System.setProperty(KeyType.ALLOWED, t1 + " " + t2);
		long count = Arrays.stream(KeyType.values())//
				.filter(k -> k.getType().equals(t1) || k.getType().equals(t2)) //
				.count();
		assertEquals(count, KeyType.getAllowedValues().length);
		Set<KeyType> allowed = new HashSet<KeyType>(Arrays.asList(KeyType.getAllowedValues()));
		assertTrue(allowed.stream().allMatch(k -> k.getType().equals(t1) || k.getType().equals(t2)));
	}
}
