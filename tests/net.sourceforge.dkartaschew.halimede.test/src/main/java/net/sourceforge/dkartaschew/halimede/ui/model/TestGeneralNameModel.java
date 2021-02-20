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

import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;

/**
 * Tests for the GeneralNameModel
 */
public class TestGeneralNameModel {

	/**
	 * Test basic object initial state.
	 */
	@Test
	public void testObject() {
		GeneralNameModel model = new GeneralNameModel();
		assertEquals("", model.getValue());
		assertEquals(GeneralNameTag.dNSName, model.getTag());
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
	}

	@Test
	public void testFromGeneralName() {
		GeneralName name = new GeneralName(GeneralName.uniformResourceIdentifier, "http://a.com");
		GeneralNameModel model = new GeneralNameModel(name);
		assertEquals(GeneralNameTag.uniformResourceIdentifier, model.getTag());
		assertEquals("http://a.com", model.getValue());
	}

	@Test(expected = NullPointerException.class)
	public void testFromNullGeneralName() {
		new GeneralNameModel(null);
	}

	@Test
	public void testTag() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(GeneralNameTag.uniformResourceIdentifier);
		assertEquals(GeneralNameTag.uniformResourceIdentifier, model.getTag());
	}

	@Test
	public void testNullTag() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(null);
		assertEquals(null, model.getTag());
	}

	@Test
	public void testValue() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(GeneralNameTag.uniformResourceIdentifier);
		model.setValue("http://a.com");
		assertEquals("http://a.com", model.getValue());
	}

	@Test
	public void testNullValue() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(GeneralNameTag.uniformResourceIdentifier);
		model.setValue("http://a.com");
		assertEquals("http://a.com", model.getValue());
		model.setValue(null);
		assertEquals(null, model.getValue());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullTagGenerate() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(null);
		model.createGeneralNameFromModel();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullValueGenerate() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(GeneralNameTag.uniformResourceIdentifier);
		model.setValue(null);
		model.createGeneralNameFromModel();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEmptyValueGenerate() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(GeneralNameTag.uniformResourceIdentifier);
		model.setValue("");
		model.createGeneralNameFromModel();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBadValueGenerate() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(GeneralNameTag.uniformResourceIdentifier);
		model.setValue("123$%&^%");
		model.createGeneralNameFromModel();
	}
	
	@Test
	public void testValueGenerate() {
		GeneralNameModel model = new GeneralNameModel();
		model.setTag(GeneralNameTag.uniformResourceIdentifier);
		model.setValue("http://a.com");
		GeneralName name = model.createGeneralNameFromModel();
		assertEquals(GeneralName.uniformResourceIdentifier, name.getTagNo());
		assertEquals("http://a.com", name.getName().toString());
	}
}
