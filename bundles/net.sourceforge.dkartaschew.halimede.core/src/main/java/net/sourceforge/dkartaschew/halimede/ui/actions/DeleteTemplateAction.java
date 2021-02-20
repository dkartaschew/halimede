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

import java.util.logging.Level;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.ui.util.Dialogs;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class DeleteTemplateAction extends Action {

	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	/**
	 * The template to delete.
	 */
	private final ICertificateKeyPairTemplate element;
	/**
	 * The CA which owns the template.
	 */
	private final CertificateAuthority ca;

	@Inject
	private Logger logger;

	/**
	 * Create a new delete template action
	 * 
	 * @param element The element to delete.
	 * @param ca The CA which owns the template.
	 */
	public DeleteTemplateAction(ICertificateKeyPairTemplate element, CertificateAuthority ca) {
		super("Delete Template");
		setToolTipText("Delete this Template");
		this.element = element;
		this.ca = ca;
	}

	@Override
	public void run() {
		if (ca == null) {
			MessageDialog.openError(shell, "Failed to delete the template",
					"Failed to delete the Certificate Template due to the following error:" + System.lineSeparator()
							+ "Missing Certificate Authority Instance");
			return;
		}
		ca.getActivityLogger().log(Level.INFO, "Start Delete Template {0}", element);
		if (Dialogs.openConfirm(shell, "Confirm Delete", "Are you sure you wish to delete this template?", "Delete", "Cancel")) {
			try {
				if (logger != null) {
					logger.warn("User selected to delete the following template: " + element.toString());
				}
				ca.removeCertificateTemplate(element);
				MessageDialog.openInformation(shell, "Template Deletion", "The template has been deleted");
			} catch (Throwable e) {
				if (logger != null)
					logger.error(e, "Failed to delete template");
				/*
				 * Bad data?
				 */
				MessageDialog.openError(shell, "Failed to delete the template",
						"Failed to delete the Certificate Template due to the following error:" + System.lineSeparator()
								+ ExceptionUtil.getMessage(e));
				return;
			}
		} else {
			ca.getActivityLogger().log(Level.INFO, "Cancel delete Template {0}", element);
		}
	}
}
