/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
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

public class CertificateAuthorityElement implements ICertificateTreeNode {

	/**
	 * The nodes type;
	 */
	private final ElementType type;

	/**
	 * The parent.
	 */
	private final CertificateAuthorityNode parent;

	/**
	 * The sub node for the CA.
	 * 
	 * @param type The type of element.
	 * @param parent The parent of this element.
	 */
	public CertificateAuthorityElement(ElementType type, CertificateAuthorityNode parent) {
		this.parent = parent;
		this.type = type;
	}

	@Override
	public String getImage() {
		return PluginDefaults.IMG_FOLDER;
	}

	@Override
	public String getDescription() {
		return type.name();
	}

	/**
	 * Get the parent
	 * 
	 * @return The parent of this node
	 */
	public CertificateAuthorityNode getParent() {
		return parent;
	}

	@Override
	public ElementType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		CertificateAuthorityElement other = (CertificateAuthorityElement) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getDescription();
	}

	@Override
	public Object[] getItems() {
		switch (type) {
		case Issued:
			return parent.getCertificateAuthority().getIssuedCertificates().toArray();
		case Pending:
			return parent.getCertificateAuthority().getCertificateRequests().toArray();
		case Revoked:
			return parent.getCertificateAuthority().getRevokedCertificates().toArray();
		case Template:
			return parent.getCertificateAuthority().getCertificateKeyPairTemplates().toArray();
		case CRLs:
			return parent.getCertificateAuthority().getCRLs().toArray();
		default:
			return new Object[0];
		}
	}

}
