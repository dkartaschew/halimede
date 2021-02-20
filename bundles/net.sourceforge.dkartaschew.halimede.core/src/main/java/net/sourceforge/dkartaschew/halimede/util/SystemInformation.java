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
package net.sourceforge.dkartaschew.halimede.util;

import java.security.Security;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import net.sourceforge.dkartaschew.halimede.Activator;
import net.sourceforge.dkartaschew.halimede.data.render.ICertificateOutputRenderer;

public class SystemInformation {

	/**
	 * Units for memory conversion.
	 */
	private final static String[] units = new String[] { "B", "KB", "MB", "GB", "TB", "PB", "EB" };

	/**
	 * The maximum available memory
	 */
	private final long maxMemory;
	/**
	 * The total amount the memory;
	 */
	private final long totalMemory;
	/**
	 * The amount of free memory
	 */
	private final long freeMemory;

	/**
	 * Create a new instance of the system information/
	 */
	public SystemInformation() {
		System.gc();
		Runtime runtime = Runtime.getRuntime();
		maxMemory = runtime.maxMemory();
		freeMemory = runtime.freeMemory();
		totalMemory = runtime.totalMemory();
	}

	/**
	 * Render the System Information to the given output renderer.
	 * 
	 * @param r The renderer to output to.
	 */
	public void render(ICertificateOutputRenderer r) {
		// Render each part to the output.
		application(r);
		r.addHorizontalLine();
		javaRuntime(r);
		r.addHorizontalLine();
		javaProperties(r);
		r.addHorizontalLine();
		operatingSystemEnvironment(r);
		r.addHorizontalLine();
		osgiBundles(r);
		r.addHorizontalLine();
		cryptoProviders(r);
	}

	/**
	 * Render application information and the date time
	 * 
	 * @param r The renderer to output to.
	 */
	private void application(ICertificateOutputRenderer r) {
		r.addHeaderLine("Application");
		r.addEmptyLine();
		try {
			Bundle bundle = FrameworkUtil.getBundle(getClass());
			if (bundle != null) {
				Version version = bundle.getVersion();
				r.addContentLine("Name: " + bundle.getHeaders().get("Bundle-Name"));
				r.addContentLine("Bundle: " + bundle.getSymbolicName());
				r.addContentLine("Version: " + version.toString());
			}
			r.addContentLine(DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
		} catch (Throwable e) {
			r.addContentLine("Error: " + ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Render the java runtime.
	 * 
	 * @param r The renderer to output to.
	 */
	private void javaRuntime(ICertificateOutputRenderer r) {
		Runtime runtime = Runtime.getRuntime();
		r.addHeaderLine("Java Runtime");
		r.addEmptyLine();
		try {
			r.addContentLine("Java Version: " + getVersion());
			r.addContentLine("Processors : " + runtime.availableProcessors());
			r.addContentLine("Used Memory: " + readableSize(totalMemory - freeMemory));
			r.addContentLine("Total Memory: " + readableSize(totalMemory));
			r.addContentLine("Max Available Memory: " + readableSize(maxMemory));
		} catch (Throwable e) {
			r.addContentLine("Error: " + ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Get the java version
	 * 
	 * @return The version.
	 */
	private String getVersion() {
		String version = System.getProperty("java.version");
		if (version.startsWith("1.")) {
			return version.substring(2);
		} else {
			return version;
		}
	}

	/**
	 * Render the java runtime.
	 * 
	 * @param r The renderer to output to.
	 */
	private void javaProperties(ICertificateOutputRenderer r) {
		r.addHeaderLine("Java System Properties");
		r.addEmptyLine();
		try {
			Properties system = System.getProperties();
			List<String> keys = new ArrayList<>();
			system.keySet().forEach(e -> keys.add((String) e));
			Collections.sort(keys);
			for (String key : keys) {
				r.addContentLine(key + " : " + system.getProperty(key));
			}
		} catch (Throwable e) {
			r.addContentLine("Error: " + ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Render the OS environment
	 * 
	 * @param r The renderer to output to.
	 */
	private void operatingSystemEnvironment(ICertificateOutputRenderer r) {
		r.addHeaderLine("Operating System Environment");
		r.addEmptyLine();
		try {
			Map<String, String> env = System.getenv();
			List<String> keys = new ArrayList<>();
			keys.addAll(env.keySet());
			Collections.sort(keys);
			for (String key : keys) {
				r.addContentLine(key + " : " + env.get(key));
			}
		} catch (Throwable e) {
			r.addContentLine("Error: " + ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Render the collection of OSGi bundles
	 * 
	 * @param r The renderer to output to.
	 */
	private void osgiBundles(ICertificateOutputRenderer r) {
		r.addHeaderLine("Application Bundles");
		r.addEmptyLine();
		try {
			if (Activator.getDefault() == null) {
				r.addContentLine("Not in OSGi environment?");
				return;
			}
			BundleContext bundleCtx = Activator.getDefault().getContext();
			if (bundleCtx == null) {
				r.addContentLine("No Bundle Context Available?");
				return;
			}

			final Bundle[] bundles = bundleCtx.getBundles();
			if (bundles == null || bundles.length == 0) {
				r.addContentLine("No Bundles Available?");
				return;
			}
			Map<String, Bundle> bundleMap = new HashMap<>();
			List<String> names = new ArrayList<>();
			for (Bundle b : bundles) {
				bundleMap.put(b.getSymbolicName(), b);
				names.add(b.getSymbolicName());
			}
			Collections.sort(names);
			for (String name : names) {
				Bundle bundle = bundleMap.get(name);
				Version version = bundle.getVersion();
				String state = getState(bundle.getState());
				String location = bundle.getLocation();
				r.addContentLine(name + " : v" + version + " : " + state + " : " + location);
			}
		} catch (Throwable e) {
			r.addContentLine("Error: " + ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Render the collection of crypto providers
	 * 
	 * @param r The renderer to output to.
	 */
	private void cryptoProviders(ICertificateOutputRenderer r) {
		r.addHeaderLine("Cryptographic Providers");
		r.addEmptyLine();
		try {
			java.security.Provider p[] = Security.getProviders();
			for (int i = 0; i < p.length; i++) {
				java.security.Provider p0 = p[i];
				r.addContentLine(p0.toString() + " (\"" + p0.getName() + "\" " + p0.getVersionStr() + ")");
				List<String> items = new ArrayList<>();
				for (Enumeration<Object> e = p0.keys(); e.hasMoreElements();) {
					items.add(e.nextElement().toString());
				}
				Collections.sort(items);
				for (String s : items) {
					String s0 = s.toLowerCase();
					if (!(s0.contains("alias") || s0.contains("provider.id"))) {
						r.addContentLine("   " + s);
					}
				}
			}
		} catch (Throwable e) {
			r.addContentLine("Error: " + ExceptionUtil.getMessage(e));
		}
	}

	/**
	 * Convert the bundle state to textual
	 * 
	 * @param state The bundle state
	 * @return The string representation.
	 */
	private String getState(int state) {
		switch (state) {
		case Bundle.UNINSTALLED:
			return "UNINSTALLED";
		case Bundle.INSTALLED:
			return "INSTALLED";
		case Bundle.RESOLVED:
			return "RESOLVED";
		case Bundle.STARTING:
			return "STARTING";
		case Bundle.STOPPING:
			return "STOPPING";
		case Bundle.ACTIVE:
			return "ACTIVE";
		}
		return "UNKNOWN";
	}

	/**
	 * Convert the size to a readable value
	 * 
	 * @param size The size to convert
	 * @return A readable size
	 */
	private String readableSize(long size) {
		if (size <= 0) {
			return "0";
		}
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

}
