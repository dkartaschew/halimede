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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.ui.util.Dialogs;

public class ExitHandler {

	@Execute
	public void execute(IWorkbench workbench, Shell shell, IEclipseContext context) {
		// Execute the default handler if available.
		IWindowCloseHandler handler = context.get(IWindowCloseHandler.class);
		if (handler != null) {
			ContextInjectionFactory.invoke(handler, Execute.class, context);
			return;
		}

		if (Dialogs.openConfirm(shell, "Quit", "Are you sure you wish to quit the application?", "Quit", "Cancel")) {
			workbench.close();
		}
	}
}
