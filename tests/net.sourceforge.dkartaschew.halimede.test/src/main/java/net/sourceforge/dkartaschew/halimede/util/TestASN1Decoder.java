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

package net.sourceforge.dkartaschew.halimede.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CRLException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestASN1Decoder {

	@Test
	public void test_DSA_SHA1_DER() throws IOException {
		test("dsa4096key_der.csr");
	}

	@Test
	public void test_DSA_SHA256_DER() throws IOException {
		test("dsa4096key_sha256_der.csr");
	}

	@Test
	public void test_DSA_SHA1_PEM() throws IOException {
		test("dsa4096key_pem.csr");
	}

	@Test
	public void test_DSA_SHA256_PEM() throws IOException {
		test("dsa4096key_sha256_pem.csr");
	}

	@Test
	public void test_PKCS12() throws IOException {
		test("rsa4096key.p12");
	}

	@Test
	public void test_PKCS12Encrypted() throws IOException {
		test("rsa4096.p12");
	}

	@Test
	public void testX509_DER_RSA() throws IOException {
		test("rsacert.cer");
	}

	@Test
	public void testX509_PEM_RSA() throws IOException {
		test("rsacert.pem");
	}

	@Test
	public void test_RSA_DER() throws IOException {
		test("rsa4096key.der");
	}

	@Test
	public void test_RSA_PEM() throws IOException {
		test("rsa4096key.pem");
	}

	@Test
	public void test_RSA_P8_DER() throws IOException {
		test("rsa4096key_der.p8");
	}

	@Test
	public void test_RSA_P8_PEM() throws IOException {
		test("rsa4096key_pem.p8");
	}

	@Test
	public void test_RSA_P8_AES_DER() throws IOException {
		test("rsa4096key_aes_der.p8");
	}

	@Test
	public void test_RSA_P8_AES_PEM() throws IOException {
		test("rsa4096key_aes_pem.p8");
	}

	@Test
	public void test_RSA_P8_DES_DER() throws IOException {
		test("rsa4096key_des_der.p8");
	}

	@Test
	public void test_RSA_P8_DES_PEM() throws IOException {
		test("rsa4096key_des_pem.p8");
	}
	
	@Test
	public void ICA_DER() throws IOException, CRLException {
		test("CRLs" + File.separator +"ica.sz.5388.der.crl");
	}

	@Test
	public void ICA_PEM() throws IOException, CRLException {
		test("CRLs" + File.separator +"ica.sz.5388.pem.crl");
	}

	@Test
	public void Telstra() throws IOException, CRLException {
		test("CRLs" + File.separator +"Telstra RSS Issuing CA1.crl");
	}

	@Test(expected = NullPointerException.class)
	public void testNullFilename() throws IOException {
		ASN1Decoder.create(null);
	}
	
	@Test(expected = IOException.class)
	public void testInvalidFilename() throws IOException {
		Path file = TestUtilities.getFile("rsa4096.p12").getParent();
		ASN1Decoder.create(file);
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testMissingFilename() throws IOException {
		ASN1Decoder.create(Paths.get("ras4096.p12"));
	}

	private void test(String filename) throws IOException {
		Path file = TestUtilities.getFile(filename);
		ASN1Decoder d = ASN1Decoder.create(file);
		String decode = d.decode();
		assertNotNull(decode);
		assertFalse(decode.isEmpty());
		assertFalse(decode, decode.contains("unknown object"));
		System.out.println(decode);
		d.render(new TextOutputRenderer(System.out));
	}
}
