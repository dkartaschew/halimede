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

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
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
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.BCDSTU4145PrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.pqc.jcajce.provider.sphincs.BCSphincs256PrivateKey;
import org.bouncycastle.pqc.jcajce.provider.sphincs.BCSphincs256PublicKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSMTPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSMTPublicKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSPrivateKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSPublicKey;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.IIssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.ui.labelproviders.GeneralNameLabelProvider;
import net.sourceforge.dkartaschew.halimede.util.CertificateExtensionUtil;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.Digest;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

/**
 * A basic renderer to render a CSR to a output renderer
 */
public class CertificateRenderer {

	private final static int WRAP = 32;

	/**
	 * The model to print
	 */
	private final IssuedCertificateProperties model;
	private final CertificateAuthority camodel;

	/**
	 * The Certificate to render
	 * 
	 * @param model The model to render.
	 */
	public CertificateRenderer(IssuedCertificateProperties model) {
		this.model = model;
		this.camodel = null;
	}

	/**
	 * The Certificate to render
	 * 
	 * @param model The model to render.
	 */
	public CertificateRenderer(CertificateAuthority model) {
		this.model = null;
		this.camodel = model;
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
		String desc = null;
		if (model != null) {
			/*
			 * Issued Certificate
			 */
			desc = this.model.getProperty(Key.description);
			if (desc == null) {
				desc = this.model.getProperty(Key.subject);
			}
			r.addHeaderLine(desc);
			r.addContentLine("Created Date:", model.getProperty(Key.creationDate));
			if (this.model.getProperty(Key.revokeDate) != null) {
				r.addContentLine("Revoked Date:", model.getProperty(Key.revokeDate));
			}
			if (this.model.getProperty(Key.revokeCode) != null) {
				try {
					r.addContentLine("Revocation Reason:", //
							RevokeReasonCode.valueOf(model.getProperty(Key.revokeCode)).getDescription());
				} catch (Throwable e) {
					// Ignore and go with raw value.
					r.addContentLine("Revocation Reason:", model.getProperty(Key.revokeCode));
				}
			}
			if (this.model.getProperty(Key.comments) != null) {
				r.addContentLine("Comments:", model.getProperty(Key.comments));
			}
			r.addHorizontalLine();
		} else {
			/*
			 * CA Description
			 */
			desc = this.camodel.getDescription();
			r.addHeaderLine(this.camodel.getDescription());
			r.addContentLine("UUID:", this.camodel.getCertificateAuthorityID().toString(), true);
			r.addContentLine("Location:", this.camodel.getBasePath().toString());
			r.addHorizontalLine();
		}

		/*
		 * CA Private Key
		 */
		KeyPair keys = null;
		if (model != null) {
			/*
			 * Issued Certificate
			 */
			try {
				IIssuedCertificate ic = this.model.loadIssuedCertificate(null);
				keys = new KeyPair(ic.getPublicKey(), ic.getPrivateKey());
			} catch (KeyStoreException | InvalidPasswordException | IOException e) {
				r.addHeaderLine("ERROR UNABLE TO ACCESS Certificate Information");
				r.addContentLine("Error:", ExceptionUtil.getMessage(e));
			}
		} else {
			/*
			 * CA Private Key
			 */
			try {
				keys = this.camodel.getKeyPair();
			} catch (DatastoreLockedException e) {
				r.addHeaderLine("ERROR UNABLE TO ACCESS Certificate Information");
				r.addContentLine("Error:", ExceptionUtil.getMessage(e));
			}
		}
		if (keys != null && keys.getPrivate() != null) {
			r.addHeaderLine(desc + " Private Key");
			r.addContentLine("Key Algorithm:", keys.getPrivate().getAlgorithm());
			r.addContentLine("Key Length/Size:", Integer.toString(KeyPairFactory.getKeyLength(keys)));
			PrivateKey pkey = keys.getPrivate();
			if (pkey instanceof ECPrivateKey) {
				ECPrivateKey eckey = (ECPrivateKey) pkey;
				ECParameterSpec spec = eckey.getParams();
				if (spec != null && spec instanceof ECNamedCurveSpec) {
					ECNamedCurveSpec ecspec = (ECNamedCurveSpec) spec;
					r.addContentLine("Curve Name:", ecspec.getName());
				} else if (pkey instanceof BCDSTU4145PrivateKey) {
					BCDSTU4145PrivateKey eckey2 = (BCDSTU4145PrivateKey) pkey;
					org.bouncycastle.jce.spec.ECParameterSpec spec2 = eckey2.getParameters();
					if (spec2 != null && spec2 instanceof org.bouncycastle.jce.spec.ECNamedCurveParameterSpec) {
						org.bouncycastle.jce.spec.ECNamedCurveParameterSpec ecspec = (org.bouncycastle.jce.spec.ECNamedCurveParameterSpec) spec2;
						r.addContentLine("Curve Name:", ecspec.getName());
					}
				}
			}
			if (pkey instanceof GOST3410PrivateKey) {
				GOST3410PrivateKey gostKey = (GOST3410PrivateKey) pkey;
				String algID = gostKey.getParameters().getPublicKeyParamSetOID();
				for (KeyType t : KeyType.values()) {
					if (t.getParameters() != null && t.getParameters().equals(algID)) {
						r.addContentLine("GOST 34.10:", t.getDescription());
					}
				}
			}
			if (pkey instanceof BCEdDSAPrivateKey) {
				r.addContentLine("Curve Name:", pkey.getAlgorithm());
			}
			if(pkey instanceof BCSphincs256PrivateKey) {
				BCSphincs256PrivateKey sphincs = (BCSphincs256PrivateKey) pkey;
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
			if(pkey instanceof BCXMSSPrivateKey) {
				BCXMSSPrivateKey xmss = (BCXMSSPrivateKey)pkey;
				r.addContentLine("Tree Digest:", xmss.getTreeDigest());
				r.addContentLine("Height:", Integer.toString(xmss.getHeight()));
			}
			if(pkey instanceof BCXMSSMTPrivateKey) {
				BCXMSSMTPrivateKey xmss = (BCXMSSMTPrivateKey)pkey;
				r.addContentLine("Tree Digest:", xmss.getTreeDigest());
				r.addContentLine("Height:", Integer.toString(xmss.getHeight()));
				r.addContentLine("Layers:", Integer.toString(xmss.getLayers()));
			}
			// NOP for qTESLA

			r.addContentLine("SHA1 Fingerprint:", //
					Strings.toHexString(Digest.sha1(keys.getPrivate().getEncoded()), " ", WRAP), true);
			r.addContentLine("SHA512 Fingerprint:", //
					Strings.toHexString(Digest.sha512(keys.getPrivate().getEncoded()), " ", WRAP), true);
			r.addHorizontalLine();
		}
		X509Certificate certificate = null;
		if (model != null) {
			/*
			 * Issued Certificate
			 */
			try {
				IIssuedCertificate ic = this.model.loadIssuedCertificate(null);
				certificate = (X509Certificate) ic.getCertificateChain()[0];
			} catch (KeyStoreException | InvalidPasswordException | IOException e) {
				r.addHeaderLine("ERROR UNABLE TO ACCESS Certificate Information");
				r.addContentLine("Error:", ExceptionUtil.getMessage(e));

			}
		} else {
			try {
				certificate = (X509Certificate) camodel.getCertificate();
			} catch (DatastoreLockedException e) {
				r.addHeaderLine("ERROR UNABLE TO ACCESS Certificate Information");
				r.addContentLine("Error:", ExceptionUtil.getMessage(e));
			}
		}
		/*
		 * Certificate.
		 */
		Objects.requireNonNull(certificate, "Missing Certificate?");

		r.addHeaderLine(desc + " Certificate");
		/*
		 * Subject
		 */
		r.addHeaderLine("Subject");
		r.addContentLine("X.500 Name:", certificate.getSubjectX500Principal().toString());
		/*
		 * Issuer
		 */
		r.addHeaderLine("Issuer");
		r.addContentLine("X.500 Name:", certificate.getIssuerX500Principal().toString());
		/*
		 * Base Details
		 */
		r.addHeaderLine("Issued Certificate");
		r.addContentLine("Version:", Integer.toString(certificate.getVersion()));
		r.addContentLine("Serial:", Strings.asDualValue(certificate.getSerialNumber()));
		r.addContentLine("Not Valid Before:", DateTimeUtil.toString(certificate.getNotBefore()));
		r.addContentLine("Not Valid After:", DateTimeUtil.toString(certificate.getNotAfter()));
		/*
		 * Finger prints.
		 */
		try {
			r.addContentLine("SHA1 Fingerprint:", //
					Strings.toHexString(Digest.sha1(certificate.getEncoded()), " ", WRAP), true);
			r.addContentLine("SHA512 Fingerprint:", //
					Strings.toHexString(Digest.sha512(certificate.getEncoded()), " ", WRAP), true);
		} catch (CertificateEncodingException e) {
			r.addHeaderLine("ERROR UNABLE TO GENERATE Certificate Fingerprints");
			r.addContentLine("Error:", ExceptionUtil.getMessage(e));
		}

		/*
		 * Public Key Info
		 */
		PublicKey pkey = certificate.getPublicKey();
		Objects.requireNonNull(pkey, "Missing Public key from Certificate?");
		r.addHeaderLine("Public Key");
		r.addContentLine("Key Algorithm:", pkey.getAlgorithm());
		r.addContentLine("Key Length/Size:", Integer.toString(KeyPairFactory.getKeyLength(pkey)));
		if (pkey instanceof ECPublicKey) {
			ECPublicKey eckey = (ECPublicKey) pkey;
			ECParameterSpec spec = eckey.getParams();
			if (spec != null && spec instanceof ECNamedCurveSpec) {
				ECNamedCurveSpec ecspec = (ECNamedCurveSpec) spec;
				r.addContentLine("Curve Name:", ecspec.getName());
			}
			/*
			 * Spec may be the JRE NamedCurve but this API is protected can cannot be accessed...
			 */
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
		if(pkey instanceof BCEdDSAPublicKey) {
			r.addContentLine("Curve Name:", pkey.getAlgorithm());
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
		// NOP for qTELSA
		
		// Add key material
		r.addContentLine("Public Key:", Strings.prettyPrint(pkey), true);

		/*
		 * X509v3 Extensions
		 */

		try {
			JcaX509CertificateHolder cert = new JcaX509CertificateHolder(certificate);
			// Start with our basic extensions.
			/*
			 * Constraints
			 */
			if (cert.getExtensions() != null) {
				BasicConstraints constraints = BasicConstraints.fromExtensions(cert.getExtensions());
				if (constraints != null) {
					r.addHeaderLine("Basic Constraints");
					r.addContentLine("Certificate Authority:", constraints.isCA() ? "True" : "False");
					if (constraints.isCA() && constraints.getPathLenConstraint() != null) {
						r.addContentLine("Certificate Chain Depth:", constraints.getPathLenConstraint().toString());
					}
					Extension ext = cert.getExtension(Extension.basicConstraints);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}
				/*
				 * CRL Location
				 */
				Extension crlExt = cert.getExtension(Extension.cRLDistributionPoints);
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
				 * Key Usage
				 */
				KeyUsage keyUsage = KeyUsage.fromExtensions(cert.getExtensions());
				if (keyUsage != null) {
					r.addHeaderLine("Key Usage");
					String v = KeyUsageEnum.asKeyUsageEnum(keyUsage).stream()//
							.map(u -> u.toString())//
							.sorted()//
							.collect(Collectors.joining(System.lineSeparator()));
					r.addContentLine("Usage:", v);
					Extension ext = cert.getExtension(Extension.keyUsage);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}
				/*
				 * Extended Key Usage
				 */
				ExtendedKeyUsage exKeyUsage = ExtendedKeyUsage.fromExtensions(cert.getExtensions());
				if (exKeyUsage != null) {
					r.addHeaderLine("Extended Key Usage");
					String v = ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(exKeyUsage).stream()//
							.map(u -> u.toString())//
							.sorted()//
							.collect(Collectors.joining(System.lineSeparator()));
					r.addContentLine("Usage:", v);
					Extension ext = cert.getExtension(Extension.extendedKeyUsage);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}
				/*
				 * Subject Alt Names.
				 */
				GeneralNames subjAltNames = GeneralNames.fromExtensions(cert.getExtensions(),
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
					Extension ext = cert.getExtension(Extension.subjectAlternativeName);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}

				/*
				 * subjectKeyIdentifier
				 */
				SubjectKeyIdentifier info = SubjectKeyIdentifier.fromExtensions(cert.getExtensions());
				if (info != null) {
					Extension ext = cert.getExtension(Extension.subjectKeyIdentifier);
					r.addHeaderLine("Subject Public Key Identifier");
					r.addContentLine("Data:", Strings.toHexString(info.getKeyIdentifier(), " ", WRAP), true);
					r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
				}

				/*
				 * authorityKeyIdentifier
				 */
				AuthorityKeyIdentifier id = AuthorityKeyIdentifier.fromExtensions(cert.getExtensions());
				if (id != null) {
					r.addHeaderLine("Authority Key Identifier");
					if (id.getAuthorityCertIssuer() != null) {
						GeneralName[] names = id.getAuthorityCertIssuer().getNames();
						GeneralNameLabelProvider p = new GeneralNameLabelProvider();
						String v = Arrays.stream(names)//
								.map(n -> p.getText(n))//
								.sorted()//
								.collect(Collectors.joining(System.lineSeparator()));
						r.addContentLine("Authority Certificate Issuer:", v);
					}
					BigInteger i = id.getAuthorityCertSerialNumber();
					if (i != null) {
						r.addContentLine("Authority Serial Number:", Strings.asDualValue(i));
					}
					if (id.getAuthorityCertIssuer() == null && i == null) {
						r.addContentLine("Authority Key Identifier:",
								Strings.toHexString(id.getKeyIdentifier(), " ", WRAP), true);
					}
					Extension ext = cert.getExtension(Extension.authorityKeyIdentifier);
					if (ext != null) {
						r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
					}
				}

				/*
				 * Netscape comments.
				 */
				Extension nsComment = cert.getExtension(MiscObjectIdentifiers.netscapeCertComment);
				if (nsComment != null) {
					r.addHeaderLine("Netscape Comment");
					ASN1Encodable data = nsComment.getParsedValue();
					if (data != null) {
						r.addContentLine("Comment:", data.toString());
					}
					r.addContentLine("Critical:", nsComment.isCritical() ? "True" : "False");
				}

				/*
				 * All others.
				 */
				Extensions exts = cert.getExtensions();
				for (ASN1ObjectIdentifier objId : exts.getExtensionOIDs()) {
					if (showExtension(objId)) {
						Extension ext = exts.getExtension(objId);
						if (ext != null) {
							String header;
							if ((header = CertificateExtensionUtil.getDescription(objId)) == null) {
								header = ext.getExtnId().getId();
							}
							r.addHeaderLine(header);
							r.addContentLine("Data:", Strings.toHexString(ext.getExtnValue().getOctets(), " ", WRAP), true);
							r.addContentLine("Critical:", ext.isCritical() ? "True" : "False");
						}
					}
				}
			}

		} catch (CertificateEncodingException e) {
			r.addHeaderLine("ERROR UNABLE TO ACCESS Certificate Information");
			r.addContentLine("Error:", ExceptionUtil.getMessage(e));
		}

		/*
		 * Cert Signature
		 */
		r.addHeaderLine("Certificate Signature");
		r.addContentLine("Signature Algorithm:", certificate.getSigAlgName());
		r.addContentLine("Signature:", Strings.toHexString(certificate.getSignature(), " ", WRAP), true);
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
		if (objId.equals(Extension.authorityKeyIdentifier))
			return false;
		if (objId.equals(Extension.subjectKeyIdentifier))
			return false;
		if (objId.equals(Extension.cRLDistributionPoints))
			return false;
		if (objId.equals(MiscObjectIdentifiers.netscapeCertComment)) {
			return false;
		}
		return true;
	}
}
