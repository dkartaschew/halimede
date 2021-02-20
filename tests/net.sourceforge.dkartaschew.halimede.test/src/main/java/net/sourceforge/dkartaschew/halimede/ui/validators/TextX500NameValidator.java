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

package net.sourceforge.dkartaschew.halimede.ui.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.bouncycastle.asn1.x500.X500Name;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Basic test of X500Name validation warning...
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TextX500NameValidator {

	private X500Name i1 = new X500Name("CN=issuer");
	private X500Name i2 = new X500Name("CN=issuer2");

	@Test
	public void testOject() {
		X500NameValidator v1 = new X500NameValidator(null);
		X500NameValidator v2 = new X500NameValidator(null);
		X500NameValidator v3 = new X500NameValidator(Collections.emptyList());
		Collection<X500Name> c1 = new ArrayList<>();
		Collection<X500Name> c2 = new ArrayList<>();
		c1.add(i1);
		c2.add(i1);
		c2.add(i2);

		X500NameValidator v4 = new X500NameValidator(c1);
		X500NameValidator v5 = new X500NameValidator(c2);
		X500NameValidator v6 = new X500NameValidator(c2);

		System.out.println(v1.toString());
		System.out.println(v3.toString());
		System.out.println(v4.toString());
		System.out.println(v5.toString());
		assertEquals(v1, v2);
		assertNotEquals(v1, v3);
		assertNotEquals(v3, v4);
		assertNotEquals(v5, v4);
		assertEquals(v1.hashCode(), v2.hashCode());
		assertNotEquals(v1.hashCode(), v3.hashCode());
		assertNotEquals(v3.hashCode(), v4.hashCode());
		assertNotEquals(v5.hashCode(), v4.hashCode());
		
		assertFalse(v1.equals(null));
		assertTrue(v1.equals(v1));
		assertFalse(v1.equals(new Object()));
		assertTrue(v5.equals(v6));
	}

	@Test
	public void testNullName() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate(null);
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testEmptyName() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate("");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testMalformedName() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate("124%$#");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testValidName() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate("CN=Test");
		assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
	}

	@Test
	public void testValidName2() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate("CN=Test2,C=AU");
		assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
	}

	@Test
	public void testMissingCN() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate("O=Me,C=AU");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}
	
	@Test
	public void testEmptyCN() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate("CN=,O=Me,C=AU");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}
	
	@Test
	public void testInavlidRDN() {
		X500NameValidator v = new X500NameValidator(null);
		IStatus s = v.validate("CN=,O=Me,C=AU,EMAIL_SUP=a@b.com");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testValidNameEmptyIssuerList() {
		X500NameValidator v = new X500NameValidator(Collections.emptyList());
		IStatus s = v.validate("CN=Test");
		assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
	}

	@Test
	public void testValidName2EmptyIssuerList() {
		X500NameValidator v = new X500NameValidator(Collections.emptyList());
		IStatus s = v.validate("CN=Test2,C=AU");
		assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
	}

	@Test
	public void testEqualIssuerList() {
		Collection<X500Name> c2 = new ArrayList<>();
		c2.add(i1);
		c2.add(i2);
		X500NameValidator v = new X500NameValidator(c2);
		IStatus s = v.validate(i1.toString());
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testEqualIssuerList2() {
		Collection<X500Name> c2 = new ArrayList<>();
		c2.add(i1);
		c2.add(i2);
		X500NameValidator v = new X500NameValidator(c2);
		IStatus s = v.validate(i2.toString());
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testNotEqualIssuerList() {
		Collection<X500Name> c2 = new ArrayList<>();
		c2.add(i1);
		c2.add(i2);
		X500NameValidator v = new X500NameValidator(c2);
		IStatus s = v.validate("CN=Test2,C=AU");
		assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
	}
}
