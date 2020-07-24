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

package net.sourceforge.dkartaschew.halimede.e4rcp.command;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.actions.RestoreCAAction;

public class RestoreCAHandler {

	/**
	 * The Certificate Authority manager.
	 */
	@Inject
	private CertificateAuthourityManager manager;

	@Inject
	private UISynchronize sync;

	@Execute
	public void execute(Shell shell, IEclipseContext context) {
		/*
		 * Find our manager
		 */
		if (manager == null) {
			sync.asyncExec(() -> {
				MessageDialog.openError(shell, "Restore Operation Failed",
						"The restore operation failed with the following error: Manager not available?");
			});
			return;
		}
		RestoreCAAction action = new RestoreCAAction(manager);
		ContextInjectionFactory.inject(action, context);
		sync.asyncExec(action);
	}
}
