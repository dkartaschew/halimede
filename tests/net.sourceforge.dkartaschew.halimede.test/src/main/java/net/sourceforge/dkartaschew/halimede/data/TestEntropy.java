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

package net.sourceforge.dkartaschew.halimede.data;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestEntropy {

	@Parameters(name = "l {0} :n {1} :r {2}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] { //
				{ -1, 0, 0.0 }, //
				{ 0, 0, 0.0 }, //
				{ 1, 4, 6.6 }, //
				{ 2, 6, 13.2 }, //
				{ 3, 8, 19.7 }, //
				{ 4, 10, 26.3 }, //
				{ 5, 12, 32.8 }, //
				{ 6, 14, 39.3 }, //
				{ 7, 16, 45.9 }, //
				{ 8, 18, 52.4 }, //
				{ 10, 21, 65.5 }, //
				{ 12, 24, 78.7 }, //
				{ 14, 27, 91.8 }, //
				{ 16, 30, 104.8 }, //
				{ 18, 33, 118.0 }, //
				{ 20, 36, 131.1 }, //
				{ 22, 38, 144.2 }, //
				{ 24, 40, 157.3 }, //
				{ 30, 46, 196.6 }, //
				{ 40, 56, 262.1 }//
		});
	}

	private int length;
	private double nist;
	private double random;

	public TestEntropy(int length, double nist, double random) {
		this.length = length;
		this.nist = nist;
		this.random = random;
	}

	@Test
	public void testObject() {
		Entropy st = new Entropy();
		assertEquals(st.hashCode(), st.hashCode());
		st.toString();
		assertEquals(st, st);
	}

	@Test
	public void nist800() {
		assertEquals(nist, Entropy.nistSP800(fillString(length, 'a')), 0.1);
	}

	@Test
	public void deafult() {
		assertEquals(nist, Entropy.calculate(fillString(length, 'a')), 0.1);
	}

	@Test
	public void randomStr() {
		// base on a 94 char address space.
		assertEquals(random, Entropy.random(fillString(length, 'a')), 0.1);
	}

	// Create a string of length.
	public static String fillString(int count, char c) {
		if (count < 0) {
			return null;
		}
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			sb.append(c);
		}
		return sb.toString();
	}
}
