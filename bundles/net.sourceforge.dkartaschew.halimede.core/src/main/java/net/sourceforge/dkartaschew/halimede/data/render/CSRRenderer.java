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

package net.sourceforge.dkartaschew.halimede.data.render;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.bouncycastle.pqc.jcajce.provider.sphincs.BCSphincs256PublicKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSMTPublicKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSPublicKey;

import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.ui.labelproviders.GeneralNameLabelProvider;
import net.sourceforge.dkartaschew.halimede.util.CertificateExtensionUtil;
import net.sourceforge.dkartaschew.halimede.util.Digest;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

/**
 * A basic renderer to render a CSR to a output renderer
 */
public class CSRRenderer {

	private final static int WRAP = 32;

	/**
	 * The model to print
	 */
	private final CertificateRequestProperties model;

	/**
	 * The CRL to render
	 * 
	 * @param model The model to render.
	 */
	public CSRRenderer(CertificateRequestProperties model) {
		this.model = model;
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
			r.addHeaderLine(model.getProperty(Key.subject));
		}

		try {

			ICertificateRequest csr = model.getCertificateRequest();
			CertificateRequestPKCS10 holder = (CertificateRequestPKCS10) csr;

			r.addContentLine("Import Date:", model.getProperty(Key.importDate));
			r.addContentLine("Subject:", model.getProperty(Key.subject));

			/*
			 * subjectKeyIdentifier
			 */
			SubjectPublicKeyInfo info = holder.getSubjectPublicKeyInfo();
			if (info != null) {
				r.addHeaderLine("Subject Public Key Info");
				// Extract the public key from the SubjectPublicKeyInfo
				JcaPKCS10CertificationRequest jcaHolder = new JcaPKCS10CertificationRequest(holder.getEncoded())
						.setProvider(BouncyCastleProvider.PROVIDER_NAME);
				PublicKey pkey = jcaHolder.getPublicKey();

				r.addContentLine("Key Algorithm:", pkey.getAlgorithm());
				r.addContentLine("Key Length/Size:",
						Integer.toString(KeyPairFactory.getKeyLength(new KeyPair(pkey, null))));
				if (pkey instanceof ECPublicKey) {
					ECPublicKey eckey = (ECPublicKey) pkey;
					ECParameterSpec spec = eckey.getParams();
					if (spec != null && spec instanceof ECNamedCurveSpec) {
						ECNamedCurveSpec ecspec = (ECNamedCurveSpec) spec;
						r.addContentLine("Curve Name:", ecspec.getName());
					}
				}
				if (pkey instanceof GOST3410PublicKey) {
					GOST3410PublicKey gostKey = (GOST3410PublicKey) pkey;
					String algID = gostKey.getParameters().getPublicKeyParamSetOID();
					for (KeyType t : KeyType.values()) {
						if (t.getParameters() != null && t.getParameters().equals(algID)) {
							r.addContentLine("GOST 34.10:", t.getDescription());
						}
					}
				}
				if(pkey instanceof BCSphincs256PublicKey) {
					BCSphincs256PublicKey sphincs = (BCSphincs256PublicKey) pkey;
					try {
						/*
						 * NASTY, but to determine the key, when need the digest.
						 */
						Field f = sphincs.getClass().getDeclaredField("treeDigest");
						f.setAccessible(true);
						ASN1ObjectIdentifier digest = (ASN1ObjectIdentifier) f.get(sphincs);
						if (digest.equals(NISTObjectIdentifiers.id_sha512_256)) {
							r.addContentLine("Tree Digest:", "SHA512-256");
						}
						if (digest.equals(NISTObjectIdentifiers.id_sha3_256)) {
							r.addContentLine("Tree Digest:", "SHA3-256");
						}
					} catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
						throw new RuntimeException("BC SPHINCS-256 modified", e);
					}
				}
				if(pkey instanceof BCXMSSPublicKey) {
					BCXMSSPublicKey xmss = (BCXMSSPublicKey)pkey;
					r.addContentLine("Tree Digest:", xmss.getTreeDigest());
					r.addContentLine("Height:", Integer.toString(xmss.getHeight()));
				}
				if(pkey instanceof BCXMSSMTPublicKey) {
					BCXMSSMTPublicKey xmss = (BCXMSSMTPublicKey)pkey;
					r.addContentLine("Tree Digest:", xmss.getTreeDigest());
					r.addContentLine("Height:", Integer.toString(xmss.getHeight()));
					r.addContentLine("Layers:", Integer.toString(xmss.getLayers()));
				}
				
				r.addContentLine("Public Key:", Strings.prettyPrint(pkey), true);
			}

			r.addHeaderLine("Certiticate Signing Request Fingerprints");
			r.addContentLine("SHA1 Fingerprint:", //
					Strings.toHexString(Digest.sha1(holder.getEncoded()), " ", WRAP), true);
			r.addContentLine("SHA512 Fingerprint:", //
					Strings.toHexString(Digest.sha512(holder.getEncoded()), " ", WRAP), true);

			r.addHorizontalLine();

			/*
			 * CSR Extensions Certificates
			 */
			r.addHeaderLine("Extensions");
			Extensions entries = holder.getExtensions();
			if (entries != null) {
				// Constraints
				BasicConstraints constraints = BasicConstraints.fromExtensions(entries);
				if (constraints != null) {
					r.addHeaderLine("Basic Constraints");
					r.addContentLine("Certificate Authority:", constraints.isCA() ? "True" : "False");
					if (constraints.isCA() && constraints.getPathLenConstraint() != null) {
						r.addContentLine("Certificate Chain Depth:", constraints.getPathLenConstraint().toString());
					}
					Extension ext = entries.getExtension(Extension.basicConstraints);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}
				/*
				 * Key Usage
				 */
				KeyUsage keyUsage = KeyUsage.fromExtensions(holder.getExtensions());
				if (keyUsage != null) {
					r.addHeaderLine("Key Usage");
					String v = KeyUsageEnum.asKeyUsageEnum(keyUsage).stream()//
							.map(u -> u.toString())//
							.sorted()//
							.collect(Collectors.joining(System.lineSeparator()));
					r.addContentLine("Usage:", v);
					Extension ext = holder.getExtension(Extension.keyUsage);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}
				/*
				 * Extended Key Usage
				 */
				ExtendedKeyUsage exKeyUsage = ExtendedKeyUsage.fromExtensions(holder.getExtensions());
				if (exKeyUsage != null) {
					r.addHeaderLine("Extended Key Usage");
					String v = ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(exKeyUsage).stream()//
							.map(u -> u.toString())//
							.sorted()//
							.collect(Collectors.joining(System.lineSeparator()));
					r.addContentLine("Usage:", v);
					Extension ext = holder.getExtension(Extension.extendedKeyUsage);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}
				/*
				 * Subject Alt Names.
				 */
				GeneralNames subjAltNames = GeneralNames.fromExtensions(holder.getExtensions(),
						Extension.subjectAlternativeName);
				if (subjAltNames != null) {
					r.addHeaderLine("Subject Alternate Names");
					GeneralName[] names = subjAltNames.getNames();
					GeneralNameLabelProvider p = new GeneralNameLabelProvider();
					String v = Arrays.stream(names)//
							.map(n -> p.getText(n))//
							.sorted()//
							.collect(Collectors.joining(System.lineSeparator()));
					r.addContentLine("Alternate Names:", v);
					Extension ext = holder.getExtension(Extension.subjectAlternativeName);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}

				/*
				 * Netscape comments.
				 */
				Extension nsComment = holder.getExtension(MiscObjectIdentifiers.netscapeCertComment);
				if (nsComment != null) {
					r.addHeaderLine("Netscape Comment");
					ASN1Encodable data = nsComment.getParsedValue();
					if (data != null) {
						r.addContentLine("Comment:", data.toString());
					}
					r.addContentLine("Critical:", nsComment.isCritical() ? "True" : "False");
				}
				/*
				 * CRL Location
				 */
				Extension crlExt = entries.getExtension(Extension.cRLDistributionPoints);
				if (crlExt != null) {
					CRLDistPoint points = CRLDistPoint.getInstance(crlExt.getParsedValue());
					if (points != null) {
						r.addHeaderLine("CRL Location");
						if (points.getDistributionPoints() != null) {
							for (DistributionPoint point : points.getDistributionPoints()) {
								GeneralNames names = point.getCRLIssuer();
								GeneralNameLabelProvider p = new GeneralNameLabelProvider();
								/*
								 * Get issuers
								 */
								String key = "CRL Issuers:";
								String value = "Unknown";
								if (names != null && names.getNames() != null && names.getNames().length > 0) {

									value = Arrays.stream(names.getNames())//
											.map(n -> p.getValue(n))//
											.sorted()//
											.collect(Collectors.joining(System.lineSeparator()));
								}
								r.addContentLine(key, value);

								key = "CRL Locations:";
								value = "N/A";
								DistributionPointName distPoint = point.getDistributionPoint();
								ASN1Encodable dPointsEnc = distPoint.getName();
								if (dPointsEnc instanceof GeneralNames) {
									GeneralNames dPointsNames = (GeneralNames) dPointsEnc;
									value = Arrays.stream(dPointsNames.getNames())//
											.map(n -> p.getValue(n))//
											.sorted()//
											.collect(Collectors.joining(System.lineSeparator()));

								}
								r.addContentLine(key, value);

							}
						}
						r.addContentLine("Critical:", crlExt.isCritical() ? "True" : "False");
					}
				}
				/*
				 * All others.
				 */
				Extensions exts = holder.getExtensions();
				for (ASN1ObjectIdentifier objId : exts.getExtensionOIDs()) {
					if (showExtension(objId)) {
						Extension ext = exts.getExtension(objId);
						if (ext != null) {
							String header;
							if ((header = CertificateExtensionUtil.getDescription(objId)) == null) {
								header = ext.getExtnId().getId();
							}
							r.addHeaderLine(header);
							r.addContentLine("Data:", Strings.toHexString(ext.getExtnValue().getOctets(), " ", WRAP),
									true);
							r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
						}
					}
				}

			} else {
				// getRevokedCertificates() returned null
				r.addContentLine("", "No Extensions");
			}

			r.addHorizontalLine();
			/*
			 * CRL Signature
			 */
			r.addHeaderLine("Certificate Signing Request Signature");
			Object alg = null;
			try {
				alg = SignatureAlgorithm.forOID(holder.getSignatureAlgorithm().getAlgorithm());
			} catch (NoSuchElementException e) {
				// ignore
			}
			if (alg != null && alg instanceof SignatureAlgorithm) {
				r.addContentLine("Signature Algorithm:", alg.toString());
			} else {
				r.addContentLine("Signature Algorithm:", holder.getSignatureAlgorithm().getAlgorithm().getId());
			}
			r.addContentLine("Signature:", Strings.toHexString(holder.getSignature(), " ", WRAP), true);

		} catch (Throwable e) {
			// logger.error(e, e.getMessage());
			r.addHeaderLine("ERROR UNABLE TO ACCESS CSR");
			r.addContentLine("Error:", ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Determine if this is a non-handled extension objId, and we should show it.
	 * 
	 * @param objId The object ID
	 * @return TRUE if we don't explicitly handle this X509v3 extension id separately;
	 */
	private boolean showExtension(ASN1ObjectIdentifier objId) {
		if (objId.equals(Extension.basicConstraints))
			return false;
		if (objId.equals(Extension.keyUsage))
			return false;
		if (objId.equals(Extension.extendedKeyUsage))
			return false;
		if (objId.equals(Extension.subjectAlternativeName))
			return false;
		if (objId.equals(Extension.cRLDistributionPoints))
			return false;
		if (objId.equals(MiscObjectIdentifiers.netscapeCertComment)) {
			return false;
		}
		return true;
	}
}
