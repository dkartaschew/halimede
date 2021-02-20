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

package net.sourceforge.dkartaschew.halimede.ui.composite;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.ui.util.SWTColorUtils;

/**
 * Composite for header area in CertificatePane implementations.
 */
public class CertificateHeaderComposite extends Composite {

	private ToolBar toolbar;

	/**
	 * Create the composite.
	 * 
	 * @param parent The parent
	 * @param style The default style
	 * @param model The model for header information
	 */
	public CertificateHeaderComposite(Composite parent, int style, HeaderCompositeMenuModel model) {
		super(parent, style);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 0;
		setLayout(gridLayout);

		Label lblHeader = new Label(this, SWT.NONE);
		lblHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblHeader.setText(model.getHeader());
		// Set the colour depending on light or dark theme.
		Color color = SWTColorUtils.isDarkColour(lblHeader.getBackground()) //
				? PluginDefaults.getResourceManager().createColor(PluginDefaults.HEADER_COLOUR_DARK)
				: PluginDefaults.getResourceManager().createColor(PluginDefaults.HEADER_COLOUR_LIGHT);
		lblHeader.setForeground(color);
		/*
		 * Set font to bold
		 */
		int fontHeight = lblHeader.getFont().getFontData()[0].getHeight() + 2;
		FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblHeader.getFont())//
				.setStyle(SWT.BOLD)//
				.setHeight(fontHeight);
		lblHeader.setFont(PluginDefaults.getResourceManager().createFont(boldDescriptor));

		toolbar = new ToolBar(this, SWT.FLAT);
		ToolItem itemCreate = new ToolItem(toolbar, SWT.DROP_DOWN);
		itemCreate.setToolTipText(model.getToolItem());
		itemCreate.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(model.getToolItemImage())));

		if(model.getToolItemSelectionListener() != null) {
			itemCreate.addSelectionListener(model.getToolItemSelectionListener());
		}
		final Menu menu = model.getDropDownMenu();

		itemCreate.addListener(SWT.Selection, event -> {
			if (event.detail == SWT.ARROW) {
				Rectangle bounds = itemCreate.getBounds();
				Point point = toolbar.toDisplay(bounds.x, bounds.y + bounds.height);
				menu.setLocation(point);
				menu.setVisible(true);
			}
		});
	}

	/**
	 * Get the save toolbar
	 * 
	 * @return The save toolbar.
	 */
	public ToolBar getToolbar() {
		return toolbar;
	}
	
	@Override
	public void setEnabled (boolean enabled) {
		if(!isDisposed()) {
			super.setEnabled(enabled);
			toolbar.setEnabled(enabled);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
