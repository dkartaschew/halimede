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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.ui.CertificateRequestDetailsPart;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class DeleteCertificateRequestAction extends Action implements SelectionListener {

	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	/**
	 * The template to delete.
	 */
	private final CertificateRequestProperties element;
	/**
	 * The CA which owns the template.
	 */
	private final CertificateAuthority ca;
	/**
	 * The parent part;
	 */
	private CertificateRequestDetailsPart parentPart;

	@Inject
	private Logger logger;

	/**
	 * Create a new delete CSR action
	 * 
	 * @param element The element to delete.
	 * @param ca The CA which owns the template.
	 *  @param part The parent part to close on execution of this action.
	 */
	public DeleteCertificateRequestAction(CertificateRequestProperties element, CertificateAuthority ca,
			CertificateRequestDetailsPart part) {
		super("Delete Certificate Request");
		setToolTipText("Delete this Certificate Request");
		this.element = element;
		this.ca = ca;
		this.parentPart = part;
	}

	@Override
	public void run() {
		if (ca == null) {
			MessageDialog.openError(shell, "Failed to delete the CSR",
					"Failed to delete the Certificate Request due to the following error:" + System.lineSeparator()
							+ "Missing Certificate Authority Instance");
			return;
		}
		if (MessageDialog.openConfirm(shell, "Confirm Delete",
				"Are you sure you wish to delete this Certificate Request?")) {
			try {
				if (logger != null) {
					logger.warn("User selected to delete the following Certificate Request: " + element.toString());
				}
				ca.removeCertificateSigningRequest(element);
				if (parentPart != null) {
					parentPart.close();
				}
				MessageDialog.openInformation(shell, "Certificate Request Deletion",
						"The Certificate Request has been deleted");
			} catch (Throwable e) {
				if (logger != null)
					logger.error(e, "Failed to delete Certificate Request");
				/*
				 * Bad data?
				 */
				MessageDialog.openError(shell, "Failed to delete the Certificate Request",
						"Failed to delete the Certificate Request due to the following error:" + System.lineSeparator()
								+ ExceptionUtil.getMessage(e));
				return;
			}
		}
	}
	

	@Override
	public void widgetSelected(SelectionEvent e) {
		if(e.detail == SWT.ARROW) {
			return;
		}
		run();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		if(e.detail == SWT.ARROW) {
			return;
		}
		run();
	}
}
