/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.Collection;

import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.util.Store;

import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

public class X509CRLEncoder {

	/*
	 * Setup BC crypto provider.
	 */
	static {
		ProviderUtil.setupProviders();
	}

	public static X509CRL open(Path filename) throws IOException {
		try (PEMParser pemParser = new PEMParser(new InputStreamReader(
				Files.newInputStream(filename), StandardCharsets.UTF_8))) {
			Object object = pemParser.readObject();
			if (object == null) {
				// May be plain DER without enough info for PEMParser

				// Try simple X509 CRL .
				try (InputStream fis = Files.newInputStream(filename)) {
					return new JcaX509CRLConverter().getCRL(new X509CRLHolder(fis));
				} catch (CRLException | CertIOException e) {
					// throw new IOException("Parsing of file failed", e);
				}
				// Retry with DER CMS Store
				byte[] data = Files.readAllBytes(filename);
				try {
					CMSSignedData cms = new CMSSignedData(data);
					Store<X509CRLHolder> crls = cms.getCRLs();
					Collection<?> collection = crls.getMatches(null);
					return parseCMSStore(collection);
				} catch (CMSException e) {
					throw new IOException("Parsing of file failed", e);
				}
			}

			if (object instanceof X509CRL) {
				return (X509CRL) object;
			} else if (object instanceof X509CRLHolder) {
				try {
					return new JcaX509CRLConverter().getCRL((X509CRLHolder) object);
				} catch (CRLException e) {
					throw new IOException("Parsing of certificate information failed", e);
				}
			} else if (object instanceof ContentInfo) {
				try {
					CMSSignedData cms = new CMSSignedData((ContentInfo) object);
					Store<X509CRLHolder> crls = cms.getCRLs();
					Collection<?> collection = crls.getMatches(null);
					return parseCMSStore(collection);
				} catch (CMSException e) {
					throw new IOException("Parsing of certificate information failed", e);
				}

			} else {
				throw new UnsupportedEncodingException("Unhandled class: " + object.getClass().getName());
			}
		}
	}

	/**
	 * Parse the collection of objects, and get the first CRL list available
	 * 
	 * @param collection The collection of objects to parse
	 * @throws IOException If parsing fails.
	 */
	private static X509CRL parseCMSStore(Collection<?> collection) throws IOException {
		for (Object o : collection) {
			if (o instanceof X509CRLHolder) {
				try {
					return new JcaX509CRLConverter().getCRL((X509CRLHolder) o);
				} catch (CRLException e) {
					throw new IOException("Parsing of certificate information failed", e);
				}
			}
		}
		return null;
	}

	/**
	 * Create a new CRL
	 * 
	 * @param filename The filename to store as
	 * @param encoding The encoding type
	 * @param crl The build CRL information
	 * @throws IOException Writing to the file failed
	 */
	public static void create(Path filename, EncodingType encoding, X509CRL crl) throws IOException {
		try (OutputStream out = Files.newOutputStream(filename)) {
			switch (encoding) {
			case PEM:
				try (JcaPEMWriter writer = new JcaPEMWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));) {
					writer.writeObject(crl);
				}
				break;
			case DER:
			default:
				try {
					out.write(crl.getEncoded());
					out.flush();
				} catch (CRLException e) {
					throw new IOException(e);
				}
				break;
			}
		}
	}
}
