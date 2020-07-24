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

import java.security.SecureRandomSpi;
import java.util.Objects;

/**
 * Fast random number generator.
 * <p>
 * This PRNG implements a XORShift generator (very fast, medium quality), and is <b>NOT</b> crypto safe. <br>
 * See: <a href="http://www.jstatsoft.org/v08/i14/">XORShift generator</a> for the original paper.
 * <p>
 * This implementation should provide 10-30x performance improvement over SHA1PRNG.
 * <p>
 * This implementation is for testing purposes only where quality of SecureRandom is NOT required.
 * 
 * @author Darran Kartaschew
 * @version 1.0
 */
public class NotSecureRandomSpi extends SecureRandomSpi {

	private static final long serialVersionUID = 388958914042773601L;

	/**
	 * The current value;
	 */
	private long x = 0;

	/**
	 * Create a new Secure Random SPI instance with seed set to current time.
	 */
	public NotSecureRandomSpi() {
		x = System.currentTimeMillis();
	}

	@Override
	protected void engineSetSeed(byte[] seed) {
		Objects.requireNonNull(seed, "No seed value for engine seed");
		if (seed.length == 0) {
			return;
		}
		long s = x;
		int lastIndex = seed.length - 1;
		for (int i = 0; i < seed.length; i++) {
			s += seed[i];
			// If not last byte, then shift.
			if (i != lastIndex) {
				s <<= 8;
			}
		}
		x ^= s;
		if (x == 0) {
			x = System.currentTimeMillis();
		}
	}

	@Override
	protected void engineNextBytes(byte[] bytes) {
		fill(bytes);
	}

	@Override
	protected byte[] engineGenerateSeed(int numBytes) {
		if (numBytes <= 0) {
			throw new IllegalArgumentException("Negative size");
		}
		return fill(new byte[numBytes]);
	}

	/**
	 * Fill the given array with next values.
	 * 
	 * @param array The array to fill.
	 * @return The array filled
	 */
	private byte[] fill(byte[] array) {
		int offset = 0;
		int remaining = array.length;
		while (remaining > 0) {
			long next = nextLong();
			if (remaining >= 8) {
				fill(array, offset, next);
			} else {
				fillS(array, offset, next);
			}
			offset += 8;
			remaining -= 8;
		}
		return array;
	}

	/**
	 * Fill the array with long value
	 * 
	 * @param array The array to fill.
	 * @param offset The offset to start at
	 * @param value The value to fill into the array.
	 */
	private void fill(byte[] array, int offset, long value) {
		array[offset + 0] = (byte) (value);
		array[offset + 1] = (byte) (value >> 8);
		array[offset + 2] = (byte) (value >> 16);
		array[offset + 3] = (byte) (value >> 24);
		array[offset + 4] = (byte) (value >> 32);
		array[offset + 5] = (byte) (value >> 40);
		array[offset + 6] = (byte) (value >> 48);
		array[offset + 7] = (byte) (value >> 56);
	}

	/**
	 * Fill the array with long value.
	 * 
	 * @param array The array to fill.
	 * @param offset The offset to start at
	 * @param value The value to fill.
	 */
	private void fillS(byte[] array, int offset, long value) {
		for (int i = 0; i < 8; i++) {
			if (offset + i >= array.length) {
				return;
			}
			array[offset + i] = (byte) (value);
			value >>= 8;
		}
	}

	/**
	 * Get the next long value.
	 * 
	 * @return The next long value.
	 */
	private long nextLong() {
		/*
		 * The "magic" values of 21, 35 and 4 have been found to produce good results. With these values, the generator
		 * has a full period of (2^64)-1, and the resulting values pass Marsaglia's "Diehard battery" of statistical
		 * tests for randomness.
		 */
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		return x;
	}

}
