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

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.ui.model.X500NameModel;

public class TestX500NameModel {

	private String subject = "C=AU,ST=Queensland,L=Gold Coast,STREET=1 Nowhere Pl,O=Internet Widgits Pty Ltd,OU=Development,CN=CA Manager,E=d@a.com";
	
	@Test
	public void testALL() {
		X500Name name = new X500Name(subject);
		X500NameModel model = X500NameModel.create(name);
		assertEquals("CA Manager", model.getCommonName());
		assertEquals("AU", model.getCountry());
		assertEquals("Queensland", model.getState());
		assertEquals("Gold Coast", model.getLocation());
		assertEquals("Internet Widgits Pty Ltd", model.getOrganisation());
		assertEquals("Development", model.getOrganisationUnit());
		assertEquals("d@a.com", model.getEmailAddress());
		assertEquals("1 Nowhere Pl", model.getStreet());
	}
	
	@Test
	public void testALLCreate() {
		
		X500NameModel model = new X500NameModel();
		model.setCommonName("CA Manager");
		model.setCountry("AU");
		model.setState("Queensland");
		model.setLocation("Gold Coast");
		model.setOrganisation("Internet Widgits Pty Ltd");
		model.setOrganisationUnit("Development");
		model.setEmailAddress("d@a.com");
		model.setStreet("1 Nowhere Pl");
		
		X500Name name = new X500Name(subject);
		assertEquals(name, model.asX500Name());
	}
	
	@Test
	public void testCN() {
		X500Name name = new X500Name(subject);
		X500NameModel model = X500NameModel.create(name);
		assertEquals("CA Manager", model.getCommonName());
	}
	
	@Test
	public void testNullX500Name() {
		X500NameModel model = X500NameModel.create(null);
		assertEquals("", model.getCommonName());
		assertEquals("", model.getCountry());
		assertEquals("", model.getState());
		assertEquals("", model.getLocation());
		assertEquals("", model.getOrganisation());
		assertEquals("", model.getOrganisationUnit());
		assertEquals("", model.getEmailAddress());
		assertEquals("", model.getStreet());
	}
	
	@Test
	public void testCreateCN() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("CA Manager");
		assertEquals("CA Manager", model.getCommonName());
		X500Name n = new X500Name("CN=CA Manager");
		assertEquals(n, model.asX500Name());
	}
	
	@Test
	public void testCreateCNwithEmptyField() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("CA Manager");
		assertEquals("CA Manager", model.getCommonName());
		X500Name n = new X500Name("CN=CA Manager");
		assertEquals(n, model.asX500Name());
		
		model.setEmailAddress("");
		assertEquals(n, model.asX500Name());
		model.setEmailAddress("   ");
		assertEquals(n, model.asX500Name());
	}
	
	@Test
	public void testCreateCNEmpty() {
		X500NameModel model = new X500NameModel();
		
		model.setCommonName("");
		assertEquals(null, model.asX500Name());
		
		model.setCommonName(" ");
		assertEquals(null, model.asX500Name());
		
		model.setCommonName("                        ");
		assertEquals(null, model.asX500Name());
		
		model.setCommonName("\n");
		assertEquals(null, model.asX500Name());
		
		model.setCommonName("\n    ");
		assertEquals(null, model.asX500Name());
		
		model.setCommonName("\n    \n");
		assertEquals(null, model.asX500Name());
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_CN() {
		X500NameModel model = new X500NameModel();
		model.setCommonName(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_OU() {
		X500NameModel model = new X500NameModel();
		model.setOrganisationUnit(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_E() {
		X500NameModel model = new X500NameModel();
		model.setEmailAddress(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_O() {
		X500NameModel model = new X500NameModel();
		model.setOrganisation(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_L() {
		X500NameModel model = new X500NameModel();
		model.setLocation(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_ST() {
		X500NameModel model = new X500NameModel();
		model.setStreet(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_S() {
		X500NameModel model = new X500NameModel();
		model.setState(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testNullField_C() {
		X500NameModel model = new X500NameModel();
		model.setCountry(null);
	}
	
	@Test
	public void testEscapted() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("CN=CA Manager,New Manager");
		assertEquals("CN=CA Manager,New Manager", model.getCommonName());
		X500Name n = new X500Name("CN=CN\\=CA Manager\\,New Manager");
		assertEquals(n, model.asX500Name());
		
		// Ensure escaped chars are handled correctly...
		X500NameModel newModel = X500NameModel.create(n);
		assertEquals("CN=CA Manager,New Manager", newModel.getCommonName());
	}
	
	@Test
	public void testEscapted2() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("CN=CA Manager\\,New Manager");
		assertEquals("CN=CA Manager\\,New Manager", model.getCommonName());
		X500Name n = new X500Name("CN=CN\\=CA Manager\\\\\\,New Manager");
		assertEquals(n, model.asX500Name());
		
		// Ensure escaped chars are handled correctly...
		X500NameModel newModel = X500NameModel.create(n);
		assertEquals("CN=CA Manager\\,New Manager", newModel.getCommonName());
	}
	
	@Test
	public void testEscapted3() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("CN=CA Manager\\,New Manager # 12345");
		model.setCountry("AU");
		model.setEmailAddress("\"user\\.a@aolc.om");
		model.setLocation("My Home");
		model.setOrganisation("Org. Inc. Pty/Ltd #asd");
		model.setOrganisationUnit("Development Farm");
		model.setState("Queensland");
		model.setStreet("1234 nowhere place");
		
		X500Name n = model.asX500Name();

		X500NameModel newModel = X500NameModel.create(n);
		X500Name n2 = newModel.asX500Name();

		X500NameModel newModel2 = X500NameModel.create(n2);
		X500Name n3 = newModel2.asX500Name();
		
		System.out.println(n.toString());
		// Ensure escaped / Unicode chars are handled correctly...
		assertEquals(n, n2);
		assertEquals(n2, n3);
	}
	
	@Test
	public void testUnicodeUA() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("Моє ім'я");
		model.setCountry("UA");
		model.setEmailAddress("my.name@bestisp.com");
		model.setLocation("Київ Україна");
		model.setOrganisation("Київська політехніка");
		model.setOrganisationUnit("розробка програмного забезпечення");
		model.setState("Україна");
		model.setStreet("Пр. Перемоги, 37");
		
		X500Name n = model.asX500Name();

		X500NameModel newModel = X500NameModel.create(n);
		X500Name n2 = newModel.asX500Name();

		X500NameModel newModel2 = X500NameModel.create(n2);
		X500Name n3 = newModel2.asX500Name();
		
		System.out.println(n.toString());
		// Ensure escaped / Unicode chars are handled correctly...
		assertEquals(n, n2);
		assertEquals(n2, n3);
	}
	
	@Test
	public void testUnicodeDK() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("Elin Lykke");
		model.setCountry("DK");
		model.setEmailAddress("elin@lykke.co.dk");
		model.setLocation("Rønne");
		model.setOrganisation("Lykke Hjemmesoftware");
		model.setOrganisationUnit("markedsføring");
		model.setState("Bornholm");
		model.setStreet("1, ingen gade");
		
		X500Name n = model.asX500Name();

		X500NameModel newModel = X500NameModel.create(n);
		X500Name n2 = newModel.asX500Name();

		X500NameModel newModel2 = X500NameModel.create(n2);
		X500Name n3 = newModel2.asX500Name();
		
		System.out.println(n.toString());
		// Ensure escaped / Unicode chars are handled correctly...
		assertEquals(n, n2);
		assertEquals(n2, n3);
	}
	
	@Test
	public void testUnicodeIL() {
		X500NameModel model = new X500NameModel();
		model.setCommonName("יוסף");
		model.setCountry("IL");
		model.setEmailAddress("jo@isp.il");
		model.setLocation("תֵּל־אָבִיב–יָפוֹ");
		model.setOrganisation("אוניברסיטת תל אביב");
		model.setOrganisationUnit("מהנדס תוכנה");
		model.setState("");
		model.setStreet("1, אין רחוב");
		
		X500Name n = model.asX500Name();
		
		X500NameModel newModel = X500NameModel.create(n);
		X500Name n2 = newModel.asX500Name();

		X500NameModel newModel2 = X500NameModel.create(n2);
		X500Name n3 = newModel2.asX500Name();
		
		System.out.println(n.toString());
		// Ensure escaped / Unicode chars are handled correctly...
		assertEquals(n, n2);
		assertEquals(n2, n3);
	}
}
