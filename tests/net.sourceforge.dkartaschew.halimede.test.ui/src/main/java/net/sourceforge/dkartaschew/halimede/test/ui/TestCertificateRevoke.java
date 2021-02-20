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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.data.X509CRLEncoder;
import net.sourceforge.dkartaschew.halimede.test.swtbot.SWTBotCDateTime;
import net.sourceforge.dkartaschew.halimede.ui.CRLDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.CertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCertificateRevoke {

	private static SWTWorkbenchBot bot;
	private static String tmp;
	private static String caName;
	private static CertificateAuthourityManager manager;

	@Inject
	private CertificateAuthourityManager holder;

	private static SWTBotView parent;

	private static Date next;

	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 10;
		tmp = TestUtilities.TMP;
		bot = new SWTWorkbenchBot(TestUtilities.getEclipseContext());
		caName = TestUtilities.createBasicCA(bot, tmp);
	}

	@AfterClass
	public static void cleanup() throws IOException {
		if (parent != null) {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}

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
	public void a001_revokeCertificate() throws Exception {
		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Pair").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

		String certDescription = "C_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
		String subjectDN = "CN=User-" + UUID.randomUUID().toString();

		bot.textWithLabel("Description:").setText(certDescription);
		bot.textWithLabel("Subject:").setText(subjectDN);
		bot.comboBox(0).setSelection("RSA 512");
		bot.toolbarDropDownButton().click();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

		table.doubleClick(table.indexOf(certDescription), 0);

		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Revoke the Certificate").click();

		bot.waitUntil(shellIsActive("Confirm Revoke"));

		bot.activeShell();
		assertTrue(bot.button("Revoke").isEnabled());

		bot.comboBox().setSelection("Superseded");
		bot.button("Revoke").click();

		bot.waitUntil(shellIsActive("Certificate Revoked"));
		bot.activeShell();
		bot.button("OK").click();

		primaryTree.getTreeItem(caName).getNode("Revoked").select();
		table = bot.table();
		table.doubleClick(table.indexOf(certDescription), 0);

		SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CertificateDetailsPart.ID));

		try {
			SWTBotStyledText text = bot.styledText();
			List<String> lines = text.getLines();
			String caSubj = TestUtilities.createCASubjectName(caName);
			assertEquals(certDescription, lines.get(0));
			assertTrue(TestUtilities.hasKeyValue(lines, "Subject", "X.500 Name:", subjectDN));
			assertTrue(TestUtilities.hasKeyValue(lines, "Issuer", "X.500 Name:", caSubj));
			assertTrue(TestUtilities.hasKeyValue(lines, "Revocation Reason:", "Superseded"));
		} finally {
			v.close();
		}
	}

	@Test
	public void a002_revokeCertificateViaContext() throws Exception {
		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		SWTBotTable table = bot.table();
		int tableRows = table.rowCount();

		// Create the Certificate View.
		primaryTree.contextMenu("Create New Client Key/Certificate Pair").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

		String certDescription = "C_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
		String subjectDN = "CN=User-" + UUID.randomUUID().toString();

		bot.textWithLabel("Description:").setText(certDescription);
		bot.textWithLabel("Subject:").setText(subjectDN);
		bot.comboBox(0).setSelection("RSA 512");
		bot.toolbarDropDownButton().click();

		primaryTree.getTreeItem(caName).getNode("Issued").select();
		bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

		table.select(table.indexOf(certDescription));
		table.contextMenu("Revoke Certificate").click();

		bot.waitUntil(shellIsActive("Confirm Revoke"));

		bot.activeShell();
		assertTrue(bot.button("Revoke").isEnabled());

		bot.comboBox().setSelection("Cessation Of Operation");
		bot.button("Revoke").click();

		bot.waitUntil(shellIsActive("Certificate Revoked"));
		bot.activeShell();
		bot.button("OK").click();

		primaryTree.getTreeItem(caName).getNode("Revoked").select();
		table = bot.table();
		table.doubleClick(table.indexOf(certDescription), 0);

		SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CertificateDetailsPart.ID));

		try {
			SWTBotStyledText text = bot.styledText();
			List<String> lines = text.getLines();
			String caSubj = TestUtilities.createCASubjectName(caName);
			assertEquals(certDescription, lines.get(0));
			assertTrue(TestUtilities.hasKeyValue(lines, "Subject", "X.500 Name:", subjectDN));
			assertTrue(TestUtilities.hasKeyValue(lines, "Issuer", "X.500 Name:", caSubj));
			assertTrue(TestUtilities.hasKeyValue(lines, "Revocation Reason:", "Cessation Of Operation"));
		} finally {
			v.close();
		}
	}

	@Test
	public void a003_createCRL() throws Exception {
		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("Revoked").select();
		primaryTree.contextMenu("Create CRL").click();

		bot.waitUntil(shellIsActive("Create CRL"));
		bot.activeShell();
		next = SWTBotCDateTime.get(bot, 0).getDate();
		bot.button("Create CRL").click();

		SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CRLDetailsPart.ID));

		try {
			SWTBotStyledText text = bot.styledText();
			List<String> lines = text.getLines();
			String caSubj = TestUtilities.createCASubjectName(caName);
			assertTrue(TestUtilities.hasKeyValue(lines, "Issuer:", caSubj));
			assertTrue(TestUtilities.hasKeyValue(lines, "Total Certificates Count:", "2"));
			assertTrue(TestUtilities.hasKeyValue(lines, "Revocation Reason:", "Superseded"));
			assertTrue(TestUtilities.hasKeyValue(lines, "Revocation Reason:", "Cessation Of Operation"));
		} finally {
			v.close();
		}
	}

	@Test
	public void a004_exportCRL_DER() throws Exception {
		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("CRLs").select();
		SWTBotTable table = bot.table();
		table.doubleClick(table.indexOf("1"), 0);

		SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CRLDetailsPart.ID));
		Path filename = TestUtilities.constructTempFile("crl.", ".crl");
		try {
			SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
			btn.menuItem("Export the CRL").click();

			bot.waitUntil(shellIsActive("Export CRL#1"));

			bot.activeShell();
			assertFalse(bot.button("Export").isEnabled());

			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("CRL Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			String caSubj = TestUtilities.createCASubjectName(caName);

			// Open and confirm it's ours.
			X509CRL crl = X509CRLEncoder.open(filename);
			assertEquals(2, crl.getRevokedCertificates().size());
			assertEquals(caSubj, crl.getIssuerX500Principal().toString());
			// second precision...
			assertEquals(next.getTime() / 1000, crl.getNextUpdate().getTime() / 1000);

		} finally {
			TestUtilities.delete(filename);
			v.close();
		}

	}

	@Test
	public void a005_exportCRL_PEM() throws Exception {
		SWTBotTree primaryTree = bot.tree();

		primaryTree.getTreeItem(caName).getNode("CRLs").select();
		SWTBotTable table = bot.table();
		table.doubleClick(table.indexOf("1"), 0);

		SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CRLDetailsPart.ID));
		Path filename = TestUtilities.constructTempFile("crl.", ".crl");
		try {
			SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
			btn.menuItem("Export the CRL").click();

			bot.waitUntil(shellIsActive("Export CRL#1"));

			bot.activeShell();
			assertFalse(bot.button("Export").isEnabled());

			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("CRL Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN X509 CRL-----", lines.get(0));

			String caSubj = TestUtilities.createCASubjectName(caName);

			// Open and confirm it's ours.
			X509CRL crl = X509CRLEncoder.open(filename);
			assertEquals(2, crl.getRevokedCertificates().size());
			assertEquals(caSubj, crl.getIssuerX500Principal().toString());
			// second precision...
			assertEquals(next.getTime() / 1000, crl.getNextUpdate().getTime() / 1000);

		} finally {
			TestUtilities.delete(filename);
			v.close();
		}

	}

}
