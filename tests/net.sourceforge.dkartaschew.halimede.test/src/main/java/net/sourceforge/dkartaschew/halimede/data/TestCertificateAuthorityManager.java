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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.random.NotSecureRandom;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

/**
 * Test Certificate Authority Manager
 */
public class TestCertificateAuthorityManager {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

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

	/**
	 * Test basic elements, including property change listeners.
	 */
	@Test
	public void testBasicSetup() {
		CertificateAuthourityManager mgr = new CertificateAuthourityManager();
		assertEquals(0, mgr.getCertificateAuthorities().size());

		List<PropertyChangeEvent> events = new ArrayList<>();
		PropertyChangeListener l = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				events.add(evt);
			}
		};
		mgr.addPropertyChangeListener(l);

		PropertyChangeEvent evt = new PropertyChangeEvent(this, "name", Boolean.FALSE, Boolean.TRUE);
		mgr.propertyChange(evt);

		mgr.propertyChange(null); // should not throw.

		mgr.removePropertyChangeListener(l);

		assertEquals(1, events.size());
		assertEquals("name", events.get(0).getPropertyName());
	}

	/**
	 * Open an existing CA
	 * 
	 * @throws IOException Reading the CA failed.
	 * @throws CertificateEncodingException Reading the CA failed.
	 * @throws KeyStoreException Reading the CA failed.
	 * @throws InvalidPasswordException Reading the CA failed.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testOpenExisiting()
			throws IOException, CertificateEncodingException, KeyStoreException, InvalidPasswordException {
		Path path = TestUtilities.getFolder("CA");
		Path dest = Paths.get(TestUtilities.TMP, "CA");

		CertificateAuthourityManager mgr = new CertificateAuthourityManager();

		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			List<PropertyChangeEvent> events = new ArrayList<>();
			mgr.addPropertyChangeListener((e) -> events.add(e));

			// Add an existing CA.
			CertificateAuthority ca = mgr.open(dest);
			assertNotNull(ca);

			assertTrue(ca.isLocked());
			ca.unlock(PASSWORD);
			assertFalse(ca.isLocked());

			assertEquals(2, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(0).getPropertyName());
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, events.get(1).getPropertyName());

			events.clear();

			mgr.remove(ca);
			assertNotNull(ca);
			assertEquals(2, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(1).getPropertyName());
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, events.get(0).getPropertyName());
			assertEquals(0, ((Set<CertificateAuthority>) events.get(1).getNewValue()).size());

			assertTrue(ca.isLocked());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Open an existing CA, and remove the listener, ensure the CA is locked.
	 * 
	 * @throws IOException Reading the CA failed.
	 * @throws CertificateEncodingException Reading the CA failed.
	 * @throws KeyStoreException Reading the CA failed.
	 * @throws InvalidPasswordException Reading the CA failed.
	 */
	@Test
	public void testOpenExisitinCheckListeners()
			throws IOException, CertificateEncodingException, KeyStoreException, InvalidPasswordException {
		Path path = TestUtilities.getFolder("CA");
		Path dest = Paths.get(TestUtilities.TMP, "CA");

		CertificateAuthourityManager mgr = new CertificateAuthourityManager();

		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			List<PropertyChangeEvent> events = new ArrayList<>();
			PropertyChangeListener l = new PropertyChangeListener() {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					events.add(evt);
				}
			};
			mgr.addPropertyChangeListener(l);

			// Add an existing CA.
			CertificateAuthority ca = mgr.open(dest);
			assertNotNull(ca);

			assertTrue(ca.isLocked());
			ca.unlock(PASSWORD);
			assertFalse(ca.isLocked());

			assertEquals(2, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(0).getPropertyName());
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, events.get(1).getPropertyName());

			events.clear();

			// Removing the listener, should LOCK the ca.
			mgr.removePropertyChangeListener(l);

			assertTrue(ca.isLocked());
			assertEquals(1, mgr.getCertificateAuthorities().size());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Create a new CA for the manager
	 * 
	 * @throws IOException Reading the CA failed.
	 * @throws CertificateEncodingException Reading the CA failed.
	 * @throws KeyStoreException Reading the CA failed.
	 * @throws InvalidPasswordException Reading the CA failed.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAddNew()
			throws IOException, CertificateEncodingException, KeyStoreException, InvalidPasswordException {

		Path dest = Paths.get(TestUtilities.TMP, "CA");
		Files.createDirectories(dest);

		CertificateAuthourityManager mgr = new CertificateAuthourityManager();

		try {
			List<PropertyChangeEvent> events = new ArrayList<>();
			mgr.addPropertyChangeListener((e) -> events.add(e));

			// Add a new CA.
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = mgr.create(dest, IssuedCertificate.openPKCS12(file, PASSWORD), CA_DESCRIPTION);
			assertFalse(ca.isLocked());

			assertNotNull(ca);
			assertEquals(1, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(0).getPropertyName());

			events.clear();

			assertTrue(mgr.remove(ca));
			assertNotNull(ca);
			assertEquals(2, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(1).getPropertyName());
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, events.get(0).getPropertyName());
			assertEquals(0, ((Set<CertificateAuthority>) events.get(1).getNewValue()).size());

			assertTrue(ca.isLocked());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Check for correct exception on null path
	 * 
	 * @throws IOException Reading the CA failed.
	 */
	@Test(expected = IOException.class)
	public void testNullExistingCA() throws IOException {
		CertificateAuthourityManager mgr = new CertificateAuthourityManager();
		mgr.open(null);
	}

	/**
	 * Check for correct exception on null details for new CA
	 * 
	 * @throws IOException Reading the CA failed.
	 * @throws CertificateEncodingException Reading the CA failed.
	 */
	@Test(expected = IOException.class)
	public void testNullNewCA() throws IOException, CertificateEncodingException {
		CertificateAuthourityManager mgr = new CertificateAuthourityManager();
		mgr.create(null, null, CA_DESCRIPTION);
	}

	/**
	 * Check to ensure CA removal is correct.
	 */
	@Test
	public void testRemoveNull() {
		CertificateAuthourityManager mgr = new CertificateAuthourityManager();
		List<PropertyChangeEvent> events = new ArrayList<>();
		mgr.addPropertyChangeListener((e) -> events.add(e));

		assertFalse(mgr.remove(null));

		assertEquals(0, events.size());
	}

	/**
	 * Check to ensure CA removal is correct.
	 * 
	 * @throws IOException Failed to open CA.
	 * @throws CertificateEncodingException Failed to open CA.
	 */
	@Test
	public void testRemoveUnknown() throws IOException, CertificateEncodingException {
		Path path = TestUtilities.getFolder("CA");
		Path dest = Paths.get(TestUtilities.TMP, "CA");

		CertificateAuthourityManager mgr = new CertificateAuthourityManager();

		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			List<PropertyChangeEvent> events = new ArrayList<>();
			mgr.addPropertyChangeListener((e) -> events.add(e));

			CertificateAuthority ca = CertificateAuthority.open(dest);

			assertFalse(mgr.remove(ca));

			assertEquals(0, events.size());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Open an existing CA
	 * 
	 * @throws IOException Reading the CA failed.
	 * @throws CertificateEncodingException Reading the CA failed.
	 * @throws KeyStoreException Reading the CA failed.
	 * @throws InvalidPasswordException Reading the CA failed.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testOpenDuplicate()
			throws IOException, CertificateEncodingException, KeyStoreException, InvalidPasswordException {
		Path path = TestUtilities.getFolder("CA");
		Path dest = Paths.get(TestUtilities.TMP, "CA");

		CertificateAuthourityManager mgr = new CertificateAuthourityManager();

		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			List<PropertyChangeEvent> events = new ArrayList<>();
			mgr.addPropertyChangeListener((e) -> events.add(e));

			// Add an existing CA.
			CertificateAuthority ca = mgr.open(dest);
			assertNotNull(ca);

			assertTrue(ca.isLocked());
			ca.unlock(PASSWORD);
			assertFalse(ca.isLocked());

			assertEquals(2, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(0).getPropertyName());
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, events.get(1).getPropertyName());

			events.clear();

			CertificateAuthority ca2 = mgr.open(dest);
			assertNull(ca2);
			assertEquals(0, events.size());

			mgr.remove(ca);
			assertNotNull(ca);
			assertEquals(2, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(1).getPropertyName());
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, events.get(0).getPropertyName());
			assertEquals(0, ((Set<CertificateAuthority>) events.get(1).getNewValue()).size());

			assertTrue(ca.isLocked());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Create a new CA for the manager, then add a duplicate CA.
	 * 
	 * @throws IOException Reading the CA failed.
	 * @throws CertificateEncodingException Reading the CA failed.
	 * @throws KeyStoreException Reading the CA failed.
	 * @throws InvalidPasswordException Reading the CA failed.
	 */
	@Test(expected = IOException.class)
	public void testAddDuplicate()
			throws IOException, CertificateEncodingException, KeyStoreException, InvalidPasswordException {

		Path dest = Paths.get(TestUtilities.TMP, "CA");
		Files.createDirectories(dest);

		CertificateAuthourityManager mgr = new CertificateAuthourityManager();

		try {
			List<PropertyChangeEvent> events = new ArrayList<>();
			mgr.addPropertyChangeListener((e) -> events.add(e));

			// Add a new CA.
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = mgr.create(dest, IssuedCertificate.openPKCS12(file, PASSWORD), CA_DESCRIPTION);
			assertFalse(ca.isLocked());

			assertNotNull(ca);
			assertEquals(1, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(0).getPropertyName());

			events.clear();

			mgr.create(dest, IssuedCertificate.openPKCS12(file, PASSWORD), CA_DESCRIPTION);
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Create a new CA for the manager, then add a duplicate CA.
	 * 
	 * @throws IOException Reading the CA failed.
	 * @throws CertificateEncodingException Reading the CA failed.
	 * @throws KeyStoreException Reading the CA failed.
	 * @throws InvalidPasswordException Reading the CA failed.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAddThenOpenDuplicate()
			throws IOException, CertificateEncodingException, KeyStoreException, InvalidPasswordException {

		Path dest = Paths.get(TestUtilities.TMP, "CA");
		Files.createDirectories(dest);

		CertificateAuthourityManager mgr = new CertificateAuthourityManager();

		try {
			List<PropertyChangeEvent> events = new ArrayList<>();
			mgr.addPropertyChangeListener((e) -> events.add(e));

			// Add a new CA.
			Path file = TestUtilities.getFile("ec521_aes_2.p12");
			CertificateAuthority ca = mgr.create(dest, IssuedCertificate.openPKCS12(file, PASSWORD), CA_DESCRIPTION);
			assertFalse(ca.isLocked());

			assertNotNull(ca);
			assertEquals(1, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(0).getPropertyName());

			events.clear();

			CertificateAuthority ca2 = mgr.open(dest);
			assertNull(ca2);
			assertEquals(0, events.size());

			mgr.remove(ca);
			assertNotNull(ca);
			assertEquals(2, events.size());
			assertEquals(CertificateAuthourityManager.PROPERTY, events.get(1).getPropertyName());
			assertEquals(CertificateAuthority.PROPERTY_UNLOCK, events.get(0).getPropertyName());
			assertEquals(0, ((Set<CertificateAuthority>) events.get(1).getNewValue()).size());

			assertTrue(ca.isLocked());
		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Test basic elements, including property change listeners.
	 */
	@Test
	public void testListenerRemoval() {
		CertificateAuthourityManager mgr = new CertificateAuthourityManager();
		assertEquals(0, mgr.getCertificateAuthorities().size());

		List<PropertyChangeEvent> events = new ArrayList<>();
		PropertyChangeListener l = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				events.add(evt);
			}
		};
		mgr.addPropertyChangeListener(l);
		mgr.addPropertyChangeListener(e -> events.add(e));

		PropertyChangeEvent evt = new PropertyChangeEvent(this, "name", Boolean.FALSE, Boolean.TRUE);
		mgr.propertyChange(evt);

		mgr.propertyChange(null); // should not throw.

		mgr.removePropertyChangeListener(l);

		assertEquals(2, events.size());
		assertEquals("name", events.get(0).getPropertyName());
	}
}
