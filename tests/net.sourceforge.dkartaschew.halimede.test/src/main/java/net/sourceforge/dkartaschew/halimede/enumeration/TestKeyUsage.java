/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2018 Darran Kartaschew 
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bouncycastle.asn1.x509.KeyUsage;
import org.junit.Test;

public class TestKeyUsage {

	@Test
	public void testKeyUsage() {
		for (KeyUsageEnum k : KeyUsageEnum.values()) {
			KeyUsage u = k.asKeyUsage();
			assertTrue(u.hasUsages(k.getKeyUsage()));
		}
	}

	@Test
	public void testKeyUsageDetail() {
		assertTrue(KeyUsageEnum.cRLSign.asKeyUsage().hasUsages(KeyUsage.cRLSign));
		assertTrue(KeyUsageEnum.digitalSignature.asKeyUsage().hasUsages(KeyUsage.digitalSignature));
		assertTrue(KeyUsageEnum.nonRepudiation.asKeyUsage().hasUsages(KeyUsage.nonRepudiation));
		assertTrue(KeyUsageEnum.keyEncipherment.asKeyUsage().hasUsages(KeyUsage.keyEncipherment));
		assertTrue(KeyUsageEnum.dataEncipherment.asKeyUsage().hasUsages(KeyUsage.dataEncipherment));
		assertTrue(KeyUsageEnum.keyAgreement.asKeyUsage().hasUsages(KeyUsage.keyAgreement));
		assertTrue(KeyUsageEnum.keyCertSign.asKeyUsage().hasUsages(KeyUsage.keyCertSign));
		assertTrue(KeyUsageEnum.encipherOnly.asKeyUsage().hasUsages(KeyUsage.encipherOnly));
		assertTrue(KeyUsageEnum.decipherOnly.asKeyUsage().hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageNullArray() {
		KeyUsage k = KeyUsageEnum.asKeyUsage((KeyUsageEnum[]) null);
		assertNotNull(k);
		assertFalse(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageNullCollection() {
		KeyUsage k = KeyUsageEnum.asKeyUsage((Collection<KeyUsageEnum>) null);
		assertNotNull(k);
		assertFalse(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageZeroArray() {
		KeyUsageEnum[] arr = new KeyUsageEnum[0];
		KeyUsage k = KeyUsageEnum.asKeyUsage(arr);
		assertNotNull(k);
		assertFalse(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageNullElementInArray() {
		KeyUsageEnum[] arr = new KeyUsageEnum[1];
		KeyUsage k = KeyUsageEnum.asKeyUsage(arr);
		assertNotNull(k);
		assertFalse(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageEmptyCollection() {
		KeyUsage k = KeyUsageEnum.asKeyUsage(Collections.emptyList());
		assertNotNull(k);
		assertFalse(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageNullElementInCollection() {
		List<KeyUsageEnum> collection = new ArrayList<>();
		collection.add(null);
		KeyUsage k = KeyUsageEnum.asKeyUsage(collection);
		assertNotNull(k);
		assertFalse(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageElementInArray() {
		KeyUsageEnum[] arr = new KeyUsageEnum[1];
		arr[0] = KeyUsageEnum.cRLSign;
		KeyUsage k = KeyUsageEnum.asKeyUsage(arr);
		assertNotNull(k);
		assertTrue(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageElementInCollection() {
		List<KeyUsageEnum> collection = new ArrayList<>();
		collection.add(KeyUsageEnum.cRLSign);
		KeyUsage k = KeyUsageEnum.asKeyUsage(collection);
		assertNotNull(k);
		assertTrue(k.hasUsages(KeyUsage.cRLSign));
		assertFalse(k.hasUsages(KeyUsage.digitalSignature));
		assertFalse(k.hasUsages(KeyUsage.nonRepudiation));
		assertFalse(k.hasUsages(KeyUsage.keyEncipherment));
		assertFalse(k.hasUsages(KeyUsage.dataEncipherment));
		assertFalse(k.hasUsages(KeyUsage.keyAgreement));
		assertFalse(k.hasUsages(KeyUsage.keyCertSign));
		assertFalse(k.hasUsages(KeyUsage.encipherOnly));
		assertFalse(k.hasUsages(KeyUsage.decipherOnly));
	}

	@Test
	public void testKeyUsageEnumNull() {
		Collection<KeyUsageEnum> usage = KeyUsageEnum.asKeyUsageEnum(null);
		assertNotNull(usage);
		assertEquals(0, usage.size());
	}

	@Test
	public void testKeyUsageEnum0() {
		Collection<KeyUsageEnum> usage = KeyUsageEnum.asKeyUsageEnum(new KeyUsage(0));
		assertNotNull(usage);
		assertEquals(0, usage.size());
	}

	@Test
	public void testKeyUsageEnumCRL() {
		Collection<KeyUsageEnum> usage = KeyUsageEnum.asKeyUsageEnum(new KeyUsage(KeyUsage.cRLSign));
		assertNotNull(usage);
		assertEquals(1, usage.size());
		assertTrue(usage.contains(KeyUsageEnum.cRLSign));
	}

	@Test
	public void testKeyUsageEnumCRLDS() {
		Collection<KeyUsageEnum> usage = KeyUsageEnum
				.asKeyUsageEnum(new KeyUsage(KeyUsage.cRLSign | KeyUsage.digitalSignature));
		assertNotNull(usage);
		assertEquals(2, usage.size());
		assertTrue(usage.contains(KeyUsageEnum.cRLSign));
		assertTrue(usage.contains(KeyUsageEnum.digitalSignature));
	}

	@Test
	public void testEnum() {
		for (KeyUsageEnum t : KeyUsageEnum.values()) {
			assertEquals(t, KeyUsageEnum.valueOf(t.name()));
			assertEquals(0, t.compareTo(t));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
		}
	}

}
