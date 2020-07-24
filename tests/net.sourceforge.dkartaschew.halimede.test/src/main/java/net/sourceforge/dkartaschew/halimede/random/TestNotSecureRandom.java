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
package net.sourceforge.dkartaschew.halimede.random;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.junit.Test;

public class TestNotSecureRandom {

	@Test
	public void performanceTestSecureRandom() throws NoSuchAlgorithmException {

		int[] rounds = { 1_000, 8, 1_000, 16, 1_000, 32, 1_000, 64, 1_000, 128, 1_000, 256, //
				100_000, 8, 100_000, 16, 100_000, 32, 100_000, 64, 100_000, 128, 100_000, 256, //
				10_000_000, 8, 10_000_000, 16, 10_000_000, 32, 10_000_000, 64, 10_000_000, 128, 10_000_000, 256, };

		SecureRandom[] rnd = { new SecureRandom(), //
				SecureRandom.getInstance("SHA1PRNG"), //
				new NotSecureRandom() };

		for (int i = 0; i < rounds.length; i += 2) {
			int count = rounds[i];
			int blockSize = rounds[i + 1];
			System.out.println();
			for (SecureRandom r : rnd) {
				long tm = test(r, count, blockSize);
				System.out.println(r.toString() + " : (" + count + "@" + blockSize + ") = " + tm);
			}
		}

	}

	/**
	 * Obtain blocksize chunks of random data
	 * 
	 * @param rnd The PRNG to test
	 * @param iterations The number of iterations
	 * @param blocksize The blocksize or number of bytes to acquire on each iteration.
	 * @return The time in msec to complete
	 */
	private long test(SecureRandom rnd, int iterations, int blocksize) {
		long start = System.currentTimeMillis();
		byte[] data = new byte[blocksize];
		while (iterations > 0) {
			rnd.nextBytes(data);
			iterations--;
		}
		return System.currentTimeMillis() - start;
	}

}
