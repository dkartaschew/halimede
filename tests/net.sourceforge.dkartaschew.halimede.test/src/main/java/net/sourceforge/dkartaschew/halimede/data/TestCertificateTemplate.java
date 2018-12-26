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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.GeneralNamesBuilder;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCertificateTemplate {

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void testEmpty() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void complete() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			Collection<KeyPurposeId> extKeyUsage = new ArrayList<>();
			extKeyUsage.add(KeyPurposeId.id_kp_codeSigning);
			extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);

			GeneralNames subjAltNames = new GeneralNamesBuilder()//
					.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setCARequest(true);
			template.setCreationDate(ZonedDateTime.now());
			template.setDescription("My Template");
			template.setKeyType(KeyType.EC_secp521r1);
			template.setSubject(new X500Name("CN=Me"));
			template.setKeyUsage(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.dataEncipherment));
			template.setSubjectAltNames(subjAltNames);
			template.setExtendedKeyUsage(extKeyUsage);
			template.setCrlIssuer(new X500Name("CN=MeAndYou"));
			template.setCrlLocation("http://local/cgi?crl");
			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
			assertEquals(template.hashCode(), template2.hashCode());
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void testZonedDateTime() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setDescription("My Template");
			template.setCreationDate(ZonedDateTime.now());

			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void caReq() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setCARequest(true);
			template.setDescription("My Template");
			template.setCrlIssuer(new X500Name("CN=MeAndYou"));
			template.setCrlLocation("http://local/cgi?crl");
			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void subjectX500() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setDescription("My Template");
			template.setSubject(new X500Name("CN=Me"));

			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void keyType() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setDescription("My Template");
			template.setKeyType(KeyType.EC_secp521r1);

			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void keyUsage() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setDescription("My Template");
			template.setKeyUsage(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.dataEncipherment));

			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void extKeyUsage() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			Collection<KeyPurposeId> extKeyUsage = new ArrayList<>();
			extKeyUsage.add(KeyPurposeId.id_kp_codeSigning);
			extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);

			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setDescription("My Template");
			template.setExtendedKeyUsage(extKeyUsage);

			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test to create and read a configuration.
	 * 
	 * @throws Exception Test failure
	 */
	@Test
	public void subAltNames() throws Exception {
		Path path = Paths.get(TestUtilities.TMP, "templ" + CertificateKeyPairTemplate.DEFAULT_EXTENSION);

		try {
			GeneralNames subjAltNames = new GeneralNamesBuilder()//
					.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

			CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
			template.setDescription("My Template");
			template.setSubjectAltNames(subjAltNames);

			template.store(path);

			CertificateKeyPairTemplate template2 = CertificateKeyPairTemplate.read(path);
			assertEquals(template, template2);
		} finally {
			TestUtilities.delete(path);
		}
	}

	/**
	 * Basic test of object properties.
	 */
	@Test
	public void testObject() {
		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		assertEquals(template, template);
		assertEquals(template.hashCode(), template.hashCode());
		assertTrue(template.toString().contains("Template"));
		assertFalse(template.toString().contains("null"));

		assertNull(template.getCertificatePolicies());
		assertNull(template.getCreationDate());
		assertNull(template.getCrlIssuer());
		assertNull(template.getCrlLocation());
		assertNull(template.getDescription());
		assertNull(template.getExtendedKeyUsage());
		assertNull(template.getKeyUsage());
		assertNull(template.getKeyType());
		assertNull(template.getSubject());
		assertNull(template.getSubjectAltNames());
		assertFalse(template.isCARequest());

		assertNull(template.getExtendedKeyUsageVector());

		assertFalse(template.equals(null));
		assertFalse(template.equals(new Object()));
		assertTrue(template.equals(template));
	}

	/**
	 * Basic test of object properties.
	 */
	@Test
	public void testObjectToString() {
		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		assertEquals(template, template);
		assertEquals(template.hashCode(), template.hashCode());
		assertTrue(template.toString().contains("Template"));
		assertFalse(template.toString().contains("null"));

		X500Name subject = new X500Name("CN=Subject");
		template.setSubject(subject);
		assertEquals(subject, template.getSubject());
		assertEquals(subject.toString(), template.toString());
		
		template.setDescription("Description");
		assertEquals("Description", template.getDescription());
		assertEquals("Description", template.toString());


	}

	/**
	 * Basic test of object properties.
	 */
	@Test
	public void testExtKeyUsageVector() {
		Collection<KeyPurposeId> extKeyUsage = new ArrayList<>();
		extKeyUsage.add(KeyPurposeId.id_kp_codeSigning);
		extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);

		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		template.setDescription("My Template");
		template.setExtendedKeyUsage(extKeyUsage);

		ASN1EncodableVector v = template.getExtendedKeyUsageVector();
		assertEquals(2, v.size());
		assertEquals(KeyPurposeId.id_kp_codeSigning, v.get(0));
		assertEquals(KeyPurposeId.id_kp_clientAuth, v.get(1));
	}

	/**
	 * Basic test to convert to Certificate Request.
	 */
	@Test
	public void templateToCertificateRequest() throws Exception {

		Collection<KeyPurposeId> extKeyUsage = new ArrayList<>();
		extKeyUsage.add(KeyPurposeId.id_kp_codeSigning);
		extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);

		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();

		CertificateKeyPairTemplate template = new CertificateKeyPairTemplate();
		template.setCARequest(true);
		template.setCreationDate(ZonedDateTime.now());
		template.setDescription("My Template");
		template.setKeyType(KeyType.EC_secp521r1);
		template.setSubject(new X500Name("CN=Me"));
		template.setKeyUsage(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.dataEncipherment));
		template.setSubjectAltNames(subjAltNames);
		template.setExtendedKeyUsage(extKeyUsage);
		template.setCrlIssuer(new X500Name("CN=MeAndYou"));
		template.setCrlLocation("http://local/cgi?crl");

		CertificateRequest req = (CertificateRequest) template.asCertificateRequest();
		assertEquals(template.isCARequest(), req.isCARequest());
		assertEquals(template.getCertificatePolicies(), req.getCertificatePolicies());
		assertEquals(template.getCrlLocation(), req.getCrlLocation());
		assertEquals(template.getCrlIssuer(), req.getCrlIssuer());
		assertEquals(template.getCreationDate(), req.getCreationDate());
		assertEquals(template.getSubject(), req.getSubject());
		assertEquals(template.getKeyType(), req.getKeyType());
		assertEquals(template.getDescription(), req.getDescription());
		assertEquals(template.getKeyUsage(), req.getKeyUsage());
		ASN1EncodableVector v = template.getExtendedKeyUsageVector();
		ASN1EncodableVector v2 = req.getExtendedKeyUsageVector();
		assertEquals(v.size(), v2.size());
		for(int i = 0; i < v.size(); i++) {
			assertEquals(v.get(i), v2.get(i));
		}
		
		assertEquals(template.getSubjectAltNames(), req.getSubjectAlternativeName());
	}

	/**
	 * Basic test to test equality and hashCode
	 */
	@Test
	public void testEqualsHashCode() throws Exception {

		Collection<KeyPurposeId> extKeyUsage = new ArrayList<>();
		extKeyUsage.add(KeyPurposeId.id_kp_codeSigning);
		extKeyUsage.add(KeyPurposeId.id_kp_clientAuth);
		
		Collection<KeyPurposeId> extKeyUsage2 = new ArrayList<>();
		extKeyUsage2.add(KeyPurposeId.id_kp_capwapAC);
		extKeyUsage2.add(KeyPurposeId.id_kp_clientAuth);

		GeneralNames subjAltNames = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user")).build();
		GeneralNames subjAltNames2 = new GeneralNamesBuilder()//
				.addName(new GeneralName(GeneralName.uniformResourceIdentifier, "usr://home/user2")).build();

		CertificateKeyPairTemplate t1 = new CertificateKeyPairTemplate();
		CertificateKeyPairTemplate t2 = new CertificateKeyPairTemplate();
		
		assertEquals(t1, t2);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// Is CA
		
		t1.setCARequest(true);
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setCARequest(true);
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// Create date 
		t1.setCreationDate(ZonedDateTime.now());
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setCreationDate(ZonedDateTime.now().plusHours(1));
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setCreationDate(t1.getCreationDate());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// Description
		t1.setDescription("My Template");
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setDescription("My Template2");
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setDescription(t1.getDescription());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// Key Type
		t1.setKeyType(KeyType.EC_secp521r1);
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setKeyType(KeyType.EC_secp224r1);
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setKeyType(t1.getKeyType());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// Subject
		t1.setSubject(new X500Name("CN=Me"));
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setSubject(new X500Name("CN=MeToo"));
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setSubject(t1.getSubject());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());

		// KeyUsage
		t1.setKeyUsage(new KeyUsage(KeyUsage.digitalSignature | KeyUsage.dataEncipherment));
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setKeyUsage(new KeyUsage(KeyUsage.cRLSign | KeyUsage.dataEncipherment));
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setKeyUsage(t1.getKeyUsage());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// CRL Issuer
		t1.setCrlIssuer(new X500Name("CN=MeAndYou"));
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setCrlIssuer(new X500Name("CN=MeAndYouToo"));
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setCrlIssuer(t1.getCrlIssuer());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// CRL Location
		t1.setCrlLocation("http://local/cgi?crl");
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setCrlLocation("http://local/cgi?crl2");
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setCrlLocation(t1.getCrlLocation());
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// Subject Alt Names
		t1.setSubjectAltNames(subjAltNames);
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setSubjectAltNames(subjAltNames2);
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setSubjectAltNames(subjAltNames);
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
		// Extended Usage
		t1.setExtendedKeyUsage(extKeyUsage);
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setExtendedKeyUsage(extKeyUsage2);
		assertNotEquals(t1, t2);
		assertNotEquals(t2, t1);
		assertNotEquals(t1.hashCode(), t2.hashCode());
		
		t2.setExtendedKeyUsage(extKeyUsage);
		assertEquals(t1, t2);
		assertEquals(t2, t1);
		assertEquals(t1.hashCode(), t2.hashCode());
		
	}
	
	/**
	 * Basic testof comparator
	 */
	@Test
	public void testComparison() {
		CertificateKeyPairTemplate p = new CertificateKeyPairTemplate();
		CertificateKeyPairTemplate p2 = new CertificateKeyPairTemplate();

		assertEquals(p, p2);

		assertEquals(0, p.compare(p, p2));
		assertEquals(0, p.compare(null, null));
		assertEquals(-1, p.compare(null, p2));
		assertEquals(1, p.compare(p, null));
		assertEquals(1, p.compareTo(null));
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p.compareTo(p));
		
		p.setDescription("a");
		p2.setDescription("a");
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p2.compareTo(p));
		
		p2.setDescription("b");
		assertEquals("a".compareTo("b"), p.compareTo(p2));
		assertEquals("b".compareTo("a"), p2.compareTo(p));
		p2.setDescription("a");
		assertEquals(0, p.compareTo(p2));
		
		p.setSubject(new X500Name("CN=A"));
		p2.setSubject(new X500Name("CN=A"));
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p2.compareTo(p));
		
		p2.setSubject(new X500Name("CN=B"));
		assertEquals("CN=A".compareTo("CN=B"), p.compareTo(p2));
		assertEquals("CN=B".compareTo("CN=A"), p2.compareTo(p));
		p2.setSubject(new X500Name("CN=A"));
		assertEquals(0, p.compareTo(p2));
		
		p.setDescription(null);
		p2.setDescription(null);
		p.setSubject(null);
		p2.setSubject(null);
		
		assertEquals(0, p.compareTo(p2));
		p.setDescription("a");
		assertEquals(-1, p.compareTo(p2));
		assertEquals(1, p2.compareTo(p));
		
		p2.setDescription("b");
		assertEquals(-1, p.compareTo(p2));
		assertEquals(1, p2.compareTo(p));
		p2.setDescription("a");
		
		assertEquals(0, p.compareTo(p2));
		p.setSubject(new X500Name("CN=A"));
		assertEquals(-1, p.compareTo(p2));
		assertEquals(1, p2.compareTo(p));
	}
	
}
