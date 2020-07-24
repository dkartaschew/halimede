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

package net.sourceforge.dkartaschew.halimede.ui.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;

import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

public class TestNewCAModel {

	private String STR = "StringValue";
	private String X500 = "CN=" + STR;
	private String CRL = "http://" + STR;
	private ZonedDateTime now = ZonedDateTime.now();
	private KeyType type = KeyType.DSA_3072;

	@Test
	public void testCADescription() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getcADescription());
		model.setcADescription(STR);
		assertEquals(STR, model.getcADescription());
		model.setcADescription(null);
		assertEquals(null, model.getcADescription());
	}

	@Test
	public void testBaseLocation() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getBaseLocation());
		model.setBaseLocation(STR);
		assertEquals(STR, model.getBaseLocation());
		model.setBaseLocation(null);
		assertEquals(null, model.getBaseLocation());
	}

	@Test
	public void testX500Name() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getX500Name());
		model.setX500Name(X500);
		assertEquals(X500, model.getX500Name());
		model.setX500Name(null);
		assertEquals(null, model.getX500Name());
	}

	@Test
	public void testCRLLocation() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getcRLLocation());
		model.setcRLLocation(CRL);
		assertEquals(CRL, model.getcRLLocation());
		model.setcRLLocation(null);
		assertEquals(null, model.getcRLLocation());
	}

	@Test
	public void testStartDate() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getStartDate());
		model.setStartDate(now);
		assertEquals(now, model.getStartDate());
		model.setStartDate(null);
		assertEquals(null, model.getStartDate());
	}

	@Test
	public void testExpiryDate() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getExpiryDate());
		model.setExpiryDate(now);
		assertEquals(now, model.getExpiryDate());
		model.setExpiryDate(null);
		assertEquals(null, model.getExpiryDate());
	}

	@Test
	public void testKeyType() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getKeyType());
		model.setKeyType(type);
		assertEquals(type, model.getKeyType());
		model.setKeyType(null);
		assertEquals(null, model.getKeyType());
	}

	@Test
	public void testPassword() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getPassword());
		model.setPassword(STR);
		assertEquals(STR, model.getPassword());
		model.setPassword(null);
		assertEquals(null, model.getPassword());
	}

	@Test
	public void testIsPkcs12() {
		NewCAModel model = new NewCAModel();
		assertEquals(false, model.isPkcs12());
		model.setPkcs12(true);
		assertEquals(true, model.isPkcs12());
		model.setPkcs12(false);
		assertEquals(false, model.isPkcs12());
	}

	@Test
	public void testIsCertPrivateKey() {
		NewCAModel model = new NewCAModel();
		assertEquals(false, model.isCertPrivateKey());
		model.setCertPrivateKey(true);
		assertEquals(true, model.isCertPrivateKey());
		model.setCertPrivateKey(false);
		assertEquals(false, model.isCertPrivateKey());
	}

	@Test
	public void testGetPkcs12Filename() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getPkcs12Filename());
		model.setPkcs12Filename(STR);
		assertEquals(STR, model.getPkcs12Filename());
		model.setPkcs12Filename(null);
		assertEquals(null, model.getPkcs12Filename());
	}

	@Test
	public void testGetCertFilename() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getCertFilename());
		model.setCertFilename(STR);
		assertEquals(STR, model.getCertFilename());
		model.setCertFilename(null);
		assertEquals(null, model.getCertFilename());
	}

	@Test
	public void testPrivateKeyFilename() {
		NewCAModel model = new NewCAModel();
		assertEquals(null, model.getCertFilename());
		model.setCertFilename(STR);
		assertEquals(STR, model.getCertFilename());
		model.setCertFilename(null);
		assertEquals(null, model.getCertFilename());
	}

	@Test
	public void testObject() {
		NewCAModel model1 = new NewCAModel();
		NewCAModel model2 = new NewCAModel();
		assertEquals(model1, model2);
		assertTrue(!model1.toString().contains("null"));
		int hashCode = model1.hashCode();
		
		assertFalse(model1.equals(null));
		assertTrue(model1.equals(model1));
		assertFalse(model1.equals(new Object()));
		/*
		 * Description
		 */
		
		model1.setcADescription(STR);
		model2.setcADescription(STR);

		assertEquals(model1, model2);
		assertTrue(!model1.toString().contains("null"));
		assertTrue(model1.toString().contains(STR));
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setcADescription(X500);
		assertNotEquals(model1, model2);

		model2.setcADescription(null);
		assertNotEquals(model1, model2);

		model2.setcADescription(model1.getcADescription());
		model1.setcADescription(null);
		assertNotEquals(model1, model2);
		
		model1.setcADescription(model2.getcADescription());
		assertEquals(model1, model2);
		
		/*
		 * Base location
		 */
		
		model1.setBaseLocation(STR);
		model2.setBaseLocation(STR);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setBaseLocation(X500);
		assertNotEquals(model1, model2);

		model2.setBaseLocation(null);
		assertNotEquals(model1, model2);

		model2.setBaseLocation(model1.getBaseLocation());
		model1.setBaseLocation(null);
		assertNotEquals(model1, model2);
		
		model1.setBaseLocation(model2.getBaseLocation());
		
		assertEquals(model1, model2);
		
		/*
		 * X500 Name
		 */
		
		model1.setX500Name(X500);
		model2.setX500Name(X500);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setX500Name(STR);
		assertNotEquals(model1, model2);

		model2.setX500Name(null);
		assertNotEquals(model1, model2);

		model2.setX500Name(model1.getX500Name());
		model1.setX500Name(null);
		assertNotEquals(model1, model2);
		
		model1.setX500Name(model2.getX500Name());
		assertEquals(model1, model2);
		
		/*
		 * CRL Location
		 */
		
		model1.setcRLLocation(CRL);
		model2.setcRLLocation(CRL);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setcRLLocation(STR);
		assertNotEquals(model1, model2);

		model2.setcRLLocation(null);
		assertNotEquals(model1, model2);

		model2.setcRLLocation(model1.getcRLLocation());
		model1.setcRLLocation(null);
		assertNotEquals(model1, model2);
		
		model1.setcRLLocation(model2.getcRLLocation());
		assertEquals(model1, model2);
		
		/*
		 * Start Date
		 */
		
		model1.setStartDate(now);
		model2.setStartDate(now);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setStartDate(now.minusMinutes(1));
		assertNotEquals(model1, model2);

		model2.setStartDate(null);
		assertNotEquals(model1, model2);

		model2.setStartDate(model1.getStartDate());
		model1.setStartDate(null);
		assertNotEquals(model1, model2);
		
		model1.setStartDate(model2.getStartDate());
		assertEquals(model1, model2);
		
		/*
		 * Expiry Date
		 */
		
		model1.setExpiryDate(now);
		model2.setExpiryDate(now);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setExpiryDate(now.minusMinutes(1));
		assertNotEquals(model1, model2);

		model2.setExpiryDate(null);
		assertNotEquals(model1, model2);

		model2.setExpiryDate(model1.getExpiryDate());
		model1.setExpiryDate(null);
		assertNotEquals(model1, model2);
		
		model1.setExpiryDate(model2.getExpiryDate());
		assertEquals(model1, model2);
		
		/*
		 * Key Type
		 */
		
		model1.setKeyType(type);
		model2.setKeyType(type);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setKeyType(KeyType.EC_prime192v1);
		assertNotEquals(model1, model2);

		model2.setKeyType(null);
		assertNotEquals(model1, model2);

		model2.setKeyType(model1.getKeyType());
		model1.setKeyType(null);
		assertNotEquals(model1, model2);
		
		model1.setKeyType(model2.getKeyType());
		assertEquals(model1, model2);
		
		/*
		 * Password
		 */
		
		model1.setPassword(STR);
		model2.setPassword(STR);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setPassword(X500);
		assertNotEquals(model1, model2);

		model2.setPassword(null);
		assertNotEquals(model1, model2);

		model2.setPassword(model1.getPassword());
		model1.setPassword(null);
		assertNotEquals(model1, model2);
		
		model1.setPassword(model2.getPassword());
		assertEquals(model1, model2);
		
		/*
		 * PKCS12 location
		 */
		
		model1.setPkcs12Filename(STR);
		model2.setPkcs12Filename(STR);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setPkcs12Filename(X500);
		assertNotEquals(model1, model2);

		model2.setPkcs12Filename(null);
		assertNotEquals(model1, model2);

		model2.setPkcs12Filename(model1.getPkcs12Filename());
		model1.setPkcs12Filename(null);
		assertNotEquals(model1, model2);
		
		model1.setPkcs12Filename(model2.getPkcs12Filename());
		assertEquals(model1, model2);
		
		/*
		 * Cert location
		 */
		
		model1.setCertFilename(STR);
		model2.setCertFilename(STR);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setCertFilename(X500);
		assertNotEquals(model1, model2);

		model2.setCertFilename(null);
		assertNotEquals(model1, model2);

		model2.setCertFilename(model1.getCertFilename());
		model1.setCertFilename(null);
		assertNotEquals(model1, model2);
		
		model1.setCertFilename(model2.getCertFilename());
		assertEquals(model1, model2);
		
		/*
		 * Private Key location
		 */
		
		model1.setPrivateKeyFilename(STR);
		model2.setPrivateKeyFilename(STR);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model2.setPrivateKeyFilename(X500);
		assertNotEquals(model1, model2);

		model2.setPrivateKeyFilename(null);
		assertNotEquals(model1, model2);

		model2.setPrivateKeyFilename(model1.getPrivateKeyFilename());
		model1.setPrivateKeyFilename(null);
		assertNotEquals(model1, model2);
		
		model1.setPrivateKeyFilename(model2.getPrivateKeyFilename());
		assertEquals(model1, model2);
		
		/*
		 * is PKCS12
		 */
		
		model1.setPkcs12(true);
		model2.setPkcs12(true);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model1.setPkcs12(true);
		model2.setPkcs12(false);
		assertNotEquals(model1, model2);

		model1.setPkcs12(false);
		model2.setPkcs12(true);
		assertNotEquals(model1, model2);
		
		model1.setPkcs12(true);
		model2.setPkcs12(true);
		assertEquals(model1, model2);
		
		/*
		 * is private key / cert combo
		 */
		
		model1.setCertPrivateKey(true);
		model2.setCertPrivateKey(true);

		assertEquals(model1, model2);
		assertNotEquals(hashCode, model1.hashCode());
		hashCode = model1.hashCode();

		model1.setCertPrivateKey(true);
		model2.setCertPrivateKey(false);
		assertNotEquals(model1, model2);

		model1.setCertPrivateKey(false);
		model2.setCertPrivateKey(true);
		assertNotEquals(model1, model2);
		
		model1.setCertPrivateKey(true);
		model2.setCertPrivateKey(true);
		assertEquals(model1, model2);
	}
}
