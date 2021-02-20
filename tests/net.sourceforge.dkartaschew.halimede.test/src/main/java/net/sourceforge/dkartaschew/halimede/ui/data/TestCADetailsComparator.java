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
package net.sourceforge.dkartaschew.halimede.ui.data;

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.junit.Test;
import org.mockito.Mockito;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;

public class TestCADetailsComparator {

	@Test
	public void testDirection() {
		IssuedCertificateComparator colcomp = new IssuedCertificateComparator();
		CADetailsComparator<IssuedCertificateProperties> comp = //
				new CADetailsComparator<IssuedCertificateProperties>(colcomp);

		// Check defaults
		assertEquals(0, comp.getColumn());
		assertEquals(SWT.DOWN, comp.getDirection());

		// Manually set direction
		comp.setDirection(SWT.UP);
		assertEquals(SWT.UP, comp.getDirection());
		
		comp.setDirection(SWT.DOWN);
		assertEquals(SWT.DOWN, comp.getDirection());
		
		comp.setDirection(SWT.UP);
		assertEquals(SWT.UP, comp.getDirection());

		// Set same column should switch directions.
		comp.setColumn(0);
		assertEquals(SWT.DOWN, comp.getDirection());

		comp.setColumn(0);
		assertEquals(SWT.UP, comp.getDirection());

		// New column to down.
		comp.setColumn(1);
		assertEquals(SWT.DOWN, comp.getDirection());

		// New column to down.
		comp.setColumn(2);
		assertEquals(SWT.DOWN, comp.getDirection());
	}
	
	@Test
	public void testDefaultColumn() {
		IssuedCertificateComparator colcomp = new IssuedCertificateComparator();
		CADetailsComparator<IssuedCertificateProperties> comp = //
				new CADetailsComparator<IssuedCertificateProperties>(colcomp);
		
		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.description)).thenReturn("Abc");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.description)).thenReturn("abcd");
		
		// Default compare
		assertEquals(0, comp.compare(prop1, prop1));
		
		assertEquals(-1, comp.compare(prop1, prop2));
		assertEquals(1, comp.compare(prop2, prop1));
		
		// Switch directions.
		comp.setDirection(SWT.UP);
		assertEquals(SWT.UP, comp.getDirection());
		
		// And now should be same, as in this mode, direction is ignored
		assertEquals(-1, comp.compare(prop1, prop2));
		assertEquals(1, comp.compare(prop2, prop1));
		
	}
	
	@Test
	public void testColumn0() {
		IssuedCertificateComparator colcomp = new IssuedCertificateComparator();
		CADetailsComparator<IssuedCertificateProperties> comp = //
				new CADetailsComparator<IssuedCertificateProperties>(colcomp);
		
		Viewer view = Mockito.mock(Viewer.class);
		
		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.description)).thenReturn("Abc");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.description)).thenReturn("abcd");
		
		// Default compare
		assertEquals(0, comp.compare(view, prop1, prop1));
		
		assertEquals(-1, comp.compare(view, prop1, prop2));
		assertEquals(1, comp.compare(view, prop2, prop1));
		
		// Switch directions.
		comp.setDirection(SWT.UP);
		assertEquals(SWT.UP, comp.getDirection());
		
		// And now should be opposite.
		assertEquals(1, comp.compare(view, prop1, prop2));
		assertEquals(-1, comp.compare(view, prop2, prop1));
		
	}
	
	@Test
	public void testColumn1() {
		IssuedCertificateComparator colcomp = new IssuedCertificateComparator();
		CADetailsComparator<IssuedCertificateProperties> comp = //
				new CADetailsComparator<IssuedCertificateProperties>(colcomp);
		
		Viewer view = Mockito.mock(Viewer.class);
		
		IssuedCertificateProperties prop1 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop1.getProperty(Key.subject)).thenReturn("CN=Abc");

		IssuedCertificateProperties prop2 = Mockito.mock(IssuedCertificateProperties.class);
		Mockito.when(prop2.getProperty(Key.subject)).thenReturn("CN=abcd");
		
		comp.setColumn(1);
		
		// Default compare
		assertEquals(0, comp.compare(view, prop1, prop1));
		
		assertEquals(-1, comp.compare(view, prop1, prop2));
		assertEquals(1, comp.compare(view, prop2, prop1));
		
		// Switch directions.
		comp.setDirection(SWT.UP);
		assertEquals(SWT.UP, comp.getDirection());
		
		// And now should be opposite.
		assertEquals(1, comp.compare(view, prop1, prop2));
		assertEquals(-1, comp.compare(view, prop2, prop1));
		
	}
}
