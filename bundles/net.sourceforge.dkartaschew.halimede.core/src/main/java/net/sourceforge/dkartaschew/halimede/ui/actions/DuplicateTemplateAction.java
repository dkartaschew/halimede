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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.InputDialogEx;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;

public class DuplicateTemplateAction extends Action {

	/**
	 * The template to delete.
	 */
	private ICertificateKeyPairTemplate element;
	/**
	 * The node that contains the reference to the CA
	 */
	private CertificateAuthority ca;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	protected Shell shell;

	@Inject
	private IEclipseContext context;

	/**
	 * Create a new edit template action
	 * 
	 * @param ca The Certificate Authority node
	 * @param element The current template.
	 * @param editor The ID of the part stack to add the part to.
	 */
	public DuplicateTemplateAction(CertificateAuthority ca, ICertificateKeyPairTemplate element) {
		super("Duplicate Template");
		setToolTipText("Duplicate this Template");
		this.ca = ca;
		this.element = element;
	}

	@Override
	public void run() {
		NewCertificateModel model = new NewCertificateModel(ca, element);
		model.setRepresentsTemplateOnly(true);

		InputDialogEx dialog = new InputDialogEx(shell, "Description", "Enter Description for New Template",
				model.getDescription(), null, "Duplicate", "Cancel", "Description for the New Duplicated Template.");

		if (dialog.open() == IDialogConstants.CANCEL_ID) {
			// Abort
			return;
		}
		model.setDescription(dialog.getValue());

		// Wrap this directly into the create template create listener.
		CreateTemplateListener createListener = new CreateTemplateListener(model, true);
		ContextInjectionFactory.inject(createListener, context);
		createListener.widgetSelected(null);
	}
}
