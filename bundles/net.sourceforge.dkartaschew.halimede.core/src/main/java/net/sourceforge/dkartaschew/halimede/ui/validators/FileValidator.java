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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

public class FileValidator implements IValidator<String> {

	@Override
	public IStatus validate(String value) {
		if (value == null) {
			return ValidationStatus.error("Filename cannot be empty");
		}
		if (value.isEmpty()) {
			return ValidationStatus.error("Filename cannot be empty");
		}
		Path p = Paths.get(value);
		if (!p.isAbsolute()) {
			return ValidationStatus.error("Filename field must contain an absolute path.");
		}
		if (!Files.exists(p) || !Files.isRegularFile(p) || !Files.isReadable(p)) {
			return ValidationStatus.error("The file does not exist, is not a regular file or is not readable.");
		}
		return ValidationStatus.ok();
	}

}
