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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;

/**
 * Node decoration provider to add a Lock icon for locked CAs.
 */
public class INodeDecorationProvider implements ILabelDecorator {

	/**
	 * Lock Icon
	 */
	private final static ImageDescriptor LOCK_OVERLAY = PluginDefaults
			.createImageDescriptor(PluginDefaults.IMG_LOCK_OVERLAY);

	/**
	 * Cached copy of the lock overlayed image.
	 */
	private Image lockOverlay;

	/**
	 * Lock location.
	 */
	private final static int LOCK_LOCATION = IDecoration.TOP_LEFT;

	@Override
	public Image decorateImage(Image image, Object element) {
		if (element instanceof CertificateAuthorityNode) {
			CertificateAuthorityNode node = (CertificateAuthorityNode) element;
			if (node.getCertificateAuthority().isLocked()) {
				if (lockOverlay == null) {
					lockOverlay = new DecorationOverlayIcon(image, LOCK_OVERLAY, LOCK_LOCATION).createImage();
				}
				return lockOverlay;
			}
		}
		return null;
	}

	@Override
	public String decorateText(String text, Object element) {
		return null;
	}

	@Override
	public void dispose() {
		if (lockOverlay != null) {
			lockOverlay.dispose();
		}
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// NOP
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// NOP
	}

}
