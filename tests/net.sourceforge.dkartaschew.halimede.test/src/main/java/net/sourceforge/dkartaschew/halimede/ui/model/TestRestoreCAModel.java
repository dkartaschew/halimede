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
package net.sourceforge.dkartaschew.halimede.ui.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;

import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.TestUtilities;

/**
 * Basic tests of RestoreCAModel
 */
public class TestRestoreCAModel {

	@Test
	public void testObject() {
		RestoreCAModel model = new RestoreCAModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		assertNull(model.getBaseLocation());
		assertNull(model.getFilename());
		assertFalse(model.isAddToManager());

		assertFalse(model.equals(null));
		assertFalse(model.equals(new Object()));
		assertTrue(model.equals(model));
	}
	
	@Test
	public void testBasePath() {
		RestoreCAModel model = new RestoreCAModel();
		RestoreCAModel model2 = new RestoreCAModel();
		assertTrue(model.equals(model2));

		model.setBaseLocation(Paths.get(TestUtilities.TMP).toString());
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setBaseLocation(Paths.get("model1").toString());
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setBaseLocation(model.getBaseLocation());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(Paths.get(TestUtilities.TMP).toString(), model.getBaseLocation());

		assertNull(model.getFilename());
		assertFalse(model.isAddToManager());
	}
	
	@Test
	public void testFilename() {
		RestoreCAModel model = new RestoreCAModel();
		RestoreCAModel model2 = new RestoreCAModel();
		assertTrue(model.equals(model2));

		model.setFilename("File1");
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setFilename("File2");
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setFilename("File1");
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals("File1", model.getFilename());

		assertNull(model.getBaseLocation());
		assertFalse(model.isAddToManager());
	}
	
	@Test
	public void testAddToManager() {
		RestoreCAModel model = new RestoreCAModel();
		RestoreCAModel model2 = new RestoreCAModel();
		assertTrue(model.equals(model2));

		model.setAddToManager(true);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setAddToManager(false);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setAddToManager(true);
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(true, model.isAddToManager());

		assertNull(model.getBaseLocation());
		assertNull(model.getFilename());
		assertTrue(model.isAddToManager());
		assertNull(model2.getBaseLocation());
		assertNull(model2.getFilename());
		assertTrue(model2.isAddToManager());
	}
	
}
