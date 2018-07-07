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

package net.sourceforge.dkartaschew.halimede.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;

public class UpdateCommentDialog extends Dialog {

	/**
	 * The title of the dialog.
	 */
	private String title;
	/**
	 * The description to use.
	 */
	private String description;
	/**
	 * The input value; the empty string by default.
	 */
	private String value = "";//$NON-NLS-1$
	private Text text;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
	 * @param dialogTitle the dialog title, or <code>null</code> if none
	 * @param description The dialog description to use, or <code>null</code> if none
	 * @param initialValue the initial input value, or <code>null</code> if none (equivalent to the empty string)
	 */
	public UpdateCommentDialog(Shell parentShell, String dialogTitle, String description, String initialValue) {
		super(parentShell);
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		this.title = dialogTitle;
		this.description = (description == null) ? "Update Comment" : description;
		this.value = (initialValue == null) ? "" : initialValue;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setMinimumSize(new Point(400, 300));
		shell.setText(title);
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));

		Label lblIssuedCertificateComments = new Label(container, SWT.NONE);
		lblIssuedCertificateComments.setText(description);

		text = new Text(container, SWT.BORDER | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		text.setText(value);
		text.addModifyListener(o1 -> {
			value = text.getText();
		});
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Returns the string typed into this input dialog.
	 *
	 * @return the input string
	 */
	public String getValue() {
		return value;
	}
}
