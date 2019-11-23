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

package net.sourceforge.dkartaschew.halimede.ui.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;

/**
 * Extension of basic Input Dialog to allow setting of OK and Cancel Button text.
 *
 */
public class InputDialogEx extends InputDialog {

	/**
	 * The text to use for the OK button.
	 */
	private final String okButtonText;
	/**
	 * The text to use for the Cancel button.
	 */
	private final String cancelButtonText;
	/**
	 * The text to set on the text field
	 */
	private final String textTooltipText;

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
	 * @param validator an input validator, or <code>null</code> if none
	 * @param okButtonText the text for the OK button, or <code>null</code> for default.
	 * @param cancelButtonText the text for the Cancel button, or <code>null</code> for default.
	 * @param textTooltipText the text for the text field tooltip, or <code>null</code> for default.
	 */
	public InputDialogEx(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator, String okButtonText, String cancelButtonText, String textTooltipText) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
		this.okButtonText = okButtonText;
		this.cancelButtonText = cancelButtonText;
		this.textTooltipText = textTooltipText;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if (okButtonText != null) {
			getButton(IDialogConstants.OK_ID).setText(okButtonText);
		}
		if (cancelButtonText != null) {
			getButton(IDialogConstants.CANCEL_ID).setText(cancelButtonText);
		}
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Control p = super.createDialogArea(parent);
		if (textTooltipText != null) {
			getText().setToolTipText(textTooltipText);
		}
		return p;
	}

}
