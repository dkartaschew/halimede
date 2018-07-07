/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2018 Darran Kartaschew 
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

package net.sourceforge.dkartaschew.halimede.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.security.Security;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.util.Date;
import java.util.Set;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.render.CRLRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.HTMLOutputRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCommercialCRLs {

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Test
	public void AusCERT() throws IOException, CRLException {
		test("AusCERTServerCA.crl", "C=AU,O=AusCERT,OU=Certificate Services,CN=AusCERT Server CA",
				"2018-01-12T03:59:25.000Z", 0x0c03);
	}

	@Test
	public void DFN_DE() throws IOException, CRLException {
		test("cdp1.pca.dfn.de.crl",
				"C=DE,ST=Berlin,L=Berlin,O=Beuth Hochschule fuer Technik Berlin,OU=Hochschulrechenzentrum,CN=Beuth Hochschule Berlin CA,E=pki@beuth-hochschule.de",
				"2018-01-17T20:24:09.000Z", 2221);
	}

	@Test
	public void GoDaddy() throws IOException, CRLException {
		test("godaddyextendedissuing.crl",
				"C=US,ST=Arizona,L=Scottsdale,O=GoDaddy.com\\, Inc.,OU=http://certificates.godaddy.com/repository,CN=Go Daddy Secure Certification Authority,SERIALNUMBER=07969287",
				"2018-01-14T11:05:28.000Z", 3434);
	}

	@Test
	public void GeoTrust_Global() throws IOException, CRLException {
		test("gtglobal.crl", "C=US,O=GeoTrust Inc.,CN=GeoTrust Global CA", "2018-03-31T08:19:38.000Z", -1);
	}

	@Test
	public void GeoTrust_True_CA1() throws IOException, CRLException {
		test("gttc64b.crl", "C=US,O=GeoTrust Inc,CN=GeoTrust True Credentials CA 1", "2018-07-19T22:20:51.000Z", -1);
	}

	@Test
	public void GeoTrust_True_CA2() throws IOException, CRLException {
		test("gttcca2.crl", "C=US,O=GeoTrust Inc.,CN=GeoTrust True Credentials CA 2", "2018-01-18T09:03:00.000Z", -1);
	}

	@Test
	public void ICA_DER() throws IOException, CRLException {
		test("ica.sz.5388.der.crl",
				"C=CZ,CN=I.CA Public CA/RSA 07/2015,O=První certifikační autorita\\, a.s.,SERIALNUMBER=NTRCZ-26439395",
				"2018-01-09T09:48:53.000Z", 5388);
	}

	@Test
	public void ICA_PEM() throws IOException, CRLException {
		test("ica.sz.5388.pem.crl",
				"C=CZ,CN=I.CA Public CA/RSA 07/2015,O=První certifikační autorita\\, a.s.,SERIALNUMBER=NTRCZ-26439395",
				"2018-01-09T09:48:53.000Z", 5388);
	}

	@Test
	public void Telstra() throws IOException, CRLException {
		test("Telstra RSS Issuing CA1.crl", "DC=com,DC=telstra,DC=dir,DC=core,CN=Telstra RSS Issuing CA1",
				"2015-08-21T23:04:07.000Z", 12582);
	}

	private void test(String filename, String subj, String nextUpdate, int serialNumber)
			throws IOException, CRLException {
		Path file = TestUtilities.getFile("CRLs" + File.separator + filename);
		assertNotNull(file);

		X509CRL crl = X509CRLEncoder.open(file);
		assertNotNull(crl);
		X509CRLHolder holder = new X509CRLHolder(crl.getEncoded());

		X500Name p = holder.getIssuer();
		Extension ext = holder.getExtension(Extension.cRLNumber);
		CRLNumber serial = null;
		if (ext != null) {
			serial = CRLNumber.getInstance(ext.getParsedValue());
		}
		Date nxt = crl.getNextUpdate();
		Date issue = crl.getThisUpdate();
		System.out.println(DateTimeUtil.toString(issue));
		assertEquals(subj, p.toString());
		if (serial != null)
			assertEquals(serialNumber, serial.getCRLNumber().intValue());
		assertEquals(nextUpdate, DateTimeUtil.toString(nxt));

		Set<? extends X509CRLEntry> entries = crl.getRevokedCertificates();
		if (entries != null)
			for (X509CRLEntry entry : entries) {
				Date d = entry.getRevocationDate();
				BigInteger s = entry.getSerialNumber();
				RevokeReasonCode r = RevokeReasonCode.forCRLReason(entry.getRevocationReason());
				System.out.println(
						String.format("0x%08x : %s : %s", s.longValue(), DateTimeUtil.toString(d), r.getDescription()));
			}

		// CRL
		CRLRenderer clrr = new CRLRenderer(new CRLProperties(null, crl));
		TextOutputRenderer txt = new TextOutputRenderer(new PrintStream(new TestUtilities.NullOutputStream()));
		clrr.render(txt);
		txt.finaliseRender();
		HTMLOutputRenderer html = new HTMLOutputRenderer(new PrintStream(new TestUtilities.NullOutputStream()), "");
		clrr.render(html);
		html.finaliseRender();

	}

}
