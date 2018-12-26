/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2019 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.enumeration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Pattern;

import org.bouncycastle.asn1.x509.GeneralName;

public enum GeneralNameTag {

	/**
	 * Other Name
	 */
	OtherName(GeneralName.otherName, "Other"), //
	/**
	 * RFC 822 Name
	 */
	rfc822Name(GeneralName.rfc822Name, "Email (RFC822)"), //
	/**
	 * DNS Registration
	 */
	dNSName(GeneralName.dNSName, "DNS"), //
	/**
	 * X400 Identifier
	 */
	x400Address(GeneralName.x400Address, "X400"), //
	/**
	 * Directory name
	 */
	directoryName(GeneralName.directoryName, "DirectoryName"), //
	/**
	 * EDI Party Name
	 */
	ediPartyName(GeneralName.ediPartyName, "EDI"), //
	/**
	 * URI - general resource type
	 */
	uniformResourceIdentifier(GeneralName.uniformResourceIdentifier, "URI"), //
	/**
	 * IP Address.
	 */
	iPAddress(GeneralName.iPAddress, "IP Address"), //
	/**
	 * Registered OID
	 */
	registeredID(GeneralName.registeredID, "Registed ID");

	/**
	 * Description
	 */
	private final String description;
	/**
	 * Usage tag
	 */
	private final int tag;

	/**
	 * Create a new Enum base KeyUsage
	 * 
	 * @param description The textual description
	 * @param usage Key Usage bit value.
	 */
	private GeneralNameTag(int tag, String description) {
		this.description = description;
		this.tag = tag;
	}

	/**
	 * Get the description
	 * 
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Get the usage tag
	 * 
	 * @return The tag
	 */
	public int getTag() {
		return tag;
	}

	@Override
	public String toString() {
		return description;
	}

	/**
	 * Create a general name for the given tag.
	 * 
	 * @param value The value
	 * @return A general name of the given type.
	 * @throws IllegalArgumentException The string value is incorrect for the tag type.
	 */
	public GeneralName asGeneralName(String value) throws IllegalArgumentException {
		if (tag == GeneralName.uniformResourceIdentifier) {
			try {
				URI u = new URI(value);
				return new GeneralName(tag, u.toString());
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		if (tag == GeneralName.dNSName) {
			Pattern p = Pattern.compile("^(?=.{1,253}\\.?$)(?:(?!-|[^.]+_)[A-Za-z0-9-_]{1,63}(?<!-)(?:\\.|$)){2,}$");
			if (p.matcher(value).matches()) {
				return new GeneralName(tag, value);
			} else {
				throw new IllegalArgumentException("DNS name appears invalid");
			}
		}
		return new GeneralName(tag, value);
	}

	/**
	 * Get the GeneralNameTag for the given tag ID
	 * 
	 * @param tagNo The tag number/id
	 * @return The general name tag or null if unknown.
	 * @throws NoSuchElementException The tag no is invalid.
	 */
	public static GeneralNameTag forTag(int tagNo) {
		return Arrays.stream(GeneralNameTag.values()).filter(o -> o.tag == tagNo).findFirst().get();
	}

	/**
	 * Get the general name tag based on the given description
	 * 
	 * @param description The description
	 * @return The general name tag or null if unknown.
	 * @throws NoSuchElementException The description doesn't match a known element.
	 * @throws NullPointerException The description was null.
	 */
	public static GeneralNameTag forDescription(String description) {
		Objects.requireNonNull(description, "Description was null");
		return Arrays.stream(GeneralNameTag.values()).filter(o -> o.description.equals(description)).findFirst().get();
	}
}
