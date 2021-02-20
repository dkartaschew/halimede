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
package net.sourceforge.dkartaschew.halimede.test.ui;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCASettings {

	private static SWTWorkbenchBot bot;
	private static String tmp;
	private static String caName;
	private static CertificateAuthourityManager manager;

	@Inject
	private CertificateAuthourityManager holder;

	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 10;
		tmp = TestUtilities.TMP;
		bot = new SWTWorkbenchBot(TestUtilities.getEclipseContext());
		manager = TestUtilities.getEclipseContext().get(CertificateAuthourityManager.class);
		caName = TestUtilities.createBasicCA(bot, tmp);
	}

	@AfterClass
	public static void cleanup() throws IOException {
		if (manager != null) {
			List<CertificateAuthority> calist = new ArrayList<>();
			calist.addAll(manager.getCertificateAuthorities());
			calist.forEach(manager::remove);
		}
		try {
			TestUtilities.cleanup(Paths.get(tmp, caName));
		} catch (IOException e) {
			// Ignore.
		}
	}

	@Before
	public void setup() {
		if (manager == null) {
			ContextInjectionFactory.inject(this, TestUtilities.getEclipseContext());
			manager = holder;
		}
	}

	@After
	public void closeShells() {
		SWTBotShell shell = bot.activeShell();
		while (!shell.getText().contains(PluginDefaults.APPLICATION_FULLNAME)) {
			shell.close();
			shell = bot.activeShell();
		}
	}

	@Test
	public void basicSettingsDialog() throws Exception {
		Collection<CertificateAuthority> calist = manager.getCertificateAuthorities();
		CertificateAuthority ca = calist.stream().filter(c -> c.getDescription().equals(caName)).findFirst().get();

		SWTBotTree primaryTree = bot.tree();
		primaryTree.getTreeItem(caName).select();
		primaryTree.contextMenu("Certificate Authority Settings").click();

		bot.waitUntil(shellIsActive("Certificate Authority Settings"));
		SWTBotShell settings = bot.activeShell();

		try {
			assertFalse(bot.textWithLabel("Description:").isReadOnly());
			assertTrue(bot.textWithLabel("Certificate Subject:").isReadOnly());
			assertTrue(bot.textWithLabel("Location:").isReadOnly());

			assertEquals(ca.getDescription(), bot.textWithLabel("Description:").getText());
			assertEquals(ca.getSignatureAlgorithm().toString(), bot.comboBox().selection());
			assertEquals(ca.getExpiryDays(), bot.spinner().getSelection());
			assertEquals(ca.isIncrementalSerial(), bot.checkBox(0).isChecked());
			assertEquals(ca.isEnableLog(), bot.checkBox(1).isChecked());
		} finally {
			bot.button("Cancel").click();
			bot.waitUntil(Conditions.shellCloses(settings));
		}
	}

	@Test
	public void changeDescription() throws Exception {
		Collection<CertificateAuthority> calist = manager.getCertificateAuthorities();
		CertificateAuthority ca = calist.stream().filter(c -> c.getDescription().equals(caName)).findFirst().get();

		SWTBotTree primaryTree = bot.tree();
		primaryTree.getTreeItem(caName).select();
		primaryTree.contextMenu("Certificate Authority Settings").click();

		bot.waitUntil(shellIsActive("Certificate Authority Settings"));
		SWTBotShell settings = bot.activeShell();

		try {

			bot.textWithLabel("Description:").setText("");
			assertFalse(bot.button("Save").isEnabled());

			bot.textWithLabel("Description:").setText("caName");

			bot.button("Save").click();
			bot.waitUntil(Conditions.shellCloses(settings));

			assertEquals("caName", ca.getDescription());

		} finally {
			ca.setDescription(caName);
		}
	}

	@Test
	public void changeExpiryDays() throws Exception {
		Collection<CertificateAuthority> calist = manager.getCertificateAuthorities();
		CertificateAuthority ca = calist.stream().filter(c -> c.getDescription().equals(caName)).findFirst().get();

		SWTBotTree primaryTree = bot.tree();
		primaryTree.getTreeItem(caName).select();
		primaryTree.contextMenu("Certificate Authority Settings").click();

		bot.waitUntil(shellIsActive("Certificate Authority Settings"));
		SWTBotShell settings = bot.activeShell();

		try {
			bot.spinner().setSelection(31);

			bot.button("Save").click();
			bot.waitUntil(Conditions.shellCloses(settings));

			assertEquals(31, ca.getExpiryDays());
		} finally {
			ca.setExpiryDays(365);
		}
	}

	@Test
	public void changeLogEnabled() throws Exception {
		Collection<CertificateAuthority> calist = manager.getCertificateAuthorities();
		CertificateAuthority ca = calist.stream().filter(c -> c.getDescription().equals(caName)).findFirst().get();

		SWTBotTree primaryTree = bot.tree();
		primaryTree.getTreeItem(caName).select();
		primaryTree.contextMenu("Certificate Authority Settings").click();

		bot.waitUntil(shellIsActive("Certificate Authority Settings"));
		SWTBotShell settings = bot.activeShell();

		try {
			bot.checkBox(1).click();
			bot.button("Save").click();
			bot.waitUntil(Conditions.shellCloses(settings));
			assertEquals(false, ca.isEnableLog());

		} finally {
			ca.setEnableLog(true);
		}
	}

	@Test
	public void changeSerialNumberInc() throws Exception {
		Collection<CertificateAuthority> calist = manager.getCertificateAuthorities();
		CertificateAuthority ca = calist.stream().filter(c -> c.getDescription().equals(caName)).findFirst().get();

		SWTBotTree primaryTree = bot.tree();
		primaryTree.getTreeItem(caName).select();
		primaryTree.contextMenu("Certificate Authority Settings").click();

		bot.waitUntil(shellIsActive("Certificate Authority Settings"));
		SWTBotShell settings = bot.activeShell();

		try {
			bot.checkBox(0).click();
			bot.button("Save").click();
			bot.waitUntil(Conditions.shellCloses(settings));
			assertEquals(false, ca.isIncrementalSerial());

		} finally {
			ca.setIncrementalSerial(true);
		}
	}

	@Test
	public void changeSignature() throws Exception {
		Collection<CertificateAuthority> calist = manager.getCertificateAuthorities();
		CertificateAuthority ca = calist.stream().filter(c -> c.getDescription().equals(caName)).findFirst().get();

		SWTBotTree primaryTree = bot.tree();
		primaryTree.getTreeItem(caName).select();
		primaryTree.contextMenu("Certificate Authority Settings").click();

		bot.waitUntil(shellIsActive("Certificate Authority Settings"));
		SWTBotShell settings = bot.activeShell();

		try {
			bot.comboBox().setSelection("SHA1withECDSA");
			bot.button("Save").click();
			bot.waitUntil(Conditions.shellCloses(settings));
			assertEquals(SignatureAlgorithm.SHA1withECDSA, ca.getSignatureAlgorithm());

		} finally {
			ca.setSignatureAlgorithm(SignatureAlgorithm.SHA512withECDSA);
		}
	}

}
