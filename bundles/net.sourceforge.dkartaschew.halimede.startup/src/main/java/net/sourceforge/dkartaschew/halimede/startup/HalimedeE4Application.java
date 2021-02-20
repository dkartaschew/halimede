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

package net.sourceforge.dkartaschew.halimede.startup;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.ui.internal.workbench.WorkbenchLogger;
import org.eclipse.e4.ui.internal.workbench.swt.E4Application;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.internal.location.Locker_JavaNio;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

/**
 * Halimede CA E4 Application entry point.
 * <p>
 * This sub-classes the default E4Application implementation to provide a nice application specific "we are already
 * running" dialog, rather than the generic eclipse is open one.
 */
@SuppressWarnings("restriction")
public class HalimedeE4Application extends E4Application {

	/**
	 * Application Icon.
	 */
	private static final String APPLICATION_ICON = "icons/application-icon.png"; //$NON-NLS-1$
	private static final String BASE_LOCATION = "osgi.instance.area"; //$NON-NLS-1$
	private static final String DEFAULT_LOCK_FILENAME = ".metadata/.lock"; //$NON-NLS-1$

	@Override
	public Object start(IApplicationContext applicationContext) throws Exception {
		BundleContext context = Activator.getDefault().getBundle().getBundleContext();
		String location = context.getProperty(BASE_LOCATION);
		Display display = null;
		try {
			File lockFile = Paths.get(new URI(location)).resolve(DEFAULT_LOCK_FILENAME).toFile();
			if (lockFile.exists()) {
				// Attempt to lock it...
				Locker_JavaNio locker = new Locker_JavaNio(lockFile, true);
				if (locker.isLocked()) {

					display = getApplicationDisplay();

					URL imageURL = FileLocator.find(context.getBundle(), new Path(APPLICATION_ICON), null);
					ImageDescriptor image = ImageDescriptor.createFromURL(imageURL);
					Image icon = image.createImage();

					MessageDialog dialog = new MessageDialog(
							new Shell(display, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL), icon,
							"Halimede CA",
							"An instance of Halimede CA is already running." + System.lineSeparator()
									+ System.lineSeparator() + "Only one instance may run at any time."
									+ System.lineSeparator() + System.lineSeparator()
									+ "This application will now exit.");
					dialog.open();

					// Be mean!
					return EXIT_OK;
				}
			}
		} catch (Throwable e) {
			WorkbenchLogger logger = new WorkbenchLogger(Activator.PLUGIN_ID);
			logger.error(e, e.getMessage()); // $NON-NLS-1$
		} finally {
			if (display != null)
				display.dispose();
		}
		// Our startup application check worked ok, so pass onto the underlying application.
		return super.start(applicationContext);
	}
}
