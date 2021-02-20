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

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

/**
 * Key Type size warning, to inform the user that the keying material may take time to generate.
 */
public class KeyTypeWarningValidator implements IValidator<KeyType> {

	@Override
	public IStatus validate(KeyType key) {
		if (key.getType().equals("DSA") && key.getBitLength() >= 1024) {
			return ValidationStatus.warning("DSA Keys may take time to generate.");
		}
		if (key.getType().equals("RSA") && key.getBitLength() > 1024) {
			return ValidationStatus.warning("RSA Keys greater than 1024 bits may take time to generate.");
		}
		if (key.getType().equals("XMSS") && key.getHeight() > 10) {
			return ValidationStatus.warning("XMSS Keys with a height greater than 10 may take time to generate. (In some case key generation may take hours).");
		}
		if (key.getType().equals("XMSSMT") && key.getHeight() / key.getLayers() > 5) {
			return ValidationStatus.warning("XMSS-MT Keys may take time to generate. (In some case key generation may take hours).");
		}
		return ValidationStatus.ok();
	}

}
