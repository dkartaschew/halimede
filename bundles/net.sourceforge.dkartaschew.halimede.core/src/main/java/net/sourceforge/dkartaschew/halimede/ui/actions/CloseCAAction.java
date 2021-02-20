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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import java.util.logging.Level;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.ui.util.Dialogs;

@SuppressWarnings("restriction")
public class CloseCAAction extends Action {

	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	/**
	 * CA Manager.
	 */
	private final CertificateAuthourityManager manager;
	/**
	 * CA Node
	 */
	private final CertificateAuthorityNode node;

	@Inject
	private Logger logger;

	/**
	 * Create a new action that closes the CA.
	 * 
	 * @param manager The CA Manager which owns the CA.
	 * @param node The node to act on
	 */
	public CloseCAAction(CertificateAuthourityManager manager, CertificateAuthorityNode node) {
		super("Close Certificate Authority");
		setToolTipText("Close this Certificate Authority");
		this.manager = manager;
		this.node = node;
	}

	@Override
	public void run() {
		if (Dialogs.openConfirm(shell, "Close Certificate Authority",
				"Are you sure you wish to close this Certificate Authority?", "Close", "Cancel")) {
			if (logger != null) {
				logger.info("User selected to close the CA at location: "
						+ node.getCertificateAuthority().getBasePath().toString());
			}
			node.getCertificateAuthority().getActivityLogger().log(Level.INFO, "Closing Certificate Authority");
			manager.remove(node.getCertificateAuthority());
		}
	}
}
