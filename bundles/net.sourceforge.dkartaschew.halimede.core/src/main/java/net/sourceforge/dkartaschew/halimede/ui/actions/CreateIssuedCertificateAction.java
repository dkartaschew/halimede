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

import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Level;

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
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CreateIssuedCertificateAction extends Action {

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
	private EPartService partService;

	@Inject
	private MApplication application;

	@Inject
	private EModelService modelService;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell parent;

	/**
	 * Create a new action.
	 * 
	 * @param ca The Certificate Authority node
	 * @param editor The ID of the part stack to add the part to.
	 */
	public CreateIssuedCertificateAction(CertificateAuthority ca, String editor) {
		super("Create New Client Key/Certificate Pair");
		this.ca = ca;
		this.editor = editor;
		setEnabled(!ca.isLocked());
		if (!ca.isLocked()) {
			setToolTipText("Create a new Certificate to be signed by this authority");
		} else {
			setToolTipText("Unlock the authority to enable creation of a new Client Key/Certificate Pair.");
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(!ca.isLocked() ? enabled : false);
	}

	@Override
	public void run() {
		// Check to ensure we are not past the CA's expiry date
		try {
			ZonedDateTime now = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
			X509Certificate cert = (X509Certificate) ca.getCertificate();
			ZonedDateTime CAStart = DateTimeUtil.toZonedDateTime(cert.getNotBefore());
			ZonedDateTime CAExpiry = DateTimeUtil.toZonedDateTime(cert.getNotAfter());
			if (CAStart.isAfter(now) || CAExpiry.isBefore(now)) {
				MessageDialog.openError(parent, "CA Date",
						"This CA's certificate is currently not valid."
								+ " Either the Certificates Start/NotBefore date is in the future, or the"
								+ " Certificate has expired (Expiry/NoAfter date is in the past)."
								+ " Unable to create a new certificate to be issued by this Certificate Authority.");
				return;
			}
		} catch (DatastoreLockedException e) {
			if (logger != null) {
				logger.error(e, "Unable to get CA certificate?");
			}
			MessageDialog.openError(parent, "CA Locked", "This CA's certificate datastore is currently locked."
					+ " Unable to create a new certificate to be issued by this Certificate Authority.");
			return;
		} catch (Throwable e) {
			if (logger != null) {
				logger.error(e, "Unhandled error?");
			}
			MultiStatus s = new MultiStatus(PluginDefaults.ID, IStatus.ERROR, ExceptionUtil.getMessage(e), e);
			ErrorDialog.openError(parent, "CA Error", "An unhandled error occurred attempting to access the CA.", s);
			return;
		}

		List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
		if (stacks == null || stacks.isEmpty()) {
			logger.error("No Part Stacks found, unable to add view to existing Part");
			return;
		}
		ca.getActivityLogger().log(Level.INFO, "Start NEW Certificate");
		// Create a new one.
		MPart part = MBasicFactory.INSTANCE.createPart();
		part.setLabel(NewCertificateDetailsPart.LABEL);
		part.setDescription(NewCertificateDetailsPart.DESCRIPTION);
		part.setContributionURI("bundleclass://" + PluginDefaults.ID + "/" + NewCertificateDetailsPart.class.getName());
		part.setElementId(NewCertificateDetailsPart.ID + "#" + System.currentTimeMillis());
		part.setCloseable(true);
		part.setToBeRendered(true);
		part.getTags().add(CAManagerProcessor.CLOSE_TAG);

		// Add our data to the part.
		part.getTransientData().put(NewCertificateDetailsPart.CA, ca);

		// Find the preferred part stack, otherwise just use the first one.
		MPartStack stack = stacks.stream().filter(p -> p.getElementId().equals(editor)).findFirst()
				.orElse(stacks.get(0));

		// Add our element.
		stack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}

}
