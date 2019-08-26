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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRadio;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.dkartaschew.halimede.test.swtbot.SWTBotCDateTime;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCreateCA {

	private static SWTBot bot;
	private static String tmp;
	private static String keyMaterialPassword = "changeme";

	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 10;
		bot = new SWTBot();
		tmp = TestUtilities.TMP;
	}

	@Test
	public void createCA() throws Exception {

		SWTBotTree primaryTree = bot.tree();
		int rows = primaryTree.rowCount();
		
		primaryTree.contextMenu("Create a New Certificate Authority").click();

		bot.waitUntil(shellIsActive("Create Certificate Authority"));
		assertFalse(bot.button("Create").isEnabled());

		bot.textWithLabel("Description:").setText("Test");
		bot.textWithLabel("Location:").setText(tmp);

		SWTBotButton createButton = bot.button("Create");
		SWTBotText subjectField = bot.textWithLabel("Subject:");
		SWTBotText passphrase1Field = bot.textWithLabel("Passphrase:");
		SWTBotText passphrase2Field = bot.textWithLabel("Confirmation:");
		SWTBotCDateTime date1 = SWTBotCDateTime.get(bot, 0);
		SWTBotCDateTime date2 = SWTBotCDateTime.get(bot, 1);

		assertFalse(createButton.isEnabled());

		assertTrue(subjectField.getText().isEmpty());

		bot.button("...", 1).click();
		bot.waitUntil(Conditions.shellIsActive("X500 Name Assistant"));
		SWTBotShell x500Dialog = bot.activeShell();
		SWTBotButton updateButton = bot.button("Update");
		assertFalse(updateButton.isEnabled());
		bot.textWithLabel("Common Name (CN):").setText("TestCert");
		assertTrue(updateButton.isEnabled());
		bot.textWithLabel("Email Address (E):").setText("e@aol.com");
		bot.textWithLabel("Organizational Unit Name (OU):").setText("Test");
		assertTrue(updateButton.isEnabled());
		updateButton.click();

		bot.waitUntil(Conditions.shellCloses(x500Dialog));

		assertFalse(subjectField.getText().isEmpty());
		assertTrue(createButton.isEnabled());

		Date date = date1.getDate();
		date1.setDate(null);
		assertFalse(createButton.isEnabled());

		date1.setDate(date);
		assertTrue(createButton.isEnabled());

		date = date2.getDate();
		date2.setDate(null);
		assertFalse(createButton.isEnabled());

		date2.setDate(date);
		assertTrue(createButton.isEnabled());

		passphrase1Field.setText("Password");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Passwor");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Password");
		assertTrue(createButton.isEnabled());
		createButton.click();

		bot.waitUntilWidgetAppears(Conditions.treeHasRows(primaryTree, rows + 1));

		primaryTree.getTreeItem("Test").expand();
		primaryTree.getTreeItem("Test").getNode("CRLs").select();
		primaryTree.getTreeItem("Test").getNode("Issued").select();
		primaryTree.getTreeItem("Test").getNode("Pending").select();
		primaryTree.getTreeItem("Test").getNode("Revoked").select();
		primaryTree.getTreeItem("Test").getNode("Template").select();
		
		primaryTree.getTreeItem("Test").collapse();
	}
	
	@Test
	public void createCAExistingP12() throws Exception {

		SWTBotTree primaryTree = bot.tree();
		int rows = primaryTree.rowCount();

		primaryTree.contextMenu("Create a New Certificate Authority from Existing Certificate").click();

		bot.waitUntil(shellIsActive("Create Certificate Authority"));
		assertFalse(bot.button("Create").isEnabled());
		
		bot.textWithLabel("Description:").setText("Test2");
		bot.textWithLabel("Location:").setText(tmp);

		SWTBotButton createButton = bot.button("Create");
		SWTBotText passphrase1Field = bot.textWithLabel("Passphrase:");
		SWTBotText passphrase2Field = bot.textWithLabel("Confirmation:");
		SWTBotText pkcs12Field = bot.textWithLabel("Filename:");
		SWTBotText privKeyField = bot.textWithLabel("Private Key:");
		SWTBotText certField = bot.textWithLabel("Certificate:");
		
		SWTBotRadio pkcs12 = bot.radio("PKCS#12");
		SWTBotRadio certPriv = bot.radio("Certificate/Key Pair");
		
		assertTrue(pkcs12.isEnabled());
		assertTrue(certPriv.isEnabled());
		assertTrue(pkcs12Field.isEnabled());
		assertFalse(certField.isEnabled());
		assertFalse(privKeyField.isEnabled());
				
		pkcs12.click();
		assertFalse(createButton.isEnabled());
		pkcs12Field.setText(TestUtilities.getFile("dsa4096.p12").toString());
		assertTrue(createButton.isEnabled());
		
		passphrase1Field.setText("Password");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Passwor");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Password");
		assertTrue(createButton.isEnabled());
		
		createButton.click();
		
		bot.waitUntilWidgetAppears(shellIsActive("Keying Material Passphrase"));
		SWTBotShell passwordDialog = bot.activeShell();
		bot.text().setText(keyMaterialPassword);
		bot.button("OK").click();
		bot.waitUntil(shellCloses(passwordDialog));
		
		bot.waitUntilWidgetAppears(Conditions.treeHasRows(primaryTree, rows + 1));

		primaryTree.getTreeItem("Test2").expand();
		primaryTree.getTreeItem("Test2").getNode("CRLs").select();
		primaryTree.getTreeItem("Test2").getNode("Issued").select();
		primaryTree.getTreeItem("Test2").getNode("Pending").select();
		primaryTree.getTreeItem("Test2").getNode("Revoked").select();
		primaryTree.getTreeItem("Test2").getNode("Template").select();
		
		primaryTree.getTreeItem("Test2").collapse();
	}

	@Test
	public void createCAExistingCertKey() throws Exception {

		SWTBotTree primaryTree = bot.tree();
		int rows = primaryTree.rowCount();

		primaryTree.contextMenu("Create a New Certificate Authority from Existing Certificate").click();

		bot.waitUntil(shellIsActive("Create Certificate Authority"));
		assertFalse(bot.button("Create").isEnabled());
		
		bot.textWithLabel("Description:").setText("Test3");
		bot.textWithLabel("Location:").setText(tmp);

		SWTBotButton createButton = bot.button("Create");
		SWTBotText passphrase1Field = bot.textWithLabel("Passphrase:");
		SWTBotText passphrase2Field = bot.textWithLabel("Confirmation:");
		SWTBotText pkcs12Field = bot.textWithLabel("Filename:");
		SWTBotText privKeyField = bot.textWithLabel("Private Key:");
		SWTBotText certField = bot.textWithLabel("Certificate:");
		
		SWTBotRadio pkcs12 = bot.radio("PKCS#12");
		SWTBotRadio certPriv = bot.radio("Certificate/Key Pair");
		
		assertTrue(pkcs12.isEnabled());
		assertTrue(certPriv.isEnabled());
		assertTrue(pkcs12Field.isEnabled());
		assertFalse(certField.isEnabled());
		assertFalse(privKeyField.isEnabled());
				
		certPriv.click();
		assertFalse(createButton.isEnabled());
		certField.setText(TestUtilities.getFile("dsacert.pem").toString());
		privKeyField.setText(TestUtilities.getFile("dsa4096key_des3.pem").toString());
		assertTrue(createButton.isEnabled());
		
		passphrase1Field.setText("Password");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Passwor");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Password");
		assertTrue(createButton.isEnabled());
		
		createButton.click();
		
		bot.waitUntilWidgetAppears(shellIsActive("Keying Material Passphrase"));
		SWTBotShell passwordDialog = bot.activeShell();
		bot.text().setText(keyMaterialPassword);
		bot.button("OK").click();
		bot.waitUntil(shellCloses(passwordDialog));
		
		bot.waitUntilWidgetAppears(Conditions.treeHasRows(primaryTree, rows + 1));

		primaryTree.getTreeItem("Test3").expand();
		primaryTree.getTreeItem("Test3").getNode("CRLs").select();
		primaryTree.getTreeItem("Test3").getNode("Issued").select();
		primaryTree.getTreeItem("Test3").getNode("Pending").select();
		primaryTree.getTreeItem("Test3").getNode("Revoked").select();
		primaryTree.getTreeItem("Test3").getNode("Template").select();
		
		primaryTree.getTreeItem("Test3").collapse();
		
	}
	
	@Test
	public void testCreateCAMenu() {
		bot.menu("File").menu("Create New Certificate Authority").click();
		SWTBotShell shell = bot.shell("Create Certificate Authority");
		assertNotNull(shell);
		bot.sleep(2000);
		bot.button("Cancel").click();
	}
	
	@Test
	public void testCreateCAExistingMenu() {
		bot.menu("File").menu("Create New Certificate Authority from Existing Material").click();
		SWTBotShell shell = bot.shell("Create Certificate Authority");
		assertNotNull(shell);
		bot.sleep(2000);
		bot.button("Cancel").click();
	}
	
	@AfterClass
	public static void sleep() throws IOException {
		TestUtilities.cleanup(Paths.get(tmp, "Test"));
		TestUtilities.cleanup(Paths.get(tmp, "Test2"));
		TestUtilities.cleanup(Paths.get(tmp, "Test3"));
		SWTBotPreferences.PLAYBACK_DELAY = 0;
	}

}

