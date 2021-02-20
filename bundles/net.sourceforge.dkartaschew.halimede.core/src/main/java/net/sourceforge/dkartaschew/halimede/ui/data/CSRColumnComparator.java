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

package net.sourceforge.dkartaschew.halimede.ui.data;

import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.PendingCertificatesPane;

public class CSRColumnComparator extends AbstractColumnComparator<CertificateRequestProperties> {

	@Override
	public int compare(int columnIndex, CertificateRequestProperties e1, CertificateRequestProperties e2) {
		if (e1 == null && e2 == null) {
			return 0;
		}
		if (e1 == null) {
			return 1;
		}
		if (e2 == null) {
			return -1;
		}
		switch (columnIndex) {
		case -1:
		case PendingCertificatesPane.COLUMN_SUBJECT:
			return compareString(e1.getProperty(CertificateRequestProperties.Key.subject),
					e2.getProperty(CertificateRequestProperties.Key.subject));
		case PendingCertificatesPane.COLUMN_IMPORT_DATE:
			return compareDate(e1.getProperty(CertificateRequestProperties.Key.importDate),
					e2.getProperty(CertificateRequestProperties.Key.importDate));
		case PendingCertificatesPane.COLUMN_COMMENTS:
			return compareString(e1.getProperty(CertificateRequestProperties.Key.comments),
					e2.getProperty(CertificateRequestProperties.Key.comments));
		case PendingCertificatesPane.COLUMN_KEY_TYPE:
			return compareKeyType(e1.getProperty(CertificateRequestProperties.Key.keyType),
					e2.getProperty(CertificateRequestProperties.Key.keyType));
		}
		return 0;
	}

}
