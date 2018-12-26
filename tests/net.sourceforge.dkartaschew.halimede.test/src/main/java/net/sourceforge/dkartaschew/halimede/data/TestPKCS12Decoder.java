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
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.Security;
import java.security.cert.Certificate;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPKCS12Decoder {

	private final String PASSWORD = "changeme";

	private final String BADPASSWORD = "!changeme";

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
		Security.addProvider(new BouncyCastlePQCProvider());
	}
	
	/*
	 * Non-encrypted p12 in DER
	 */
	@Test
	public void test_RSA() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096key.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, null);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER
	 */
	@Test
	public void test_RSA_Alias() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_RSA_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (AES cipher for private key)
	 */
	@Test
	public void test_RSA_Alias_AES() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096_aes.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (no alias defined). (AES cipher for private key)
	 */
	@Test
	public void test_RSA_Password_AES() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096_aes_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}
	
	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_BadPassword() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, BADPASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Non-encrypted p12 in DER
	 */
	@Test
	public void test_DSA() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096key.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, null);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER
	 */
	@Test
	public void test_DSA_Alias() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_DSA_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}
	
	/*
	 * Encrypted p12 in DER  (AES cipher for private key)
	 */
	@Test
	public void test_DSA_Alias_AES() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096_aes.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).  (AES cipher for private key)
	 */
	@Test
	public void test_DSA_Password_AES() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096_aes_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}


	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_BadPassword() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, BADPASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Non-encrypted p12 in DER
	 */
	@Test
	public void test_EC() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521key.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, null);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER
	 */
	@Test
	public void test_EC_Alias() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_EC_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (AES cipher for private key)
	 */
	@Test
	public void test_EC_Alias_AES() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521_aes.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}

	/*
	 * Encrypted p12 in DER (no alias defined). (AES cipher for private key)
	 */
	@Test
	public void test_EC_Password_AES() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521_aes_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, PASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}
	
	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test(expected = InvalidPasswordException.class)
	public void test_EC_BadPassword() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521_2.p12");
		PKCS12Decoder decoder = PKCS12Decoder.open(file, BADPASSWORD);
		Certificate[] certificate = decoder.getCertificateChain();
		KeyPair keys = decoder.getKeyPair();
		TestUtilities.displayCertificate(certificate);
		TestUtilities.displayKeys(keys);
	}
}
