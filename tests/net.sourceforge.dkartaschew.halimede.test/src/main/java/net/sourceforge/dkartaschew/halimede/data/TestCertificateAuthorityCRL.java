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
import java.security.Security;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.render.CRLRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestCertificateAuthorityCRL {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("CN=MyCACert");
	private X500Name subject = new X500Name("CN=MySubjectCert");
	private X500Name subject2 = new X500Name("CN=MySubjectCert2");

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
		Security.addProvider(new BouncyCastlePQCProvider());
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
	public TestCertificateAuthorityCRL(KeyType caKey) {
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

		/*
		 * Request 1
		 */

		// Include them all!
		KeyUsage subKeyUsage = KeyUsageEnum.asKeyUsage(KeyUsageEnum.values());
		ASN1EncodableVector extKeyUsage = ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values());

		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

		CertificateRequest req = new CertificateRequest();
		req.setcARequest(false);
		req.setCreationDate(ZonedDateTime.now());
		req.setDescription(subject.toString());
		req.setSubject(subject);
		req.setKeyType(caKey);
		req.setKeyUsage(subKeyUsage);
		req.setExtendedKeyUsage(extKeyUsage);
		req.setSubjectAlternativeName(subjAltNames);

		IssuedCertificateProperties c = ca.signAndStoreCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c);

		try {
			c.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),
					BouncyCastleProvider.PROVIDER_NAME);
		} catch (NoSuchAlgorithmException e) {
			c.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),
					BouncyCastlePQCProvider.PROVIDER_NAME);
		}
		
		/*
		 * Request 2
		 */

		// Include them all!
		GeneralNames subjAltNames2 = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user2")).build();

		CertificateRequest req2 = new CertificateRequest();
		req2.setcARequest(false);
		req2.setCreationDate(ZonedDateTime.now());
		req2.setDescription(subject2.toString());
		req2.setSubject(subject2);
		req2.setKeyType(caKey);
		req2.setKeyUsage(subKeyUsage);
		req2.setExtendedKeyUsage(extKeyUsage);
		req2.setSubjectAlternativeName(subjAltNames2);

		IssuedCertificateProperties c2 = ca.signAndStoreCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c2);

		try {
			c.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),
					BouncyCastleProvider.PROVIDER_NAME);
		} catch (NoSuchAlgorithmException e) {
			c.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),
					BouncyCastlePQCProvider.PROVIDER_NAME);
		}

		/*
		 * Ensure we have both certs.
		 */
		Collection<IssuedCertificateProperties> issued = ca.getIssuedCertificates();
		assertEquals(2, issued.size());
		assertTrue(issued.contains(c));
		assertTrue(issued.contains(c2));

		/*
		 * Revoke 1
		 */
		ca.revokeCertificate(c, ZonedDateTime.now(), RevokeReasonCode.AFFILIATION_CHANGED);

		/*
		 * Ensure we have both certs.
		 */
		issued = ca.getIssuedCertificates();
		assertEquals(1, issued.size());
		assertTrue(issued.contains(c2));

		Collection<IssuedCertificateProperties> revoked = ca.getRevokedCertificates();
		assertEquals(1, revoked.size());
		assertTrue(revoked.contains(c));

		CRLProperties crl = ca.createCRL(ZonedDateTime.now().plusSeconds(3600));
		Path crlFilename = Paths.get(TestUtilities.TMP, "crl.crl");
		X509CRLEncoder.create(crlFilename, EncodingType.PEM, crl.getCRL());

		X509CRL pemCRL = X509CRLEncoder.open(crlFilename);
		X509CRL derCRL = X509CRLEncoder
				.open(path.resolve(CertificateAuthority.X509CRL_PATH).resolve(crl.getProperty(Key.crlFilename)));
		assertNotNull(crl);
		assertNotNull(pemCRL);
		assertNotNull(derCRL);
		System.out.println(crl);
		System.out.println(pemCRL);
		System.out.println(derCRL);

		assertEquals(crl.getCRL(), pemCRL);
		assertEquals(crl.getCRL(), derCRL);

		/*
		 * Revoke 2
		 */
		ca.revokeCertificate(c2, ZonedDateTime.now(), RevokeReasonCode.CA_COMPROMISE);
		crl = ca.createCRL(ZonedDateTime.now().plusSeconds(3600));
		X509CRLEncoder.create(crlFilename, EncodingType.PEM, crl.getCRL());
		pemCRL = X509CRLEncoder.open(crlFilename);
		derCRL = X509CRLEncoder
				.open(path.resolve(CertificateAuthority.X509CRL_PATH).resolve(crl.getProperty(Key.crlFilename)));

		assertNotNull(crl);
		assertNotNull(pemCRL);
		assertNotNull(derCRL);
		System.out.println(crl);
		System.out.println(pemCRL);
		System.out.println(derCRL);

		assertEquals(crl.getCRL(), pemCRL);
		assertEquals(crl.getCRL(), derCRL);

		CRLRenderer r = new CRLRenderer(crl);
		r.render(new TextOutputRenderer(System.out));
	}
}
