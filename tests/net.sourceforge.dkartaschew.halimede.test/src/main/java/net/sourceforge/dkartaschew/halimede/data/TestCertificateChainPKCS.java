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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.random.NotSecureRandom;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

/**
 * Tests for storage of PKCS#12 and PKCS#7 for completeness.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestCertificateChainPKCS {

	// Short test only creates a single run instance.
	private final static boolean SHORT = true;
	// Print time to complete each encode/decode operation
	private final static boolean TIME_OUTPUT = false;

	private final String[] PASSWORD = { "changeme", "", null };
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("CN=MyCACert");
	private X500Name subject = new X500Name("CN=MySubjectCert");

	private Path path;
	private CertificateAuthority ca;
	private KeyPair key;

	@BeforeClass
	public static void setupTest() throws NoSuchAlgorithmException {
		ProviderUtil.setupProviders();
		NotSecureRandom rnd = new NotSecureRandom();
		CryptoServicesRegistrar.setSecureRandom(rnd);
		KeyPairFactory.resetSecureRandom(rnd);
	}
	
	@AfterClass
	public static void teardown() {
		CryptoServicesRegistrar.setSecureRandom(null);
		KeyPairFactory.resetSecureRandom(null);
	}

	// @Parameters(name = "{0}")
	public static Collection<KeyType> keyData() {
		KeyTypeWarningValidator v = new KeyTypeWarningValidator();
		
		// Only do for keying material of 2048 bits or less.
		Collection<KeyType> data = Arrays.stream(KeyType.values())//
				.filter(key -> (v.validate(key) == ValidationStatus.ok()))//
				.collect(Collectors.toList());
		return data;
	}

	@Parameters(name = "{0}")
	public static Collection<KeyType> data() {
		if (SHORT) {
			List<KeyType> data = new ArrayList<>();
			data.add(KeyType.RSA_1024);
			return data;
		}
		return data();
	}

	/**
	 * The CA keying material type.
	 */
	private final KeyType caKey;

	/**
	 * Create a new test with the given CA and Subject keying type
	 * 
	 * @param caKey The CA Key
	 */
	public TestCertificateChainPKCS(KeyType caKey) {
		this.caKey = caKey;
	}

	@Before
	public void setup() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
			CertificateException, OperatorCreationException, CertIOException, IOException {
		/*
		 * CA
		 */
		path = Paths.get(TestUtilities.TMP, "CA");
		assertTrue(path.toFile().mkdirs());

		key = KeyPairFactory.generateKeyPair(caKey);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
				true);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD[0]);

		ca = CertificateAuthority.create(path, ic);
		ca.setDescription(CA_DESCRIPTION);
		assertFalse(ca.isLocked());
	}

	@After
	public void tearDown() throws IOException {
		TestUtilities.cleanup(path);
	}

	/**
	 * Test PKCS12
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testPKCS12() throws Throwable {
		// Do a request for each keying material type.
		for (KeyType subKey : keyData()) {

			/*
			 * Request
			 */

			// Include them all!
			KeyUsage subKeyUsage = KeyUsageEnum.asKeyUsage(KeyUsageEnum.values());
			ASN1EncodableVector extKeyUsage = ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values());

			GeneralNames subjAltNames = new GeneralNamesBuilder()//
					.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

			CertificateRequest req = new CertificateRequest();
			req.setcARequest(false);
			req.setSubject(subject);
			req.setKeyType(subKey);
			req.setKeyUsage(subKeyUsage);
			req.setExtendedKeyUsage(extKeyUsage);
			req.setSubjectAlternativeName(subjAltNames);

			long startTime = System.nanoTime();

			Certificate c = ca.signCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
					ZonedDateTime.now().plusSeconds(360));

			long endTime = System.nanoTime();
			if (TIME_OUTPUT)
				System.out.println("Create Cert + Key: " + subKey + " : " + (endTime - startTime));

			assertNotNull(c);

			c.verify(key.getPublic(), BouncyCastleProvider.PROVIDER_NAME);

			assertTrue(c instanceof X509Certificate);
			IssuedCertificate ic = new IssuedCertificate(req.getKeyPair(),
					new X509Certificate[] { (X509Certificate) c, (X509Certificate) ca.getCertificate() }, null, null,
					PASSWORD[0]);

			Path file = Paths.get(TestUtilities.TMP, "cert.p12");

			for (String p : PASSWORD) {
				for (PKCS12Cipher cipher : PKCS12Cipher.values()) {

					startTime = System.nanoTime();
					ic.createPKCS12(file, p, "1", cipher);
					endTime = System.nanoTime();

					if (TIME_OUTPUT)
						System.out.println("Create PKCS#12: " + subKey + " , Password: " + p + " , Cipher: " + cipher
								+ " , Time: " + (endTime - startTime));

					// Reload.

					startTime = System.nanoTime();
					IIssuedCertificate reloaded = IssuedCertificate.openPKCS12(file, p);
					endTime = System.nanoTime();

					if (TIME_OUTPUT)
						System.out.println("Reload PKCS#12: " + subKey + " , Password: " + p + " , Cipher: " + cipher
								+ " , Time: " + (endTime - startTime));

					startTime = System.nanoTime();

					Certificate[] chain = reloaded.getCertificateChain();
					assertEquals(2, chain.length);
					X509Certificate x509c = (X509Certificate) chain[0];

					assertEquals(subject.toString(), x509c.getSubjectDN().getName());
					assertEquals(issuer.toString(), x509c.getIssuerDN().getName());

					// Check key usage, and extended key usage.
					JcaX509CertificateHolder x509ch = new JcaX509CertificateHolder(x509c);
					KeyUsage x509KeyUsage = KeyUsage.fromExtensions(x509ch.getExtensions());
					assertNotNull(x509KeyUsage);
					assertEquals(subKeyUsage, x509KeyUsage);

					ExtendedKeyUsage x509ExtKeyUsage = ExtendedKeyUsage.fromExtensions(x509ch.getExtensions());
					assertNotNull(x509ExtKeyUsage);
					for (int i = 0; i < extKeyUsage.size(); i++) {
						assertTrue(x509ExtKeyUsage.hasKeyPurposeId((KeyPurposeId) extKeyUsage.get(i)));
					}
					assertEquals(x509ExtKeyUsage.size(), extKeyUsage.size());

					// Check SubjectAltNames.
					GeneralNames x509sab = GeneralNames.fromExtensions(x509ch.getExtensions(),
							Extension.subjectAlternativeName);
					assertNotNull(x509sab);
					assertEquals(subjAltNames, x509sab);

					// Check parent.
					x509c = (X509Certificate) chain[1];
					assertEquals(issuer.toString(), x509c.getSubjectDN().getName());
					assertEquals(ca.getCertificate().getPublicKey(), x509c.getPublicKey());

					endTime = System.nanoTime();
					if (TIME_OUTPUT)
						System.out.println("Checks PKCS#12: " + subKey + " , Password: " + p + " , Cipher: " + cipher
								+ " , Time: " + (endTime - startTime));
				}
			}
		}
	}

	/**
	 * Test PKCS7
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testPKCS7() throws Throwable {
		// Do a request for each keying material type.
		for (KeyType subKey : keyData()) {

			/*
			 * Request
			 */

			// Include them all!
			KeyUsage subKeyUsage = KeyUsageEnum.asKeyUsage(KeyUsageEnum.values());
			ASN1EncodableVector extKeyUsage = ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values());

			GeneralNames subjAltNames = new GeneralNamesBuilder()//
					.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

			CertificateRequest req = new CertificateRequest();
			req.setcARequest(false);
			req.setSubject(subject);
			req.setKeyType(subKey);
			req.setKeyUsage(subKeyUsage);
			req.setExtendedKeyUsage(extKeyUsage);
			req.setSubjectAlternativeName(subjAltNames);

			Certificate c = ca.signCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
					ZonedDateTime.now().plusSeconds(360));

			assertNotNull(c);

			c.verify(key.getPublic(), BouncyCastleProvider.PROVIDER_NAME);

			assertTrue(c instanceof X509Certificate);
			IssuedCertificate ic = new IssuedCertificate(req.getKeyPair(),
					new X509Certificate[] { (X509Certificate) c, (X509Certificate) ca.getCertificate() }, null, null,
					PASSWORD[0]);

			Path file = Paths.get(TestUtilities.TMP, "cert.p12");

			for (EncodingType type : EncodingType.values()) {

				ic.createCertificateChain(file, type);

				// Reload.

				IIssuedCertificate reloaded = IssuedCertificate.openPKCS7(file);

				Certificate[] chain = reloaded.getCertificateChain();
				assertEquals(2, chain.length);
				X509Certificate x509c = (X509Certificate) chain[0];

				assertEquals(subject.toString(), x509c.getSubjectDN().getName());
				assertEquals(issuer.toString(), x509c.getIssuerDN().getName());

				// Check key usage, and extended key usage.
				JcaX509CertificateHolder x509ch = new JcaX509CertificateHolder(x509c);
				KeyUsage x509KeyUsage = KeyUsage.fromExtensions(x509ch.getExtensions());
				assertNotNull(x509KeyUsage);
				assertEquals(subKeyUsage, x509KeyUsage);

				ExtendedKeyUsage x509ExtKeyUsage = ExtendedKeyUsage.fromExtensions(x509ch.getExtensions());
				assertNotNull(x509ExtKeyUsage);
				for (int i = 0; i < extKeyUsage.size(); i++) {
					assertTrue(x509ExtKeyUsage.hasKeyPurposeId((KeyPurposeId) extKeyUsage.get(i)));
				}
				assertEquals(x509ExtKeyUsage.size(), extKeyUsage.size());

				// Check SubjectAltNames.
				GeneralNames x509sab = GeneralNames.fromExtensions(x509ch.getExtensions(),
						Extension.subjectAlternativeName);
				assertNotNull(x509sab);
				assertEquals(subjAltNames, x509sab);

				// Check parent.
				x509c = (X509Certificate) chain[1];
				assertEquals(issuer.toString(), x509c.getSubjectDN().getName());
				assertEquals(ca.getCertificate().getPublicKey(), x509c.getPublicKey());
			}

		}
	}
}
