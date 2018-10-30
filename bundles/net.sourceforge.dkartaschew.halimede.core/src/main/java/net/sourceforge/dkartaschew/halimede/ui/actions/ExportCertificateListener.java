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

import java.nio.file.Paths;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.ExportInformationDialog;
import net.sourceforge.dkartaschew.halimede.ui.model.ExportInformationModel;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

/**
 * A event listener to export the primary Certificate Chain from CA
 */
@SuppressWarnings("restriction")
public class ExportCertificateListener implements SelectionListener {

	/**
	 * The cert to extract the certificate from
	 */
	private final IssuedCertificateProperties certificate;
	/**
	 * Flag to include the entire chain or just the certificate for the CA itself.
	 */
	private final boolean includeFullChain;
	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@Inject
	private Logger logger;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * Create a new export certificate information listener.
	 * 
	 * @param certificate The certificate model to export it's certificates from
	 * @param includeFullChain TRUE to include the full chain.
	 */
	public ExportCertificateListener(IssuedCertificateProperties certificate, boolean includeFullChain) {
		this.certificate = certificate;
		this.includeFullChain = includeFullChain;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.detail == SWT.ARROW) {
			return;
		}
		ExportInformationModel model = new ExportInformationModel();
		model.setDialogTitle("Export Certificate");
		model.setSaveDialogTitle("File to save Certificate Information");
		model.setSaveDialogFilterExtensions(new String[] { "*.cer", "*.*" });
		model.setSaveDialogFilterNames(new String[] { "Certificate (*.cer)", "All Files (*.*)" });
		
		ExportInformationDialog dialog = new ExportInformationDialog(shell, model);
		if (dialog.open() == IDialogConstants.OK_ID) {
			String certdesc = certificate.getProperty(Key.description);
			if (certdesc == null) {
				certdesc = certificate.getProperty(Key.subject);
			}
			String certDescription = certdesc;
			String desc = "Exporting Issued Certificates from '" + certDescription + "'";
			Job job = Job.create(desc, monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, desc,  1);
					if (logger != null) {
						logger.info("Exporting to: " + model.getFilename());
					}
					if (includeFullChain) {
						certificate.loadIssuedCertificate(null).createCertificateChain(Paths.get(model.getFilename()),
								model.getEncoding());
					} else {
						certificate.loadIssuedCertificate(null).createCertificate(Paths.get(model.getFilename()),
								model.getEncoding());
					}
					subMonitor.worked(1);
					sync.asyncExec(() -> {
						MessageDialog.openInformation(shell, "Certificate(s) Exported",
								"The Issued Certificate '" + certDescription + "' Certificates have been exported to '"
										+ model.getFilename() + "'.");
					});
				} catch (Throwable ex) {
					if (logger != null)
						logger.error(ex, "Exporting the Certificate(s) Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Exporting Certificate(s) Failed",
								"Exporting Certificate(s) from Issued Certificate '" + certDescription
										+ "' failed with the following error: " + ExceptionUtil.getMessage(ex));
					});
				}
				if(monitor != null) {
					monitor.done();
				}
				return Status.OK_STATUS;
			});
			job.schedule();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

}
