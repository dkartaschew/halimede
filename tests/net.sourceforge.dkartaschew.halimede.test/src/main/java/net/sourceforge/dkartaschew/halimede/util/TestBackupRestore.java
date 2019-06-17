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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.CertificateEncodingException;
import java.util.UUID;

import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;

/**
 * Test Backup and restore
 */
public class TestBackupRestore {

	private final String CA_DESCRIPTION = "My CA";

	@Test
	public void basicBackup() throws SecurityException, IOException, CertificateEncodingException {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);

		Path zipFile = Paths.get(TestUtilities.TMP, "CA.zip");
		BackupUtil.backup(ca, zipFile, (s, i, t) -> {
			System.out.println(String.format("Backup %s : %d / %d", s, i, t));
			return true;
		});
		
		// Now restore the file to TMP.
		Path destination = Paths.get(TestUtilities.TMP);
		BackupUtil.restore(zipFile, destination, (s, i, t) -> {
			System.out.println(String.format("Restore %s : %d / %d", s, i, t));
			return true;
		});
	}

}
