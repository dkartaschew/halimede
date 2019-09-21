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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;

/**
 * Adhoc testing for audit log implementation
 */
public class TestAuditLog {
	
	@Before
	public void setup() {
		Path dest = Paths.get(TestUtilities.TMP, "CA");
		try {
			TestUtilities.cleanup(dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void basicDualLogSetup() throws SecurityException, IOException {
		Logger log1 = getLogger(UUID.fromString("6ea8d959-87b0-4d3d-affe-264d17006a33"));
		Logger log2 = getLogger(UUID.fromString("0dcc2f08-ed92-4e94-bdd8-9c93e816061f"));
		log1.info("Log 1: Simple Message");
		log2.info("Log 2: Simple Message");
		for (int i = 0; i < 10; i++) {
			log1.log(Level.INFO, "Log 1: Simple Message {0}", i);
			log2.log(Level.INFO, "Log 2: Simple Message {0} = {1}", new Object[] { i, i * 2 });
		}
	}

	/**
	 * Writing out the log file.
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void testBasicFileLogger() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		Path dest = Paths.get(TestUtilities.TMP, "CA");
		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			Path dest2 = Paths.get(TestUtilities.TMP, "CA", "Log");
			try {
				TestUtilities.cleanup(dest2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// ensure original state.
			CertificateAuthority ca = CertificateAuthority.open(dest);
			assertNotNull(ca);
			ca.setEnableLog(true);

			// Create basic file logger.
			IActivityLogger logger = ca.getActivityLogger();
			assertNotNull(logger);

			// Should NOT throw
			logger.propertyChange(null);
			logger.propertyChange(new PropertyChangeEvent(logger, CertificateAuthority.PROPERTY_ENABLE_LOG, null, Long.valueOf(1)));

			// Write out some logs.
			logger.log(Level.INFO, "Log 1");
			logger.log(Level.SEVERE, "Log S: Simple Message");
			logger.log(Level.WARNING, "Log W: Simple Message");
			for (int i = 0; i < 10; i++) {
				logger.log(Level.INFO, "Log I: Simple Message {0}", i);
				logger.log(Level.SEVERE, "Log S: Simple Message {0} = {1}", new Object[] { i, i * 2 });
			}

			ca.setEnableLog(false);

			File[] files = dest.resolve(CertificateAuthority.LOG_PATH).toFile().listFiles();
			// should only be 1 file.
			assertEquals(2, files.length);
			File logFile = files[0];
			if (logFile.getName().endsWith("lck")) {
				logFile = files[1];
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
			long l = logFile.length();

			// Write out some more logs.
			logger.log(Level.INFO, "Log 1");
			logger.log(Level.SEVERE, "Log S: Simple Message");
			logger.log(Level.WARNING, "Log W: Simple Message");
			for (int i = 0; i < 10; i++) {
				logger.log(Level.INFO, "Log I: Simple Message {0}", i);
				logger.log(Level.SEVERE, "Log S: Simple Message {0} = {1}", new Object[] { i, i * 2 });
			}
			files = dest.resolve(CertificateAuthority.LOG_PATH).toFile().listFiles();

			// should only be 2 file.
			assertEquals(2, files.length);
			logFile = files[0];
			if (logFile.getName().endsWith("lck")) {
				logFile = files[1];
			}
			// And should have not been updated
			assertEquals(l, logFile.length());

		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	/**
	 * Writing out the log file.
	 * 
	 * @throws Exception The opening of the CA failed.
	 */
	@Test
	public void testConsoleFileLogger() throws Exception {
		Path path = TestUtilities.getFolder("CA");
		Path dest = Paths.get(TestUtilities.TMP, "CA");
		// Copy to /tmp
		try {
			TestUtilities.copyFolder(path, dest);
			Path dest2 = Paths.get(TestUtilities.TMP, "CA", "Log");
			try {
				TestUtilities.cleanup(dest2);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Create a log file to cause IOException on creation of log file.
			Path logLock = dest.resolve(CertificateAuthority.LOG_PATH);
			Files.createFile(logLock);

			// ensure original state.
			CertificateAuthority ca = CertificateAuthority.open(dest);
			assertNotNull(ca);
			ca.setEnableLog(true);

			// Create basic console logger.
			IActivityLogger logger = ca.getActivityLogger();
			assertNotNull(logger);
			assertTrue(logger instanceof ConsoleActivityLog);

			// Should NOT throw
			logger.propertyChange(null);
			logger.propertyChange(new PropertyChangeEvent(logger, CertificateAuthority.PROPERTY_ENABLE_LOG, null, Long.valueOf(1)));

			// Write out some logs.
			logger.log(Level.INFO, "Log 1");
			logger.log(Level.SEVERE, "Log S: Simple Message");
			logger.log(Level.WARNING, "Log W: Simple Message");
			for (int i = 0; i < 10; i++) {
				logger.log(Level.INFO, "Log I: Simple Message {0}", i);
				logger.log(Level.SEVERE, "Log S: Simple Message {0} = {1}", new Object[] { i, i * 2 });
			}

			ca.setEnableLog(false);

			// Write out some more logs.
			logger.log(Level.INFO, "Log 1");
			logger.log(Level.SEVERE, "Log S: Simple Message");
			logger.log(Level.WARNING, "Log W: Simple Message");
			for (int i = 0; i < 10; i++) {
				logger.log(Level.INFO, "Log I: Simple Message {0}", i);
				logger.log(Level.SEVERE, "Log S: Simple Message {0} = {1}", new Object[] { i, i * 2 });
			}

		} finally {
			TestUtilities.cleanup(dest);
		}
	}

	public Logger getLogger(UUID id) throws SecurityException, IOException {
		Path baseLocation = Paths.get(TestUtilities.TMP, "log");
		Files.createDirectories(baseLocation);
		String logFile = baseLocation.toString() + File.separator + id.toString() + ".%g.log";

		FileHandler handler = new FileHandler(logFile, 10 * 1024 * 1024, 1000, true);
		handler.setFormatter(new Formatter() {
			private final String format = "[%1$tFT%1$tT.%1$tLZ] [%2$-7s] %3$s %n";
			private final ZoneId zone = ZoneId.of("UTC");

			@Override
			public synchronized String format(LogRecord lr) {
				return String.format(format, ZonedDateTime.ofInstant(//
						Instant.ofEpochMilli(lr.getMillis()), zone), //
						lr.getLevel().getLocalizedName(), //
						formatMessage(lr));
			}
		});

		Logger logger = Logger.getLogger(PluginDefaults.ID + "." + id.toString());
		logger.setUseParentHandlers(false);
		logger.addHandler(handler);
		logger.setLevel(Level.ALL);
		return logger;
	}
}
