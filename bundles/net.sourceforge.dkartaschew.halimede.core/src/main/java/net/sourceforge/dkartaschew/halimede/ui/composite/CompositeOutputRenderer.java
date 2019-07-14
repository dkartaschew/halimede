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

package net.sourceforge.dkartaschew.halimede.ui.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import net.sourceforge.dkartaschew.halimede.data.render.ICertificateOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.util.SWTFontUtils;

public class CompositeOutputRenderer extends Composite implements ICertificateOutputRenderer {

	/**
	 * The widget to display the contents
	 */
	private StyledText textArea;
	/**
	 * Header font.
	 */
	private Font headerFont;
	/**
	 * System monospace font.
	 */
	private Font monospace;
	/**
	 * The current length.
	 */
	private int currentLength;
	/**
	 * Current EOL
	 */
	private final String EOL = System.lineSeparator();
	/**
	 * EOL Length.
	 */
	private final int EOLLength = EOL.length();
	/**
	 * Size of the header font.
	 */
	private final static int HEADER_FONT_HEIGHT_ADJUSTMENT = 2;

	/**
	 * Create the composite based renderer.
	 * 
	 * @param parent The parent composite.
	 * @param style The applied style.
	 * @param title The title.
	 */
	public CompositeOutputRenderer(Composite parent, int style, String title) {
		super(parent, SWT.BORDER);
		init(parent, title);
	}

	/**
	 * Initialise the composite
	 * 
	 * @param parent The parent instance.
	 * @param title The title.
	 */
	private void init(Composite parent, String title) {

		this.monospace = SWTFontUtils.getMonospacedFont(getDisplay());
		FontData[] font = parent.getFont().getFontData();
		font[0].setHeight(font[0].getHeight() + HEADER_FONT_HEIGHT_ADJUSTMENT);
		font[0].setStyle(SWT.BOLD);
		this.headerFont = new Font(getDisplay(), font[0]);
		/*
		 * Start layout.
		 */
		setLayout(new GridLayout(1, false));

		textArea = new StyledText(this, SWT.DOUBLE_BUFFERED | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL);
		textArea.setAlwaysShowScrollBars(false);
		textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textArea.setMenu(getMenu());

		setBackground(textArea.getBackground());
	}

	@Override
	public void finaliseRender() {
		// NOP
	}

	@Override
	protected void checkSubclass() {
		/* Do nothing - Subclassing is allowed */
	}

	@Override
	public void dispose() {
		super.dispose();
		if (headerFont != null && !headerFont.isDisposed()) {
			headerFont.dispose();
		}
	}

	@Override
	public void addHeaderLine(String value) {
		StyleRange range = new StyleRange();
		range.start = currentLength;
		range.length = value.length();
		range.font = headerFont;
		textArea.append(value);
		textArea.append(EOL);
		textArea.setStyleRange(range);
		textArea.setLineVerticalIndent(textArea.getLineCount() - 2 , 8);
		currentLength += (range.length + EOLLength);
	}

	@Override
	public void addEmptyLine() {
		textArea.append(EOL);
		currentLength += EOLLength;
	}

	@Override
	public void addContentLine(String key, String value) {
		addContentLine(key, value, false);
	}

	@Override
	public void addContentLine(String key, String value, boolean monospace) {

		StyleRange range = new StyleRange();
		range.start = currentLength;
		range.length = key.length();
		range.fontStyle = SWT.BOLD;
		textArea.append(key);
		textArea.append(EOL);
		textArea.setStyleRange(range);
		currentLength += (range.length + EOLLength);
		textArea.setLineIndent(textArea.getLineCount() - 2, 1, 8);

		range = new StyleRange();
		range.start = currentLength;
		range.length = value.length();
		range.font = this.monospace;
		textArea.append(value);
		textArea.append(EOL);
		if (monospace) {
			range = new StyleRange();
			range.start = currentLength;
			range.length = value.length();
			range.font = this.monospace;
			textArea.setStyleRange(range);
		}
		int c = countEOL(value);
		textArea.setLineIndent(textArea.getLineCount() - (1 + c), c, 16);

		currentLength += (value.length() + EOLLength);
	}

	private int countEOL(String value) {
		if (value.contains(EOL)) {
			int lastIndex = 0;
			int count = 0;
			while (lastIndex != -1) {
				lastIndex = value.indexOf(EOL, lastIndex);
				if (lastIndex != -1) {
					count++;
					lastIndex += EOL.length();
				}
			}
			return count + 1;
		}
		return 1;
	}

	@Override
	public void addHorizontalLine() {
		textArea.append("--- --- ---");
		textArea.append(EOL);
		currentLength += EOLLength + 11;
	}

}
