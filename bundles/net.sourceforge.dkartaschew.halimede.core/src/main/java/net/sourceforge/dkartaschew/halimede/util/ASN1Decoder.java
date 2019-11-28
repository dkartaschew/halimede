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

package net.sourceforge.dkartaschew.halimede.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.util.ASN1Dump;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;

import net.sourceforge.dkartaschew.halimede.data.render.ICertificateOutputRenderer;

public class ASN1Decoder {

	/**
	 * The filename to read to decode as ASN1
	 */
	private final Path file;

	/**
	 * Create a ASN1 decoder
	 * 
	 * @param filename The file to decode
	 * @return A decode
	 * @throws IOException If the file does not exist, or is not readable.
	 */
	public static ASN1Decoder create(Path filename) throws IOException {
		Objects.requireNonNull(filename, "Filename was null");
		if (!Files.exists(filename)) {
			throw new FileNotFoundException("File '" + filename + "' not found.");
		}
		if (!Files.isRegularFile(filename) || !Files.isReadable(filename)) {
			throw new IOException("File '" + filename + "' is not readable.");
		}
		return new ASN1Decoder(filename);
	}

	/**
	 * Create a new decoder based on the given file
	 * 
	 * @param file The file to decode
	 */
	private ASN1Decoder(Path file) {
		this.file = file;
	}

	/**
	 * Decode the file as ASN1 to string
	 * 
	 * @return The file decoded
	 * @throws IOException If decoding fails.
	 */
	public String decode() throws IOException {
		StringBuilder sb = new StringBuilder();
		try (PEMParser parser = new PEMParser(Files.newBufferedReader(file, StandardCharsets.UTF_8))) {
			Object object = parser.readObject();
			if (object == null) {
				// May be plain DER without enough info for PEMParser
				return decodeDER();
			}
			while (object != null) {
				// Some PEM Objects don't directly implement ASN1Primitive or ASN1Encodable so massage
				if (object instanceof X509CRLHolder) {
					object = ASN1Primitive.fromByteArray(((X509CRLHolder) object).getEncoded());
				} else if (object instanceof X509CertificateHolder) {
					object = ASN1Primitive.fromByteArray(((X509CertificateHolder) object).getEncoded());
				} else if (object instanceof PKCS10CertificationRequest) {
					object = ASN1Primitive.fromByteArray(((PKCS10CertificationRequest) object).getEncoded());
				} else if (object instanceof PKCS8EncryptedPrivateKeyInfo) {
					object = ASN1Primitive.fromByteArray(((PKCS8EncryptedPrivateKeyInfo) object).getEncoded());
				}
				sb.append(ASN1Dump.dumpAsString(object, true));
				object = parser.readObject();
			}
		} catch (MalformedInputException e) {
			// May be plain DER without enough info for PEMParser
			return decodeDER();
		}
		return sb.toString();
	}

	/**
	 * Decode the file as ASN1 to string
	 * 
	 * @return The file decoded
	 * @throws IOException If decoding fails.
	 */
	private String decodeDER() throws IOException {
		byte[] data = Files.readAllBytes(file);
		StringBuilder sb = new StringBuilder();
		try (ASN1InputStream input = new ASN1InputStream(data)) {
			ASN1Primitive p;
			while ((p = input.readObject()) != null) {
				sb.append(ASN1Dump.dumpAsString(p, true));
			}
		}
		return sb.toString();
	}

	/**
	 * Render the decoded contents to the renderer.
	 * 
	 * @param renderer The renderer to write to.
	 */
	public void render(ICertificateOutputRenderer renderer) {
		renderer.addHeaderLine("ASN1 Decode");
		renderer.addContentLine("Filename", file.toString());
		try {
			renderer.addContentLine("", decode(), true);
		} catch (IOException e) {
			renderer.addContentLine("Failed to decode: " + ExceptionUtil.getMessage(e));
		}
	}
}
