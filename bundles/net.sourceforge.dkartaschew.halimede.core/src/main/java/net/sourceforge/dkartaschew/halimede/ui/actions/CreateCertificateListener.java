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

import javax.inject.Inject;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

/**
 * Action to create a new Certificate.
 */
public class CreateCertificateListener implements SelectionListener {

	/**
	 * The model...
	 */
	private final NewCertificateModel model;
	/**
	 * The part to close if successful
	 */
	private final NewCertificateDetailsPart part;
	/**
	 * Binding context...
	 */
	private IObservableValue<IStatus> validationStatus;
	
	
	@Inject 
	private UISynchronize sync;

	public CreateCertificateListener(NewCertificateModel model, NewCertificateDetailsPart part) {
		this.model = model;
		this.part = part;
	}

	@SuppressWarnings("unchecked")
	public void setBindingContext(DataBindingContext context) {
		// Create a list of all validators made available via bindings and global validators.
		IObservableList list = new WritableList<>(context.getValidationRealm());
		list.addAll(context.getBindings());
		list.addAll(context.getValidationStatusProviders());
		validationStatus = new AggregateValidationStatus(context.getValidationRealm(), list,
				AggregateValidationStatus.MAX_SEVERITY);
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if(e.detail == SWT.ARROW) {
			return;
		}
		IStatus s = validationStatus.getValue();
		if (s.getSeverity() != IStatus.ERROR) {
			// attempt...
			// Create the CA as a Job.
			Job job = Job.create("Create Certificate - " + model.getDescription(), monitor -> {

				try {
					part.setClosable(false);
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Create Certificate - " + model.getDescription(), 2);
					ICertificateRequest r = model.asCertificateRequest();
					// This will generate the keying material.
					r.getSubjectPublicKeyInfo();
					subMonitor.worked(1);
					// Now sign and store.
					model.getCa().signAndStoreCertificateRequest(//
							r, //
							model.getStartDate(), //
							model.getExpiryDate(), //
							model.isUseCAPassword() ? model.getCa().getPassword() : model.getPassword());
					subMonitor.worked(1);
					
					// Close the part.
					part.close();

					// remove the cert request.
					if (model.isCertificateRequest()) {
						model.getCa().removeCertificateSigningRequest(model.getCsr());
					}
				} catch (Throwable ex) {
					part.setClosable(true);
					sync.asyncExec(() -> {
						MessageDialog.openError(e.display.getActiveShell(), "Creating the Certificate Failed",
								"Creating the Certificate failed with the following error: " + ExceptionUtil.getMessage(ex));
					});
				}
				if(monitor != null) {
					monitor.done();
				}
				return Status.OK_STATUS;

			});

			job.schedule();

		} else {
			MessageDialog.openError(e.display.getActiveShell(), "Missing details", 
					"Please check certificate request information: " + s.getMessage());
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

}
