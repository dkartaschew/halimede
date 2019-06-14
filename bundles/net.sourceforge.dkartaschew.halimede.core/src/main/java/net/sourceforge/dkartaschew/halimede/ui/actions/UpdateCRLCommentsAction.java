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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties.Key;
import net.sourceforge.dkartaschew.halimede.ui.composite.CADetailPane;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.UpdateCommentDialog;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class UpdateCRLCommentsAction extends Action {

	private final CRLProperties crl;

	/**
	 * CA Details pane
	 */
	private final CADetailPane caDetailsPane;
	/**
	 * CA
	 */
	private final CertificateAuthority ca;
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
	 * Create a new Comment update action
	 * 
	 * @param crl The crl to update
	 * @param ca The holding CA
	 * @param caDetailsPane The pane to request a refresh.
	 */
	public UpdateCRLCommentsAction(CRLProperties crl, CertificateAuthority ca, CADetailPane caDetailsPane) {
		super("Update Comment");
		setToolTipText("Update the Comment for this CRL");
		this.crl = crl;
		this.ca = ca;
		this.caDetailsPane = caDetailsPane;
	}

	@Override
	public void run() {
		ca.getActivityLogger().log(Level.INFO, "CRL Comment {0}", crl.getProperty(Key.crlSerialNumber));
		UpdateCommentDialog dialog = new UpdateCommentDialog(shell, "Comment", "CRL Comments:",
				crl.getProperty(Key.comments));
		if (dialog.open() == IDialogConstants.OK_ID) {

			String desc = crl.getProperty(Key.issueDate);
			Job job = Job.create("Update Comments - " + desc, monitor -> {

				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Update Comments - " + desc, 1);
					crl.setProperty(Key.comments, dialog.getValue());
					ca.getActivityLogger().log(Level.INFO, "Updated CRL Comment {0}", crl.getProperty(Key.crlSerialNumber));
					// Get the CA to update the backing store.
					ca.updateCRLProperties(crl);
					// And get the Pane to do a refresh. (simple property updates won't propagated through).
					caDetailsPane.refresh();
					subMonitor.done();

					sync.asyncExec(() -> {
						MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Comment Updated",
								"The comments for this CRL has been updated.");
					});

				} catch (Throwable ex) {
					logger.error(ex, ExceptionUtil.getMessage(ex));
					sync.asyncExec(() -> {
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Updating the CRL Failed",
								"Updating the CRL failed with the following error: " + ExceptionUtil.getMessage(ex));
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
