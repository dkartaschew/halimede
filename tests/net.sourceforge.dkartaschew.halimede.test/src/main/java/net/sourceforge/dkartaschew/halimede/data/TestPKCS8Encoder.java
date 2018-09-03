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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.util.ArrayList;
import java.util.Collection;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestPKCS8Encoder {

	@Parameters(name = "{0} {1}")
	public static Collection<Object[]> data() {
		Collection<Object[]> data = new ArrayList<>();
		for (EncodingType enc : EncodingType.values()) {
			for (PKCS8Cipher alg : PKCS8Cipher.values()) {
				data.add(new Object[] { enc, alg });
			}
		}
		return data;
	}

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
		Security.addProvider(new BouncyCastlePQCProvider());
	}

	private final String PASSWORD = "changeme";

	private final EncodingType type;

	private final PKCS8Cipher cipher;

	public TestPKCS8Encoder(EncodingType type, PKCS8Cipher cipher) {
		this.type = type;
		this.cipher = cipher;
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_RSA_No_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.key");
		testPCKS8(file, null, fn);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_RSA_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("rsa4096key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.key");
		testPCKS8(file, PASSWORD, fn);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_DSA_No_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.key");
		testPCKS8(file, null, fn);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_DSA_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("dsa4096key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.key");
		testPCKS8(file, PASSWORD, fn);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_EC_No_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.key");
		testPCKS8(file, null, fn);
	}

	/*
	 * Encrypted p12 in DER (no alias defined).
	 */
	@Test
	public void test_EC_Password() throws IOException, InvalidPasswordException, KeyStoreException {
		Path file = TestUtilities.getFile("ec521key.p12");
		Path fn = Paths.get(TestUtilities.TMP, "tmp.key");
		testPCKS8(file, PASSWORD, fn);
	}

	private void testPCKS8(Path file, String password, Path target)
			throws IOException, InvalidPasswordException, KeyStoreException {
		// Save
		IIssuedCertificate ic = IssuedCertificate.openPKCS12(file, null);
		ic.createPKCS8(target, password, type, cipher);

		// reload
		try {
			PKCS8Decoder pkcs8 = PKCS8Decoder.open(target, password);
			PrivateKey key = pkcs8.getKeyPair().getPrivate();
			// System.out.println(key);
			if (ic.getPrivateKey() instanceof ECPrivateKey) {
				ECPrivateKey k = (ECPrivateKey) ic.getPrivateKey();
				assertEquals(k.getS(), ((ECPrivateKey) key).getS());
			} else {
				assertEquals(ic.getPrivateKey(), key);
			}
		} finally {
			TestUtilities.delete(target);
		}
	}
}
