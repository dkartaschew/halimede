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

package net.sourceforge.dkartaschew.halimede.util;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class TestDigests {

	private byte[] data() {
		return "The quick brown fox".getBytes(StandardCharsets.UTF_8);
	}

	@Test
	public void testObject() {
		Digest st = new Digest();
		assertEquals(st.hashCode(), st.hashCode());
		st.toString();
		assertEquals(st, st);
	}

	@Test(expected = NullPointerException.class)
	public void nullData() {
		Digest.md5(null);
	}

	@Test
	public void md5() {
		assertEquals("a2004f37730b9445670a738fa0fc9ee5", //
				Strings.toHexString(Digest.md5(data())));
	}

	@Test
	public void sha1() {
		assertEquals("c519c1a06cdbeb2bc499e22137fb48683858b345", //
				Strings.toHexString(Digest.sha1(data())));
	}

	@Test
	public void sha256() {
		assertEquals("5cac4f980fedc3d3f1f99b4be3472c9b30d56523e632d151237ec9309048bda9", //
				Strings.toHexString(Digest.sha256(data())));
	}

	@Test
	public void sha384() {
		assertEquals("2e45933dd1a1e6a6928a732d58abeb180c225e5e7b99c64eb6f233a7b99ee4635c17f44ca544cf620cf4289deb4c08cf", //
				Strings.toHexString(Digest.sha384(data())));
	}

	@Test
	public void sha512() {
		assertEquals(
				"015e6d23e760f612cca616c54f110cb12dd54213f1e046c7607081372402eff4936b379296ed549236020afb37bd3e728a044a4243754f095498c98bc24f77e0", //
				Strings.toHexString(Digest.sha512(data())));
	}

	@Test
	public void gost3411() {
		assertEquals("4ffab0480add23e6018a46fc7f6696298ef714a9a97f6353e3d2925a177542bd", //
				Strings.toHexString(Digest.gost3411(data())));
	}

	@Test
	public void gost3411_2012_256() {
		assertEquals("2a47e26fb8fd4b46668fb8835b3f8966a692ad062d17398a907f025ba4762aa7", //
				Strings.toHexString(Digest.gost3411_2012_256(data())));
	}

	@Test
	public void gost3411_2012_512() {
		assertEquals(
				"4671da46d7bf2fdc33d13502c7d0ceb7bbbf49bf0a5413fdbb3eac07204eb4b5f572e641c212cd15879d8f29b885dbe35fbf09c0c90f58489e4738f8fa718d95", //
				Strings.toHexString(Digest.gost3411_2012_512(data())));
	}

}
