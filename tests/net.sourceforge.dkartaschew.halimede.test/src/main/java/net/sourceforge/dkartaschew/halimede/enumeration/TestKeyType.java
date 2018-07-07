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
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.exceptions.UnknownKeyTypeException;

public class TestKeyType {

	/*
	 * Test generate key pair, and get key type from the pair.
	 */
	@Test
	public void testKeyToKeyType() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, IOException {
		Collection<KeyType> data = Arrays.stream(KeyType.values())//
				.filter(key -> (key.getBitLength() <= TestUtilities.TEST_MAX_KEY_LENGTH))//
				.collect(Collectors.toList());
		
		for(KeyType type : data) {
			KeyPair key = KeyPairFactory.generateKeyPair(type);
			PublicKey pubKey = key.getPublic();
			try (ASN1InputStream is = new ASN1InputStream(new ByteArrayInputStream(pubKey.getEncoded()));) {
				ASN1Sequence seq = (ASN1Sequence) is.readObject();
				SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(seq);
				
				KeyType keyType = KeyPairFactory.forKeyInformation(info);
				assertEquals(type, keyType);
			}
		}
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
			assertEquals(t.ordinal(),  KeyType.getIndex(t));
			index++;
		}
	}
	
	@Test(expected=NullPointerException.class)
	public void testEnumTypegetDescriptionNull() {
		assertEquals(null, KeyType.getKeyTypeDescription(null));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEnumTypegetDescriptionEmpty() {
		assertEquals(null, KeyType.getKeyTypeDescription(""));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEnumTypegetDescriptionUnknown() {
		assertEquals(null, KeyType.getKeyTypeDescription("Unknown"));
	}
	
	@Test(expected=NullPointerException.class)
	public void testForDescriptionNull() {
		assertEquals(null, KeyType.forDescription(null));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testForDescriptionEmpty() {
		assertEquals(null, KeyType.forDescription(""));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testForDescriptionUnknown() {
		assertEquals(null, KeyType.forDescription("Unknown"));
	}
	
	@Test(expected=NullPointerException.class)
	public void testForKeyNull() throws UnknownKeyTypeException {
		assertEquals(null, KeyType.forKey(null));
	}
	
	@Test(expected=UnknownKeyTypeException.class)
	public void testForKeyUnknown() throws UnknownKeyTypeException {
		PublicKey k = mock(PublicKey.class);
		when(k.getAlgorithm()).thenReturn("Unknown");
		assertEquals(null, KeyType.forKey(k));
	}
}
