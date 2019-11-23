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

package net.sourceforge.dkartaschew.halimede.ui.node;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;

public class CertificateAuthorityNode implements ICertificateTreeNode {

	private static final ElementType[] subtypes = { //
			ElementType.Issued, //
			ElementType.Pending, //
			ElementType.Template, //
			ElementType.Revoked, //
			ElementType.CRLs};

	private final Object[] children;

	private final CertificateAuthority ca;

	/**
	 * Create new CA node
	 * 
	 * @param ca The underlying CA
	 */
	public CertificateAuthorityNode(CertificateAuthority ca) {
		this.ca = ca;
		children = new Object[subtypes.length];
		for (int i = 0; i < subtypes.length; i++) {
			children[i] = new CertificateAuthorityElement(subtypes[i], this);
		}
	}

	@Override
	public String getImage() {
		return PluginDefaults.IMG_CERTIFICATE;
	}

	@Override
	public String getDescription() {
		String desc = ca.getDescription();
		if (desc == null || desc.isEmpty()) {
			return "CA " + ca.getCertificateAuthorityID().toString();
		}
		return desc;
	}

	/**
	 * Get the CA for this node
	 * 
	 * @return The CA
	 */
	public CertificateAuthority getCertificateAuthority() {
		return ca;
	}

	/**
	 * Get all child objects
	 * 
	 * @return An array of children.
	 */
	public Object[] getChildren() {
		return children;
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
		result = prime * result + ((ca == null) ? 0 : ca.hashCode());
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
		CertificateAuthorityNode other = (CertificateAuthorityNode) obj;
		if (ca == null) {
			if (other.ca != null)
				return false;
		} else if (!ca.equals(other.ca))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	public Object[] getItems() {
		return null; //new Object[0]; // Note; CADetails Pane assumes null is this node
	}

	@Override
	public ElementType getType() {
		return subtypes[0];
	}
}
