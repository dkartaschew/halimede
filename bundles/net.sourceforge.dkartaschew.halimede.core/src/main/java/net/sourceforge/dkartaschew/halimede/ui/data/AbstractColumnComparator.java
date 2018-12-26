/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2019 Darran Kartaschew 
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

import java.time.ZonedDateTime;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.ui.composite.IColumnComparator;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

public abstract class AbstractColumnComparator<V> implements IColumnComparator<V> {

	/**
	 * Compare the two strings as strings.
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareString(String e1, String e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		return e1.compareToIgnoreCase(e2);
	}

	/**
	 * Compare the two strings as ZonedDateTime.
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareDate(String e1, String e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		ZonedDateTime d1 = DateTimeUtil.toZonedDateTime(e1);
		ZonedDateTime d2 = DateTimeUtil.toZonedDateTime(e2);
		return d1.compareTo(d2);
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
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		KeyType k1 = (KeyType) KeyType.valueOf(e1);
		KeyType k2 = (KeyType) KeyType.valueOf(e2);
		return k1.compare(k2);
		
	}

	/**
	 * Compare the two strings as KeyType
	 * 
	 * @param e1 The first string
	 * @param e2 The second string
	 * @return The comparison.
	 */
	protected int compareRevokeReason(String e1, String e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		RevokeReasonCode k1 = (RevokeReasonCode) RevokeReasonCode.valueOf(e1);
		RevokeReasonCode k2 = (RevokeReasonCode) RevokeReasonCode.valueOf(e2);
		return k1.getDescription().compareTo(k2.getDescription());
		
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
		return e1.toString().compareToIgnoreCase(e2.toString());
	}
}
