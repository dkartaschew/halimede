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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import java.util.NoSuchElementException;

import org.eclipse.swt.graphics.Image;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.IssuedCertificatesPane;
import net.sourceforge.dkartaschew.halimede.util.Strings;

public class IssuedCertificateColumnLabelProvider extends CADetailsLabelProvider<IssuedCertificateProperties> {

	@Override
	public String getColumnText(IssuedCertificateProperties element, int columnIndex) {
		switch (columnIndex) {
		case IssuedCertificatesPane.COLUMN_DESCRIPTION:
			return Strings.trim(element.getProperty(Key.description), Strings.WRAP);
		case IssuedCertificatesPane.COLUMN_SUBJECT:
			return element.getProperty(Key.subject);
		case IssuedCertificatesPane.COLUMN_KEY_TYPE:
			try {
				return KeyType.getKeyTypeDescription(element.getProperty(Key.keyType));
			} catch (NoSuchElementException | NullPointerException e) {
				return null;
			}
		case IssuedCertificatesPane.COLUMN_START_DATE:
			return element.getProperty(Key.startDate);
		case IssuedCertificatesPane.COLUMN_EXPIRY_DATE:
			return element.getProperty(Key.endDate);
		case IssuedCertificatesPane.COLUMN_ISSUE_DATE:
			return element.getProperty(Key.creationDate);
		case IssuedCertificatesPane.COLUMN_COMMENTS:
			return Strings.trim(element.getProperty(Key.comments), Strings.WRAP);
		}
		return null;
	}

	@Override
	public String getColumnTooltipText(IssuedCertificateProperties element, int columnIndex) {
		switch (columnIndex) {
		case IssuedCertificatesPane.COLUMN_DESCRIPTION:
			return element.getProperty(Key.description);
		case IssuedCertificatesPane.COLUMN_COMMENTS:
			return element.getProperty(Key.comments);
		}
		return getColumnText(element, columnIndex);
	}

	@Override
	public Image getColumnImage(IssuedCertificateProperties element, int columnIndex) {
		return null;
	}

}
