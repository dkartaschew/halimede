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

import java.io.IOException;
import java.security.KeyStoreException;
import java.util.List;
import java.util.logging.Level;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.ui.CertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.PassphraseDialog;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@SuppressWarnings("restriction")
public class ViewCertificateInformationAction extends Action implements SelectionListener, Runnable {

	/**
	 * The node that contains the reference to the cert
	 */
	private IssuedCertificateProperties certificate;

	/**
	 * The ID of the stack to add to.
	 */
	private String editor;
	/**
	 * Unlock password
	 */
	private String password;
	/**
	 * Flag to indicate that the part should be marked as dirty on construction...
	 */
	private boolean dirty;

	@Inject
	private Logger logger;

	@Inject
	private EPartService partService;

	@Inject
	private MApplication application;

	@Inject
	private EModelService modelService;

	@Inject
	@Optional
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	/**
	 * Create a new action.
	 * 
	 * @param cert The Certificate node
	 * @param password The password to unlock
	 * @param editor The ID of the part stack to add the part to.
	 */
	public ViewCertificateInformationAction(IssuedCertificateProperties cert, String password, String editor) {
		this(cert, password, editor, false);
	}

	/**
	 * Create a new action.
	 * 
	 * @param cert The Certificate node
	 * @param password The password to unlock
	 * @param editor The ID of the part stack to add the part to.
	 * @param dirty Mark the part as dirty.
	 */
	public ViewCertificateInformationAction(IssuedCertificateProperties cert, String password, String editor,
			boolean dirty) {
		super("View Certificate Details");
		setToolTipText("View the Certificate Details");
		this.certificate = cert;
		this.password = password;
		this.editor = editor;
		this.dirty = dirty;
	}

	@Override
	public void run() {
		if (shell == null || shell.isDisposed()) {
			shell = Display.getDefault().getActiveShell();
		}
		try {
			if (certificate == null) {
				if (logger != null) {
					logger.error("Unable to get certificate?");
				}
				MessageDialog.openError(shell, "Certificate Missing", "The Certificate information is missing");
				return;
			}
		} catch (Throwable e) {
			if (logger != null) {
				logger.error(e, "Unhandled error?");
			}
			MultiStatus s = new MultiStatus(PluginDefaults.ID, IStatus.ERROR, ExceptionUtil.getMessage(e), e);
			ErrorDialog.openError(shell, "Certificate Error",
					"An unhandled error occurred attempting to access the Certificate.", s);
			return;
		}

		if (!certificate.hasIssuedCertificate()) {
			// prompt for password.
			try {
				certificate.loadIssuedCertificate(null);
			} catch (IOException | KeyStoreException | InvalidPasswordException e) {
				// failed...
			}
			try {
				certificate.loadIssuedCertificate(password);
			} catch (IOException | KeyStoreException | InvalidPasswordException e) {
				// failed...
			}
			if (!certificate.hasIssuedCertificate()) {
				PassphraseDialog dialog = new PassphraseDialog(shell, "Certificate Passphrase",
						"Enter the passphrase to unlock the Certificate", "");
				while (dialog.open() == IDialogConstants.OK_ID) {
					try {
						certificate.loadIssuedCertificate(dialog.getValue());
						break;
					} catch (KeyStoreException | InvalidPasswordException e) {
						/*
						 * Unlock password failed. IOException/CertificateEncodingException are permanent failures.
						 */
					} catch (Throwable e) {
						if (logger != null)
							logger.error(e, ExceptionUtil.getMessage(e));
						/*
						 * Bad data?
						 */
						MessageDialog.openError(shell, "Certificate Integrity",
								"Failed to unlock the Certificate due to the following error:" + System.lineSeparator()
										+ ExceptionUtil.getMessage(e));
						return;

					}
					dialog.setErrorMessage("Bad Passphrase Supplied");
				}
				if (!certificate.hasIssuedCertificate()) {
					return;
				}
			}
		}

		List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
		if (stacks == null || stacks.isEmpty()) {
			logger.error("No Part Stacks found, unable to add view to existing Part");
			return;
		}

		String desc = certificate.getProperty(Key.description);
		if (desc == null) {
			desc = certificate.getProperty(Key.subject);
		}
		desc = Strings.trim(desc, PluginDefaults.PART_HEADER_LENGTH);

		if (this.certificate.getCertificateAuthority() != null) {
			this.certificate.getCertificateAuthority().getActivityLogger().log(Level.INFO,
					"View Certificate Details {0}", this.certificate.getProperty(Key.subject));
		}
		
		// Create a new one.
		MPart part = MBasicFactory.INSTANCE.createPart();
		part.setLabel("Certificate '" + desc + "' Details");
		part.setDescription(CertificateDetailsPart.DESCRIPTION);
		part.setContributionURI("bundleclass://" + PluginDefaults.ID + "/" + CertificateDetailsPart.class.getName());
		part.setElementId(CertificateDetailsPart.ID + "#" + System.currentTimeMillis());
		part.setCloseable(true);
		part.setToBeRendered(true);
		part.setDirty(dirty);
		part.getTags().add(CAManagerProcessor.CLOSE_TAG);

		// Add our data to the part.
		part.getTransientData().put(CertificateDetailsPart.CERTIFICATE, certificate);
		part.getTransientData().put(CertificateDetailsPart.PARTSTACK_EDITOR, editor);

		// Find the preferred part stack, otherwise just use the first one.
		MPartStack stack = stacks.stream().filter(p -> p.getElementId().equals(editor)).findFirst()
				.orElse(stacks.get(0));

		// Add our element.
		stack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
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
