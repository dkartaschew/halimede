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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
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
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.AfterClass;
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
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.random.NotSecureRandom;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestCertificateAuthoritySigning {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("CN=MyCACert");
	private X500Name subject = new X500Name("CN=MySubjectCert");

	@BeforeClass
	public static void setup() throws NoSuchAlgorithmException {
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

	@Parameters(name = "{0}")
	public static Collection<KeyType> data() {
		KeyTypeWarningValidator v = new KeyTypeWarningValidator();
		
		// Only do for keying material of 2048 bits or less.
		Collection<KeyType> data = Arrays.stream(KeyType.values())//
				.filter(key -> (v.validate(key) == ValidationStatus.ok()))//
				.collect(Collectors.toList());
		return data;
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
	public TestCertificateAuthoritySigning(KeyType caKey) {
		this.caKey = caKey;
	}

	/**
	 * Test basic signing routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSigning() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		assertTrue(path.toFile().mkdirs());
		try {
			testCertifcateGeneration(path, caKey);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test with the given combination
	 * 
	 * @param path The path to the CA
	 * @param caKey The CA Keying material type
	 * @param subKey The Subject Keying material type.
	 * @throws Throwable The creation failed.
	 */
	private void testCertifcateGeneration(Path path, KeyType caKey) throws Throwable {
		/*
		 * CA
		 */

		KeyPair key = KeyPairFactory.generateKeyPair(caKey);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
				true);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);

		CertificateAuthority ca = CertificateAuthority.create(path, ic);
		ca.setDescription(CA_DESCRIPTION);
		assertFalse(ca.isLocked());

		// Do a request for each keying material type.
		for (KeyType subKey : data()) {

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
			try {
				c.verify(key.getPublic(), BouncyCastleProvider.PROVIDER_NAME);
			} catch (NoSuchAlgorithmException e) {
				c.verify(key.getPublic(), BouncyCastlePQCProvider.PROVIDER_NAME);
			}

			TestUtilities.displayCertificate(new Certificate[] { c });

			assertTrue(c instanceof X509Certificate);
			X509Certificate x509c = (X509Certificate) c;
			assertEquals(subject.toString(), x509c.getSubjectDN().getName());
			assertEquals(issuer.toString(), x509c.getIssuerDN().getName());
			assertEquals(ca.getNextSerialNumber().longValue() - 1, x509c.getSerialNumber().longValue());

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
		}
	}
}
