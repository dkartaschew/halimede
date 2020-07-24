/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2020 Darran Kartaschew 
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
package net.sourceforge.dkartaschew.halimede.ui.util;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Utility methods for basic message dialogs.
 */
public class Dialogs {

	/**
	 * Open a message confirmation dialog.
	 * 
	 * @param parent The parent shell
	 * @param title The title of the dialog
	 * @param message The message to display
	 * @param okButton The Text to display in place of the OK button
	 * @param cancelButton The Text to display in place of the Cancel button
	 * @return TRUE as confirmation. (OK button pressed)
	 */
	public static boolean openConfirm(Shell parent, String title, String message, String okButton,
			String cancelButton) {
		return new MessageDialog(parent, title, null, message, MessageDialog.CONFIRM, 0, okButton, cancelButton)
				.open() == 0;
	}
}
