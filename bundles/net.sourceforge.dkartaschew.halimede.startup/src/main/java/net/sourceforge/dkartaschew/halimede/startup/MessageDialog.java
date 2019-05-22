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

package net.sourceforge.dkartaschew.halimede.startup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class MessageDialog extends Dialog {

	private Shell shell;
	private final Image icon;
	private final String message;

	/**
	 * Create a new message dialog with the given icon and message.
	 * 
	 * @param parentShell The parent shell
	 * @param icon The icon to display
	 * @param title The title of the dialog
	 * @param message The message to display in the dialog.
	 */
	public MessageDialog(Shell parentShell, Image icon, String title, String message) {
		super(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		parentShell.setText(title);
		parentShell.setImage(icon);
		this.shell = parentShell;
		this.icon = icon;
		this.message = message;
	}

	/**
	 * Open the dialog.
	 * 
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();

		Rectangle parentSize = getParent().getDisplay().getPrimaryMonitor().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
		int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;
		shell.setLocation(new Point(locationX, locationY));

		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}

	protected void createContents() {
		GridLayout shellLayout = new GridLayout(1, false);
		shellLayout.marginBottom = 10;
		shellLayout.marginLeft = 10;
		shellLayout.marginRight = 10;
		shellLayout.marginTop = 10;

		shell.setLayout(shellLayout);

		Composite container = new Composite(shell, SWT.NONE);
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.marginWidth = 50;
		gl_container.horizontalSpacing = 20;
		gl_container.verticalSpacing = 20;
		container.setLayout(gl_container);

		Label lblIcon = new Label(container, SWT.NONE);
		lblIcon.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 1));
		lblIcon.setImage(icon);

		Label lblText = new Label(container, SWT.WRAP);
		lblText.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		lblText.setText(message);

		Composite buttonPane = new Composite(shell, SWT.NONE);
		buttonPane.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		buttonPane.setLayout(new FillLayout(SWT.HORIZONTAL));

		Button btnOK = new Button(buttonPane, SWT.NONE);
		btnOK.setText("  OK  ");
		btnOK.addListener(SWT.Selection, e -> shell.dispose());
		shell.pack();
	}

}
