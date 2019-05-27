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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.CertificateManagerView;
import net.sourceforge.dkartaschew.halimede.ui.actions.OpenCAAction;

public class OpenCAHandler {

	/**
	 * CA Manager
	 */
	private CertificateAuthourityManager manager;

	@Inject
	private UISynchronize sync;

	@Inject
	private EPartService partService;

	@Execute
	public void execute(Shell shell, IEclipseContext context) {
		/*
		 * Find our manager
		 */
		MPart view = partService.findPart("net.sourceforge.dkartaschew.halimede.e4rcp.part.main");
		Object obj = view.getObject();
		if (obj instanceof CertificateManagerView) {
			manager = ((CertificateManagerView) obj).getCAManager();
		} else {
			sync.asyncExec(() -> {
				MessageDialog.openError(shell, "Opening the CA Failed",
						"Opening the CA failed with the following error: Manager not available?");
			});
			return;
		}
		OpenCAAction action = new OpenCAAction(manager);
		ContextInjectionFactory.inject(action, context);
		sync.asyncExec(action);
	}
}
