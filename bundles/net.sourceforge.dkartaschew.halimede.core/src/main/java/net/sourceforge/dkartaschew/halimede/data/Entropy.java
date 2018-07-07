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

package net.sourceforge.dkartaschew.halimede.data;

/**
 * Entropy calculation helpers.
 */
public class Entropy {

	/**
	 * Max entropy length for strength bar.
	 */
	public static final int MAX = 256;

	/**
	 * Possible message to display for strength indicator for Random entropy calculation.
	 */
	public final static String MESSAGE_RANDOM = "The strength is based on assumption that the phrase is pure "
			+ "random from a 94 character address space. Full length reports at least " + MAX + "bits of entropy has "
			+ "been reached with the mentioned assumption.";

	/**
	 * Possible message to display for strength indicator for NIST entropy calculation.
	 */
	public final static String MESSAGE_NIST = "The strength is based on NIST SP800-63-2 (2013). See Appendix A "
			+ "for details. Full length reports at least " + MAX + "bits of entropy has been reached with the"
			+ " mentioned assumption.";

	/**
	 * Calculate entry of the phrase.
	 * <p>
	 * The method is based on NIST SP800-63-2 (2013). See Appendix A for details.
	 * 
	 * @param phrase The phrase to calculate entropy
	 * @return The entropy in bits.
	 */
	public static double calculate(String phrase) {
		return nistSP800(phrase);
	}

	/**
	 * Calculate entry of the phrase.
	 * <p>
	 * The method is based on NIST SP800-63-2 (2013). See Appendix A for details.
	 * 
	 * @param phrase The phrase to calculate entropy
	 * @return The entropy in bits.
	 */
	public static double nistSP800(String phrase) {
		if (phrase == null) {
			return 0;
		}
		int length = phrase.length();
		if (length == 0) {
			return 0;
		}
		if (length == 1) {
			return 4;
		}
		if (length < 8) {
			return 4 + (length - 1) * 2;
		}
		double entropy = 0.0;
		if (length < 20) {
			entropy = 18 + ((length - 8) * 1.5);
		} else {
			entropy = 36 + (length - 20);
		}
		return entropy;
	}

	/**
	 * Calculate entry of the phrase.
	 * <p>
	 * The method is based on assumption that the phrase is pure random based on a 94 character address space.
	 * 
	 * @param phrase The phrase to calculate entropy
	 * @return The entropy in bits.
	 */
	public static double random(String phrase) {
		return random(phrase, 94);
	}

	/**
	 * Calculate entry of the phrase.
	 * <p>
	 * The method is based on assumption that the phrase is pure random based on the defined character address space.
	 * 
	 * @param phrase The phrase to calculate entropy
	 * @param addressSpace The size ofthe address space
	 * @return The entropy in bits.
	 */
	public static double random(String phrase, int addressSpace) {
		if (phrase == null) {
			return 0;
		}
		int length = phrase.length();
		double entropy = length * (Math.log(addressSpace) / Math.log(2.0));
		return entropy;
	}
}
