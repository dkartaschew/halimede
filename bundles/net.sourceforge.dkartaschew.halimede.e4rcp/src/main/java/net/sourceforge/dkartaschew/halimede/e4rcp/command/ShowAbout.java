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

package net.sourceforge.dkartaschew.halimede.e4rcp.command;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.e4rcp.dialogs.AboutDialog;

public class ShowAbout {

	@Execute
	public void showAbout(Display display, IEclipseContext context) {
		Shell shell = display.getActiveShell();
		if (shell == null) {
			Shell[] shells = display.getShells();
			if (shells == null || shells.length == 0) {
				shell = new Shell(display);
			} else {
				shell = shells[0];
			}
		}
		AboutDialog dialog = new AboutDialog(shell);
		ContextInjectionFactory.inject(dialog, context);
		dialog.open();
	}
}
