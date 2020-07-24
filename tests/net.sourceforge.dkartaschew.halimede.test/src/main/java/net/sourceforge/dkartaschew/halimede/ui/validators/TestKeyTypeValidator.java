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

package net.sourceforge.dkartaschew.halimede.ui.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

/**
 * Basic test of key type warning...
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestKeyTypeValidator {

	/**
	 * Get the test data
	 * 
	 * @return The test data
	 */
	@Parameters(name = "{0}")
	public static Collection<KeyType> data() {
		return Arrays.asList(KeyType.values());
	}

	/**
	 * The key type for test.
	 */
	private final KeyType type;

	/**
	 * Create a test run for the given type
	 * 
	 * @param type The key type.
	 */
	public TestKeyTypeValidator(KeyType type) {
		this.type = type;
	}

	/**
	 * Test the keytype for appropriate warning level.
	 */
	@Test
	public void testKeyType() {
		KeyTypeWarningValidator v = new KeyTypeWarningValidator();
		IStatus s = v.validate(type);
		if (type.getType().equals("RSA") && type.getBitLength() > 1024) {
			assertEquals(ValidationStatus.warning("").getSeverity(), s.getSeverity());
		} else if (type.getType().equals("DSA") && type.getBitLength() >= 1024) {
			assertEquals(ValidationStatus.warning("").getSeverity(), s.getSeverity());
		} else if (type.getType().equals("XMSS") && type.getHeight() > 10) {
			assertEquals(ValidationStatus.warning("").getSeverity(), s.getSeverity());
		} else if (type.getType().equals("XMSSMT") && type.getHeight() / type.getLayers() > 5) {
			assertEquals(ValidationStatus.warning("").getSeverity(), s.getSeverity());
		} else {
			assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
		}

		assertTrue(v.equals(v));
		assertEquals(v.hashCode(), v.hashCode());
		v.toString();
	}

}
