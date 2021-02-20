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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Paths;
import java.util.UUID;

import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

/**
 * Basic tests of CASettingsModel
 */
public class TestCASettingsModel {

	@Test
	public void testObject() {
		CASettingsModel model = new CASettingsModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		assertNull(model.getBasePath());
		assertNull(model.getDescription());
		assertEquals(0, model.getExpiryDays());
		assertNull(model.getNodeID());
		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());
		assertNull(model.getSubject());
		assertFalse(model.isEnableLog());
		assertFalse(model.isIncrementalSerial());

		assertFalse(model.equals(null));
		assertFalse(model.equals(new Object()));
		assertTrue(model.equals(model));
	}

	@Test
	public void testUUID() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		UUID id = UUID.randomUUID();
		model.setNodeID(id);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());
		model.toString();

		model2.setNodeID(UUID.randomUUID());
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setNodeID(model.getNodeID());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(id, model.getNodeID());

		assertNull(model.getBasePath());
		assertNull(model.getDescription());
		assertEquals(0, model.getExpiryDays());
		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());
		assertNull(model.getSubject());
	}

	@Test
	public void testBasePath() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setBasePath(Paths.get(TestUtilities.TMP));
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setBasePath(Paths.get("model1"));
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setBasePath(model.getBasePath());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(Paths.get(TestUtilities.TMP), model.getBasePath());

		assertNull(model.getDescription());
		assertEquals(0, model.getExpiryDays());
		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());
		assertNull(model.getSubject());
	}

	@Test
	public void testDescription() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setBasePath(Paths.get(TestUtilities.TMP));
		model2.setBasePath(model.getBasePath());

		model.setDescription("Description");
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setDescription("Description2");
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setDescription(model.getDescription());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals("Description", model.getDescription());

		assertEquals(0, model.getExpiryDays());
		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());
		assertNull(model.getSubject());
	}

	@Test
	public void testExpiry() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setBasePath(Paths.get(TestUtilities.TMP));
		model2.setBasePath(model.getBasePath());

		model.setDescription("Description");
		model2.setDescription(model.getDescription());

		model.setExpiryDays(12);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setExpiryDays(-1);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setExpiryDays(model.getExpiryDays());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(12, model.getExpiryDays());

		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());
		assertNull(model.getSubject());
	}

	@Test
	public void testSubject() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setBasePath(Paths.get(TestUtilities.TMP));
		model2.setBasePath(model.getBasePath());

		model.setDescription("Description");
		model2.setDescription(model.getDescription());

		model.setExpiryDays(12);
		model2.setExpiryDays(model.getExpiryDays());

		model.setSubject("CN=Text2");
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setSubject("CN=Text");
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setSubject(model.getSubject());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals("CN=Text2", model.getSubject());

		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());

	}

	@Test
	public void testSignature() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setBasePath(Paths.get(TestUtilities.TMP));
		model2.setBasePath(model.getBasePath());

		model.setDescription("Description");
		model2.setDescription(model.getDescription());

		model.setExpiryDays(12);
		model2.setExpiryDays(model.getExpiryDays());

		model.setSubject("CN=Text2");
		model2.setSubject(model.getSubject());

		model.setSignatureAlgorithm(SignatureAlgorithm.MD2withRSA);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setSignatureAlgorithm(SignatureAlgorithm.SHA1withRSA);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setSignatureAlgorithm(model.getSignatureAlgorithm());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(SignatureAlgorithm.MD2withRSA, model.getSignatureAlgorithm());

		assertNull(model.getSignatureAlgorithms());

	}

	@Test
	public void testSignatures() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setBasePath(Paths.get(TestUtilities.TMP));
		model2.setBasePath(model.getBasePath());

		model.setDescription("Description");
		model2.setDescription(model.getDescription());

		model.setExpiryDays(12);
		model2.setExpiryDays(model.getExpiryDays());

		model.setSubject("CN=Text2");
		model2.setSubject(model.getSubject());

		model.setSignatureAlgorithm(SignatureAlgorithm.MD2withRSA);
		model2.setSignatureAlgorithm(model.getSignatureAlgorithm());

		model.setSignatureAlgorithms(
				SignatureAlgorithm.forType(SignatureAlgorithm.MD2withRSA).toArray(new SignatureAlgorithm[0]));
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setSignatureAlgorithms(
				SignatureAlgorithm.forType(SignatureAlgorithm.SHA1withDSA).toArray(new SignatureAlgorithm[0]));
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setSignatureAlgorithms(model.getSignatureAlgorithms());
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertArrayEquals(SignatureAlgorithm.forType(SignatureAlgorithm.MD2withRSA).toArray(new SignatureAlgorithm[0]),
				model.getSignatureAlgorithms());

	}
	
	@Test
	public void testEnableLog() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setEnableLog(true);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setEnableLog(false);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setEnableLog(true);
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(true, model.isEnableLog());
		assertEquals(true, model2.isEnableLog());

		assertNull(model.getDescription());
		assertEquals(0, model.getExpiryDays());
		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());
		assertNull(model.getSubject());
	}
	
	@Test
	public void testIncrementalSerial() {
		CASettingsModel model = new CASettingsModel();
		CASettingsModel model2 = new CASettingsModel();

		model.setNodeID(UUID.randomUUID());
		model2.setNodeID(model.getNodeID());

		model.setIncrementalSerial(true);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setIncrementalSerial(false);
		assertNotEquals(model, model2);
		assertNotEquals(model2, model);
		assertNotEquals(model.hashCode(), model2.hashCode());

		model2.setIncrementalSerial(true);
		assertEquals(model, model2);
		assertEquals(model.hashCode(), model2.hashCode());

		assertEquals(true, model.isIncrementalSerial());
		assertEquals(true, model2.isIncrementalSerial());

		assertNull(model.getDescription());
		assertEquals(0, model.getExpiryDays());
		assertNull(model.getSignatureAlgorithm());
		assertNull(model.getSignatureAlgorithms());
		assertNull(model.getSubject());
	}
}
