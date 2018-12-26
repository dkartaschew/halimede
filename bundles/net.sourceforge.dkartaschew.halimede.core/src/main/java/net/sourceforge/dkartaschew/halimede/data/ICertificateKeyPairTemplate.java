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

import java.io.IOException;
import java.nio.file.Path;
import java.time.ZonedDateTime;

import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;

/**
 * Generic interface for all Cerificate Key Pair implementations.
 */
public interface ICertificateKeyPairTemplate {

	/**
	 * Default extension for templates
	 */
	public static String DEFAULT_EXTENSION = ".xml";

	/**
	 * Open a Certificate request as a templated instance.
	 * 
	 * @param file The file to open.
	 * @return A ICertificateKeyPairTemplate
	 * @throws IOException Reading the template failed.
	 */
	public static ICertificateKeyPairTemplate open(Path file) throws IOException {
		try {
			return CertificateKeyPairTemplate.read(file);
		} catch (Throwable e) {
			throw new IOException("Failed to read template file", e);
		}
	}

	/**
	 * Store the template
	 * 
	 * @param filename The filename to store the template.
	 * @throws Exception If serialisation fails.
	 */
	void store(Path filename) throws Exception;

	/**
	 * Get this Template Instance as a Certificate Request, so a concrete X509Certificate can be created.
	 * 
	 * @return A Certificate Request.
	 */
	ICertificateRequest asCertificateRequest();

	/**
	 * The creation date of the certificate template.
	 * 
	 * @return The creation date of the template.
	 */
	ZonedDateTime getCreationDate();
}