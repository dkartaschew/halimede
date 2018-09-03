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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;

/**
 * Tests for signing failures.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCertificateSigningFailureModes {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("CN=MyCACert");
	private X500Name subjectEquals = new X500Name("CN=MyCACert");
	private X500Name subject = new X500Name("CN=Cert");

	private Path path;
	private CertificateAuthority ca;

	@BeforeClass
	public static void setupClass() {
		Security.addProvider(new BouncyCastleProvider());
		Security.addProvider(new BouncyCastlePQCProvider());
	}

	@Before
	public void setup() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException,
			CertificateException, OperatorCreationException, CertIOException, IOException {
		/*
		 * CA
		 */
		path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());

		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.EC_secp521r1);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
				true);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);

		ca = CertificateAuthority.create(path, ic);
		ca.setDescription(CA_DESCRIPTION);
		assertFalse(ca.isLocked());
	}

	@After
	public void tearDown() throws IOException {
		TestUtilities.cleanup(path);
	}

	/**
	 * Test No Subject
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullSubject() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(null);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, ZonedDateTime.now().plusSeconds(10), ZonedDateTime.now().plusSeconds(360));
	}

	/**
	 * Test No start Time
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullStartTime() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, null, ZonedDateTime.now().plusSeconds(360));
	}

	/**
	 * Test No Expiry Time
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullExpiryTime() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, ZonedDateTime.now().plusSeconds(360), null);
	}

	/**
	 * Test Expiry before Start Time
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExpiryBeforeStartTime() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, ZonedDateTime.now().plusSeconds(360), ZonedDateTime.now());
	}

	/**
	 * Test No key type
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullKeyType() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(null);

		ca.signCertificateRequest(req, ZonedDateTime.now(), ZonedDateTime.now().plusSeconds(360));
	}

	/**
	 * Test Subject equals issuers
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSubjectEqualIssur() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subjectEquals);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, ZonedDateTime.now(), ZonedDateTime.now().plusSeconds(360));
	}

	/**
	 * Test Start Before Issuer Start
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testStartBeforeIssuerStart() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, ZonedDateTime.now().minusHours(1), ZonedDateTime.now().plusSeconds(360));
	}

	/**
	 * Test Expiry After Issuer Expiry
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testExpiryAfterIssuerExpiry() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, ZonedDateTime.now(), ZonedDateTime.now().plusHours(10));
	}

	/**
	 * Test Start After Issuer Expiry
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testStartAfterIssuerExpiry() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		ca.signCertificateRequest(req, ZonedDateTime.now().plusHours(9), ZonedDateTime.now().plusHours(10));
	}

	/**
	 * Test CA Locked
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void testLockedCA() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		ca.lock();
		ca.signCertificateRequest(req, ZonedDateTime.now(), ZonedDateTime.now().plusSeconds(10));
	}

	/**
	 * Test Null CA Instance
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNoCAInstance() throws Throwable {

		CertificateRequest req = new CertificateRequest();
		req.setSubject(subject);
		req.setKeyType(KeyType.RSA_512);

		CertificateFactory.signCertificateRequest(null, req, ZonedDateTime.now(), ZonedDateTime.now());
	}

	/**
	 * Test Null CA Instance
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNoRequestInstance() throws Throwable {
		ca.signCertificateRequest(null, ZonedDateTime.now(), ZonedDateTime.now().plusSeconds(10));
	}
}
