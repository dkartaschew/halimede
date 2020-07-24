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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.DistributionPointName;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CRLConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

/**
 * Helper Utility Class for generation of X509 Certificates.
 */
public class CertificateFactory {

	/**
	 * RND Generator for keying material.
	 */
	private final static SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
	
	/**
	 * Generate a self signed certificate
	 * 
	 * @param subject The subject
	 * @param expiryDate The required expiry date
	 * @param keyPair The key pair for signing
	 * @param signatureAlgorithm The signature algorithm
	 * @return A X509v3 certificate
	 * @throws CertificateException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertIOException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 */
	public static X509Certificate generateSelfSignedCertificate(X500Name subject, ZonedDateTime expiryDate,
			KeyPair keyPair, SignatureAlgorithm signatureAlgorithm)
			throws CertificateException, OperatorCreationException, CertIOException, IOException {
		return generateSelfSignedCertificate(subject, expiryDate, keyPair, signatureAlgorithm, false);
	}

	/**
	 * Generate a self signed certificate
	 * 
	 * @param subject The subject
	 * @param expiryDate The required expiry date
	 * @param keyPair The key pair for signing
	 * @param signatureAlgorithm The signature algorithm
	 * @param isCA TRUE if this is a ROOT CA certificate.
	 * @return A X509v3 certificate
	 * @throws CertificateException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertIOException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 */
	public static X509Certificate generateSelfSignedCertificate(X500Name subject, ZonedDateTime expiryDate,
			KeyPair keyPair, SignatureAlgorithm signatureAlgorithm, boolean isCA)
			throws CertificateException, OperatorCreationException, CertIOException, IOException {
		return generateSelfSignedCertificate(subject, ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE), expiryDate, keyPair,
				signatureAlgorithm, isCA);
	}

	/**
	 * Generate a self signed certificate
	 * 
	 * @param subject The subject
	 * @param startDate The required start date
	 * @param expiryDate The required expiry date
	 * @param keyPair The key pair for signing
	 * @param signatureAlgorithm The signature algorithm
	 * @param isCA TRUE if this is a ROOT CA certificate.
	 * @return A X509v3 certificate
	 * @throws CertificateException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertIOException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 */
	public static X509Certificate generateSelfSignedCertificate(X500Name subject, ZonedDateTime startDate,
			ZonedDateTime expiryDate, KeyPair keyPair, SignatureAlgorithm signatureAlgorithm, boolean isCA)
			throws CertificateException, OperatorCreationException, CertIOException, IOException {
		return generateSelfSignedCertificate(subject, startDate, expiryDate, keyPair, signatureAlgorithm, isCA, null);
	}

	/**
	 * Generate a self signed certificate
	 * 
	 * @param subject The subject
	 * @param startDate The required start date
	 * @param expiryDate The required expiry date
	 * @param keyPair The key pair for signing
	 * @param signatureAlgorithm The signature algorithm
	 * @param isCA TRUE if this is a ROOT CA certificate.
	 * @param CRLLocation The CRL Location.
	 * @return A X509v3 certificate
	 * @throws CertificateException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertIOException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 */
	public static X509Certificate generateSelfSignedCertificate(X500Name subject, ZonedDateTime startDate,
			ZonedDateTime expiryDate, KeyPair keyPair, SignatureAlgorithm signatureAlgorithm, boolean isCA,
			GeneralName CRLLocation)
			throws CertificateException, OperatorCreationException, CertIOException, IOException {
		if (startDate.isAfter(expiryDate)) {
			throw new IllegalArgumentException("Expiry Date before Start Date");
		}

		X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(subject, //
				BigInteger.valueOf(System.currentTimeMillis()), //
				Date.from(startDate.toInstant()), //
				Date.from(expiryDate.toInstant()), //
				subject, //
				SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(keyPair.getPublic().getEncoded())));

		if (isCA) {
			certGen.addExtension(Extension.subjectKeyIdentifier, true, createSubjectKeyIdentifier(keyPair.getPublic()));
			certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));

			KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign);
			certGen.addExtension(Extension.keyUsage, true, usage);

			if (CRLLocation != null) {
				DistributionPointName name = new DistributionPointName(DistributionPointName.FULL_NAME,
						new GeneralNames(CRLLocation));
				DistributionPoint point = new DistributionPoint(name, null, new GeneralNames(new GeneralName(subject)));
				certGen.addExtension(Extension.cRLDistributionPoints, false,
						new CRLDistPoint(new DistributionPoint[] { point }));
			}
		}
		JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
		conv.setProvider(BouncyCastleProvider.PROVIDER_NAME);
		ContentSigner cs = getContentSigner(keyPair.getPrivate(), signatureAlgorithm);
		return conv.getCertificate(certGen.build(cs));
	}

	/**
	 * Generate a self signed certificate
	 * 
	 * @param subject The subject
	 * @param startDate The required start date
	 * @param expiryDate The required expiry date
	 * @param keyPair The key pair for signing
	 * @param signatureAlgorithm The signature algorithm
	 * @param request A certificate request.
	 * @return A X509v3 certificate
	 * @throws CertificateException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertIOException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 */
	public static X509Certificate generateSelfSignedCertificate(X500Name subject, ZonedDateTime startDate,
			ZonedDateTime expiryDate, KeyPair keyPair, SignatureAlgorithm signatureAlgorithm,
			ICertificateRequest request)
			throws CertificateException, OperatorCreationException, CertIOException, IOException {
		if (startDate.isAfter(expiryDate)) {
			throw new IllegalArgumentException("Expiry Date before Start Date");
		}

		X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(subject, //
				BigInteger.valueOf(System.currentTimeMillis()), //
				Date.from(startDate.toInstant()), //
				Date.from(expiryDate.toInstant()), //
				subject, //
				request.getSubjectPublicKeyInfo());

		certGen.addExtension(Extension.subjectKeyIdentifier, true, createSubjectKeyIdentifier(keyPair.getPublic()));
		// Check for intermediate CA generation.
		if (request.isCARequest()) {
			// Ensure CA Depth is fixed at 1.
			certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));

			if (request.getCertificatePolicies() != null) {
				certGen.addExtension(Extension.certificatePolicies, false, request.getCertificatePolicies());
			}
			if (request.getCRLDistributionPoint() != null) {
				certGen.addExtension(Extension.cRLDistributionPoints, false, request.getCRLDistributionPoint());
			}
		}

		// Add key usage and extended key usage details.
		if (request.getKeyUsage() != null) {
			certGen.addExtension(Extension.keyUsage, true, request.getKeyUsage());
		}

		if (request.getExtendedKeyUsage() != null) {
			certGen.addExtension(Extension.extendedKeyUsage, true, request.getExtendedKeyUsage());
		}

		if (request.getSubjectAlternativeName() != null) {
			certGen.addExtension(Extension.subjectAlternativeName, true, request.getSubjectAlternativeName());
		}

		JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
		conv.setProvider(BouncyCastleProvider.PROVIDER_NAME);
		ContentSigner cs = getContentSigner(keyPair.getPrivate(), signatureAlgorithm);
		return conv.getCertificate(certGen.build(cs));
	}

	/**
	 * Create an ASN1 Subject Key Identifier
	 * 
	 * @param key The public key
	 * @return ASN1 Subject Key Identifier
	 * @throws IOException ASN1 sequence creation failed.
	 */
	private static SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey key) throws IOException {
		try (ASN1InputStream is = new ASN1InputStream(new ByteArrayInputStream(key.getEncoded()));) {
			ASN1Sequence seq = (ASN1Sequence) is.readObject();
			SubjectPublicKeyInfo info = SubjectPublicKeyInfo.getInstance(seq);
			return new BcX509ExtensionUtils().createSubjectKeyIdentifier(info);
		}
	}

	/**
	 * Sign the certificate request
	 * 
	 * @param issuerInformation The CA
	 * @param certRequest The certificate request.
	 * @param startDate The certificate start date.
	 * @param expiryDate The certificate expiry date.
	 * @return The signed certificate.
	 * @throws IllegalArgumentException Certificate Request is missing details.
	 * @throws CertIOException Creation of the certificate fails
	 * @throws DatastoreLockedException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertificateException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 */
	public static Certificate signCertificateRequest(CertificateAuthority issuerInformation,
			ICertificateRequest certRequest, ZonedDateTime startDate, ZonedDateTime expiryDate) throws IOException,
			CertIOException, DatastoreLockedException, OperatorCreationException, CertificateException {

		if (issuerInformation == null) {
			throw new IllegalArgumentException("No Certificate Authority Provided");
		}
		if (certRequest == null) {
			throw new IllegalArgumentException("No Certificate Request Provided");
		}
		if (certRequest.getSubject() == null) {
			throw new IllegalArgumentException("Certificate Request missing subject identifier");
		}
		if (certRequest.getSubjectPublicKeyInfo() == null) {
			throw new IllegalArgumentException("Certificate Request missing Public Key");
		}
		if (startDate == null || expiryDate == null) {
			throw new IllegalArgumentException("Missing Certificate Date information");
		}

		X509Certificate caCert = (X509Certificate) issuerInformation.getCertificate();
		JcaX509CertificateHolder issuer = new JcaX509CertificateHolder(caCert);
		if (startDate.isAfter(expiryDate)) {
			throw new IllegalArgumentException("Expiry Date before Start Date");
		}
		/*
		 * Ensure the start/expiry dates are within the CA's date range.
		 */
		if (startDate.isBefore(DateTimeUtil.toZonedDateTime(issuer.getNotBefore()))) {
			throw new IllegalArgumentException("Start Date before Issuers Start Date.");
		}
		if (startDate.isAfter(DateTimeUtil.toZonedDateTime(issuer.getNotAfter()))) {
			throw new IllegalArgumentException("Start Date after Issuers Expiry Date.");
		}
		if (expiryDate.isBefore(DateTimeUtil.toZonedDateTime(issuer.getNotBefore()))) {
			throw new IllegalArgumentException("Expiry Date before Issuers Start Date.");
		}
		if (expiryDate.isAfter(DateTimeUtil.toZonedDateTime(issuer.getNotAfter()))) {
			throw new IllegalArgumentException("Expiry Date after Issuers Expiry Date.");
		}
		/*
		 * Ensure the subject X500Name is not equal to any of the issuers in the issuer cert chain.
		 */
		X500Name subject = certRequest.getSubject();
		Certificate[] issuerChain = issuerInformation.getCertificateChain();
		for (Certificate c : issuerChain) {
			X509Certificate ic = (X509Certificate) c;
			X500Name ici = X500Name.getInstance(ic.getSubjectX500Principal().getEncoded());
			if (subject.equals(ici)) {
				throw new IllegalArgumentException("Subject X500Name matches Issuer X500Name");
			}
		}

		// Start certificate generation.
		X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(//
				issuer.getSubject(), //
				issuerInformation.getNextSerialNumber(), //
				Date.from(startDate.toInstant()), //
				Date.from(expiryDate.toInstant()), //
				certRequest.getSubject(), //
				certRequest.getSubjectPublicKeyInfo());

		certGen.addExtension(//
				Extension.subjectKeyIdentifier, //
				true, //
				new DEROctetString(certRequest.getSubjectPublicKeyInfo()));

		certGen.addExtension(//
				Extension.authorityKeyIdentifier, //
				true, //
				new AuthorityKeyIdentifier(//
						new GeneralNames(//
								new GeneralName(//
										issuer.getSubject())),
						issuer.getSerialNumber()));

		// Add key usage and extended key usage details.
		if (certRequest.getKeyUsage() != null) {
			certGen.addExtension(Extension.keyUsage, true, certRequest.getKeyUsage());
		}

		if (certRequest.getExtendedKeyUsage() != null) {
			certGen.addExtension(Extension.extendedKeyUsage, true, certRequest.getExtendedKeyUsage());
		}

		if (certRequest.getSubjectAlternativeName() != null) {
			certGen.addExtension(Extension.subjectAlternativeName, true, certRequest.getSubjectAlternativeName());
		}

		// Check for intermediate CA generation.
		if (certRequest.isCARequest()) {
			// Ensure CA Depth is fixed at 1.
			if (issuerInformation.canCreateIntermediateCA()) {
				certGen.addExtension(Extension.basicConstraints, true, new BasicConstraints(0));
			} else {
				throw new IllegalStateException("Unable to create Intermediate CA Certificate");
			}
			if (certRequest.getCertificatePolicies() != null) {
				certGen.addExtension(Extension.certificatePolicies, false, certRequest.getCertificatePolicies());
			}
			if (certRequest.getCRLDistributionPoint() != null) {
				certGen.addExtension(Extension.cRLDistributionPoints, false, certRequest.getCRLDistributionPoint());
			}
		}

		// Now generate the certificate.
		JcaX509CertificateConverter conv = new JcaX509CertificateConverter();
		conv.setProvider(BouncyCastleProvider.PROVIDER_NAME);
		ContentSigner cs = getContentSigner(issuerInformation.getKeyPair().getPrivate(),
				issuerInformation.getSignatureAlgorithm());
		return conv.getCertificate(certGen.build(cs));
	}

	/**
	 * Attempt to determine if the supplied IssuedCertificate instance is capable of signing.
	 * 
	 * @param issuedCertificate The certificate to check.
	 * @return TRUE if this appears to be a CA certificate.
	 * @throws CertificateEncodingException The Certificate is invalid.
	 */
	public static boolean isCACertificate(IIssuedCertificate issuedCertificate) throws CertificateEncodingException {
		if (issuedCertificate == null) {
			throw new IllegalArgumentException("Passed Certificate is null");
		}
		Certificate[] chain = issuedCertificate.getCertificateChain();
		if (chain == null || chain.length == 0) {
			throw new IllegalStateException("Missing Certificate Chain?");
		}
		X509Certificate cert = (X509Certificate) chain[0];
		if (cert.getBasicConstraints() < 0) {
			return false;
		}
		// Check any extensions...
		JcaX509CertificateHolder x509ch = new JcaX509CertificateHolder(cert);
		KeyUsage x509KeyUsage = KeyUsage.fromExtensions(x509ch.getExtensions());
		if (x509KeyUsage != null) {
			return x509KeyUsage.hasUsages(KeyUsage.cRLSign | KeyUsage.keyCertSign);
		}
		return true;
	}

	/**
	 * Generate a CRL for the given CA.
	 * 
	 * @param issuerInformation The CA which contains the revoked certificates.
	 * @param nextUpdate The next expected update of the CRL
	 * @return The generated CRL
	 * @throws IOException Writing to the file failed
	 * @throws CertificateEncodingException Unable to create the signing information.
	 * @throws OperatorCreationException Unable to create the DER encoded CRL
	 * @throws DatastoreLockedException The issuers information is currently locked.
	 * @throws CRLException If generation of the CRL fails.
	 */
	public static X509CRL generateCRL(CertificateAuthority issuerInformation, ZonedDateTime nextUpdate)
			throws DatastoreLockedException, CertificateEncodingException, IOException, OperatorCreationException,
			CRLException {

		ZonedDateTime now = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
		X509Certificate caCert = (X509Certificate) issuerInformation.getCertificate();
		JcaX509CertificateHolder issuer = new JcaX509CertificateHolder(caCert);

		X509v2CRLBuilder crlGen = new X509v2CRLBuilder(issuer.getSubject(), Date.from(now.toInstant()));
		crlGen.setNextUpdate(Date.from(nextUpdate.toInstant()));
		crlGen.addExtension(Extension.cRLNumber, false, new CRLNumber(issuerInformation.getNextSerialCRLNumber()));
		crlGen.addExtension(//
				Extension.authorityKeyIdentifier, //
				true, //
				new AuthorityKeyIdentifier(//
						new GeneralNames(new GeneralName(issuer.getSubject())), issuer.getSerialNumber()));

		List<IssuedCertificateProperties> revoked = new ArrayList<>(issuerInformation.getRevokedCertificates());
		for (IssuedCertificateProperties cert : revoked) {
			ZonedDateTime expiryDate = DateTimeUtil.toZonedDateTime(cert.getProperty(Key.endDate));
			// Only add if not already expired.
			if (now.isBefore(expiryDate)) {
				BigInteger serialNum = BigInteger
						.valueOf(Long.parseLong(cert.getProperty(Key.certificateSerialNumber)));
				ZonedDateTime revokeDate = DateTimeUtil.toZonedDateTime(cert.getProperty(Key.revokeDate));
				int reason = RevokeReasonCode.valueOf(cert.getProperty(Key.revokeCode)).getCode();
				crlGen.addCRLEntry(serialNum, Date.from(revokeDate.toInstant()), reason);
			}
		}
		ContentSigner cs = getContentSigner(issuerInformation.getKeyPair().getPrivate(),
				issuerInformation.getSignatureAlgorithm());
		return new JcaX509CRLConverter().getCRL(crlGen.build(cs));
	}

	/**
	 * Get the applicable Content Signer.
	 * 
	 * @param privKey The private key to sign with
	 * @param signatureAlgorithm The signature algorithm
	 * @return An applicable ContentSigner.
	 * @throws OperatorCreationException
	 * @throws NoSuchProviderException
	 * @throws NoSuchAlgorithmException
	 */
	private static ContentSigner getContentSigner(PrivateKey privKey, SignatureAlgorithm signatureAlgorithm)
			throws OperatorCreationException {
		// GOST3411withDSTU4145 + Rainbow is missing from the BC DefaultSignatureAlgorithmIdentifierFinder
		if (signatureAlgorithm.isInBCCentralDirectory()) {
				return new JcaContentSignerBuilder(signatureAlgorithm.getAlgID())
						.setProvider(signatureAlgorithm.getProvider())//
						.build(privKey);
		} else {
			// Manually create the content signer.
			try {
				final Signature sig = Signature.getInstance(signatureAlgorithm.getAlgID(),
						signatureAlgorithm.getProvider());
				final AlgorithmIdentifier signatureAlgId = new AlgorithmIdentifier(signatureAlgorithm.getOID());
				sig.initSign(privKey, random);

				return new ContentSigner() {
					private SignatureOutputStream stream = new SignatureOutputStream(sig);

					public AlgorithmIdentifier getAlgorithmIdentifier() {
						return signatureAlgId;
					}

					public OutputStream getOutputStream() {
						return stream;
					}

					public byte[] getSignature() {
						try {
							return stream.getSignature();
						} catch (SignatureException e) {
							throw new RuntimeOperatorException("exception obtaining signature: " + ExceptionUtil.getMessage(e), e);
						}
					}
				};
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException e) {
				throw new OperatorCreationException(ExceptionUtil.getMessage(e), e);
			}
		}
	}

	/**
	 * Signature Output Stream.
	 * <p>
	 * Based on {@link org.bouncycastle.operator.jcajce.JcaContentSignerBuilder.SignatureOutputStream}
	 */
	private static class SignatureOutputStream extends OutputStream {
		private Signature sig;

		SignatureOutputStream(Signature sig) {
			this.sig = sig;
		}

		public void write(byte[] bytes, int off, int len) throws IOException {
			try {
				sig.update(bytes, off, len);
			} catch (SignatureException e) {
				throw new OperatorStreamException("exception in content signer: " + ExceptionUtil.getMessage(e), e);
			}
		}

		public void write(byte[] bytes) throws IOException {
			try {
				sig.update(bytes);
			} catch (SignatureException e) {
				throw new OperatorStreamException("exception in content signer: " + ExceptionUtil.getMessage(e), e);
			}
		}

		public void write(int b) throws IOException {
			try {
				sig.update((byte) b);
			} catch (SignatureException e) {
				throw new OperatorStreamException("exception in content signer: " + ExceptionUtil.getMessage(e), e);
			}
		}

		public byte[] getSignature() throws SignatureException {
			return sig.sign();
		}
	}
}
