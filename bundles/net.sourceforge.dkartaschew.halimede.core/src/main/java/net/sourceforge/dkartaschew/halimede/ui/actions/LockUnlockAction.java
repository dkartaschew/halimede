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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import java.security.KeyStoreException;
import java.security.cert.CertificateEncodingException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.PassphraseDialog;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class LockUnlockAction extends Action {

	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	/**
	 * CA Node
	 */
	private final CertificateAuthorityNode node;
	/**
	 * Tree View.
	 */
	private final TreeViewer view;

	@Inject
	private Logger logger;

	/**
	 * Create an action to lock/unlock the CA
	 * 
	 * @param view The attached TreeViewer to refresh on
	 * @param node The node to act on
	 */
	public LockUnlockAction(TreeViewer view, CertificateAuthorityNode node) {
		super();
		if (node.getCertificateAuthority().isLocked()) {
			setText("Unlock the Certificate Authority");
		} else {
			setText("Lock the Certificate Authority");
		}
		setToolTipText("Open an existing Certificate Authority");
		this.node = node;
		this.view = view;
	}

	@Override
	public void run() {
		if (node.getCertificateAuthority().isLocked()) {
			/*
			 * Attempt to unlock without password.
			 */
			try {
				node.getCertificateAuthority().unlock(null);
				view.refresh();
				return;
			} catch (Throwable e) {
				/*
				 * Unlock without password failed.
				 */
			}
			PassphraseDialog dialog = new PassphraseDialog(shell, "Certificate Authority Passphrase",
					"Enter the passphrase to unlock the Certificate Authority", "");
			while (dialog.open() == IDialogConstants.OK_ID) {
				try {
					node.getCertificateAuthority().unlock(dialog.getValue());
					view.refresh();
					return;
				} catch (KeyStoreException | InvalidPasswordException | CertificateEncodingException e) {
					/*
					 * Unlock password failed. IOException/CertificateEncodingException are permanent failures.
					 */
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, ExceptionUtil.getMessage(e));
					/*
					 * Bad data?
					 */
					MessageDialog.openError(shell, "Certificate Authority Integrity",
							"Failed to unlock the Certificate Authority due to the following error:"
									+ System.lineSeparator() + ExceptionUtil.getMessage(e));
					return;

				}
				dialog.setErrorMessage("Bad Passphrase Supplied");
			}
		} else {
			node.getCertificateAuthority().lock();
			view.refresh();
		}
	}

}
