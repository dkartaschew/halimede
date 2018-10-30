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

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.CRLPane;

public class CRLColumnComparator extends AbstractColumnComparator<CRLProperties> {

	@Override
	public int compare(int columnIndex, CRLProperties e1, CRLProperties e2) {
		switch (columnIndex) {
		case -1:
		case CRLPane.COLUMN_CRL_NUMBER:
			return compareString(e1.getProperty(CRLProperties.Key.crlSerialNumber),
					e2.getProperty(CRLProperties.Key.crlSerialNumber));
		case CRLPane.COLUMN_SUBJECT:
			return compareString(e1.getProperty(CRLProperties.Key.issuer), 
					e2.getProperty(CRLProperties.Key.issuer));
		case CRLPane.COLUMN_START_DATE:
			return compareDate(e1.getProperty(CRLProperties.Key.issueDate),
					e2.getProperty(CRLProperties.Key.issueDate));
		case CRLPane.COLUMN_EXPIRY_DATE:
			return compareDate(e1.getProperty(CRLProperties.Key.nextExpectedDate),
					e2.getProperty(CRLProperties.Key.nextExpectedDate));
		case CRLPane.COLUMN_COMMENTS:
			return compareString(e1.getProperty(CRLProperties.Key.comments),
					e2.getProperty(CRLProperties.Key.comments));
		}
		return 0;
	}

}
