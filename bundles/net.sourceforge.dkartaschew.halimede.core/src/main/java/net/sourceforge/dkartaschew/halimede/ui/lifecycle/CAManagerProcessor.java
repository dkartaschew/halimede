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

package net.sourceforge.dkartaschew.halimede.ui.lifecycle;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

/**
 * e4 RCP lifecycle manager.
 * <p>
 * As we are a fragment, we register ourselves as a plugin processor.
 */
@SuppressWarnings("restriction")
public class CAManagerProcessor {

	/**
	 * The tag on parts that will autoclose on shutdown of eclipse. (not persist across reboots).
	 */
	public final static String CLOSE_TAG = "E4_CAMANGER_TRANSIENT_PART";

	@Inject
	protected MApplication app;

	@Inject
	protected EModelService modelService;

	@Inject
	protected EPartService partService;

	@Inject
	private Logger logger;

	@Execute
	public void execute(final IEventBroker eventBroker) {

		/**
		 * Register a shutdown service event handler to remove all transient parts from the model.
		 */
		eventBroker.subscribe(UIEvents.UILifeCycle.APP_SHUTDOWN_STARTED, event -> {
			/*
			 * Note: Do not use partService.hidePart() as this will fail.
			 */
			closeParts(false);
		});

		/**
		 * Register a shutdown service event handler to remove all transient parts from the model.
		 */
		eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, event -> {
			closeParts(true);
		});
	}

	/**
	 * Close all parts that have out close tag
	 * 
	 * @param callHidePart TRUE to request the partService to remove/hide the part. (Safe on startup, very unsafe on
	 *            shutdown).
	 */
	private void closeParts(boolean callHidePart) {
		/*
		 * get all parts that have our transient part tag.
		 */
		List<String> tags = new ArrayList<>();
		tags.add(CLOSE_TAG);
		List<MPart> parts = modelService.findElements(app, null, MPart.class, tags);
		if (parts == null || parts.isEmpty()) {
			return;
		}
		/*
		 * Cycle through all parts removing them from their parent. This is safe to do, as out parts are leafs in the
		 * model tree. Use the part service here to ensure refresh of the model.
		 */
		for (MPart p : parts) {
			try {
				MElementContainer<MUIElement> parent = p.getParent();
				parent.getChildren().remove(p);
				p.setToBeRendered(false);
				if (callHidePart) {
					partService.hidePart(p, true);
				}
			} catch (Throwable e) {
				if (logger != null) {
					logger.error(e, ExceptionUtil.getMessage(e));
				}
			}
		}
	}
}
