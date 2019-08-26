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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.PKCSIOException;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

/**
 * A PKCS8/PKCS1 decoder
 * <p>
 * This decoder attempts to handle either DER, or PEM encodings automagically.
 * <p>
 * This decoder looks for and returns the first private key found.
 * <p>
 * This decoder is named PKCS8, as PKCS8 is the container format for client private keys, which should be used instead
 * of PKCS1. (PKCS1 doesn't support pass phrase protection when using DER).
 */
public class PKCS8Decoder {

	/*
	 * Setup BC crypto provider.
	 */
	static {
		ProviderUtil.setupProviders();
	}

	/**
	 * The keypair.
	 */
	private final KeyPair keypair;

	/**
	 * Create a new private/public key pair
	 * 
	 * @param filename The filename of the keypair
	 * @param password The password. (May be null)
	 * @return A PKCS8 instance, with keypair available.
	 * @throws InvalidPasswordException The password supplied is invalid.
	 * @throws IOException The file was unable to be read.
	 */
	public static PKCS8Decoder open(Path filename, String password) throws InvalidPasswordException, IOException {
		char[] pass = (password != null) ? password.toCharArray() : new char[0];
		try (PEMParser pemParser = new PEMParser(
				new InputStreamReader(new FileInputStream(filename.toFile()), StandardCharsets.UTF_8))) {
			Object object = pemParser.readObject();
			if (object == null) {
				// May be plain DER without enough info for PEMParser
				return attemptDER(filename, pass);
			}

			if (object instanceof ASN1ObjectIdentifier) {
				// OpenSSL will emit EC Parameters, which can be ignored.
				ASN1ObjectIdentifier obj = (ASN1ObjectIdentifier) object;
				if (obj.getId().equals("1.3.132.0.35")) {
					object = pemParser.readObject();
				}
			}
			if (object instanceof PEMEncryptedKeyPair) {
				// Encrypted key - we will use provided password
				PEMEncryptedKeyPair ckp = (PEMEncryptedKeyPair) object;
				try {
					return tryEncryptedPEMKeyPair(ckp, pass, BouncyCastleProvider.PROVIDER_NAME);
				} catch (PEMException e) {
					return tryEncryptedPEMKeyPair(ckp, pass, BouncyCastlePQCProvider.PROVIDER_NAME);
				}

			} else if (object instanceof PrivateKeyInfo) {
				PrivateKeyInfo privKeyInfo = (PrivateKeyInfo) object;
				try {
					return tryPEMPrivateKey(privKeyInfo, BouncyCastleProvider.PROVIDER_NAME);
				} catch (PEMException e) {
					return tryPEMPrivateKey(privKeyInfo, BouncyCastlePQCProvider.PROVIDER_NAME);
				}
			} else if (object instanceof PEMKeyPair) {
				PEMKeyPair ukp = (PEMKeyPair) object;
				try {
					return tryPEMKeyPair(ukp, BouncyCastleProvider.PROVIDER_NAME);
				} catch (PEMException e) {
					return tryPEMKeyPair(ukp, BouncyCastlePQCProvider.PROVIDER_NAME);
				}

			} else if (object instanceof PKCS8EncryptedPrivateKeyInfo) {
				PKCS8EncryptedPrivateKeyInfo pkcs8 = (PKCS8EncryptedPrivateKeyInfo) object;
				try {
					return tryEncryptedPEMPrivateKey(pkcs8, pass, BouncyCastleProvider.PROVIDER_NAME);
				} catch (PEMException | PKCSException e) {
					return tryEncryptedPEMPrivateKey(pkcs8, pass, BouncyCastlePQCProvider.PROVIDER_NAME);
				}
			} else if (object instanceof ContentInfo) {
				throw new IOException("Unexpected PKCS#7 CMS Data?");
			} else {
				throw new UnsupportedEncodingException("Unhandled class: " + object.getClass().getName());
			}

		} catch (Throwable e) {
			if (e instanceof IOException || e instanceof PKCSException) {
				String m = e.getMessage();
				if (m.contains("password") || m.contains("secret") || m.contains("corrupted")) {
					throw new InvalidPasswordException(m);
				}
				if (e instanceof IOException) {
					IOException ioe = (IOException) e;
					throw ioe;
				}
			}
			if (e instanceof InvalidPasswordException) {
				InvalidPasswordException ioe = (InvalidPasswordException) e;
				throw ioe;
			}
			throw new IOException(e);
		}
	}

	/**
	 * Attempt to decode PEM Encrypted Private Key
	 * 
	 * @param pkcs8 The encrypted private key
	 * @param pass The password
	 * @param providerName The provider
	 * @return A PKCS8 Decoder if valid
	 * @throws PEMException PEM Exception
	 * @throws PKCSException PKCS Exception
	 */
	private static PKCS8Decoder tryEncryptedPEMPrivateKey(PKCS8EncryptedPrivateKeyInfo pkcs8, char[] pass,
			String providerName) throws PKCSException, PEMException {
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(providerName);
		JcePKCSPBEInputDecryptorProviderBuilder jce = new JcePKCSPBEInputDecryptorProviderBuilder();
		jce.setProvider(BouncyCastleProvider.PROVIDER_NAME);
		PrivateKeyInfo privKeyInfo = pkcs8.decryptPrivateKeyInfo(jce.build(pass));
		KeyPair kp = new KeyPair(null, converter.getPrivateKey(privKeyInfo));
		return new PKCS8Decoder(kp);
	}

	/**
	 * Attempt to decode PEM Private/Public Key Pair
	 * 
	 * @param ukp The key info
	 * @param providerName The provider
	 * @return A PKCS8 Decoder if valid
	 * @throws PEMException PEM Exception
	 */
	private static PKCS8Decoder tryPEMKeyPair(PEMKeyPair ukp, String providerName) throws PEMException {
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(providerName);
		return new PKCS8Decoder(converter.getKeyPair(ukp));
	}

	/**
	 * Attempt to decode PEM Private Key
	 * 
	 * @param privKeyInfo The key info
	 * @param providerName The provider
	 * @return A PKCS8 Decoder if valid
	 * @throws PEMException PEM Exception
	 */
	private static PKCS8Decoder tryPEMPrivateKey(PrivateKeyInfo privKeyInfo, String providerName) throws PEMException {
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(providerName);
		return new PKCS8Decoder(new KeyPair(null, converter.getPrivateKey(privKeyInfo)));
	}

	/**
	 * Attempt to decode PEM Encrypted Key Pair
	 * 
	 * @param ckp The encrypted key pair
	 * @param pass The password
	 * @param providerName The provider
	 * @return A PKCS8 Decoder if valid
	 * @throws PEMException PEM Exception
	 * @throws IOException IO Exception
	 */
	private static PKCS8Decoder tryEncryptedPEMKeyPair(PEMEncryptedKeyPair ckp, char[] pass, String providerName)
			throws PEMException, IOException {
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(providerName);
		PEMDecryptorProvider decProv = new JcePEMDecryptorProviderBuilder()
				.setProvider(BouncyCastleProvider.PROVIDER_NAME).build(pass);
		return new PKCS8Decoder(converter.getKeyPair(ckp.decryptKeyPair(decProv)));
	}

	/**
	 * Attempt to read the file as straight DER
	 * 
	 * @param file The file to read
	 * @param pass The password to try
	 * @return A PKCS8 instance, with keypair available.
	 * @throws InvalidPasswordException The password supplied is invalid.
	 * @throws IOException The file was unable to be read.
	 */
	private static PKCS8Decoder attemptDER(Path file, char[] pass) throws InvalidPasswordException, IOException {
		byte[] data = Files.readAllBytes(file);
		try (ASN1InputStream input = new ASN1InputStream(data);) {
			ASN1Primitive p;
			while ((p = input.readObject()) != null) {
				// Simple Private Key
				try {
					PrivateKeyInfo info = PrivateKeyInfo.getInstance(ASN1Sequence.getInstance(p));
					return tryPEMPrivateKey(info, BouncyCastleProvider.PROVIDER_NAME);
				} catch (Throwable e) {
					// NOP
					try {
						PrivateKeyInfo info = PrivateKeyInfo.getInstance(ASN1Sequence.getInstance(p));
						return tryPEMPrivateKey(info, BouncyCastlePQCProvider.PROVIDER_NAME);
					} catch (Throwable e2) {
						// NOP
					}
				}
				// Encrypted PKCS8
				try {
					PKCS8EncryptedPrivateKeyInfo info = new PKCS8EncryptedPrivateKeyInfo(p.getEncoded());
					JcePKCSPBEInputDecryptorProviderBuilder jce = new JcePKCSPBEInputDecryptorProviderBuilder();
					jce.setProvider(BouncyCastleProvider.PROVIDER_NAME);
					PrivateKeyInfo privKeyInfo = info.decryptPrivateKeyInfo(jce.build(pass));
					try {
						JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
						return new PKCS8Decoder(new KeyPair(null, converter.getPrivateKey(privKeyInfo)));
					} catch (Throwable e) {
						JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastlePQCProvider.PROVIDER_NAME);
						return new PKCS8Decoder(new KeyPair(null, converter.getPrivateKey(privKeyInfo)));
					}
				} catch (Throwable e) {
					if (e instanceof IOException || e instanceof PKCSException) {
						String m = e.getMessage();
						if (m.contains("password") || m.contains("secret") || m.contains("corrupted")) {
							throw new InvalidPasswordException(m);
						}
						if (e instanceof IOException && !(e instanceof PKCSIOException)) {
							IOException ioe = (IOException) e;
							throw ioe;
						}
					}
				}
				// PCKS1 RSA private key
				try {
					KeyPair kp = tryPKCS1RSA(ASN1Sequence.getInstance(p));
					return new PKCS8Decoder(kp);
				} catch (Throwable e) {

				}
				// PCKS1 DSA private key
				try {
					KeyPair kp = tryPKCS1DSA(ASN1Sequence.getInstance(p));
					return new PKCS8Decoder(kp);
				} catch (Throwable e) {

				}
				// PCKS1 EC private key
				try {
					KeyPair kp = tryPKCS1EC(ASN1Sequence.getInstance(p));
					return new PKCS8Decoder(kp);
				} catch (Throwable e) {

				}
			}
		}
		throw new IOException("Unable to decode");
	}

	/**
	 * Try decoding the ASN1 sequence as a PKCS1 RSA private key.
	 * <p>
	 * The internal code is based on RSAKeyPairParser from BouncyCastle.
	 * 
	 * @param seq The ASN1 sequence
	 * @return A KeyPair
	 * @throws IOException If decoding the key failed.
	 */
	private static KeyPair tryPKCS1RSA(ASN1Sequence seq) throws IOException {
		try {
			if (seq.size() != 9) {
				throw new IOException("malformed sequence in RSA private key");
			}

			RSAPrivateKey keyStruct = RSAPrivateKey.getInstance(seq);
			RSAPublicKey pubSpec = new RSAPublicKey(keyStruct.getModulus(), keyStruct.getPublicExponent());
			AlgorithmIdentifier algId = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
			return new KeyPair(converter.getPublicKey(new SubjectPublicKeyInfo(algId, pubSpec)),
					converter.getPrivateKey(new PrivateKeyInfo(algId, keyStruct)));
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("problem creating RSA private key: " + ExceptionUtil.getMessage(e), e);
		}
	}

	/**
	 * Try decoding the ASN1 sequence as a PKCS1 DSA private key.
	 * <p>
	 * The internal code is based on DSAKeyPairParser from BouncyCastle.
	 * 
	 * @param seq The ASN1 sequence
	 * @return A KeyPair
	 * @throws IOException If decoding the key failed.
	 */
	private static KeyPair tryPKCS1DSA(ASN1Sequence seq) throws IOException {
		try {

			if (seq.size() != 6) {
				throw new IOException("malformed sequence in DSA private key");
			}

			// ASN1Integer v = (ASN1Integer)seq.getObjectAt(0);
			ASN1Integer p = ASN1Integer.getInstance(seq.getObjectAt(1));
			ASN1Integer q = ASN1Integer.getInstance(seq.getObjectAt(2));
			ASN1Integer g = ASN1Integer.getInstance(seq.getObjectAt(3));
			ASN1Integer y = ASN1Integer.getInstance(seq.getObjectAt(4));
			ASN1Integer x = ASN1Integer.getInstance(seq.getObjectAt(5));
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
			return new KeyPair(
					converter.getPublicKey(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa,
							new DSAParameter(p.getValue(), q.getValue(), g.getValue())), y)),
					converter.getPrivateKey(new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa,
							new DSAParameter(p.getValue(), q.getValue(), g.getValue())), x)));
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("problem creating DSA private key: " + ExceptionUtil.getMessage(e), e);
		}
	}

	/**
	 * Try decoding the ASN1 sequence as a PKCS1 EC private key.
	 * <p>
	 * The internal code is based on ECDSAKeyPairParser from BouncyCastle.
	 * 
	 * @param seq The ASN1 sequence
	 * @return A KeyPair
	 * @throws IOException If decoding the key failed.
	 */
	private static KeyPair tryPKCS1EC(ASN1Sequence seq) throws IOException {
		try {
			org.bouncycastle.asn1.sec.ECPrivateKey pKey = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(seq);
			AlgorithmIdentifier algId = new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey,
					pKey.getParameters());
			PrivateKeyInfo privInfo = new PrivateKeyInfo(algId, pKey);
			SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(algId, pKey.getPublicKey().getBytes());

			JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME);
			return new KeyPair(converter.getPublicKey(pubInfo), converter.getPrivateKey(privInfo));
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException("problem creating EC private key: " + ExceptionUtil.getMessage(e), e);
		}
	}

	/**
	 * Create a new instance.
	 * 
	 * @param keypair The keypair.
	 */
	private PKCS8Decoder(KeyPair keypair) {
		this.keypair = keypair;
	}

	/**
	 * Get the found key pair
	 * 
	 * @return The found key pair.
	 */
	public KeyPair getKeyPair() {
		return keypair;
	}

}
