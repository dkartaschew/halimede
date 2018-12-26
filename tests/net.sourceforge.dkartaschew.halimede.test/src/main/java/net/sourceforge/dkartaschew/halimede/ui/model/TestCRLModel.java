/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2019 Darran Kartaschew 
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
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.time.ZonedDateTime;

import org.junit.Test;

public class TestCRLModel {

	private String CA = "Description";
	private BigInteger serial = BigInteger.TEN;
	private ZonedDateTime now = ZonedDateTime.now();
	
	private String CA_2 = "Description2";
	private BigInteger serial_2 = BigInteger.ONE;
	private ZonedDateTime now_2 = ZonedDateTime.now().plusMinutes(10);
	
	@Test
	public void testObject() {
		CACRLModel model1 = new CACRLModel(CA, serial, now);
		CACRLModel model2 = new CACRLModel(CA_2, serial_2, now_2);
		assertNotEquals(model1, model2);
		assertNotEquals(model1.hashCode(), model2.hashCode());
		
		assertTrue(model1.toString().contains(CA));
		assertTrue(model1.toString().contains(serial.toString()));
		
		assertTrue(model1.equals(model1));
		assertFalse(model1.equals(null));
		assertFalse(model1.equals(new Object()));
		
		int hashCode = model1.hashCode();
		model1.setNextDate(now_2);
		assertNotEquals(hashCode, model1.hashCode());
	}
	
	@Test(expected=NullPointerException.class)
	public void testCANull() {
		new CACRLModel(null, serial, now);
	}
	
	@Test(expected=NullPointerException.class)
	public void testSerialNull() {
		new CACRLModel(CA, null, now);
	}
	
	@Test(expected=NullPointerException.class)
	public void testIssueNull() {
		new CACRLModel(CA, serial, null);
	}
	
	@Test
	public void testConstruction() {
		CACRLModel model = new CACRLModel(CA, serial, now);
		assertEquals(CA, model.getCa());
		assertEquals(serial, model.getSerial());
		assertEquals(now, model.getIssueDate());
	}
	
	@Test
	public void testNextDate() {
		CACRLModel model = new CACRLModel(CA, serial, now);
		assertEquals(null, model.getNextDate());
		model.setNextDate(now_2);
		assertEquals(now_2, model.getNextDate());
		model.setNextDate(null);
		assertEquals(null, model.getNextDate());
	}
	
	@Test
	public void testEquality() {
		CACRLModel model1 = new CACRLModel(CA, serial, now);
		CACRLModel model2 = new CACRLModel(CA, serial, now);
		assertEquals(model1, model2);
		model2.setNextDate(now_2);
		assertNotEquals(model1, model2);
		model1.setNextDate(now_2);
		assertEquals(model1, model2);
		model2.setNextDate(null);
		assertNotEquals(model1, model2);
	}
	
	@Test
	public void testEqualityCA() {
		CACRLModel model1 = new CACRLModel(CA, serial, now);
		CACRLModel model2 = new CACRLModel(CA_2, serial, now);
		assertNotEquals(model1, model2);
	}
	
	@Test
	public void testEqualitySerial() {
		CACRLModel model1 = new CACRLModel(CA, serial, now);
		CACRLModel model2 = new CACRLModel(CA, serial_2, now);
		assertNotEquals(model1, model2);
	}
	
	@Test
	public void testEqualityIssueDate() {
		CACRLModel model1 = new CACRLModel(CA, serial, now);
		CACRLModel model2 = new CACRLModel(CA, serial, now_2);
		assertNotEquals(model1, model2);
	}
}
