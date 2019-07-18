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

package net.sourceforge.dkartaschew.halimede.e4rcp.ui;

import java.util.Collection;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * An application close handler.
 * <p>
 * This handler will check for dirty parts and propmt to save.
 */
public class HalimedeCloseHandler implements IWindowCloseHandler {

	final private IEclipseContext context;

	/**
	 * Create a new Halimede Close handler
	 * @param context The application context.
	 */
	public HalimedeCloseHandler(IEclipseContext context) {
		this.context = context;
	}

	@Execute
	@Override
	public boolean close(MWindow window) {
		Shell shell = context.getActive(Shell.class);
		if (shell == null) {
			shell = context.get(Shell.class);
		}
		if (MessageDialog.openConfirm(shell, "Quit", "Are you sure you wish to quit the application?")) {
			EPartService partService = window.getContext().get(EPartService.class);
			Collection<MPart> parts = partService.getDirtyParts();
			if(!parts.isEmpty()) {
				ISaveHandler saveHandler = window.getContext().get(ISaveHandler.class);
				if (!saveHandler.saveParts(parts, true)) {
					return false;
				}
			}
			return context.get(IWorkbench.class).close();
		}
		return false;
	}

}
