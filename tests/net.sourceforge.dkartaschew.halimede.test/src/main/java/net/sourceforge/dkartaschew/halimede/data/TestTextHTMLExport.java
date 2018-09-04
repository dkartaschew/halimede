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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Collection;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.render.CRLRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.CSRRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.CertificateRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.HTMLOutputRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTextHTMLExport {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("CN=MyCACert");
	private X500Name subject = new X500Name("CN=MySubjectCert");
	private X500Name subject2 = new X500Name("CN=MySubjectCert2");

	private final String csrFilename = "ec_email.csr";

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
		Security.addProvider(new BouncyCastlePQCProvider());
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
			testCertifcateGeneration(path);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test with the given combination
	 * 
	 * @param path The path to the CA
	 * @throws Throwable The creation failed.
	 */
	private void testCertifcateGeneration(Path path) throws Throwable {
		/*
		 * CA
		 */

		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.EC_sect571r1);
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
		req.setKeyType(KeyType.DSA_512);
		req.setKeyUsage(subKeyUsage);
		req.setExtendedKeyUsage(extKeyUsage);
		req.setSubjectAlternativeName(subjAltNames);

		IssuedCertificateProperties c1 = ca.signAndStoreCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c1);

		c1.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),
				BouncyCastleProvider.PROVIDER_NAME);

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
		req2.setKeyType(KeyType.GOST_3410_2012_512_A);
		req2.setKeyUsage(subKeyUsage);
		req2.setExtendedKeyUsage(extKeyUsage);
		req2.setSubjectAlternativeName(subjAltNames2);

		IssuedCertificateProperties c2 = ca.signAndStoreCertificateRequest(req2, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c2);

		c2.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),
				BouncyCastleProvider.PROVIDER_NAME);

		/*
		 * Ensure we have both certs.
		 */
		Collection<IssuedCertificateProperties> issued = ca.getIssuedCertificates();
		assertEquals(2, issued.size());
		assertTrue(issued.contains(c1));
		assertTrue(issued.contains(c2));

		/*
		 * Revoke 1
		 */
		ca.revokeCertificate(c1, ZonedDateTime.now(), RevokeReasonCode.AFFILIATION_CHANGED);

		/*
		 * Ensure we have both certs.
		 */
		issued = ca.getIssuedCertificates();
		assertEquals(1, issued.size());
		assertTrue(issued.contains(c2));

		Collection<IssuedCertificateProperties> revoked = ca.getRevokedCertificates();
		assertEquals(1, revoked.size());
		assertTrue(revoked.contains(c1));
		c1.loadIssuedCertificate(PASSWORD);

		CRLProperties crl = ca.createCRL(ZonedDateTime.now().plusSeconds(3600));

		// Insert CSR
		Path file = TestUtilities.getFile(csrFilename);
		assertNotNull(file);

		CertificateRequestProperties csr = ca.addCertificateSigningRequest(file);

		/*
		 * Request 3
		 */

		// Include them all!
		GeneralNames subjAltNames3 = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user2")).build();

		CertificateRequest req3 = new CertificateRequest();
		req3.setcARequest(false);
		req3.setCreationDate(ZonedDateTime.now());
		req3.setDescription(subject2.toString());
		req3.setSubject(subject2);
		req3.setKeyType(KeyType.DSTU4145_0);
		req3.setKeyUsage(subKeyUsage);
		req3.setExtendedKeyUsage(extKeyUsage);
		req3.setSubjectAlternativeName(subjAltNames3);

		IssuedCertificateProperties c3 = ca.signAndStoreCertificateRequest(req3, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c3);

		c3.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),
				BouncyCastleProvider.PROVIDER_NAME);
		
		/*
		 * Request 4 - Rainbow
		 */

		CertificateRequest req4 = createRequest(KeyType.Rainbow);

		IssuedCertificateProperties c4 = ca.signAndStoreCertificateRequest(req4, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c4);

		c4.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),	BouncyCastleProvider.PROVIDER_NAME);
		
		
		/*
		 * Request 5 - SPHINCS
		 */

		CertificateRequest req5 = createRequest(KeyType.SPHINCS_SHA3_256);

		IssuedCertificateProperties c5 = ca.signAndStoreCertificateRequest(req5, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c5);

		c5.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),	BouncyCastleProvider.PROVIDER_NAME);

		/*
		 * Request 6 - XMSS
		 */

		CertificateRequest req6 = createRequest(KeyType.XMSS_SHA2_10_256);

		IssuedCertificateProperties c6 = ca.signAndStoreCertificateRequest(req6, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c6);

		c6.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),	BouncyCastleProvider.PROVIDER_NAME);

		/*
		 * Request 7 - XMSS-MT
		 */

		CertificateRequest req7 = createRequest(KeyType.XMSSMT_SHA2_20_4_256);

		IssuedCertificateProperties c7 = ca.signAndStoreCertificateRequest(req7, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c7);

		c7.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),	BouncyCastleProvider.PROVIDER_NAME);

		
		/*
		 * Request 8 - GOST
		 */

		CertificateRequest req8 = createRequest(KeyType.GOST_3410_94_A);

		IssuedCertificateProperties c8 = ca.signAndStoreCertificateRequest(req8, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360), PASSWORD);

		assertNotNull(c8);

		c8.loadIssuedCertificate(PASSWORD).getCertificateChain()[0].verify(key.getPublic(),	BouncyCastleProvider.PROVIDER_NAME);

		
		/*
		 * Test Renderers
		 */

		// CA Cert
		CertificateRenderer caR = new CertificateRenderer(ca);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		caR.render(txt);
		txt.finaliseRender();
		HTMLOutputRenderer html = new HTMLOutputRenderer(System.out, "");
		caR.render(html);
		html.finaliseRender();

		render(c1);
		render(c2);
		render(c3);
		render(c4);
		render(c5);
		render(c6);
		render(c7);
		render(c8);
		
		// CSR
		CSRRenderer csrr = new CSRRenderer(csr);
		txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
		html = new HTMLOutputRenderer(System.out, "");
		csrr.render(html);
		html.finaliseRender();

		// CRL
		CRLRenderer clrr = new CRLRenderer(crl);
		txt = new TextOutputRenderer(System.out);
		clrr.render(txt);
		txt.finaliseRender();
		html = new HTMLOutputRenderer(System.out, "");
		clrr.render(html);
		html.finaliseRender();

	}

	private CertificateRequest createRequest(KeyType type) {
		CertificateRequest req4 = new CertificateRequest();
		req4.setcARequest(false);
		req4.setCreationDate(ZonedDateTime.now());
		req4.setDescription(subject.toString());
		req4.setSubject(subject);
		req4.setKeyType(type);
		return req4;
	}

	private void render(IssuedCertificateProperties cProps) {
		CertificateRenderer cert = new CertificateRenderer(cProps);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		cert.render(txt);
		txt.finaliseRender();
		HTMLOutputRenderer html = new HTMLOutputRenderer(System.out, "");
		cert.render(html);
		html.finaliseRender();
	}
}
