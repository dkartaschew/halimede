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

import java.nio.file.Paths;
import java.util.logging.Level;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties.Key;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class ImportCSRAction extends Action {

	private final CertificateAuthority ca;

	@Inject
	private Logger logger;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	
	@Inject 
	private UISynchronize sync;

	public ImportCSRAction(CertificateAuthority ca) {
		super("Import a Certificate Request");
		this.ca = ca;
		setToolTipText("Import a Certificate to be signed by this authority");
	}

	@Override
	public void run() {
		ca.getActivityLogger().log(Level.INFO, "Import CSR into CA");
		FileDialog dialog = new FileDialog(shell);
		dialog.setText("Import CSR");
		dialog.setFilterExtensions(new String[] { "*.csr", "*.*" });
		dialog.setFilterNames(new String[] { "Certificate Signing Request (*.csr)", "All Files (*.*)" });
		String path = dialog.open();
		if (path != null) {
			// Create the CA as a Job.
			Job job = Job.create("Importing Certificate Signing Request - " + path, monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Importing Certificate Signing Request - " + path, 1);
					if (logger != null) {
						logger.info("User selected to import CSR at location: " + path);
					}
					ca.getActivityLogger().log(Level.INFO, "Import CSR {0}", path);
					CertificateRequestProperties csr = ca.addCertificateSigningRequest(Paths.get(path));
					subMonitor.worked(1);
					sync.asyncExec(() -> {
						MessageDialog.openInformation(shell, "Importing Certificate Signing Request Completed",
								"Importing Certificate Signing Request Completed Successfully." + System.lineSeparator()
										+ "Subject: " + csr.getProperty(Key.subject));
					});
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, "Importing Certificate Signing Request Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Importing Certificate Signing Request Failed",
								"Importing Certificate Signing Request failed with the following error: "
										+ ExceptionUtil.getMessage(e));
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
