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

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class OpenCAAction extends Action implements Runnable {

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
	 * Create an Action for opening an existing CA
	 * 
	 * @param manager The root CA Manager.
	 */
	public OpenCAAction(CertificateAuthourityManager manager) {
		super("Open a Certificate Authority");
		setToolTipText("Open an existing Certificate Authority");
		this.manager = manager;
	}

	@Override
	public void run() {
		DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setText("Base Location for CA");
		dialog.setMessage("Select a directory");
		String path = dialog.open();
		if (path != null) {
			// Create the CA as a Job.
			Job job = Job.create("Opening Certificate Authority - " + path, monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Opening Certificate Authority - " + path, 1);
					if (logger != null) {
						logger.info("User selected to open the CA at location: " + path);
					}
					manager.open(Paths.get(path));
					subMonitor.worked(1);
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, "Opening the CA Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Opening the CA Failed",
								"Opening the CA failed with the following error: " + ExceptionUtil.getMessage(e));
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
