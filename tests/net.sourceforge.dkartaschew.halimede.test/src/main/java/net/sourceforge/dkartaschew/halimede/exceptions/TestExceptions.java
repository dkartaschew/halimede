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

package net.sourceforge.dkartaschew.halimede.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestExceptions {

	@Test
	public void testDatastoreLockedException() {
		DatastoreLockedException e = new DatastoreLockedException("Locked");
		assertEquals("Locked", e.getMessage());
		assertEquals("Locked", ExceptionUtil.getMessage(e));
		e.toString();
		assertEquals(e.hashCode(), e.hashCode());
	}

	@Test
	public void testInvalidPasswordException() {
		InvalidPasswordException e = new InvalidPasswordException("InvalidPasswordException");
		assertEquals("InvalidPasswordException", e.getMessage());
		assertEquals("InvalidPasswordException", ExceptionUtil.getMessage(e));
		e.toString();
		assertEquals(e.hashCode(), e.hashCode());
	}

	@Test
	public void testUnknownKeyTypeException() {
		UnknownKeyTypeException e = new UnknownKeyTypeException("UnknownKeyTypeException");
		assertEquals("UnknownKeyTypeException", e.getMessage());
		assertEquals("UnknownKeyTypeException", ExceptionUtil.getMessage(e));
		e.toString();
		assertEquals(e.hashCode(), e.hashCode());
	}
	
	@Test
	public void testExceptionUtilNull() {
		assertEquals("No exception passed?", ExceptionUtil.getMessage(null));
	}
	
	@Test
	public void testExceptionUtilNoMessage() {
		assertEquals(IOException.class.getName(), ExceptionUtil.getMessage(new IOException()));
	}
	
	@Test
	public void testExceptionUtilEmptyMessage() {
		assertEquals(IOException.class.getName(), ExceptionUtil.getMessage(new IOException("")));
	}
	
	@Test
	public void testExceptionUtilEmptyMessage2() {
		assertEquals(IOException.class.getName(), ExceptionUtil.getMessage(new IOException("  ")));
	}
	
	@Test
	public void testExceptionUtilTrimMessage() {
		assertEquals("abc", ExceptionUtil.getMessage(new IOException("  abc   ")));
	}
	
	@Test
	public void testExceptionUtilObject() {
		ExceptionUtil e = new ExceptionUtil();
		assertTrue(e.equals(e));
		e.toString();
		assertEquals(e.hashCode(), e.hashCode());
	}
}
