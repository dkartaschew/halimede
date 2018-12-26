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
package net.sourceforge.dkartaschew.halimede.ui.composite;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.swt.graphics.Image;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.ui.node.ICertificateTreeNode;

/**
 * Basic Label provider for the CA List Pane.
 */
public class CALabelProvider extends LabelProvider implements IStyledLabelProvider {

	@Override
	public String getText(Object element) {
		return ((ICertificateTreeNode) element).getDescription();
	}

	@Override
	public Image getImage(Object element) {
		return PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(((ICertificateTreeNode) element).getImage()));
	}

	@Override
	public StyledString getStyledText(Object element) {
		return new StyledString(getText(element));
	}

}
