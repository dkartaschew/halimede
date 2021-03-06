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

import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.TemplatesPane;

public class TemplateColumnComparator extends AbstractColumnComparator<CertificateKeyPairTemplate> {

	@Override
	public int compare(int columnIndex, CertificateKeyPairTemplate e1, CertificateKeyPairTemplate e2) {
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
		case TemplatesPane.COLUMN_DESCRIPTION:
			return compareString(e1.getDescription(), e2.getDescription());
		case TemplatesPane.COLUMN_SUBJECT:
			return compareX500Name(e1.getSubject(), e2.getSubject());
		case TemplatesPane.COLUMN_KEY_TYPE:
			return compareKeyType(e1.getKeyType(), e2.getKeyType());
		case TemplatesPane.COLUMN_CREATE_DATE:
			return compareDate(e1.getCreationDate(), e2.getCreationDate());
		}
		return 0;
	}

}
