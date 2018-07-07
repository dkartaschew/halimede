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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.NoSuchElementException;

import org.bouncycastle.asn1.x509.CRLReason;
import org.junit.Test;

public class TestCRLReason {

	/*
	 * Test Revoke code to BC CRL Reason.
	 */
	@Test
	public void TestBC_CRLReason_Unspecified() {
		assertEquals(CRLReason.lookup(CRLReason.unspecified), RevokeReasonCode.UNSPECIFIED.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.unspecified), CRLReason.lookup(RevokeReasonCode.UNSPECIFIED.getCode()));
	}

	@Test
	public void TestBC_CRLReason_keyCompromise() {
		assertEquals(CRLReason.lookup(CRLReason.keyCompromise), RevokeReasonCode.KEY_COMPROMISE.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.keyCompromise), CRLReason.lookup(RevokeReasonCode.KEY_COMPROMISE.getCode()));
	}

	@Test
	public void TestBC_CRLReason_cACompromise() {
		assertEquals(CRLReason.lookup(CRLReason.cACompromise), RevokeReasonCode.CA_COMPROMISE.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.cACompromise), CRLReason.lookup(RevokeReasonCode.CA_COMPROMISE.getCode()));
	}

	@Test
	public void TestBC_CRLReason_affiliationChanged() {
		assertEquals(CRLReason.lookup(CRLReason.affiliationChanged), RevokeReasonCode.AFFILIATION_CHANGED.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.affiliationChanged), CRLReason.lookup(RevokeReasonCode.AFFILIATION_CHANGED.getCode()));
	}

	@Test
	public void TestBC_CRLReason_superseded() {
		assertEquals(CRLReason.lookup(CRLReason.superseded), RevokeReasonCode.SUPERSEDED.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.superseded), CRLReason.lookup(RevokeReasonCode.SUPERSEDED.getCode()));
	}

	@Test
	public void TestBC_CRLReason_cessationOfOperation() {
		assertEquals(CRLReason.lookup(CRLReason.cessationOfOperation), RevokeReasonCode.CESSATION_OF_OPERATION.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.cessationOfOperation), CRLReason.lookup(RevokeReasonCode.CESSATION_OF_OPERATION.getCode()));
	}

	@Test
	public void TestBC_CRLReason_certificateHold() {
		assertEquals(CRLReason.lookup(CRLReason.certificateHold), RevokeReasonCode.CERTIFICATE_HOLD.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.certificateHold), CRLReason.lookup(RevokeReasonCode.CERTIFICATE_HOLD.getCode()));
	}

	@Test
	public void TestBC_CRLReason_removeFromCRL() {
		assertEquals(CRLReason.lookup(CRLReason.removeFromCRL), RevokeReasonCode.REMOVE_FROM_CRL.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.removeFromCRL), CRLReason.lookup(RevokeReasonCode.REMOVE_FROM_CRL.getCode()));
	}

	@Test
	public void TestBC_CRLReason_privilegeWithdrawn() {
		assertEquals(CRLReason.lookup(CRLReason.privilegeWithdrawn), RevokeReasonCode.PRIVILEGE_WITHDRAWN.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.privilegeWithdrawn), CRLReason.lookup(RevokeReasonCode.PRIVILEGE_WITHDRAWN.getCode()));
	}

	@Test
	public void TestBC_CRLReason_aACompromise() {
		assertEquals(CRLReason.lookup(CRLReason.aACompromise), RevokeReasonCode.AA_COMPROMISE.getCRLReason());
		assertEquals(CRLReason.lookup(CRLReason.aACompromise), CRLReason.lookup(RevokeReasonCode.AA_COMPROMISE.getCode()));
	}

	/*
	 * Test CRL Reason to Revoke Code
	 */
	
	@Test
	public void Test_RevokeReasonCode_unspecified() {
		assertEquals(RevokeReasonCode.UNSPECIFIED, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.unspecified)));
		assertEquals(RevokeReasonCode.UNSPECIFIED, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.UNSPECIFIED));
	}
	
	@Test
	public void Test_RevokeReasonCode_keyCompromise() {
		assertEquals(RevokeReasonCode.KEY_COMPROMISE, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.keyCompromise)));
		assertEquals(RevokeReasonCode.KEY_COMPROMISE, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.KEY_COMPROMISE));
	}
	
	@Test
	public void Test_RevokeReasonCode_cACompromise() {
		assertEquals(RevokeReasonCode.CA_COMPROMISE, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.cACompromise)));
		assertEquals(RevokeReasonCode.CA_COMPROMISE, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.CA_COMPROMISE));
	}
	
	@Test
	public void Test_RevokeReasonCode_affiliationChanged() {
		assertEquals(RevokeReasonCode.AFFILIATION_CHANGED, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.affiliationChanged)));
		assertEquals(RevokeReasonCode.AFFILIATION_CHANGED, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.AFFILIATION_CHANGED));
	}
	
	@Test
	public void Test_RevokeReasonCode_superseded() {
		assertEquals(RevokeReasonCode.SUPERSEDED, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.superseded)));
		assertEquals(RevokeReasonCode.SUPERSEDED, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.SUPERSEDED));
	}
	
	@Test
	public void Test_RevokeReasonCode_cessationOfOperation() {
		assertEquals(RevokeReasonCode.CESSATION_OF_OPERATION, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.cessationOfOperation)));
		assertEquals(RevokeReasonCode.CESSATION_OF_OPERATION, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.CESSATION_OF_OPERATION));
	}
	
	@Test
	public void Test_RevokeReasonCode_certificateHold() {
		assertEquals(RevokeReasonCode.CERTIFICATE_HOLD, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.certificateHold)));
		assertEquals(RevokeReasonCode.CERTIFICATE_HOLD, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.CERTIFICATE_HOLD));
	}
	
	@Test
	public void Test_RevokeReasonCode_removeFromCRL(){
		assertEquals(RevokeReasonCode.REMOVE_FROM_CRL, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.removeFromCRL)));
		assertEquals(RevokeReasonCode.REMOVE_FROM_CRL, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.REMOVE_FROM_CRL));
	}
	
	@Test
	public void Test_RevokeReasonCode_privilegeWithdrawn() {
		assertEquals(RevokeReasonCode.PRIVILEGE_WITHDRAWN, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.privilegeWithdrawn)));
		assertEquals(RevokeReasonCode.PRIVILEGE_WITHDRAWN, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.PRIVILEGE_WITHDRAWN));
	}
	
	@Test
	public void Test_RevokeReasonCode_aACompromise() {
		assertEquals(RevokeReasonCode.AA_COMPROMISE, RevokeReasonCode.forCRLReason(CRLReason.lookup(CRLReason.aACompromise)));
		assertEquals(RevokeReasonCode.AA_COMPROMISE, RevokeReasonCode.forCRLReason(java.security.cert.CRLReason.AA_COMPROMISE));
	}
	
	@Test
	public void Test_RevokeReasonCode_NULL() {
		assertEquals(RevokeReasonCode.UNSPECIFIED, RevokeReasonCode.forCRLReason((CRLReason)null));
	}
	
	@Test
	public void Test_RevokeReasonCode_NULL2() {
		assertEquals(RevokeReasonCode.UNSPECIFIED, RevokeReasonCode.forCRLReason((java.security.cert.CRLReason)null));
	}
	
	/*
	 * Codes based on description.
	 */
	
	@Test
	public void Test_RevokeReasonCode_Description() {
		for(RevokeReasonCode r : RevokeReasonCode.values()) {
			assertEquals(r, RevokeReasonCode.forDescription(r.getDescription()));
		}
	}
	
	@Test(expected=NullPointerException.class)
	public void Test_RevokeReasonCode_NULLDescription() {
		assertEquals(RevokeReasonCode.UNSPECIFIED, RevokeReasonCode.forDescription(null));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void Test_UnknownDescription() {
		assertEquals(RevokeReasonCode.UNSPECIFIED, RevokeReasonCode.forDescription(""));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void Test_UnknownDescription2() {
		assertEquals(RevokeReasonCode.UNSPECIFIED, RevokeReasonCode.forDescription("unkno"));
	}
	
	/*
	 * Ensure the valid list doesn't include Unknown (7).
	 */
	@Test
	public void Test_ValidList() {
		List<Object> codes = RevokeReasonCode.getValidList();
		assertFalse(codes.contains(RevokeReasonCode.UNKNOWN));
	}
	
	@Test
	public void testEnum() {
		for(RevokeReasonCode t : RevokeReasonCode.values()) {
			assertEquals(t, RevokeReasonCode.valueOf(t.name()));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
		}
	}
}
