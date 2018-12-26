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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStoreException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.data.IIssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.NewCAExistingMaterialDialog;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.PassphraseDialog;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCAModel;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CreateNewCAExistingMaterialAction extends Action implements Runnable {

	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	/**
	 * CA Manager
	 */
	private final CertificateAuthourityManager manager;

	@Inject
	private Logger logger;
	
	@Inject 
	private UISynchronize sync;
	
	/**
	 * Create an Action for creating a new CA
	 * 
	 * @param manager The root CA Manager.
	 */
	public CreateNewCAExistingMaterialAction(CertificateAuthourityManager manager) {
		super("Create a New Certificate Authority from Existing Certificate");
		setToolTipText("Create a new Certificate Authority from Existing Certificate");
		this.manager = manager;
	}

	@Override
	public void run() {
		NewCAModel model = new NewCAModel();
		model.setPkcs12(true);

		NewCAExistingMaterialDialog dialog = new NewCAExistingMaterialDialog(shell, model);
		if (dialog.open() == IDialogConstants.OK_ID) {

			// Attempt to open the material in main thread, so we can prompt for passwords...
			IIssuedCertificate icTemp = null;
			String password = null;
			try {
				if (model.isPkcs12()) {
					icTemp = IssuedCertificate.openPKCS12(Paths.get(model.getPkcs12Filename()), password);
				} else {
					icTemp = IssuedCertificate.openPKCS7_8(Paths.get(model.getCertFilename()), Paths.get(model.getPrivateKeyFilename()),
							password);
				}
			} catch (Throwable e) {
				// ignore...
			}

			if (icTemp == null) {
				// prompt for password
				PassphraseDialog passwordDialog = new PassphraseDialog(shell, "Keying Material Passphrase",
						"Enter the passphrase to unlock the Keying Material", "");
				while (passwordDialog.open() == IDialogConstants.OK_ID) {
					password = passwordDialog.getValue();
					try {
						if (model.isPkcs12()) {
							icTemp = IssuedCertificate.openPKCS12(Paths.get(model.getPkcs12Filename()), password);
						} else {
							icTemp = IssuedCertificate.openPKCS7_8(Paths.get(model.getCertFilename()),
									Paths.get(model.getPrivateKeyFilename()), password);
						}
						// It worked...
						break;
					} catch (KeyStoreException | InvalidPasswordException e) {
						/*
						 * Unlock password failed. IOException/CertificateEncodingException are permanent failures.
						 */
					} catch (Throwable e) {
						if (logger != null)
							logger.error(e, ExceptionUtil.getMessage(e));
						/*
						 * Bad data?
						 */
						MessageDialog.openError(shell, "Certificate/Keying Material Integrity",
								"Failed to unlock the Certificate/Keying Material due to the following error:"
										+ System.lineSeparator() + ExceptionUtil.getMessage(e));
						return;
					}
					passwordDialog.setErrorMessage("Bad Passphrase Supplied");
				}
			}

			if (icTemp == null) {
				MessageDialog.openError(shell, "Creation Cancelled",
						"Creation of Certificate Authority has been cancelled");
				return;
			}
			// Create a new IC instance with the password as stored in the dialog.
			IIssuedCertificate ic = new IssuedCertificate(new KeyPair(icTemp.getPublicKey(), icTemp.getPrivateKey()),
					icTemp.getCertificateChain(), null, null, model.getPassword());

			// Create the CA as a Job.
			Job job = Job.create("Create Certificate Authority - " + model.getcADescription(), monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Create Certificate Authority - " + model.getcADescription(), 3);
					Path path = Paths.get(model.getBaseLocation(), model.getcADescription());
					Files.createDirectories(path);
					subMonitor.worked(1);
					manager.create(path, ic, model.getcADescription());
					subMonitor.worked(1);
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, "Creating the CA Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Creating the CA Failed",
								"Creating the CA failed with the following error: " + ExceptionUtil.getMessage(e));
					});
				}
				if (monitor != null) {
					monitor.done();
				}
				return Status.OK_STATUS;

			});

			job.schedule();
		}
	}
}
