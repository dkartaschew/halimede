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
package net.sourceforge.dkartaschew.halimede.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.backup.BackupManifest;
import net.sourceforge.dkartaschew.halimede.backup.BackupManifestEntry;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;

/**
 * Test Backup and restore
 */
public class TestBackupRestore {

	private final String CA_DESCRIPTION = "My CA";
	private final UUID CA_ID = UUID.fromString("7779894e-226f-4230-81ab-612c4387abff");

	/**
	 * Basic progress monitor to console.
	 */
	class TestMonitor extends NullProgressMonitor {

		int cancelAt = 0;
		int work = 0;
		int totalWork = 0;

		TestMonitor() {
		}

		TestMonitor(int cancelAt) {
			this.cancelAt = cancelAt;
		}

		@Override
		public void beginTask(String name, int totalWork) {
			System.out.println(name + " " + totalWork);
			this.totalWork = totalWork;
			this.work = 0;
		}

		@Override
		public void setTaskName(String name) {
			System.out.println(name);
		}

		@Override
		public void subTask(String name) {
			System.out.println(name);
		}

		@Override
		public void worked(int work) {
			this.work = this.work + work;
			System.out.println(this.work + "/" + this.totalWork);
			if (cancelAt != 0 && this.work >= cancelAt) {
				setCanceled(true);
			}
		}
	};

	/**
	 * Basic test of object properties.
	 */
	@Test
	public void testManifestEntryObject() {
		BackupManifestEntry mfe = new BackupManifestEntry();
		BackupManifestEntry mfe2 = new BackupManifestEntry();
		assertEquals(mfe, mfe);
		assertEquals(mfe.hashCode(), mfe.hashCode());
		assertTrue(mfe.toString().contains("Backup"));
		assertTrue(mfe.toString().contains("null"));

		assertNull(mfe.getFilename());
		assertNull(mfe.getSha512());
		assertEquals(0, mfe.getSize());

		assertFalse(mfe.equals(null));
		assertFalse(mfe.equals(new Object()));
		assertTrue(mfe.equals(mfe));
		assertTrue(mfe.equals(mfe2));
		assertTrue(mfe2.equals(mfe));

		mfe.setFilename("abc");
		assertEquals(mfe.hashCode(), mfe.hashCode());
		assertNotEquals(mfe.hashCode(), mfe2.hashCode());
		assertFalse(mfe.equals(mfe2));
		assertFalse(mfe2.equals(mfe));
		mfe2.setFilename("abc");
		assertEquals(mfe.hashCode(), mfe2.hashCode());
		assertTrue(mfe.equals(mfe2));
		assertTrue(mfe2.equals(mfe));

		mfe.setSha512("123");
		assertEquals(mfe.hashCode(), mfe.hashCode());
		assertNotEquals(mfe.hashCode(), mfe2.hashCode());
		assertFalse(mfe.equals(mfe2));
		assertFalse(mfe2.equals(mfe));
		mfe2.setSha512("123");
		assertEquals(mfe.hashCode(), mfe2.hashCode());
		assertTrue(mfe.equals(mfe2));
		assertTrue(mfe2.equals(mfe));

		mfe.setSize(32);
		assertEquals(mfe.hashCode(), mfe.hashCode());
		assertNotEquals(mfe.hashCode(), mfe2.hashCode());
		assertFalse(mfe.equals(mfe2));
		assertFalse(mfe2.equals(mfe));
		mfe2.setSize(32);
		assertEquals(mfe.hashCode(), mfe2.hashCode());
		assertTrue(mfe.equals(mfe2));
		assertTrue(mfe2.equals(mfe));

	}

	/**
	 * Basic test of object properties.
	 */
	@Test
	public void testManifestObject() {
		BackupManifest mf = new BackupManifest();
		BackupManifest mf2 = new BackupManifest();
		assertEquals(mf, mf);
		assertEquals(mf.hashCode(), mf.hashCode());
		assertTrue(mf.toString().contains("Backup"));
		assertTrue(mf.toString().contains("null"));

		assertNull(mf.getCreationDate());
		assertNull(mf.getDescription());
		assertNull(mf.getUuid());
		assertNull(mf.getEntries());

		assertFalse(mf.equals(null));
		assertFalse(mf.equals(new Object()));
		assertTrue(mf.equals(mf));
		assertTrue(mf.equals(mf2));
		assertTrue(mf2.equals(mf));

		mf.setDescription(CA_DESCRIPTION);
		assertEquals(mf.hashCode(), mf.hashCode());
		assertNotEquals(mf.hashCode(), mf2.hashCode());
		assertFalse(mf.equals(mf2));
		assertFalse(mf2.equals(mf));
		mf2.setDescription(CA_DESCRIPTION);
		assertEquals(mf.hashCode(), mf2.hashCode());
		assertTrue(mf.equals(mf2));
		assertTrue(mf2.equals(mf));

		mf.setUuid(CA_ID);
		assertEquals(mf.hashCode(), mf.hashCode());
		assertNotEquals(mf.hashCode(), mf2.hashCode());
		assertFalse(mf.equals(mf2));
		assertFalse(mf2.equals(mf));
		mf2.setUuid(UUID.fromString(CA_ID.toString()));
		assertEquals(mf.hashCode(), mf2.hashCode());
		assertTrue(mf.equals(mf2));
		assertTrue(mf2.equals(mf));

		Instant now = Instant.now();
		mf.setCreationDate(ZonedDateTime.ofInstant(now, ZoneId.of("UTC")));
		assertEquals(mf.hashCode(), mf.hashCode());
		assertNotEquals(mf.hashCode(), mf2.hashCode());
		assertFalse(mf.equals(mf2));
		assertFalse(mf2.equals(mf));
		mf2.setCreationDate(ZonedDateTime.ofInstant(now, ZoneId.of("UTC")));
		assertEquals(mf.hashCode(), mf2.hashCode());
		assertTrue(mf.equals(mf2));
		assertTrue(mf2.equals(mf));

		mf.addEntry(new BackupManifestEntry("abc", 32, "123"));
		assertEquals(mf.hashCode(), mf.hashCode());
		assertNotEquals(mf.hashCode(), mf2.hashCode());
		assertFalse(mf.equals(mf2));
		assertFalse(mf2.equals(mf));
		mf2.addEntry(new BackupManifestEntry("abc", 32, "123"));
		assertEquals(mf.hashCode(), mf2.hashCode());
		assertTrue(mf.equals(mf2));
		assertTrue(mf2.equals(mf));
	}

	@Test
	public void basicBackupRestore() throws SecurityException, IOException, CertificateEncodingException {
		TestMonitor monitor = new TestMonitor();
		try {
			Path path = TestUtilities.getFolder("CA");
			CertificateAuthority ca = CertificateAuthority.open(path);
			assertEquals(ca.getCertificateAuthorityID(), CA_ID);
			assertTrue(ca.isLocked());
			assertEquals(ca.getDescription(), CA_DESCRIPTION);

			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			BackupUtil.backup(ca, zipFile, monitor);

			// Now restore the file to TMP.
			Path destination = Paths.get(TestUtilities.TMP);
			BackupUtil.restore(zipFile, destination, monitor);
		} finally {
			// cleanup.
			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			Path destination = Paths.get(TestUtilities.TMP, CA_DESCRIPTION);
			TestUtilities.cleanup(zipFile);
			TestUtilities.cleanup(destination);
		}
	}

	@Test
	public void basicBackupRestoreNoMonitor() throws SecurityException, IOException, CertificateEncodingException {
		try {
			Path path = TestUtilities.getFolder("CA");
			CertificateAuthority ca = CertificateAuthority.open(path);
			assertEquals(ca.getCertificateAuthorityID(), CA_ID);
			assertTrue(ca.isLocked());
			assertEquals(ca.getDescription(), CA_DESCRIPTION);

			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			BackupUtil.backup(ca, zipFile, null);

			// Now restore the file to TMP.
			Path destination = Paths.get(TestUtilities.TMP);
			BackupUtil.restore(zipFile, destination, null);
		} finally {
			// cleanup.
			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			Path destination = Paths.get(TestUtilities.TMP, CA_DESCRIPTION);
			TestUtilities.cleanup(zipFile);
			TestUtilities.cleanup(destination);
		}
	}

	@Test(expected = NullPointerException.class)
	public void backupNullCA() throws IOException {
		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
		BackupUtil.backup(null, zipFile, null);
	}

	@Test(expected = NullPointerException.class)
	public void backupNullTargetFilename() throws IOException, CertificateEncodingException {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), CA_ID);
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		BackupUtil.backup(ca, null, null);
	}

	@Test
	public void backupCancelOperation() throws IOException, CertificateEncodingException {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), CA_ID);
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");

		BackupUtil.backup(ca, zipFile, new TestMonitor(10));

		// assert that the target no longer exists!
		assertFalse(Files.exists(zipFile));
	}

	@Test
	public void basicBackupFileExists() throws SecurityException, IOException, CertificateEncodingException {
		try {
			Path path = TestUtilities.getFolder("CA");
			CertificateAuthority ca = CertificateAuthority.open(path);
			assertEquals(ca.getCertificateAuthorityID(), CA_ID);
			assertTrue(ca.isLocked());
			assertEquals(ca.getDescription(), CA_DESCRIPTION);

			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			Files.createFile(zipFile);

			BackupUtil.backup(ca, zipFile, null);

			// The target file should be overwritten...
			assertTrue(Files.exists(zipFile));
			assertTrue(zipFile.toFile().length() > 10);

		} finally {
			// cleanup.
			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			TestUtilities.cleanup(zipFile);
		}
	}

	@Test(expected = NullPointerException.class)
	public void restoreNullFile() throws IOException {
		Path destination = Paths.get(TestUtilities.TMP);
		BackupUtil.restore(null, destination, null);
	}

	@Test(expected = NullPointerException.class)
	public void restoreNullDestination() throws IOException {
		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
		BackupUtil.restore(zipFile, null, null);
	}

	@Test(expected = IOException.class)
	public void restoreZipMissing() throws IOException {
		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
		Path destination = Paths.get(TestUtilities.TMP);
		BackupUtil.restore(zipFile, destination, null);
	}

	@Test(expected = IOException.class)
	public void restoreZipIsFolder() throws IOException {
		Path zipFile = Paths.get(TestUtilities.TMP);
		Path destination = Paths.get(TestUtilities.TMP);
		BackupUtil.restore(zipFile, destination, null);
	}

	@Test(expected = IOException.class)
	public void restoreDestinationNotExist() throws IOException {
		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
		Path destination = Paths.get(TestUtilities.TMP, "UnknownFolder");
		BackupUtil.restore(zipFile, destination, null);
	}

	@Test(expected = IOException.class)
	public void restoreDestinationFile() throws IOException {
		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
		Path destination = Paths.get(TestUtilities.TMP, "CA.zip");
		Files.createFile(zipFile);
		try {
			BackupUtil.restore(zipFile, destination, null);
		} finally {
			TestUtilities.cleanup(destination);
		}
	}
	
	@Test(expected = IOException.class)
	public void restoreEmptyZipFile() throws IOException {
		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
		Path destination = Paths.get(TestUtilities.TMP);
		Files.createFile(zipFile);
		BackupUtil.restore(zipFile, destination, null);
	}
	
	@Test
	public void restoreCancelRestore() throws SecurityException, IOException, CertificateEncodingException {
		TestMonitor monitor = new TestMonitor();
		try {
			Path path = TestUtilities.getFolder("CA");
			CertificateAuthority ca = CertificateAuthority.open(path);
			assertEquals(ca.getCertificateAuthorityID(), CA_ID);
			assertTrue(ca.isLocked());
			assertEquals(ca.getDescription(), CA_DESCRIPTION);

			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			BackupUtil.backup(ca, zipFile, monitor);

			// Now restore the file to TMP, but cancel
			Path destination = Paths.get(TestUtilities.TMP);
			BackupUtil.restore(zipFile, destination, new TestMonitor(10));
			
			assertFalse(Files.exists(destination.resolve(CA_DESCRIPTION)));
		} finally {
			// cleanup.
			Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
			Path destination = Paths.get(TestUtilities.TMP, CA_DESCRIPTION);
			TestUtilities.cleanup(zipFile);
			TestUtilities.cleanup(destination);
		}
	}
}
