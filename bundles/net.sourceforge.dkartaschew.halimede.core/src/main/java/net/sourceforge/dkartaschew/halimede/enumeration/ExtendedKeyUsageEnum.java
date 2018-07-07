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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.KeyPurposeId;

/**
 * Basic Enumeration of ExtendedKeyUsage.
 */
public enum ExtendedKeyUsageEnum {

	/**
	 * Any usage
	 */
	anyExtendedKeyUsage("Any Usage", KeyPurposeId.anyExtendedKeyUsage),
	/**
	 * { id-kp 1 }
	 */
	kp_serverAuth("Server Authentication", KeyPurposeId.id_kp_serverAuth),
	/**
	 * { id-kp 2 }
	 */
	id_kp_clientAuth("Client Authentication", KeyPurposeId.id_kp_clientAuth),
	/**
	 * { id-kp 3 }
	 */
	id_kp_codeSigning("Code Signing", KeyPurposeId.id_kp_codeSigning),
	/**
	 * { id-kp 4 }
	 */
	id_kp_emailProtection("Email Protection", KeyPurposeId.id_kp_emailProtection),
	/**
	 * Usage deprecated by RFC4945 - was { id-kp 5 }
	 */
	id_kp_ipsecEndSystem("IPSEC End System Certificate", KeyPurposeId.id_kp_ipsecEndSystem),
	/**
	 * Usage deprecated by RFC4945 - was { id-kp 6 }
	 */
	id_kp_ipsecTunnel("IPSEC Tunnel Certificate", KeyPurposeId.id_kp_ipsecTunnel),
	/**
	 * Usage deprecated by RFC4945 - was { idkp 7 }
	 */
	id_kp_ipsecUser("IPSEC User Certificate", KeyPurposeId.id_kp_ipsecUser),
	/**
	 * { id-kp 8 }
	 */
	id_kp_timeStamping("Time Stamping", KeyPurposeId.id_kp_timeStamping),
	/**
	 * { id-kp 9 }
	 */
	id_kp_OCSPSigning("OSCP Signing", KeyPurposeId.id_kp_OCSPSigning),
	/**
	 * { id-kp 10 }
	 */
	id_kp_dvcs("DVCS", KeyPurposeId.id_kp_dvcs),
	/**
	 * { id-kp 11 } deprecated
	 */
	id_kp_sbgpCertAAServerAuth("CA Cert AA Server Auth", KeyPurposeId.id_kp_sbgpCertAAServerAuth),
	/**
	 * { id-kp 12 } deprecated
	 */
	id_kp_scvp_responder("SCVP Responder", KeyPurposeId.id_kp_scvp_responder),
	/**
	 * { id-kp 13 }
	 */
	id_kp_eapOverPPP("EAP Over PPP", KeyPurposeId.id_kp_eapOverPPP),
	/**
	 * { id-kp 14 }
	 */
	id_kp_eapOverLAN("EAP Over LAN", KeyPurposeId.id_kp_eapOverLAN),
	/**
	 * { id-kp 15 }
	 */
	id_kp_scvpServer("SVCP Server", KeyPurposeId.id_kp_scvpServer),
	/**
	 * { id-kp 16 }
	 */
	id_kp_scvpClient("SCVP Client", KeyPurposeId.id_kp_scvpClient),
	/**
	 * { id-kp 17 }
	 */
	id_kp_ipsecIKE("IPSec IKE", KeyPurposeId.id_kp_ipsecIKE),
	/**
	 * { id-kp 18 }
	 */
	id_kp_capwapAC("CAPWAP (AC)", KeyPurposeId.id_kp_capwapAC),
	/**
	 * { id-kp 19 }
	 */
	id_kp_capwapWTP("CAPWAP (WTP)", KeyPurposeId.id_kp_capwapWTP),

	/*
	 * Additional defined in RFC7299
	 */
	/**
	 * EKU for SIP
	 */
	id_kp_sipDomain("Extended Key Usage (SIP)", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("20"))), //
	/**
	 * SSH Client
	 */
	id_kp_secureShellClient("SSH Client Certificate", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("21"))), //
	/**
	 * SSH Server
	 */
	id_kp_secureShellServer("SSH Server Certificate", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("22"))), //
	/**
	 * SEND RFC6494
	 */
	id_kp_sendRouter("SEND Router", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("23"))), //
	/**
	 * SEND RFC6494
	 */
	id_kp_sendProxiedRouter("SEND Proxied Router", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("24"))), //
	/**
	 * SEND RFC6494
	 */
	id_kp_sendOwner("SEND Owner", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("25"))), //
	/**
	 * SEND RFC6494
	 */
	id_kp_sendProxiedOwner("SEND Proxied Owner", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("26"))), //
	/**
	 * Certificate Management over CMS - RFC 6402
	 */
	id_kp_cmcCA("Certificate Management over CMS (CA)",
			KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("27"))), //
	/**
	 * Certificate Management over CMS - RFC 6402
	 */
	id_kp_cmcRA("Certificate Management over CMS (RA)",
			KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("28"))), //
	/**
	 * Certificate Management over CMS - RFC 6402
	 */
	id_kp_cmcArchive("Certificate Management over CMS (Archive)",
			KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("29"))), //
	/**
	 * Certificate Management over CMS - RFC BGPSEC
	 */
	id_kp_bgpsecRouter("BGPSEC Router Certificate", KeyPurposeId.getInstance(ExtendedKeyUsageID.id_kp.branch("30"))),

	//
	// microsoft key purpose ids
	//
	/**
	 * { 1 3 6 1 4 1 311 20 2 2 }
	 */
	id_kp_smartcardlogon("SmartCard Logon", KeyPurposeId.id_kp_smartcardlogon),

	/**
	 *
	 */
	id_kp_macAddress("MAC Address", KeyPurposeId.id_kp_macAddress),

	/**
	 * Microsoft Server Gated Crypto (msSGC) see http://www.alvestrand.no/objectid/1.3.6.1.4.1.311.10.3.3.html
	 */
	id_kp_msSGC("Microsoft Server Gated Crypto (msSGC)", KeyPurposeId.id_kp_msSGC),
	/**
	 * Netscape Server Gated Crypto (nsSGC) see http://www.alvestrand.no/objectid/2.16.840.1.113730.4.1.html
	 */
	id_kp_nsSGC("Netscape Server Gated Crypto (nsSGC)", KeyPurposeId.id_kp_nsSGC);

	/**
	 * Description
	 */
	private final String description;
	/**
	 * Usage
	 */
	private final KeyPurposeId usage;

	/**
	 * Create a new Enum base KeyUsage
	 * 
	 * @param description The textual description
	 * @param usage Key Usage bit value.
	 */
	private ExtendedKeyUsageEnum(String description, KeyPurposeId usage) {
		this.description = description;
		this.usage = usage;
	}

	/**
	 * Get the int type.
	 * 
	 * @return bit type.
	 */
	public KeyPurposeId getKeyUsage() {
		return usage;
	}

	@Override
	public String toString() {
		return description;
	}

	/**
	 * Get the single value as a Key Usage.
	 * 
	 * @return A Key Usage.
	 */
	public ASN1EncodableVector asKeyUsage() {
		ASN1EncodableVector v = new ASN1EncodableVector();
		v.add(usage);
		return v;
	}

	/**
	 * Convert the collection to a ASN1 Vector of extended key usage
	 * 
	 * @param usage The collection of usage
	 * @return An ASN1EncodableVector instance or null if empty collection
	 */
	public static ASN1EncodableVector asExtKeyUsage(Collection<ExtendedKeyUsageEnum> usage) {
		if (usage == null || usage.isEmpty()) {
			return null;
		}
		ASN1EncodableVector v = new ASN1EncodableVector();
		usage.stream().filter(i -> i != null).map(i -> i.usage).forEach(v::add);
		if(v.size() == 0) {
			return null;
		}
		return v;
	}

	/**
	 * Convert the collection to a ASN1 Vector of extended key usage
	 * 
	 * @param usage The collection of usage
	 * @return An ASN1EncodableVector instance or null if empty collection
	 */
	public static ASN1EncodableVector asExtKeyUsage(ExtendedKeyUsageEnum[] usage) {
		if (usage == null || usage.length == 0) {
			return null;
		}
		ASN1EncodableVector v = new ASN1EncodableVector();
		Arrays.stream(usage).filter(i -> i != null).map(i -> i.usage).forEach(v::add);
		if(v.size() == 0) {
			return null;
		}
		return v;
	}

	/**
	 * Convert the KeyUsage instance to a collection of enums
	 * 
	 * @param usage The key usage instance
	 * @return Collections of enums that make up the key usage
	 */
	public static Collection<ExtendedKeyUsageEnum> asExtendedKeyUsageEnum(ASN1EncodableVector usage) {
		if(usage == null) {
			return new ArrayList<ExtendedKeyUsageEnum>();
		}
		List<ExtendedKeyUsageEnum> values = new ArrayList<>();
		final int sz = usage.size();
		for (int i = 0; i < sz; i++) {
			KeyPurposeId id_kp = (KeyPurposeId) usage.get(i);
			for (ExtendedKeyUsageEnum e : ExtendedKeyUsageEnum.values()) {
				if (e.getKeyUsage().equals(id_kp)) {
					values.add(e);
				}
			}
		}
		return values;
	}
	
	/**
	 * Convert the KeyUsage instance to a collection of enums
	 * 
	 * @param usage The key usage instance
	 * @return Collections of enums that make up the key usage
	 */
	public static Collection<ExtendedKeyUsageEnum> asExtendedKeyUsageEnum(ExtendedKeyUsage usage) {
		if(usage == null) {
			return new ArrayList<ExtendedKeyUsageEnum>();
		}
		List<ExtendedKeyUsageEnum> values = new ArrayList<>();
		for(KeyPurposeId id_kp : usage.getUsages()) {
			for (ExtendedKeyUsageEnum e : ExtendedKeyUsageEnum.values()) {
				if (e.getKeyUsage().equals(id_kp)) {
					values.add(e);
				}
			}
		}
		return values;
	}
}
