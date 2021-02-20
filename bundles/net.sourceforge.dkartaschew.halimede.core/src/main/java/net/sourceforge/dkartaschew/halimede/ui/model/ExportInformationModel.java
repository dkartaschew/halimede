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

import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;

/**
 * Model for exporting certificate information.
 */
public class ExportInformationModel {

	private String filename;
	private String password;
	private PKCS8Cipher pkcs8Cipher;
	private PKCS12Cipher pkcs12Cipher;
	private EncodingType encoding;
	private String dialogTitle;
	private String saveDialogTitle;
	private String[] saveDialogFilterExtensions;
	private String[] saveDialogFilterNames;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public EncodingType getEncoding() {
		return encoding;
	}

	public void setEncoding(EncodingType encoding) {
		this.encoding = encoding;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PKCS8Cipher getPkcs8Cipher() {
		return pkcs8Cipher;
	}

	public void setPkcs8Cipher(PKCS8Cipher pkcs8Cipher) {
		this.pkcs8Cipher = pkcs8Cipher;
	}

	public PKCS12Cipher getPkcs12Cipher() {
		return pkcs12Cipher;
	}

	public void setPkcs12Cipher(PKCS12Cipher pkcs12Cipher) {
		this.pkcs12Cipher = pkcs12Cipher;
	}

	public String getDialogTitle() {
		return dialogTitle;
	}

	public void setDialogTitle(String dialogTitle) {
		this.dialogTitle = dialogTitle;
	}

	public String getSaveDialogTitle() {
		return saveDialogTitle;
	}

	public void setSaveDialogTitle(String saveDialogTitle) {
		this.saveDialogTitle = saveDialogTitle;
	}

	public String[] getSaveDialogFilterExtensions() {
		return saveDialogFilterExtensions;
	}

	public void setSaveDialogFilterExtensions(String[] saveDialogFilterExtensions) {
		this.saveDialogFilterExtensions = saveDialogFilterExtensions;
	}

	public String[] getSaveDialogFilterNames() {
		return saveDialogFilterNames;
	}

	public void setSaveDialogFilterNames(String[] saveDialogFilterNames) {
		this.saveDialogFilterNames = saveDialogFilterNames;
	}

}
