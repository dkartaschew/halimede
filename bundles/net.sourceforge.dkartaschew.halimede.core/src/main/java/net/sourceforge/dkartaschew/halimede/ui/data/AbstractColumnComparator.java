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

package net.sourceforge.dkartaschew.halimede.ui.data;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.ui.composite.IColumnComparator;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

public abstract class AbstractColumnComparator<V> implements IColumnComparator<V> {

	/**
	 * Compare the two strings as strings.
	 * <p>
	 * Note: Null strings are treated as equal to empty string.
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareString(String e1, String e2) {
		if ((e1 == null || e1.isEmpty()) && (e2 == null || e2.isEmpty())) {
			return 0;
		}
		if (e1 == null || e1.isEmpty()) {
			return 1;
		}
		if (e2 == null || e2.isEmpty()) {
			return -1;
		}
		// normalise the return result as -1, 0, or 1.
		int res = e1.compareToIgnoreCase(e2);
		return normaliseResult(res);
	}

	/**
	 * Compare the two strings as ZonedDateTime.
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareDate(String e1, String e2) {
		if ((e1 == null || e1.isEmpty()) && (e2 == null || e2.isEmpty())) {
			return 0;
		}
		if (e1 == null || e1.isEmpty()) {
			return 1;
		}
		if (e2 == null || e2.isEmpty()) {
			return -1;
		}
		ZonedDateTime d1 = null;
		try {
			d1 = DateTimeUtil.toZonedDateTime(e1);
		} catch (DateTimeParseException e) {
			// ignore
		}
		ZonedDateTime d2 = null;
		try {
			d2 = DateTimeUtil.toZonedDateTime(e2);
		} catch (DateTimeParseException e) {
			// ignore
		}
		return compareDate(d1, d2);
	}

	/**
	 * Compare the ZonedDateTime.
	 * 
	 * @param e1 The date
	 * @param e2 The date
	 * @return The comparison.
	 */
	protected int compareDate(ZonedDateTime e1, ZonedDateTime e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		return e1.compareTo(e2);
	}

	/**
	 * Compare the two strings as KeyType
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareKeyType(String e1, String e2) {
		if ((e1 == null || e1.isEmpty()) && (e2 == null || e2.isEmpty())) {
			return 0;
		}
		if (e1 == null || e1.isEmpty()) {
			return 1;
		}
		if (e2 == null || e2.isEmpty()) {
			return -1;
		}
		KeyType k1 = null;
		try {
			k1 = (KeyType) KeyType.valueOf(e1);
		} catch (IllegalArgumentException | NoSuchElementException e) {
			// ignore.
		}
		KeyType k2 = null;
		try {
			k2 = (KeyType) KeyType.valueOf(e2);
		} catch (IllegalArgumentException | NoSuchElementException e) {
			// ignore.
		}
		return compareKeyType(k1, k2);

	}

	/**
	 * Compare the two KeyType
	 * 
	 * @param e1 The first key type
	 * @param e2 The second key type
	 * @return The comparison.
	 */
	protected int compareKeyType(KeyType e1, KeyType e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		int res = e1.compare(e2);
		return normaliseResult(res);
	}

	/**
	 * Compare the two strings as Revoke Reason Code
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareRevokeReason(String e1, String e2) {
		if ((e1 == null || e1.isEmpty()) && (e2 == null || e2.isEmpty())) {
			return 0;
		}
		if (e1 == null || e1.isEmpty()) {
			return 1;
		}
		if (e2 == null || e2.isEmpty()) {
			return -1;
		}
		RevokeReasonCode k1 = null;
		try {
			k1 = (RevokeReasonCode) RevokeReasonCode.valueOf(e1);
		} catch (IllegalArgumentException | NoSuchElementException e) {
			// ignore.
		}
		RevokeReasonCode k2 = null;
		try {
			k2 = (RevokeReasonCode) RevokeReasonCode.valueOf(e2);
		} catch (IllegalArgumentException | NoSuchElementException e) {
			// ignore.
		}
		return compareRevokeReason(k1, k2);
	}

	/**
	 * Compare the two Revoke Reason Code
	 * 
	 * @param e1 The first code
	 * @param e2 The second code
	 * @return The comparison.
	 */
	protected int compareRevokeReason(RevokeReasonCode e1, RevokeReasonCode e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		int res = e1.getDescription().compareToIgnoreCase(e2.getDescription());
		return normaliseResult(res);
	}

	/**
	 * Compare the two X500Names
	 * 
	 * @param e1 The first DN
	 * @param e2 The second DN
	 * @return The comparison.
	 */
	protected int compareX500Name(X500Name e1, X500Name e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		int res = e1.toString().compareToIgnoreCase(e2.toString());
		return normaliseResult(res);
	}

	/**
	 * Compare the two strings as integers.
	 * <p>
	 * Note: Null strings are treated as equal to empty string.
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareInteger(String e1, String e2) {
		if ((e1 == null || e1.isEmpty()) && (e2 == null || e2.isEmpty())) {
			return 0;
		}
		if (e1 == null || e1.isEmpty()) {
			return 1;
		}
		if (e2 == null || e2.isEmpty()) {
			return -1;
		}
		BigInteger d1 = null;
		try {
			d1 = new BigInteger(e1);
		} catch (NumberFormatException e) {
			// ignore
		}
		BigInteger d2 = null;
		try {
			d2 = new BigInteger(e2);
		} catch (NumberFormatException e) {
			// ignore
		}
		return compareInteger(d1, d2);
	}

	/**
	 * Compare the two integers
	 * 
	 * @param e1 The first integer
	 * @param e2 The second integer
	 * @return The comparison.
	 */
	protected int compareInteger(BigInteger e1, BigInteger e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		return e1.compareTo(e2);
	}

	/**
	 * Normalise the given result
	 * 
	 * @param res The result to normalise as -1, 0, 1
	 * @return The result
	 */
	private int normaliseResult(int res) {
		if (res < -1) {
			return -1;
		}
		if (res > 1) {
			return 1;
		}
		return res;
	}
}
