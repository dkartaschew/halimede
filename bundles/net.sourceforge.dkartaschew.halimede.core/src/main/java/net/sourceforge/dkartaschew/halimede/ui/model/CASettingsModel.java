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

import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

public class CASettingsModel {

	/**
	 * The node id of the CA
	 */
	private UUID nodeID;

	/**
	 * The base path
	 */
	private Path basePath;

	/**
	 * The description
	 */
	private String description;

	/**
	 * The number of days for expiry.
	 */
	private int expiryDays;

	/**
	 * The default signature algorithm.
	 */
	private SignatureAlgorithm signatureAlgorithm;
	
	/**
	 * The default signature algorithm.
	 */
	private SignatureAlgorithm[] signatureAlgorithms;
	
	/**
	 * The Certificate subject.
	 */
	private String subject;
	
	/**
	 * Incremental Serial denotes if we are using incremental serial (true) or
	 * timestamp (false) as the serial.
	 */
	private boolean incrementalSerial;
	
	/**
	 * Activity Log enable flag.
	 */
	private boolean enableLog;

	public UUID getNodeID() {
		return nodeID;
	}

	public void setNodeID(UUID nodeID) {
		this.nodeID = nodeID;
	}

	public Path getBasePath() {
		return basePath;
	}

	public void setBasePath(Path basePath) {
		this.basePath = basePath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getExpiryDays() {
		return expiryDays;
	}

	public void setExpiryDays(int expiryDays) {
		this.expiryDays = expiryDays;
	}

	public SignatureAlgorithm getSignatureAlgorithm() {
		return signatureAlgorithm;
	}

	public void setSignatureAlgorithm(SignatureAlgorithm signatureAlgorithm) {
		this.signatureAlgorithm = signatureAlgorithm;
	}
	
	public SignatureAlgorithm[] getSignatureAlgorithms() {
		return signatureAlgorithms;
	}

	public void setSignatureAlgorithms(SignatureAlgorithm[] signatureAlgorithms) {
		this.signatureAlgorithms = signatureAlgorithms;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isIncrementalSerial() {
		return incrementalSerial;
	}

	public void setIncrementalSerial(boolean incrementalSerial) {
		this.incrementalSerial = incrementalSerial;
	}
		
	public boolean isEnableLog() {
		return enableLog;
	}

	public void setEnableLog(boolean enableLog) {
		this.enableLog = enableLog;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((basePath == null) ? 0 : basePath.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + expiryDays;
		result = prime * result + (incrementalSerial ? 1231 : 1237);
		result = prime * result + (enableLog ? 1231 : 1237);
		result = prime * result + ((nodeID == null) ? 0 : nodeID.hashCode());
		result = prime * result + ((signatureAlgorithm == null) ? 0 : signatureAlgorithm.hashCode());
		result = prime * result + Arrays.hashCode(signatureAlgorithms);
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		CASettingsModel other = (CASettingsModel) obj;
		if (basePath == null) {
			if (other.basePath != null)
				return false;
		} else if (!basePath.equals(other.basePath))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (expiryDays != other.expiryDays)
			return false;
		if (incrementalSerial != other.incrementalSerial)
			return false;
		if (enableLog != other.enableLog)
			return false;
		if (nodeID == null) {
			if (other.nodeID != null)
				return false;
		} else if (!nodeID.equals(other.nodeID))
			return false;
		if (signatureAlgorithm != other.signatureAlgorithm)
			return false;
		if (!Arrays.equals(signatureAlgorithms, other.signatureAlgorithms))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if(nodeID == null) {
			return "CASettingsModel";
		}
		return "CASettingsModel [node=" + nodeID + "]";
	}

}
