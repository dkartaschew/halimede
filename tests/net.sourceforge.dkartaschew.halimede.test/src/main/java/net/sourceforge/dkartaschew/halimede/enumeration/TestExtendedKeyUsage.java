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
package net.sourceforge.dkartaschew.halimede.enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.junit.Test;

public class TestExtendedKeyUsage {

	@Test
	public void testEnum() {
		for (ExtendedKeyUsageEnum t : ExtendedKeyUsageEnum.values()) {
			assertEquals(t, ExtendedKeyUsageEnum.valueOf(t.name()));
			assertEquals(0, t.compareTo(t));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
			System.out.println(t.getKeyUsage());
		}
		ExtendedKeyUsageID i = new ExtendedKeyUsageID();
		i.toString();
		i.equals(i);
		i.hashCode();
	}

	@Test
	public void testNULLCollectionASN1Vector() {
		assertEquals(null, ExtendedKeyUsageEnum.asExtKeyUsage((Collection<ExtendedKeyUsageEnum>) null));
	}

	@Test
	public void testEmptyCollectionASN1Vector() {
		assertEquals(null, ExtendedKeyUsageEnum.asExtKeyUsage(new ArrayList<>()));
	}

	@Test
	public void testNullinCollectionASN1Vector() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(null);
		assertEquals(null, ExtendedKeyUsageEnum.asExtKeyUsage(c));
	}

	@Test
	public void testNullinCollectionASN1Vector2() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(null);
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		
		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));

	}

	@Test
	public void testNullinCollectionASN1Vector3() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);
		c.add(null);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));

	}

	@Test
	public void testCollectionASN1Vector() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());

		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));
	}

	@Test
	public void testAsASN1Vector() {

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		assertTrue(equals(v, ExtendedKeyUsageEnum.id_kp_capwapAC.asKeyUsage()));
	}

	@Test
	public void testCollectionASN1Vector2() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);
		c.add(ExtendedKeyUsageEnum.anyExtendedKeyUsage);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		v.add(ExtendedKeyUsageEnum.anyExtendedKeyUsage.getKeyUsage());
		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));

	}

	@Test
	public void testNULLArrayASN1Vector() {
		assertEquals(null, ExtendedKeyUsageEnum.asExtKeyUsage((ExtendedKeyUsageEnum[]) null));
	}

	@Test
	public void testEmptyArrayASN1Vector() {
		assertEquals(null, ExtendedKeyUsageEnum.asExtKeyUsage(new ExtendedKeyUsageEnum[0]));
	}

	@Test
	public void testNullinArrayASN1Vector() {
		ExtendedKeyUsageEnum[] c = new ExtendedKeyUsageEnum[1];
		assertEquals(null, ExtendedKeyUsageEnum.asExtKeyUsage(c));
	}

	@Test
	public void testNullinArrayASN1Vector2() {
		ExtendedKeyUsageEnum[] c = new ExtendedKeyUsageEnum[2];
		c[0] = (null);
		c[1] = (ExtendedKeyUsageEnum.id_kp_capwapAC);
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));
	}

	@Test
	public void testNullinArrayASN1Vector3() {
		ExtendedKeyUsageEnum[] c = new ExtendedKeyUsageEnum[2];
		c[0] = (ExtendedKeyUsageEnum.id_kp_capwapAC);
		c[1] = (null);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));
	}

	@Test
	public void testArrayASN1Vector() {
		ExtendedKeyUsageEnum[] c = new ExtendedKeyUsageEnum[1];
		c[0] = (ExtendedKeyUsageEnum.id_kp_capwapAC);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());

		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));
	}

	@Test
	public void testArrayASN1Vector2() {
		ExtendedKeyUsageEnum[] c = new ExtendedKeyUsageEnum[2];
		c[0] = (ExtendedKeyUsageEnum.id_kp_capwapAC);
		c[1] = (ExtendedKeyUsageEnum.anyExtendedKeyUsage);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		v.add(ExtendedKeyUsageEnum.anyExtendedKeyUsage.getKeyUsage());

		assertTrue(equals(v, ExtendedKeyUsageEnum.asExtKeyUsage(c)));
	}

	@Test
	public void testKEUToCollection() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);
		c.add(ExtendedKeyUsageEnum.anyExtendedKeyUsage);

		KeyPurposeId[] ids = new KeyPurposeId[2];
		ids[0] = ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage();
		ids[1] = ExtendedKeyUsageEnum.anyExtendedKeyUsage.getKeyUsage();
		ExtendedKeyUsage u = new ExtendedKeyUsage(ids);

		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(u));
	}

	@Test
	public void testKEUToCollection2() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);

		KeyPurposeId[] ids = new KeyPurposeId[1];
		ids[0] = ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage();
		ExtendedKeyUsage u = new ExtendedKeyUsage(ids);

		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(u));
	}

	@Test
	public void testKEUToCollectionEmpty() {
		Collection<ExtendedKeyUsageEnum> c = Collections.emptyList();

		KeyPurposeId[] ids = new KeyPurposeId[0];
		ExtendedKeyUsage u = new ExtendedKeyUsage(ids);

		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(u));
	}

	@Test
	public void testASN1VectorToCollectionNULL() {
		Collection<ExtendedKeyUsageEnum> c = Collections.emptyList();
		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum((ASN1EncodableVector) null));
	}

	@Test
	public void testASN1VectorToCollection() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);
		c.add(ExtendedKeyUsageEnum.anyExtendedKeyUsage);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());
		v.add(ExtendedKeyUsageEnum.anyExtendedKeyUsage.getKeyUsage());

		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(v));
	}

	@Test
	public void testASN1VectorToCollection2() {
		List<ExtendedKeyUsageEnum> c = new ArrayList<>();
		c.add(ExtendedKeyUsageEnum.id_kp_capwapAC);

		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(ExtendedKeyUsageEnum.id_kp_capwapAC.getKeyUsage());

		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(v));
	}

	@Test
	public void testASN1VectorToCollectionEmpty() {
		Collection<ExtendedKeyUsageEnum> c = Collections.emptyList();
		ASN1EncodableVector v = new ASN1EncodableVector();
		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(v));
	}

	@Test
	public void testKEUToCollectionNULL() {
		Collection<ExtendedKeyUsageEnum> c = Collections.emptyList();
		assertEquals(c, ExtendedKeyUsageEnum.asExtendedKeyUsageEnum((ExtendedKeyUsage) null));
	}

	private boolean equals(ASN1EncodableVector v1, ASN1EncodableVector v2) {
		if (v1 == null && v2 == null)
			return true;
		if (v1 == null || v2 == null)
			return false;
		if (v1.size() != v2.size())
			return false;
		
		List<Object> o1 = new ArrayList<>();
		List<Object> o2 = new ArrayList<>();
		for(int i = 0; i < v1.size(); i++) {
			o1.add(v1.get(i));
			o2.add(v2.get(i));
		}
		return o1.equals(o2);
	}
}
