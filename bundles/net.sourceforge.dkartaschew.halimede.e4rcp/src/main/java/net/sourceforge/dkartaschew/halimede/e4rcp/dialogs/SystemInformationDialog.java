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

package net.sourceforge.dkartaschew.halimede.e4rcp.dialogs;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.render.HTMLOutputRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.ICertificateOutputRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.composite.CompositeOutputRenderer;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.SystemInformation;

@SuppressWarnings("restriction")
public class SystemInformationDialog extends Dialog {

	/**
	 * The system information.
	 */
	private final SystemInformation info;

	@Inject
	private Logger logger;

	@Inject
	private UISynchronize sync;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public SystemInformationDialog(Shell parentShell) {
		super(parentShell);
		info = new SystemInformation();
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("System Information");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_APPLICATION)));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		CompositeOutputRenderer composite = new CompositeOutputRenderer(container, SWT.NONE, "System Information");
		info.render(composite);
		composite.finaliseRender();
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.redraw();
		Menu menu = composite.getMenu();
		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Save As...");
		item.setAccelerator(SWT.MOD1 + 's');
		item.addListener(SWT.Selection, e -> exportReport());
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(640, 480);
	}

	/**
	 * Export the system information as a report.
	 */
	private void exportReport() {
		FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
		dialog.setText("Save as...");
		dialog.setOverwrite(true);
		dialog.setFilterExtensions(new String[] { "*.html", "*.txt", "*.*" });
		dialog.setFilterNames(new String[] { "HTML File (*.html)", "Text File (*.txt)", "All Files (*.*)" });
		String dir = dialog.open();
		if (dir != null) {
			final String desc = "Exporting System Information";
			Job job = Job.create(desc, monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, desc, 1);
					if (logger != null) {
						logger.info("Exporting to: " + dir);
					}
					try (PrintStream out = new PrintStream(dir, StandardCharsets.UTF_8.name())) {
						ICertificateOutputRenderer renderer;
						if (dir.toLowerCase().endsWith(".html")) {
							renderer = new HTMLOutputRenderer(out, "System Information");
						} else {
							renderer = new TextOutputRenderer(out);
						}
						info.render(renderer);
						renderer.finaliseRender();
					}
					subMonitor.worked(1);
					sync.asyncExec(() -> {
						MessageDialog.openInformation(getShell(), "System Information",
								"System Information has been exported to '" + dir + "'.");
					});
				} catch (Throwable ex) {
					if (logger != null)
						logger.error(ex, "Exporting the System Information Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(getShell(), "Exporting System Information Failed",
								"System Information failed with the following error: " + ExceptionUtil.getMessage(ex));
					});
				}
				if (monitor != null) {
					monitor.done();
				}
				return Status.OK_STATUS;
			});
			job.schedule();
		}
	}
}
