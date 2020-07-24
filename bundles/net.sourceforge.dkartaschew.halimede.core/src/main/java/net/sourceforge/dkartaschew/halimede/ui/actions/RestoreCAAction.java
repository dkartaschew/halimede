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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.RestoreCADialog;
import net.sourceforge.dkartaschew.halimede.ui.model.RestoreCAModel;
import net.sourceforge.dkartaschew.halimede.util.BackupUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class RestoreCAAction extends Action implements Runnable {

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

	/**
	 * Create a new action that restores a CA.
	 * 
	 * @param manager The CA Manager
	 */
	public RestoreCAAction(CertificateAuthourityManager manager) {
		super("Restore Certificate Authority");
		setToolTipText("Restore Certificate Authority from Backup File.");
		this.manager = manager;
	}

	@Override
	public void run() {
		if (logger != null) {
			logger.info("Start restore of CA");
		}
		/*
		 * Get the restoration details
		 */
		RestoreCAModel model = new RestoreCAModel();
		model.setAddToManager(true);
		RestoreCADialog dialog = new RestoreCADialog(shell, model);
		if (dialog.open() == IDialogConstants.OK_ID) {
			if (logger != null) {
				logger.info("User selected to restore '" + model.getFilename() + "' to location: "
						+ model.getBaseLocation());
			}

			/*
			 * Create the progress dialog and run.
			 */
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(shell);
			AtomicReference<Throwable> failure = new AtomicReference<>();
			try {
				progressDialog.run(true, true, m -> {
					try {
						Path location = BackupUtil.restore(Paths.get(model.getFilename()),
								Paths.get(model.getBaseLocation()), m);
						if (location != null && model.isAddToManager()) {
							m.subTask("");
							progressDialog.setCancelable(false);
							SubMonitor subMonitor = SubMonitor.convert(m, //
									"Opening Certificate Authority - " + location, 1);
							manager.open(location);
							subMonitor.done();
						}
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
				MessageDialog.openInformation(shell, "Restore " + status, "Restore of Certificate Authority backup '"
						+ model.getFilename() + "' to '" + model.getBaseLocation() + "' " + status);
			} else {
				if (logger != null) {
					logger.error(failure.get(), "Restore Failed with: " + ExceptionUtil.getMessage(failure.get()));
				}
				MessageDialog.openError(shell, "Backup Failed",
						"Restore of Certificate Authority '" + model.getFilename() + "' to '" + model.getBaseLocation()
								+ "' failed with error: " + ExceptionUtil.getMessage(failure.get()));

			}
		}
	}
}
