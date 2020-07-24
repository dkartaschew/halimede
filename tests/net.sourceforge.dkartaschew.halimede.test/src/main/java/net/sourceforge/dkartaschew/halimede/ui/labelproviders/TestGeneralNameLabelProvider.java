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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;

/**
 * Basic tests for label provider.
 * <p>
 * Tests images, column text and column tooltip.
 */
public class TestGeneralNameLabelProvider {

	@Test
	public void testNullValues() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		assertNull(provider.getImage(null));
		assertEquals("", provider.getText(null));
		assertEquals("", provider.getValue(null));
	}
	
	@Test
	public void testObjectValues() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		assertNull(provider.getImage(""));
		assertEquals("", provider.getText(""));
		assertEquals("", provider.getValue(""));
	}
		
	@Test
	public void testDirectoryName() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		GeneralName name = GeneralNameTag.directoryName.asGeneralName("CN=a");
		assertNull(provider.getImage(name));
		assertEquals("DirectoryName: CN=a", provider.getText(name));
		assertEquals("CN=a", provider.getValue(name));
	}
	
	@Test
	public void testEmailName() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		GeneralName name = GeneralNameTag.rfc822Name.asGeneralName("a@a.com");
		assertNull(provider.getImage(name));
		assertEquals("Email (RFC822): a@a.com", provider.getText(name));
		assertEquals("a@a.com", provider.getValue(name));
	}
	
	@Test
	public void testOtherName() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		GeneralName name = new GeneralName(GeneralName.otherName, new DERIA5String("Other"));
		assertNull(provider.getImage(name));
		assertEquals("Other: Other", provider.getText(name));
		assertEquals("Other", provider.getValue(name));
	}
	
	@Test
	public void testIPAddressv4() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		GeneralName name = GeneralNameTag.iPAddress.asGeneralName("127.0.0.1");
		assertNull(provider.getImage(name));
		assertEquals("IP Address: 127.0.0.1", provider.getText(name));
		assertEquals("127.0.0.1", provider.getValue(name));
	}
	
	@Test
	public void testIPAddressv6() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		GeneralName name = GeneralNameTag.iPAddress.asGeneralName("2001:8003:74c6:3600:fa8b:bb8e:fdfc:2e19");
		assertNull(provider.getImage(name));
		assertEquals("IP Address: 2001:8003:74c6:3600:fa8b:bb8e:fdfc:2e19", provider.getText(name));
		assertEquals("2001:8003:74c6:3600:fa8b:bb8e:fdfc:2e19", provider.getValue(name));
	}
	
	@Test
	public void testIPAddressv6_2() {
		GeneralNameLabelProvider provider = new GeneralNameLabelProvider();
		GeneralName name = GeneralNameTag.iPAddress.asGeneralName("2::1");
		assertNull(provider.getImage(name));
		assertEquals("IP Address: 2:0:0:0:0:0:0:1", provider.getText(name));
		assertEquals("2:0:0:0:0:0:0:1", provider.getValue(name));
	}
}
