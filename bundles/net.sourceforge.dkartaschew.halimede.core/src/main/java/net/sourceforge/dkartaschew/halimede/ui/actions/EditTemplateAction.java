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

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MBasicFactory;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.jface.action.Action;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.ui.TemplateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.lifecycle.CAManagerProcessor;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@SuppressWarnings("restriction")
public class EditTemplateAction extends Action {

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

//	@Inject
//	@Named(IServiceConstants.ACTIVE_SHELL)
//	private Shell shell;

	/**
	 * Create a new edit template action
	 * 
	 * @param ca The Certificate Authority node
	 * @param element The current template.
	 * @param editor The ID of the part stack to add the part to.
	 */
	public EditTemplateAction(CertificateAuthority ca, ICertificateKeyPairTemplate element, String editor) {
		super("Edit Template");
		setToolTipText("Edit this Template");
		this.ca = ca;
		this.element = element;
		this.editor = editor;
	}

	@Override
	public void run() {
		NewCertificateModel model = new NewCertificateModel(ca, element);
		model.setRepresentsTemplateOnly(true);

		List<MPartStack> stacks = modelService.findElements(application, null, MPartStack.class, null);
		if (stacks == null || stacks.isEmpty()) {
			logger.error("No Part Stacks found, unable to add view to existing Part");
			return;
		}

		// Create a new one.
		String desc = Strings.trim(model.getDescription(), PluginDefaults.PART_HEADER_LENGTH);
		MPart part = MBasicFactory.INSTANCE.createPart();
		part.setLabel(TemplateDetailsPart.LABEL + " '" + desc  + "'");
		part.setDescription(TemplateDetailsPart.DESCRIPTION);
		part.setContributionURI("bundleclass://" + PluginDefaults.ID + "/" + TemplateDetailsPart.class.getName());
		part.setElementId(TemplateDetailsPart.ID + "#" + System.currentTimeMillis());
		part.setCloseable(true);
		part.setToBeRendered(true);
		part.setDirty(false);
		part.getTags().add(CAManagerProcessor.CLOSE_TAG);

		// Add our data to the part.
		part.getTransientData().put(TemplateDetailsPart.CA, ca);
		part.getTransientData().put(TemplateDetailsPart.MODEL, model);

		// Find the preferred part stack, otherwise just use the first one.
		MPartStack stack = stacks.stream().filter(p -> p.getElementId().equals(editor)).findFirst()
				.orElse(stacks.get(0));

		// Add our element.
		stack.getChildren().add(part);
		partService.showPart(part, PartState.ACTIVATE);
	}
}
