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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.render.CertificateRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.HTMLOutputRenderer;
import net.sourceforge.dkartaschew.halimede.data.render.TextOutputRenderer;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestCommercialCertificates {

	@BeforeClass
	public static void setup() {
		Security.addProvider(new BouncyCastleProvider());
	}

	@Test
	public void AddTrust() throws IOException {
		test("AddTrust External CA Root.cer",
				"CN=AddTrust External CA Root, OU=AddTrust External TTP Network, O=AddTrust AB, C=SE",
				"2000-05-30T10:48:38Z", "2020-05-30T10:48:38Z");
	}

	@Test
	public void Adobe() throws IOException {
		test("AdodeRootCertificate.cer",
				"CN=Adobe Systems Incorporated, OU=Information Systems, OU=Digital ID Class 3 - Microsoft Software Validation v2, O=Adobe Systems Incorporated, L=San Jose, ST=California, C=US",
				"2009-11-05T00:00:00Z", "2010-12-10T23:59:59Z");
	}

	@Test
	public void Certum() throws IOException {
		test("Certum Trusted Network CA.crt",
				"CN=Certum Trusted Network CA, OU=Certum Certification Authority, O=Unizeto Technologies S.A., C=PL",
				"2011-04-15T20:15:34Z", "2021-04-15T20:25:34Z");
	}

	@Test
	public void DTS() throws IOException {
		test("DTSInc.cer", 
				"CN=\"DTS, Inc.\", O=\"DTS, Inc.\", L=Calabasas, ST=California, C=US",
				"2012-01-20T00:00:00Z", "2015-01-28T12:00:00Z");
	}

	@Test
	public void DigiCert() throws IOException {
		test("DigiCert Assured ID Root CA.crt",
				"CN=DigiCert Assured ID Root CA, OU=www.digicert.com, O=DigiCert Inc, C=US", "2011-04-15T19:41:37Z",
				"2021-04-15T19:51:37Z");
	}

	@Test
	public void DigiCert2() throws IOException {
		test("DigiCert Global Root CA.crt", 
				"CN=DigiCert Global Root CA, OU=www.digicert.com, O=DigiCert Inc, C=US",
				"2011-04-15T19:48:35Z", "2021-04-15T19:58:35Z");
	}

	@Test
	public void DigiCert3() throws IOException {
		test("DigiCert High Assurance Code Signing.cer",
				"CN=DigiCert High Assurance Code Signing CA-1, OU=www.digicert.com, O=DigiCert Inc, C=US",
				"2011-02-10T12:00:00Z", "2026-02-10T12:00:00Z");
	}

	@Test
	public void DigiCert4() throws IOException {
		test("DigiCert High Assurance EV Root CA 2.cer",
				"CN=DigiCert High Assurance EV Root CA, OU=www.digicert.com, O=DigiCert Inc, C=US",
				"2010-01-13T19:20:32Z", "2015-09-30T18:19:47Z");
	}

	@Test
	public void DigiCert5() throws IOException {
		test("DigiCert High Assurance EV Root CA.crt",
				"CN=DigiCert High Assurance EV Root CA, OU=www.digicert.com, O=DigiCert Inc, C=US",
				"2011-04-15T19:45:33Z", "2021-04-15T19:55:33Z");
	}

	@Test
	public void Entrust() throws IOException {
		test("Entrust Certification Authority (2048).crt",
				"CN=Entrust.net Certification Authority (2048), OU=(c) 1999 Entrust.net Limited, OU=www.entrust.net/CPS_2048 incorp. by ref. (limits liab.), O=Entrust.net",
				"2011-04-15T19:52:17Z", "2021-04-15T20:02:17Z");
	}

	@Test
	public void GTE() throws IOException {
		test("GTE CyberTrust Root.cer",
				"CN=GTE CyberTrust Global Root, OU=\"GTE CyberTrust Solutions, Inc.\", O=GTE Corporation, C=US",
				"1998-08-13T00:29:00Z", "2018-08-13T23:59:00Z");
	}

	@Test
	public void GeoTrust() throws IOException {
		test("GeoTrust Primary Certification Authority 2.cer",
				"OU=Equifax Secure Certificate Authority, O=Equifax, C=US", "1998-08-22T16:41:51Z",
				"2018-08-22T16:41:51Z");
	}

	@Test
	public void GeoTrust2() throws IOException {
		test("GeoTrust Primary Certification Authority.cer",
				"CN=GeoTrust Primary Certification Authority, O=GeoTrust Inc., C=US", "2011-02-22T19:35:51Z",
				"2021-02-22T19:45:51Z");
	}

	@Test
	public void GeoTrust3() throws IOException {
		test("GeoTrust Primary Certification Authority - G3.cer",
				"CN=GeoTrust Primary Certification Authority - G3, OU=(c) 2008 GeoTrust Inc. - For authorized use only, O=GeoTrust Inc., C=US",
				"2011-02-22T19:40:44Z", "2021-02-22T19:50:44Z");
	}

	@Test
	public void GlobalSign() throws IOException {
		test("GlobalSign.cer", 
				"CN=GlobalSign Root CA, OU=Root CA, O=GlobalSign nv-sa, C=BE", "1998-09-01T12:00:00Z",
				"2028-01-28T12:00:00Z");
	}

	@Test
	public void GlobalSign2() throws IOException {
		test("GlobalSign Root CA2.cer", 
				"CN=GlobalSign Root CA, OU=Root CA, O=GlobalSign nv-sa, C=BE",
				"1998-09-01T12:00:00Z", "2028-01-28T12:00:00Z");
	}

	@Test
	public void GlobalSign3() throws IOException {
		test("GlobalSign Root CA.crt", 
				"CN=GlobalSign Root CA, OU=Root CA, O=GlobalSign nv-sa, C=BE",
				"2011-04-15T19:55:08Z", "2021-04-15T20:05:08Z");
	}

	@Test
	public void GoDaddy() throws IOException {
		test("Go Daddy Root Certificate Authority - G2.crt",
				"CN=Go Daddy Root Certificate Authority - G2, O=\"GoDaddy.com, Inc.\", L=Scottsdale, ST=Arizona, C=US",
				"2011-04-15T19:57:40Z", "2021-04-15T20:07:40Z");
	}

	@Test
	public void Intel() throws IOException {
		test("IntelCodeSigning1.cer",
				"CN=Intel Corporation-Mobile Wireless Group, OU=Mobile Wireless Group, O=Intel Corporation",
				"2011-12-16T23:53:56Z", "2014-11-30T23:53:56Z");
	}

	@Test
	public void Intel2() throws IOException {
		test("IntelCodeSigning2.cer", 
				"CN=Intel External Basic Issuing CA 3B, O=Intel Corporation, C=US",
				"2009-05-15T19:27:26Z", "2015-05-15T19:37:26Z");
	}

	@Test
	public void Intel3() throws IOException {
		test("IntelCodeSigning3.cer", 
				"CN=Intel External Basic Policy CA, O=Intel Corporation, C=US",
				"2006-02-16T18:01:30Z", "2016-02-19T18:01:30Z");
	}

	@Test
	public void Microsoft() throws IOException {
		test("MicCodSigPCA_08-31-2010.crt",
				"CN=Microsoft Code Signing PCA, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2010-08-31T22:19:32Z", "2020-08-31T22:29:32Z");
	}

	@Test
	public void Microsoft2() throws IOException {
		test("MicrosoftSecureBoot2.cer",
				"CN=Microsoft Corporation UEFI CA 2011, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2011-06-27T21:22:45Z", "2026-06-27T21:32:45Z");
	}

	@Test
	public void Microsoft3() throws IOException {
		test("MicrosoftWindowsAppStore.cer",
				"CN=Microsoft Corporation, OU=MOPR, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2013-09-24T17:41:41Z", "2014-12-24T17:41:41Z");
	}

	@Test
	public void Microsoft4() throws IOException {
		test("MicrosoftWindows.cer", 
				"CN=Microsoft Windows, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2013-06-17T21:43:38Z", "2014-09-17T21:43:38Z");
	}

	@Test
	public void Microsoft5() throws IOException {
		test("MicrosoftWindowsProductionCA.cer",
				"CN=Microsoft Windows Production PCA 2011, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2011-10-19T18:41:42Z", "2026-10-19T18:51:42Z");
	}

	@Test
	public void Microsoft6() throws IOException {
		test("Microsoft3rdPartyUEFICertificate.cer",
				"CN=Microsoft Corporation Third Party Marketplace Root, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2010-10-05T22:02:28Z", "2035-10-05T22:09:33Z");
	}

	@Test
	public void Microsoft7() throws IOException {
		test("Microsoft Code Signing 2011.cer",
				"CN=Microsoft Code Signing PCA 2011, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2011-07-08T20:59:09Z", "2026-07-08T21:09:09Z");
	}

	@Test
	public void Microsoft8() throws IOException {
		test("Microsoft Root Authority 2.cer",
				"CN=Microsoft Root Authority, OU=Microsoft Corporation, OU=Copyright (c) 1997 Microsoft Corp.",
				"1997-01-10T07:00:00Z", "2020-12-31T07:00:00Z");
	}

	@Test
	public void Microsoft9() throws IOException {
		test("Microsoft Root Authority 3.cer",
				"CN=Microsoft Root Certificate Authority 2010, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2010-06-23T21:57:24Z", "2035-06-23T22:04:01Z");
	}

	@Test
	public void Microsoft10() throws IOException {
		test("Microsoft Root Authority 4.cer",
				"CN=Microsoft Root Certificate Authority 2011, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2011-03-22T22:05:28Z", "2036-03-22T22:13:04Z");
	}

	@Test
	public void Microsoft11() throws IOException {
		test("Microsoft Root Authority.cer",
				"CN=Microsoft Root Authority, OU=Microsoft Corporation, OU=Copyright (c) 1997 Microsoft Corp.",
				"1997-01-10T07:00:00Z", "2020-12-31T07:00:00Z");
	}

	@Test
	public void Microsoft12() throws IOException {
		test("MicrosoftRootCert.crt", 
				"CN=Microsoft Root Certificate Authority, DC=microsoft, DC=com",
				"2001-05-09T23:19:22Z", "2021-05-09T23:28:13Z");
	}

	@Test
	public void Microsoft13() throws IOException {
		test("MicrosoftSecureBoot1.cer",
				"CN=Microsoft Windows Production PCA 2011, O=Microsoft Corporation, L=Redmond, ST=Washington, C=US",
				"2011-10-19T18:41:42Z", "2026-10-19T18:51:42Z");
	}

	@Test
	public void Neterion() throws IOException {
		test("Neterion Inc.cer", 
				"CN=Neterion.com", 
				"2006-10-31T02:10:12Z", "2039-12-31T23:59:59Z");
	}

	@Test
	public void	NetLock() throws IOException {
		test("NetLock Arany (Class Gold).crt",
				"CN=NetLock Arany (Class Gold) Főtanúsítvány, OU=Tanúsítványkiadók (Certification Services), O=NetLock Kft., L=Budapest, C=HU",
				"2011-04-15T20:08:01Z", "2021-04-15T20:18:01Z");
	}

	@Test
	public void	NetLock2() throws IOException {
		test("NetLock Platina (Class Platinum).crt",
				"CN=NetLock Platina (Class Platinum) Főtanúsítvány, OU=Tanúsítványkiadók (Certification Services), O=NetLock Kft., L=Budapest, C=HU",
				"2011-04-15T20:09:41Z", "2021-04-15T20:19:41Z");
	}

	@Test
	public void Security() throws IOException {
		test("Security Communication RootCA1.crt", 
				"OU=Security Communication RootCA1, O=SECOM Trust.net, C=JP",
				"2011-04-15T20:11:37Z", "2021-04-15T20:21:37Z");
	}

	@Test
	public void Starfield() throws IOException {
		test("Starfield Root Certificate Authority - G2.crt",
				"CN=Starfield Root Certificate Authority - G2, O=\"Starfield Technologies, Inc.\", L=Scottsdale, ST=Arizona, C=US",
				"2011-04-15T19:59:31Z", "2021-04-15T20:09:31Z");
	}

	@Test
	public void StartCom() throws IOException {
		test("StartCom Certification Authority.crt",
				"CN=StartCom Certification Authority, OU=Secure Digital Certificate Signing, O=StartCom Ltd., C=IL",
				"2011-04-15T20:13:19Z", "2021-04-15T20:23:19Z");
	}

	@Test
	public void TrustCenter() throws IOException {
		test("TC TrustCenter Class 2 CA II.crt",
				"CN=TC TrustCenter Class 2 CA II, OU=TC TrustCenter Class 2 CA, O=TC TrustCenter GmbH, C=DE",
				"2011-04-11T21:51:45Z", "2021-04-11T22:01:45Z");
	}

	@Test
	public void Thawte() throws IOException {
		test("Thawte Premium Server CA.cer",
				"EMAILADDRESS=premium-server@thawte.com, CN=Thawte Premium Server CA, OU=Certification Services Division, O=Thawte Consulting cc, L=Cape Town, ST=Western Cape, C=ZA",
				"1996-08-01T00:00:00Z", "2021-01-01T23:59:59Z");
	}

	@Test
	public void Thawte2() throws IOException {
		test("thawte Primary Root CA.cer",
				"CN=thawte Primary Root CA, OU=\"(c) 2006 thawte, Inc. - For authorized use only\", OU=Certification Services Division, O=\"thawte, Inc.\", C=US",
				"2011-02-22T19:31:57Z", "2021-02-22T19:41:57Z");
	}

	@Test
	public void Thawte3() throws IOException {
		test("thawte Primary Root CA - G3.cer",
				"CN=thawte Primary Root CA - G3, OU=\"(c) 2008 thawte, Inc. - For authorized use only\", OU=Certification Services Division, O=\"thawte, Inc.\", C=US",
				"2011-02-22T19:44:03Z", "2021-02-22T19:54:03Z");
	}

	@Test
	public void VeriSign() throws IOException {
		test("VeriSign Class 3 Public Primary CA.cer",
				"OU=Class 3 Public Primary Certification Authority, O=\"VeriSign, Inc.\", C=US", "1996-01-29T00:00:00Z",
				"2028-08-01T23:59:59Z");
	}

	@Test
	public void VeriSign2() throws IOException {
		test("VeriSign Class 3 Public Primary Certification Authority - G5.cer",
				"CN=VeriSign Class 3 Public Primary Certification Authority - G5, OU=\"(c) 2006 VeriSign, Inc. - For authorized use only\", OU=VeriSign Trust Network, O=\"VeriSign, Inc.\", C=US",
				"2011-02-22T19:25:17Z", "2021-02-22T19:35:17Z");
	}

	@Test
	public void VeriSign3() throws IOException {
		test("VeriSign Class 3 Public Primary Certification Authority - MD2.cer",
				"OU=Class 3 Public Primary Certification Authority, O=\"VeriSign, Inc.\", C=US", "1996-01-29T00:00:00Z",
				"2028-08-01T23:59:59Z");
	}

	@Test
	public void VeriSign4() throws IOException {
		test("VeriSign Code Signing.cer",
				"CN=VeriSign Class 3 Code Signing 2009-2 CA, OU=Terms of use at https://www.verisign.com/rpa (c)09, OU=VeriSign Trust Network, O=\"VeriSign, Inc.\", C=US",
				"2009-05-21T00:00:00Z", "2019-05-20T23:59:59Z");
	}

	@Test
	public void VeriSign5() throws IOException {
		test("VeriSign Universal Root Certification Authority.cer",
				"CN=VeriSign Universal Root Certification Authority, OU=\"(c) 2008 VeriSign, Inc. - For authorized use only\", OU=VeriSign Trust Network, O=\"VeriSign, Inc.\", C=US",
				"2011-02-22T19:46:39Z", "2021-02-22T19:56:39Z");
	}

	@Test
	public void NVIDIA() throws IOException {
		test("nVidia Corporation.cer", 
				"CN=NVIDIA CORPORATION", 
				"2012-08-14T02:44:04Z", "2013-08-14T02:54:04Z");
	}

	@Test
	public void NVIDIA2() throws IOException {
		test("nVidia Primary CA - 2012.cer", 
				"CN=NVIDIA Subordinate CA, DC=nvidia, DC=com", "2012-08-10T00:04:04Z",
				"2014-08-10T00:14:04Z");
	}

	@Test
	public void NVIDIA3() throws IOException {
		test("nVidia Primary CA.cer", "CN=NVIDIA Subordinate CA, DC=nvidia, DC=com", 
				"2012-08-10T00:04:04Z",
				"2014-08-10T00:14:04Z");
	}

	private void test(String filename, String subj, String start, String expiry) throws IOException {
		Path file = TestUtilities.getFile("Certificates" + File.separator + filename);
		assertNotNull(file);
		
		PKCS7Decoder decoder = PKCS7Decoder.open(file);
		Certificate[] certificate = decoder.getCertificateChain();
		
		X509Certificate cert = (X509Certificate) certificate[0];
		assertNotNull(cert);
		
		X500Principal p = cert.getSubjectX500Principal();
		X500Principal e = new X500Principal(subj);
		assertEquals(e.getName(), p.getName());
		
		assertEquals(Date.from(ZonedDateTime.parse(start).toInstant()), cert.getNotBefore());
		
		assertEquals(Date.from(ZonedDateTime.parse(expiry).toInstant()), cert.getNotAfter());
		
		// Request 1
		IIssuedCertificate ce = new IssuedCertificate(null, certificate, Paths.get(filename), null, null);
		
		CertificateRenderer cert1 = new CertificateRenderer(new IssuedCertificateProperties(null, ce));
		TextOutputRenderer txt = new TextOutputRenderer(System.out);
		cert1.render(txt);
		txt.finaliseRender();
		HTMLOutputRenderer html = new HTMLOutputRenderer(System.out, "");
		cert1.render(html);
		html.finaliseRender();
	}

}
