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

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;
import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
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
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.TemplateDetailsPart;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestTemplates {

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
	public void createSimpleTemplate() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Template").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Template").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));
		try {
			String certDescription = "T_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

			bot.textWithLabel("Description:").setText(certDescription);
			bot.textWithLabel("Subject:").setText("CN=User");
			bot.comboBox(0).setSelection("RSA 512");
			bot.toolbarDropDownButton().click();

			bot.waitUntil(shellIsActive("Template Updated"));
			bot.shell("Template Updated").close();

			primaryTree.getTreeItem(caName).getNode("Template").select();
			bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

			table.doubleClick(table.indexOf(certDescription), 0);

			SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

			try {
				assertEquals(certDescription, bot.textWithLabel("Description:").getText());
				assertEquals("CN=User", bot.textWithLabel("Subject:").getText());
				assertEquals("RSA 512", bot.comboBox(0).selection());
			} finally {
				v.close();
			}
		} finally {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}
	}

	@Test
	public void editSimpleTemplate() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Template").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Template").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));
		try {
			String certDescription = "T_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

			bot.textWithLabel("Description:").setText(certDescription);
			bot.textWithLabel("Subject:").setText("CN=User");
			bot.comboBox(0).setSelection("RSA 512");
			bot.toolbarDropDownButton().click();

			bot.waitUntil(shellIsActive("Template Updated"));
			bot.shell("Template Updated").close();

			primaryTree.getTreeItem(caName).getNode("Template").select();
			bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

			table.select(table.indexOf(certDescription));
			table.contextMenu("Edit Template").click();

			SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));

			try {
				assertEquals(certDescription, bot.textWithLabel("Description:").getText());
				assertEquals("CN=User", bot.textWithLabel("Subject:").getText());
				assertEquals("RSA 512", bot.comboBox(0).selection());

				bot.textWithLabel("Subject:").setText("CN=" + certDescription);

				bot.toolbarDropDownButton().click();

				bot.waitUntil(shellIsActive("Template Updated"));
				bot.shell("Template Updated").close();

				table.select(table.indexOf(certDescription));
				table.contextMenu("Edit Template").click();

				SWTBotView v2 = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));
				try {
					assertEquals(certDescription, bot.textWithLabel("Description:").getText());
					assertEquals("CN=" + certDescription, bot.textWithLabel("Subject:").getText());
					assertEquals("RSA 512", bot.comboBox(0).selection());
				} finally {
					v2.close();
				}
			} finally {
				if (v.getPart().isVisible()) {
					v.close();
				}
			}
		} finally {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}
	}

	@Test
	public void createKeyUsage() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Template").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Template").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));
		try {
			String certDescription = "T_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

			bot.textWithLabel("Description:").setText(certDescription);
			bot.textWithLabel("Subject:").setText("CN=User");
			bot.comboBox(0).setSelection("RSA 512");
			SWTBotTable keyusage = bot.tableInGroup("Key Usage");
			keyusage.getTableItem("Digital Signature").check();
			keyusage.getTableItem("CRL Signing").check();

			bot.toolbarDropDownButton().click();

			bot.waitUntil(shellIsActive("Template Updated"));
			bot.shell("Template Updated").close();

			primaryTree.getTreeItem(caName).getNode("Template").select();
			bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

			table.doubleClick(table.indexOf(certDescription), 0);

			SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

			try {
				assertEquals(certDescription, bot.textWithLabel("Description:").getText());
				assertEquals("CN=User", bot.textWithLabel("Subject:").getText());
				assertEquals("RSA 512", bot.comboBox(0).selection());

				keyusage = bot.tableInGroup("Key Usage");
				assertTrue(keyusage.getTableItem("Digital Signature").isChecked());
				assertTrue(keyusage.getTableItem("CRL Signing").isChecked());
				assertFalse(keyusage.getTableItem("Key Agreement").isChecked());
			} finally {
				v.close();
			}
		} finally {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}
	}

	@Test
	public void createExtendedKeyUsage() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Template").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Template").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));
		try {
			String certDescription = "T_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

			bot.textWithLabel("Description:").setText(certDescription);
			bot.textWithLabel("Subject:").setText("CN=User");
			bot.comboBox(0).setSelection("RSA 512");
			SWTBotTable keyusage = bot.tableInGroup("Extended Key Usage");
			keyusage.getTableItem("Code Signing").check();
			keyusage.getTableItem("Email Protection").check();

			bot.toolbarDropDownButton().click();

			bot.waitUntil(shellIsActive("Template Updated"));
			bot.shell("Template Updated").close();

			primaryTree.getTreeItem(caName).getNode("Template").select();
			bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

			table.doubleClick(table.indexOf(certDescription), 0);

			SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

			try {
				assertEquals(certDescription, bot.textWithLabel("Description:").getText());
				assertEquals("CN=User", bot.textWithLabel("Subject:").getText());
				assertEquals("RSA 512", bot.comboBox(0).selection());

				keyusage = bot.tableInGroup("Extended Key Usage");
				assertTrue(keyusage.getTableItem("Code Signing").isChecked());
				assertTrue(keyusage.getTableItem("Email Protection").isChecked());
				assertFalse(keyusage.getTableItem("Any Usage").isChecked());
			} finally {
				v.close();
			}
		} finally {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}
	}

	@Test
	public void createSubjectAltName() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Template").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Template").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));
		try {
			String certDescription = "T_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

			bot.textWithLabel("Description:").setText(certDescription);
			bot.textWithLabel("Subject:").setText("CN=User");
			bot.comboBox(0).setSelection("RSA 512");

			SWTBotButton addBtn = bot.buttonInGroup("Subject Alternate Names", 0);
			SWTBotList sanTable = bot.listInGroup("Subject Alternate Names");
			assertEquals(0, sanTable.itemCount());

			addBtn.click();

			bot.waitUntil(shellIsActive("Subject Alternate Name"));
			SWTBotShell sh = bot.activeShell();

			SWTBotButton okBtn = bot.button("Update");
			assertFalse(okBtn.isEnabled());
			bot.text().setText("abc.com");
			bot.comboBox().setSelection("DNS");
			assertTrue(okBtn.isEnabled());
			okBtn.click();
			bot.waitUntil(shellCloses(sh));

			assertEquals(1, sanTable.itemCount());

			bot.toolbarDropDownButton().click();

			bot.waitUntil(shellIsActive("Template Updated"));
			bot.shell("Template Updated").close();

			primaryTree.getTreeItem(caName).getNode("Template").select();
			bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

			table.doubleClick(table.indexOf(certDescription), 0);

			SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

			try {
				assertEquals(certDescription, bot.textWithLabel("Description:").getText());
				assertEquals("CN=User", bot.textWithLabel("Subject:").getText());
				assertEquals("RSA 512", bot.comboBox(0).selection());

				sanTable = bot.listInGroup("Subject Alternate Names");
				assertEquals(1, sanTable.itemCount());
				assertEquals("DNS: abc.com", sanTable.itemAt(0));
			} finally {
				v.close();
			}
		} finally {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}
	}
	
	@Test
	public void deleteTemplate() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Template").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Template").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(TemplateDetailsPart.ID));
		try {
			String certDescription = "T_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

			bot.textWithLabel("Description:").setText(certDescription);
			bot.textWithLabel("Subject:").setText("CN=User");
			bot.comboBox(0).setSelection("RSA 512");
			bot.toolbarDropDownButton().click();

			bot.waitUntil(shellIsActive("Template Updated"));
			bot.shell("Template Updated").close();

			primaryTree.getTreeItem(caName).getNode("Template").select();
			bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

			table.select(table.indexOf(certDescription));
			table.contextMenu("Delete Template").click();
			
			bot.waitUntil(shellIsActive("Confirm Delete"));
			bot.shell("Confirm Delete").setFocus();
			bot.button("Delete").click();
			
			bot.waitUntil(shellIsActive("Template Deletion"));
			bot.shell("Template Deletion").close();
			
			bot.waitUntil(Conditions.tableHasRows(table, tableRows));
		} finally {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}
	}
}
