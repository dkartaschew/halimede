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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestHTMLEncode {

	@Test
	public void testObject() {
		HTMLEncode st = new HTMLEncode();
		assertEquals(st.hashCode(), st.hashCode());
		st.toString();
		assertEquals(st, st);
	}
	
	@Test
	public void testHTML_NULL() {
		assertEquals("", HTMLEncode.escape(null));
	}
	
	@Test
	public void testHTML_Empty() {
		assertEquals("", HTMLEncode.escape(""));
	}
	
	@Test
	public void testHTML_Simple() {
		assertEquals("Simple", HTMLEncode.escape("Simple"));
	}
	
	@Test
	public void testHTML_Bold() {
		assertEquals("&lt;b&gt;", HTMLEncode.escape("<b>"));
	}
	
	@Test
	public void testHTML_Quote() {
		assertEquals("&quot;b&quot;", HTMLEncode.escape("\"b\""));
	}
	
	@Test
	public void testHTML_Amp() {
		assertEquals("&amp;b", HTMLEncode.escape("&b"));
	}
	
	@Test
	public void testNumericEncode() {
		char[] values = {0x80, 0x2000};
		String v = new String(values);
		assertEquals("&#x80;&#x2000;", HTMLEncode.escape(v));
	}
}
