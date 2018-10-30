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

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class CreateTemplateListener implements SelectionListener {

	private final NewCertificateModel model;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * Create a certificate template instance.
	 * 
	 * @param model The certificate model.
	 */
	public CreateTemplateListener(NewCertificateModel model) {
		this.model = model;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.detail == SWT.ARROW) {
			return;
		}
		Job job = Job.create("Create Template - " + model.getDescription(), monitor -> {

			try {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Create Template - " + model.getDescription(), 1);
				ZonedDateTime cDate = model.getCreationDate();
				model.setCreationDate(ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE));
				model.getCa().addTemplate(model.asTemplate());
				model.setCreationDate(cDate);
				subMonitor.done();

				sync.asyncExec(() -> {
					MessageDialog.openInformation(e.display.getActiveShell(), "Template Created",
							"The template has been created.");
				});

			} catch (Throwable ex) {
				sync.asyncExec(() -> {
					MessageDialog.openError(e.display.getActiveShell(), "Creating the Template Failed",
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
