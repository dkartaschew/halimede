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

package net.sourceforge.dkartaschew.halimede.command;

import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.IIssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCertificateInformationAction;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.PassphraseDialog;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class ShowPKCS12 {

	@Inject
	private Logger logger;
	@Inject
	private IEclipseContext context;
	@Inject 
	private UISynchronize sync;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.handler.show.pkcs12";

	public static final String COMMAND_PARAM = "net.sourceforge.dkartaschew.halimede.handler.show.commandparameter.editorid";

	@Execute
	public void execute(ParameterizedCommand command, Shell shell) {

		// See if we have a command parameter;
		String editor = PluginDefaults.EDITOR;
		if (command != null && command.getParameterMap() != null) {
			Object ed = command.getParameterMap().get(COMMAND_PARAM);
			if (ed != null && ed instanceof String) {
				String edi = (String) ed;
				if (!edi.isEmpty()) {
					editor = edi;
				}
			}
		}

		// Get the certificate filename...
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setText("Open Certificate File");
		dlg.setOverwrite(true);
		dlg.setFilterExtensions(new String[] { "*.p12", "*.*" });
		dlg.setFilterNames(new String[] { "PKCS#12 (*.p12)", "All Files (*.*)" });
		String filename = dlg.open();
		if (filename == null) {
			return;
		}

		try {
			// Attempt to open the material in main thread, so we can prompt for passwords...
			IIssuedCertificate icTemp = null;
			String password = null;
			try {
				icTemp = IssuedCertificate.openPKCS12(Paths.get(filename), password);
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
						icTemp = IssuedCertificate.openPKCS12(Paths.get(filename), password);
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

			IssuedCertificateProperties p = new IssuedCertificateProperties(null, icTemp);
			// Set at least the subject...
			X509Certificate c = (X509Certificate) icTemp.getCertificateChain()[0];
			p.setProperty(Key.subject, c.getSubjectX500Principal().toString());

			ViewCertificateInformationAction action = new ViewCertificateInformationAction(p, null, editor);
			ContextInjectionFactory.inject(action, context);
			sync.asyncExec(action);

		} catch (Throwable e) {
			logger.error(e, ExceptionUtil.getMessage(e));
			sync.asyncExec(() -> {
				MessageDialog.openError(shell, "Opening the Certificate Failed",
						"Opening the Certificate failed with the following error: " + ExceptionUtil.getMessage(e));
			});
		}
	}
}
