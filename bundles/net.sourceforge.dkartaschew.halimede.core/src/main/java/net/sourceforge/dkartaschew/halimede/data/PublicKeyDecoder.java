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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Hashtable;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;

import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class PublicKeyDecoder {

	private static Hashtable<ASN1ObjectIdentifier, String> keyAlgorithms = new Hashtable<>();

	/*
	 * Setup BC crypto provider.
	 */
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		//
		// key types
		//
		keyAlgorithms.put(PKCSObjectIdentifiers.rsaEncryption, "RSA");
		keyAlgorithms.put(X9ObjectIdentifiers.id_dsa, "DSA");
	}

	/**
	 * Create a new public key
	 * 
	 * @param filename The filename of the public key
	 * @return A CertificateRequest instance, with certificate and signing information available.
	 * @throws IOException The file was unable to be read.
	 */
	public static PublicKey open(Path filename) throws IOException {
		Objects.requireNonNull(filename, "Filename was null");
		try (PEMParser pemParser = new PEMParser(new InputStreamReader(
				new FileInputStream(filename.toFile()), StandardCharsets.UTF_8))) {
			Object object = pemParser.readObject();
			if (object == null) {
				// May be plain DER without enough info for PEMParser
				return attemptDER(filename);
			}
			if (object instanceof PublicKey) {
				return (PublicKey) object;
			} else if (object instanceof SubjectPublicKeyInfo) {
				SubjectPublicKeyInfo info = (SubjectPublicKeyInfo) object;
				return getPublicKey(info);
			} else {
				throw new UnsupportedEncodingException("Unhandled class: " + object.getClass().getName());
			}
		} catch (Throwable e) {
			if (e instanceof IOException) {
				IOException ioe = (IOException) e;
				throw ioe;
			}
			throw new IOException(e);
		}
	}

	/**
	 * Attempt to read the file as straight DER
	 * 
	 * @param file The file to read
	 * @return A CertificateRequest instance, with certificate and signing information available.
	 * @throws IOException The file was unable to be read.
	 */
	private static PublicKey attemptDER(Path file) throws IOException {
		byte[] data = Files.readAllBytes(file);
		try {
			return getPublicKey(SubjectPublicKeyInfo.getInstance(data));
		} catch (Throwable e) {
			// NOP
		}
		try {
			AsymmetricKeyParameter p = PublicKeyFactory.createKey(data);
			System.out.println(p);
			// TODO: IMPLEMENT
		} catch (Throwable e) {
			// NOP
		}
		throw new IOException("Unable to decode");
	}

	/**
	 * Get the public Key from the given SubjectPublicKeyInfo item.
	 * 
	 * @param keyInfo The SubjectPublicKeyInfo
	 * @return The Public Key
	 * @throws InvalidKeyException The keying type is not supported.
	 * @throws NoSuchAlgorithmException The keying type is not supported.
	 */
	public static PublicKey getPublicKey(SubjectPublicKeyInfo keyInfo)
			throws InvalidKeyException, NoSuchAlgorithmException {
		Objects.requireNonNull(keyInfo, "SubjectPublicKeyInfo was null");
		try {
			X509EncodedKeySpec xspec = new X509EncodedKeySpec(keyInfo.getEncoded());
			KeyFactory kFact;
			JcaJceHelper helper = new DefaultJcaJceHelper();
			try {
				kFact = helper.createKeyFactory(keyInfo.getAlgorithm().getAlgorithm().getId());
			} catch (NoSuchAlgorithmException e) {
				//
				// try an alternate
				//
				if (keyAlgorithms.get(keyInfo.getAlgorithm().getAlgorithm()) != null) {
					String keyAlgorithm = (String) keyAlgorithms.get(keyInfo.getAlgorithm().getAlgorithm());

					kFact = helper.createKeyFactory(keyAlgorithm);
				} else {
					throw e;
				}
			}

			return kFact.generatePublic(xspec);
		} catch (InvalidKeySpecException e) {
			throw new InvalidKeyException("Error decoding public key");
		} catch (IOException e) {
			throw new InvalidKeyException("Error extracting key encoding");
		} catch (NoSuchProviderException e) {
			throw new NoSuchAlgorithmException("Cannot find provider: " + ExceptionUtil.getMessage(e));
		}
	}

}
