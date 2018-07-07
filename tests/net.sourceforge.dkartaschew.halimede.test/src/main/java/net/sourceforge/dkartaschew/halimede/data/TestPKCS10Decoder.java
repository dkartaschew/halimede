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

package net.sourceforge.dkartaschew.halimede.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;
import java.util.Iterator;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DLSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.render.CSRRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.HTMLOutputRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPKCS10Decoder {

	private final X500Name subj = new X500Name(
			"C=AU,ST=Queensland,L=Gold Coast,O=Internet Widgits Pty Ltd,CN=CA Manager");

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Test
	public void test_DSA_SHA1_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_der.csr");
	}

	@Test
	public void test_DSA_SHA256_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_sha256_der.csr");
	}

	@Test
	public void test_DSA_SHA1_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_pem.csr");
	}

	@Test
	public void test_DSA_SHA256_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_sha256_pem.csr");
	}

	@Test
	public void test_RSA_SHA1_DER() throws IOException, InvalidPasswordException {
		test("rsa4096_der.csr");
	}

	@Test
	public void test_RSA_SHA512_DER() throws IOException, InvalidPasswordException {
		test("rsa4096_sha512_der.csr");
	}

	@Test
	public void test_RSA_SHA1_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096_pem.csr");
	}

	@Test
	public void test_RSA_SHA512_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096_sha512_pem.csr");
	}

	@Test
	public void test_EC_SHA1_DER() throws IOException, InvalidPasswordException {
		test("ec521key_der.csr");
	}

	@Test
	public void test_EC_SHA256_DER() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_der.csr");
	}

	@Test
	public void test_EC_SHA1_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_pem.csr");
	}

	@Test
	public void test_EC_SHA256_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_pem.csr");
	}

	@Test
	public void test_EC_SHA1_DER_CA() throws IOException, InvalidPasswordException {
		test("ec521key_der_ca.csr");
	}

	@Test
	public void test_EC_SHA256_DER_CA() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_der_ca.csr");
	}

	@Test
	public void test_EC_SHA1_PEM_CA() throws IOException, InvalidPasswordException {
		test("ec521key_pem_ca.csr");
	}

	@Test
	public void test_EC_SHA256_PEM_CA() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_pem_ca.csr");
	}

	@Test
	public void test_EC_SHA1_DER_KEY() throws IOException, InvalidPasswordException {
		test("ec521key_der_key.csr");
	}

	@Test
	public void test_EC_SHA256_DER_KEY() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_der_key.csr");
	}

	@Test
	public void test_EC_SHA1_PEM_KEY() throws IOException, InvalidPasswordException {
		test("ec521key_pem_key.csr");
	}

	@Test
	public void test_EC_SHA256_PEM_KEY() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_pem_key.csr");
	}

	@Test
	public void test_EC_SHA1_DER_CA_Details() throws IOException, InvalidPasswordException {
		test("ec521key_der_ca.csr", true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
	}

	@Test
	public void test_EC_SHA256_DER_CA_Details() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_der_ca.csr", true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
	}

	@Test
	public void test_EC_SHA1_PEM_CA_Details() throws IOException, InvalidPasswordException {
		test("ec521key_pem_ca.csr", true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
	}

	@Test
	public void test_EC_SHA256_PEM_CA_Details() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_pem_ca.csr", true, new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign));
	}

	@Test
	public void test_EC_SHA1_DER_KEY_Details() throws IOException, InvalidPasswordException {
		test("ec521key_der_key.csr", false, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.digitalSignature
				| KeyUsage.keyEncipherment | KeyUsage.dataEncipherment));
	}

	@Test
	public void test_EC_SHA256_DER_KEY_Details() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_der_key.csr", false, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.digitalSignature
				| KeyUsage.keyEncipherment | KeyUsage.dataEncipherment));
	}

	@Test
	public void test_EC_SHA1_PEM_KEY_Details() throws IOException, InvalidPasswordException {
		test("ec521key_pem_key.csr", false, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.digitalSignature
				| KeyUsage.keyEncipherment | KeyUsage.dataEncipherment));
	}

	@Test
	public void test_EC_SHA256_PEM_KEY_Details() throws IOException, InvalidPasswordException {
		test("ec521key_sha512_pem_key.csr", false, new KeyUsage(KeyUsage.nonRepudiation | KeyUsage.digitalSignature
				| KeyUsage.keyEncipherment | KeyUsage.dataEncipherment));
	}

	private void test(String filename) throws IOException, InvalidPasswordException {
		Path file = TestUtilities.getFile(filename);
		ICertificateRequest req = PKCS10Decoder.open(file);
		assertNotNull(req);
		System.out.println(req.getSubject());
		// System.out.println(req.isCARequest());
		// System.out.println(req.getKeyUsage());
		// System.out.println(req.getSubjectPublicKeyInfo());

		// CSR
		CSRRenderer csrr = new CSRRenderer(new CertificateRequestProperties(null, req));
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
		HTMLOutputRenderer html = new HTMLOutputRenderer(System.out, "");
		csrr.render(html);
		html.finaliseRender();
	}

	private void test(String filename, boolean isCA, KeyUsage usage) throws IOException, InvalidPasswordException {
		Path file = TestUtilities.getFile(filename);
		ICertificateRequest req = PKCS10Decoder.open(file);
		assertNotNull(req);
		assertEquals(subj, req.getSubject());
		assertEquals(isCA, req.isCARequest());
		assertEquals(usage, req.getKeyUsage());
		DLSequence seq = (DLSequence) req.getSubjectAlternativeName();
		Iterator<ASN1Encodable> it = seq.iterator();
		while (it.hasNext()) {
			ASN1Encodable val = it.next();
			ASN1TaggedObject obj = DERTaggedObject.getInstance(val);
			GeneralName name = GeneralName.getInstance(obj);
			System.out.println(name);
		}

		CSRRenderer csrr = new CSRRenderer(new CertificateRequestProperties(null, req));
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
		HTMLOutputRenderer html = new HTMLOutputRenderer(System.out, "");
		csrr.render(html);
		html.finaliseRender();
	}

}
