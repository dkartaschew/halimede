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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestIssuedCertificate {

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
	}

	@Test(expected = NullPointerException.class)
	public void testIssuedCertificateObjectNULL() {
		new IssuedCertificate(null, null, null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIssuedCertificateObjectNoCertificates() {
		new IssuedCertificate(null, new Certificate[0], null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testIssuedCertificateObjectEmptyCertificates() {
		new IssuedCertificate(null, new Certificate[1], null, null, null);
	}

	@Test
	public void testIssuedCertificate() {
		IssuedCertificate ic = new IssuedCertificate(null, new Certificate[] { new MockCertificate("Mock") }, null, null, null);
		assertEquals("Certificate: Mock", ic.toString());
	}

	@Test(expected = NullPointerException.class)
	public void testKeyPair() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		PKCS7Decoder decoder = PKCS7Decoder.open(file);
		Certificate[] chain = decoder.getCertificateChain();
		new IssuedCertificate(new KeyPair(null, null), chain, null, null, null);
	}

	@Test
	public void testObject() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertTrue(ic2.equals(ic2));
		assertFalse(ic2.equals(null));
		assertFalse(ic2.equals(new Object()));
		assertFalse(ic2.toString().contains("null"));
		assertEquals(ic2.hashCode(), ic2.hashCode());
	}

	@Test
	public void testCertificateOnly() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2);
		assertEquals(file, ic2.getCertFilename());
		assertNotNull(ic2.getCertificateChain());
		assertNull(ic2.getKeyFilename());
		assertNull(ic2.getPassword());
		assertNull(ic2.getPrivateKey());
		assertNotNull(ic2.getPublicKey());

		Certificate[] chain = ic2.getCertificateChain();
		assertEquals(1, chain.length);
		assertTrue(chain[0] instanceof X509Certificate);
		X509Certificate cert = (X509Certificate) chain[0];

		PublicKey pub = ic2.getPublicKey();
		assertEquals("RSA", pub.getAlgorithm());
		assertEquals(pub, cert.getPublicKey());
	}

	@Test
	public void testPrivateAndCertOnly() throws IOException, InvalidKeyException, IllegalArgumentException,
			NoSuchAlgorithmException, SignatureException, InvalidPasswordException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		Path key = TestUtilities.getFile("rsa4096key.pem");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7_8(file, key, null);
		assertNotNull(ic2);
		assertEquals(file, ic2.getCertFilename());
		assertNotNull(ic2.getCertificateChain());
		assertEquals(key, ic2.getKeyFilename());
		assertNull(ic2.getPassword());
		assertNotNull(ic2.getPrivateKey());
		assertNotNull(ic2.getPublicKey());

		Certificate[] chain = ic2.getCertificateChain();
		assertEquals(1, chain.length);
		assertTrue(chain[0] instanceof X509Certificate);
		X509Certificate cert = (X509Certificate) chain[0];

		PublicKey pub = ic2.getPublicKey();
		assertEquals("RSA", pub.getAlgorithm());
		assertEquals(pub, cert.getPublicKey());
	}

	@Test
	public void testPrivateAndCertOnlyPassword() throws IOException, InvalidKeyException, IllegalArgumentException,
			NoSuchAlgorithmException, SignatureException, InvalidPasswordException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		Path key = TestUtilities.getFile("rsa4096key_aes256.pem");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7_8(file, key, "changeme");
		assertNotNull(ic2);
		assertEquals(file, ic2.getCertFilename());
		assertNotNull(ic2.getCertificateChain());
		assertEquals(key, ic2.getKeyFilename());
		assertEquals("changeme", ic2.getPassword());
		assertNotNull(ic2.getPrivateKey());
		assertNotNull(ic2.getPublicKey());

		Certificate[] chain = ic2.getCertificateChain();
		assertEquals(1, chain.length);
		assertTrue(chain[0] instanceof X509Certificate);
		X509Certificate cert = (X509Certificate) chain[0];

		PublicKey pub = ic2.getPublicKey();
		assertEquals("RSA", pub.getAlgorithm());
		assertEquals(pub, cert.getPublicKey());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPrivateAndCertMismatch() throws IOException, InvalidKeyException, IllegalArgumentException,
			NoSuchAlgorithmException, SignatureException, InvalidPasswordException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		Path key = TestUtilities.getFile("ec521key_aes256.pem");
		IssuedCertificate.openPKCS7_8(file, key, "changeme");
	}

	@Test
	public void testPKCS12() throws Throwable {
		Path file = TestUtilities.getFile("rsa_email.p12");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS12(file, null);
		assertNotNull(ic2);
		assertEquals(file, ic2.getCertFilename());
		assertNotNull(ic2.getCertificateChain());
		assertEquals(null, ic2.getKeyFilename());
		assertEquals(null, ic2.getPassword());
		assertNotNull(ic2.getPrivateKey());
		assertNotNull(ic2.getPublicKey());

		Certificate[] chain = ic2.getCertificateChain();
		assertEquals(1, chain.length);
		assertTrue(chain[0] instanceof X509Certificate);
		X509Certificate cert = (X509Certificate) chain[0];

		PublicKey pub = ic2.getPublicKey();
		assertEquals("RSA", pub.getAlgorithm());
		assertEquals(pub, cert.getPublicKey());
	}

	@Test
	public void testPublicKeyExport() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		Path tmp = Paths.get(TestUtilities.TMP, "tmp_pem.pub");
		Path tmp2 = Paths.get(TestUtilities.TMP, "tmp_der.pub");

		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2.getPublicKey());

		// store and reload
		try {
			ic2.createPublicKey(tmp, EncodingType.PEM);
			ic2.createPublicKey(tmp2, EncodingType.DER);

			PublicKey pubKeyPem = PublicKeyDecoder.open(tmp);
			PublicKey pubKeyDer = PublicKeyDecoder.open(tmp2);

			assertEquals("RSA", pubKeyPem.getAlgorithm());
			assertEquals("RSA", pubKeyDer.getAlgorithm());

			assertEquals(ic2.getPublicKey(), pubKeyPem);
			assertEquals(ic2.getPublicKey(), pubKeyDer);

		} finally {
			TestUtilities.delete(tmp);
			TestUtilities.delete(tmp2);
		}
	}

	@Test(expected = NullPointerException.class)
	public void testPrivateKeyExportNullCipher() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNull(ic2.getPrivateKey());

		Path tmp = Paths.get(TestUtilities.TMP, "tmp_pem.pub");
		ic2.createPKCS8(tmp, null, EncodingType.DER, null);
	}

	@Test(expected = NullPointerException.class)
	public void testPrivateKeyExportNullEncoding() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNull(ic2.getPrivateKey());

		Path tmp = Paths.get(TestUtilities.TMP, "tmp_pem.pub");
		ic2.createPKCS8(tmp, null, null, PKCS8Cipher.DES3_CBC);
	}

	@Test(expected = IllegalStateException.class)
	public void testPrivateKeyExportNoKey() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNull(ic2.getPrivateKey());

		Path tmp = Paths.get(TestUtilities.TMP, "tmp_pem.pub");
		ic2.createPKCS8(tmp, null, EncodingType.DER, PKCS8Cipher.DES3_CBC);
	}

	@Test
	public void testPrivateKeyExport() throws Throwable {
		Path file = TestUtilities.getFile("rsa_email.p12");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS12(file, null);
		assertNotNull(ic2);
		Path tmp = Paths.get(TestUtilities.TMP, "tmp_pem.p8");

		try {
			String[] passwords = { null, "pass" };
			for (EncodingType type : EncodingType.values()) {
				for (PKCS8Cipher cipher : PKCS8Cipher.values()) {
					for (String p : passwords) {
						ic2.createPKCS8(tmp, p, type, cipher);
						PKCS8Decoder keys = PKCS8Decoder.open(tmp, p);
						assertEquals(ic2.getPrivateKey(), keys.getKeyPair().getPrivate());
					}

				}

			}
		} finally {
			TestUtilities.delete(tmp);
		}
	}

	@Test(expected = NullPointerException.class)
	public void testExportCertificateNullEncoding() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2);
		Path tmp = Paths.get(TestUtilities.TMP, "tmp.cer");
		ic2.createCertificate(tmp, null);
	}

	@Test(expected = NullPointerException.class)
	public void testExportCertificateNullPath() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2);
		ic2.createCertificate(null, EncodingType.DER);
	}

	@Test
	public void testExportCertificate() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2);
		Path tmp = Paths.get(TestUtilities.TMP, "tmp.cer");
		try {
			for (EncodingType type : EncodingType.values()) {
				ic2.createCertificate(tmp, type);
				PKCS7Decoder certs = PKCS7Decoder.open(tmp);
				assertNotNull(certs.getCertificateChain());
				assertEquals(1, certs.getCertificateChain().length);
				assertEquals(ic2.getCertificateChain()[0], certs.getCertificateChain()[0]);
			}
		} finally {
			TestUtilities.delete(tmp);
		}
	}

	@Test(expected = NullPointerException.class)
	public void testExportCertificateChainNullEncoding() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2);
		Path tmp = Paths.get(TestUtilities.TMP, "tmp.cer");
		ic2.createCertificateChain(tmp, null);
	}

	@Test(expected = NullPointerException.class)
	public void testExportCertificateChainNullPath() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2);
		ic2.createCertificateChain(null, EncodingType.DER);
	}

	@Test
	public void testExportCertificateChain() throws IOException {
		Path file = TestUtilities.getFile("rsa_email.cer");
		IIssuedCertificate ic2 = IssuedCertificate.openPKCS7(file);
		assertNotNull(ic2);
		Path tmp = Paths.get(TestUtilities.TMP, "tmp.cer");
		try {
			for (EncodingType type : EncodingType.values()) {
				ic2.createCertificateChain(tmp, type);
				Certificate[] chain = ic2.getCertificateChain();

				PKCS7Decoder certs = PKCS7Decoder.open(tmp);
				assertNotNull(certs.getCertificateChain());
				assertEquals(chain.length, certs.getCertificateChain().length);
				assertArrayEquals(chain, certs.getCertificateChain());
			}
		} finally {
			TestUtilities.delete(tmp);
		}
	}

	@Test
	public void testExportPKCS12() throws Throwable {
		Path file = TestUtilities.getFile("rsa_email.p12");
		IIssuedCertificate ic = IssuedCertificate.openPKCS12(file, null);
		assertNotNull(ic);
		Path tmp = Paths.get(TestUtilities.TMP, "tmp.p12");
		try {
			String[] passwords = { null, "pass" };
			String[] alias = { null, "", "1", "alias" };
			for (String p : passwords) {
				ic.createPKCS12(tmp, p);
				IIssuedCertificate ic2 = IssuedCertificate.openPKCS12(tmp, p);
				assertArrayEquals(ic.getCertificateChain(), ic2.getCertificateChain());
				assertEquals(ic.getPrivateKey(), ic2.getPrivateKey());
				assertEquals(ic.getPublicKey(), ic2.getPublicKey());
			}
			for (String p : passwords) {
				for (String a : alias) {
					ic.createPKCS12(tmp, p, a);
					IIssuedCertificate ic2 = IssuedCertificate.openPKCS12(tmp, p);
					assertArrayEquals(ic.getCertificateChain(), ic2.getCertificateChain());
					assertEquals(ic.getPrivateKey(), ic2.getPrivateKey());
					assertEquals(ic.getPublicKey(), ic2.getPublicKey());
				}
			}

		} finally {
			TestUtilities.delete(tmp);
		}
	}
	
	@Test(expected=NullPointerException.class)
	public void testOpenPKCS12NullFilename() throws Throwable {
		IssuedCertificate.openPKCS12(null, null);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testOpenPKCS12UnknownFilename() throws Throwable {
		IssuedCertificate.openPKCS12(Paths.get("/File"), null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testOpenPKCS7NullFilename() throws Throwable {
		IssuedCertificate.openPKCS7(null);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testOpenPKCS7UnknownFilename() throws Throwable {
		IssuedCertificate.openPKCS7(Paths.get("/File"));
	}
	
	@Test(expected=NullPointerException.class)
	public void testOpenPKCS78NullFilename() throws Throwable {
		IssuedCertificate.openPKCS7_8(null, null, null);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testOpenPKCS78UnknownFilename() throws Throwable {
		IssuedCertificate.openPKCS7_8(Paths.get("/File"), Paths.get("/File"), null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testExportPKCS12NullFilename() throws Throwable {
		Path file = TestUtilities.getFile("rsa_email.p12");
		IIssuedCertificate ic = IssuedCertificate.openPKCS12(file, null);
		assertNotNull(ic);
		ic.createPKCS12(null, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testExportPKCS12NullFilename2() throws Throwable {
		Path file = TestUtilities.getFile("rsa_email.p12");
		IIssuedCertificate ic = IssuedCertificate.openPKCS12(file, null);
		assertNotNull(ic);
		ic.createPKCS12(null, null, null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testExportPKCS12NullFilename3() throws Throwable {
		Path file = TestUtilities.getFile("rsa_email.p12");
		IIssuedCertificate ic = IssuedCertificate.openPKCS12(file, null);
		assertNotNull(ic);
		ic.createPKCS12(null, null, null, null);
	}
}
