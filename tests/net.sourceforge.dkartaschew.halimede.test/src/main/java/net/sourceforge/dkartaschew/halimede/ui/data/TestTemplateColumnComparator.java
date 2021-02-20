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

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.TemplatesPane;

public class TestTemplateColumnComparator {

	@Test
	public void columnInvalid() {
		TemplateColumnComparator comp = new TemplateColumnComparator();

		final int idx = 100;

		CertificateKeyPairTemplate prop1 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop1.getDescription()).thenReturn("1");

		CertificateKeyPairTemplate prop2 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop2.getDescription()).thenReturn("2");

		assertEquals(0, comp.compare(idx, prop1, prop2));
		assertEquals(0, comp.compare(idx, prop2, prop1));
	}

	@Test
	public void columnDescription() {
		TemplateColumnComparator comp = new TemplateColumnComparator();

		final int idx = TemplatesPane.COLUMN_DESCRIPTION;

		CertificateKeyPairTemplate propNull = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(propNull.getDescription()).thenReturn(null);

		CertificateKeyPairTemplate propEmpty = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(propEmpty.getDescription()).thenReturn("");

		CertificateKeyPairTemplate prop1 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop1.getDescription()).thenReturn("abc");

		CertificateKeyPairTemplate prop2 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop2.getDescription()).thenReturn("bdc");

		CertificateKeyPairTemplate prop3 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop3.getDescription()).thenReturn("def");

		CertificateKeyPairTemplate prop4 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop4.getDescription()).thenReturn("zzz");

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
		TemplateColumnComparator comp = new TemplateColumnComparator();

		final int idx = -1;

		CertificateKeyPairTemplate propNull = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(propNull.getDescription()).thenReturn(null);

		CertificateKeyPairTemplate propEmpty = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(propEmpty.getDescription()).thenReturn("");

		CertificateKeyPairTemplate prop1 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop1.getDescription()).thenReturn("abc");

		CertificateKeyPairTemplate prop2 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop2.getDescription()).thenReturn("bdc");

		CertificateKeyPairTemplate prop3 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop3.getDescription()).thenReturn("def");

		CertificateKeyPairTemplate prop4 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop4.getDescription()).thenReturn("zzz");

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
		TemplateColumnComparator comp = new TemplateColumnComparator();

		final int idx = TemplatesPane.COLUMN_SUBJECT;

		CertificateKeyPairTemplate propNull = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(propNull.getSubject()).thenReturn(null);

		CertificateKeyPairTemplate prop1 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop1.getSubject()).thenReturn(new X500Name("CN=abc"));

		CertificateKeyPairTemplate prop2 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop2.getSubject()).thenReturn(new X500Name("CN=bdc"));

		CertificateKeyPairTemplate prop3 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop3.getSubject()).thenReturn(new X500Name("CN=def"));

		CertificateKeyPairTemplate prop4 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop4.getSubject()).thenReturn(new X500Name("CN=zzz"));

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, prop2, prop2));

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
		TemplateColumnComparator comp = new TemplateColumnComparator();

		final int idx = TemplatesPane.COLUMN_KEY_TYPE;

		CertificateKeyPairTemplate propNull = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(propNull.getKeyType()).thenReturn(null);

		CertificateKeyPairTemplate prop1 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop1.getKeyType()).thenReturn(KeyType.DSA_512);

		CertificateKeyPairTemplate prop2 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop2.getKeyType()).thenReturn(KeyType.DSA_2048);

		CertificateKeyPairTemplate prop3 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop3.getKeyType()).thenReturn(KeyType.EC_c2pnb176w1);

		CertificateKeyPairTemplate prop4 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop4.getKeyType()).thenReturn(KeyType.qTESLA_P_I);
		
		CertificateKeyPairTemplate prop5 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop5.getKeyType()).thenReturn(KeyType.XMSSMT_SHA2_20_2_256);
		
		CertificateKeyPairTemplate prop6 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop6.getKeyType()).thenReturn(KeyType.XMSSMT_SHA2_40_4_512);

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, prop2, prop2));

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
	public void columnCreateDate() {
		TemplateColumnComparator comp = new TemplateColumnComparator();

		final int idx = TemplatesPane.COLUMN_CREATE_DATE;

		ZonedDateTime dt = ZonedDateTime.now();

		CertificateKeyPairTemplate propNull = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(propNull.getCreationDate()).thenReturn(null);

		CertificateKeyPairTemplate prop1 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop1.getCreationDate()).thenReturn(dt);

		CertificateKeyPairTemplate prop2 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop2.getCreationDate()).thenReturn(dt.plusMinutes(1));

		CertificateKeyPairTemplate prop3 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop3.getCreationDate()).thenReturn(dt.plusDays(1));

		CertificateKeyPairTemplate prop4 = Mockito.mock(CertificateKeyPairTemplate.class);
		Mockito.when(prop4.getCreationDate()).thenReturn(dt.plusMonths(1));

		assertEquals(0, comp.compare(idx, null, null));
		assertEquals(-1, comp.compare(idx, propNull, null));
		assertEquals(-1, comp.compare(idx, prop1, null));
		assertEquals(1, comp.compare(idx, null, prop1));

		assertEquals(0, comp.compare(idx, propNull, propNull));
		assertEquals(0, comp.compare(idx, prop2, prop2));

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
}
