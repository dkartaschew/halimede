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

package net.sourceforge.dkartaschew.halimede.ui.model;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;

/**
 * Basic tests of the ExportInformationModel
 */
public class TestExportInformationModel {

	@Test
	public void testObject() {
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		assertNull(model.getDialogTitle());
		assertNull(model.getEncoding());
		assertNull(model.getFilename());
		assertNull(model.getPassword());
		assertNull(model.getPkcs12Cipher());
		assertNull(model.getPkcs8Cipher());
		assertNull(model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testDialogTitle() {
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		assertEquals("Title", model.getDialogTitle());
		assertNull(model.getEncoding());
		assertNull(model.getFilename());
		assertNull(model.getPassword());
		assertNull(model.getPkcs12Cipher());
		assertNull(model.getPkcs8Cipher());
		assertNull(model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testEncoding() {
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertNull(model.getFilename());
		assertNull(model.getPassword());
		assertNull(model.getPkcs12Cipher());
		assertNull(model.getPkcs8Cipher());
		assertNull(model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testFilename() {
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		model.setFilename("Filename");
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertEquals("Filename", model.getFilename());
		assertNull(model.getPassword());
		assertNull(model.getPkcs12Cipher());
		assertNull(model.getPkcs8Cipher());
		assertNull(model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testPassword() {
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		model.setFilename("Filename");
		model.setPassword("Password");
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertEquals("Filename", model.getFilename());
		assertEquals("Password", model.getPassword());
		assertNull(model.getPkcs12Cipher());
		assertNull(model.getPkcs8Cipher());
		assertNull(model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testPKCS12() {
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		model.setFilename("Filename");
		model.setPassword("Password");
		model.setPkcs12Cipher(PKCS12Cipher.DES3);
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertEquals("Filename", model.getFilename());
		assertEquals("Password", model.getPassword());
		assertEquals(PKCS12Cipher.DES3, model.getPkcs12Cipher());
		assertNull(model.getPkcs8Cipher());
		assertNull(model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testPKCS8() {
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		model.setFilename("Filename");
		model.setPassword("Password");
		model.setPkcs12Cipher(PKCS12Cipher.DES3);
		model.setPkcs8Cipher(PKCS8Cipher.AES_128_CBC);
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertEquals("Filename", model.getFilename());
		assertEquals("Password", model.getPassword());
		assertEquals(PKCS12Cipher.DES3, model.getPkcs12Cipher());
		assertEquals(PKCS8Cipher.AES_128_CBC, model.getPkcs8Cipher());
		assertNull(model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testFilterExt() {
		String[] ext = new String[] { "*.cer", "*.*" };
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		model.setFilename("Filename");
		model.setPassword("Password");
		model.setPkcs12Cipher(PKCS12Cipher.DES3);
		model.setPkcs8Cipher(PKCS8Cipher.AES_128_CBC);
		model.setSaveDialogFilterExtensions(ext);
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertEquals("Filename", model.getFilename());
		assertEquals("Password", model.getPassword());
		assertEquals(PKCS12Cipher.DES3, model.getPkcs12Cipher());
		assertEquals(PKCS8Cipher.AES_128_CBC, model.getPkcs8Cipher());
		assertArrayEquals(ext, model.getSaveDialogFilterExtensions());
		assertNull(model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testFilterName() {
		String[] ext = new String[] { "*.cer", "*.*" };
		String[] names = new String[] { "Certificate (*.cer)", "All Files (*.*)" };
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		model.setFilename("Filename");
		model.setPassword("Password");
		model.setPkcs12Cipher(PKCS12Cipher.DES3);
		model.setPkcs8Cipher(PKCS8Cipher.AES_128_CBC);
		model.setSaveDialogFilterExtensions(ext);
		model.setSaveDialogFilterNames(names);
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertEquals("Filename", model.getFilename());
		assertEquals("Password", model.getPassword());
		assertEquals(PKCS12Cipher.DES3, model.getPkcs12Cipher());
		assertEquals(PKCS8Cipher.AES_128_CBC, model.getPkcs8Cipher());
		assertArrayEquals(ext, model.getSaveDialogFilterExtensions());
		assertArrayEquals(names, model.getSaveDialogFilterNames());
		assertNull(model.getSaveDialogTitle());
	}
	
	@Test
	public void testSaveDialogTitle() {
		String[] ext = new String[] { "*.cer", "*.*" };
		String[] names = new String[] { "Certificate (*.cer)", "All Files (*.*)" };
		ExportInformationModel model = new ExportInformationModel();
		assertEquals(model, model);
		assertEquals(model.hashCode(), model.hashCode());
		model.toString();
		model.setDialogTitle("Title");
		model.setEncoding(EncodingType.DER);
		model.setFilename("Filename");
		model.setPassword("Password");
		model.setPkcs12Cipher(PKCS12Cipher.DES3);
		model.setPkcs8Cipher(PKCS8Cipher.AES_128_CBC);
		model.setSaveDialogFilterExtensions(ext);
		model.setSaveDialogFilterNames(names);
		model.setSaveDialogTitle("DialogTitle");
		assertEquals("Title", model.getDialogTitle());
		assertEquals(EncodingType.DER, model.getEncoding());
		assertEquals("Filename", model.getFilename());
		assertEquals("Password", model.getPassword());
		assertEquals(PKCS12Cipher.DES3, model.getPkcs12Cipher());
		assertEquals(PKCS8Cipher.AES_128_CBC, model.getPkcs8Cipher());
		assertArrayEquals(ext, model.getSaveDialogFilterExtensions());
		assertArrayEquals(names, model.getSaveDialogFilterNames());
		assertEquals("DialogTitle", model.getSaveDialogTitle());
	}
}
