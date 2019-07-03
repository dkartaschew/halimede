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

import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestPKCS12Encoder {

	@Parameters(name = "Cipher={0}")
	public static Collection<PKCS12Cipher> data() {
		return Arrays.asList(PKCS12Cipher.values());
	}

	private final String PASSWORD = "changeme";

	private final String PASSWORD2 = "!changeme";

	private final PKCS12Cipher cipher;

	public TestPKCS12Encoder(PKCS12Cipher cipher) {
		this.cipher = cipher;
	}

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
	}

	/*
	 * Non-encrypted p12 in DER
	 */
	@Test
	public void test_RSA() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, null, fn, null, null);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_RSA_No_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, null, null);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_RSA_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, PASSWORD2, PASSWORD2);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_BadPassword() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, PASSWORD2, PASSWORD);
	}

	/*
	 * Non-encrypted p12 in DER
	 */
	@Test
	public void test_DSA() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, null, fn, null, null);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_DSA_No_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, null, null);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_DSA_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, PASSWORD2, PASSWORD2);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_BadPassword() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, PASSWORD2, PASSWORD);
	}

	/*
	 * Non-encrypted p12 in DER
	 */
	@Test
	public void test_EC() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, null, fn, null, null);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_EC_No_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, null, null);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_EC_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, PASSWORD2, PASSWORD2);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test(expected = InvalidPasswordException.class)
	public void test_EC_BadPassword() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521_2.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.p12");
		testPCKS12(file, PASSWORD, fn, PASSWORD2, PASSWORD);
	}

	private void testPCKS12(Path file, String password, Path fn, String password2, String password3)
			throws IOException, InvalidPasswordException, KeyStoreException {
		// Save
		IIssuedCertificate ic = IssuedCertificate.openPKCS12(file, password);
		ic.createPKCS12(fn, password2, null, cipher);

		// reload
		try {
			IIssuedCertificate ic2 = IssuedCertificate.openPKCS12(fn, password3);
			Certificate[] certificate = ic2.getCertificateChain();
			Key key = ic2.getPrivateKey();
			TestUtilities.displayCertificate(certificate);
			assertNotNull(key);
		} finally {
			TestUtilities.delete(fn);
		}
	}

}
