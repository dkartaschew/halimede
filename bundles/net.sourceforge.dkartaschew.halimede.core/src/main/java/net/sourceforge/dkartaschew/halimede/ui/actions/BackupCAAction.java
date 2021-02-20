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

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.util.BackupUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class BackupCAAction extends Action {

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
	 * Create a new action that creates a backup of the CA.
	 * 
	 * @param node The node to act on
	 */
	public BackupCAAction(CertificateAuthorityNode node) {
		super("Backup Certificate Authority");
		setToolTipText("Create a backup of this Certificate Authority");
		this.node = node;
	}

	@Override
	public void run() {
		node.getCertificateAuthority().getActivityLogger().log(Level.INFO, "Start Backup Certificate Authority");
		/*
		 * Get the filename to backup to.
		 */
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Backup Certificate Authority");
		dialog.setOverwrite(true);
		dialog.setFileName(node.getDescription() + ".zip");
		dialog.setFilterExtensions(new String[] { "*.zip", "*.*" });
		dialog.setFilterNames(new String[] { "Zip File (*.zip)", "All Files (*.*)" });
		String filename = dialog.open();
		if (filename != null) {
			final CertificateAuthority ca = node.getCertificateAuthority();
			if (logger != null) {
				logger.info("User selected to backup the CA at location: " + filename);
			}
			ca.getActivityLogger().log(Level.INFO, "Backup Certificate Authority to " + filename);

			/*
			 * Create the progress dialog and run.
			 */
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(shell);
			AtomicReference<Throwable> failure = new AtomicReference<>();
			try {
				progressDialog.run(true, true, m -> {
					try {
						BackupUtil.backup(ca, Paths.get(filename), m);
					} catch (Throwable e) {
						failure.set(e);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				failure.set(e);
			}
			/*
			 * Get the completion status, and display completion dialog.
			 */
			String status = progressDialog.getReturnCode() != Window.CANCEL ? "Complete" : "Cancelled";
			if (failure.get() == null) {
				MessageDialog.openInformation(shell, "Backup " + status, "Backup of Certificate Authority '"
						+ ca.getDescription() + "' to '" + filename + "' " + status);
			} else {
				if (logger != null) {
					logger.error(failure.get(), "Backup Failed with: " + ExceptionUtil.getMessage(failure.get()));
				}
				MessageDialog.openError(shell, "Backup Failed",
						"Backup of Certificate Authority '" + ca.getDescription() + "' to '" + filename
								+ "' failed with error: " + ExceptionUtil.getMessage(failure.get()));

			}
			ca.getActivityLogger().log(Level.INFO, "Backup Certificate Authority " + status);
		} else {
			node.getCertificateAuthority().getActivityLogger().log(Level.INFO, "Backup Certificate Authority Aborted");
		}
	}
}
