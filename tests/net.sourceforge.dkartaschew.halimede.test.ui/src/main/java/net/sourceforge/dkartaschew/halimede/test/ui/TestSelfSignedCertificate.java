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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotList;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.IIssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.data.PKCS12Decoder;
import net.sourceforge.dkartaschew.halimede.data.PKCS7Decoder;
import net.sourceforge.dkartaschew.halimede.data.PKCS8Decoder;
import net.sourceforge.dkartaschew.halimede.data.PublicKeyDecoder;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.ui.CertificateDetailsPart;
import net.sourceforge.dkartaschew.halimede.ui.NewSelfSignedCertificateDetailsPart;

@RunWith(SWTBotJunit4ClassRunner.class)
public class TestSelfSignedCertificate {

	private static SWTWorkbenchBot bot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		SWTBotPreferences.PLAYBACK_DELAY = 10;
		bot = new SWTWorkbenchBot(TestUtilities.getEclipseContext());
	}

	@AfterClass
	public static void cleanup() throws IOException {
		// NOP
	}

	@Before
	public void setup() {
		// NOP
	}

	@After
	public void closeShells() {
		SWTBotShell shell = bot.activeShell();
		while (!shell.getText().contains(PluginDefaults.APPLICATION_FULLNAME)) {
			shell.close();
			shell = bot.activeShell();
		}
	}

	/**
	 * Single unit test for all aspects of Self Signed Certificate
	 * 
	 * @throws Exception Test failure.
	 */
	@Test
	public void createSelfSignedCertificate() throws Exception {

		bot.menu("Utilities").menu("Create Self Signed Certificate").click();

		// Bot.waitUnitl we have a part relies on a running perspective...
		SWTBotView parent = TestUtilities.waitForActivePart(bot,
				Matchers.startsWith(NewSelfSignedCertificateDetailsPart.ID));
		try {
			String user = "SSC_" + Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);

			bot.textWithLabel("Subject:").setText("CN=" + user);
			bot.comboBox(0).setSelection("RSA 512");

			SWTBotTable keyusage = bot.tableInGroup("Key Usage");
			keyusage.getTableItem("Digital Signature").check();

			SWTBotTable extkeyusage = bot.tableInGroup("Extended Key Usage");
			extkeyusage.getTableItem("Code Signing").check();
			extkeyusage.getTableItem("Email Protection").check();

			SWTBotToolbarButton addBtn = bot.toolbarButtonInGroup("Subject Alternate Names", 0);
			SWTBotList sanTable = bot.listInGroup("Subject Alternate Names");
			assertEquals(0, sanTable.itemCount());

			addBtn.click();

			bot.waitUntil(shellIsActive("Subject Alternate Name"));
			SWTBotShell sh = bot.activeShell();

			SWTBotButton okBtn = bot.button("Update");
			assertFalse(okBtn.isEnabled());
			bot.text().setText("a@abc.com");
			bot.comboBox().setSelection("Email (RFC822)");
			assertTrue(okBtn.isEnabled());
			okBtn.click();
			bot.waitUntil(shellCloses(sh));

			assertEquals(1, sanTable.itemCount());

			// Create the certificate
			bot.toolbarDropDownButton().click();

			SWTBotView v = TestUtilities.waitForActivePart(bot, Matchers.startsWith(CertificateDetailsPart.ID));

			try {
				SWTBotStyledText text = bot.styledText();
				List<String> lines = text.getLines();
				assertEquals("CN=" + user, lines.get(0));
				assertTrue(TestUtilities.hasKeyValue(lines, "Key Algorithm:", "RSA"));
				assertTrue(TestUtilities.hasKeyValue(lines, "Key Length/Size:", "512"));
				assertTrue(TestUtilities.hasKeyValue(lines, "Subject", "X.500 Name:", "CN=" + user));
				assertTrue(TestUtilities.hasKeyValue(lines, "Issuer", "X.500 Name:", "CN=" + user));
				assertTrue(TestUtilities.hasKeyValue(lines, "Key Usage", "Usage:", "Digital Signature"));
				assertTrue(TestUtilities.hasKeyValue(lines, "Extended Key Usage", "Usage:", "Code Signing",
						"Email Protection"));
				assertTrue(TestUtilities.hasKeyValue(lines, "Alternate Names:", "Email (RFC822): a@abc.com"));
				assertFalse(TestUtilities.hasKeyValue(lines, "DNS: abc.com"));
				assertFalse(lines.contains("Basic Constraints:"));
				assertFalse(lines.contains("CRL Locations:"));

				MPart part = v.getPart();
				IssuedCertificateProperties certificate = (IssuedCertificateProperties) part.getTransientData()
						.get(CertificateDetailsPart.CERTIFICATE);
				IIssuedCertificate c = certificate.loadIssuedCertificate(null);

				// Haven't saved...
				assertTrue(v.getPart().isDirty());
				exportPKCS12("CN=" + user, c.getCertificateChain()[0]);
				
				// Have saved.
				assertFalse(v.getPart().isDirty());
				exportCertificate("CN=" + user, c.getCertificateChain()[0]);
				exportPrivateKey(c.getPrivateKey());
				exportPublicKey(c.getPublicKey());

			} finally {
				if (v.getPart().isVisible()) {
					if (v.getPart().isDirty()) {
						v.close();
						bot.waitUntil(shellIsActive("Save Material"));
						sh = bot.activeShell();
						bot.button("Don't Save").click();
					} else {
						v.close();
					}
				}
			}
		} finally {
			if (parent.getPart().isVisible()) {
				parent.close();
			}
		}
	}

	private void exportPKCS12(String subject, Certificate certificate)
			throws IOException, KeyStoreException, InvalidPasswordException {
		SWTBotToolbarDropDownButton btn = bot.toolbarDropDownButton();
		btn.menuItem("Export as PKCS#12 Keystore").click();

		bot.waitUntil(shellIsActive("Export Key Information (PKCS#12)"));

		bot.activeShell();
		/*
		 * No Password.
		 */
		assertFalse(bot.button("Export").isEnabled());
		Path filename = TestUtilities.constructTempFile("certificate.", ".p12");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DES3");
			// bot.text(1).setText("changeme");
			// bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Key Information Exported"));
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
			PKCS12Decoder privKey = PKCS12Decoder.open(filename, null);
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());

			X509Certificate cert = (X509Certificate) privKey.getCertificateChain()[0];
			assertEquals(subject, cert.getSubjectX500Principal().getName());
			assertEquals(subject, cert.getIssuerX500Principal().getName());

			assertEquals(certificate.getPublicKey(), cert.getPublicKey());
		} finally {
			TestUtilities.delete(filename);
		}

		/*
		 * Password
		 */

		btn = bot.toolbarDropDownButton();
		btn.menuItem("Export as PKCS#12 Keystore").click();

		bot.waitUntil(shellIsActive("Export Key Information (PKCS#12)"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		filename = TestUtilities.constructTempFile("certificate.", ".p12");
		try {
			bot.text().setText(filename.toString());
			bot.comboBox().setSelection("DES3");
			bot.text(1).setText("changeme");
			bot.text(2).setText("changeme");
			assertTrue(bot.button("Export").isEnabled());

			bot.button("Export").click();

			bot.waitUntil(shellIsActive("Key Information Exported"));
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
			PKCS12Decoder privKey = PKCS12Decoder.open(filename, "changeme");
			PrivateKey privateKey = privKey.getKeyPair().getPrivate();
			assertEquals("RSA", privateKey.getAlgorithm());

			X509Certificate cert = (X509Certificate) privKey.getCertificateChain()[0];
			assertEquals(subject, cert.getSubjectX500Principal().getName());
			assertEquals(subject, cert.getIssuerX500Principal().getName());

			assertEquals(certificate.getPublicKey(), cert.getPublicKey());
		} finally {
			TestUtilities.delete(filename);
		}
	}

	private void exportPublicKey(PublicKey pubKey) throws IOException {
		/*
		 * PEM
		 */
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
			assertEquals(512, KeyPairFactory.getKeyLength(publicKey));
			assertEquals(pubKey, publicKey);
		} finally {
			TestUtilities.delete(filename);
		}

		/*
		 * DER
		 */
		btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Public Key").click();

		bot.waitUntil(shellIsActive("Export Public Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		filename = TestUtilities.constructTempFile("certificate.", ".key");
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
			assertEquals(512, KeyPairFactory.getKeyLength(publicKey));
			assertEquals(pubKey, publicKey);
		} finally {
			TestUtilities.delete(filename);
		}

	}

	private void exportPrivateKey(PrivateKey expectedPrivateKey) throws InvalidPasswordException, IOException {
		/*
		 * PEM - No password
		 */
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
			assertEquals(expectedPrivateKey, privateKey);
		} finally {
			TestUtilities.delete(filename);
		}

		/*
		 * PEM - Password
		 */
		btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		filename = TestUtilities.constructTempFile("certificate.", ".key");
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
			assertEquals(expectedPrivateKey, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}

		/*
		 * DER - No Password
		 */
		btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		filename = TestUtilities.constructTempFile("certificate.", ".key");
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
			assertEquals(expectedPrivateKey, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}

		/*
		 * DER - Password
		 */
		btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Private Key").click();

		bot.waitUntil(shellIsActive("Export Private Key"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		filename = TestUtilities.constructTempFile("certificate.", ".key");
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
			assertEquals(expectedPrivateKey, privateKey);

		} finally {
			TestUtilities.delete(filename);
		}
	}

	private void exportCertificate(String subject, Certificate certificate) throws IOException {
		/*
		 * PEM
		 */
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
			X500Principal subjectDN = cert.getSubjectX500Principal();
			X500Principal expected = new X500Principal(subject);
			assertEquals(expected, subjectDN);
			assertEquals(certificate.getPublicKey(), cert.getPublicKey());
		} finally {
			TestUtilities.delete(filename);
		}

		/*
		 * DER
		 */
		btn = bot.toolbarDropDownButton();
		btn.menuItem("Export the Certificate").click();

		bot.waitUntil(shellIsActive("Export Certificate"));

		bot.activeShell();
		assertFalse(bot.button("Export").isEnabled());
		filename = TestUtilities.constructTempFile("certificate.", ".cer");
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
			X500Principal subjectDN = cert.getSubjectX500Principal();
			X500Principal expected = new X500Principal(subject);
			assertEquals(expected, subjectDN);
			assertEquals(certificate.getPublicKey(), cert.getPublicKey());
		} finally {
			TestUtilities.delete(filename);
		}
	}

}
