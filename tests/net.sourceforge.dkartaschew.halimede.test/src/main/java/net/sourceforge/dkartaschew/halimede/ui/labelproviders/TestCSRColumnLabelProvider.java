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

import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.CRLPane;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.PendingCertificatesPane;
import net.sourceforge.dkartaschew.halimede.util.Strings;

/**
 * Basic tests for label provider.
 * <p>
 * Tests images, column text and column tooltip.
 */
public class TestCSRColumnLabelProvider {

	@Test
	public void testImages() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		for (int i = 0; i <= PendingCertificatesPane.COLUMN_COMMENTS; i++) {
			assertNull(provider.getColumnImage(prop, i));
		}
	}

	@Test
	public void testOutOfIndex() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		assertNull(provider.getColumnText(prop, -1));
		assertNull(provider.getColumnText(prop, CRLPane.COLUMN_COMMENTS + 1));
		assertNull(provider.getColumnTooltipText(prop, -1));
		assertNull(provider.getColumnTooltipText(prop, CRLPane.COLUMN_COMMENTS + 1));
	}
	
	@Test
	public void testColum0() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = "CN=Test";
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.subject)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 0));
		assertEquals(desc, provider.getColumnTooltipText(prop, 0));
	}

	@Test
	public void testColum0_Null() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.subject)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 0));
		assertEquals(null, provider.getColumnTooltipText(prop, 0));
	}
	
	@Test
	public void testColum1() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = "RSA_512";
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.keyType)).thenReturn(desc);
		assertEquals("RSA 512", provider.getColumnText(prop, 1));
		assertEquals("RSA 512", provider.getColumnTooltipText(prop, 1));
	}

	@Test
	public void testColum1_BadValue() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = "RSA2_512";
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.keyType)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 1));
		assertEquals(null, provider.getColumnTooltipText(prop, 1));
	}

	@Test
	public void testColum1_Null() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.keyType)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 1));
		assertEquals(null, provider.getColumnTooltipText(prop, 1));
	}
	
	@Test
	public void testColum2() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = ZonedDateTime.now().toString();
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.importDate)).thenReturn(desc);
		assertEquals(desc, provider.getColumnText(prop, 2));
		assertEquals(desc, provider.getColumnTooltipText(prop, 2));
	}

	@Test
	public void testColum2_Null() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.importDate)).thenReturn(desc);
		assertEquals(null, provider.getColumnText(prop, 2));
		assertEquals(null, provider.getColumnTooltipText(prop, 2));
	}
	
	@Test
	public void testColum3() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = "A Really Long Description 01234567890 ABCDEFGHIJKLMNOP";
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.comments)).thenReturn(desc);
		assertEquals(desc.substring(0, Strings.WRAP) + "...", provider.getColumnText(prop, 3));
		assertEquals(desc, provider.getColumnTooltipText(prop, 3));
	}

	@Test
	public void testColum3_Null() {
		CSRColumnLabelProvider provider = new CSRColumnLabelProvider();
		CertificateRequestProperties prop = Mockito.mock(CertificateRequestProperties.class);
		String desc = null;
		Mockito.when(prop.getProperty(CertificateRequestProperties.Key.comments)).thenReturn(desc);
		assertEquals("", provider.getColumnText(prop, 3));
		assertEquals(null, provider.getColumnTooltipText(prop, 3));
	}
	
}
