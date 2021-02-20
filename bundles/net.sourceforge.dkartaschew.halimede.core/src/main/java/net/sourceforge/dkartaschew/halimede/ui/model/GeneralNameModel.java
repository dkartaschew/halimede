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

package net.sourceforge.dkartaschew.halimede.ui.model;

import java.util.Objects;

import org.bouncycastle.asn1.x509.GeneralName;

import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.ui.labelproviders.GeneralNameLabelProvider;

/**
 * Model for creation of General Name Dialog
 */
public class GeneralNameModel {

	/**
	 * The type of general name
	 */
	private GeneralNameTag tag;

	/**
	 * The value of the general name.
	 */
	private String value;

	/**
	 * Create an empty model. (default tag is DNS).
	 */
	public GeneralNameModel() {
		tag = GeneralNameTag.dNSName;
		value = "";
	}

	/**
	 * Create a model from a General Name
	 * 
	 * @param name The general name.
	 */
	public GeneralNameModel(GeneralName name) {
		Objects.requireNonNull(name, "GeneralName cannot be null for model");
		tag = GeneralNameTag.forTag(name.getTagNo());
		value = new GeneralNameLabelProvider().getValue(name);
	}

	/**
	 * Get the tag enum
	 * 
	 * @return The tag
	 */
	public GeneralNameTag getTag() {
		return tag;
	}

	/**
	 * Set the tag
	 * 
	 * @param tag The tag
	 */
	public void setTag(GeneralNameTag tag) {
		this.tag = tag;
	}

	/**
	 * Get the value of the general name
	 * 
	 * @return The current set value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the value
	 * 
	 * @param value The value.
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Create a general name for the given tag.
	 * 
	 * @return A general name of the given type.
	 * @throws IllegalArgumentException The string value is incorrect for the tag type.
	 */
	public GeneralName createGeneralNameFromModel() {
		if (tag == null) {
			throw new IllegalArgumentException("Missing tag for General Name");
		}
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("Missing value for General Name");
		}
		return tag.asGeneralName(value);
	}

}
