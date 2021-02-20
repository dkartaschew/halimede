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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;

import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.CRLPane;
import net.sourceforge.dkartaschew.halimede.util.Strings;

/**
 * Basic tests for label provider.
 * <p>
 * Tests images, column text and column tooltip.
 */
public class TestCRLColumnLabelProvider {

	@Test
	public void testImages() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		for (int i = 0; i <= CRLPane.COLUMN_COMMENTS; i++) {
			assertNull(provider.getColumnImage(prop, i));
		}
	}

	@Test
	public void testOutOfIndex() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		assertNull(provider.getColumnText(prop, -1));
		assertNull(provider.getColumnText(prop, CRLPane.COLUMN_COMMENTS + 1));
		assertNull(provider.getColumnTooltipText(prop, -1));
		assertNull(provider.getColumnTooltipText(prop, CRLPane.COLUMN_COMMENTS + 1));
	}
	
	@Test
	public void testColum0() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = "1";
		Mockito.when(prop.getProperty(CRLProperties.Key.crlSerialNumber)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 0));
		assertEquals(desc, provider.getColumnTooltipText(prop, 0));
	}

	@Test
	public void testColum0_Null() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CRLProperties.Key.crlSerialNumber)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 0));
		assertEquals(null, provider.getColumnTooltipText(prop, 0));
	}
	
	@Test
	public void testColum1() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = "CN=Test";
		Mockito.when(prop.getProperty(CRLProperties.Key.issuer)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 1));
		assertEquals(desc, provider.getColumnTooltipText(prop, 1));
	}

	@Test
	public void testColum1_Null() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CRLProperties.Key.issuer)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 1));
		assertEquals(null, provider.getColumnTooltipText(prop, 1));
	}
	
	@Test
	public void testColum2() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = ZonedDateTime.now().toString();
		Mockito.when(prop.getProperty(CRLProperties.Key.issueDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 2));
		assertEquals(desc, provider.getColumnTooltipText(prop, 2));
	}

	@Test
	public void testColum2_Null() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CRLProperties.Key.issueDate)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 2));
		assertEquals(null, provider.getColumnTooltipText(prop, 2));
	}
	
	@Test
	public void testColum3() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = ZonedDateTime.now().toString();
		Mockito.when(prop.getProperty(CRLProperties.Key.nextExpectedDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 3));
		assertEquals(desc, provider.getColumnTooltipText(prop, 3));
	}

	@Test
	public void testColum3_Null() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CRLProperties.Key.nextExpectedDate)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 3));
		assertEquals(null, provider.getColumnTooltipText(prop, 3));
	}
	
	@Test
	public void testColum4() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = "A Really Long Description 01234567890 ABCDEFGHIJKLMNOP";
		Mockito.when(prop.getProperty(CRLProperties.Key.comments)).thenReturn(desc);
		assertEquals(desc.substring(0, Strings.WRAP) + "...", provider.getColumnText(prop, 4));
		assertEquals(desc, provider.getColumnTooltipText(prop, 4));
	}

	@Test
	public void testColum4_Null() {
		CRLColumnLabelProvider provider = new CRLColumnLabelProvider();
		CRLProperties prop = Mockito.mock(CRLProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CRLProperties.Key.comments)).thenReturn(desc);
		assertEquals("", provider.getColumnText(prop, 4));
		assertEquals(null, provider.getColumnTooltipText(prop, 4));
	}
}
