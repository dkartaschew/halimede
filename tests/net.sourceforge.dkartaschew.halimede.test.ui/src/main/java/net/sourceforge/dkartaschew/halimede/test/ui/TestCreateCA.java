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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;

import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCreateCA {

	private static SWTBot bot;
	private static String tmp;

	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 10;
		bot = new SWTBot();
		tmp = TestUtilities.TMP;
	}

	@Test
	public void createCA() throws Exception {
		
		SWTBotTree primaryTree = bot.tree();
		
		primaryTree.contextMenu("Create a New Certificate Authority").click();
		
		assertFalse(bot.button("Create").isEnabled());
		
		bot.textWithLabel("Description:").setText("Test");
		bot.textWithLabel("Location:").setText(tmp);
		
		SWTBotButton createButton = bot.button("Create");
		SWTBotText subjectField = bot.textWithLabel("Subject:");
		SWTBotText passphrase1Field = bot.textWithLabel("Passphrase:");
		SWTBotText passphrase2Field = bot.textWithLabel("Confirmation:");
		
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
		
		passphrase1Field.setText("Password");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Passwor");
		assertFalse(createButton.isEnabled());
		passphrase2Field.setText("Password");
		assertTrue(createButton.isEnabled());
		createButton.click();
		
		bot.waitUntilWidgetAppears(Conditions.treeHasRows(primaryTree, 1));
		
		primaryTree.getTreeItem("Test").expand();
		primaryTree.getTreeItem("Test").getNode("CRLs").select();
		primaryTree.getTreeItem("Test").getNode("Issued").select();
		primaryTree.getTreeItem("Test").getNode("Pending").select();
		primaryTree.getTreeItem("Test").getNode("Revoked").select();
		primaryTree.getTreeItem("Test").getNode("Template").select();
	}

	@AfterClass
	public static void sleep() throws IOException {
		TestUtilities.cleanup(Paths.get(tmp, "Test"));
		SWTBotPreferences.PLAYBACK_DELAY = 0;
	}
}
