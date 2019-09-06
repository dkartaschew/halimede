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

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellIsActive;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.matchers.WithPartId;
import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.hamcrest.core.StringStartsWith;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.BundleContext;

import net.sourceforge.dkartaschew.halimede.e4rcp.Activator;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCreateCertificate {

	private static SWTWorkbenchBot bot;
	private static String tmp;
	private static List<String> cas = new ArrayList<>();

	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 10;
		tmp = TestUtilities.TMP;
	}

	@AfterClass
	public static void sleep() throws IOException {
		cas.forEach(p -> {
			try {
				TestUtilities.cleanup(Paths.get(tmp, p));
			} catch (IOException e) {
				// Ignore.
			}
		});
		SWTBotPreferences.PLAYBACK_DELAY = 0;
	}

	@Test
	public void createCertificate() throws Exception {
		bot = new SWTWorkbenchBot(getEclipseContext());

		final String caName = "Test-" + UUID.randomUUID().toString();
		cas.add(caName);

		SWTBotTree primaryTree = bot.tree();
		int rows = primaryTree.rowCount();

		primaryTree.contextMenu("Create a New Certificate Authority").click();

		bot.waitUntil(shellIsActive("Create Certificate Authority"));
		bot.textWithLabel("Description:").setText(caName);
		bot.textWithLabel("Location:").setText(tmp);
		bot.textWithLabel("Subject:").setText("CN=" + caName.replace("-", ""));
		bot.textWithLabel("Passphrase:").setText("Password");
		bot.textWithLabel("Confirmation:").setText("Password");
		bot.button("Create").click();

		bot.waitUntilWidgetAppears(Conditions.treeHasRows(primaryTree, rows + 1));

		primaryTree.getTreeItem(caName).expand();
		primaryTree.getTreeItem(caName).getNode("Issued").select();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Pair").click();

		bot.waitUntil(Conditions.waitForPart(getEclipseContext(),
				WithPartId.withPartId(StringStartsWith.startsWith(NewCertificateDetailsPart.ID))));

		SWTBotView v = bot.activePart();
		System.out.println(v.getTitle());
		System.out.println(v.getId());

		bot.textWithLabel("Description:").setText(caName);
		bot.textWithLabel("Subject:").setText("CN=User");
		SWTBotToolbarDropDownButton btn2 = bot.toolbarDropDownButton();
		btn2.click();

		primaryTree.getTreeItem(caName).collapse();
	}

	protected static IEclipseContext getEclipseContext() {
		BundleContext bndCtx = Activator.getDefault().getContext();
		IEclipseContext rootContext = EclipseContextFactory.getServiceContext(bndCtx);
		IEclipseContext app = rootContext.get(IWorkbench.class).getApplication().getContext();
		return app;
	}

}
