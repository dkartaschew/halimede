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

package net.sourceforge.dkartaschew.halimede.e4ide.command;

import java.nio.file.Paths;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.ICertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCertificateRequestInformationAction;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class ShowCSR {
	@Inject
	private Logger logger;
	@Inject
	private IEclipseContext context;
	@Inject 
	private UISynchronize sync;
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.handler.show.csr";

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
		dlg.setText("Open CSR File");
		dlg.setOverwrite(true);
		dlg.setFilterExtensions(new String[] { "*.csr", "*.*" });
		dlg.setFilterNames(new String[] { "Certificate Request (*.csr)", "All Files (*.*)" });
		String filename = dlg.open();
		if (filename == null) {
			return;
		}

		try {
			ICertificateRequest csr = CertificateRequestPKCS10.create(Paths.get(filename));
			CertificateRequestProperties properties = new CertificateRequestProperties(null, csr);
			ViewCertificateRequestInformationAction action = new ViewCertificateRequestInformationAction(properties, editor);
			ContextInjectionFactory.inject(action, context);
			sync.asyncExec(action);
		} catch (Throwable e) {
			logger.error(e, ExceptionUtil.getMessage(e));
			sync.asyncExec(() -> {
				MessageDialog.openError(shell, "Opening the CSR Failed",
						"Opening the Certificate Request failed with the following error: " + ExceptionUtil.getMessage(e));
			});
		}
	}
}
