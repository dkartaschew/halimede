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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;

public class MockCertificate extends Certificate {

	public MockCertificate(String type) {
		super(type);
	}
	
	@Override
	public void verify(PublicKey key, String sigProvider) throws CertificateException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchProviderException, SignatureException {
		throw new CertificateEncodingException("Invalid");
	}

	@Override
	public void verify(PublicKey key) throws CertificateException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchProviderException, SignatureException {
		throw new CertificateEncodingException("Invalid");
	}

	@Override
	public String toString() {
		return getType();
	}

	@Override
	public PublicKey getPublicKey() {
		return null;
	}

	@Override
	public byte[] getEncoded() throws CertificateEncodingException {
		return new byte[0];
	}
}
