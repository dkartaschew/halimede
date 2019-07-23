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

package net.sourceforge.dkartaschew.halimede.enumeration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.NoSuchElementException;

import org.bouncycastle.asn1.x509.GeneralName;
import org.junit.Ignore;
import org.junit.Test;

public class TestGeneralNameTag {

	@Test
	public void testEnum() {
		for (GeneralNameTag t : GeneralNameTag.values()) {
			assertEquals(t, GeneralNameTag.valueOf(t.name()));
			assertEquals(0, t.compareTo(t));
			assertTrue(t.equals(t));
			System.out.println(t.toString());
			System.out.println(t.getDescription());
			
			assertEquals(t, GeneralNameTag.forDescription(t.getDescription()));
			assertEquals(t, GeneralNameTag.forTag(t.getTag()));
		}
	}
	
	@Test(expected=NullPointerException.class)
	public void testForDescriptionNull() {
		assertEquals(null, GeneralNameTag.forDescription(null));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testForDescriptionEmpty() {
		assertEquals(null, GeneralNameTag.forDescription(""));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testForDescriptionUnknown() {
		assertEquals(null, GeneralNameTag.forDescription("Unknown"));
	}
	
	@Test(expected=NoSuchElementException.class)
	public void testForTagUnknown() {
		assertEquals(null, GeneralNameTag.forTag(-1));
	}
    
    @Test
    @Ignore
	public void testForGeneralNameOther() {
		GeneralName n = new GeneralName(GeneralName.otherName, "http://a.com");
		assertEquals(n, GeneralNameTag.OtherName.asGeneralName("http://a.com"));
	}
    
    @Test
	public void testForGeneralNameRFC() {
		GeneralName n = new GeneralName(GeneralName.rfc822Name, "a@a.com");
		assertEquals(n, GeneralNameTag.rfc822Name.asGeneralName("a@a.com"));
	}
    
    @Test
	public void testForGeneralNameDNS() {
		GeneralName n = new GeneralName(GeneralName.dNSName, "a.com");
		assertEquals(n, GeneralNameTag.dNSName.asGeneralName("a.com"));
	}
    
    @Test(expected=IllegalArgumentException.class)
    public void testForGeneralNameDNS_BadValue() {
    	GeneralNameTag.dNSName.asGeneralName("123.::a");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testForGeneralNameDNS_BadValue2() {
    	GeneralNameTag.dNSName.asGeneralName("");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testForGeneralNameDNS_BadValue3() {
    	GeneralNameTag.dNSName.asGeneralName("  ");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testForGeneralNameDNS_BadValueNull() {
    	GeneralNameTag.dNSName.asGeneralName(null);
    }
    
    @Test
    @Ignore
	public void testForGeneralNameX400() {
		GeneralName n = new GeneralName(GeneralName.x400Address, "CN=T");
		assertEquals(n, GeneralNameTag.x400Address.asGeneralName("CN=T"));
	}
    
    @Test
	public void testForGeneralNameDN() {
		GeneralName n = new GeneralName(GeneralName.directoryName, "CN=T");
		assertEquals(n, GeneralNameTag.directoryName.asGeneralName("CN=T"));
	}
    @Test
    @Ignore
	public void testForGeneralNameEDI() {
		GeneralName n = new GeneralName(GeneralName.ediPartyName, "http://a.com");
		assertEquals(n, GeneralNameTag.ediPartyName.asGeneralName("http://a.com"));
	}
    @Test
	public void testForGeneralNameURI() {
		GeneralName n = new GeneralName(GeneralName.uniformResourceIdentifier, "http://a.com");
		assertEquals(n, GeneralNameTag.uniformResourceIdentifier.asGeneralName("http://a.com"));
	}
    
    @Test(expected=IllegalArgumentException.class)
	public void testForGeneralNameURI_BadValue() {
		GeneralNameTag.uniformResourceIdentifier.asGeneralName("http@\\  123 []?://a.com");
	}
    
    @Test(expected=IllegalArgumentException.class)
	public void testForGeneralNameURI_BadValue2() {
		GeneralNameTag.uniformResourceIdentifier.asGeneralName("");
	}
    
    @Test(expected=IllegalArgumentException.class)
  	public void testForGeneralNameURI_BadValue3() {
  		GeneralNameTag.uniformResourceIdentifier.asGeneralName("  ");
  	}
    
    @Test(expected=IllegalArgumentException.class)
	public void testForGeneralNameURI_BadValueNull() {
		GeneralNameTag.uniformResourceIdentifier.asGeneralName(null);
	}
    
	@Test
	public void testForGeneralNameIP() {
		GeneralName n = new GeneralName(GeneralName.iPAddress, "::1");
		assertEquals(n, GeneralNameTag.iPAddress.asGeneralName("::1"));
	}
	
	@Test
	public void testForGeneralNameRID() {
		GeneralName n = new GeneralName(GeneralName.registeredID, ExtendedKeyUsageID.id_kp.getId());
		assertEquals(n, GeneralNameTag.registeredID.asGeneralName(ExtendedKeyUsageID.id_kp.getId()));
	}
}
