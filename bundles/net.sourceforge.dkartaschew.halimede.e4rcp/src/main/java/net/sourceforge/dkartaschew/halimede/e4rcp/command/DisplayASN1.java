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

package net.sourceforge.dkartaschew.halimede.e4rcp.command;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.e4rcp.Activator;
import net.sourceforge.dkartaschew.halimede.e4rcp.ui.ASN1DisplayPart;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;
import net.sourceforge.dkartaschew.halimede.util.ASN1Decoder;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@SuppressWarnings("restriction")
public class DisplayASN1 {
	@Inject
	private Logger logger;

	@Inject
	private EPartService partService;

	@Inject
	private MApplication application;

	@Inject
	private EModelService modelService;

	@Inject
	private UISynchronize sync;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.handler.show.asn1";

	public static final String COMMAND_PARAM = "net.sourceforge.dkartaschew.halimede.handler.show.commandparameter.editorid";

	@Execute
	public void execute(ParameterizedCommand command, Shell shell) {

		// See if we have a command parameter;
		String editor = PluginDefaults.EDITOR;
		if (command != null && command.getParameterMap() != null) {
			Object ed = command.getParameterMap().get(COMMAND_PARAM);
			if (ed != null && ed instanceof String) {
				String edi = (String) ed;
				if (!edi.isEmpty()) {
					editor = edi;
				}
			}
		}

		// Get the certificate filename...
		FileDialog dlg = new FileDialog(shell, SWT.OPEN);
		dlg.setText("Open File");
		dlg.setOverwrite(true);
		dlg.setFilterExtensions(new String[] { "*.*" });
		dlg.setFilterNames(new String[] { "All Files (*.*)" });
		String filename = dlg.open();
		if (filename == null) {
			return;
		}

		ASN1Decoder decoder;
		try {
			decoder = ASN1Decoder.create(Paths.get(filename));
		} catch (IOException e1) {
			logger.error(e1, ExceptionUtil.getMessage(e1));
			sync.asyncExec(() -> {
				MessageDialog.openError(shell, "Opening the File Failed",
						"Opening the File failed with the following error: " + ExceptionUtil.getMessage(e1));
			});
			return;
		}
		String editor0 = editor;
		Job job = Job.create("Decode ASN.1", monitor -> {
			try {
				SubMonitor subMonitor = SubMonitor.convert(monitor, "Decode ASN.1", 2);
				decoder.decode();
				subMonitor.worked(1);

				List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
				if (stacks == null || stacks.isEmpty()) {
					logger.error("No Part Stacks found, unable to add view to existing Part");
					return Status.OK_STATUS;
				}
				// Create a new one.
				MPart part = MBasicFactory.INSTANCE.createPart();
				part.setLabel(Strings.trim(decoder.getFile().getFileName().toString(), PluginDefaults.PART_HEADER_LENGTH));
				part.setDescription(ASN1DisplayPart.DESCRIPTION);
				part.setContributionURI("bundleclass://" + Activator.PLUGIN_ID + "/" + ASN1DisplayPart.class.getName());
				part.setElementId(ASN1DisplayPart.ID + "#" + System.currentTimeMillis());
				part.setCloseable(true);
				part.setToBeRendered(true);
				part.setDirty(false);
				part.getTags().add(CAManagerProcessor.CLOSE_TAG);

				// Add our data to the part.
				part.getTransientData().put(ASN1DisplayPart.ASN1DECODER, decoder);
				part.getTransientData().put(ASN1DisplayPart.PARTSTACK_EDITOR, editor0);

				// Find the preferred part stack, otherwise just use the first one.
				MPartStack stack = stacks.stream().filter(p -> p.getElementId().equals(editor0)).findFirst()
						.orElse(stacks.get(0));

				sync.asyncExec(() -> {
					// Add our element.
					stack.getChildren().add(part);
					partService.showPart(part, PartState.ACTIVATE);
				});
				subMonitor.done();

			} catch (Throwable e) {
				logger.error(e, ExceptionUtil.getMessage(e));
				sync.asyncExec(() -> {
					MessageDialog.openError(shell, "Opening the File Failed",
							"Opening the File failed with the following error: " + ExceptionUtil.getMessage(e));
				});

			} finally {
				if (monitor != null) {
					monitor.done();
				}
			}
			return Status.OK_STATUS;

		});

		job.schedule();
	}

}
