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

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;

import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;

/**
 * Collection helper to get valid Certificate Signature algorithms for given key types.
 */
public enum SignatureAlgorithm {

	/*
	 * EC
	 */
	SHA1withECDSA("SHA1withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA1), //
	SHA224withECDSA("SHA224withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA224), //
	SHA256withECDSA("SHA256withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA256), //
	SHA384withECDSA("SHA384withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA384), //
	SHA512withECDSA("SHA512withECDSA", X9ObjectIdentifiers.ecdsa_with_SHA512), //
	SHA3_224withECDSA("SHA3-224withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_224), //
	SHA3_256withECDSA("SHA3-256withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_256), //
	SHA3_384withECDSA("SHA3-384withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_384), //
	SHA3_512withECDSA("SHA3-512withECDSA", NISTObjectIdentifiers.id_ecdsa_with_sha3_512), //

	/*
	 * DSA
	 */
	SHA1withDSA("SHA1withDSA", X9ObjectIdentifiers.id_dsa_with_sha1), //
	SHA224withDSA("SHA224withDSA", NISTObjectIdentifiers.dsa_with_sha224), //
	SHA256withDSA("SHA256withDSA", NISTObjectIdentifiers.dsa_with_sha256), //
	SHA384withDSA("SHA384withDSA", NISTObjectIdentifiers.dsa_with_sha384), //
	SHA512withDSA("SHA512withDSA", NISTObjectIdentifiers.dsa_with_sha512), //
	SHA3_224withDSA("SHA3-224withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_224), //
	SHA3_256withDSA("SHA3-256withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_256), //
	SHA3_384withDSA("SHA3-384withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_384), //
	SHA3_512withDSA("SHA3-512withDSA", NISTObjectIdentifiers.id_dsa_with_sha3_512), //

	/*
	 * RSA
	 */
	MD2withRSA("MD2withRSA", PKCSObjectIdentifiers.md2WithRSAEncryption), //
	MD5withRSA("MD5withRSA", PKCSObjectIdentifiers.md5WithRSAEncryption), //
	SHA1withRSA("SHA1withRSA", PKCSObjectIdentifiers.sha1WithRSAEncryption), //
	SHA224withRSA("SHA224withRSA", PKCSObjectIdentifiers.sha224WithRSAEncryption), //
	SHA256withRSA("SHA256withRSA", PKCSObjectIdentifiers.sha256WithRSAEncryption), //
	SHA384withRSA("SHA384withRSA", PKCSObjectIdentifiers.sha384WithRSAEncryption), //
	SHA512withRSA("SHA512withRSA", PKCSObjectIdentifiers.sha512WithRSAEncryption), //
	SHA3_224withRSA("SHA3-224withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224), //
	SHA3_256withRSA("SHA3-256withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256), //
	SHA3_384withRSA("SHA3-384withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384), //
	SHA3_512withRSA("SHA3-512withRSA", NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512), //
	RIPEMD128withRSA("RIPEMD128withRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128), //
	RIPEMD160withRSA("RIPEMD160withRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160), //
	RIPEMD256withRSA("RIPEMD256withRSA", TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256), //

	/*
	 * GOST3410
	 */
	GOST3411withGOST3410("GOST3411withGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94), //
	GOST3411withECGOST3410("GOST3411withECGOST3410", CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001), //
	GOST3411withECGOST3410_2012_256("GOST3411-2012-256WITHECGOST3410-2012-256",	RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256), //
	GOST3411withECGOST3410_2012_512("GOST3411-2012-512WITHECGOST3410-2012-512",	RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512),
	
	/**
	 * DSTU4145
	 */
	GOST3411withDSTU4145("GOST3411withDSTU4145", UAObjectIdentifiers.dstu4145be);
	
	private final String algID;
	private final ASN1ObjectIdentifier oid;

	/**
	 * Create a signature type
	 * 
	 * @param algID The algorithm Name.
	 * @param oid The ASN1 Object ID.
	 */
	private SignatureAlgorithm(String algID, ASN1ObjectIdentifier oid) {
		this.algID = algID;
		this.oid = oid;
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
	 * @throws NullPointerException The description was null.
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
	 * @throws NullPointerException The description was null.
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
					SHA3_512withECDSA});

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
					SHA3_512withECDSA});

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
			return SignatureAlgorithm.SHA512withECDSA;
		case "GOST3410":
			return SignatureAlgorithm.GOST3411withGOST3410;
		case "ECGOST3410":
			return SignatureAlgorithm.GOST3411withECGOST3410;
		case "ECGOST3410-2012":
			if (KeyPairFactory.getKeyLength(new KeyPair(key, null)) == 512) {
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
