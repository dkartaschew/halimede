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
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;

public class PassphraseDialog extends Dialog {

	/**
	 * The title of the dialog.
	 */
	private String title;

	/**
	 * The message to display, or <code>null</code> if none.
	 */
	private String message;

	/**
	 * The input value; the empty string by default.
	 */
	private String value = "";//$NON-NLS-1$

	/**
	 * Input text widget.
	 */
	private Text text;

	/**
	 * Error message string.
	 */
	private String errorMessage;

	/**
	 * Creates an input dialog with OK and Cancel buttons. Note that the dialog will have no visual representation (no
	 * widgets) until it is told to open.
	 * <p>
	 * Note that the <code>open</code> method blocks for input dialogs.
	 * </p>
	 *
	 * @param parentShell the parent shell, or <code>null</code> to create a top-level shell
	 * @param dialogTitle the dialog title, or <code>null</code> if none
	 * @param dialogMessage the dialog message, or <code>null</code> if none
	 * @param initialValue the initial input value, or <code>null</code> if none (equivalent to the empty string)
	 */
	public PassphraseDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue) {
		super(parentShell);
		this.title = dialogTitle;
		this.message = dialogMessage;
		this.value = (initialValue == null) ? "" : initialValue;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Cancel buttons by default
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		// do this here because setting the text will set enablement on the ok
		// button
		text.setFocus();
		if (value != null) {
			text.setText(value);
			text.selectAll();
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// create message

		Label label = new Label(composite, SWT.WRAP);
		label.setText(message);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		text = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		text.addModifyListener(o1 -> {
			value = text.getText();
		});

		if (errorMessage != null) {
			Label errorLabel = new Label(composite, SWT.WRAP);
			errorLabel.setText(errorMessage);
			data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
					| GridData.VERTICAL_ALIGN_CENTER);
			data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
			errorLabel.setLayoutData(data);
			errorLabel.setAlignment(SWT.LEFT);
			/*
			 * Set font to small - bold
			 */
			Font parentFont = errorLabel.getFont();
			int fontHeight = parentFont.getFontData()[0].getHeight() - 1;
			FontDescriptor boldDescriptor = FontDescriptor.createFrom(parentFont).setStyle(SWT.BOLD).setHeight(fontHeight);
			Font boldFont = boldDescriptor.createFont(errorLabel.getDisplay());
			errorLabel.setFont(boldFont);
		}
		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Returns the string typed into this input dialog.
	 *
	 * @return the input string
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set an error message to display.
	 * 
	 * @param error An error message to display.
	 */
	public void setErrorMessage(String error) {
		this.errorMessage = error;
	}
}
