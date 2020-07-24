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

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties.Key;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.CRLPane;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

public class TestCRLColumnComparator {

	@Test
	public void columnInvalid() {
		CRLColumnComparator comp = new CRLColumnComparator();
		
		final int idx = 100;
		
		CRLProperties prop1 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop1.getProperty(Key.crlSerialNumber)).thenReturn("1");
		
		CRLProperties prop2 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop2.getProperty(Key.crlSerialNumber)).thenReturn("2");	
		
		assertEquals(0, comp.compare(idx, prop1, prop2));
		assertEquals(0, comp.compare(idx, prop2, prop1));
	}
	
	@Test
	public void columnCRLNumber() {
		CRLColumnComparator comp = new CRLColumnComparator();
		
		final int idx = CRLPane.COLUMN_CRL_NUMBER;
		
		CRLProperties propNull = Mockito.mock(CRLProperties.class);
		Mockito.when(propNull.getProperty(Key.crlSerialNumber)).thenReturn(null);
		
		CRLProperties propEmpty = Mockito.mock(CRLProperties.class);
		Mockito.when(propEmpty.getProperty(Key.crlSerialNumber)).thenReturn("");
		
		CRLProperties prop1 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop1.getProperty(Key.crlSerialNumber)).thenReturn("1");
		
		CRLProperties prop2 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop2.getProperty(Key.crlSerialNumber)).thenReturn("2");	
		
		CRLProperties prop3 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop3.getProperty(Key.crlSerialNumber)).thenReturn("3");
		
		CRLProperties prop4 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop4.getProperty(Key.crlSerialNumber)).thenReturn("20");
		
		CRLProperties prop5 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop5.getProperty(Key.crlSerialNumber)).thenReturn("abcd");

		CRLProperties prop6 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop6.getProperty(Key.crlSerialNumber)).thenReturn("abcde");
		
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
	public void columnCRLNumberDefault() {
		CRLColumnComparator comp = new CRLColumnComparator();
		
		final int idx = -1;
		
		CRLProperties propNull = Mockito.mock(CRLProperties.class);
		Mockito.when(propNull.getProperty(Key.crlSerialNumber)).thenReturn(null);
		
		CRLProperties propEmpty = Mockito.mock(CRLProperties.class);
		Mockito.when(propEmpty.getProperty(Key.crlSerialNumber)).thenReturn("");
		
		CRLProperties prop1 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop1.getProperty(Key.crlSerialNumber)).thenReturn("1");
		
		CRLProperties prop2 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop2.getProperty(Key.crlSerialNumber)).thenReturn("2");	
		
		CRLProperties prop3 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop3.getProperty(Key.crlSerialNumber)).thenReturn("3");
		
		CRLProperties prop4 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop4.getProperty(Key.crlSerialNumber)).thenReturn("20");
		
		CRLProperties prop5 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop5.getProperty(Key.crlSerialNumber)).thenReturn("abcd");
		
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
	}
	
	@Test
	public void columnCRLSubject() {
		CRLColumnComparator comp = new CRLColumnComparator();
		
		final int idx = CRLPane.COLUMN_SUBJECT;
		
		CRLProperties propNull = Mockito.mock(CRLProperties.class);
		Mockito.when(propNull.getProperty(Key.issuer)).thenReturn(null);
		
		CRLProperties propEmpty = Mockito.mock(CRLProperties.class);
		Mockito.when(propEmpty.getProperty(Key.issuer)).thenReturn("");
		
		CRLProperties prop1 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop1.getProperty(Key.issuer)).thenReturn("CN=abc");
		
		CRLProperties prop2 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop2.getProperty(Key.issuer)).thenReturn("CN=abcd");	
		
		CRLProperties prop3 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop3.getProperty(Key.issuer)).thenReturn("CN=bg");
		
		CRLProperties prop4 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop4.getProperty(Key.issuer)).thenReturn("CN=zzz");
		
		CRLProperties prop5 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop5.getProperty(Key.issuer)).thenReturn("abcd");
		
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
	public void columnCRLStartDate() {
		CRLColumnComparator comp = new CRLColumnComparator();
		
		final int idx = CRLPane.COLUMN_START_DATE;
		ZonedDateTime now = ZonedDateTime.now();
		
		CRLProperties propNull = Mockito.mock(CRLProperties.class);
		Mockito.when(propNull.getProperty(Key.issueDate)).thenReturn(null);
		
		CRLProperties propEmpty = Mockito.mock(CRLProperties.class);
		Mockito.when(propEmpty.getProperty(Key.issueDate)).thenReturn("");
		
		CRLProperties prop1 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop1.getProperty(Key.issueDate)).thenReturn(DateTimeUtil.toString(now));
		
		CRLProperties prop2 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop2.getProperty(Key.issueDate)).thenReturn(DateTimeUtil.toString(now.plusHours(1)));	
		
		CRLProperties prop3 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop3.getProperty(Key.issueDate)).thenReturn(DateTimeUtil.toString(now.plusDays(1)));
		
		CRLProperties prop4 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop4.getProperty(Key.issueDate)).thenReturn(DateTimeUtil.toString(now.plusDays(2)));
		
		CRLProperties prop5 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop5.getProperty(Key.issueDate)).thenReturn("123445677888");
		
		CRLProperties prop6 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop6.getProperty(Key.issueDate)).thenReturn("abcde");
		
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
	public void columnCRLExpiryDate() {
		CRLColumnComparator comp = new CRLColumnComparator();
		
		final int idx = CRLPane.COLUMN_EXPIRY_DATE;
		ZonedDateTime now = ZonedDateTime.now();
		
		CRLProperties propNull = Mockito.mock(CRLProperties.class);
		Mockito.when(propNull.getProperty(Key.nextExpectedDate)).thenReturn(null);
		
		CRLProperties propEmpty = Mockito.mock(CRLProperties.class);
		Mockito.when(propEmpty.getProperty(Key.nextExpectedDate)).thenReturn("");
		
		CRLProperties prop1 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop1.getProperty(Key.nextExpectedDate)).thenReturn(DateTimeUtil.toString(now));
		
		CRLProperties prop2 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop2.getProperty(Key.nextExpectedDate)).thenReturn(DateTimeUtil.toString(now.plusHours(1)));	
		
		CRLProperties prop3 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop3.getProperty(Key.nextExpectedDate)).thenReturn(DateTimeUtil.toString(now.plusDays(1)));
		
		CRLProperties prop4 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop4.getProperty(Key.nextExpectedDate)).thenReturn(DateTimeUtil.toString(now.plusDays(2)));
		
		CRLProperties prop5 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop5.getProperty(Key.nextExpectedDate)).thenReturn("123445677888");
		
		CRLProperties prop6 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop6.getProperty(Key.nextExpectedDate)).thenReturn("abcde");
		
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
		CRLColumnComparator comp = new CRLColumnComparator();
		
		final int idx = CRLPane.COLUMN_COMMENTS;
		
		CRLProperties propNull = Mockito.mock(CRLProperties.class);
		Mockito.when(propNull.getProperty(Key.comments)).thenReturn(null);
		
		CRLProperties propEmpty = Mockito.mock(CRLProperties.class);
		Mockito.when(propEmpty.getProperty(Key.comments)).thenReturn("");
		
		CRLProperties prop1 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop1.getProperty(Key.comments)).thenReturn("Abc");
		
		CRLProperties prop2 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop2.getProperty(Key.comments)).thenReturn("abcd");	
		
		CRLProperties prop3 = Mockito.mock(CRLProperties.class);
		Mockito.when(prop3.getProperty(Key.comments)).thenReturn("bg");
		
		CRLProperties prop4 = Mockito.mock(CRLProperties.class);
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
