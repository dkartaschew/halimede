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

package net.sourceforge.dkartaschew.halimede.data.persistence;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;

import org.bouncycastle.util.encoders.Base64;

/**
 * A Persistence delegate for handling byte[].
 * 
 * byte[] default encoding is not space efficient, so use this delegate to encode binary data as Base64
 */
public class ByteArrayPersistenceDelegate extends PersistenceDelegate {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PersistenceDelegate#instantiate(java.lang.Object, java.beans.Encoder)
	 */
	@Override
	protected Expression instantiate(Object oldInstance, Encoder out) {
		byte[] e = (byte[]) oldInstance;
		// Use this class as the decode delegate, so we are not tied to BC for decoding.
		return new Expression(e, ByteArrayPersistenceDelegate.class, "decode",
				new Object[] { ByteArrayPersistenceDelegate.encode(e) });
	}

	/**
	 * Decode a Base64 encoded string to byte array
	 * 
	 * @param encoded The encoded string
	 * @return A byte[]
	 */
	public static byte[] decode(String encoded) {
		return Base64.decode(encoded);
	}

	/**
	 * Encode a byte array to Base64 string
	 * 
	 * @param data The binary data to encode
	 * @return A string representation.
	 */
	public static String encode(byte[] data) {
		return Base64.toBase64String(data);
	}
}
