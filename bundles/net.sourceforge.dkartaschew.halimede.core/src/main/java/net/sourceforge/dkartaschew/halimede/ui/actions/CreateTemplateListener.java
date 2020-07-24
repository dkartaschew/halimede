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

import java.time.ZonedDateTime;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class CreateTemplateListener implements SelectionListener {

	private final NewCertificateModel model;

	private final boolean duplicateAction;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	protected Shell shell;

	@Inject
	private UISynchronize sync;

	/**
	 * Create a certificate template instance.
	 * 
	 * @param model The certificate model.
	 */
	public CreateTemplateListener(NewCertificateModel model) {
		this(model, false);
	}

	/**
	 * Create a certificate template instance.
	 * 
	 * @param model The certificate model.
	 * @param duplicateAction TRUE if this is a duplicate action.
	 */
	public CreateTemplateListener(NewCertificateModel model, boolean duplicateAction) {
		this.model = model;
		this.duplicateAction = duplicateAction;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e != null && e.detail == SWT.ARROW) {
			return;
		}
		String desc = !duplicateAction //
				? "Create Template - " + model.getDescription()
				: "Duplicate Template - " + model.getDescription();
		Job job = Job.create(desc, monitor -> {

			try {
				if (!duplicateAction) {
					model.getCa().getActivityLogger().log(Level.INFO, "Start Template {0}", model.getDescription());
				} else {
					model.getCa().getActivityLogger().log(Level.INFO, "Duplicate Template {0}", model.getDescription());
				}
				SubMonitor subMonitor = SubMonitor.convert(monitor, desc, 1);
				ZonedDateTime cDate = model.getCreationDate();
				model.setCreationDate(ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE));
				model.getCa().addTemplate(model.asTemplate());
				model.setCreationDate(cDate);
				subMonitor.done();

				sync.asyncExec(() -> {
					if (!duplicateAction) {
						MessageDialog.openInformation(shell, "Template Created", "The template has been created.");
					} else {
						MessageDialog.openInformation(shell, "Template Duplicated",
								"The template has been duplicated.");
					}
				});

			} catch (Throwable ex) {
				sync.asyncExec(() -> {
					if (!duplicateAction) {
						MessageDialog.openError(shell, "Creating the Template Failed",
								"Creating the Template failed with the following error: "
										+ ExceptionUtil.getMessage(ex));
					} else {
						MessageDialog.openError(shell, "Duplicating the Template Failed",
								"Duplicating the Template failed with the following error: "
										+ ExceptionUtil.getMessage(ex));
					}
				});
			}
			if (monitor != null) {
				monitor.done();
			}
			return Status.OK_STATUS;

		});

		job.schedule();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

}
