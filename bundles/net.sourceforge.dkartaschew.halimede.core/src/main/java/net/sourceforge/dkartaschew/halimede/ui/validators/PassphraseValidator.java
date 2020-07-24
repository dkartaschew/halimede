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

import java.util.Objects;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class PassphraseValidator extends MultiValidator {

	private final IObservableValue<String> field1;
	private final IObservableValue<String> field2;
	private final IObservableValue<Boolean> ignorePasswordValidation;
	private final int warningLength;

	/**
	 * Create a new validator
	 * 
	 * @param field1 The first field
	 * @param field2 The second field
	 * @param warningLength The length of the password required to return OK, rather than Warning.
	 */
	public PassphraseValidator(IObservableValue<String> field1, IObservableValue<String> field2, int warningLength) {
		this(field1, field2, warningLength, null);
	}

	/**
	 * Create a new validator
	 * 
	 * @param field1 The first field
	 * @param field2 The second field
	 * @param warningLength The length of the password required to return OK, rather than Warning.
	 * @param ignorePasswordValidation Field to use to ignore validation (may be NULL).
	 */
	public PassphraseValidator(IObservableValue<String> field1, IObservableValue<String> field2, int warningLength,
			IObservableValue<Boolean> ignorePasswordValidation) {
		Objects.requireNonNull(field1, "First Passphrase field is null");
		Objects.requireNonNull(field2, "Second Passphrase field is null");
		if (warningLength < 0) {
			throw new IllegalArgumentException("Warning length must be zero or positive");
		}
		this.field1 = field1;
		this.field2 = field2;
		this.warningLength = warningLength;
		this.ignorePasswordValidation = ignorePasswordValidation;
	}

	@Override
	protected IStatus validate() {
		if (ignorePasswordValidation != null) {
			if (ignorePasswordValidation.getValue()) {
				return ValidationStatus.ok();
			}
		}
		String str = (String) field1.getValue();
		String str2 = (String) field2.getValue();
		if (str == null || str.isEmpty()) {
			// additional test... If both strings are null / empty and 0 warning length.
			if (warningLength == 0) {
				if (str2 == null || str2.isEmpty()) {
					return ValidationStatus.ok();
				} else {
					return ValidationStatus.error("Passwords are not equal");
				}
			}
			return ValidationStatus.warning("Password should not be empty.");
		}
		if (!str.equals(str2)) {
			return ValidationStatus.error("Passwords are not equal");
		}
		if (str.length() < warningLength) {
			return ValidationStatus
					.warning(String.format("Password should be at least %d character(s)", warningLength));
		}
		return ValidationStatus.ok();
	}

}
