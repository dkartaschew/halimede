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

package net.sourceforge.dkartaschew.halimede.util;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.x500.X500Name;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;

public class CertificateUtil {

	/**
	 * Get a collection of issuer X500Names
	 * 
	 * @param ca The CA to enquire
	 * @return A collection of issuer X500Names
	 */
	public static Collection<X500Name> getIssuers(CertificateAuthority ca) {
		if (ca == null || ca.isLocked()) {
			return null;
		}
		try {
			Certificate[] chain = ca.getCertificateChain();
			if (chain == null) {
				return null; // Shouldn't happen...
			}
			return Arrays.stream(chain)//
					.filter(c -> c instanceof X509Certificate)//
					.map(c -> X500Name.getInstance(((X509Certificate) c).getSubjectX500Principal().getEncoded())) //
					.collect(Collectors.toList());

		} catch (DatastoreLockedException e) {
			// WTF?
		}
		return null;
	}
}
