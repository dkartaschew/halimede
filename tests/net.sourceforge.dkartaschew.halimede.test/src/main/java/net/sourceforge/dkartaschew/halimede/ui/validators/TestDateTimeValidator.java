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

import java.time.ZonedDateTime;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unchecked")
public class TestDateTimeValidator {

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
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, null, null);
		v.toString();
		v.hashCode();
		assertTrue(v.equals(v));
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullStart() {
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		new DatePeriodValidator(null, endDate, null, null);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructionNullEnd() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		new DatePeriodValidator(startDate, null, null, null);
	}

	@Test
	public void testNullStartDate() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		when(startDate.getValue()).thenReturn(null);
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		when(endDate.getValue()).thenReturn(ZonedDateTime.now());

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, null, null);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testNullEndDate() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		when(startDate.getValue()).thenReturn(ZonedDateTime.now());
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		when(endDate.getValue()).thenReturn(null);

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, null, null);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testOKDate() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		when(startDate.getValue()).thenReturn(ZonedDateTime.now());
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		when(endDate.getValue()).thenReturn(ZonedDateTime.now().plusHours(1));

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, null, null);
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testErrorDate() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		when(startDate.getValue()).thenReturn(ZonedDateTime.now().plusHours(1));
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		when(endDate.getValue()).thenReturn(ZonedDateTime.now());

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, null, null);
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testOKDateCADates() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		when(startDate.getValue()).thenReturn(ZonedDateTime.now());
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		when(endDate.getValue()).thenReturn(ZonedDateTime.now().plusHours(1));

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, //
				ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1));
		assertEquals(ValidationStatus.ok().getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testNotBeforeDateCADates() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		when(startDate.getValue()).thenReturn(ZonedDateTime.now());
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		when(endDate.getValue()).thenReturn(ZonedDateTime.now().plusHours(1));

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, //
				ZonedDateTime.now().plusMinutes(1), ZonedDateTime.now().plusDays(1));
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}

	@Test
	public void testNotAfterDateCADates() {
		IObservableValue<ZonedDateTime> startDate = mock(IObservableValue.class);
		when(startDate.getValue()).thenReturn(ZonedDateTime.now());
		IObservableValue<ZonedDateTime> endDate = mock(IObservableValue.class);
		when(endDate.getValue()).thenReturn(ZonedDateTime.now().plusHours(1));

		DatePeriodValidator v = new DatePeriodValidator(startDate, endDate, //
				ZonedDateTime.now().minusDays(1), ZonedDateTime.now());
		assertEquals(ValidationStatus.error("").getSeverity(), v.validate().getSeverity());
	}
}
