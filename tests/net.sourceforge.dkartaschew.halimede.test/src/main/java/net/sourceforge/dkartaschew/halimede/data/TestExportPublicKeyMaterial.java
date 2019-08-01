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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.AfterClass;
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
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.random.NotSecureRandom;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Parameterized.class)
public class TestExportPublicKeyMaterial {

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
	
	@Parameters(name = "{0}")
	public static Collection<KeyType> data() {
		KeyTypeWarningValidator v = new KeyTypeWarningValidator();
		
		// Only do for keying material of 2048 bits or less.
		Collection<KeyType> data = Arrays.stream(KeyType.values())//
				.filter(key -> (v.validate(key) == ValidationStatus.ok()))//
				.collect(Collectors.toList());
		return data;
	}

	private final Path fn = Paths.get(TestUtilities.TMP, "key.pub");
	private final KeyType type;

	public TestExportPublicKeyMaterial(KeyType type) {
		this.type = type;
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testKeyGenerateDER() throws Exception {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		IssuedCertificate ic = new IssuedCertificate(key, new Certificate[] { new MockCertificate("Mock") }, null, null, null);
		ic.createPublicKey(fn, EncodingType.DER);
		reloadAndCompare(key, fn, ic);
	}

	/*
	 * Key Generation in DER (no alias defined).
	 */
	@Test
	public void testKeyGeneratePEM() throws IOException, InvalidPasswordException, KeyStoreException,
			NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		KeyPair key = KeyPairFactory.generateKeyPair(type);
		IssuedCertificate ic = new IssuedCertificate(key, new Certificate[] { new MockCertificate("Mock") }, null, null, null);
		ic.createPublicKey(fn, EncodingType.PEM);
		reloadAndCompare(key, fn, ic);
	}

	
	/**
	 * Save and reload the keying material
	 * 
	 * @param key The key to save/restore
	 * @param fn The filename to use
	 * @param ic The IssuedCertificate Instance.
	 * @throws InvalidPasswordException Bad Password
	 * @throws IOException Unable to save/restore.
	 */
	private void reloadAndCompare(KeyPair key, Path fn, IssuedCertificate ic)
			throws InvalidPasswordException, IOException {
		// reload
		try {
			PublicKey pkey = PublicKeyDecoder.open(fn);
			assertEquals(ic.getPublicKey(), pkey);
		} finally {
			TestUtilities.delete(fn);
		}
	}

}
