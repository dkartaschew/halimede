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

package net.sourceforge.dkartaschew.halimede.startup;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "net.sourceforge.dkartaschew.halimede.startup"; //$NON-NLS-1$

	/**
	 * The shared instance of this plugin/bundle.
	 */
	private static Activator plugin;

	/**
	 * The bundle that THIS class resides in.
	 */
	private Bundle bundle;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * Start operations on the bundle.
	 * 
	 * @param context The bundle context.
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		bundle = context.getBundle();
	}

	/**
	 * Stop the bundle.
	 * 
	 * @param context The bundle context
	 * @throws Exception If stopping the bundle fails.
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		bundle = null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Get the bundle that this activator resides in.
	 * 
	 * @return The bundle.
	 */
	public Bundle getBundle() {
		return bundle;
	}

}
