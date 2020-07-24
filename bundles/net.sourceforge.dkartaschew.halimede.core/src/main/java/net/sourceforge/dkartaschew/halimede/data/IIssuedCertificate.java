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
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;

public interface IIssuedCertificate {

	/**
	 * Get the Certificate for this instance
	 * 
	 * @return The certificate chain for this instance.
	 */
	Certificate[] getCertificateChain();

	/**
	 * Get the private key for this instance.
	 * 
	 * @return The private key, or NULL if none exists.
	 */
	PrivateKey getPrivateKey();

	/**
	 * Get the public key for this instance.
	 * 
	 * @return The public key.
	 */
	PublicKey getPublicKey();

	/**
	 * Get the PKCS#12 or PKCS#8 container filename.
	 * 
	 * @return the PKCS#12 or PKCS#8 container filename.
	 */
	Path getCertFilename();

	/**
	 * Get the PKCS#7 container filename.
	 * 
	 * @return the PKCS#7 container filename.
	 */
	Path getKeyFilename();

	/**
	 * Get the password to the key material.
	 * 
	 * @return the password used to access the key material.
	 */
	String getPassword();

	/**
	 * Save this Issued Certificate in a PKCS12 container
	 * 
	 * @param filename The filename to use
	 * @param password The password to use. (may be NULL for no password)
	 * @throws IOException If writing the file failed.
	 */
	void createPKCS12(Path filename, String password) throws IOException;

	/**
	 * Save this Issued Certificate in a PKCS12 container
	 * 
	 * @param filename The filename to use
	 * @param password The password to use. (may be NULL for no password)
	 * @param alias The alias to use within the PKCS12 container.
	 * @throws IOException If writing the file failed.
	 */
	void createPKCS12(Path filename, String password, String alias) throws IOException;

	/**
	 * Save this Issued Certificate in a PKCS12 container
	 * 
	 * @param filename The filename to use
	 * @param password The password to use. (may be NULL for no password)
	 * @param alias The alias to use within the PKCS12 container.
	 * @param cipher The Cipher to use to encrypt the PKCS12 contents with. (Note: AES is not widely supported). If
	 *            password is NULL, this value is ignored.
	 * @throws IOException If writing the file failed.
	 */
	void createPKCS12(Path filename, String password, String alias, PKCS12Cipher cipher) throws IOException;

	/**
	 * Save the client certificate to the given file.
	 * 
	 * @param filename The filename to use
	 * @param encoding The encoding to use.
	 * @throws IOException If writing the file failed.
	 */
	void createCertificate(Path filename, EncodingType encoding) throws IOException;

	/**
	 * Save the complete certificate chain to the given file.
	 * 
	 * @param filename The filename to use
	 * @param encoding The encoding to use.
	 * @throws IOException If writing the file failed.
	 */
	void createCertificateChain(Path filename, EncodingType encoding) throws IOException;

	/**
	 * Create PKCS8 or equivalent private key file.
	 * 
	 * @param filename The filename to use
	 * @param password The password to encrypt with. (may be NULL for no password).
	 * @param encoding The type of encoding.
	 * @param encryptionAlg The encryption algorithm. (may be NULL if no password).
	 * @throws IOException If writing the file failed.
	 * @throws IllegalStateException If there is NO private key available to encode.
	 */
	void createPKCS8(Path filename, String password, EncodingType encoding, PKCS8Cipher encryptionAlg)
			throws IOException, IllegalStateException;

	/**
	 * Create public key file.
	 * 
	 * @param filename The filename to use
	 * @param encoding The type of encoding.
	 * @throws IOException If writing the file failed.
	 */
	void createPublicKey(Path filename, EncodingType encoding) throws IOException;

}