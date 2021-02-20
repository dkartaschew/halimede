/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2021 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.data.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Objects;

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPKCS8Generator;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.bc.BcPKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.bouncycastle.util.io.pem.PemObjectGenerator;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.IIssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.PKCS12Decoder;
import net.sourceforge.dkartaschew.halimede.data.PKCS7Decoder;
import net.sourceforge.dkartaschew.halimede.data.PKCS8Decoder;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;

public class IssuedCertificate implements IIssuedCertificate {

	/**
	 * RND Generator for keying material.
	 */
	private final static SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
	/**
	 * The Key Pair
	 */
	private final KeyPair keyPair;
	/**
	 * The public signed by us X509v3 Certificate.
	 */
	private final Certificate[] certificates;
	/**
	 * The filename of the PKCS#12 or PKCS#7 container.
	 */
	private Path certFilename;
	/**
	 * The filename of the PKCS#8 container
	 */
	private Path keyFilename;
	/**
	 * The password used to open the key material.
	 */
	private String password;

	/**
	 * The size of data to use for creating a signature from (when loading PKCS8).
	 */
	private final static int SIGNATURE_BLOCK_SIZE = 2048;

	/**
	 * Read a PKCS#12 file and decode as a single private key + certificate.
	 * 
	 * @param filename The filename of the PKCS#12 container.
	 * @param password The password required to open the container. (may be NULL if no password supplied. Note: a empty
	 *            string will be a considered a supplied password).
	 * @return An Issued Certificate instance.
	 * @throws InvalidPasswordException The password supplied to unlock the private key or PKCS#12 file was invalid.
	 * @throws IOException If opening/reading the file fails
	 * @throws KeyStoreException The keystore failed.
	 */
	public static IIssuedCertificate openPKCS12(Path filename, String password)
			throws InvalidPasswordException, IOException, KeyStoreException {
		Objects.requireNonNull(filename, "No filename provided");
		PKCS12Decoder file = PKCS12Decoder.open(filename, password);
		return new IssuedCertificate(file.getKeyPair(), file.getCertificateChain(), filename, null, password);
	}

	/**
	 * Read a PKCS#7 file and decode as a certificate.
	 * 
	 * @param filename The filename of the PKCS#7 container.
	 * @return An Issued Certificate instance, without a private key available.
	 * @throws IOException If opening/reading the file fails
	 */
	public static IIssuedCertificate openPKCS7(Path filename) throws IOException {
		PKCS7Decoder file = PKCS7Decoder.open(filename);
		return new IssuedCertificate(null, file.getCertificateChain(), filename, null, null);
	}

	/**
	 * Read a PKCS#7 file and decode as a certificate, and a PKCS#8 file and decode as a keypair.
	 * 
	 * @param certificate The filename of the certificate
	 * @param key The filename of the key
	 * @param password The password needed to access the key file. (may be NULL if no password supplied. Note: a empty
	 *            string will be a considered a supplied password).
	 * @return An Issued Certificate instance.
	 * @throws InvalidPasswordException The password supplied to unlock the private key or PKCS#8 file was invalid.
	 * @throws IllegalArgumentException If the certificate and keying material don't match.
	 * @throws IOException If opening/reading the file(s) fails
	 * @throws NoSuchAlgorithmException The algorithm for keying material checks doesn't exist
	 * @throws InvalidKeyException The keying material is invalid.
	 * @throws SignatureException The keying material is invalid.
	 */
	public static IIssuedCertificate openPKCS7_8(Path certificate, Path key, String password)
			throws InvalidPasswordException, IllegalArgumentException, IOException, NoSuchAlgorithmException,
			InvalidKeyException, SignatureException {
		PKCS7Decoder file = PKCS7Decoder.open(certificate);
		PKCS8Decoder keyfile = PKCS8Decoder.open(key, password);
		// Ensure this is a matching pair.
		Certificate[] c = file.getCertificateChain();
		PublicKey publicKey = c[0].getPublicKey();
		PublicKey pkey2 = keyfile.getKeyPair().getPublic();
		if (pkey2 != null) {
			// The key file contains both the private and public keys...
			if (!publicKey.equals(pkey2)) {
				throw new IllegalArgumentException("Keying Material doesn't match Supplied Certificate");
			}
		}

		// Now check the private key against the
		PrivateKey privateKey = keyfile.getKeyPair().getPrivate();

		/*
		 * So what we do is sign something the private key, and verify with the public...
		 */
		byte[] data = new byte[SIGNATURE_BLOCK_SIZE];
		random.nextBytes(data);

		/*
		 * Create a signature.
		 */
		SignatureAlgorithm alg = SignatureAlgorithm.getDefaultSignature(publicKey);

		Signature s = createSignature(alg);
		s.initSign(privateKey);
		s.update(data);
		byte[] signature = s.sign();

		/*
		 * Now check the signature with the public key.
		 */
		s = createSignature(alg);
		s.initVerify(publicKey);
		s.update(data);
		if (!s.verify(signature)) {
			throw new IllegalArgumentException("Keying Material doesn't match Supplied Certificate");
		}

		return new IssuedCertificate(new KeyPair(publicKey, privateKey), c, certificate, key, password);
	}

	/**
	 * Create a Signature for the given algorithm
	 * 
	 * @param alg The SignatureAlgorithm
	 * @return The Signature Instance to perform signing operations.
	 * @throws NoSuchAlgorithmException The algorithm for keying material checks doesn't exist
	 */
	private static Signature createSignature(SignatureAlgorithm alg) throws NoSuchAlgorithmException {
		Signature s = null;
		try {
			s = Signature.getInstance(alg.getAlgID(), BouncyCastleProvider.PROVIDER_NAME);
		} catch (NoSuchProviderException | NoSuchAlgorithmException e) {
			try {
				s = Signature.getInstance(alg.getAlgID());
			} catch (NoSuchAlgorithmException e1) {
				throw new NoSuchAlgorithmException("The algorithm '" + alg.getAlgID() + "' is invalid");
			}
		}
		return s;
	}

	/**
	 * Create a new IssuedCertificate Instance.
	 * 
	 * @param keyPair The private/public key pair (may be null).
	 * @param certificates The certificate chain.
	 * @param certFilename The Certificate filename
	 * @param keyFilename The key filename
	 * @param password The password used to access the keypair.
	 * @throws IllegalArgumentException If invalid Certificate was supplied.
	 * @throws NullPointerException If no certificate chain provided.
	 */
	public IssuedCertificate(KeyPair keyPair, Certificate[] certificates, Path certFilename, Path keyFilename,
			String password) {
		Objects.requireNonNull(certificates, "No Certificate supplied");
		if (certificates.length == 0) {
			throw new IllegalArgumentException("Invalid certificate or chain provided");
		}
		for (Certificate c : certificates) {
			if (c == null) {
				throw new IllegalArgumentException("Invalid certificate or chain provided");
			}
		}
		if (keyPair != null) {
			Objects.requireNonNull(keyPair.getPrivate(), "Missing Private Key from Key material");
		}
		this.keyPair = keyPair;
		this.certificates = certificates;
		this.certFilename = certFilename;
		this.keyFilename = keyFilename;
		this.password = password;
	}

	@Override
	public Certificate[] getCertificateChain() {
		return certificates;
	}

	@Override
	public PrivateKey getPrivateKey() {
		if (keyPair == null) {
			return null;
		}
		return keyPair.getPrivate();
	}

	@Override
	public PublicKey getPublicKey() {
		if (keyPair == null) {
			return certificates[0].getPublicKey();
		}
		if (keyPair.getPublic() == null) {
			return certificates[0].getPublicKey();
		}
		return keyPair.getPublic();
	}

	@Override
	public Path getCertFilename() {
		return certFilename;
	}

	@Override
	public Path getKeyFilename() {
		return keyFilename;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public void createPKCS12(Path filename, String password) throws IOException {
		createPKCS12(filename, password, null);
	}

	@Override
	public void createPKCS12(Path filename, String password, String alias) throws IOException {
		char[] pass = (password != null) ? password.toCharArray() : new char[0];
		alias = (alias == null || alias.isEmpty()) ? "1" : alias;
		try {
			/*
			 * Use the JSSE keystore implementation, as there appears to be a bug in the BC implementation not storing
			 * the entire chain is NO password.
			 */
			KeyStore p12 = KeyStore.getInstance(PKCS12Decoder.KEYSTORE);// ,BouncyCastleProvider.PROVIDER_NAME);
			p12.load(null, pass);
			p12.setKeyEntry(alias, keyPair.getPrivate(), pass, certificates);
			try (OutputStream out = new FileOutputStream(filename.toFile())) {
				p12.store(out, pass);
				out.flush();
			}
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
			throw new IOException("Failed to store information", e);
		}
	}

	@Override
	public void createPKCS12(Path filename, String password, String alias, PKCS12Cipher cipher) throws IOException {
		if (cipher == null || cipher == PKCS12Cipher.DES3 || password == null || password.isEmpty()) {
			createPKCS12(filename, password, alias);
			return;
		}
		try {
			JcePKCSPBEOutputEncryptorBuilder pbe = new JcePKCSPBEOutputEncryptorBuilder(cipher.getID())
					.setProvider(BouncyCastleProvider.PROVIDER_NAME);
			OutputEncryptor enc = pbe.build(password.toCharArray());

			JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
			/*
			 * Certificates
			 */
			PKCS12SafeBag[] certs = new PKCS12SafeBag[certificates.length];
			for (int i = 0; i < certificates.length; i++) {
				X509Certificate c = (X509Certificate) certificates[i];
				PKCS12SafeBagBuilder certBuidler = new JcaPKCS12SafeBagBuilder(c);
				if (i == 0) {
					certBuidler.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
							extUtils.createSubjectKeyIdentifier(getPublicKey()));
				}
				certs[i] = certBuidler.build();
			}
			/*
			 * Private Key
			 */
			PKCS12SafeBagBuilder keyBagBuilder = new JcaPKCS12SafeBagBuilder(keyPair.getPrivate(), enc)//
					.addBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId,
							extUtils.createSubjectKeyIdentifier(getPublicKey()));

			/*
			 * construct the actual key store
			 */
			PKCS12PfxPduBuilder pfxPduBuilder = new PKCS12PfxPduBuilder();
			pfxPduBuilder.addEncryptedData(enc, certs);
			pfxPduBuilder.addData(keyBagBuilder.build());

			PKCS12PfxPdu pfx = pfxPduBuilder.build(new BcPKCS12MacCalculatorBuilder(), password.toCharArray());
			try (OutputStream out = new FileOutputStream(filename.toFile())) {
				out.write(pfx.getEncoded());
				out.flush();
			}
		} catch (NoSuchAlgorithmException | PKCSException | OperatorCreationException e) {
			throw new IOException("Failed to store information", e);
		}
	}

	@Override
	public void createCertificate(Path filename, EncodingType encoding) throws IOException {
		try (OutputStream out = new FileOutputStream(filename.toFile())) {
			try {
				switch (encoding) {
				case PEM:
					try (JcaPEMWriter writer = new JcaPEMWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));) {
						writer.writeObject(certificates[0]);
					}
					break;
				case DER:
				default:
					out.write(certificates[0].getEncoded());
					out.flush();
					break;
				}
			} catch (CertificateEncodingException e) {
				throw new IOException("Failed to store information", e);
			}
		}
	}

	@Override
	public void createCertificateChain(Path filename, EncodingType encoding) throws IOException {
		try (OutputStream out = new FileOutputStream(filename.toFile())) {
			switch (encoding) {
			case PEM:
				try (JcaPEMWriter writer = new JcaPEMWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));) {
					for (Certificate c : certificates) {
						writer.writeObject(c);
					}
				}
				break;
			case DER:
			default:
				CMSTypedData msg = new CMSProcessableByteArray(PluginDefaults.ID.getBytes(StandardCharsets.UTF_8));
				try {
					CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
					gen.addCertificates(new JcaCertStore(Arrays.asList(certificates)));
					CMSSignedData sigData = gen.generate(msg, false);
					out.write(sigData.getEncoded());
					out.flush();
				} catch (CertificateEncodingException | CMSException e) {
					throw new IOException("Failed to store information", e);
				}
				break;
			}

		}
	}

	@Override
	public void createPKCS8(Path filename, String password, EncodingType encoding, PKCS8Cipher encryptionAlg)
			throws IOException, IllegalStateException {
		Objects.requireNonNull(encoding, "No Encoding provided");
		Objects.requireNonNull(encryptionAlg, "No Cipher provided");
		char[] pass = (password != null) ? password.toCharArray() : new char[0];
		if (keyPair == null || keyPair.getPrivate() == null) {
			throw new IllegalStateException("No Private Key present");
		}
		PrivateKey key = keyPair.getPrivate();
		try (OutputStream out = new FileOutputStream(filename.toFile())) {
			PemObjectGenerator gen;
			if (password != null) {
				try {
					JceOpenSSLPKCS8EncryptorBuilder encryptorBuilder = new JceOpenSSLPKCS8EncryptorBuilder(
							encryptionAlg.getID());
					encryptorBuilder.setProvider(BouncyCastleProvider.PROVIDER_NAME);
					encryptorBuilder.setPasssword(pass);

					OutputEncryptor oe = encryptorBuilder.build();
					gen = new JcaPKCS8Generator(key, oe);
				} catch (OperatorCreationException e) {
					throw new IOException("Cannot setup encryption modules", e);
				}
			} else {
				gen = new JcaPKCS8Generator(key, null);
			}

			if (encoding == EncodingType.PEM) {
				try (JcaPEMWriter writer = new JcaPEMWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));) {
					writer.writeObject(gen);
					writer.flush();
				}
			} else {
				if (password == null) {
					out.write(key.getEncoded());
				} else {
					out.write(gen.generate().getContent());
				}
				out.flush();
			}
		}
	}

	@Override
	public void createPublicKey(Path filename, EncodingType encoding) throws IOException {
		try (OutputStream out = new FileOutputStream(filename.toFile())) {
			switch (encoding) {
			case PEM:
				try (JcaPEMWriter writer = new JcaPEMWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));) {
					writer.writeObject(getPublicKey());
				}
				break;
			case DER:
			default:
				out.write(getPublicKey().getEncoded());
				out.flush();
				break;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (certFilename != null) {
			Path filename = certFilename.getFileName();
			if (filename != null) {
				sb.append(filename.toString());
				sb.append(" ");
			}
		}
		if (certificates[0] instanceof X509Certificate) {
			sb.append("[");
			sb.append(((X509Certificate) certificates[0]).getSubjectDN().getName());
			sb.append("]");
		}
		if (sb.length() > 1) {
			return sb.toString();
		}
		return "Certificate: " + certificates[0].getType();
	}
}
