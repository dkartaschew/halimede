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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.swtbot.e4.finder.waits.Conditions;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
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
import net.sourceforge.dkartaschew.halimede.data.IIssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.PKCS7Decoder;
import net.sourceforge.dkartaschew.halimede.data.PKCS8Decoder;
import net.sourceforge.dkartaschew.halimede.data.PublicKeyDecoder;
import net.sourceforge.dkartaschew.halimede.ui.CertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.NewCertificateDetailsPart;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestCertificateExport {

	private static SWTWorkbenchBot bot;
	private static String tmp;
	private static String caName;
	private static CertificateAuthourityManager manager;

	@Inject
	private CertificateAuthourityManager holder;

	private static SWTBotView certificateView;
	private static SWTBotView parent;

	private static String subjectDN;

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

		if (certificateView != null) {
			if (certificateView.getPart().isVisible()) {
				certificateView.close();
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

			if (certificateView == null) {
				SWTBotTree primaryTree = bot.tree();

				primaryTree.getTreeItem(caName).getNode("Issued").select();
				SWTBotTable table = bot.table();
				int tableRows = table.rowCount();

				// Create the Certificate View.
				primaryTree.contextMenu("Create New Client Key/Certificate Pair").click();

				// Bot.waitUnitl we have a part relies on a running perspective...
				parent = TestUtilities.waitForActivePart(bot, Matchers.startsWith(NewCertificateDetailsPart.ID));

				String certDescription = "C_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
				subjectDN = "CN=User-" + UUID.randomUUID().toString();

				bot.textWithLabel("Description:").setText(certDescription);
				bot.textWithLabel("Subject:").setText(subjectDN);
				bot.comboBox(0).setSelection("RSA 512");
				bot.toolbarDropDownButton().click();

				primaryTree.getTreeItem(caName).getNode("Issued").select();
				bot.waitUntil(Conditions.tableHasRows(table, tableRows + 1));

				table.doubleClick(table.indexOf(certDescription), 0);

				certificateView = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CertificateDetailsPart.ID));
			}
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
	public void exportCertificatePEM() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Certificate").click();

		bot.waitUntil(shellIsActive("Export Certificate"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".cer");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Certificate(s) Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN CERTIFICATE-----", lines.get(0));

			// Open and confirm it's ours.
			PKCS7Decoder pkcs7 = PKCS7Decoder.open(filename);
			Certificate[] certs = pkcs7.getCertificateChain();
			assertEquals(1, certs.length);
			X509Certificate cert = (X509Certificate) certs[0];
			X500Principal subject = cert.getSubjectX500Principal();
			X500Principal expected = new X500Principal(subjectDN);
			assertEquals(expected, subject);
		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportCertificateDER() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Certificate").click();

		bot.waitUntil(shellIsActive("Export Certificate"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".cer");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Certificate(s) Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			// Open and confirm it's ours.
			PKCS7Decoder pkcs7 = PKCS7Decoder.open(filename);
			Certificate[] certs = pkcs7.getCertificateChain();
			assertEquals(1, certs.length);
			X509Certificate cert = (X509Certificate) certs[0];
			X500Principal subject = cert.getSubjectX500Principal();
			X500Principal expected = new X500Principal(subjectDN);
			assertEquals(expected, subject);
		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportCertificateChainPEM() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Certificate Chain").click();

		bot.waitUntil(shellIsActive("Export Certificate"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".cer");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Certificate(s) Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN CERTIFICATE-----", lines.get(0));

			// Open and confirm it's ours.
			PKCS7Decoder pkcs7 = PKCS7Decoder.open(filename);
			Certificate[] certs = pkcs7.getCertificateChain();
			assertEquals(2, certs.length);
			X509Certificate cert = (X509Certificate) certs[0];
			X500Principal subject = cert.getSubjectX500Principal();
			X500Principal expected = new X500Principal(subjectDN);
			assertEquals(expected, subject);
		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportCertificateChainDER() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Certificate Chain").click();

		bot.waitUntil(shellIsActive("Export Certificate"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".cer");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Certificate(s) Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			// Open and confirm it's ours.
			PKCS7Decoder pkcs7 = PKCS7Decoder.open(filename);
			Certificate[] certs = pkcs7.getCertificateChain();
			assertEquals(2, certs.length);
			X509Certificate cert = (X509Certificate) certs[0];
			X500Principal subject = cert.getSubjectX500Principal();
			X500Principal expected = new X500Principal(subjectDN);
			assertEquals(expected, subject);
		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPublicKeyPEM() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Public Key").click();

		bot.waitUntil(shellIsActive("Export Public Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Public Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN PUBLIC KEY-----", lines.get(0));

			// Open and confirm it's ours.
			PublicKey publicKey = PublicKeyDecoder.open(filename);
			assertEquals("RSA", publicKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PublicKey k = ic.getPublicKey();
			assertEquals(k, publicKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPublicKeyDER() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Public Key").click();

		bot.waitUntil(shellIsActive("Export Public Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Public Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			// Open and confirm it's ours.
			PublicKey publicKey = PublicKeyDecoder.open(filename);
			assertEquals("RSA", publicKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PublicKey k = ic.getPublicKey();
			assertEquals(k, publicKey);
		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyPEMNoPassword() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN PRIVATE KEY-----", lines.get(0));

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, null);
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyPEMPassword_AES() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			bot.comboBox(1).setSelection("AES_256_CBC");
			assertTrue(bot.button("Export").isEnabled());

			bot.text(1).setText("changeme");
			assertFalse(bot.button("Export").isEnabled());
			bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.text(1).setText("changeme1");
			assertFalse(bot.button("Export").isEnabled());

			bot.text(1).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN ENCRYPTED PRIVATE KEY-----", lines.get(0));

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, "changeme");
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyPEMPassword_DES() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			bot.comboBox(1).setSelection("DES3_CBC");
			bot.text(1).setText("changeme");
			bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN ENCRYPTED PRIVATE KEY-----", lines.get(0));

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, "changeme");
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyPEMPassword_RC2() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("PEM");
			bot.comboBox(1).setSelection("PBE_SHA1_RC2_40");
			bot.text(1).setText("changeme");
			bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			List<String> lines = Files.readAllLines(filename, StandardCharsets.UTF_8);
			assertEquals("-----BEGIN ENCRYPTED PRIVATE KEY-----", lines.get(0));

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, "changeme");
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyDERNoPassword() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, null);
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyDERPassword_AES() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			bot.comboBox(1).setSelection("AES_256_CBC");
			assertTrue(bot.button("Export").isEnabled());

			bot.text(1).setText("changeme");
			assertFalse(bot.button("Export").isEnabled());
			bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.text(1).setText("changeme1");
			assertFalse(bot.button("Export").isEnabled());

			bot.text(1).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, "changeme");
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyDERPassword_DES() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			bot.comboBox(1).setSelection("DES3_CBC");
			bot.text(1).setText("changeme");
			bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, "changeme");
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	@Test
	public void exportPrivateKeyDERPassword_RC2() throws Exception {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".key");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DER");
			bot.comboBox(1).setSelection("PBE_SHA1_RC2_40");
			bot.text(1).setText("changeme");
			bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Private Key Exported"));
			bot.activeShell();
			bot.button("OK").click();

			// Now check out file is PEM and a single certificate.
			byte[] data = Files.readAllBytes(filename);
			assertNotEquals('-', data[0]);
			assertNotEquals('-', data[1]);
			assertNotEquals('-', data[2]);
			assertNotEquals('-', data[3]);
			assertNotEquals('-', data[4]);

			// Open and confirm it's ours.
			PKCS8Decoder privKey = PKCS8Decoder.open(filename, "changeme");
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());
			CertificateAuthority ca = manager.getCertificateAuthorities().stream()
					.filter(c -> c.getDescription().equals(caName)).findFirst().get();
			IssuedCertificateProperties is = ca.getIssuedCertificates().stream()
					.filter(c -> c.getProperty(Key.subject).equals(subjectDN)).findFirst().get();
			IIssuedCertificate ic = is.loadIssuedCertificate("");
			PrivateKey k = ic.getPrivateKey();
			assertEquals(k, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}
}
