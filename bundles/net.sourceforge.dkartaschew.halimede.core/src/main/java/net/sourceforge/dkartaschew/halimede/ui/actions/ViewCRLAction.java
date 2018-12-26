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

import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties.Key;
import net.sourceforge.dkartaschew.halimede.ui.CRLDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class ViewCRLAction extends Action implements Runnable {

	/**
	 * The CRL to view
	 */
	private CRLProperties crl;

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
	
	@Inject 
	private UISynchronize sync;

	/**
	 * Create a new CRL Action
	 * 
	 * @param ca The Certificate Authority
	 * @param editor The editor to add the new CRL to.
	 */
	public ViewCRLAction(CRLProperties crl, String editor) {
		super("View CRL");
		this.crl = crl;
		this.editor = editor;
		setToolTipText("View a Certificate Revokation List (CRL) for this authority");
	}

	@Override
	public void run() {

		Job job = Job.create("View CRL - " + crl.getProperty(Key.crlSerialNumber).toString(), monitor -> {

			try {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "View CRL - " + crl.getProperty(Key.crlSerialNumber).toString(), 2);

				List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
				if (stacks == null || stacks.isEmpty()) {
					logger.error("No Part Stacks found, unable to add view to existing Part");
					throw new NoSuchElementException("No Part Stack available?");
				}

				// Create a new one.
				MPart part = MBasicFactory.INSTANCE.createPart();
				part.setLabel(CRLDetailsPart.LABEL + " '" + crl.getProperty(Key.crlSerialNumber).toString() + "'");
				part.setDescription(CRLDetailsPart.DESCRIPTION);
				part.setContributionURI("bundleclass://" + PluginDefaults.ID + "/" + CRLDetailsPart.class.getName());
				part.setElementId(CRLDetailsPart.ID + "#" + System.currentTimeMillis());
				part.setCloseable(true);
				part.setToBeRendered(true);
				part.setDirty(false);
				part.getTags().add(CAManagerProcessor.CLOSE_TAG);
				
				// Add our data to the part.
				part.getTransientData().put(CRLDetailsPart.MODEL, crl);

				sync.asyncExec(() -> {
					// Find the preferred part stack, otherwise just use the first one.
					MPartStack stack = stacks.stream().filter(p -> p.getElementId().equals(editor)).findFirst()
							.orElse(stacks.get(0));

					// Add our element.
					stack.getChildren().add(part);
					partService.showPart(part, PartState.ACTIVATE);
				});
				subMonitor.worked(1);
			} catch (Throwable e) {
				if (logger != null)
					logger.error(e, "Viewing the CRL Failed");
				sync.asyncExec(() -> {
					MessageDialog.openError(shell, "Viewing the CRL Failed",
							"Viewing the CRL failed with the following error: " + ExceptionUtil.getMessage(e));
				});
			}
			if (monitor != null) {
				monitor.done();
			}
			return Status.OK_STATUS;

		});

		job.schedule();

	}

}
