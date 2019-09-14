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
package net.sourceforge.dkartaschew.halimede.test.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.CertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCreateCertificate {

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
	public void createSimpleCertificate() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Pair").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

		String certDescription = "C_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

		bot.textWithLabel("Description:").setText(certDescription);
		bot.textWithLabel("Subject:").setText("CN=User");
		bot.comboBox(0).setSelection("RSA 512");
		bot.toolbarDropDownButton().click();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

		table.doubleClick(table.indexOf(certDescription), 0);

		SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CertificateDetailsPart.ID));

		try {
			SWTBotStyledText text = bot.styledText();
			List<String> lines = text.getLines();
			String caSubj = TestUtilities.createCASubjectName(caName);
			assertEquals(certDescription, lines.get(0));
			assertTrue(TestUtilities.hasKeyValue(lines, "Key Algorithm:", "RSA"));
			assertTrue(TestUtilities.hasKeyValue(lines, "Key Length/Size:", "512"));
			assertTrue(TestUtilities.hasKeyValue(lines, "Subject", "X.500 Name:", "CN=User"));
			assertTrue(TestUtilities.hasKeyValue(lines, "Issuer", "X.500 Name:", caSubj));
		} finally {
			v.close();
		}
	}

}
