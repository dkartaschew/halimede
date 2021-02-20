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

package net.sourceforge.dkartaschew.halimede.log;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Level;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;

/**
 * Activity Logger
 * <p>
 * This offers a subset of JUL ({@link java.util.Logger}) logging services.
 */
public interface IActivityLogger extends PropertyChangeListener {

	/**
	 * Create a new activity logger for the given CA.
	 * 
	 * @param ca The Certificate Authority to link to.
	 * @return The logger for the given CA.
	 */
	public static IActivityLogger createLogger(CertificateAuthority ca) {
		try {
			IActivityLogger logger = new FileActivityLog(ca);
			ca.addPropertyChangeListener(logger);
			return logger;
		} catch (IOException | SecurityException e) {
			// Reset to console logger!
			IActivityLogger logger = new ConsoleActivityLog(ca);
			ca.addPropertyChangeListener(logger);
			return logger;
		}
	}

	/**
	 * Log a message, with no arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then the given message logged.
	 *
	 * @param level One of the message level identifiers, e.g., SEVERE
	 * @param msg The string message (or a key in the message catalog)
	 */
	public void log(Level level, String msg);

	/**
	 * Log a message, with one object parameter.
	 * <p>
	 * If the logger is currently enabled for the given message level then the message is logged.
	 *
	 * @param level One of the message level identifiers, e.g., SEVERE
	 * @param msg The string message (or a key in the message catalog)
	 * @param param1 parameter to the message
	 */
	public void log(Level level, String msg, Object param1);

	/**
	 * Log a message, with an array of object arguments.
	 * <p>
	 * If the logger is currently enabled for the given message level then the message is logged.
	 *
	 * @param level One of the message level identifiers, e.g., SEVERE
	 * @param msg The string message (or a key in the message catalog)
	 * @param params array of parameters to the message
	 */
	public void log(Level level, String msg, Object params[]);

}
