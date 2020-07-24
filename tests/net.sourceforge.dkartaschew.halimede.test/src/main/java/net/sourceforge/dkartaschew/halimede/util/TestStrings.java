/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.ECPublicKey;

import org.bouncycastle.jce.provider.JCEECPublicKey;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

public class TestStrings {

	private final byte[] data = {0x00, 0x01, 0x02, 0x03, 0x04 };
	
	@Test
	public void testObject() {
		Strings st = new Strings();
		assertEquals(st.hashCode(), st.hashCode());
		st.toString();
		assertEquals(st, st);
	}
	
	@Test
	public void testAsDualValueNull() {
		assertEquals("0x0 (0)", Strings.asDualValue(null));
	}
	
	@Test
	public void testAsDualValueZero() {
		assertEquals("0x0 (0)", Strings.asDualValue(BigInteger.ZERO));
	}
	
	@Test
	public void testAsDualValueOne() {
		assertEquals("0x1 (1)", Strings.asDualValue(BigInteger.ONE));
	}
	
	@Test
	public void testAsDualValueTen() {
		assertEquals("0xa (10)", Strings.asDualValue(BigInteger.TEN));
	}
	
	@Test
	public void testTrimNull() {
		assertEquals("", Strings.trim(null, 1));
	}
	
	@Test
	public void testTrimNullNegative() {
		assertEquals("", Strings.trim(null, -1));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testTrimStringNegative() {
		Strings.trim("", -1);
	}
	
	@Test
	public void testTrimStringZeroLength() {
		assertEquals("...", Strings.trim("abc", 0));
	}
	
	@Test
	public void testTrimStringOneLength() {
		assertEquals("a...", Strings.trim("abc", 1));
	}
	
	@Test
	public void testTrimStringNoTrim() {
		assertEquals("abc", Strings.trim("abc", 10));
	}
	
	@Test
	public void testTrimStringWhitespaceTrim() {
		assertEquals("abc", Strings.trim("                abc                    ", 10));
	}
	
	@Test
	public void testTrimStringLineSeparator() {
		assertEquals("abc", Strings.trim("                abc                    "  + System.lineSeparator(), 10));
	}
	
	@Test
	public void testTrimStringLineSeparatorStart() {
		assertEquals("abc", Strings.trim(System.lineSeparator() + "                abc                    "  + System.lineSeparator(), 10));
	}
	
	@Test
	public void testTrimStringLineSeparatorMid() {
		assertEquals("....", Strings.trim("." + System.lineSeparator() + "                abc                    "  + System.lineSeparator(), 10));
	}
	
	@Test
	public void testToHextSimple() {
		assertEquals("0001020304", Strings.toHexString(data));
	}
	
	@Test
	public void testToHextSimpleEmpty() {
		assertEquals("", Strings.toHexString(new byte[0]));
	}
	
	@Test(expected=NullPointerException.class)
	public void testToHexSimpleNull() {
		Strings.toHexString(null);
	}
	
	@Test
	public void testFromHextSimple() {
		assertArrayEquals(data, Strings.fromHexString("0001020304"));
	}
	
	@Test
	public void testFromHextSimpleEmpty() {
		assertArrayEquals(new byte[0], Strings.fromHexString(""));
	}
	
	@Test(expected=NullPointerException.class)
	public void testFromHexSimpleNull() {
		Strings.fromHexString(null);
	}
	
	@Test
	public void testToHexSeparator() {
		assertEquals("0001020304", Strings.toHexString(data, "", Integer.MAX_VALUE));
	}
	
	@Test
	public void testToHexSeparatorEmpty() {
		assertEquals("", Strings.toHexString(new byte[0], "", 0));
	}
	
	@Test(expected=NullPointerException.class)
	public void testToHexSeparatorNull() {
		Strings.toHexString(null, null, 0);
	}
	
	@Test
	public void testToHexSeparatorSpace() {
		assertEquals("00 01 02 03 04", Strings.toHexString(data, " ", Integer.MAX_VALUE));
	}
	
	@Test
	public void testToHexSeparatorSpaceWrap0() {
		assertEquals("00 01 02 03 04", Strings.toHexString(data, " ", 0));
	}
	
	@Test
	public void testToHexSeparatorSpaceWrap1() {
		String cr = System.lineSeparator();
		assertEquals("00" + cr + "01" + cr + "02" + cr + "03" + cr + "04", Strings.toHexString(data, " ", 1));
	}
	
	@Test
	public void testToHexSeparatorSpaceWrap2() {
		String cr = System.lineSeparator();
		assertEquals("00 01" + cr + "02 03" + cr + "04", Strings.toHexString(data, " ", 2));
	}
	
	@Test
	public void testToHexSeparatorPrefix() {
		assertEquals("0001020304", Strings.toHexString(data, "", Integer.MAX_VALUE, ""));
	}
	
	@Test
	public void testToHexSeparatorPrefixEmpty() {
		assertEquals("", Strings.toHexString(new byte[0], "", 0, ""));
	}
	
	@Test(expected=NullPointerException.class)
	public void testToHexSeparatorPrefixNull() {
		Strings.toHexString(null, null, Integer.MAX_VALUE, null);
	}
	
	@Test
	public void testToHexSeparatorPrefixSpace() {
		assertEquals("00 01 02 03 04", Strings.toHexString(data, " ", Integer.MAX_VALUE, " "));
	}
	
	@Test
	public void testToHexSeparatorSpacePrefixWrap0() {
		assertEquals("00 01 02 03 04", Strings.toHexString(data, " ", 0, System.lineSeparator()));
	}
	
	@Test
	public void testToHexSeparatorSpacePrefixWrap1() {
		String cr = System.lineSeparator()  + "  ";
		assertEquals("00" + cr + "01" + cr + "02" + cr + "03" + cr + "04", Strings.toHexString(data, " ", 1, "  "));
	}
	
	@Test
	public void testToHexSeparatorSpacePrefixWrapNULL() {
		String cr = System.lineSeparator();
		assertEquals("00" + cr + "01" + cr + "02" + cr + "03" + cr + "04", Strings.toHexString(data, " ", 1, null));
	}
	
	@Test
	public void testToHexSeparatorSpacePrefixWrap2() {
		String cr = System.lineSeparator() + "abc " ;
		assertEquals("00 01" + cr + "02 03" + cr + "04", Strings.toHexString(data, " ", 2, "abc "));
	}
	
	@Test
	public void testPrettyPinkNull() {
		assertEquals("", Strings.prettyPrint(null));
	}
	
	@Test
	public void testPrettyPinkRSA() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.RSA_512);
		assertNotNull(key.getPublic());
		assertNotEquals("", Strings.prettyPrint(key.getPublic()));
	}
	
	@Test
	public void testPrettyPinkDSA() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.DSA_512);
		assertNotNull(key.getPublic());
		assertNotEquals("", Strings.prettyPrint(key.getPublic()));
	}
	
	@Test
	public void testPrettyPinkECDSA() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.EC_prime192v1);
		assertNotNull(key.getPublic());
		assertNotEquals("", Strings.prettyPrint(key.getPublic()));
	}
	
	@Test
	public void testPrettyPinkECDSA2() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.EC_prime192v1);
		assertNotNull(key.getPublic());
		JCEECPublicKey ec = new JCEECPublicKey((ECPublicKey) key.getPublic());
		assertNotEquals("", Strings.prettyPrint(ec));
	}
	
	@Test
	public void testPrettyPinkECGOST() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.GOST_3410_2001_A);
		assertNotNull(key.getPublic());
		assertNotEquals("", Strings.prettyPrint(key.getPublic()));
	}
	
	@Test
	public void testPrettyPinkGOST() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.GOST_3410_94_A);
		assertNotNull(key.getPublic());
		assertNotEquals("", Strings.prettyPrint(key.getPublic()));
	}
}
