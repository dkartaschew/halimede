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

package net.sourceforge.dkartaschew.halimede.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.Date;

import org.junit.Test;

public class TestDateTimeUtil {

	@Test
	public void testObject() {
		DateTimeUtil st = new DateTimeUtil();
		assertEquals(st.hashCode(), st.hashCode());
		st.toString();
		assertEquals(st, st);
	}
	
	@Test
	public void dateFormat_Date() {
		Date date = new Date();
		assertEquals(date, DateTimeUtil.toDate(DateTimeUtil.toString(date)));
	}

	@Test
	public void dateFormat_ZonedDateTime() {
		// For comparison, date need to be in the same time zone.
		// We also set the sub second component to 0 to account for precision differences between types.
		ZonedDateTime date = Instant.now().atZone(ZoneId.of("UTC")).withNano(0);
		assertEquals(date, DateTimeUtil.toZonedDateTime(DateTimeUtil.toString(date)));
	}

	@Test
	public void dateFormat_Date_toZonedDateTime() {
		Date date = new Date();
		assertEquals(date, DateTimeUtil.toDate(DateTimeUtil.toZonedDateTime(date)));
	}

	@Test
	public void dateFormat_ZonedDateTime_toDate() {
		// For comparison, date need to be in the same time zone.
		// We also set the sub second component to 0 to account for precision differences between types.
		ZonedDateTime date = Instant.now().atZone(ZoneId.of("UTC")).withNano(0);
		assertEquals(date, DateTimeUtil.toZonedDateTime(DateTimeUtil.toDate(date)));
	}
	
	@Test
	public void dateFormat_Date_to_ZonedDateTime() {
		// For comparison, date need to be in the same time zone
		// We also set the sub second component to 0 to account for precision differences between types.
		Instant time = Instant.now().with(ChronoField.MICRO_OF_SECOND, 0);
		ZonedDateTime date = time.atZone(ZoneId.of("UTC")).withNano(0);
		Date date2 = Date.from(time);
		assertEquals(date, DateTimeUtil.toZonedDateTime(DateTimeUtil.toString(date2)));
	}

	@Test
	public void dateFormat_ZonedDateTime_to_Date() {
		// For comparison, date need to be in the same time zone.
		Instant time = Instant.now();
		ZonedDateTime date = time.atZone(ZoneId.of("UTC"));
		Date date2 = Date.from(time);
		assertEquals(date2, DateTimeUtil.toDate(DateTimeUtil.toString(date)));
	}
	
	@Test
	public void dateFormat_Date_null() {
		assertNull(DateTimeUtil.toString((Date)null));
	}
	
	@Test
	public void dateFormat_ZonedDateTime_null() {
		assertNull(DateTimeUtil.toString((ZonedDateTime)null));
	}
	
	@Test
	public void dateFormat_toDate_null() {
		assertNull(DateTimeUtil.toDate((String)null));
	}
	
	@Test
	public void dateFormat_toZonedDateTime_null() {
		assertNull(DateTimeUtil.toZonedDateTime((String)null));
	}
	
	@Test
	public void dateFormat_ZonedDateTimetoDate_null() {
		assertNull(DateTimeUtil.toDate((ZonedDateTime)null));
	}
	
	@Test
	public void dateFormat_DatetoZonedDateTime_null() {
		assertNull(DateTimeUtil.toZonedDateTime((Date)null));
	}
	
}
