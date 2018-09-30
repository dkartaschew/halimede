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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import net.sourceforge.dkartaschew.halimede.data.render.ICertificateOutputRenderer;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

public class CompositeOutputRenderer extends Composite implements ICertificateOutputRenderer {

	/**
	 * The widget to display the contents
	 */
	private Browser textArea;
	/**
	 * The HTML renderer
	 */
	private CompositeHTMLRenderer renderer;
	/**
	 * The stream we capture the render to pass to the browser.
	 */
	private ByteArrayOutputStream stream;

	/**
	 * Create the composite based renderer.
	 * 
	 * @param parent The parent composite.
	 * @param style  The applied style.
	 * @param title  The title.
	 */
	public CompositeOutputRenderer(Composite parent, int style, String title) {
		super(parent, SWT.BORDER);
		init(parent, title);
	}

	/**
	 * Initialise the composite
	 * 
	 * @param parent The parent instance.
	 * @param title  The title.
	 */
	private void init(Composite parent, String title) {
		/*
		 * Start layout.
		 */
		setLayout(new GridLayout(1, false));
		setMenu(getBrowserMenu());

		textArea = new Browser(this, SWT.DOUBLE_BUFFERED);
		textArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textArea.setMenu(getMenu());
		stream = new ByteArrayOutputStream();
		renderer = new CompositeHTMLRenderer(new PrintStream(stream), title, getShell());
	}

	@Override
	public void finaliseRender() {
		renderer.finaliseRender();
		try {
			textArea.setText(stream.toString(StandardCharsets.UTF_8.name()));
		} catch (UnsupportedEncodingException e) {
			// Should never throw
			textArea.setText(ExceptionUtil.getMessage(e));
		}
	}

	@Override
	protected void checkSubclass() {
		/* Do nothing - Subclassing is allowed */
	}

	@Override
	public void addHeaderLine(String value) {
		renderer.addHeaderLine(value);
	}

	@Override
	public void addEmptyLine() {
		renderer.addEmptyLine();
	}

	@Override
	public void addContentLine(String key, String value) {
		addContentLine(key, value, false);
	}

	@Override
	public void addContentLine(String key, String value, boolean monospace) {
		renderer.addContentLine(key, value, monospace);
	}

	@Override
	public void addHorizontalLine() {
		renderer.addHorizontalLine();
	}

	/**
	 * Get the custom browser menu.
	 * <P>
	 * This menu only contains a "copy" command.
	 * 
	 * @return A customised browser menu.
	 */
	private Menu getBrowserMenu() {
		Menu menu = new Menu(getShell(), SWT.POP_UP);
		MenuItem item = new MenuItem(menu, SWT.NONE);
		item.setText("Copy");
		item.addListener(SWT.Selection, e -> textArea.execute("document.execCommand('copy')"));
		return menu;
	}

}
