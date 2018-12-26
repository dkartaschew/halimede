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

package net.sourceforge.dkartaschew.halimede.ui.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.data.PKCS10Decoder;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestNewCertificateModel {

	private String STR = "StringValue";
	private String X500 = "CN=" + STR;
	private X500Name X500n = new X500Name(X500);
	private String CRL = "http://" + STR;
	private ZonedDateTime now = ZonedDateTime.now();
	private KeyType type = KeyType.RSA_512;
	private ASN1EncodableVector extKeyUsage = ExtendedKeyUsageEnum.asExtKeyUsage(ExtendedKeyUsageEnum.values());

	@Test
	public void testBasicObject() {
		NewCertificateModel model = new NewCertificateModel(null);
		assertEquals(null, model.getCa());
		assertTrue(model.equals(model));
		assertFalse(model.equals(null));
		assertFalse(model.equals(new Object()));
		assertEquals(model.hashCode(), model.hashCode());
		assertFalse(model.toString().contains("null"));
	}

	@Test
	public void testDescription() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setDescription(STR);
		assertEquals(STR, model.getDescription());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		// assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setDescription(null);
		assertNull(model.getDescription());
	}

	@Test
	public void testCreationDate() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setCreationDate(now);
		assertEquals(now, model.getCreationDate());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		// assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setCreationDate(null);
		assertNull(model.getCreationDate());
	}

	@Test
	public void testCRLIssuer() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setCrlIssuer(X500n);
		assertEquals(X500n, model.getCrlIssuer());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		// assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setCrlIssuer(null);
		assertNull(model.getCrlIssuer());
	}

	@Test
	public void testCRLLocation() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setCrlLocation(CRL);
		assertEquals(CRL, model.getCrlLocation());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		// assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setCrlLocation(null);
		assertNull(model.getCrlLocation());
	}

	@Test
	public void testStartDate() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setStartDate(now);
		assertEquals(now, model.getStartDate());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		// assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setStartDate(null);
		assertNull(model.getStartDate());
	}

	@Test
	public void testExpiryDate() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setExpiryDate(now);
		assertEquals(now, model.getExpiryDate());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		// assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setExpiryDate(null);
		assertNull(model.getExpiryDate());
	}

	@Test
	public void testExtKeyUsage() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setExtendedKeyUsage(extKeyUsage);
		assertEquals(extKeyUsage, model.getExtendedKeyUsageVector());
		assertEquals(new DERSequence(extKeyUsage), model.getExtendedKeyUsage());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		// assertNull(model.getExtendedKeyUsage());
		// assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setExtendedKeyUsage(null);
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getExtendedKeyUsage());
	}

	@Test(expected = NullPointerException.class)
	public void testKeyPairMissingPublic() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		KeyPair p = new KeyPair(null, null);
		model.setKeyPair(p);
	}

	@Test
	public void testKeyPair() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		KeyPair p = KeyPairFactory.generateKeyPair(type);
		model.setKeyPair(p);
		assertEquals(p, model.getKeyPair());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		// assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNotNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setKeyPair(null);
		assertNull(model.getKeyPair());
	}

	@Test
	public void testKeyType() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setKeyType(type);
		assertEquals(type, model.getKeyType());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		// This will generate a keypair for us.
		assertNotNull(model.getKeyPair());
		// assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNotNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setKeyType(null);
		assertNull(model.getKeyType());
	}

	@Test
	public void testKeyUsage() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setKeyUsage(KeyUsageEnum.keyAgreement.asKeyUsage());
		assertEquals(KeyUsageEnum.keyAgreement.asKeyUsage(), model.getKeyUsage());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		// assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setKeyUsage(null);
		assertNull(model.getKeyUsage());
	}

	@Test
	public void testNotAfter() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setNotAfter(now);
		assertEquals(now, model.getNotAfter());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		// assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setNotAfter(null);
		assertNull(model.getNotAfter());
	}

	@Test
	public void testNotBefore() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setNotBefore(now);
		assertEquals(now, model.getNotBefore());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		// assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setNotBefore(null);
		assertNull(model.getNotBefore());
	}

	@Test
	public void testPassword() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setPassword(STR);
		assertEquals(STR, model.getPassword());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		// assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setPassword(null);
		assertNull(model.getPassword());
	}

	@Test
	public void testSubject() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setSubject(X500n);
		assertEquals(X500n, model.getSubject());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		// assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setSubject(null);
		assertNull(model.getSubject());
	}

	@Test
	public void testSubjectAltName() throws Throwable {
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

		NewCertificateModel model = new NewCertificateModel(null);
		model.setSubjectAlternativeName(subjAltNames);
		assertEquals(subjAltNames, model.getSubjectAlternativeName());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		// assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setSubjectAlternativeName(null);
		assertNull(model.getSubjectAlternativeName());
	}

	@Test
	public void testCARequest() throws Throwable {

		NewCertificateModel model = new NewCertificateModel(null);
		model.setcARequest(true);
		assertEquals(true, model.iscARequest());
		assertEquals(true, model.isCARequest());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		// assertFalse(model.isCARequest());
		// assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setcARequest(false);
		assertEquals(false, model.iscARequest());
		assertEquals(false, model.isCARequest());
	}

	@Test
	public void testCAPassword() throws Throwable {

		NewCertificateModel model = new NewCertificateModel(null);
		model.setUseCAPassword(true);
		assertEquals(true, model.isUseCAPassword());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		// assertTrue(model.isUseCAPassword());

		model.setUseCAPassword(false);
		assertEquals(false, model.isUseCAPassword());
	}

	@Test
	public void testISTemplate() throws Throwable {

		NewCertificateModel model = new NewCertificateModel(null);
		model.setRepresentsTemplateOnly(true);
		assertEquals(true, model.isRepresentsTemplateOnly());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		// assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setRepresentsTemplateOnly(false);
		assertEquals(false, model.isRepresentsTemplateOnly());
	}

	@Test
	public void testCertificatePolicy() throws Throwable {

		NewCertificateModel model = new NewCertificateModel(null);
		model.setCertificatePolicies(new ASN1Integer(1));
		assertEquals(new ASN1Integer(1), model.getCertificatePolicies());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		// assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setCertificatePolicies(null);
		assertNull(model.getCertificatePolicies());
	}

	@Test
	public void testCertificateRequest() throws Throwable {
		Path file = TestUtilities.getFile("rsa4096_der.csr");
		ICertificateRequest request = PKCS10Decoder.open(file);
		CertificateRequestProperties props = new CertificateRequestProperties(null, request);

		NewCertificateModel model = new NewCertificateModel(null);
		assertFalse(model.isCertificateRequest());
		model.setCsr(props);
		assertEquals(props, model.getCsr());
		assertTrue(model.isCertificateRequest());

		// Assert all other fields as per defaults.
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		// assertNull(model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsage());
		assertNull(model.getExtendedKeyUsageVector());
		assertNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertNull(model.getSubject());
		assertNull(model.getSubjectAlternativeName());
		assertNull(model.getSubjectPublicKeyInfo());
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		// assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());

		model.setCsr(null);
		assertNull(model.getCsr());
		assertFalse(model.isCertificateRequest());
	}
	
	@Test
	public void testModelFromCertificateRequest() throws Throwable {
		Path file = TestUtilities.getFile("rsa4096_der.csr");
		ICertificateRequest request = PKCS10Decoder.open(file);
		CertificateRequestProperties props = new CertificateRequestProperties(null, request);

		NewCertificateModel model = new NewCertificateModel(null, props);
		
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertNull(model.getCreationDate());
		assertNull(model.getCRLDistributionPoint());
		assertNull(model.getCrlIssuer());
		assertNull(model.getCrlLocation());
		assertEquals(props, model.getCsr());
		assertNull(model.getDescription());
		assertNull(model.getExpiryDate());
		assertEquals(request.getExtendedKeyUsage(), model.getExtendedKeyUsageVector());
		assertNotNull(model.getKeyPair());
		assertNull(model.getKeyType());
		assertNull(model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertEquals("C=AU,ST=Queensland,L=Gold Coast,O=Internet Widgits Pty Ltd,CN=CA Manager", 
				model.getSubject().toString());
		assertNull(model.getSubjectAlternativeName());
		assertNotNull(model.getSubjectPublicKeyInfo());
		
		assertFalse(model.isCARequest());
		assertFalse(model.iscARequest());
		assertTrue(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());
		
		
	}
	
	@Test
	public void testModelFromTemplate() throws Throwable {
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();
		
		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		template.setCARequest(true);
		template.setCreationDate(now);
		template.setCrlIssuer(X500n);
		template.setCrlLocation(CRL);
		template.setDescription(STR);
		template.setKeyType(type);
		template.setKeyUsage(KeyUsageEnum.decipherOnly.asKeyUsage());
		template.setSubject(X500n);
		template.setSubjectAltNames(subjAltNames);

		NewCertificateModel model = new NewCertificateModel(null, template);
		
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertEquals(now, model.getCreationDate());
		assertNotNull(model.getCRLDistributionPoint());
		assertEquals(X500n, model.getCrlIssuer());
		assertEquals(CRL, model.getCrlLocation());
		assertNull(model.getCsr());
		assertEquals(STR, model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsageVector());
		assertNotNull(model.getKeyPair());
		assertEquals(type, model.getKeyType());
		assertEquals(template.getKeyUsage(), model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertEquals(X500n, model.getSubject());
		assertEquals(subjAltNames, model.getSubjectAlternativeName());
		assertNotNull(model.getSubjectPublicKeyInfo());
		
		assertTrue(model.isCARequest());
		assertTrue(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());
		
		
	}
	
	@Test
	public void testModelFromRequest() throws Throwable {
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();
		
		CertificateRequest template = new CertificateRequest();
		template.setcARequest(true);
		template.setCreationDate(now);
		template.setCrlIssuer(X500n);
		template.setCrlLocation(CRL);
		template.setDescription(STR);
		template.setKeyType(type);
		template.setKeyUsage(KeyUsageEnum.decipherOnly.asKeyUsage());
		template.setSubject(X500n);
		template.setSubjectAlternativeName(subjAltNames);

		NewCertificateModel model = new NewCertificateModel(null, template);
		
		assertNull(model.getCa());
		assertNull(model.getCertificatePolicies());
		assertEquals(null, model.getCreationDate());
		assertNotNull(model.getCRLDistributionPoint());
		assertEquals(X500n, model.getCrlIssuer());
		assertEquals(CRL, model.getCrlLocation());
		assertNull(model.getCsr());
		assertEquals(STR, model.getDescription());
		assertNull(model.getExpiryDate());
		assertNull(model.getExtendedKeyUsageVector());
		assertNotNull(model.getKeyPair());
		assertEquals(type, model.getKeyType());
		assertEquals(template.getKeyUsage(), model.getKeyUsage());
		assertNull(model.getNotAfter());
		assertNull(model.getNotBefore());
		assertNull(model.getPassword());
		assertNull(model.getStartDate());
		assertEquals(X500n, model.getSubject());
		assertEquals(subjAltNames, model.getSubjectAlternativeName());
		assertNotNull(model.getSubjectPublicKeyInfo());
		
		assertTrue(model.isCARequest());
		assertTrue(model.iscARequest());
		assertFalse(model.isCertificateRequest());
		assertFalse(model.isRepresentsTemplateOnly());
		assertTrue(model.isUseCAPassword());
		
		
	}
	
	@Test
	public void testCRLDistPoint() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setcARequest(true);
		model.setCrlIssuer(X500n);
		assertNull(model.getCRLDistributionPoint());
	}
	
	@Test
	public void testCRLDistPoint2() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setcARequest(true);
		model.setCrlLocation(CRL);
		assertNull(model.getCRLDistributionPoint());
	}

	@Test
	public void testCRLDistPoint3() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setcARequest(true);
		model.setCrlLocation(CRL);
		model.setCrlIssuer(X500n);
		
		ASN1Encodable crl = model.getCRLDistributionPoint();
		assertNotNull(crl);
		
		assertTrue(crl instanceof CRLDistPoint);
		CRLDistPoint point = (CRLDistPoint)crl;
		assertEquals(1, point.getDistributionPoints().length);
		
		DistributionPoint dp = point.getDistributionPoints()[0];
		assertTrue(dp.getCRLIssuer().getNames()[0].getName().toString().contains(X500n.toString()));
	}
	
	@Test
	public void testModelToTemplate() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setDescription(STR);
		ICertificateKeyPairTemplate template = model.asTemplate();
		assertEquals(template, model);
	}
	
	@Test
	public void testModelToRequest() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		model.setDescription(STR);
		ICertificateRequest template = model.asCertificateRequest();
		assertEquals(template, model);
	}
	
	@Test
	public void testModelToString() throws Throwable {
		NewCertificateModel model = new NewCertificateModel(null);
		assertFalse(model.toString().contains("null"));
		model.setDescription(STR);
		assertEquals(STR, model.toString());
		model.setSubject(X500n);
		assertEquals(X500n.toString(), model.toString());
	}
	
	@Test
	public void testModelToTemplateStore() throws Throwable {
		
		Path filename = Paths.get(TestUtilities.TMP, "templ" + ICertificateKeyPairTemplate.DEFAULT_EXTENSION);
	
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();
		
		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		template.setCARequest(true);
		template.setCreationDate(now);
		template.setCrlIssuer(X500n);
		template.setCrlLocation(CRL);
		template.setDescription(STR);
		template.setKeyType(type);
		template.setKeyUsage(KeyUsageEnum.decipherOnly.asKeyUsage());
		template.setSubject(X500n);
		template.setSubjectAltNames(subjAltNames);

		NewCertificateModel model = new NewCertificateModel(null, template);
		model.store(filename);
		
		CertificateKeyPairTemplate n = (CertificateKeyPairTemplate) ICertificateKeyPairTemplate.open(filename);
		
		assertEquals(true, n.isCARequest());
		assertEquals(now, n.getCreationDate());
		assertEquals(X500n, n.getCrlIssuer());
		assertEquals(CRL, n.getCrlLocation());
		assertEquals(STR, n.getDescription());
		assertEquals(type, n.getKeyType());
		assertEquals(KeyUsageEnum.decipherOnly.asKeyUsage(), n.getKeyUsage());
		assertEquals(X500n, n.getSubject());
		assertEquals(subjAltNames, n.getSubjectAltNames());
	}
		
	@Test
	public void testModelToTemplateStore2() throws Throwable {
		
		Path filename = Paths.get(TestUtilities.TMP, "templ" + ICertificateKeyPairTemplate.DEFAULT_EXTENSION);
	
		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();
		Collection<KeyPurposeId> ext = new ArrayList<>();
		ext.add(KeyPurposeId.anyExtendedKeyUsage);
		
		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		template.setCARequest(true);
		template.setCreationDate(now);
		template.setCrlIssuer(X500n);
		template.setCrlLocation(CRL);
		template.setDescription(STR);
		template.setKeyType(type);
		template.setKeyUsage(KeyUsageEnum.decipherOnly.asKeyUsage());
		template.setExtendedKeyUsage(ext);
		template.setSubject(X500n);
		template.setSubjectAltNames(subjAltNames);

		NewCertificateModel model = new NewCertificateModel(null, template);
		model.store(filename);
		
		CertificateKeyPairTemplate n = (CertificateKeyPairTemplate) ICertificateKeyPairTemplate.open(filename);
		
		assertEquals(true, n.isCARequest());
		assertEquals(now, n.getCreationDate());
		assertEquals(X500n, n.getCrlIssuer());
		assertEquals(CRL, n.getCrlLocation());
		assertEquals(STR, n.getDescription());
		assertEquals(type, n.getKeyType());
		assertEquals(KeyUsageEnum.decipherOnly.asKeyUsage(), n.getKeyUsage());
		assertEquals(X500n, n.getSubject());
		assertEquals(subjAltNames, n.getSubjectAltNames());
		assertEquals(ext, n.getExtendedKeyUsage());
	}
}
