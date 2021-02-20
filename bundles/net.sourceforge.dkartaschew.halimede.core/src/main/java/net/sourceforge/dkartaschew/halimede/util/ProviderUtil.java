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

package net.sourceforge.dkartaschew.halimede.util;

import java.security.Provider;
import java.security.Security;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricKeyInfoConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;
import org.bouncycastle.pqc.jcajce.provider.qtesla.QTESLAKeyFactorySpi;

/**
 * Application Utility class to correctly setup BC and BCPQC providers.
 */
public class ProviderUtil {

	/**
	 * Setup BC and BCPQC providers.
	 */
	public static void setupProviders() {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
		if (Security.getProvider(BouncyCastlePQCProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastlePQCProvider());

			// correct support for qTESLA
			Provider provider = Security.getProvider(BouncyCastlePQCProvider.PROVIDER_NAME);
			ConfigurableProvider cp = (ConfigurableProvider) provider;

			AsymmetricKeyInfoConverter keyFact = new QTESLAKeyFactorySpi();

			final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.qtesla.";

			cp.addAlgorithm("KeyFactory.QTESLA-P-I", PREFIX + "QTESLAKeyFactorySpi");
			cp.addAlgorithm("KeyFactory.QTESLA-P-III", PREFIX + "QTESLAKeyFactorySpi");

			cp.addAlgorithm("KeyPairGenerator.QTESLA-P-I", PREFIX + "KeyPairGeneratorSpi");
			cp.addAlgorithm("KeyPairGenerator.QTESLA-P-III", PREFIX + "KeyPairGeneratorSpi");

			registerOid(cp, PQCObjectIdentifiers.qTESLA, "QTESLA", keyFact);
			registerOid(cp, PQCObjectIdentifiers.qTESLA_p_I, "QTESLA-P-I", keyFact);
			registerOid(cp, PQCObjectIdentifiers.qTESLA_p_III, "QTESLA-P-III", keyFact);
		}
	}

	/**
	 * Register the given OID for key
	 * 
	 * @param provider The provider to add to.
	 * @param oid The OID to add
	 * @param name The name
	 * @param keyFactory The key factory to utilise
	 */
	private static void registerOid(ConfigurableProvider provider, ASN1ObjectIdentifier oid, String name,
			AsymmetricKeyInfoConverter keyFactory) {
		if (!((Provider) provider).containsKey("Alg.Alias.KeyFactory." + oid)) {
			provider.addAlgorithm("Alg.Alias.KeyFactory." + oid, name);
		}
		if (!((Provider) provider).containsKey("Alg.Alias.KeyPairGenerator." + oid)) {
			provider.addAlgorithm("Alg.Alias.KeyPairGenerator." + oid, name);
		}
		provider.addKeyInfoConverter(oid, keyFactory);
	}

}
