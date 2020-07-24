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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility functions for DateTime conversions
 */
public class DateTimeUtil {

	/**
	 * Default Zone for util functions.
	 */
	public final static ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
	/**
	 * Default format patter for CDateTime widget.
	 */
	public final static String DEFAULT_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

	/**
	 * Date time formatter for date store/load operations.
	 */
	public final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_FORMAT)
			.withZone(ZoneId.of("UTC"));

	/**
	 * Convert the date to the predefined date storage format.
	 * 
	 * @param date The date
	 * @return The preferred string representation.
	 */
	public static String toString(Date date) {
		if (date == null) {
			return null;
		}
		return FORMATTER.format(date.toInstant());
	}

	/**
	 * Convert the date to the predefined date storage format.
	 * 
	 * @param date The date
	 * @return The preferred string representation.
	 */
	public static String toString(ZonedDateTime date) {
		if (date == null) {
			return null;
		}
		return FORMATTER.format(date);
	}

	/**
	 * Convert the string from the predefined string format to a {@link Date} object
	 * 
	 * @param date The date in string format
	 * @return The date
	 */
	public static Date toDate(String date) {
		if (date == null) {
			return null;
		}
		return Date.from(toZonedDateTime(date).toInstant());
	}

	/**
	 * Convert the string from the predefined string format to a {@link ZonedDateTime} object
	 * 
	 * @param date The date in string format
	 * @return The date
	 */
	public static ZonedDateTime toZonedDateTime(String date) {
		if (date == null) {
			return null;
		}
		return ZonedDateTime.parse(date, FORMATTER);
	}

	/**
	 * Convert the Date to a {@link ZonedDateTime} object with ZoneID of UTC
	 * 
	 * @param date The date in string format
	 * @return The date
	 */
	public static ZonedDateTime toZonedDateTime(Date date) {
		if (date == null) {
			return null;
		}
		return ZonedDateTime.ofInstant(date.toInstant(), DEFAULT_ZONE);
	}

	/**
	 * Convert the ZonedDateTime to a {@link Date} object
	 * 
	 * @param date The date
	 * @return The date
	 */
	public static Date toDate(ZonedDateTime date) {
		if (date == null) {
			return null;
		}
		return Date.from(date.toInstant());
	}

}
