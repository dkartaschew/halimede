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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestSelfSignedCertificates {

	/*-
	 * Signature algorithms:
	 * http://www.bouncycastle.org/wiki/display/JA1/X.509+Public+Key+Certificate+and+Certification+Request+Generation
	 */
	@Parameters(name = "{0} {1}")
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<>();
		for (KeyType key : KeyType.values()) {
			// Only do for keying material of 2048 bits or less.
			if (key.getBitLength() <= TestUtilities.TEST_MAX_KEY_LENGTH) {
				for (SignatureAlgorithm alg : SignatureAlgorithm.forType(key)) {
					data.add(new Object[] { key, alg });
				}
			}
		}
		return data;
	}

	private final Path fn = Paths.get(TestUtilities.TMP, "cert.cer");
	private final KeyType type;
	private final SignatureAlgorithm signatureAlg;
	private final X500Name issuer = new X500Name("CN=MyCert");
	private final GeneralName CRLLocation = GeneralNameTag.uniformResourceIdentifier
			.asGeneralName("http://www.host.local/cgi?crl");

	public TestSelfSignedCertificates(KeyType type, SignatureAlgorithm signatureAlgorithm) {
		this.type = type;
		this.signatureAlg = signatureAlgorithm;
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testKeyGenerateDER() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.DER);
		reloadAndCompare(fn, ic);
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testKeyGeneratePEM() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.PEM);
		reloadAndCompare(fn, ic);
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testCAKeyGenerateDER() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, true);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.DER);
		reloadAndCompare(fn, ic);
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testCAKeyGeneratePEM() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, true);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.PEM);
		reloadAndCompare(fn, ic);
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testCACRLKeyGenerateDER() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, ZonedDateTime.now(), //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, true, CRLLocation);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.DER);
		reloadAndCompare(fn, ic);
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testCACRLKeyGeneratePEM() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, ZonedDateTime.now(), //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, true, CRLLocation);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.PEM);
		reloadAndCompare(fn, ic);
	}

	/**
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testSelfSignGPKeyGenerateDER() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

		CertificateRequest req = new CertificateRequest();
		req.setKeyPair(key);
		req.setSubject(issuer);
		req.setcARequest(false);
		req.setKeyUsage(KeyUsageEnum.nonRepudiation.asKeyUsage());
		req.setExtendedKeyUsage(ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values()));
		req.setSubjectAlternativeName(subjAltNames);

		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, ZonedDateTime.now(), //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, req);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.DER);
		reloadAndCompare(fn, ic);
	}

	/**
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testSelfSignGPKeyGeneratePEM() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

		CertificateRequest req = new CertificateRequest();
		req.setKeyPair(key);
		req.setSubject(issuer);
		req.setcARequest(false);
		req.setKeyUsage(KeyUsageEnum.nonRepudiation.asKeyUsage());
		req.setExtendedKeyUsage(ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values()));
		req.setSubjectAlternativeName(subjAltNames);

		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, ZonedDateTime.now(), //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, req);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.PEM);
		reloadAndCompare(fn, ic);
	}

	/**
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testSelfSignCAKeyGenerateDER() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

		CertificateRequest req = new CertificateRequest();
		req.setKeyPair(key);
		req.setSubject(issuer);
		req.setcARequest(true);
		req.setKeyUsage(KeyUsageEnum.keyCertSign.asKeyUsage());
		req.setExtendedKeyUsage(ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values()));
		req.setSubjectAlternativeName(subjAltNames);

		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, ZonedDateTime.now(), //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, req);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.DER);
		reloadAndCompare(fn, ic);
	}

	/**
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testSelfSignCAKeyGeneratePEM() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

		CertificateRequest req = new CertificateRequest();
		req.setKeyPair(key);
		req.setSubject(issuer);
		req.setcARequest(true);
		req.setKeyUsage(KeyUsageEnum.keyCertSign.asKeyUsage());
		req.setExtendedKeyUsage(ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values()));
		req.setSubjectAlternativeName(subjAltNames);

		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, ZonedDateTime.now(), //
				ZonedDateTime.now().plusSeconds(3600), key, signatureAlg, req);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, null);
		ic.createCertificate(fn, EncodingType.PEM);
		reloadAndCompare(fn, ic);
	}

	/**
	 * Save and reload the keying material
	 * 
	 * @param key The key to save/restore
	 * @param fn The filename to use
	 * @param ic The IssuedCertificate Instance.
	 * @throws InvalidPasswordException Bad Password
	 * @throws IOException Unable to save/restore.
	 */
	private void reloadAndCompare(Path fn, IssuedCertificate ic) throws Throwable {
		// reload
		try {
			PKCS7Decoder pkcs7 = PKCS7Decoder.open(fn);
			X509Certificate cert = (X509Certificate) pkcs7.getCertificateChain()[0];
			// System.out.println(cert);
			assertEquals(ic.getCertificateChain()[0], cert);
			assertEquals(ic.getPublicKey(), cert.getPublicKey());
			cert.verify(ic.getPublicKey());
			cert.verify(cert.getPublicKey());
		} finally {
			TestUtilities.delete(fn);
		}
	}

}
