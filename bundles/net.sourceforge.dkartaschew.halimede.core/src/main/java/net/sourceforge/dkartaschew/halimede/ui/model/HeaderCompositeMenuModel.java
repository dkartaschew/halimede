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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class HeaderCompositeMenuModel {

	/**
	 * Text for the header bar.
	 */
	private String header;
	/**
	 * Tooltip for primary icon
	 */
	private String toolItem;
	/**
	 * Icon for primary button
	 */
	private String toolItemImage;
	/**
	 * Selection listener for primary button
	 */
	private SelectionListener toolItemSelectionListener;
	/**
	 * Primary menu...
	 */
	private Menu dropDownMenu;
	/**
	 * Collection of menu items.
	 */
	private final List<MenuItem> menuItems = new ArrayList<>();

	/**
	 * Get the header description string
	 * 
	 * @return the header string
	 */
	public String getHeader() {
		return header;
	}

	/**
	 * Set the header string
	 * 
	 * @param header the header to set
	 */
	public void setHeader(String header) {
		this.header = header;
	}

	/**
	 * Get the primary button tooltop
	 * 
	 * @return the toolItem
	 */
	public String getToolItem() {
		return toolItem;
	}

	/**
	 * Set the primary button tooltop
	 * 
	 * @param toolItem the toolItem to set
	 */
	public void setToolItem(String toolItem) {
		this.toolItem = toolItem;
	}

	/**
	 * Get the primary button image
	 * 
	 * @return the toolItemImage
	 */
	public String getToolItemImage() {
		return toolItemImage;
	}

	/**
	 * Set the primary button image
	 * 
	 * @param toolItemImage the image to set
	 */
	public void setToolItemImage(String toolItemImage) {
		this.toolItemImage = toolItemImage;
	}

	/**
	 * Get the primary button selection listener
	 * 
	 * @return the toolItemSelectionListener
	 */
	public SelectionListener getToolItemSelectionListener() {
		return toolItemSelectionListener;
	}

	/**
	 * Set the primary button selection listener
	 * 
	 * @param toolItemSelectionListener the toolItemSelectionListener to set
	 */
	public void setToolItemSelectionListener(SelectionListener toolItemSelectionListener) {
		this.toolItemSelectionListener = toolItemSelectionListener;
	}

	/**
	 * Get the primary menu for the drop down button
	 * 
	 * @return the dropDownMenu
	 */
	public Menu getDropDownMenu() {
		return dropDownMenu;
	}

	/**
	 * Set the primary menu for the drop down button
	 * 
	 * @param dropDownMenu the dropDownMenu to set
	 */
	public void setDropDownMenu(Menu dropDownMenu) {
		this.dropDownMenu = dropDownMenu;
	}

	/**
	 * Get the collection of menu items in the drop down menu.
	 * 
	 * @return the menuItems
	 */
	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

}
