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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Basic digest helper functions for the UI.
 */
public class Digest {

	/*
	 * Setup BC crypto provider.
	 */
	static {
		ProviderUtil.setupProviders();
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] md5(byte[] content) {
		return digest("MD5", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] sha1(byte[] content) {
		return digest("SHA1", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] sha256(byte[] content) {
		return digest("SHA256", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] sha384(byte[] content) {
		return digest("SHA384", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] sha512(byte[] content) {
		return digest("SHA512", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] gost3411(byte[] content) {
		return digest("GOST3411", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] gost3411_2012_256(byte[] content) {
		return digest("GOST3411-2012-256", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	public static byte[] gost3411_2012_512(byte[] content) {
		return digest("GOST3411-2012-512", content);
	}

	/**
	 * Get the digest for the given input
	 * 
	 * @param algID The digest ID.
	 * @param content The content to obtain the digest for.
	 * @return The digest as a byte array.
	 */
	private static byte[] digest(String algID, byte[] content) {
		try {
			return MessageDigest.getInstance(algID, BouncyCastleProvider.PROVIDER_NAME).digest(content);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
		}
		return new byte[0];
	}
}
