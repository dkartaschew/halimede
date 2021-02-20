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

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;

import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.RevokedCertificatesPane;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

public class TestRevokedColumnComparator {

	@Test
	public void columnInvalid() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = 100;

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.description)).thenReturn("1");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.description)).thenReturn("2");

		assertEquals(0, comp.compare(idx, prop1, prop2));
		assertEquals(0, comp.compare(idx, prop2, prop1));
	}

	@Test
	public void columnDescription() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_DESCRIPTION;

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.description)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.description)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.description)).thenReturn("Abc");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.description)).thenReturn("abcd");

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.description)).thenReturn("bg");

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.description)).thenReturn("zzz");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));
	}

	@Test
	public void columnDefault() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = -1;

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.description)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.description)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.description)).thenReturn("Abc");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.description)).thenReturn("abcd");

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.description)).thenReturn("bg");

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.description)).thenReturn("zzz");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));
	}

	@Test
	public void columnSubject() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_SUBJECT;

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.subject)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.subject)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.subject)).thenReturn("CN=abc");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.subject)).thenReturn("CN=abcd");

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.subject)).thenReturn("CN=bg");

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.subject)).thenReturn("CN=zzz");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

	}

	@Test
	public void columnKeyType() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_KEY_TYPE;

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.keyType)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.keyType)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.keyType)).thenReturn(KeyType.DSA_512.name());

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.keyType)).thenReturn(KeyType.DSA_2048.name());

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.keyType)).thenReturn(KeyType.EC_c2pnb176w1.name());

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.keyType)).thenReturn(KeyType.qTESLA_P_I.name());

		IssuedCertificateProperties prop5 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop5.getProperty(Key.keyType)).thenReturn(KeyType.XMSSMT_SHA2_20_2_256.name());

		IssuedCertificateProperties prop6 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop6.getProperty(Key.keyType)).thenReturn(KeyType.XMSSMT_SHA2_40_4_512.name());

		IssuedCertificateProperties prop7 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop7.getProperty(Key.keyType)).thenReturn("DSA_4096");

		IssuedCertificateProperties prop8 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop8.getProperty(Key.keyType)).thenReturn("EC_P521_NIST");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

		assertEquals(1, comp.compare(idx, prop4, prop5));
		assertEquals(-1, comp.compare(idx, prop5, prop4));

		assertEquals(-1, comp.compare(idx, prop5, prop6));
		assertEquals(1, comp.compare(idx, prop6, prop5));

		assertEquals(-1, comp.compare(idx, prop6, prop7));
		assertEquals(1, comp.compare(idx, prop7, prop6));

		assertEquals(0, comp.compare(idx, prop7, prop8));
		assertEquals(0, comp.compare(idx, prop8, prop7));
	}

	@Test
	public void columnIssueDate() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_ISSUE_DATE;
		ZonedDateTime now = ZonedDateTime.now();

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.creationDate)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.creationDate)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.creationDate)).thenReturn(DateTimeUtil.toString(now));

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.creationDate)).thenReturn(DateTimeUtil.toString(now.plusHours(1)));

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.creationDate)).thenReturn(DateTimeUtil.toString(now.plusDays(1)));

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.creationDate)).thenReturn(DateTimeUtil.toString(now.plusDays(2)));

		IssuedCertificateProperties prop5 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop5.getProperty(Key.creationDate)).thenReturn("123445677888");

		IssuedCertificateProperties prop6 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop6.getProperty(Key.creationDate)).thenReturn("abcde");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

		assertEquals(-1, comp.compare(idx, prop1, prop5));
		assertEquals(1, comp.compare(idx, prop5, prop1));

		assertEquals(0, comp.compare(idx, prop5, prop6));
		assertEquals(0, comp.compare(idx, prop6, prop5));
	}

	@Test
	public void columnStartDate() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_START_DATE;
		ZonedDateTime now = ZonedDateTime.now();

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.startDate)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.startDate)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.startDate)).thenReturn(DateTimeUtil.toString(now));

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.startDate)).thenReturn(DateTimeUtil.toString(now.plusHours(1)));

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.startDate)).thenReturn(DateTimeUtil.toString(now.plusDays(1)));

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.startDate)).thenReturn(DateTimeUtil.toString(now.plusDays(2)));

		IssuedCertificateProperties prop5 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop5.getProperty(Key.startDate)).thenReturn("123445677888");

		IssuedCertificateProperties prop6 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop6.getProperty(Key.startDate)).thenReturn("abcde");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

		assertEquals(-1, comp.compare(idx, prop1, prop5));
		assertEquals(1, comp.compare(idx, prop5, prop1));

		assertEquals(0, comp.compare(idx, prop5, prop6));
		assertEquals(0, comp.compare(idx, prop6, prop5));
	}

	@Test
	public void columnExpiryDate() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_EXPIRY_DATE;
		ZonedDateTime now = ZonedDateTime.now();

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.endDate)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.endDate)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.endDate)).thenReturn(DateTimeUtil.toString(now));

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.endDate)).thenReturn(DateTimeUtil.toString(now.plusHours(1)));

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.endDate)).thenReturn(DateTimeUtil.toString(now.plusDays(1)));

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.endDate)).thenReturn(DateTimeUtil.toString(now.plusDays(2)));

		IssuedCertificateProperties prop5 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop5.getProperty(Key.endDate)).thenReturn("123445677888");

		IssuedCertificateProperties prop6 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop6.getProperty(Key.endDate)).thenReturn("abcde");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

		assertEquals(-1, comp.compare(idx, prop1, prop5));
		assertEquals(1, comp.compare(idx, prop5, prop1));

		assertEquals(0, comp.compare(idx, prop5, prop6));
		assertEquals(0, comp.compare(idx, prop6, prop5));
	}

	@Test
	public void columnRevokeDate() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_REVOKE_DATE;
		ZonedDateTime now = ZonedDateTime.now();

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.revokeDate)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.revokeDate)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.revokeDate)).thenReturn(DateTimeUtil.toString(now));

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.revokeDate)).thenReturn(DateTimeUtil.toString(now.plusHours(1)));

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.revokeDate)).thenReturn(DateTimeUtil.toString(now.plusDays(1)));

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.revokeDate)).thenReturn(DateTimeUtil.toString(now.plusDays(2)));

		IssuedCertificateProperties prop5 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop5.getProperty(Key.revokeDate)).thenReturn("123445677888");

		IssuedCertificateProperties prop6 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop6.getProperty(Key.revokeDate)).thenReturn("abcde");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

		assertEquals(-1, comp.compare(idx, prop1, prop5));
		assertEquals(1, comp.compare(idx, prop5, prop1));

		assertEquals(0, comp.compare(idx, prop5, prop6));
		assertEquals(0, comp.compare(idx, prop6, prop5));
	}

	@Test
	public void columnRevokeCode() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_REVOKE_REASON;

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.revokeCode)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.revokeCode)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.revokeCode)).thenReturn(RevokeReasonCode.AFFILIATION_CHANGED.name());

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.revokeCode)).thenReturn(RevokeReasonCode.CESSATION_OF_OPERATION.name());

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.revokeCode)).thenReturn(RevokeReasonCode.KEY_COMPROMISE.name());

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.revokeCode)).thenReturn(RevokeReasonCode.SUPERSEDED.name());

		IssuedCertificateProperties prop5 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop5.getProperty(Key.revokeCode)).thenReturn("123445677888");

		IssuedCertificateProperties prop6 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop6.getProperty(Key.revokeCode)).thenReturn("abcde");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

		assertEquals(-1, comp.compare(idx, prop1, prop5));
		assertEquals(1, comp.compare(idx, prop5, prop1));

		assertEquals(0, comp.compare(idx, prop5, prop6));
		assertEquals(0, comp.compare(idx, prop6, prop5));
	}

	@Test
	public void columnComment() {
		RevokedCertificateComparator comp = new RevokedCertificateComparator();

		final int idx = RevokedCertificatesPane.COLUMN_COMMENTS;

		IssuedCertificateProperties propNull = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propNull.getProperty(Key.comments)).thenReturn(null);

		IssuedCertificateProperties propEmpty = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(propEmpty.getProperty(Key.comments)).thenReturn("");

		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.comments)).thenReturn("Abc");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.comments)).thenReturn("abcd");

		IssuedCertificateProperties prop3 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop3.getProperty(Key.comments)).thenReturn("bg");

		IssuedCertificateProperties prop4 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop4.getProperty(Key.comments)).thenReturn("zzz");

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));

		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop1, prop2));
		assertEquals(1, comp.compare(idx, prop2, prop1));

		assertEquals(-1, comp.compare(idx, prop2, prop3));
		assertEquals(1, comp.compare(idx, prop3, prop2));

		assertEquals(-1, comp.compare(idx, prop3, prop4));
		assertEquals(1, comp.compare(idx, prop4, prop3));

	}
}
