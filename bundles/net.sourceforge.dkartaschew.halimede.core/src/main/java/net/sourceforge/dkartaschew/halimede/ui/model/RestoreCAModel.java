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

/**
 * Model for restoring CA.
 */
public class RestoreCAModel {

	private String filename;
	private String baseLocation;
	private boolean addToManager;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getBaseLocation() {
		return baseLocation;
	}

	public void setBaseLocation(String baseLocation) {
		this.baseLocation = baseLocation;
	}

	public boolean isAddToManager() {
		return addToManager;
	}

	public void setAddToManager(boolean addToManager) {
		this.addToManager = addToManager;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (addToManager ? 1231 : 1237);
		result = prime * result + ((baseLocation == null) ? 0 : baseLocation.hashCode());
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
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
		RestoreCAModel other = (RestoreCAModel) obj;
		if (addToManager != other.addToManager)
			return false;
		if (baseLocation == null) {
			if (other.baseLocation != null)
				return false;
		} else if (!baseLocation.equals(other.baseLocation))
			return false;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getFilename());
		sb.append(" => ");
		sb.append(getBaseLocation());
		sb.append(" : ");
		sb.append(isAddToManager());
		return sb.toString();
	}
}
