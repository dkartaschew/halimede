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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;

public class PKCS12Decoder {

	/*
	 * Setup BC crypto provider.
	 */
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		if (Security.getProvider(BouncyCastlePQCProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastlePQCProvider());
		}
	}
	
	/**
	 * The keystore type
	 */
	public final static String KEYSTORE = "PKCS12";
	/**
	 * The certificate.
	 */
	private final Certificate[] certificates;
	/**
	 * The keypair.
	 */
	private final KeyPair keypair;

	/**
	 * Create a new PKCS12 instance.
	 * 
	 * @param filename The filename of the PKCS12 container.
	 * @param password The password. (May be null)
	 * @return A PKCS12 instance, with the Certificate and keypair available.
	 * @throws InvalidPasswordException The password was not valid.
	 * @throws KeyStoreException Creating the underlying Java Keystore failed.
	 * @throws IOException Reading the file failed.
	 */
	public static PKCS12Decoder open(Path filename, String password)
			throws InvalidPasswordException, KeyStoreException, IOException {
		KeyStore p12;
		try {
			p12 = KeyStore.getInstance(KEYSTORE, BouncyCastleProvider.PROVIDER_NAME);
		} catch (NoSuchProviderException e){
			p12 = KeyStore.getInstance(KEYSTORE);
		}
		char[] pass = (password != null) ? password.toCharArray() : new char[0];
		try (FileInputStream fis = new FileInputStream(filename.toFile())) {
			p12.load(fis, pass);
			Enumeration<String> e = p12.aliases();
			while (e.hasMoreElements()) {
				String alias = (String) e.nextElement();
				Certificate[] c = p12.getCertificateChain(alias);
				Key key = p12.getKey(alias, pass);
				if (key instanceof PrivateKey) {
					return new PKCS12Decoder(c, new KeyPair(c[0].getPublicKey(), (PrivateKey) key));
				}
			}
		} catch (Throwable e) {
			if (e instanceof IOException) {
				String m = e.getMessage();
				if (m.contains("password")) {
					throw new InvalidPasswordException(m);
				}
				IOException ioe = (IOException) e;
				throw ioe;
			}
			throw new IOException(e);
		}
		throw new IOException("Empty PKCS12 store.");
	}

	/**
	 * Create a new instance.
	 * 
	 * @param certificates The certificate to present.
	 * @param keypair The keypair.
	 */
	private PKCS12Decoder(Certificate[] certificates, KeyPair keypair) {
		this.certificates = certificates;
		this.keypair = keypair;
	}

	/**
	 * Get the found certificate.
	 * 
	 * @return The found certificate.
	 */
	public Certificate[] getCertificateChain() {
		return certificates;
	}

	/**
	 * Get the found keypair
	 * 
	 * @return The found keypair.
	 */
	public KeyPair getKeyPair() {
		return keypair;
	}

}
