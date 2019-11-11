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

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.e4rcp.dialogs.AboutDialog;

public class ShowAbout {

	/**
	 * Active Shell instance
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	protected Shell shell;

	/**
	 * Is the dialog active/displayed flag;
	 * <p>
	 * This flag is needed for macOS application support. The application menu allows this handler to be called multiple
	 * times irrespective if the dialog is already open.<br>
	 * Note: We don't need to worry about thread safety, as the execute method is only run in the UI thread.
	 */
	protected boolean active = false;

	/**
	 * Open the about dialog if not already shown.
	 * 
	 * @param context The current application context.
	 */
	@Execute
	public void showAbout(IEclipseContext context) {
		if (!active) {
			active = true;
			try {
				AboutDialog dialog = new AboutDialog(shell);
				ContextInjectionFactory.inject(dialog, context);
				dialog.open();
			} finally {
				active = false;
			}
		}
	}
}
