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

import java.util.Collection;

import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class X500NameValidator implements IValidator<String> {

	private final Collection<X500Name> issuers;

	/**
	 * Create a new X500Name validators
	 * 
	 * @param names The collection of names that the validation cannot be equal to. (May be NULL for no name checks
	 *            required).
	 */
	public X500NameValidator(Collection<X500Name> names) {
		this.issuers = names;
	}

	@Override
	public IStatus validate(String value) {
		if (value == null) {
			return ValidationStatus.error("X500 Name cannot be empty");
		}
		if (value.isEmpty()) {
			return ValidationStatus.error("X500 Name cannot be empty");
		}
		// Attempt to convert to X500Name, and confirm it has a CN RDN.
		try {
			X500Name name = new X500Name(value);
			if (cannotBe(name)) {
				return ValidationStatus.error("X500Name cannot be equal to an Issuers DN");
			}
			RDN[] rdns = name.getRDNs(BCStyle.CN);
			for (RDN r : rdns) {
				for (AttributeTypeAndValue attr : r.getTypesAndValues()) {
					if (attr != null) {
						String commonName = attr.getValue().toString();
						if (!commonName.isEmpty()) {
							return ValidationStatus.ok();
						}
					}
				}
			}
			return ValidationStatus.error("X500Name missing Common Name element.");
		} catch (Throwable e) {
			return ValidationStatus.error("Does not appear to be a valid X500Name? Error: " + ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Issue this name is not equal to one of the names provided.
	 * 
	 * @param name The X500 name
	 * @return TRUE if the provided X500Name is equal to one of the names provided.
	 */
	private boolean cannotBe(X500Name name) {
		if (issuers == null || issuers.isEmpty()) {
			return false;
		}
		return issuers.contains(name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((issuers == null) ? 0 : issuers.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		X500NameValidator other = (X500NameValidator) obj;
		if (issuers == null) {
			if (other.issuers != null)
				return false;
		} else if (!issuers.equals(other.issuers))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		if(issuers == null || issuers.isEmpty()) {
			return "X500NameValidator []";
		}
		return "X500NameValidator " + issuers.toString();
	}

}
