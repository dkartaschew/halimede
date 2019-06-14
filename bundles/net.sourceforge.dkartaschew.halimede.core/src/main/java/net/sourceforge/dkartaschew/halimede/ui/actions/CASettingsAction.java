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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.security.cert.X509Certificate;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.CASettingsDialog;
import net.sourceforge.dkartaschew.halimede.ui.model.CASettingsModel;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;

@SuppressWarnings("restriction")
public class CASettingsAction extends Action {

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

	@Inject
	private Logger logger;

	/**
	 * Create a new action that closes the CA.
	 * 
	 * @param node The node to act on
	 */
	public CASettingsAction(CertificateAuthorityNode node) {
		super("Certificate Authority Settings");
		setToolTipText("Settings for this Certificate Authority");
		this.node = node;
	}

	@Override
	public void run() {
		CASettingsModel model = new CASettingsModel();
		try {
			node.getCertificateAuthority().getActivityLogger().log(Level.INFO, "Accessing Certificate Authority Settings");
			X509Certificate x509 = (X509Certificate) node.getCertificateAuthority().getCertificate();
			model.setSubject(x509.getSubjectDN().getName());
			model.setNodeID(node.getCertificateAuthority().getCertificateAuthorityID());
			model.setBasePath(node.getCertificateAuthority().getBasePath());
			model.setDescription(node.getCertificateAuthority().getDescription());
			model.setExpiryDays(node.getCertificateAuthority().getExpiryDays());
			model.setIncrementalSerial(node.getCertificateAuthority().isIncrementalSerial());
			model.setEnableLog(node.getCertificateAuthority().isEnableLog());

			// Get our current signature algorithm, and get the other valid one for this type.
			SignatureAlgorithm sigAl = node.getCertificateAuthority().getSignatureAlgorithm();
			model.setSignatureAlgorithm(sigAl);
			Collection<SignatureAlgorithm> algs = SignatureAlgorithm.forType(sigAl);
			model.setSignatureAlgorithms(algs.toArray(new SignatureAlgorithm[algs.size()]));
		} catch (DatastoreLockedException e) {
			if (logger != null) {
				logger.error(e, "Unable to set CA Settings");
			}
			MessageDialog.openError(shell, "Settings Failed", "Obtaining settings for Certificate Authority failed.");
		}

		Dialog dialog = new CASettingsDialog(shell, model);
		if (dialog.open() == IDialogConstants.OK_ID) {
			node.getCertificateAuthority().getActivityLogger().log(Level.INFO, "Updating Certificate Authority Settings");
			try {
				node.getCertificateAuthority().setDescription(model.getDescription());
				node.getCertificateAuthority().setExpiryDays(model.getExpiryDays());
				node.getCertificateAuthority().setSignatureAlgorithm(model.getSignatureAlgorithm());
				node.getCertificateAuthority().setIncrementalSerial(model.isIncrementalSerial());
				node.getCertificateAuthority().setEnableLog(model.isEnableLog());
			} catch (IOException | IllegalArgumentException e) {
				if (logger != null) {
					logger.error(e, "Unable to set CA Settings");
				}
				MessageDialog.openError(shell, "Settings Failed",
						"Updating settings for Certificate Authority failed.");
			}
		}
	}
}
