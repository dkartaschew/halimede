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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import org.eclipse.swt.graphics.Image;

import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.TemplatesPane;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

public class TemplateColumnLabelProvider extends CADetailsLabelProvider<CertificateKeyPairTemplate> {

	@Override
	public String getColumnText(CertificateKeyPairTemplate element, int columnIndex) {
		switch (columnIndex) {
		case TemplatesPane.COLUMN_DESCRIPTION:
			return Strings.trim(element.getDescription(), Strings.WRAP);
		case TemplatesPane.COLUMN_SUBJECT:
			if (element.getSubject() != null)
				return element.getSubject().toString();
			return "";
		case TemplatesPane.COLUMN_KEY_TYPE:
			if (element.getKeyType() != null)
				return element.getKeyType().getDescription();
			return "";
		case TemplatesPane.COLUMN_CREATE_DATE:
			return DateTimeUtil.toString(element.getCreationDate());
		}
		return null;
	}

	@Override
	public String getColumnTooltipText(CertificateKeyPairTemplate element, int columnIndex) {
		if(columnIndex == TemplatesPane.COLUMN_DESCRIPTION) {
			return element.getDescription();
		}
		return getColumnText(element, columnIndex);
	}

	@Override
	public Image getColumnImage(CertificateKeyPairTemplate element, int columnIndex) {
		return null;
	}

}
