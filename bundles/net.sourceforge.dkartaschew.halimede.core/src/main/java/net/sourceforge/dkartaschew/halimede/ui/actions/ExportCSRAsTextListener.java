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

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.render.CSRRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

/**
 * A event listener to export the information from the CSR.
 */
@SuppressWarnings("restriction")
public class ExportCSRAsTextListener implements SelectionListener {

	/**
	 * The CSR
	 */
	private final CertificateRequestProperties csr;
	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	@Inject
	private Logger logger;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * Create a new export CSR information listener.
	 * 
	 * @param csr The CSR to export from
	 */
	public ExportCSRAsTextListener(CertificateRequestProperties csr) {
		this.csr = csr;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.detail == SWT.ARROW) {
			return;
		}
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Save as Text");
		dialog.setOverwrite(true);
		dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });
		dialog.setFilterNames(new String[] { "Text File (*.txt)", "All Files (*.*)" });
		String dir = dialog.open();
		if (dir != null) {

			String desc = "Exporting CSR #" + csr.getProperty(Key.subject);
			Job job = Job.create(desc, monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, desc,  1);
					if (logger != null) {
						logger.info("Exporting to: " + dir);
					}

					CSRRenderer model = new CSRRenderer(csr);
					try (PrintStream out = new PrintStream(dir, StandardCharsets.UTF_8.name())) {
						TextOutputRenderer renderer = new TextOutputRenderer(out);
						renderer.addHeaderLine("CSR: " + csr.getProperty(Key.subject));
						renderer.addHorizontalLine();
						model.render(renderer);
						renderer.finaliseRender();
					}
					subMonitor.worked(1);
					sync.asyncExec(() -> {
						MessageDialog.openInformation(shell, "Certificate Request Exported", "Certificate Request for ;"
								+ csr.getProperty(Key.subject) + "; has been exported to '" + dir + "'.");
					});
				} catch (Throwable ex) {
					if (logger != null)
						logger.error(ex, "Exporting the Certificate Request Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Exporting Certificate Request Failed",
								"Certificate Request for '" + csr.getProperty(Key.subject)
										+ "' failed with the following error: " + ExceptionUtil.getMessage(ex));
					});
				}
				if(monitor != null) {
					monitor.done();
				}
				return Status.OK_STATUS;
			});
			job.schedule();
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		widgetSelected(e);
	}

}
