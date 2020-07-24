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

import net.sourceforge.dkartaschew.halimede.ui.TemplateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class EditTemplateListener implements SelectionListener {

	private final NewCertificateModel model;
	/**
	 * The part to close if successful
	 */
	private final TemplateDetailsPart part;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	protected Shell shell;

	@Inject
	private UISynchronize sync;

	/**
	 * Edit a certificate template instance.
	 * 
	 * @param model The certificate model.
	 * @param part The part to close.
	 */
	public EditTemplateListener(NewCertificateModel model, TemplateDetailsPart part) {
		this.model = model;
		this.part = part;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e != null && e.detail == SWT.ARROW) {
			return;
		}
		Job job = Job.create("Update Template - " + model.getDescription(), monitor -> {

			try {
				model.getCa().getActivityLogger().log(Level.INFO, "Update Template {0}", model.getDescription());
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Update Template - " + model.getDescription(), 1);
				model.getCa().addTemplate(model.asTemplate());
				subMonitor.done();

				sync.asyncExec(() -> {
					MessageDialog.openInformation(shell, "Template Updated", "The template has been updated.");
				});
				// Close the part.
				part.close();

			} catch (Throwable ex) {
				sync.asyncExec(() -> {
					MessageDialog.openError(shell, "Creating the Template Failed",
							"Creating the Template failed with the following error: " + ExceptionUtil.getMessage(ex));
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
