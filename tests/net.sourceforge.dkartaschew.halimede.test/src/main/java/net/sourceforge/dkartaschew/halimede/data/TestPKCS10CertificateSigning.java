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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.render.CSRRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPKCS10CertificateSigning {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";
	private X500Name issuer = new X500Name("CN=MyCACert");
	private X500Name subjectX = new X500Name("CN=MySubjectCert");
	private final X500Name reqSubject = new X500Name("C=AU,ST=Queensland,L=Gold Coast,O=Internet Widgits Pty Ltd,CN=CA Manager");
	//private final String subject = "CN=CA Manager, O=Internet Widgits Pty Ltd, L=Gold Coast, ST=Queensland, C=AU";
	private final KeyUsage subjectKeyUsage = new KeyUsage(
			KeyUsage.nonRepudiation | KeyUsage.digitalSignature | KeyUsage.keyEncipherment | KeyUsage.dataEncipherment);
	private final KeyUsage caKeyUsage = new KeyUsage(KeyUsage.keyCertSign + KeyUsage.cRLSign);

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
	}

	@Test
	public void TestPKCS10CertificateObject() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("dsa4096key_der.csr"));
		assertTrue(reqA.equals(reqA));
		assertFalse(reqA.equals(null));
		assertFalse(reqA.equals(new Object()));
		String str = reqA.toString();
		assertFalse(str.contains("null"));
	}

	@Test(expected = NullPointerException.class)
	public void TestPKCS10CertificateObjectNUL() {
		new CertificateRequestPKCS10(null);
	}

	/**
	 * Test equals() for DSA
	 * 
	 * @throws Throwable Test failure
	 */
	@Test
	public void testCSREquals_DSA() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("dsa4096key_der.csr"));
		ICertificateRequest reqB = CertificateRequestPKCS10.create(TestUtilities.getFile("dsa4096key_der.csr"));
		assertTrue(reqA.equals(reqB));
		assertEquals(reqA.hashCode(), reqB.hashCode());
	}

	/**
	 * Test equals() for RSA
	 * 
	 * @throws Throwable Test failure
	 */
	@Test
	public void testCSREquals_RSA() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("rsa4096_der.csr"));
		ICertificateRequest reqB = CertificateRequestPKCS10.create(TestUtilities.getFile("rsa4096_der.csr"));
		assertTrue(reqA.equals(reqB));
		assertEquals(reqA.hashCode(), reqB.hashCode());
	}

	/**
	 * Test equals() for EC
	 * 
	 * @throws Throwable Test failure
	 */
	@Test
	public void testCSREquals_EC() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_sha512_pem.csr"));
		ICertificateRequest reqB = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_sha512_pem.csr"));
		assertTrue(reqA.equals(reqB));
		assertEquals(reqA.hashCode(), reqB.hashCode());
	}

	/**
	 * Test equals() for DSA
	 * 
	 * @throws Throwable Test failure
	 */
	@Test
	public void testCSRNotEquals_DSA() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("dsa4096key_der.csr"));
		ICertificateRequest reqB = CertificateRequestPKCS10.create(TestUtilities.getFile("dsa4096key_sha256_der.csr"));
		assertFalse(reqA.equals(reqB));
		assertNotEquals(reqA.hashCode(), reqB.hashCode());
	}

	/**
	 * Test equals() for RSA
	 * 
	 * @throws Throwable Test failure
	 */
	@Test
	public void testCSRNotEquals_RSA() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("rsa4096_pem.csr"));
		ICertificateRequest reqB = CertificateRequestPKCS10.create(TestUtilities.getFile("rsa4096_sha512_pem.csr"));
		assertFalse(reqA.equals(reqB));
		assertNotEquals(reqA.hashCode(), reqB.hashCode());
	}

	/**
	 * Test equals() for EC
	 * 
	 * @throws Throwable Test failure
	 */
	@Test
	public void testCSRNotEquals_EC() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_pem.csr"));
		ICertificateRequest reqB = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_sha512_pem.csr"));
		assertFalse(reqA.equals(reqB));
		assertNotEquals(reqA.hashCode(), reqB.hashCode());
	}

	/**
	 * Test basic signing routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSigning_DSA() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		Path file = TestUtilities.getFile("dsa4096key_der.csr");
		assertTrue(path.toFile().mkdirs());
		try {
			testCertifcateGeneration(path, KeyType.EC_sect571r1, file);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test basic signing routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSigning_RSA() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		Path file = TestUtilities.getFile("rsa4096_der.csr");
		assertTrue(path.toFile().mkdirs());
		try {
			testCertifcateGeneration(path, KeyType.EC_sect571r1, file);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test basic signing routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSigning_EC() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		Path file = TestUtilities.getFile("ec521key_der_key.csr");
		assertTrue(path.toFile().mkdirs());
		try {
			testCertifcateGeneration(path, KeyType.EC_sect571r1, file);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test basic signing routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSigning_EC2() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		Path file = TestUtilities.getFile("ec521key_sha512_pem_key.csr");
		assertTrue(path.toFile().mkdirs());
		try {
			testCertifcateGeneration(path, KeyType.EC_sect571r1, file);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test basic signing routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSigning_EC_CA() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		Path file = TestUtilities.getFile("ec521key_sha512_pem_ca.csr");
		assertTrue(path.toFile().mkdirs());
		try {
			testCertifcateGeneration(path, KeyType.EC_sect571r1, file);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	@Test
	public void testSubjectAlternateNames() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec_email.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;

		// Should be 1 SAN - an email address.
		GeneralNames names = req.getSubjectAlternativeNames();
		assertNotNull(names);
		assertEquals(1, names.getNames().length);
		GeneralName name = names.getNames()[0];
		GeneralNameTag tag = GeneralNameTag.forTag(name.getTagNo());
		assertEquals(GeneralNameTag.rfc822Name, tag);

	}

	@Test
	public void testKeyUsage() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec_email.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;
		KeyUsage usage = req.getKeyUsage();
		assertNotNull(usage);
		assertTrue(usage.hasUsages(KeyUsage.digitalSignature));

	}

	@Test
	public void testExtendedKeyUsage() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec_email.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;

		ASN1EncodableVector extUsage = req.getExtendedKeyUsageVector();
		assertNotNull(extUsage);
		Collection<ExtendedKeyUsageEnum> e = ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(extUsage);
		assertEquals(2, e.size());
		assertTrue(e.contains(ExtendedKeyUsageEnum.id_kp_clientAuth));
		assertTrue(e.contains(ExtendedKeyUsageEnum.id_kp_emailProtection));
	}

	@Test
	public void testNetscapeComment() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec_email.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;

		Extension nsComment = req.getExtension(MiscObjectIdentifiers.netscapeCertComment);
		assertNotNull(nsComment);
		ASN1Encodable data = nsComment.getParsedValue();
		assertNotNull(data);
		assertEquals("Email S/MIME Certificate", data.toString());
	}
	
	@Test
	public void testCA2_CRL() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_der_ca2.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;

		assertTrue(req.isCARequest());
		Extensions ext = req.getExtensions();
		assertNotNull(ext);
		BasicConstraints constraints = BasicConstraints.fromExtensions(ext);
		assertNull(constraints);
		KeyUsage usage = req.getKeyUsage();
		assertTrue(usage.hasUsages(KeyUsage.keyCertSign));
	}

	@Test
	public void testCA2_CRLIssuerAndLocation() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_der_ca2.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;

		String location = req.getCrlLocation();
		X500Name issuer = req.getCrlIssuer();
		
		assertNotNull(location);
		assertNotNull(issuer);
		assertEquals("C=AU,O=Internet Widgits Pty Ltd,CN=CRLIssuer", issuer.toString());
		assertEquals("http://example.net/ca.jsp?crl", location);
	}
	
	@Test
	public void testCA_CRLIssuerAndLocation() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_der_ca.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;

		String location = req.getCrlLocation();
		X500Name issuer = req.getCrlIssuer();
		
		assertNull(location);
		assertNull(issuer);
	}
	
	@Test
	public void testCSRRender() throws Throwable {
		ICertificateRequest req = CertificateRequestPKCS10.create(TestUtilities.getFile("ec_email.csr"));
		CertificateRequestProperties csr = new CertificateRequestProperties(null, req);
		CSRRenderer csrr = new CSRRenderer(csr);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
	}
	
	@Test
	public void testCSRRender2() throws Throwable {
		ICertificateRequest req = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_pem_ca.csr"));
		CertificateRequestProperties csr = new CertificateRequestProperties(null, req);
		CSRRenderer csrr = new CSRRenderer(csr);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
	}
	
	@Test
	public void testCSRRender2_DER() throws Throwable {
		ICertificateRequest req = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_der_ca.csr"));
		CertificateRequestProperties csr = new CertificateRequestProperties(null, req);
		CSRRenderer csrr = new CSRRenderer(csr);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
	}
	
	@Test
	public void testCSRRender3() throws Throwable {
		ICertificateRequest req = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_pem_ca2.csr"));
		CertificateRequestProperties csr = new CertificateRequestProperties(null, req);
		CSRRenderer csrr = new CSRRenderer(csr);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
	}
	
	@Test
	public void testCSRRender3_DER() throws Throwable {
		ICertificateRequest req = CertificateRequestPKCS10.create(TestUtilities.getFile("ec521key_der_ca2.csr"));
		CertificateRequestProperties csr = new CertificateRequestProperties(null, req);
		CSRRenderer csrr = new CSRRenderer(csr);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
	}
	
	@Test
	public void testCSRNoExtensions() throws Throwable {
		ICertificateRequest reqA = CertificateRequestPKCS10.create(TestUtilities.getFile("rsa4096_der.csr"));
		CertificateRequestPKCS10 req = (CertificateRequestPKCS10) reqA;
		assertNull(req.getExtensions());
		assertNull(req.getExtension(MiscObjectIdentifiers.netscapeCertComment));
		assertNull(req.getSubjectAlternativeNames());
		assertNull(req.getSubjectAlternativeName());
		CertificateRequestProperties csr = new CertificateRequestProperties(null, req);
		CSRRenderer csrr = new CSRRenderer(csr);
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		csrr.render(txt);
		txt.finaliseRender();
	}
	
	/**
	 * Basic listener to capture listener notifications
	 */
	class Listener implements PropertyChangeListener {

		PropertyChangeEvent evt;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			this.evt = evt;
		}

	}

	/**
	 * Test basic signing and store routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSignAndStore() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		assertTrue(path.toFile().mkdirs());
		try {
			Listener l = new Listener();

			/*
			 * CA
			 */

			KeyPair key = KeyPairFactory.generateKeyPair(KeyType.RSA_512);
			X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
					ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
					true);
			IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);

			CertificateAuthority ca = CertificateAuthority.create(path, ic);
			ca.setDescription(CA_DESCRIPTION);
			assertFalse(ca.isLocked());
			ca.addPropertyChangeListener(l);

			/*
			 * Request
			 */

			KeyUsage subKeyUsage = new KeyUsage(KeyUsage.dataEncipherment + KeyUsage.digitalSignature);
			ASN1EncodableVector extKeyUsage = new ASN1EncodableVector();
			extKeyUsage.add(KeyPurposeId.id_kp_codeSigning);
			extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);

			GeneralNames subjAltNames = new GeneralNamesBuilder()//
					.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

			CertificateRequest req = new CertificateRequest();
			req.setcARequest(false);
			req.setSubject(subjectX);
			req.setKeyType(KeyType.RSA_512);
			req.setKeyUsage(subKeyUsage);
			req.setExtendedKeyUsage(extKeyUsage);
			req.setSubjectAlternativeName(subjAltNames);
			req.setCreationDate(ZonedDateTime.now());

			IssuedCertificateProperties c = ca.signAndStoreCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
					ZonedDateTime.now().plusSeconds(360), PASSWORD);

			assertNotNull(c);
			assertEquals(req.getKeyType(), KeyType.valueOf(c.getProperty(Key.keyType)));

			assertNotNull(l.evt);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<IssuedCertificateProperties> newValue = new ArrayList<>((Collection) l.evt.getNewValue());
			assertEquals(1, newValue.size());
			assertEquals(c, newValue.get(0));

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test basic signing and store routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSignAndStorePKCS10() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		assertTrue(path.toFile().mkdirs());
		try {
			Listener l = new Listener();

			/*
			 * CA
			 */

			KeyPair key = KeyPairFactory.generateKeyPair(KeyType.RSA_512);
			X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
					ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
					true);
			IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);

			CertificateAuthority ca = CertificateAuthority.create(path, ic);
			ca.setDescription(CA_DESCRIPTION);
			assertFalse(ca.isLocked());
			ca.addPropertyChangeListener(l);

			/*
			 * Request
			 */
			Path pkcs10file = TestUtilities.getFile("ec521key_der_key.csr");
			ICertificateRequest req = CertificateRequestPKCS10.create(pkcs10file);

			IssuedCertificateProperties c = ca.signAndStoreCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
					ZonedDateTime.now().plusSeconds(360), PASSWORD);

			assertNotNull(c);
			assertEquals(req.getSubject().toString(), c.getProperty(Key.subject));

			assertNotNull(l.evt);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<IssuedCertificateProperties> newValue = new ArrayList<>((Collection) l.evt.getNewValue());
			assertEquals(1, newValue.size());
			assertEquals(c, newValue.get(0));

		} finally {
			TestUtilities.cleanup(path);
		}
	}
	
	/**
	 * Test basic signing and store routine.
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	public void testSignAndStorePKCS10_DSA4096() throws Throwable {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		assertTrue(path.toFile().mkdirs());
		try {
			Listener l = new Listener();

			/*
			 * CA
			 */

			KeyPair key = KeyPairFactory.generateKeyPair(KeyType.RSA_512);
			X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
					ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
					true);
			IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);

			CertificateAuthority ca = CertificateAuthority.create(path, ic);
			ca.setDescription(CA_DESCRIPTION);
			assertFalse(ca.isLocked());
			ca.addPropertyChangeListener(l);

			/*
			 * Request (unhandled type).
			 */
			Path pkcs10file = TestUtilities.getFile("dsa4096key_der.csr");
			ca.addCertificateSigningRequest(pkcs10file);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<CertificateRequestProperties> newValue = new ArrayList<>(((ConcurrentHashMap) l.evt.getNewValue()).values());
			assertEquals(1, newValue.size());
			ICertificateRequest req = newValue.get(0).getCertificateRequest();
			assertNull(newValue.get(0).getProperty(CertificateRequestProperties.Key.keyType));

			IssuedCertificateProperties c = ca.signAndStoreCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
					ZonedDateTime.now().plusSeconds(360), PASSWORD);

			assertNotNull(c);
			assertEquals(req.getSubject().toString(), c.getProperty(Key.subject));

			assertNotNull(l.evt);
			@SuppressWarnings({ "unchecked", "rawtypes" })
			List<IssuedCertificateProperties> newValue2 = new ArrayList<>((Collection) l.evt.getNewValue());
			assertEquals(1, newValue2.size());
			assertEquals(c, newValue2.get(0));

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test with the given combination
	 * 
	 * @param path The path to the CA
	 * @param caKey The CA Keying material type
	 * @param pkcs10file The filename of the PKCS10 file.
	 * @throws Throwable The creation failed.
	 */
	private void testCertifcateGeneration(Path path, KeyType caKey, Path pkcs10file) throws Throwable {
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
		 * Request
		 */

		ICertificateRequest req = CertificateRequestPKCS10.create(pkcs10file);
		X500Name reqSubj = req.getSubject();
		assertEquals(reqSubject, reqSubj);

		Certificate c = ca.signCertificateRequest(req, ZonedDateTime.now().plusSeconds(10),
				ZonedDateTime.now().plusSeconds(360));

		assertNotNull(c);

		c.verify(key.getPublic(), BouncyCastleProvider.PROVIDER_NAME);

		TestUtilities.displayCertificate(new Certificate[] { c });

		assertTrue(c instanceof X509Certificate);
		X509Certificate x509c = (X509Certificate) c;
		assertEquals(reqSubject.toString(), x509c.getSubjectDN().getName());
		assertEquals(issuer.toString(), x509c.getIssuerDN().getName());
		assertEquals(ca.getNextSerialNumber().longValue() - 1, x509c.getSerialNumber().longValue());

		if (pkcs10file.getFileName().toString().contains("_key")) {

			// Check key usage, and extended key usage.
			JcaX509CertificateHolder x509ch = new JcaX509CertificateHolder(x509c);
			KeyUsage x509KeyUsage = KeyUsage.fromExtensions(x509ch.getExtensions());
			assertNotNull(x509KeyUsage);
			assertEquals(subjectKeyUsage, x509KeyUsage);

			ASN1EncodableVector extKeyUsage = new ASN1EncodableVector();
			extKeyUsage.add(KeyPurposeId.id_kp_serverAuth);
			extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);

			ExtendedKeyUsage x509ExtKeyUsage = ExtendedKeyUsage.fromExtensions(x509ch.getExtensions());
			assertNotNull(x509ExtKeyUsage);
			for (int i = 0; i < extKeyUsage.size(); i++) {
				assertTrue(x509ExtKeyUsage.hasKeyPurposeId((KeyPurposeId) extKeyUsage.get(i)));
			}
			assertEquals(x509ExtKeyUsage.size(), extKeyUsage.size());
			
			Extension nsComment = ((CertificateRequestPKCS10) req).getExtension(MiscObjectIdentifiers.netscapeCertComment);
			assertNull(nsComment);

		} else if (pkcs10file.getFileName().toString().contains("_ca")) {
			// Check key usage, and extended key usage.
			JcaX509CertificateHolder x509ch = new JcaX509CertificateHolder(x509c);
			KeyUsage x509KeyUsage = KeyUsage.fromExtensions(x509ch.getExtensions());
			assertNotNull(x509KeyUsage);
			assertEquals(caKeyUsage, x509KeyUsage);

			BasicConstraints constraints = BasicConstraints.fromExtensions(x509ch.getExtensions());
			assertNotNull(constraints);
			assertTrue(constraints.isCA());
		}

	}

}
