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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

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

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.render.CertificateRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

/**
 * A event listener to export the information from the issued certificate.
 */
@SuppressWarnings("restriction")
public class ExportCertificateAsTextListener implements SelectionListener {

	/**
	 * The cert to extract the key from
	 */
	private final IssuedCertificateProperties certificate;
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
	 * Create a new export certificate private key information listener.
	 * 
	 * @param certificate The Issued Certificate to export it's certificates from
	 */
	public ExportCertificateAsTextListener(IssuedCertificateProperties certificate) {
		this.certificate = certificate;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.detail == SWT.ARROW) {
			return;
		}
		if (this.certificate.getCertificateAuthority() != null) {
			this.certificate.getCertificateAuthority().getActivityLogger().log(Level.INFO,
					"Export Certificate Details {0} as Text", this.certificate.getProperty(Key.subject));
		}
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		dialog.setText("Save as Text");
		dialog.setOverwrite(true);
		dialog.setFilterExtensions(new String[] { "*.txt", "*.*" });
		dialog.setFilterNames(new String[] { "Text File (*.txt)", "All Files (*.*)" });
		String dir = dialog.open();
		if (dir != null) {

			String certdesc = certificate.getProperty(Key.description);
			if (certdesc == null) {
				certdesc = certificate.getProperty(Key.subject);
			}
			String certDescription = certdesc;
			String desc = "Exporting from '" + certDescription + "' as Text";
			Job job = Job.create(desc, monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, desc, 1);
					if (logger != null) {
						logger.info("Exporting to: " + dir);
					}
					if (this.certificate.getCertificateAuthority() != null) {
						this.certificate.getCertificateAuthority().getActivityLogger().log(Level.INFO,
								"Export Certificate Details {0} as Text as {1}",
								new Object[] { this.certificate.getProperty(Key.subject), dir });
					}
					CertificateRenderer model = new CertificateRenderer(certificate);
					try (PrintStream out = new PrintStream(dir, StandardCharsets.UTF_8.name())) {
						TextOutputRenderer renderer = new TextOutputRenderer(out);
						renderer.addHeaderLine("Certificate '" + certDescription + "' Information");
						renderer.addHorizontalLine();
						model.render(renderer);
						renderer.finaliseRender();
					}
					subMonitor.worked(1);
					sync.asyncExec(() -> {
						MessageDialog.openInformation(shell, "Certificate Information Exported",
								"The Issued Certificate '" + certDescription + "' Information have been exported to '"
										+ dir + "'.");
					});
				} catch (Throwable ex) {
					if (logger != null)
						logger.error(ex, "Exporting the Certificate Information Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Exporting the Certificate Information Failed",
								"Exporting the Certificate Information from Issued Certificate '" + certDescription
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
