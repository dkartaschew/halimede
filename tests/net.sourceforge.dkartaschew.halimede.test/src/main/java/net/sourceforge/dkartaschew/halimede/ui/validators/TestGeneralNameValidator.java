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

package net.sourceforge.dkartaschew.halimede.ui.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageID;
import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;

@SuppressWarnings("unchecked")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestGeneralNameValidator {

	private DefaultUnitTestRealm realm;

	@Before
	public void setUp() throws Exception {
		realm = new DefaultUnitTestRealm();
	}

	@After
	public void tearDown() throws Exception {
		realm.dispose();
	}

	@Test
	public void testObject() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		IObservableValue<String> name = mock(IObservableValue.class);

		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		v.toString();
		v.hashCode();
		assertTrue(v.equals(v));
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullTag() {
		IObservableValue<String> name = mock(IObservableValue.class);
		new GeneralNameValidator(null, name);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullName() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		new GeneralNameValidator(tag, null);
	}

	@Test
	public void testNullTagValue() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(null);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("abc");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testNullNameValue() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.uniformResourceIdentifier);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn(null);
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testEmptyNameValue() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.uniformResourceIdentifier);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testURI() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.uniformResourceIdentifier);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("http://good");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testURI_Bad() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.uniformResourceIdentifier);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("123$%^*&FD)DS(&(*&");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testDIR() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.directoryName);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("CN=BAD");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testDIR_Bad() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.directoryName);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("123$%^*&FD)DS(&(*&");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testDNS() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.dNSName);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("mx1.text.com");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testDNS_Bad() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.dNSName);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("123$%^*&FD)DS(&(*&");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testRFC() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.rfc822Name);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("a@a.com");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	@Ignore // Ignore as RFC names are not checked.
	public void testRFC_Bad() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.rfc822Name);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("123$%^*&FD)DS(&(*&");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testIP() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.iPAddress);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("127.0.0.1");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testIP_Bad() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.iPAddress);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn("123$%^*&FD)DS(&(*&");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testRegisteredID() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.registeredID);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn(ExtendedKeyUsageID.id_kp.getId());
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testRegisteredID_Bad() {
		IObservableValue<GeneralNameTag> tag = mock(IObservableValue.class);
		when(tag.getValue()).thenReturn(GeneralNameTag.registeredID);
		IObservableValue<String> name = mock(IObservableValue.class);
		when(name.getValue()).thenReturn(ExtendedKeyUsageID.id_kp.getId() + "123$%^*&FD)DS(&(*&");
		
		GeneralNameValidator v = new GeneralNameValidator(tag, name);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
}
