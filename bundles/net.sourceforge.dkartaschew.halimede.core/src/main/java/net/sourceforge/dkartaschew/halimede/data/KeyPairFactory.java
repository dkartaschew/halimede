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

package net.sourceforge.dkartaschew.halimede.data;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.edec.BCEdDSAPublicKey;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.GOST3410PublicKey;
import org.bouncycastle.jce.provider.JCEECPublicKey;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.pqc.asn1.RainbowPublicKey;
import org.bouncycastle.pqc.asn1.XMSSMTPublicKey;
import org.bouncycastle.pqc.asn1.XMSSPublicKey;
import org.bouncycastle.pqc.jcajce.provider.qtesla.BCqTESLAPublicKey;
import org.bouncycastle.pqc.jcajce.provider.rainbow.BCRainbowPublicKey;
import org.bouncycastle.pqc.jcajce.provider.sphincs.BCSphincs256PublicKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSMTPublicKey;
import org.bouncycastle.pqc.jcajce.provider.xmss.BCXMSSPublicKey;
import org.bouncycastle.pqc.jcajce.spec.QTESLAParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.SPHINCS256KeyGenParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSMTParameterSpec;
import org.bouncycastle.pqc.jcajce.spec.XMSSParameterSpec;

import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.exceptions.UnknownKeyTypeException;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

/**
 * Private/Public key Factory.
 */
public class KeyPairFactory {

	/**
	 * RND Generator for keying material.
	 */
	private final static SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
	
	/*
	 * Setup BC crypto provider.
	 */
	static {
		ProviderUtil.setupProviders();
	}

	/**
	 * Generate a key pair of the given type
	 * 
	 * @param type The keying material type to generate
	 * @return A private / public key pair.
	 * @throws NoSuchAlgorithmException The provider does not support the type given
	 * @throws NoSuchProviderException The provider does not exist.
	 * @throws InvalidAlgorithmParameterException The alogorithm parameter is invalid.
	 */
	public static KeyPair generateKeyPair(KeyType type)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		Objects.requireNonNull(type, "KeyType was null");
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance(type.getType(), type.getProvider());
		switch (type.getType()) {
		case "DSA":
		case "RSA":
			keyGen.initialize(type.getBitLength(), random);
			break;
		case "ECDSA":
			keyGen.initialize(ECNamedCurveTable.getParameterSpec(type.getParameters()), random);
			break;
		case "ECGOST3410":
		case "ECGOST3410-2012":
			keyGen.initialize(ECGOST3410NamedCurveTable.getParameterSpec(type.getParameters()), random);
			break;
		case "GOST3410":
			keyGen.initialize(new GOST3410ParameterSpec(type.getParameters()), random);
			break;
		case "DSTU4145":
			ECDomainParameters params = DSTU4145NamedCurves.getByOID(new ASN1ObjectIdentifier(type.getParameters()));
			ECNamedCurveParameterSpec ecParams = new ECNamedCurveParameterSpec(type.getParameters(), params.getCurve(),
					params.getG(), params.getN(), params.getH(), params.getSeed());
			keyGen.initialize(ecParams, random);
			break;
		case "Ed25519":
		case "Ed448":
			keyGen.initialize(new EdDSAParameterSpec(type.getType()), random);
			break;
		case "Rainbow":
			keyGen.initialize(type.getBitLength(), random);
			break;
		case "SPHINCS256":
			keyGen.initialize(new SPHINCS256KeyGenParameterSpec(type.getParameters()), random);
			break;
		case "XMSS":
			keyGen.initialize(new XMSSParameterSpec(type.getHeight(), type.getParameters()), random);
			break;
		case "XMSSMT":
			keyGen.initialize(new XMSSMTParameterSpec(type.getHeight(), type.getLayers(), type.getParameters()), random);
			break;
		case "qTESLA":
			keyGen.initialize(new QTESLAParameterSpec(type.getParameters()), random);
			break;
		}
		return keyGen.generateKeyPair();
	}

	/**
	 * Attempt to determine the key length based on the key given.
	 * 
	 * @param key The key pair
	 * @return The bitlength of the key(s)
	 */
	public static int getKeyLength(KeyPair key) {
		Objects.requireNonNull(key, "KeyPair was null");
		return getKeyLength(key.getPublic());
	}

	/**
	 * Attempt to determine the key length based on the key given.
	 * 
	 * @param publicKey The PublicKey
	 * @return The bitlength of the key
	 */
	public static int getKeyLength(PublicKey publicKey) {
		if (publicKey == null) {
			return 0;
		}
		if (publicKey instanceof RSAPublicKey) {
			RSAPublicKey rsapkey = (RSAPublicKey) publicKey;
			int bitLength = rsapkey.getModulus().bitLength();
			return ((bitLength + 127) / 128) * 128;
		}
		if (publicKey instanceof DSAPublicKey) {
			DSAPublicKey dsapkey = (DSAPublicKey) publicKey;
			if (dsapkey.getParams() != null) {
				return dsapkey.getParams().getP().bitLength();
			} else {
				int bitLength = dsapkey.getY().bitLength();
				return ((bitLength + 127) / 128) * 128;
			}
		}
		if (publicKey instanceof JCEECPublicKey) {
			final JCEECPublicKey ecpriv = (JCEECPublicKey) publicKey;
			if (ecpriv.getAlgorithm().contains("GOST") && !ecpriv.getAlgorithm().contains("2012")) {
				// EC GOST-2001 is 239 bits
				return 239;
			}
			final org.bouncycastle.jce.spec.ECParameterSpec spec = ecpriv.getParameters();
			if (spec != null) {
				// return spec.getN().bitLength();
				return spec.getCurve().getFieldSize();
			} else {
				// We support the key, but we don't know the key length
				return 0;
			}
		}
		if (publicKey instanceof ECPublicKey) {
			final ECPublicKey ecpriv = (ECPublicKey) publicKey;
			if (ecpriv.getAlgorithm().contains("GOST") && !ecpriv.getAlgorithm().contains("2012")) {
				// EC GOST-2001 is 239 bits
				return 239;
			}
			final java.security.spec.ECParameterSpec spec = ecpriv.getParams();
			if (spec != null) {
				return spec.getCurve().getField().getFieldSize();
			} else {
				// We support the key, but we don't know the key length
				return 0;
			}
		}
		if (publicKey instanceof GOST3410PublicKey) {
			GOST3410PublicKey gost = (GOST3410PublicKey) publicKey;
			int length = gost.getY().bitLength();
			return ((length + 127) / 128) * 128;
		}
		if (publicKey instanceof BCEdDSAPublicKey) {
			BCEdDSAPublicKey eddsa = (BCEdDSAPublicKey) publicKey;
			switch (eddsa.getAlgorithm()) {
			case "Ed25519":
				return 256;
			case "Ed448":
				return 448;
			}
		}
		if(publicKey instanceof RainbowPublicKey || publicKey instanceof BCRainbowPublicKey) {
			return 1024;
		}
		if(publicKey instanceof BCSphincs256PublicKey) {
			return 256;
		}
		if(publicKey instanceof XMSSPublicKey || publicKey instanceof BCXMSSPublicKey) {
			//XMSSPublicKey pkey = (XMSSPublicKey)publicKey;
			if(publicKey instanceof BCXMSSPublicKey) {
				switch(((BCXMSSPublicKey) publicKey).getTreeDigest()) {
				case "SHAKE128":
				case "SHA256":
					return 256;
				case "SHAKE256":
				case "SHA512":
					return 512;
				}
			}
			return 256;
		}
		if(publicKey instanceof XMSSMTPublicKey || publicKey instanceof BCXMSSMTPublicKey) {
			//XMSSMTPublicKey pkey = (XMSSMTPublicKey)publicKey;
			//XMSSPublicKey pkey = (XMSSPublicKey)publicKey;
			if(publicKey instanceof BCXMSSMTPublicKey) {
				switch(((BCXMSSMTPublicKey) publicKey).getTreeDigest()) {
				case "SHAKE128":
				case "SHA256":
					return 256;
				case "SHAKE256":
				case "SHA512":
					return 512;
				}
			}
			return 256;
		}
		if (publicKey instanceof BCqTESLAPublicKey) {
			BCqTESLAPublicKey pkey = (BCqTESLAPublicKey) publicKey;
			switch (pkey.getAlgorithm()) {
			case "qTESLA-I":
				return KeyType.qTESLA_I.getBitLength();
			case "qTESLA-III-size":
				return KeyType.qTESLA_III_size.getBitLength();
			case "qTESLA-III-speed":
				return KeyType.qTESLA_III_speed.getBitLength();
			case "qTESLA-p-I":
				return KeyType.qTESLA_P_I.getBitLength();
			case "qTESLA-p-III":
				return KeyType.qTESLA_P_III.getBitLength();
			}
		}
		return 0;
	}

	/**
	 * Attempt to extract the public key material from the SubjectPublicKeyInfo
	 * 
	 * @param subjectPublicKeyInfo The information.
	 * @return The Keytype or NULL if not match is found.
	 * @throws IOException Decoding the key type failed.
	 */
	public static KeyType forKeyInformation(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
		try {
			return KeyType.forKey(PublicKeyDecoder.getPublicKey(subjectPublicKeyInfo));
		} catch (NoSuchAlgorithmException | InvalidKeyException | UnknownKeyTypeException e) {
			throw new IOException(e);
		}
	}
}
