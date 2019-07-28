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
import static org.junit.Assume.assumeTrue;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCertificateAuthority {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("CN=MyCACert");

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
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

			assertEquals(ca, ca);
			assertFalse(ca.equals(null));
			assertEquals(ca, ca2);
			assertEquals(ca.hashCode(), ca2.hashCode());
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
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createNullLocation() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority.create(null, IssuedCertificate.openPKCS12(file, PASSWORD));
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createMissingLocation() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		// assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createFileLocation() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		Path child = path.resolve("file");
		Files.createFile(child);
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority.create(child, IssuedCertificate.openPKCS12(file, PASSWORD));
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createNullCertificate() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			CertificateAuthority.create(path, null);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createMissingCertificate() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		IIssuedCertificate cert = Mockito.mock(IIssuedCertificate.class);
		Mockito.when(cert.getCertificateChain()).thenReturn(null);
		try {
			CertificateAuthority.create(path, cert);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createEmptyCertificateChain() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		IIssuedCertificate cert = Mockito.mock(IIssuedCertificate.class);
		Mockito.when(cert.getCertificateChain()).thenReturn(new Certificate[0]);
		try {
			CertificateAuthority.create(path, cert);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createNoPublicKey() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		IIssuedCertificate cert = Mockito.mock(IIssuedCertificate.class);
		Mockito.when(cert.getCertificateChain()).thenReturn(new Certificate[1]);
		Mockito.when(cert.getPublicKey()).thenReturn(null);
		Mockito.when(cert.getPrivateKey()).thenReturn(Mockito.mock(PrivateKey.class));
		try {
			CertificateAuthority.create(path, cert);
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IOException.class)
	public void createNoPrivateKey() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		IIssuedCertificate cert = Mockito.mock(IIssuedCertificate.class);
		Mockito.when(cert.getCertificateChain()).thenReturn(new Certificate[1]);
		Mockito.when(cert.getPublicKey()).thenReturn(Mockito.mock(PublicKey.class));
		Mockito.when(cert.getPrivateKey()).thenReturn(null);
		try {
			CertificateAuthority.create(path, cert);
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
		f = ca.generateFilename(BigInteger.valueOf(0xffeeddccbbaa00l), CertificateAuthority.ISSUED_PATH, ".p12")
				.toFile();
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

	/**
	 * Test bad parameter for opening a CA
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = IOException.class)
	public void openFilename() throws Exception {
		CertificateAuthority.open(TestUtilities.getFolder("CA.serial").resolve("configuration.xml"));
	}

	/**
	 * Test bad parameter for opening a CA
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = IOException.class)
	public void openFilenamePath() throws Exception {
		CertificateAuthority.open(Paths.get("/", "tmp", ".."));
	}

	/**
	 * Test bad parameter for opening a CA
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = IOException.class)
	public void openProc() throws Exception {
		assumeTrue(System.getProperty("os.name").toLowerCase().contains("linux"));
		CertificateAuthority.open(Paths.get("/proc"));
	}

	/**
	 * Test opening an existing CA, ensuring the serial is reset correctly.
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void autoFixIssuedSerial() throws Exception {
		Path path = TestUtilities.getFolder("CA.serial");
		Path dest = Paths.get(TestUtilities.TMP, "CA.serial");
		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			// ensure original state.
			CertificateAuthoritySettings settings = CertificateAuthoritySettings
					.read(dest.resolve(CertificateAuthoritySettings.DEFAULT_NAME));
			assertEquals(BigInteger.valueOf(149793163), settings.getSerial());

			// Load as a CA, and ensure the value is updated.
			CertificateAuthority ca = CertificateAuthority.open(dest);
			assertEquals(UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"), ca.getCertificateAuthorityID());
			assertEquals(BigInteger.valueOf(1497931630855l), ca.getNextSerialNumber());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Test opening an existing CA, ensuring the serial is reset correctly.
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void autoFixCRLSerial() throws Exception {
		Path path = TestUtilities.getFolder("CA.crlSerial");
		Path dest = Paths.get(TestUtilities.TMP, "CA.crlSerial");
		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			// ensure original state.
			CertificateAuthoritySettings settings = CertificateAuthoritySettings
					.read(dest.resolve(CertificateAuthoritySettings.DEFAULT_NAME));
			assertEquals(BigInteger.valueOf(2), settings.getCRLSerial());

			// Load as a CA, and ensure the value is updated.
			CertificateAuthority ca = CertificateAuthority.open(dest);
			assertEquals(UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"), ca.getCertificateAuthorityID());
			assertEquals(BigInteger.valueOf(101), ca.getNextSerialCRLNumber());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Create and reread a CA instance
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test
	public void createAndLock() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			ca.setDescription(CA_DESCRIPTION);
			assertFalse(ca.isLocked());

			// Ensure we capture all events.
			List<PropertyChangeEvent> events = new ArrayList<>();
			ca.addPropertyChangeListener((e) -> events.add(e));

			ca.lock();
			assertTrue(ca.isLocked());

			assertEquals(1, events.size());
			PropertyChangeEvent e = events.get(0);
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, e.getPropertyName());
			assertEquals(Boolean.FALSE, e.getOldValue());
			assertEquals(Boolean.TRUE, e.getNewValue());

			events.clear();

			ca.unlock(PASSWORD);

			assertFalse(ca.isLocked());

			assertEquals(1, events.size());
			e = events.get(0);
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, e.getPropertyName());
			assertEquals(Boolean.TRUE, e.getOldValue());
			assertEquals(Boolean.FALSE, e.getNewValue());

			events.clear();
			assertFalse(ca.isLocked());

			ca.unlock(PASSWORD);

			assertFalse(ca.isLocked());

			// No change.
			assertEquals(0, events.size());

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Check CA getter/setter and property listener
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test
	public void setDescription() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			assertFalse(ca.isLocked());

			List<PropertyChangeEvent> events = new ArrayList<>();
			ca.addPropertyChangeListener((e) -> events.add(e));

			assertNull(ca.getDescription());

			ca.setDescription(CA_DESCRIPTION);
			assertEquals(CA_DESCRIPTION, ca.getDescription());

			assertEquals(1, events.size());
			PropertyChangeEvent e = events.get(0);
			assertEquals(CertificateAuthority.PROPERTY_DESCRIPTION, e.getPropertyName());
			assertEquals(null, e.getOldValue());
			assertEquals(CA_DESCRIPTION, e.getNewValue());

			events.clear();

			ca.setDescription(CA_DESCRIPTION);

			// No change.
			assertEquals(0, events.size());

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Check CA getter/setter and property listener
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test
	public void setExpiry() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			assertFalse(ca.isLocked());

			List<PropertyChangeEvent> events = new ArrayList<>();
			ca.addPropertyChangeListener((e) -> events.add(e));

			assertEquals(365, ca.getExpiryDays());

			ca.setExpiryDays(24);
			assertEquals(24, ca.getExpiryDays());

			assertEquals(1, events.size());
			PropertyChangeEvent e = events.get(0);
			assertEquals(CertificateAuthority.PROPERTY_EXPIRY, e.getPropertyName());
			assertEquals(365, e.getOldValue());
			assertEquals(24, e.getNewValue());

			events.clear();

			ca.setExpiryDays(24);

			// No change.
			assertEquals(0, events.size());

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Check CA getter/setter
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setExpiryFail() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			assertFalse(ca.isLocked());
			assertEquals(365, ca.getExpiryDays());
			ca.setExpiryDays(0); // throw
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Check CA getter/setter
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void setExpiryFailNeg() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			assertFalse(ca.isLocked());
			assertEquals(365, ca.getExpiryDays());
			ca.setExpiryDays(-1); // throw
		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Check CA getter/setter and property listener
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test
	public void setIncremenatalSerial() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			assertFalse(ca.isLocked());

			ca.setIncrementalSerial(false);

			List<PropertyChangeEvent> events = new ArrayList<>();
			ca.addPropertyChangeListener((e) -> events.add(e));

			assertFalse(ca.isIncrementalSerial());

			ca.setIncrementalSerial(true);
			assertTrue(ca.isIncrementalSerial());

			assertEquals(1, events.size());
			PropertyChangeEvent e = events.get(0);
			assertEquals(CertificateAuthority.PROPERTY_INCREMENTAL_SERIAL, e.getPropertyName());
			assertEquals(Boolean.FALSE, e.getOldValue());
			assertEquals(Boolean.TRUE, e.getNewValue());

			events.clear();

			ca.setIncrementalSerial(true);
			assertTrue(ca.isIncrementalSerial());

			// No change.
			assertEquals(0, events.size());

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Check CA getter/setter and property listener
	 * 
	 * @throws Exception The creation failed.
	 */
	@Test
	public void setLogger() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "CA");
		TestUtilities.cleanup(path);
		assertTrue(path.toFile().mkdirs());
		try {
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = CertificateAuthority.create(path, IssuedCertificate.openPKCS12(file, PASSWORD));
			assertFalse(ca.isLocked());

			List<PropertyChangeEvent> events = new ArrayList<>();
			ca.addPropertyChangeListener((e) -> events.add(e));

			assertNotNull(ca.getActivityLogger());
			assertTrue(ca.isEnableLog());

			ca.setEnableLog(false);
			assertFalse(ca.isEnableLog());

			assertEquals(1, events.size());
			PropertyChangeEvent e = events.get(0);
			assertEquals(CertificateAuthority.PROPERTY_ENABLE_LOG, e.getPropertyName());
			assertEquals(Boolean.TRUE, e.getOldValue());
			assertEquals(Boolean.FALSE, e.getNewValue());

			events.clear();

			ca.setEnableLog(false);
			assertFalse(ca.isEnableLog());

			// No change.
			assertEquals(0, events.size());

			ca.setEnableLog(true);
			assertTrue(ca.isEnableLog());

			assertEquals(1, events.size());
			e = events.get(0);
			assertEquals(CertificateAuthority.PROPERTY_ENABLE_LOG, e.getPropertyName());
			assertEquals(Boolean.FALSE, e.getOldValue());
			assertEquals(Boolean.TRUE, e.getNewValue());

		} finally {
			TestUtilities.cleanup(path);
		}
	}

	/**
	 * Test attempt to read certificate chain when locked.
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void openExistingAndReadCertLocked() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		assertNull(ca.getCertificate());
	}

	/**
	 * Test attempt to read certificate chain when locked.
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void openExistingAndReadCertChainLocked() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		assertNull(ca.getCertificateChain());
	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExistingAndReadCert() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.unlock(PASSWORD);
		assertNotNull(ca.getCertificate());
		Certificate c = ca.getCertificate();
		assertEquals("CN=CA Manager,O=Internet Widgits Pty Ltd,L=Gold Coast,ST=Queensland,C=AU",
				((X509Certificate) c).getSubjectX500Principal().getName());
	}

	/**
	 * Test attempt to read certificate chain when locked.
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExistingAndReadCertChain() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.unlock(PASSWORD);
		assertNotNull(ca.getCertificateChain());
		assertEquals(1, ca.getCertificateChain().length);
		Certificate c = ca.getCertificateChain()[0];
		assertEquals("CN=CA Manager,O=Internet Widgits Pty Ltd,L=Gold Coast,ST=Queensland,C=AU",
				((X509Certificate) c).getSubjectX500Principal().getName());

	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExistingAndExportCert() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.unlock(PASSWORD);
		assertNotNull(ca.getCertificate());
		Certificate c = ca.getCertificate();
		assertEquals("CN=CA Manager,O=Internet Widgits Pty Ltd,L=Gold Coast,ST=Queensland,C=AU",
				((X509Certificate) c).getSubjectX500Principal().getName());

		Path export = Paths.get(TestUtilities.TMP, "ca.cer");
		try {
			ca.exportCertificate(export, EncodingType.PEM);

			// reload and compare to c.
			PKCS7Decoder d = PKCS7Decoder.open(export);
			Certificate c2 = d.getCertificateChain()[0];
			assertEquals(c, c2);
		} finally {
			TestUtilities.delete(export);
		}
	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void openExistingAndExportCertLocked() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.exportCertificate(Paths.get(TestUtilities.TMP, "ca.cer"), EncodingType.PEM);
	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExistingAndExportCertChain() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.unlock(PASSWORD);
		assertNotNull(ca.getCertificateChain());
		Certificate[] c = ca.getCertificateChain();

		Path export = Paths.get(TestUtilities.TMP, "ca.cer");
		try {
			ca.exportCertificateChain(export, EncodingType.PEM);

			// reload and compare to c.
			PKCS7Decoder d = PKCS7Decoder.open(export);
			Certificate[] c2 = d.getCertificateChain();
			assertArrayEquals(c, c2);
		} finally {
			TestUtilities.cleanup(export);
		}
	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void openExistingAndExportCertChainLocked() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.exportCertificateChain(Paths.get(TestUtilities.TMP, "ca.cer"), EncodingType.PEM);
	}
	
	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExistingAndPrivateKey() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.unlock(PASSWORD);
		assertNotNull(ca.getKeyPair().getPrivate());
		PrivateKey p = ca.getKeyPair().getPrivate();

		Path export = Paths.get(TestUtilities.TMP, "ca.key");
		try {
			ca.exportPrivateKey(export, PASSWORD, EncodingType.PEM, PKCS8Cipher.DES3_CBC);

			// reload and compare to c.
			PKCS8Decoder d = PKCS8Decoder.open(export, PASSWORD);
			PrivateKey p2 = d.getKeyPair().getPrivate();
			assertEquals(p, p2);
		} finally {
			TestUtilities.cleanup(export);
		}
	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void openExistingAndExportPrivateKeyLocked() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		Path export = Paths.get(TestUtilities.TMP, "ca.key");
		ca.exportPrivateKey(export, PASSWORD, EncodingType.PEM, PKCS8Cipher.DES3_CBC);
	}
	
	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExistingAndPublicKey() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.unlock(PASSWORD);
		assertNotNull(ca.getKeyPair().getPublic());
		PublicKey p = ca.getKeyPair().getPublic();

		Path export = Paths.get(TestUtilities.TMP, "ca.key");
		try {
			ca.createPublicKey(export, EncodingType.PEM);

			// reload and compare to c.
			PublicKey p2 = PublicKeyDecoder.open(export);
			assertEquals(p, p2);
		} finally {
			TestUtilities.cleanup(export);
		}
	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void openExistingAndExportPublicKeyLocked() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		Path export = Paths.get(TestUtilities.TMP, "ca.key");
		ca.createPublicKey(export, EncodingType.PEM);
	}
	
	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void openExistingAndP12() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		ca.unlock(PASSWORD);
		assertNotNull(ca.getKeyPair());
		KeyPair p = ca.getKeyPair();

		Path export = Paths.get(TestUtilities.TMP, "ca.p12");
		try {
			ca.exportPKCS12(export, PASSWORD, "1", PKCS12Cipher.DES3);

			// reload and compare to c.
			PKCS12Decoder d = PKCS12Decoder.open(export, PASSWORD);
			KeyPair p2 = d.getKeyPair();
			assertEquals(p.getPrivate(), p2.getPrivate());
			assertEquals(p.getPublic(), p2.getPublic());
		} finally {
			TestUtilities.cleanup(export);
		}
	}

	/**
	 * Test attempt to read certificate chain
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test(expected = DatastoreLockedException.class)
	public void openExistingAndExportP12Locked() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		Path export = Paths.get(TestUtilities.TMP, "ca.key");
		ca.exportPKCS12(export, PASSWORD, "1", PKCS12Cipher.DES3);
	}
}
