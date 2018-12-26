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

package net.sourceforge.dkartaschew.halimede.ui.model;

import java.util.Objects;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;

/**
 * Very simple X500 Name model.
 */
public class X500NameModel {

	private String commonName = "";

	private String organisationUnit = "";

	private String emailAddress = "";

	private String organisation = "";

	private String location = "";

	private String street = "";

	private String state = "";

	private String country = "";

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		Objects.requireNonNull(commonName, "Common Name cannot be null");
		this.commonName = commonName.trim();
	}

	public String getOrganisationUnit() {
		return organisationUnit;
	}

	public void setOrganisationUnit(String organisationUnit) {
		Objects.requireNonNull(organisationUnit, "Organisation Unit cannot be null");
		this.organisationUnit = organisationUnit.trim();
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		Objects.requireNonNull(emailAddress, "Email cannot be null");
		this.emailAddress = emailAddress.trim();
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setOrganisation(String organisation) {
		Objects.requireNonNull(organisation, "Organisation cannot be null");
		this.organisation = organisation.trim();
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		Objects.requireNonNull(location, "Location cannot be null");
		this.location = location.trim();
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		Objects.requireNonNull(street, "Street cannot be null");
		this.street = street.trim();
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		Objects.requireNonNull(state, "State cannot be null");
		this.state = state.trim();
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		Objects.requireNonNull(country, "Country cannot be null");
		this.country = country.trim();
	}

	/**
	 * Create a new model based on the given name.
	 * <p>
	 * Only a small subset of RDNs are supported.
	 * 
	 * @param name The name to extract from
	 * @return The model instance.
	 */
	public static X500NameModel create(X500Name name) {
		X500NameModel model = new X500NameModel();
		if (name != null) {
			RDN[] rdns = name.getRDNs(BCStyle.CN);
			if (rdns.length > 0) {
				model.commonName = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
			rdns = name.getRDNs(BCStyle.EmailAddress);
			if (rdns.length > 0) {
				model.emailAddress = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
			rdns = name.getRDNs(BCStyle.OU);
			if (rdns.length > 0) {
				model.organisationUnit = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
			rdns = name.getRDNs(BCStyle.O);
			if (rdns.length > 0) {
				model.organisation = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
			rdns = name.getRDNs(BCStyle.L);
			if (rdns.length > 0) {
				model.location = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
			rdns = name.getRDNs(BCStyle.STREET);
			if (rdns.length > 0) {
				model.street = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
			rdns = name.getRDNs(BCStyle.ST);
			if (rdns.length > 0) {
				model.state = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
			rdns = name.getRDNs(BCStyle.C);
			if (rdns.length > 0) {
				model.country = IETFUtils.valueToString(rdns[0].getFirst().getValue());
			}
		}
		return model;
	}

	/**
	 * Get the X500 Name
	 * 
	 * @return The model as a X500 Name or NULL if not valid.
	 */
	public X500Name asX500Name() {
		if (commonName.trim().isEmpty()) {
			return null;
		}
		X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		add(builder, BCStyle.CN, commonName);
		add(builder, BCStyle.EmailAddress, emailAddress);
		add(builder, BCStyle.OU, organisationUnit);
		add(builder, BCStyle.O, organisation);
		add(builder, BCStyle.L, location);
		add(builder, BCStyle.STREET, street);
		add(builder, BCStyle.ST, state);
		add(builder, BCStyle.C, country);
		return builder.build();
	}

	/**
	 * Add the value to the model
	 * 
	 * @param builder The builder to add to.
	 * @param oid The ID of the value
	 * @param value The value.
	 */
	private void add(X500NameBuilder builder, ASN1ObjectIdentifier oid, String value) {
		if (!value.trim().isEmpty()) {
			builder.addRDN(oid, value);
		}
	}
}
