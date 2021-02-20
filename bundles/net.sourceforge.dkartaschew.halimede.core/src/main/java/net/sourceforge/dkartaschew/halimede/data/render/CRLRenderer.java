/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2021 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.data.render;

import java.math.BigInteger;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CRLHolder;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties.Key;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.Digest;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

/**
 * A basic renderer to render a CRL to a output renderer
 */
public class CRLRenderer {

	private final static int WRAP = 32;

	/**
	 * The model to print
	 */
	private final CRLProperties model;
	/**
	 * Limit the entries to the specified amount
	 */
	private final int entryLimit;

	/**
	 * The CRL to render
	 * 
	 * @param model The model to render.
	 */
	public CRLRenderer(CRLProperties model) {
		this(model, Integer.MAX_VALUE);
	}

	/**
	 * The CRL to render
	 * 
	 * @param model The model to render.
	 * @param entryLimit Limit the number of entries to the following limit. (default is Integer.MAX_VALUE).
	 */
	public CRLRenderer(CRLProperties model, int entryLimit) {
		this.model = model;
		this.entryLimit = entryLimit;
	}

	/**
	 * Render the CRL to the given output renderer.
	 * 
	 * @param r The renderer to output to.
	 */
	public void render(ICertificateOutputRenderer r) {
		/*
		 * Description
		 */
		if (model.getCertificateAuthority() != null) {
			String desc = model.getCertificateAuthority().getDescription();
			r.addHeaderLine(desc);
		} else {
			r.addHeaderLine(model.getProperty(Key.issuer));
		}
		try {

			X509CRL crl = model.getCRL();
			X509CRLHolder holder = new X509CRLHolder(crl.getEncoded());

			r.addContentLine("Issuer:", model.getProperty(Key.issuer));

			// See if we can get the Issuer ID ext.
			if (holder.getExtension(Extension.authorityKeyIdentifier) != null) {
				AuthorityKeyIdentifier id = AuthorityKeyIdentifier.fromExtensions(holder.getExtensions());
				if (id.getKeyIdentifier() != null) {
					r.addContentLine("Issuer ID:", Strings.toHexString(id.getKeyIdentifier(), " ", WRAP), true);
				}
				BigInteger s = id.getAuthorityCertSerialNumber();
				if (s != null) {
					r.addContentLine("Issuer Serial:", Strings.asDualValue(s));
				}
			}

			r.addContentLine("Issue Date:", model.getProperty(Key.issueDate));
			r.addContentLine("Next Update Date:", model.getProperty(Key.nextExpectedDate));
			r.addContentLine("Serial:", model.getProperty(Key.crlSerialNumber));

			r.addContentLine("SHA1 Fingerprint:", //
					Strings.toHexString(Digest.sha1(crl.getEncoded()), " ", WRAP), true);
			r.addContentLine("SHA512 Fingerprint:", //
					Strings.toHexString(Digest.sha512(crl.getEncoded()), " ", WRAP), true);

			r.addHorizontalLine();

			/*
			 * Revoked Certificates
			 */

			r.addHeaderLine("Revoked Certificates");

			Set<? extends X509CRLEntry> entries = crl.getRevokedCertificates();
			if (entries != null) {
				if (entries.isEmpty()) {
					// getRevokedCertificates() returned empty set.
					r.addContentLine("", "No Certificates");
				} else {
					List<X509CRLEntry> sortedEntries = new ArrayList<>(entries);
					try {
						Collections.sort(sortedEntries,
								Comparator.nullsLast(
										Comparator.comparing(X509CRLEntry::getRevocationDate, Date::compareTo)
												.thenComparing(X509CRLEntry::getSerialNumber, BigInteger::compareTo)));
					} catch (NullPointerException e) {
						// WTF?

					}
					r.addContentLine("Total Certificates Count:", Integer.toString(sortedEntries.size()));
					r.addEmptyLine();
					int count = 0;
					for (X509CRLEntry entry : sortedEntries) {
						Date d = entry.getRevocationDate();
						BigInteger s = entry.getSerialNumber();
						RevokeReasonCode rc = RevokeReasonCode.forCRLReason(entry.getRevocationReason());
						r.addContentLine("Certificate Serial:", Strings.asDualValue(s));
						if (d != null) {
							r.addContentLine("Revoke Date:", DateTimeUtil.toString(d));
						}
						if (rc != null) {
							r.addContentLine("Revocation Reason:", rc.getDescription());
						}
						r.addEmptyLine();
						count++;
						if (count > entryLimit) {
							// break...
							if ((sortedEntries.size() - count) > 0)
								r.addContentLine("",
										(sortedEntries.size() - count) + " additional certificates included...");
							break;
						}
					}
				}
			} else {
				// getRevokedCertificates() returned null
				r.addContentLine("", "No Certificates");
			}

			r.addHorizontalLine();
			/*
			 * CRL Signature
			 */
			r.addHeaderLine("CRL Signature");
			Object alg = null;
			try {
				alg = SignatureAlgorithm.forOID(new ASN1ObjectIdentifier(crl.getSigAlgOID()));
			} catch (NoSuchElementException | IllegalArgumentException e) {
				// ignore
			}
			if (alg != null && alg instanceof SignatureAlgorithm) {
				r.addContentLine("Signature Algorithm:", alg.toString());
			} else {
				r.addContentLine("Signature Algorithm:", crl.getSigAlgName());
			}
			r.addContentLine("Signature:", Strings.toHexString(crl.getSignature(), " ", WRAP), true);

		} catch (Throwable e) {
			// logger.error(e, e.getMessage());
			r.addHeaderLine("ERROR UNABLE TO ACCESS CRL");
			r.addContentLine("Error:", ExceptionUtil.getMessage(e));
		}
	}
}
