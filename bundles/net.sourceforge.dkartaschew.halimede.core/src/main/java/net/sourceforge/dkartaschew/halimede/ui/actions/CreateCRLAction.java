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

import java.time.ZonedDateTime;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.NewCRLDialog;
import net.sourceforge.dkartaschew.halimede.ui.model.CACRLModel;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CreateCRLAction extends Action {

	/**
	 * The node that contains the reference to the CA
	 */
	private CertificateAuthority ca;

	/**
	 * The ID of the stack to add to.
	 */
	private String editor;

	@Inject
	private Logger logger;

	@Inject
	private IEclipseContext context;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	/**
	 * Create a new CRL Action
	 * 
	 * @param ca The Certificate Authority
	 * @param editor The editor to add the new CRL to.
	 */
	public CreateCRLAction(CertificateAuthority ca, String editor) {
		super("Create CRL");
		this.ca = ca;
		this.editor = editor;
		setToolTipText("Create a Certificate Revokation List (CRL) for this authority");
	}

	@Override
	public void run() {
		CACRLModel model = new CACRLModel(ca.getDescription(), ca.peekNextSerialCRLNumber(),
				ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE));
		model.setNextDate(ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE).plusDays(ca.getExpiryDays()));
		NewCRLDialog dialog = new NewCRLDialog(shell, model);
		if (dialog.open() == IDialogConstants.OK_ID) {

			Job job = Job.create("Create CRL - " + model.getSerial().toString(), monitor -> {

				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Create CRL - " + model.getSerial().toString(), 2);
					// Create the CRL
					CRLProperties crl = ca.createCRL(model.getNextDate());
					subMonitor.worked(1);

					// And view the result.
					ViewCRLAction act = new ViewCRLAction(crl, editor);
					ContextInjectionFactory.inject(act, context);
					act.run();
					subMonitor.worked(1);
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, "Creating the CRL Failed");
					Display.getDefault().asyncExec(() -> {
						MessageDialog.openError(shell, "Creating the CRL Failed",
								"Creating the CRL failed with the following error: " + ExceptionUtil.getMessage(e));
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
}
