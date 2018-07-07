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

package net.sourceforge.dkartaschew.halimede.ui.validators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestPasphraseValidator {

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
	public void testClass() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		v.toString();
		v.hashCode();
		assertTrue(v.equals(v));
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullSecond() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		new PassphraseValidator(stringA, null, 4);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullFirst() {
		IObservableValue<String> stringB = mock(IObservableValue.class);
		new PassphraseValidator(null, stringB, 4);
	}

	@Test
	public void testConstructionZeroLength() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		new PassphraseValidator(stringA, stringB, 0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testConstructionNegativeLength() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		new PassphraseValidator(stringA, stringB, -1);
	}
	
	@Test
	public void testIgnoreStrings() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn(null);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn(null);
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(true);

		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testIgnoreStrings2() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(true);

		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testIgnoreStrings3() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("a");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(true);

		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testIgnoreStrings4() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("a");
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(true);

		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testIgnoreStrings5() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn(null);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(true);

		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testIgnoreStrings6() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn(null);
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(true);

		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testNullStrings() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn(null);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn(null);
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4);
		// Treat as EMPTY.
		assertEquals(ValidationStatus.warning("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testNullStringsZeroLength() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn(null);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn(null);
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 0);
		// Treat as EMPTY.
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testEmptyStrings() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4);
		// Treat as EMPTY.
		assertEquals(ValidationStatus.warning("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testEmptyStringsZeroLength() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 0);
		// Treat as EMPTY.
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testEmptyStringsZeroLength2() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn(null);
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 0);
		// Treat as EMPTY.
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testEmptyStringsZeroLength3() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn(null);
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 0);
		// Treat as EMPTY.
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testEmptyStringsZeroLength4() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn(" ");
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 0);
		// Treat as EMPTY.
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testShortStrings() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("abc");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("abc");
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4);
		assertEquals(ValidationStatus.warning("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testMatchingStrings() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("abc");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("abc");
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 3);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testDifferentStrings() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("abc");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testMatchingStringsWithIgnore() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("abc");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("abc");
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(false);
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 3, ignore);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}
	
	@Test
	public void testDifferentStringsWithIgnore() {
		IObservableValue<String> stringA = mock(IObservableValue.class);
		when(stringA.getValue()).thenReturn("abc");
		IObservableValue<String> stringB = mock(IObservableValue.class);
		when(stringB.getValue()).thenReturn("");
		IObservableValue<Boolean> ignore = mock(IObservableValue.class);
		when(ignore.getValue()).thenReturn(false);
		PassphraseValidator v = new PassphraseValidator(stringA, stringB, 4, ignore);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
}
