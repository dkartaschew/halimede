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

package net.sourceforge.dkartaschew.halimede.ui.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.junit.Test;

public class TestHeaderMenuModel {

	private String STR = "StringValue";
	
	@Test
	public void testHeader() {
		HeaderCompositeMenuModel model = new HeaderCompositeMenuModel();
		assertEquals(null, model.getHeader());
		model.setHeader(STR);
		assertEquals(STR, model.getHeader());
		
		assertNull(model.getToolItem());
		assertNull(model.getToolItemImage());
		assertNull(model.getToolItemSelectionListener());
		assertTrue(model.getMenuItems().isEmpty());
		assertNull(model.getDropDownMenu());
		
		model.setHeader(null);
		assertEquals(null, model.getHeader());
	}
	
	@Test
	public void testToolitem() {
		HeaderCompositeMenuModel model = new HeaderCompositeMenuModel();
		assertEquals(null, model.getToolItem());
		model.setToolItem(STR);
		assertEquals(STR, model.getToolItem());
		
		assertNull(model.getHeader());
		assertNull(model.getToolItemImage());
		assertNull(model.getToolItemSelectionListener());
		assertTrue(model.getMenuItems().isEmpty());
		assertNull(model.getDropDownMenu());
		
		model.setToolItem(null);
		assertEquals(null, model.getToolItem());
	}
	
	@Test
	public void testToolitemImage() {
		HeaderCompositeMenuModel model = new HeaderCompositeMenuModel();
		assertEquals(null, model.getToolItemImage());
		model.setToolItemImage(STR);
		assertEquals(STR, model.getToolItemImage());
		
		assertNull(model.getHeader());
		assertNull(model.getToolItem());
		assertNull(model.getToolItemSelectionListener());
		assertTrue(model.getMenuItems().isEmpty());
		assertNull(model.getDropDownMenu());
		
		model.setToolItemImage(null);
		assertEquals(null, model.getToolItemImage());
	}
	
	@Test
	public void testToolItemSelectionListener() {
		HeaderCompositeMenuModel model = new HeaderCompositeMenuModel();
		assertEquals(null, model.getToolItemSelectionListener());
		
		SelectionListener i = new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		};
		
		model.setToolItemSelectionListener(i);
		assertEquals(i, model.getToolItemSelectionListener());
		
		assertNull(model.getHeader());
		assertNull(model.getToolItem());
		assertNull(model.getToolItemImage());
		assertTrue(model.getMenuItems().isEmpty());
		assertNull(model.getDropDownMenu());
		
		model.setToolItemSelectionListener(null);
		assertEquals(null, model.getToolItemSelectionListener());
	}
	
	@Test
	public void testMenu() {
		HeaderCompositeMenuModel model = new HeaderCompositeMenuModel();
		assertEquals(null, model.getDropDownMenu());
		Menu i = mock(Menu.class);
		model.setDropDownMenu(i);
		assertEquals(i, model.getDropDownMenu());
		
		assertNull(model.getHeader());
		assertNull(model.getToolItem());
		assertNull(model.getToolItemImage());
		assertNull(model.getToolItemSelectionListener());
		assertTrue(model.getMenuItems().isEmpty());
		
		model.setDropDownMenu(null);
		assertEquals(null, model.getDropDownMenu());
	}
}
