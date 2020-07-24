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
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CreateCertificateFromTemplateAction extends Action {

	/**
	 * The template to delete.
	 */
	private ICertificateKeyPairTemplate element;
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
	private Shell shell;

	/**
	 * Create a new certificate instance from a template action
	 * 
	 * @param ca The Certificate Authority node
	 * @param element The current template.
	 * @param editor The ID of the part stack to add the part to.
	 */
	public CreateCertificateFromTemplateAction(CertificateAuthority ca, ICertificateKeyPairTemplate element,
			String editor) {
		super("Create new Certificate");
		this.ca = ca;
		this.element = element;
		this.editor = editor;
		setEnabled(!ca.isLocked());
		if (!ca.isLocked()) {
			setToolTipText("Create a new Certificate based on this Template");
		} else {
			setToolTipText("Unlock the authority to enable creation of a new Client Key/Certificate Pair from the template.");
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(!ca.isLocked() ? enabled : false);
	}

	@Override
	public void run() {
		ca.getActivityLogger().log(Level.INFO, "Start create Certificate from Template {0}", element);
		
		NewCertificateModel model = new NewCertificateModel(ca, element);

		// Check to ensure we are not past the CA's expiry date
		ZonedDateTime startDate = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
		ZonedDateTime expiryDate = startDate.plusDays(ca.getExpiryDays());
		// Check to ensure the expiry date is before the end date of the ca.
		try {
			X509Certificate cert = (X509Certificate) ca.getCertificate();
			ZonedDateTime CAExpiry = DateTimeUtil.toZonedDateTime(cert.getNotAfter());
			if (CAExpiry.isBefore(expiryDate)) {
				expiryDate = CAExpiry;
			}
			model.setNotBefore(DateTimeUtil.toZonedDateTime(cert.getNotBefore()));
			model.setNotAfter(DateTimeUtil.toZonedDateTime(cert.getNotAfter()));
		} catch (DatastoreLockedException e) {
			if (logger != null) {
				logger.error(e, "Unable to get CA certificate?");
			}
		} catch (Throwable e) {
			if (logger != null) {
				logger.error(e, "Unhandled error?");
			}
			MultiStatus s = new MultiStatus(PluginDefaults.ID, IStatus.ERROR, ExceptionUtil.getMessage(e), e);
			ErrorDialog.openError(shell, "CA Error", "An unhandled error occurred attempting to access the CA.", s);
			return;
		}

		model.setStartDate(startDate);
		model.setExpiryDate(expiryDate);
		model.setCreationDate(startDate);

		List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
		if (stacks == null || stacks.isEmpty()) {
			logger.error("No Part Stacks found, unable to add view to existing Part");
			return;
		}

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
		part.getTransientData().put(NewCertificateDetailsPart.MODEL, model);

		// Find the preferred part stack, otherwise just use the first one.
		MPartStack stack = stacks.stream().filter(p -> p.getElementId().equals(editor)).findFirst()
				.orElse(stacks.get(0));

		// Add our element.
		stack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}
}
