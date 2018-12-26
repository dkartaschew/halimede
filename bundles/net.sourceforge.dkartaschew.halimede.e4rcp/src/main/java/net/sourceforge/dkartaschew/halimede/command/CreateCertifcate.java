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

package net.sourceforge.dkartaschew.halimede.command;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.ui.NewSelfSignedCertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;

@SuppressWarnings("restriction")
public class CreateCertifcate {

	@Inject
	private Logger logger;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.handler.create.certificate";
	
	public static final String COMMAND_PARAM = "net.sourceforge.dkartaschew.halimede.handler.show.commandparameter.editorid";

	@Execute
	public void execute(EPartService partService, MApplication application, EModelService modelService,
			ParameterizedCommand command) {
		
		// Create a new one.
		MPart part = MBasicFactory.INSTANCE.createPart();
		part.setLabel(NewSelfSignedCertificateDetailsPart.LABEL);
		part.setDescription(NewSelfSignedCertificateDetailsPart.DESCRIPTION);
		part.setContributionURI("bundleclass://" + PluginDefaults.ID + "/" + NewSelfSignedCertificateDetailsPart.class.getName());
		part.setElementId(NewSelfSignedCertificateDetailsPart.ID);
		part.setCloseable(true);
		part.setToBeRendered(true);
		part.getTags().add(CAManagerProcessor.CLOSE_TAG);

		List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
		if (stacks == null || stacks.isEmpty()) {
			logger.error("No Part Stacks found, unable to add view to existing Part");
			return;
		}

		// See if we have a command parameter;
		String editor = PluginDefaults.EDITOR;
		if(command != null && command.getParameterMap() != null) {
			Object ed = command.getParameterMap().get(COMMAND_PARAM);
			if(ed != null && ed instanceof String) {
				String edi = (String)ed;
				if(!edi.isEmpty()) {
					editor = edi;
				}
			}
		}
		// Set the editor we should add to.
		part.getTransientData().put(NewSelfSignedCertificateDetailsPart.PARTSTACK_EDITOR, editor);
		
		// Find the preferred part stack, otherwise just use the first one.
		int id = 0;
		for (int i = 0; i < stacks.size(); i++) {
			MPartStack s = stacks.get(i);
			if (s.getElementId().equals(editor)) {
				id = i;
			}
		}
		// Add our element.
		stacks.get(id).getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}

}
