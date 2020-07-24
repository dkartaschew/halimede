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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;

import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.IssuedCertificatesPane;
import net.sourceforge.dkartaschew.halimede.util.Strings;

/**
 * Basic tests for label provider.
 * <p>
 * Tests images, column text and column tooltip.
 */
public class TestIssuedCertificateColumnLabelProvider {

	@Test
	public void testImages() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		for (int i = 0; i <= IssuedCertificatesPane.COLUMN_COMMENTS; i++) {
			assertNull(provider.getColumnImage(prop, i));
		}
	}

	@Test
	public void testOutOfIndex() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		assertNull(provider.getColumnText(prop, -1));
		assertNull(provider.getColumnText(prop, IssuedCertificatesPane.COLUMN_COMMENTS + 1));
		assertNull(provider.getColumnTooltipText(prop, -1));
		assertNull(provider.getColumnTooltipText(prop, IssuedCertificatesPane.COLUMN_COMMENTS + 1));
	}

	@Test
	public void testColum0() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = "A Really Long Description 01234567890 ABCDEFGHIJKLMNOP";
		Mockito.when(prop.getProperty(Key.description)).thenReturn(desc);
		assertEquals(desc.substring(0, Strings.WRAP) + "...", provider.getColumnText(prop, 0));
		assertEquals(desc, provider.getColumnTooltipText(prop, 0));
	}

	@Test
	public void testColum0_Null() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(Key.description)).thenReturn(desc);
		assertEquals("", provider.getColumnText(prop, 0));
		assertEquals(null, provider.getColumnTooltipText(prop, 0));
	}

	@Test
	public void testColum1() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = "A Really Long Description 01234567890 ABCDEFGHIJKLMNOP";
		Mockito.when(prop.getProperty(Key.subject)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 1));
		assertEquals(desc, provider.getColumnTooltipText(prop, 1));
	}

	@Test
	public void testColum2() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = "RSA_512";
		Mockito.when(prop.getProperty(Key.keyType)).thenReturn(desc);
		assertEquals("RSA 512", provider.getColumnText(prop, 2));
		assertEquals("RSA 512", provider.getColumnTooltipText(prop, 2));
	}

	@Test
	public void testColum2_BadValue() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = "RSA2_512";
		Mockito.when(prop.getProperty(Key.keyType)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 2));
		assertEquals(null, provider.getColumnTooltipText(prop, 2));
	}

	@Test
	public void testColum2_Null() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(Key.keyType)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 2));
		assertEquals(null, provider.getColumnTooltipText(prop, 2));
	}

	@Test
	public void testColum3() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = ZonedDateTime.now().toString();
		Mockito.when(prop.getProperty(Key.creationDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 3));
		assertEquals(desc, provider.getColumnTooltipText(prop, 3));
	}

	@Test
	public void testColum3_Null() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(Key.creationDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 3));
		assertEquals(desc, provider.getColumnTooltipText(prop, 3));
	}

	@Test
	public void testColum4() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = ZonedDateTime.now().toString();
		Mockito.when(prop.getProperty(Key.startDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 4));
		assertEquals(desc, provider.getColumnTooltipText(prop, 4));
	}

	@Test
	public void testColum4_Null() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(Key.startDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 4));
		assertEquals(desc, provider.getColumnTooltipText(prop, 4));
	}

	@Test
	public void testColum5() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = ZonedDateTime.now().toString();
		Mockito.when(prop.getProperty(Key.endDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 5));
		assertEquals(desc, provider.getColumnTooltipText(prop, 5));
	}

	@Test
	public void testColum5_Null() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(Key.endDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 5));
		assertEquals(desc, provider.getColumnTooltipText(prop, 5));
	}
	
	@Test
	public void testColum6() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = "A Really Long Description 01234567890 ABCDEFGHIJKLMNOP";
		Mockito.when(prop.getProperty(Key.comments)).thenReturn(desc);
		assertEquals(desc.substring(0, Strings.WRAP) + "...", provider.getColumnText(prop, 6));
		assertEquals(desc, provider.getColumnTooltipText(prop, 6));
	}

	@Test
	public void testColum6_Null() {
		IssuedCertificateColumnLabelProvider provider = new IssuedCertificateColumnLabelProvider();
		IssuedCertificateProperties prop = Mockito.mock(IssuedCertificateProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(Key.comments)).thenReturn(desc);
		assertEquals("", provider.getColumnText(prop, 6));
		assertEquals(null, provider.getColumnTooltipText(prop, 6));
	}
}
