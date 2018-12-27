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

import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;
import net.sourceforge.dkartaschew.halimede.ui.CertificateRequestDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class ViewCertificateRequestInformationAction extends Action implements SelectionListener, Runnable {

	/**
	 * The node that contains the reference to the model
	 */
	private CertificateRequestProperties model;
	/**
	 * The node that contains the reference to the model
	 */
	private IssuedCertificateProperties model2;

	/**
	 * The ID of the stack to add to.
	 */
	private String editor;

	@Inject
	private Logger logger;

	@Inject
	private EPartService partService;

	@Inject
	private MApplication application;

	@Inject
	private EModelService modelService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	/**
	 * Create a new action.
	 * 
	 * @param model The Certificate Request node
	 * @param password The password to unlock
	 * @param editor The ID of the part stack to add the part to.
	 */
	public ViewCertificateRequestInformationAction(CertificateRequestProperties model, String editor) {
		super("View Certificate Details");
		setToolTipText("View the Certificate Details");
		this.model = model;
		this.editor = editor;
	}
	
	/**
	 * Create a new action.
	 * 
	 * @param model The Certificate Request node
	 * @param password The password to unlock
	 * @param editor The ID of the part stack to add the part to.
	 */
	public ViewCertificateRequestInformationAction(IssuedCertificateProperties model, String editor) {
		super("View Original Certificate Request Details");
		setToolTipText("View the Original Certificate Request Details");
		this.model2 = model;
		this.editor = editor;
	}

	@Override
	public void run() {
		try {
			if (model == null && model2 == null) {
				if (logger != null) {
					logger.error("Unable to get certificate request?");
				}
				MessageDialog.openError(shell, "Certificate Request Missing", "The Certificate Request information is missing");
				return;
			}
		} catch (Throwable e) {
			if (logger != null) {
				logger.error(e, "Unhandled error?");
			}
			MultiStatus s = new MultiStatus(PluginDefaults.ID, IStatus.ERROR, ExceptionUtil.getMessage(e), e);
			ErrorDialog.openError(shell, "Certificate Request Error",
					"An unhandled error occurred attempting to access the Certificate Request.", s);
			return;
		}

		// Convert model2 into model
		if (model2 != null && model == null) {
			String filename = model2.getProperty(IssuedCertificateProperties.Key.csrStore);
			if (filename == null) {
				if (logger != null) {
					logger.error("Unable to get certificate request?");
				}
				MessageDialog.openError(shell, "Certificate Request Missing",
						"The Certificate Request information is missing");
				return;
			}
			try {
				Path p = model2.getCertificateAuthority().getBasePath().resolve(CertificateAuthority.ISSUED_PATH)
						.resolve(filename);
				ICertificateRequest csr = CertificateRequestPKCS10.create(p);
				model = new CertificateRequestProperties(null, csr);
			} catch (Throwable e) {
				if (logger != null) {
					logger.error("Unable to get certificate request?");
				}
				MessageDialog.openError(shell, "Certificate Request Missing",
						"The Certificate Request information is missing");

			}
		}

		List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
		if (stacks == null || stacks.isEmpty()) {
			logger.error("No Part Stacks found, unable to add view to existing Part");
			return;
		}

		// Create a new one.
		MPart part = MBasicFactory.INSTANCE.createPart();
		part.setLabel("Certificate Request Details");
		part.setDescription(CertificateRequestDetailsPart.DESCRIPTION);
		part.setContributionURI("bundleclass://" + PluginDefaults.ID + "/" + CertificateRequestDetailsPart.class.getName());
		part.setElementId(CertificateRequestDetailsPart.ID + "#" + System.currentTimeMillis());
		part.setCloseable(true);
		part.setToBeRendered(true);
		part.getTags().add(CAManagerProcessor.CLOSE_TAG);
		
		// Add our data to the part.
		part.getTransientData().put(CertificateRequestDetailsPart.MODEL, model);
		part.getTransientData().put(CertificateRequestDetailsPart.EDITOR, editor);
		part.getTransientData().put(CertificateRequestDetailsPart.CA, model.getCertificateAuthority());
		
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
