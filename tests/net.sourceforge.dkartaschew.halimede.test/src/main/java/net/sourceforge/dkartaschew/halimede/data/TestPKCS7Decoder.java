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

package net.sourceforge.dkartaschew.halimede.data;

import java.io.IOException;
import java.nio.file.Path;
import java.security.cert.Certificate;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPKCS7Decoder {

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
	}

	/*
	 * Raw X509 in DER
	 */
	@Test
	public void testX509_DER_RSA() throws IOException {
		Path file = TestUtilities.getFile("rsacert.cer");
		testPKCS7(file);
	}

	/*
	 * Raw X509 in PEM
	 */
	@Test
	public void testX509_PEM_RSA() throws IOException {
		Path file = TestUtilities.getFile("rsacert.pem");
		testPKCS7(file);
	}

	/*
	 * X509 wrapped in PKCS7
	 */
	@Test
	public void testX509_P7B_RSA() throws IOException {
		Path file = TestUtilities.getFile("rsacert.p7b");
		testPKCS7(file);
	}

	@Test
	public void testX509_P7B_RSA_DER() throws IOException {
		Path file = TestUtilities.getFile("rsacert_der.p7b");
		testPKCS7(file);
	}

	/*
	 * Raw X509 in DER
	 */
	@Test
	public void testX509_DER_DSA() throws IOException {
		Path file = TestUtilities.getFile("dsacert.cer");
		testPKCS7(file);
	}

	/*
	 * Raw X509 in PEM
	 */
	@Test
	public void testX509_PEM_DSA() throws IOException {
		Path file = TestUtilities.getFile("dsacert.pem");
		testPKCS7(file);
	}

	/*
	 * X509 wrapped in PKCS7
	 */
	@Test
	public void testX509_P7B_DSA() throws IOException {
		Path file = TestUtilities.getFile("dsacert.p7b");
		testPKCS7(file);
	}

	/*
	 * X509 wrapped in PKCS7
	 */
	@Test
	public void testX509_P7B_DSA_DER() throws IOException {
		Path file = TestUtilities.getFile("dsacert_der.p7b");
		testPKCS7(file);
	}

	/*
	 * Raw X509 in DER
	 */
	@Test
	public void testX509_DER_EC() throws IOException {
		Path file = TestUtilities.getFile("eccert.cer");
		testPKCS7(file);
	}

	/*
	 * Raw X509 in PEM
	 */
	@Test
	public void testX509_PEM_EC() throws IOException {
		Path file = TestUtilities.getFile("eccert.pem");
		testPKCS7(file);
	}

	/*
	 * X509 wrapped in PKCS7
	 */
	@Test
	public void testX509_P7B_EC() throws IOException {
		Path file = TestUtilities.getFile("eccert.p7b");
		testPKCS7(file);
	}

	@Test
	public void testX509_P7B_EC_DER() throws IOException {
		Path file = TestUtilities.getFile("eccert_der.p7b");
		testPKCS7(file);
	}

	private void testPKCS7(Path file) throws IOException {
		PKCS7Decoder decoder = PKCS7Decoder.open(file);
		Certificate[] certificate = decoder.getCertificateChain();
		TestUtilities.displayCertificate(certificate);
	}

}
