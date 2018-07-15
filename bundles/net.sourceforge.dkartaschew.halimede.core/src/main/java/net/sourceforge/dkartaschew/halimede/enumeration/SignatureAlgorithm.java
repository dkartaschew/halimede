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

package net.sourceforge.dkartaschew.halimede.enumeration;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;

import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.exceptions.UnknownKeyTypeException;

/**
 * Collection helper to get valid Certificate Signature algorithms for given key types.
 */
public enum SignatureAlgorithm {

	/*
	 * EC
	 */
	SHA1withECDSA("SHA1withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1, true), //
	SHA224withECDSA("SHA224withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224, true), //
	SHA256withECDSA("SHA256withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256, true), //
	SHA384withECDSA("SHA384withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384, true), //
	SHA512withECDSA("SHA512withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512, true), //
	SHA3_224withECDSA("SHA3-224withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_224, true), //
	SHA3_256withECDSA("SHA3-256withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_256, true), //
	SHA3_384withECDSA("SHA3-384withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_384, true), //
	SHA3_512withECDSA("SHA3-512withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_512, true), //

	/*
	 * GM SM2
	 */
	SM3withSM2("SM3withSM2", GMObjectIdentifiers.sm2sign_with_sm3, true),
	// Not implemented
//	SHA1withSM2("SHA1withSM2", GMObjectIdentifiers.sm2sign_with_sha1, false),
//	SHA256withSM2("SHA256withSM2", GMObjectIdentifiers.sm2sign_with_sha256, false),
//	SHA512withSM2("SHA512withSM2", GMObjectIdentifiers.sm2sign_with_sha512, false),
//	SHA224withSM2("SHA224withSM2", GMObjectIdentifiers.sm2sign_with_sha224, false),
//	SHA384withSM2("SHA384withSM2", GMObjectIdentifiers.sm2sign_with_sha384, false),
//	RIPEMD160withSM2("RIPEMD160withSM2", GMObjectIdentifiers.sm2sign_with_rmd160, false),
//	WHIRLPOOLwithSM2("WHIRLPOOLwithSM2", GMObjectIdentifiers.sm2sign_with_whirlpool, false),
//	BLAKE2B512withSM2("Blake2B-512withSM2", GMObjectIdentifiers.sm2sign_with_blake2b512, false),
//	BLAKE2S256withSM2("Blake2S-256withSM2", GMObjectIdentifiers.sm2sign_with_blake2s256, false),

	/*
	 * DSA
	 */
	SHA1withDSA("SHA1withDSA", X9ObjectIdentifiers.id_dsa_with_sha1, true), //
	SHA224withDSA("SHA224withDSA", NISTObjectIdentifiers.dsa_with_sha224, true), //
	SHA256withDSA("SHA256withDSA", NISTObjectIdentifiers.dsa_with_sha256, true), //
	SHA384withDSA("SHA384withDSA", NISTObjectIdentifiers.dsa_with_sha384, true), //
	SHA512withDSA("SHA512withDSA", NISTObjectIdentifiers.dsa_with_sha512, true), //
	SHA3_224withDSA("SHA3-224withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_224, true), //
	SHA3_256withDSA("SHA3-256withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_256, true), //
	SHA3_384withDSA("SHA3-384withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_384, true), //
	SHA3_512withDSA("SHA3-512withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_512, true), //

	/*
	 * RSA
	 */
	MD2withRSA("MD2withRSA", PKCSObjectIdentifiers.md2WithRSAEncryption, true), //
	MD5withRSA("MD5withRSA", PKCSObjectIdentifiers.md5WithRSAEncryption, true), //
	SHA1withRSA("SHA1withRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption, true), //
	SHA224withRSA("SHA224withRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption, true), //
	SHA256withRSA("SHA256withRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption, true), //
	SHA384withRSA("SHA384withRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption, true), //
	SHA512withRSA("SHA512withRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption, true), //
	SHA3_224withRSA("SHA3-224withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224, true), //
	SHA3_256withRSA("SHA3-256withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256, true), //
	SHA3_384withRSA("SHA3-384withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384, true), //
	SHA3_512withRSA("SHA3-512withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512, true), //
	RIPEMD128withRSA("RIPEMD128withRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128, true), //
	RIPEMD160withRSA("RIPEMD160withRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160, true), //
	RIPEMD256withRSA("RIPEMD256withRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256, true), //

	/*
	 * GOST3410
	 */
	GOST3411withGOST3410("GOST3411withGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, true), //
	GOST3411withECGOST3410("GOST3411withECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001, true), //
	GOST3411withECGOST3410_2012_256("GOST3411-2012-256WITHECGOST3410-2012-256",
			RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256, true), //
	GOST3411withECGOST3410_2012_512("GOST3411-2012-512WITHECGOST3410-2012-512",
			RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512, true),

	/**
	 * DSTU4145
	 */
	GOST3411withDSTU4145("GOST3411withDSTU4145", UAObjectIdentifiers.dstu4145be, false);

	private final String algID;
	private final ASN1ObjectIdentifier oid;
	private final boolean inBCCentralDirectory;

	/**
	 * Create a signature type
	 * 
	 * @param algID                The algorithm Name.
	 * @param oid                  The ASN1 Object ID.
	 * @param inBCCentralDirectory TRUE if this is in the BC central directory algorithm finder.
	 */
	private SignatureAlgorithm(String algID, ASN1ObjectIdentifier oid, boolean inBCCentralDirectory) {
		this.algID = algID;
		this.oid = oid;
		this.inBCCentralDirectory = inBCCentralDirectory;
	}

	/**
	 * Get the Algorithm name
	 * 
	 * @return The name of the algorithm
	 */
	public String getAlgID() {
		return algID;
	}

	/**
	 * The algorithm OID
	 * 
	 * @return The algorithm OID
	 */
	public ASN1ObjectIdentifier getOID() {
		return oid;
	}

	/**
	 * Is this algorithm in the BC central directory?
	 * <p>
	 * See {@link org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder}
	 * 
	 * @return TRUE if this is in the Central Directory.
	 */
	public boolean isInBCCentralDirectory() {
		return inBCCentralDirectory;
	}

	@Override
	public String toString() {
		return algID;
	}

	/**
	 * Get the signature based on the given description
	 * 
	 * @param value The algID obtained from the signature
	 * @return The signature based on the named algID
	 * @throws NoSuchElementException The description given doesn't match a known element.
	 * @throws NullPointerException   The description was null.
	 */
	public static Object forAlgID(String value) {
		Objects.requireNonNull(value, "AlgorithmID was null");
		return Arrays.stream(SignatureAlgorithm.values()).filter(i -> i.algID.equals(value)).findFirst().get();
	}

	/**
	 * Get the signature based on the given description
	 * 
	 * @param value The algID obtained from the signature
	 * @return The signature based on the named algID
	 * @throws NoSuchElementException The description given doesn't match a known element.
	 * @throws NullPointerException   The description was null.
	 */
	public static Object forOID(ASN1ObjectIdentifier value) {
		Objects.requireNonNull(value, "ASN1ObjectIdentifier was null");
		return Arrays.stream(SignatureAlgorithm.values()).filter(i -> i.oid.equals(value)).findFirst().get();
	}

	/**
	 * Get a collection of signature algorithms applicable for the given key type.
	 * 
	 * @param key The key type
	 * @return A collection of application algorithms for Certificates. (Collection will be empty if key is unknown).
	 */
	public static Collection<SignatureAlgorithm> forType(KeyType key) {
		Objects.requireNonNull(key, "Key is null");
		if (key == KeyType.EC_sm2p256v1) {
			return Arrays.asList(new SignatureAlgorithm[] { //
					SM3withSM2, //
//					SHA1withSM2, //
//					SHA256withSM2, //
//					SHA512withSM2, //
//					SHA224withSM2, //
//					SHA384withSM2, //
//					RIPEMD160withSM2, //
//					WHIRLPOOLwithSM2, //
//					BLAKE2B512withSM2, //
//					BLAKE2S256withSM2 
					});
		}
		switch (key.getType()) {
		case "ECDSA":
		case "EC":
			return Arrays.asList(new SignatureAlgorithm[] { //
					SHA1withECDSA, //
					SHA224withECDSA, //
					SHA256withECDSA, //
					SHA384withECDSA, //
					SHA512withECDSA, //
					SHA3_224withECDSA, //
					SHA3_256withECDSA, //
					SHA3_384withECDSA, //
					SHA3_512withECDSA });

		case "DSA":
			return Arrays.asList(new SignatureAlgorithm[] { //
					SHA1withDSA, //
					SHA224withDSA, //
					SHA256withDSA, //
					SHA384withDSA, //
					SHA512withDSA, //
					SHA3_224withDSA, //
					SHA3_256withDSA, //
					SHA3_384withDSA, //
					SHA3_512withDSA });

		case "GOST3410":
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withGOST3410 });

		case "DSTU4145":
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withDSTU4145 });

		case "ECGOST3410":
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withECGOST3410 });

		case "ECGOST3410-2012":
			if (key.getBitLength() == 256) {
				return Arrays.asList(new SignatureAlgorithm[] { //
						GOST3411withECGOST3410_2012_256 });
			} else {
				return Arrays.asList(new SignatureAlgorithm[] { //
						GOST3411withECGOST3410_2012_512 });
			}

		case "RSA":
			// All are application, only if bit length > 512.
			if (key.getBitLength() > 512) {
				return Arrays.asList(new SignatureAlgorithm[] { //
						MD2withRSA, //
						MD5withRSA, //
						SHA1withRSA, //
						SHA224withRSA, //
						SHA256withRSA, //
						SHA384withRSA, //
						SHA512withRSA, //
						SHA3_224withRSA, //
						SHA3_256withRSA, //
						SHA3_384withRSA, //
						SHA3_512withRSA, //
						RIPEMD128withRSA, //
						RIPEMD160withRSA, //
						RIPEMD256withRSA });
			} else {
				// reduced list if bit length <= 512
				return Arrays.asList(new SignatureAlgorithm[] { //
						MD2withRSA, //
						MD5withRSA, //
						SHA1withRSA, //
						SHA224withRSA, //
						SHA256withRSA, //
						RIPEMD128withRSA, //
						RIPEMD160withRSA, //
						RIPEMD256withRSA });
			}
		}
		return Collections.emptyList();
	}

	/**
	 * Get a collection of signature algorithms applicable for the given key type.
	 * 
	 * @param key The key type
	 * @return A collection of application algorithms for Certificates. (Collection will be empty if key was is
	 *         unknown).
	 */
	public static Collection<SignatureAlgorithm> forType(SignatureAlgorithm key) {
		Objects.requireNonNull(key, "Signature is null");
		switch (key) {
		case SHA1withECDSA:
		case SHA224withECDSA:
		case SHA256withECDSA:
		case SHA384withECDSA:
		case SHA512withECDSA:
		case SHA3_224withECDSA:
		case SHA3_256withECDSA:
		case SHA3_384withECDSA:
		case SHA3_512withECDSA:
			return Arrays.asList(new SignatureAlgorithm[] { //
					SHA1withECDSA, //
					SHA224withECDSA, //
					SHA256withECDSA, //
					SHA384withECDSA, //
					SHA512withECDSA, //
					SHA3_224withECDSA, //
					SHA3_256withECDSA, //
					SHA3_384withECDSA, //
					SHA3_512withECDSA });

		case SM3withSM2:
//		case SHA1withSM2:
//		case SHA256withSM2:
//		case SHA512withSM2:
//		case SHA224withSM2:
//		case SHA384withSM2:
//		case RIPEMD160withSM2:
//		case WHIRLPOOLwithSM2:
//		case BLAKE2B512withSM2:
//		case BLAKE2S256withSM2:
			return Arrays.asList(new SignatureAlgorithm[] { //
					SM3withSM2, //
//					SHA1withSM2, //
//					SHA256withSM2, //
//					SHA512withSM2, //
//					SHA224withSM2, //
//					SHA384withSM2, //
//					RIPEMD160withSM2, //
//					WHIRLPOOLwithSM2, //
//					BLAKE2B512withSM2, //
//					BLAKE2S256withSM2 
					});

		case SHA1withDSA:
		case SHA224withDSA:
		case SHA256withDSA:
		case SHA384withDSA:
		case SHA512withDSA:
		case SHA3_224withDSA:
		case SHA3_256withDSA:
		case SHA3_384withDSA:
		case SHA3_512withDSA:
			return Arrays.asList(new SignatureAlgorithm[] { //
					SHA1withDSA, //
					SHA224withDSA, //
					SHA256withDSA, //
					SHA384withDSA, //
					SHA512withDSA, //
					SHA3_224withDSA, //
					SHA3_256withDSA, //
					SHA3_384withDSA, //
					SHA3_512withDSA });

		case GOST3411withDSTU4145:
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withDSTU4145 });

		case GOST3411withGOST3410:
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withGOST3410 });

		case GOST3411withECGOST3410:
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withECGOST3410 });

		case GOST3411withECGOST3410_2012_256:
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withECGOST3410_2012_256 });

		case GOST3411withECGOST3410_2012_512:
			return Arrays.asList(new SignatureAlgorithm[] { //
					GOST3411withECGOST3410_2012_512 });

		case MD2withRSA:
		case MD5withRSA:
		case SHA1withRSA:
		case SHA224withRSA:
		case SHA256withRSA:
		case SHA384withRSA:
		case SHA512withRSA:
		case SHA3_224withRSA:
		case SHA3_256withRSA:
		case SHA3_384withRSA:
		case SHA3_512withRSA:
		case RIPEMD128withRSA:
		case RIPEMD160withRSA:
		case RIPEMD256withRSA:
			return Arrays.asList(new SignatureAlgorithm[] { //
					MD2withRSA, //
					MD5withRSA, //
					SHA1withRSA, //
					SHA224withRSA, //
					SHA256withRSA, //
					SHA384withRSA, //
					SHA512withRSA, //
					SHA3_224withRSA, //
					SHA3_256withRSA, //
					SHA3_384withRSA, //
					SHA3_512withRSA, //
					RIPEMD128withRSA, //
					RIPEMD160withRSA, //
					RIPEMD256withRSA });
		}
		return Collections.emptyList();
	}

	/**
	 * Get the default Signature for the given Key
	 * 
	 * @param key The key type.
	 * @return A default signature
	 */
	public static SignatureAlgorithm getDefaultSignature(PublicKey key) {
		Objects.requireNonNull(key, "Public Key is null");
		switch (key.getAlgorithm()) {
		case "ECDSA":
		case "EC":
			try {
				if (KeyType.forKey(key) == KeyType.EC_sm2p256v1) {
					return SignatureAlgorithm.SM3withSM2;
				}
			} catch (UnknownKeyTypeException e) {
				// Ignore and use default;
			}
			return SignatureAlgorithm.SHA512withECDSA;
		case "GOST3410":
			return SignatureAlgorithm.GOST3411withGOST3410;
		case "ECGOST3410":
			return SignatureAlgorithm.GOST3411withECGOST3410;
		case "ECGOST3410-2012":
			if (KeyPairFactory.getKeyLength(key) == 512) {
				return SignatureAlgorithm.GOST3411withECGOST3410_2012_512;
			} else {
				return SignatureAlgorithm.GOST3411withECGOST3410_2012_256;
			}
		case "DSTU4145":
			return SignatureAlgorithm.GOST3411withDSTU4145;
		case "DSA":
			return SignatureAlgorithm.SHA1withDSA;
		case "RSA":
			return SignatureAlgorithm.SHA256withRSA;
		}
		return null;
	}
}
