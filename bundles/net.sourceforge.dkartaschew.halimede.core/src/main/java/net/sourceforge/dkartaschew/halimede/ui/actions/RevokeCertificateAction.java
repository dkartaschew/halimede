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

import java.time.ZonedDateTime;
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
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.ui.CertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.QuestionDialogWithOptions;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class RevokeCertificateAction extends Action implements SelectionListener {

	private final IssuedCertificateProperties certificate;
	/**
	 * CA
	 */
	private final CertificateAuthority ca;
	/**
	 * Part to close if not-null;
	 */
	private final CertificateDetailsPart part;
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

	public RevokeCertificateAction(IssuedCertificateProperties certificate, CertificateAuthority ca) {
		super("Revoke Certificate");
		setToolTipText("Revoke this Certificate");
		this.certificate = certificate;
		this.ca = ca;
		this.part = null;
	}

	public RevokeCertificateAction(IssuedCertificateProperties certificate, CertificateAuthority ca,
			CertificateDetailsPart part) {
		super("Revoke Certificate");
		setToolTipText("Revoke this Certificate");
		this.certificate = certificate;
		this.ca = ca;
		this.part = part;
	}

	@Override
	public void run() {
		ca.getActivityLogger().log(Level.INFO, "Revoke Certificate");
		QuestionDialogWithOptions dialog = new QuestionDialogWithOptions(shell, //
				"Confirm Revoke", //
				"Are you sure you wish to revoke this certificate?" + System.lineSeparator() //
						+ System.lineSeparator() //
						+ "Reason:",
						"Revoke", //
						"Cancel", //
				RevokeReasonCode.getValidList());
		if (dialog.open() == IDialogConstants.OK_ID) {
			String desc = certificate.getProperty(Key.description);
			if (desc == null) {
				desc = certificate.getProperty(Key.subject);
			}
			String d = "Revoke Certificate - " + desc;
			ca.getActivityLogger().log(Level.INFO, "Revoke Certificate {0}", desc);
			Job job = Job.create(d, monitor -> {

				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, d, 1);
					// Get the CA to update the backing store.
					ca.updateIssuedCertificateProperties(certificate);
					RevokeReasonCode code = (RevokeReasonCode) dialog.getSelectedElement();
					ca.revokeCertificate(certificate, ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE), code);
					subMonitor.worked(1);
					if (part != null) {
						part.close();
					}
					sync.asyncExec(() -> {
						MessageDialog.openInformation(shell, "Certificate Revoked",
								"The certificate has been revoked.");
					});

				} catch (Throwable ex) {
					logger.error(ex, ExceptionUtil.getMessage(ex));
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Revoking the Certificate Failed",
								"Revoking the Certificte failed with the following error: " + ExceptionUtil.getMessage(ex));
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

	@Override
	public void widgetSelected(SelectionEvent e) {
		run();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		run();
	}
}
