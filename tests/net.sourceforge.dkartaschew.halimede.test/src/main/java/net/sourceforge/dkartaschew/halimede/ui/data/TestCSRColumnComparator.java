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
package net.sourceforge.dkartaschew.halimede.ui.data;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;

import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties.Key;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.PendingCertificatesPane;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

public class TestCSRColumnComparator {

	@Test
	public void columnInvalid() {
		CSRColumnComparator comp = new CSRColumnComparator();

		final int idx = 100;

		CertificateRequestProperties prop1 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop1.getProperty(Key.subject)).thenReturn("1");

		CertificateRequestProperties prop2 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop2.getProperty(Key.subject)).thenReturn("2");

		assertEquals(0, comp.compare(idx, prop1, prop2));
		assertEquals(0, comp.compare(idx, prop2, prop1));
	}


	@Test
	public void columnSubject() {
		CSRColumnComparator comp = new CSRColumnComparator();

		final int idx = PendingCertificatesPane.COLUMN_SUBJECT;

		CertificateRequestProperties propNull = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propNull.getProperty(Key.subject)).thenReturn(null);

		CertificateRequestProperties propEmpty = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propEmpty.getProperty(Key.subject)).thenReturn("");
		
		CertificateRequestProperties prop1 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop1.getProperty(Key.subject)).thenReturn("CN=abc");

		CertificateRequestProperties prop2 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop2.getProperty(Key.subject)).thenReturn("CN=bdc");

		CertificateRequestProperties prop3 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop3.getProperty(Key.subject)).thenReturn("CN=def");

		CertificateRequestProperties prop4 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop4.getProperty(Key.subject)).thenReturn("CN=zzz");

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));
		
		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, propNull));
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
		CSRColumnComparator comp = new CSRColumnComparator();

		final int idx = -1;

		CertificateRequestProperties propNull = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propNull.getProperty(Key.subject)).thenReturn(null);

		CertificateRequestProperties propEmpty = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propEmpty.getProperty(Key.subject)).thenReturn("");
		
		CertificateRequestProperties prop1 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop1.getProperty(Key.subject)).thenReturn("CN=abc");

		CertificateRequestProperties prop2 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop2.getProperty(Key.subject)).thenReturn("CN=bdc");

		CertificateRequestProperties prop3 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop3.getProperty(Key.subject)).thenReturn("CN=def");

		CertificateRequestProperties prop4 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop4.getProperty(Key.subject)).thenReturn("CN=zzz");

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));
		
		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, propNull));
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
		CSRColumnComparator comp = new CSRColumnComparator();

		final int idx = PendingCertificatesPane.COLUMN_KEY_TYPE;

		CertificateRequestProperties propNull = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propNull.getProperty(Key.keyType)).thenReturn(null);
		
		CertificateRequestProperties propEmpty = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propEmpty.getProperty(Key.keyType)).thenReturn("");

		CertificateRequestProperties prop1 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop1.getProperty(Key.keyType)).thenReturn(KeyType.DSA_512.name());

		CertificateRequestProperties prop2 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop2.getProperty(Key.keyType)).thenReturn(KeyType.DSA_2048.name());

		CertificateRequestProperties prop3 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop3.getProperty(Key.keyType)).thenReturn(KeyType.EC_c2pnb176w1.name());

		CertificateRequestProperties prop4 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop4.getProperty(Key.keyType)).thenReturn(KeyType.qTESLA_P_I.name());
		
		CertificateRequestProperties prop5 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop5.getProperty(Key.keyType)).thenReturn(KeyType.XMSSMT_SHA2_20_2_256.name());
		
		CertificateRequestProperties prop6 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop6.getProperty(Key.keyType)).thenReturn(KeyType.XMSSMT_SHA2_40_4_512.name());

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propNull));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, propEmpty, propEmpty));
		assertEquals(0, comp.compare(idx, prop2, prop2));
		
		assertEquals(-1, comp.compare(idx, prop1, propEmpty));
		assertEquals(-1, comp.compare(idx, prop1, propNull));
		assertEquals(1, comp.compare(idx, propEmpty, prop1));
		assertEquals(1, comp.compare(idx, propNull, prop1));

		assertEquals(-1, comp.compare(idx, prop1, propNull));
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
	}

	@Test
	public void columnImprtDate() {
		CSRColumnComparator comp = new CSRColumnComparator();

		final int idx = PendingCertificatesPane.COLUMN_IMPORT_DATE;

		ZonedDateTime now = ZonedDateTime.now();

		CertificateRequestProperties propNull = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propNull.getProperty(Key.importDate)).thenReturn(null);
		
		CertificateRequestProperties propEmpty = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propEmpty.getProperty(Key.importDate)).thenReturn("");
		
		CertificateRequestProperties prop1 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop1.getProperty(Key.importDate)).thenReturn(DateTimeUtil.toString(now));
		
		CertificateRequestProperties prop2 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop2.getProperty(Key.importDate)).thenReturn(DateTimeUtil.toString(now.plusHours(1)));	
		
		CertificateRequestProperties prop3 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop3.getProperty(Key.importDate)).thenReturn(DateTimeUtil.toString(now.plusDays(1)));
		
		CertificateRequestProperties prop4 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop4.getProperty(Key.importDate)).thenReturn(DateTimeUtil.toString(now.plusDays(2)));
		
		CertificateRequestProperties prop5 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop5.getProperty(Key.importDate)).thenReturn("123445677888");
		
		CertificateRequestProperties prop6 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop6.getProperty(Key.importDate)).thenReturn("abcde");
		
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
	public void columnCRLComment() {
		CSRColumnComparator comp = new CSRColumnComparator();

		final int idx = PendingCertificatesPane.COLUMN_COMMENTS;
		
		CertificateRequestProperties propNull = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propNull.getProperty(Key.comments)).thenReturn(null);
		
		CertificateRequestProperties propEmpty = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(propEmpty.getProperty(Key.comments)).thenReturn("");
		
		CertificateRequestProperties prop1 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop1.getProperty(Key.comments)).thenReturn("Abc");
		
		CertificateRequestProperties prop2 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop2.getProperty(Key.comments)).thenReturn("abcd");	
		
		CertificateRequestProperties prop3 = Mockito.mock(CertificateRequestProperties.class);
		Mockito.when(prop3.getProperty(Key.comments)).thenReturn("bg");
		
		CertificateRequestProperties prop4 = Mockito.mock(CertificateRequestProperties.class);
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
