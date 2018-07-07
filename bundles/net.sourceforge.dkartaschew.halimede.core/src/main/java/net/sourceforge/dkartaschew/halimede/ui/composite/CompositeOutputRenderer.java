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

package net.sourceforge.dkartaschew.halimede.ui.composite;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import net.sourceforge.dkartaschew.halimede.data.render.ICertificateOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.util.SWTFontUtils;
import net.sourceforge.dkartaschew.halimede.util.WordUtils;

public class CompositeOutputRenderer extends Composite implements ICertificateOutputRenderer {

	private final static int COLUMNS = 3;
	private final static int INSTEP_PIXELS = 15;
	private Color bkgColor;
	private Color fgColor;

	private Font headerFont;
	private Font monospaceFont;

	private Composite elements;
	private ScrolledComposite scrolledArea;

	/**
	 * Create the composite based renderer.
	 * 
	 * @param parent The parent composite.
	 * @param style The applied style.
	 * @param logger The logger to use.
	 */
	public CompositeOutputRenderer(Composite parent, int style) {
		super(parent, SWT.BORDER);
		init(parent);
	}

	/**
	 * Initialise the composite
	 * @param parent The parent instance.
	 */
	private void init(Composite parent) {
		/*
		 * Color
		 */
		bkgColor = getDisplay().getSystemColor(SWT.COLOR_WHITE);
		fgColor = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		/*
		 * Fonts
		 */
		int fontHeight = parent.getFont().getFontData()[0].getHeight();
		headerFont = FontDescriptor.createFrom(parent.getFont())//
				.setStyle(SWT.BOLD)//
				.setHeight((fontHeight + 1))//
				.createFont(getDisplay());
		monospaceFont = FontDescriptor.createFrom(//
				SWTFontUtils.getMonospacedFont(getDisplay()))//
				.setHeight(fontHeight)//
				.createFont(getDisplay());

		/*
		 * Start layout.
		 */
		setLayout(new GridLayout(1, false));

		scrolledArea = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledArea.setExpandHorizontal(true);
		scrolledArea.setExpandVertical(true);

		elements = new Composite(scrolledArea, SWT.NONE);
		elements.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		elements.setLayout(new GridLayout(3, false));
		elements.setBackground(bkgColor);
	}

	@Override
	public void finaliseRender() {
		scrolledArea.setContent(elements);
		scrolledArea.setMinSize(elements.computeSize(SWT.DEFAULT, SWT.DEFAULT));

	}

	@Override
	protected void checkSubclass() {
		/* Do nothing - Subclassing is allowed */
	}

	@Override
	public void dispose() {
		/*
		 * Clean up fonts.
		 */
		if (headerFont != null && !headerFont.isDisposed()) {
			headerFont.dispose();
		}
		if (monospaceFont != null && !monospaceFont.isDisposed()) {
			monospaceFont.dispose();
		}
		/*
		 * No need to clean colours as these are internal colours.
		 */
	}

	@Override
	public void addHeaderLine(String value) {
		Label lbl = new Label(elements, SWT.WRAP);
		lbl.setText(WordUtils.wrap(value, 100));
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, COLUMNS, 1));
		lbl.setBackground(bkgColor);
		lbl.setForeground(fgColor);
		lbl.setFont(headerFont);
	}

	@Override
	public void addEmptyLine() {
		Label lbl = new Label(elements, SWT.NONE);
		lbl.setText(" ");
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, COLUMNS, 1));
		lbl.setBackground(bkgColor);
		lbl.setForeground(fgColor);
	}

	@Override
	public void addContentLine(String key, String value) {
		addContentLine(key, value, false);
	}

	@Override
	public void addContentLine(String key, String value, boolean monospace) {
		Label lbl = new Label(elements, SWT.NONE);
		lbl.setBackground(bkgColor);
		lbl.setForeground(fgColor);
		GridData gd_lbl = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_lbl.minimumWidth = INSTEP_PIXELS;
		gd_lbl.widthHint = INSTEP_PIXELS;
		lbl.setLayoutData(gd_lbl);

		Label lblKey = new Label(elements, SWT.NONE);
		lblKey.setText(key != null ? key : "");
		lblKey.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblKey.setBackground(bkgColor);
		lblKey.setForeground(fgColor);

		Label lblValue = new Label(elements, SWT.WRAP);
		lblValue.setText(value != null ? value : "");
		lblValue.setBackground(bkgColor);
		lblValue.setForeground(fgColor);
		lblValue.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, COLUMNS - 2, 1));
		if (monospace) {
			lblValue.setFont(monospaceFont);
		}
	}

	@Override
	public void addHorizontalLine() {
		Label label = new Label(elements, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, COLUMNS, 1));
	}

}
