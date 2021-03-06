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

import java.net.URI;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class URIValidator implements IValidator<String> {

	private final IObservableValue<Boolean> validate;

	public URIValidator() {
		this(null);
	}

	public URIValidator(IObservableValue<Boolean> validate) {
		this.validate = validate;
	}

	@Override
	public IStatus validate(String value) {
		if (validate != null) {
			boolean v = validate.getValue();
			if (!v) {
				return ValidationStatus.ok();
			}
		}
		if (value == null) {
			return ValidationStatus.warning("Location URI is empty");
		}
		if (value.isEmpty()) {
			return ValidationStatus.warning("Location URI is empty");
		}
		try {
			URI uri = new URI(value);
			// attempt to constuct a general name (URI) from the URI.
			GeneralNameTag.uniformResourceIdentifier.asGeneralName(uri.toString());
			return ValidationStatus.ok();
		} catch (Throwable e) {
			return ValidationStatus.error("The Location URI is invalid: " + ExceptionUtil.getMessage(e));
		}
	}
}
