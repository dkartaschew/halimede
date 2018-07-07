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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCertificateAuthoritySettings {

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void createAndRead() throws Exception {
		CertificateAuthoritySettings settings = new CertificateAuthoritySettings();
		settings.setDescription("My CA");
		settings.setPkcs12Filename("store.p12");
		settings.setSerial(new Random().nextLong());
		settings.setCRLSerial(new Random().nextLong());
		settings.setUuid(UUID.randomUUID());
		settings.setSignatureAlgorithm(SignatureAlgorithm.SHA512withECDSA);
		settings.setExpiryDays(265);
		Path path = Paths.get(TestUtilities.TMP, CertificateAuthoritySettings.DEFAULT_NAME);
		try {
			CertificateAuthoritySettings.write(settings, path);

			CertificateAuthoritySettings settings2 = CertificateAuthoritySettings.read(path);
			assertEquals(settings, settings2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test for object base methods
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void testObject() throws Exception {
		CertificateAuthoritySettings settings = new CertificateAuthoritySettings();
		CertificateAuthoritySettings settings2 = new CertificateAuthoritySettings();
		assertFalse(settings.toString().contains("null"));
		assertFalse(settings.equals(null));
		assertFalse(settings.equals(new Object()));
		assertEquals(settings, settings);
		assertEquals(settings, settings2);
		assertEquals(settings.hashCode(), settings.hashCode());

		settings.setUuid(UUID.randomUUID());
		assertNotEquals(settings, settings2);
		assertNotEquals(settings2, settings);
		assertFalse(settings.toString().contains("null"));

		settings.setPkcs12Filename("store.p12");
		assertNotEquals(settings, settings2);
		assertNotEquals(settings2, settings);
		assertFalse(settings.toString().contains("null"));

		settings.setDescription("My CA");
		assertTrue(settings.toString().contains("My CA"));

		assertEquals(settings.hashCode(), settings.hashCode());
		assertEquals(settings, settings);
		assertNotEquals(settings, settings2);
		assertNotEquals(settings2, settings);

		settings2.setPkcs12Filename("store2.p12");
		assertEquals(settings.hashCode(), settings.hashCode());
		assertEquals(settings, settings);
		assertNotEquals(settings, settings2);
		assertNotEquals(settings2, settings);

		settings2.setUuid(UUID.randomUUID());
		assertEquals(settings.hashCode(), settings.hashCode());
		assertEquals(settings, settings);
		assertNotEquals(settings, settings2);
		assertNotEquals(settings2, settings);

		settings2.setUuid(settings.getUuid());
		assertEquals(settings.hashCode(), settings.hashCode());
		assertEquals(settings, settings);
		assertNotEquals(settings, settings2);
		assertNotEquals(settings2, settings);

		settings2.setPkcs12Filename("store.p12");
		settings2.setDescription("My CA");
		assertEquals(settings.hashCode(), settings2.hashCode());
		assertEquals(settings, settings2);

		settings.setUuid(null);
		assertFalse(settings.toString().contains("null"));

		CertificateAuthoritySettings settings3 = new CertificateAuthoritySettings(settings.getUuid());
		settings3.setPkcs12Filename(settings.getPkcs12Filename());
		assertEquals(settings.hashCode(), settings3.hashCode());
		assertEquals(settings, settings3);
	}

	/**
	 * Basic test of serial number increment
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void serial() throws Exception {
		CertificateAuthoritySettings settings = new CertificateAuthoritySettings(UUID.randomUUID());

		/*
		 * Issuing serial.
		 */
		assertEquals(0, settings.getAndIncrementSerial());
		assertEquals(1, settings.getAndIncrementSerial());
		assertEquals(2, settings.getAndIncrementSerial());
		assertEquals(3, settings.getAndIncrementSerial());

		long next = new Random().nextLong();
		settings.setSerial(next);
		assertEquals(next + 0, settings.getAndIncrementSerial());
		assertEquals(next + 1, settings.getAndIncrementSerial());
		assertEquals(next + 2, settings.getAndIncrementSerial());
		assertEquals(next + 3, settings.getAndIncrementSerial());

		/*
		 * CRL serial.
		 */

		assertEquals(1, settings.getAndIncrementCRLSerial());
		assertEquals(2, settings.getAndIncrementCRLSerial());
		assertEquals(3, settings.getAndIncrementCRLSerial());
		assertEquals(4, settings.getAndIncrementCRLSerial());

		next = new Random().nextLong();
		settings.setCRLSerial(next);
		assertEquals(next + 0, settings.getAndIncrementCRLSerial());
		assertEquals(next + 1, settings.getAndIncrementCRLSerial());
		assertEquals(next + 2, settings.getAndIncrementCRLSerial());
		assertEquals(next + 3, settings.getAndIncrementCRLSerial());
	}

}
