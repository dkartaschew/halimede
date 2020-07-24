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

import java.io.File;
import java.nio.file.Path;

import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;

/**
 * Basic test of Path validation warning...
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPathValidator {

	@Test
	public void testNullString() {
		PathValidator v = new PathValidator();
		IStatus s = v.validate(null);
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testEmptyString() {
		PathValidator v = new PathValidator();
		IStatus s = v.validate("");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testValidFile() {
		Path file = TestUtilities.getFile("ec521key_des3.pem");
		PathValidator v = new PathValidator();
		IStatus s = v.validate(file.toAbsolutePath().toString());
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testMissingFile() {
		Path file = TestUtilities.getFile("ec521key_des3.pem").getParent();
		PathValidator v = new PathValidator();
		IStatus s = v.validate(file.toAbsolutePath() + ".old");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testIsDirectory() {
		Path file = TestUtilities.getFile("ec521key_des3.pem").getParent();
		PathValidator v = new PathValidator();
		IStatus s = v.validate(file.toAbsolutePath().toString());
		assertEquals(ValidationStatus.ok().getSeverity(), s.getSeverity());
	}

	@Test
	public void testNonReadablePath() {
		File file = new File("/var/run/udisks2");
		PathValidator v = new PathValidator();
		IStatus s = v.validate(file.getAbsolutePath());
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testInvalidPath() {
		PathValidator v = new PathValidator();
		IStatus s = v.validate("http://a.com/a");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testNonAbsoluteFile() {
		PathValidator v = new PathValidator();
		IStatus s = v.validate(".." + File.separator + "file.txt");
		assertEquals(ValidationStatus.error("").getSeverity(), s.getSeverity());
	}

	@Test
	public void testObject() {
		PathValidator v = new PathValidator();
		assertTrue(v.equals(v));
		assertEquals(v.hashCode(), v.hashCode());
		v.toString();
	}

}
