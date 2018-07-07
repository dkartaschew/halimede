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

import java.util.Objects;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class GeneralNameValidator extends MultiValidator {

	private final IObservableValue<GeneralNameTag> tag;
	private final IObservableValue<String> name;

	public GeneralNameValidator(IObservableValue<GeneralNameTag> tag, IObservableValue<String> name) {
		Objects.requireNonNull(tag, "Tag cannot be null");
		Objects.requireNonNull(name, "Name cannot be null");
		this.tag = tag;
		this.name = name;
	}

	@Override
	protected IStatus validate() {
		GeneralNameTag field1 = (GeneralNameTag) tag.getValue();
		if (field1 == null) {
			return ValidationStatus.error("No Tag selected!");
		}
		String field2 = (String) name.getValue();
		if (field2 == null || field2.isEmpty()) {
			return ValidationStatus.error("The name is empty");
		}
		try {
			field1.asGeneralName(field2);
			return ValidationStatus.ok();
		} catch (Throwable e) {
			return ValidationStatus.error("The Name is invalid: " + ExceptionUtil.getMessage(e));
		}

	}

}
