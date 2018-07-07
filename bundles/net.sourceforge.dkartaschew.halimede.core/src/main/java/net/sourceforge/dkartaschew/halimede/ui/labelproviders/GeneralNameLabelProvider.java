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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.eclipse.jface.viewers.LabelProvider;

import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;

public class GeneralNameLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		if (element == null || !(element instanceof GeneralName))
			return "";

		GeneralName name = (GeneralName) element;
		GeneralNameTag tag = GeneralNameTag.forTag(name.getTagNo());
		
		StringBuffer buf = new StringBuffer();
		buf.append(tag.getDescription());
		buf.append(": ");
		switch (tag) {
		case rfc822Name:
		case dNSName:
		case uniformResourceIdentifier:
			buf.append(DERIA5String.getInstance(name.getName()).getString());
			break;
		case directoryName:
			buf.append(X500Name.getInstance(name.getName()).toString());
			break;
		default:
			buf.append(name.getName().toString());
		}
		return buf.toString();
	}
	
	public String getValue(Object element) {
		if (element == null || !(element instanceof GeneralName))
			return "";

		GeneralName name = (GeneralName) element;
		GeneralNameTag tag = GeneralNameTag.forTag(name.getTagNo());
		
		StringBuffer buf = new StringBuffer();
		switch (tag) {
		case rfc822Name:
		case dNSName:
		case uniformResourceIdentifier:
			buf.append(DERIA5String.getInstance(name.getName()).getString());
			break;
		case directoryName:
			buf.append(X500Name.getInstance(name.getName()).toString());
			break;
		default:
			buf.append(name.getName().toString());
		}
		return buf.toString();
	}

}
