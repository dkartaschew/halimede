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

package net.sourceforge.dkartaschew.halimede.data;

import static org.junit.Assert.assertEquals;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.util.Digest;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestUtilityFunctions {

	@Test
	public void testByteWrap_8() {
		System.out.println(Strings.toHexString(Digest.sha1("The quick brown fox".getBytes()), " ", 8));
	}

	@Test
	public void testByteWrap_10() {
		System.out.println(Strings.toHexString(Digest.sha1("The quick brown fox".getBytes()), " ", 10));
	}

	@Test
	public void testByteWrap_16() {
		System.out.println(Strings.toHexString(Digest.sha512("The quick brown fox".getBytes()), " ", 16));
	}

	@Test
	public void testByteWrap_16_2() {
		System.out.println(Strings.toHexString(Digest.gost3411("The quick brown fox".getBytes()), " ", 16));
	}

	@Test
	public void testByteWrap_16_3() {
		System.out.println(Strings.toHexString(Digest.gost3411_2012_256("The quick brown fox".getBytes()), " ", 16));
	}

	@Test
	public void testByteWrap_16_4() {
		System.out.println(Strings.toHexString(Digest.gost3411_2012_512("The quick brown fox".getBytes()), " ", 16));
	}

	@Test
	public void testStringTrimEmpty() {
		assertEquals("", Strings.trim("", 10));
	}

	@Test
	public void testStringTrimNull() {
		assertEquals("", Strings.trim(null, 10));
	}

	@Test
	public void testStringTrimSmall() {
		assertEquals("1", Strings.trim("1", 1));
	}
	
	@Test
	public void testStringTrimZero() {
		assertEquals("...", Strings.trim("1", 0));
	}
	
	@Test
	public void testStringTrimZero2() {
		assertEquals("", Strings.trim("", 0));
	}

	@Test
	public void testStringTrimSmall2() {
		assertEquals("1", Strings.trim("1", 10));
	}

	@Test
	public void testStringTrimSmall3() {
		assertEquals("The...", Strings.trim("The quick brown fox", 3));
	}

	@Test
	public void testStringTrimSmall4() {
		assertEquals("The qu...", Strings.trim("The quick brown fox", 6));
	}

	@Test
	public void testStringTrimLineSeparator() {
		assertEquals("The qu...", Strings.trim("The qu" + System.lineSeparator() + "ick brown fox", 10));
	}

	@Test
	public void testStringTrimLineSeparator2() {
		assertEquals("The qu...", Strings.trim(System.lineSeparator() + //
				System.lineSeparator() + //
				"The qu" + System.lineSeparator() + //
				"ick brown fox", 10));
	}
}
