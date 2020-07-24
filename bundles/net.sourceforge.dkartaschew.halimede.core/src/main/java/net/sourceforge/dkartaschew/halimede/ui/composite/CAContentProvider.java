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
package net.sourceforge.dkartaschew.halimede.ui.composite;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityElement;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;

public class CAContentProvider implements ITreeContentProvider {

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		// NOP
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return (Object[]) inputElement;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof CertificateAuthorityNode) {
			return ((CertificateAuthorityNode) parentElement).getChildren();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof CertificateAuthorityElement) {
			return ((CertificateAuthorityElement) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element instanceof CertificateAuthorityNode);
	}

}
