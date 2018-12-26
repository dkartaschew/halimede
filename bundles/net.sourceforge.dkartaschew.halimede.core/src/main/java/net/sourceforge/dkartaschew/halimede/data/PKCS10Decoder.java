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
import java.security.Security;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;

public class PKCS10Decoder {

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
	 * Create a new private/public key pair
	 * 
	 * @param filename The filename of the keypair
	 * @return A CertificateRequest instance, with certificate and signing information available.
	 * @throws IOException The file was unable to be read.
	 */
	public static ICertificateRequest open(Path filename) throws IOException {
		try (PEMParser pemParser = new PEMParser(new InputStreamReader(
				new FileInputStream(filename.toFile()), StandardCharsets.UTF_8))) {
			Object object = pemParser.readObject();
			if (object == null) {
				// May be plain DER without enough info for PEMParser
				return attemptDER(filename);
			}
			if (object instanceof PKCS10CertificationRequest) {
				// Encrypted key - we will use provided password
				PKCS10CertificationRequest ckp = (PKCS10CertificationRequest) object;
				return new CertificateRequestPKCS10(ckp);
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
	private static ICertificateRequest attemptDER(Path file) throws IOException {
		byte[] data = Files.readAllBytes(file);
		try (ASN1InputStream input = new ASN1InputStream(data)) {
			ASN1Primitive p;
			while ((p = input.readObject()) != null) {
				// Simple Private Key
				try {
					PKCS10CertificationRequest req = new PKCS10CertificationRequest(p.getEncoded());
					return new CertificateRequestPKCS10(req);
				} catch (Throwable e) {
					// NOP
				}
			}
		}
		throw new IOException("Unable to decode");
	}

}
