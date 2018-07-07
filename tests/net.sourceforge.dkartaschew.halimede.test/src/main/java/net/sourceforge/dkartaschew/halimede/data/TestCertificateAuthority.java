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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCertificateAuthority {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("CN=MyCACert");

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test
	public void createAndRead() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			ca.setDescription(CA_DESCRIPTION);
			assertFalse(ca.isLocked());

			CertificateAuthority ca2 = CertificateAuthority.open(path);
			assertEquals(ca.getCertificateAuthorityID(), ca2.getCertificateAuthorityID());
			assertTrue(ca2.isLocked());
			assertEquals(ca.getDescription(), ca2.getDescription());
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create a new CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test
	public void createNewCA() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			KeyPair key = KeyPairFactory.generateKeyPair(KeyType.EC_secp521r1);
			X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
					ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
					true);
			IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);

			CertificateAuthority ca = CertificateAuthority.create(path, ic);
			ca.setDescription(CA_DESCRIPTION);
			assertFalse(ca.isLocked());

			CertificateAuthority ca2 = CertificateAuthority.open(path);
			assertEquals(ca.getCertificateAuthorityID(), ca2.getCertificateAuthorityID());
			assertTrue(ca2.isLocked());
			assertEquals(ca.getDescription(), ca2.getDescription());
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create a new CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void createNewCA_NotCACertificate() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			KeyPair key = KeyPairFactory.generateKeyPair(KeyType.EC_secp521r1);
			X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
					ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
					false);
			IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);

			CertificateAuthority.create(path, ic);

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test opening an existing CA
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExisting() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
	}

	/**
	 * Test opening an existing CA, and check filename generation
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void checkFilenameGeneration() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);

		File f = ca.generateFilename(BigInteger.valueOf(0x1000), CertificateAuthority.ISSUED_PATH, ".p12").toFile();
		assertEquals("0000000000001000.p12", f.getName());
		f = ca.generateFilename(BigInteger.valueOf(0x100a), CertificateAuthority.ISSUED_PATH, ".p12").toFile();
		assertEquals("000000000000100a.p12", f.getName());
		f = ca.generateFilename(BigInteger.valueOf(0xdeadbeefl), CertificateAuthority.ISSUED_PATH, ".p12").toFile();
		assertEquals("00000000deadbeef.p12", f.getName());
		f = ca.generateFilename(BigInteger.valueOf(0xffeeddccbbaa00l), CertificateAuthority.ISSUED_PATH, ".p12").toFile();
		assertEquals("00ffeeddccbbaa00.p12", f.getName());
	}

	/**
	 * Create a PKCS12 file to use in the bad CA
	 * 
	 * @throws Throwable The creation failed.
	 */
	@Test
	@Ignore
	public void createBadCA() throws Throwable {
		KeyPair key = KeyPairFactory.generateKeyPair(KeyType.EC_secp521r1);
		X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(issuer, //
				ZonedDateTime.now().plusSeconds(3600), key, SignatureAlgorithm.getDefaultSignature(key.getPublic()),
				false);
		IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null, PASSWORD);
		ic.createPKCS12(Paths.get(TestUtilities.TMP, "ca.p12"), null);
	}

	/**
	 * Test opening an existing CA
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void openExisting_BadCert() throws Exception {
		Path path = TestUtilities.getFolder("CA.bad");
		CertificateAuthority ca = CertificateAuthority.open(path);
		// This should throw IllegalArgumentException
		ca.unlock(null);
	}

	/**
	 * Test missing CA instance
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = IOException.class)
	public void openMissing() throws Exception {
		CertificateAuthority.open(Paths.get(TestUtilities.TMP, "CA2"));
	}

	/**
	 * Test bad parameter for opening a CA
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = IOException.class)
	public void openMissingNull() throws Exception {
		CertificateAuthority.open(null);
	}

	/**
	 * Test bad parameter for opening a CA
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = IOException.class)
	public void openMissingEmpty() throws Exception {
		CertificateAuthority.open(Paths.get(""));
	}

}
