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

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Objects;

public class CACRLModel {

	/**
	 * The Certificate Authority Description.
	 */
	private final String ca;
	/**
	 * The CRL Serial Number
	 */
	private final BigInteger serial;
	/**
	 * The CRL Issue Date;
	 */
	private final ZonedDateTime issueDate;

	/**
	 * The CRL Next Date;
	 */
	private ZonedDateTime nextDate;

	/**
	 * Create a new CRL Model
	 * 
	 * @param ca The CA's description
	 * @param serial The serial number for the CRL
	 * @param issueDate The issue date.
	 * @throws NullPointerException If CA, Serial or issueDate are NULL.
	 */
	public CACRLModel(String ca, BigInteger serial, ZonedDateTime issueDate) {
		Objects.requireNonNull(ca, "No CA Description");
		Objects.requireNonNull(serial, "No CRL Serial");
		Objects.requireNonNull(issueDate, "No Issued Date");
		this.ca = ca;
		this.serial = serial;
		this.issueDate = issueDate;
	}

	public ZonedDateTime getNextDate() {
		return nextDate;
	}

	public void setNextDate(ZonedDateTime nextDate) {
		this.nextDate = nextDate;
	}

	public String getCa() {
		return ca;
	}

	public BigInteger getSerial() {
		return serial;
	}

	public ZonedDateTime getIssueDate() {
		return issueDate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ca.hashCode();
		result = prime * result + issueDate.hashCode();
		result = prime * result + serial.hashCode();
		result = prime * result + ((nextDate == null) ? 0 : nextDate.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CACRLModel other = (CACRLModel) obj;

		if (!ca.equals(other.ca))
			return false;
		if (!issueDate.equals(other.issueDate))
			return false;
		if (!serial.equals(other.serial))
			return false;

		if (nextDate == null) {
			if (other.nextDate != null)
				return false;
		} else if (!nextDate.equals(other.nextDate))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CACRLModel [ca=" + ca + ", serial=" + serial + "]";
	}
}
