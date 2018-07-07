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

package net.sourceforge.dkartaschew.halimede.ui.model;

import java.time.ZonedDateTime;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;

/**
 * Data model for new CA creation.
 * <p>
 * Model does NOT do validation, and is effectively a struct.
 *
 */
public class NewCAModel {

	private String cADescription;
	private String baseLocation;
	private String x500Name;
	private String cRLLocation;
	private String password;
	private ZonedDateTime startDate;
	private ZonedDateTime expiryDate;
	private KeyType keyType;

	private boolean pkcs12;
	private boolean certPrivateKey;
	private String pkcs12Filename;
	private String certFilename;
	private String privateKeyFilename;

	public String getcADescription() {
		return cADescription;
	}

	public void setcADescription(String cADescription) {
		this.cADescription = cADescription;
	}

	public String getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	public String getX500Name() {
		return x500Name;
	}

	public void setX500Name(String x500Name) {
		this.x500Name = x500Name;
	}

	public String getcRLLocation() {
		return cRLLocation;
	}

	public void setcRLLocation(String cRLLocation) {
		this.cRLLocation = cRLLocation;
	}

	public ZonedDateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public ZonedDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(ZonedDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isPkcs12() {
		return pkcs12;
	}

	public void setPkcs12(boolean pkcs12) {
		this.pkcs12 = pkcs12;
	}

	public boolean isCertPrivateKey() {
		return certPrivateKey;
	}

	public void setCertPrivateKey(boolean certPrivateKey) {
		this.certPrivateKey = certPrivateKey;
	}

	public String getPkcs12Filename() {
		return pkcs12Filename;
	}

	public void setPkcs12Filename(String pkcs12Filename) {
		this.pkcs12Filename = pkcs12Filename;
	}

	public String getCertFilename() {
		return certFilename;
	}

	public void setCertFilename(String certFilename) {
		this.certFilename = certFilename;
	}

	public String getPrivateKeyFilename() {
		return privateKeyFilename;
	}

	public void setPrivateKeyFilename(String privateKeyFilename) {
		this.privateKeyFilename = privateKeyFilename;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((baseLocation == null) ? 0 : baseLocation.hashCode());
		result = prime * result + ((cADescription == null) ? 0 : cADescription.hashCode());
		result = prime * result + ((cRLLocation == null) ? 0 : cRLLocation.hashCode());
		result = prime * result + ((certFilename == null) ? 0 : certFilename.hashCode());
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + (certPrivateKey ? 1231 : 1237);
		result = prime * result + (pkcs12 ? 1231 : 1237);
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((pkcs12Filename == null) ? 0 : pkcs12Filename.hashCode());
		result = prime * result + ((privateKeyFilename == null) ? 0 : privateKeyFilename.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + ((x500Name == null) ? 0 : x500Name.hashCode());
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
		NewCAModel other = (NewCAModel) obj;
		if (baseLocation == null) {
			if (other.baseLocation != null)
				return false;
		} else if (!baseLocation.equals(other.baseLocation))
			return false;
		if (cADescription == null) {
			if (other.cADescription != null)
				return false;
		} else if (!cADescription.equals(other.cADescription))
			return false;
		if (cRLLocation == null) {
			if (other.cRLLocation != null)
				return false;
		} else if (!cRLLocation.equals(other.cRLLocation))
			return false;
		if (certFilename == null) {
			if (other.certFilename != null)
				return false;
		} else if (!certFilename.equals(other.certFilename))
			return false;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (certPrivateKey != other.certPrivateKey)
			return false;
		if (pkcs12 != other.pkcs12)
			return false;
		if (keyType != other.keyType)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (pkcs12Filename == null) {
			if (other.pkcs12Filename != null)
				return false;
		} else if (!pkcs12Filename.equals(other.pkcs12Filename))
			return false;
		if (privateKeyFilename == null) {
			if (other.privateKeyFilename != null)
				return false;
		} else if (!privateKeyFilename.equals(other.privateKeyFilename))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (x500Name == null) {
			if (other.x500Name != null)
				return false;
		} else if (!x500Name.equals(other.x500Name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if(cADescription == null) {
			return "NewCAModel []";
		}
		return "NewCAModel [" + cADescription + "]";
	}

}
