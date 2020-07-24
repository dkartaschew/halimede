/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZonedDateTime;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPropertiesStore {

	private final Path fn = Paths.get(TestUtilities.TMP , "store.properties");

	@Test
	public void simple() throws Throwable {
		try {
			IssuedCertificateProperties p = new IssuedCertificateProperties(null);
			p.setProperty(Key.description, "description");
			p.setProperty(Key.filename, fn.toString());
			storeAndReload(p);
		} finally {
			TestUtilities.delete(fn);
		}
	}

	@Test
	public void simple2() throws Throwable {
		try {
			IssuedCertificateProperties p = new IssuedCertificateProperties(null);
			p.setProperty(Key.description, "description");
			p.setProperty(Key.filename, fn.toString());
			p.setProperty(Key.endDate, Instant.now().toString());
			storeAndReload(p);
		} finally {
			TestUtilities.delete(fn);
		}
	}

	@Test
	public void multiline() throws Throwable {
		try {
			IssuedCertificateProperties p = new IssuedCertificateProperties(null);
			p.setProperty(Key.description, "description");
			p.setProperty(Key.filename, fn.toString());
			p.setProperty(Key.comments, "Line 1\nLine 2\nLine 3");
			storeAndReload(p);
		} finally {
			TestUtilities.delete(fn);
		}
	}

	@Test
	public void equals() throws Throwable {
		IssuedCertificateProperties p = new IssuedCertificateProperties(null);
		p.setProperty(Key.description, "description");
		p.setProperty(Key.filename, "/tmp/store.properties");
		p.setProperty(Key.comments, "Line 1\nLine 2\nLine 3");

		IssuedCertificateProperties p2 = new IssuedCertificateProperties(null);
		p2.setProperty(Key.description, "description2");
		p2.setProperty(Key.filename, "/tmp/store.properties");
		p2.setProperty(Key.comments, "Line 1\nLine 2\nLine 3");

		assertNotEquals(p, p2);
		assertFalse(p.equals(null));
		assertFalse(p.equals(new Object()));
		assertTrue(p.equals(p));
		assertNotEquals(p.hashCode(), p2.hashCode());
		
		assertFalse(p.hasIssuedCertificate());
		
		assertNull(p.getCertificateAuthority());
		assertEquals("description", p.toString());
		
		p2.setProperty(Key.description, null);
		assertEquals("Issued Certificate Properties: 1074567345", p2.toString());
	}
	
	@Test
	public void comparison() throws Throwable {
		IssuedCertificateProperties p = new IssuedCertificateProperties(null);
		IssuedCertificateProperties p2 = new IssuedCertificateProperties(null);

		assertEquals(0, p.compare(p, p2));
		assertEquals(0, p.compare(null, null));
		assertEquals(-1, p.compare(null, p2));
		assertEquals(1, p.compare(p, null));
		assertEquals(1, p.compareTo(null));
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p.compareTo(p));
		
		p.setProperty(Key.description, "a");
		p2.setProperty(Key.description, "a");
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p2.compareTo(p));
		
		p2.setProperty(Key.description, "b");
		assertEquals("a".compareTo("b"), p.compareTo(p2));
		assertEquals("b".compareTo("a"), p2.compareTo(p));
		p2.setProperty(Key.description, "a");
		assertEquals(0, p.compareTo(p2));
		
		p.setProperty(Key.subject, "a");
		p2.setProperty(Key.subject, "a");
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p2.compareTo(p));
		
		p2.setProperty(Key.subject, "b");
		assertEquals("a".compareTo("b"), p.compareTo(p2));
		assertEquals("b".compareTo("a"), p2.compareTo(p));
		p2.setProperty(Key.subject, "a");
		assertEquals(0, p.compareTo(p2));
		
		ZonedDateTime s1 = ZonedDateTime.now();
		ZonedDateTime s2 = s1.plusHours(1);
		
		p.setProperty(Key.startDate, DateTimeUtil.toString(s1));
		p2.setProperty(Key.startDate, DateTimeUtil.toString(s1));
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p2.compareTo(p));
		
		p2.setProperty(Key.startDate, DateTimeUtil.toString(s2));
		assertEquals(s1.compareTo(s2), p.compareTo(p2));
		assertEquals(s2.compareTo(s1), p2.compareTo(p));
		p2.setProperty(Key.startDate, DateTimeUtil.toString(s1));
		assertEquals(0, p.compareTo(p2));
		
		p.setProperty(Key.endDate, DateTimeUtil.toString(s1));
		p2.setProperty(Key.endDate, DateTimeUtil.toString(s1));
		assertEquals(0, p.compareTo(p2));
		assertEquals(0, p2.compareTo(p));
		
		p2.setProperty(Key.endDate, DateTimeUtil.toString(s2));
		assertEquals(s1.compareTo(s2), p.compareTo(p2));
		assertEquals(s2.compareTo(s1), p2.compareTo(p));
		p2.setProperty(Key.endDate, DateTimeUtil.toString(s1));
		assertEquals(0, p.compareTo(p2));
	}
	/**
	 * Store and reload
	 * 
	 * @param p The properties.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private void storeAndReload(IssuedCertificateProperties p) throws IOException, FileNotFoundException {
		try (FileOutputStream out = new FileOutputStream(fn.toFile())) {
			p.store(out);
		}

		// Reload.
		IssuedCertificateProperties p2 = new IssuedCertificateProperties(null);
		try (FileInputStream in = new FileInputStream(fn.toFile())) {
			p2.load(in);
		}
		assertEquals(p, p2);
	}
}
