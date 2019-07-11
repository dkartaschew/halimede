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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.ZonedDateTime;

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.TemplatesPane;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

/**
 * Basic tests for label provider.
 * <p>
 * Tests images, column text and column tooltip.
 */
public class TestTemplateColumnLabelProvider {

	@Test
	public void testImages() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		for (int i = 0; i <= TemplatesPane.COLUMN_CREATE_DATE; i++) {
			assertNull(provider.getColumnImage(prop, i));
		}
	}

	@Test
	public void testOutOfIndex() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		assertNull(provider.getColumnText(prop, -1));
		assertNull(provider.getColumnText(prop, TemplatesPane.COLUMN_CREATE_DATE + 1));
		assertNull(provider.getColumnTooltipText(prop, -1));
		assertNull(provider.getColumnTooltipText(prop, TemplatesPane.COLUMN_CREATE_DATE + 1));
	}
	
	@Test
	public void testColum0() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		String desc = "A Really Long Description 01234567890 ABCDEFGHIJKLMNOP";
		Mockito.when(prop.getDescription()).thenReturn(desc);
		assertEquals(desc.substring(0, Strings.WRAP) + "...", provider.getColumnText(prop, 0));
		assertEquals(desc, provider.getColumnTooltipText(prop, 0));
	}

	@Test
	public void testColum0_Null() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		String desc = null;
		Mockito.when(prop.getDescription()).thenReturn(desc);
		assertEquals("", provider.getColumnText(prop, 0));
		assertEquals(null, provider.getColumnTooltipText(prop, 0));
	}
	
	@Test
	public void testColum1() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		String desc = "CN=Test";
		Mockito.when(prop.getSubject()).thenReturn(new X500Name(desc));
		assertEquals(desc, provider.getColumnText(prop, 1));
		assertEquals(desc, provider.getColumnTooltipText(prop, 1));
	}

	@Test
	public void testColum1_Null() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop.getSubject()).thenReturn(null);
		assertEquals("", provider.getColumnText(prop, 1));
		assertEquals("", provider.getColumnTooltipText(prop, 1));
	}
	
	@Test
	public void testColum2() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop.getKeyType()).thenReturn(KeyType.DSA_1024);
		assertEquals(KeyType.DSA_1024.getDescription(), provider.getColumnText(prop, 2));
		assertEquals(KeyType.DSA_1024.getDescription(), provider.getColumnTooltipText(prop, 2));
	}

	@Test
	public void testColum2_Null() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop.getKeyType()).thenReturn(null);
		assertEquals("", provider.getColumnText(prop, 2));
		assertEquals("", provider.getColumnTooltipText(prop, 2));
	}
	
	@Test
	public void testColum3() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		ZonedDateTime dt = ZonedDateTime.now();
		Mockito.when(prop.getCreationDate()).thenReturn(dt);
		assertEquals(DateTimeUtil.toString(dt), provider.getColumnText(prop, 3));
		assertEquals(DateTimeUtil.toString(dt), provider.getColumnTooltipText(prop, 3));
	}

	@Test
	public void testColum3_Null() {
		TemplateColumnLabelProvider provider = new TemplateColumnLabelProvider();
		CertificateKeyPairTemplate prop = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop.getKeyType()).thenReturn(null);
		assertEquals(null, provider.getColumnText(prop, 3));
		assertEquals(null, provider.getColumnTooltipText(prop, 3));
	}
	
}
