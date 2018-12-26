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

package net.sourceforge.dkartaschew.halimede.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.bouncycastle.asn1.x509.Extension;
import org.junit.Test;

public class TestCertificateExtensionUtil {

	@Test
	public void testObject() {
		CertificateExtensionUtil st = new CertificateExtensionUtil();
		assertEquals(st.hashCode(), st.hashCode());
		st.toString();
		assertEquals(st, st);
	}

	@Test
	public void testExtNull() {
		assertNull(CertificateExtensionUtil.getDescription(null));
	}

	@Test
	public void testExt_subjectAlternativeName() {
		assertEquals("Subject Alternative Name", CertificateExtensionUtil.getDescription(Extension.subjectAlternativeName));
	}

	@Test
	public void testExt_reasonCode() {
		assertEquals("Reason code", CertificateExtensionUtil.getDescription(Extension.reasonCode));
	}

	@Test
	public void testExt_cRLNumber() {
		assertEquals("CRL Number", CertificateExtensionUtil.getDescription(Extension.cRLNumber));
	}

	@Test
	public void testExt_basicConstraints() {
		assertEquals("Basic Constraints", CertificateExtensionUtil.getDescription(Extension.basicConstraints));
	}

}
