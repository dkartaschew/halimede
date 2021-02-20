/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2021 Darran Kartaschew 
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

/**
 * Multivalidator that ensures the start date is before the end date.
 */
public class DatePeriodValidator extends MultiValidator {

	private final IObservableValue<ZonedDateTime> startDate;
	private final IObservableValue<ZonedDateTime> endDate;
	private final ZonedDateTime notBefore;
	private final ZonedDateTime notAfter;
	private final DateFormat formatter = SimpleDateFormat.getDateInstance();

	/**
	 * Create a new validator
	 * 
	 * @param startDate The start date
	 * @param endDate The expiry date
	 * @param notAfter The date which the expiry cannot be after (may be null).
	 * @param notBefore The date which the start cannot be before (may be null).
	 */
	public DatePeriodValidator(IObservableValue<ZonedDateTime> startDate, IObservableValue<ZonedDateTime> endDate,
			ZonedDateTime notBefore, ZonedDateTime notAfter) {
		Objects.requireNonNull(startDate, "Start Date cannot be null");
		Objects.requireNonNull(endDate, "End Date cannot be null");
		this.startDate = startDate;
		this.endDate = endDate;
		this.notAfter = notAfter;
		this.notBefore = notBefore;
	}

	@Override
	protected IStatus validate() {
		ZonedDateTime field1 = (ZonedDateTime) startDate.getValue();
		ZonedDateTime field2 = (ZonedDateTime) endDate.getValue();
		if (field1 == null || field2 == null) {
			return ValidationStatus.error("Missing date information.");
		}
		if (field2.isBefore(field1)) {
			return ValidationStatus.error("The start date must be before the end date.");
		}
		if (notBefore != null) {
			if (field1.isBefore(notBefore)) {
				return ValidationStatus.error("The start date must be after the CA's Certificate Start Date ("
						+ formatter.format(DateTimeUtil.toDate(notBefore)) + ").");
			}
		}
		if (notAfter != null) {
			if (field2.isAfter(notAfter)) {
				return ValidationStatus.error("The expiry date must be before the CA's Certificate Expiry Date ("
						+ formatter.format(DateTimeUtil.toDate(notAfter)) + ").");
			}
		}
		return ValidationStatus.ok();
	}

}
