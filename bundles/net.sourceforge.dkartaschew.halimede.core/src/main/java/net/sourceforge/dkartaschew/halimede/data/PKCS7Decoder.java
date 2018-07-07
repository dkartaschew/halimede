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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.X509TrustedCertificateBlock;
import org.bouncycastle.util.Store;

/**
 * A PKCS7 decoder, or simple X509 Certificate decoder.
 * <p>
 * This decoder attempts to handle either DER, or PEM encodings automagically.
 * <p>
 * This decoder looks for and returns the first X509 Certificate found.
 * <p>
 * This decoder is named PKCS7, as PKCS7 is the container format for client X509 certificates.
 */
public class PKCS7Decoder {

	/*
	 * Setup BC crypto provider.
	 */
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	
	/**
	 * The certificate.
	 */
	private final Certificate[] certificates;

	public static PKCS7Decoder open(Path filename) throws IOException {
		try (PEMParser pemParser = new PEMParser(new InputStreamReader(
				new FileInputStream(filename.toFile()), StandardCharsets.UTF_8))) {
			Object object = pemParser.readObject();
			if (object == null) {
				// May be plain DER without enough info for PEMParser

				// Try simple X509 certificate.
				try (FileInputStream fis = new FileInputStream(filename.toFile());) {
					BufferedInputStream bis = new BufferedInputStream(fis);

					CertificateFactory cf = CertificateFactory.getInstance("X.509", //
							BouncyCastleProvider.PROVIDER_NAME);
					Collection<? extends Certificate> certsList = cf.generateCertificates(bis);
//					List<Certificate> certs = new ArrayList<>();
//					certs.addAll(certsList);
//					Collections.reverse(certs);
					return new PKCS7Decoder(certsList.toArray(new Certificate[certsList.size()]));
				} catch (CertificateException | NoSuchProviderException e) {
					// throw new IOException("Parsing of file failed", e);
				}
				// Retry with DER PKCS7
				byte[] data = Files.readAllBytes(filename);
				try {
					List<Certificate> certificates = new ArrayList<>();
					CMSSignedData cms = new CMSSignedData(data);
					Store<?> certs = cms.getCertificates();
					Collection<?> collection = certs.getMatches(null);
					parseCMSStore(certificates, collection);
					if (certificates.isEmpty()) {
						throw new IOException("Parsing of file failed - no elements");
					}
					return new PKCS7Decoder(certificates.toArray(new Certificate[certificates.size()]));
				} catch (CMSException e) {
					throw new IOException("Parsing of file failed", e);
				}
			}

			List<Certificate> certificates = new ArrayList<>();
			while (object != null) {
				if (object instanceof X509CertificateHolder) {
					try {
						certificates
								.add(new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
										.getCertificate((X509CertificateHolder) object));
					} catch (CertificateException e) {
						throw new IOException("Parsing of certificate information failed", e);
					}
				} else if (object instanceof X509TrustedCertificateBlock) {
					try {
						certificates
								.add(new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
										.getCertificate(((X509TrustedCertificateBlock) object).getCertificateHolder()));
					} catch (CertificateException e) {
						throw new IOException("Parsing of certificate information failed", e);
					}
				} else if (object instanceof ContentInfo) {
					try {
						CMSSignedData cms = new CMSSignedData((ContentInfo) object);
						Store<?> certs = cms.getCertificates();
						Collection<?> collection = certs.getMatches(null);
						parseCMSStore(certificates, collection);
					} catch (CMSException e) {
						throw new IOException("Parsing of certificate information failed", e);
					}

				} else {
					throw new UnsupportedEncodingException("Unhandled class: " + object.getClass().getName());
				}
				object = pemParser.readObject();
			}
			if (certificates.isEmpty()) {
				throw new IOException("Parsing of file failed - no elements");
			}
			return new PKCS7Decoder(certificates.toArray(new Certificate[certificates.size()]));
		}
	}

	/**
	 * Parse the collection of objects, and add to the Certificates List.
	 * 
	 * @param certificates The List of certificates to add to.
	 * @param collection The collection of objects to parse
	 * @throws IOException
	 */
	private static void parseCMSStore(List<Certificate> certificates, Collection<?> collection) throws IOException {
		for (Object o : collection) {
			if (o instanceof X509CertificateHolder) {
				try {
					certificates.add(new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
							.getCertificate((X509CertificateHolder) o));
				} catch (CertificateException e) {
					throw new IOException("Parsing of certificate information failed", e);
				}
			} else if (o instanceof X509TrustedCertificateBlock) {
				try {
					certificates.add(new JcaX509CertificateConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
							.getCertificate(((X509TrustedCertificateBlock) o).getCertificateHolder()));
				} catch (CertificateException e) {
					throw new IOException("Parsing of certificate information failed", e);
				}
			}
		}
	}

	/**
	 * Create a new instance.
	 * 
	 * @param certificate The certificate to present.
	 */
	private PKCS7Decoder(Certificate[] certificates) {
		this.certificates = certificates;
	}

	/**
	 * Get the found certificate.
	 * 
	 * @return The found certificate.
	 */
	public Certificate[] getCertificateChain() {
		return certificates;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(certificates);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PKCS7Decoder other = (PKCS7Decoder) obj;
		if (!Arrays.equals(certificates, other.certificates))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PKCS7Decoder [certificate=" + certificates[0] + "]";
	}
}
