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

package net.sourceforge.dkartaschew.halimede.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.UUID;

import org.bouncycastle.asn1.x500.X500Name;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;

public class TestCertificateUtil {

	private final String PASSWORD = "changeme";
	private final String CA_DESCRIPTION = "My CA";

	private X500Name issuer = new X500Name("C=AU,ST=Queensland,L=Gold Coast,O=Internet Widgits Pty Ltd,CN=CA Manager");
	
	@Test
	public void testObject() {
		CertificateUtil st = new CertificateUtil();
		assertEquals(st.hashCode(), st.hashCode());
		st.toString();
		assertEquals(st, st);
	}
	
	@Test
	public void testNullCA() {
		assertNull(CertificateUtil.getIssuers(null));
	}
	
	@Test
	public void testLockedCA() throws CertificateEncodingException, IOException {
		CertificateAuthority ca = loadCA();
		assertNull(CertificateUtil.getIssuers(ca));
	}
	
	@Test
	public void testCA() throws CertificateEncodingException, IOException, KeyStoreException, InvalidPasswordException {
		CertificateAuthority ca = loadCA();
		ca.unlock(PASSWORD);
		assertFalse(ca.isLocked());
		Collection<X500Name> names = CertificateUtil.getIssuers(ca);
		assertEquals(1, names.size());
		X500Name n = names.iterator().next();
		assertEquals(issuer, n);
	}
	
	private CertificateAuthority loadCA() throws IOException, CertificateEncodingException {
		Path path = TestUtilities.getFolder("CA");
		CertificateAuthority ca = CertificateAuthority.open(path);
		assertEquals(ca.getCertificateAuthorityID(), UUID.fromString("7779894e-226f-4230-81ab-612c4387abff"));
		assertTrue(ca.isLocked());
		assertEquals(ca.getDescription(), CA_DESCRIPTION);
		return ca;
	}
}
