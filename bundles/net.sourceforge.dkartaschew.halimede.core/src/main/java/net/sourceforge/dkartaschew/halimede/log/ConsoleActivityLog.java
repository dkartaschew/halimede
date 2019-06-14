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
package net.sourceforge.dkartaschew.halimede.log;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;

/**
 * Activity Logger that logs to the console
 */
public class ConsoleActivityLog implements IActivityLogger {

	/**
	 * Default logging template
	 */
	private final static String TEMPLATE = "[%1$tFT%1$tT.%1$tLZ] [%2$-7s] %3$s %n";
	/**
	 * Default timezone.
	 */
	private final static ZoneId ZONE = ZoneId.of("UTC");

	/**
	 * If logging is enabled.
	 */
	private volatile boolean enabled = true;
	/**
	 * Base logger.
	 */
	private final Logger logger;

	/**
	 * Create a new log.
	 * 
	 * @param ca The certificate authority.
	 * @throws IOException Creation failed.
	 * @throws SecurityException Creation failed.
	 */
	ConsoleActivityLog(CertificateAuthority ca) {
		this.enabled = ca.isEnableLog();

		// Setup the backing logger.

		UUID id = ca.getCertificateAuthorityID();

		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new Formatter() {

			@Override
			public synchronized String format(LogRecord lr) {
				return String.format(TEMPLATE, ZonedDateTime.ofInstant(//
						Instant.ofEpochMilli(lr.getMillis()), ZONE), //
						lr.getLevel().getLocalizedName(), //
						formatMessage(lr));
			}
		});

		logger = Logger.getLogger(PluginDefaults.ID + "." + id.toString());
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
	}

	@Override
	public synchronized void propertyChange(PropertyChangeEvent evt) {
		try {
			if (evt.getPropertyName().contentEquals(CertificateAuthority.PROPERTY_ENABLE_LOG)) {
				enabled = (boolean) evt.getNewValue();
			}
		} catch (NullPointerException | ClassCastException e) {
			// ignore.
		}
	}

	@Override
	public void log(Level level, String msg) {
		if (enabled) {
			logger.log(level, msg);
		}
	}

	@Override
	public void log(Level level, String msg, Object param1) {
		if (enabled) {
			logger.log(level, msg, param1);
		}
	}

	@Override
	public void log(Level level, String msg, Object[] params) {
		if (enabled) {
			logger.log(level, msg, params);
		}
	}

}
