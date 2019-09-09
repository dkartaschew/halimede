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

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCreateCertificate {

	private static SWTWorkbenchBot bot;
	private static String tmp;
	private static String caName;
	private static CertificateAuthourityManager manager;

	@BeforeClass
	public static void setup() throws Exception {
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

	@Test
	public void createSimpleCertificate() throws Exception {

		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Pair").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView v = bot.activePart();
		long s = System.currentTimeMillis();
		while (!v.getId().startsWith(NewCertificateDetailsPart.ID)) {
			if (System.currentTimeMillis() - s > 5000) {
				throw new WidgetNotFoundException("View not found");
			}
			Thread.yield();
			v = bot.activePart();
		}

		System.out.println(v.getTitle());
		System.out.println(v.getId());

		bot.textWithLabel("Description:").setText(caName);
		bot.textWithLabel("Subject:").setText("CN=User");
		SWTBotToolbarDropDownButton btn2 = bot.toolbarDropDownButton();
		btn2.click();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

	}

}
