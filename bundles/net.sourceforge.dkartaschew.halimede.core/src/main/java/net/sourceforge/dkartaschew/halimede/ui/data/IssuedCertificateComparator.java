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

package net.sourceforge.dkartaschew.halimede.ui.data;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.ui.composite.CADetailPane;

public class IssuedCertificateComparator extends AbstractColumnComparator<IssuedCertificateProperties> {

	@Override
	public int compare(int columnIndex, IssuedCertificateProperties e1, IssuedCertificateProperties e2) {
		switch (columnIndex) {
		case -1:
		case CADetailPane.COLUMN_DESCRIPTION:
			return compareString(e1.getProperty(Key.description), e2.getProperty(Key.description));
		case CADetailPane.COLUMN_SUBJECT:
			return compareString(e1.getProperty(Key.subject), e2.getProperty(Key.subject));
		case CADetailPane.COLUMN_KEY_TYPE:
			return compareKeyType(e1.getProperty(Key.keyType), e2.getProperty(Key.keyType));
		case CADetailPane.COLUMN_ISSUE_DATE:
			return compareDate(e1.getProperty(Key.creationDate), e2.getProperty(Key.creationDate));
		case CADetailPane.COLUMN_START_DATE:
			return compareDate(e1.getProperty(Key.startDate), e2.getProperty(Key.startDate));
		case CADetailPane.COLUMN_EXPIRY_DATE:
			return compareDate(e1.getProperty(Key.endDate), e2.getProperty(Key.endDate));
		case CADetailPane.COLUMN_REVOKE_DATE:
			return compareDate(e1.getProperty(Key.revokeDate), e2.getProperty(Key.revokeDate));
		case CADetailPane.COLUMN_REVOKE_REASON:
			return compareRevokeReason(e1.getProperty(Key.revokeCode), e2.getProperty(Key.revokeCode));
		case CADetailPane.COLUMN_COMMENTS:
			return compareString(e1.getProperty(Key.comments), e2.getProperty(Key.comments));
		}
		return 0;
	}
}
