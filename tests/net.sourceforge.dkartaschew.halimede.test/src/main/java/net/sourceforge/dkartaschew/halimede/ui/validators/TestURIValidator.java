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

package net.sourceforge.dkartaschew.halimede.ui.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Basic test of URI validation warning...
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestURIValidator {

	@Test
	public void testNullString() {
		URIValidator v = new URIValidator();
		IStatus s = v.validate(null);
		assertEquals(ValidationStatus.warning("").getSeverity(), s.getSeverity());
	}
	
	@Test
	public void testEmptyString() {
		URIValidator v = new URIValidator();
		IStatus s = v.validate("");
		assertEquals(ValidationStatus.warning("").getSeverity(), s.getSeverity());
	}
	
	@Test
	public void testValidString() {
		URIValidator v = new URIValidator();
		IStatus s = v.validate("http://a.com/a");
		assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
	}
	
	@Test
	public void testInvalidString() {
		URIValidator v = new URIValidator();
		IStatus s = v.validate("http://a.\\#$%!com/a");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testObject() {
		URIValidator v = new URIValidator();
		assertTrue(v.equals(v));
		assertEquals(v.hashCode(), v.hashCode());
		v.toString();
	}

}
