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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import java.util.NoSuchElementException;

import org.eclipse.swt.graphics.Image;

import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.PendingCertificatesPane;
import net.sourceforge.dkartaschew.halimede.util.Strings;

public class CSRColumnLabelProvider extends CADetailsLabelProvider<CertificateRequestProperties> {

	@Override
	public String getColumnText(CertificateRequestProperties element, int columnIndex) {
		switch (columnIndex) {
		case PendingCertificatesPane.COLUMN_SUBJECT:
			return element.getProperty(CertificateRequestProperties.Key.subject);
		case PendingCertificatesPane.COLUMN_KEY_TYPE:
			try {
				return KeyType.getKeyTypeDescription(element.getProperty(CertificateRequestProperties.Key.keyType));
			} catch (NullPointerException | NoSuchElementException | IllegalArgumentException e) {
				return null;
			}
		case PendingCertificatesPane.COLUMN_IMPORT_DATE:
			return element.getProperty(CertificateRequestProperties.Key.importDate);
		case PendingCertificatesPane.COLUMN_COMMENTS:
			return Strings.trim(element.getProperty(CertificateRequestProperties.Key.comments), Strings.WRAP);
		}
		return null;
	}

	@Override
	public String getColumnTooltipText(CertificateRequestProperties element, int columnIndex) {
		if(columnIndex == PendingCertificatesPane.COLUMN_COMMENTS) {
			return element.getProperty(CertificateRequestProperties.Key.comments);
		}
		return getColumnText(element, columnIndex);
	}

	@Override
	public Image getColumnImage(CertificateRequestProperties element, int columnIndex) {
		return null;
	}

}
