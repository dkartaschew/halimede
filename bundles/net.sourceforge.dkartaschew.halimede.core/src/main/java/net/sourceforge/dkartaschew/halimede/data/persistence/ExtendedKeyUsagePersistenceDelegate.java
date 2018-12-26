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

package net.sourceforge.dkartaschew.halimede.data.persistence;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.IOException;

import org.bouncycastle.asn1.x509.KeyPurposeId;

/**
 * A Persistence delegate for handling BC KeyPurposedID
 */
public class ExtendedKeyUsagePersistenceDelegate extends PersistenceDelegate {

	/*
	 * (non-Javadoc)
	 * @see java.beans.PersistenceDelegate#instantiate(java.lang.Object, java.beans.Encoder)
	 */
	@Override
	protected Expression instantiate(Object oldInstance, Encoder out) {
		KeyPurposeId keyId = (KeyPurposeId) oldInstance;
		try {
			return new Expression(oldInstance, KeyPurposeId.class, "getInstance", new Object[] { keyId.getEncoded() });
		} catch (IOException e) {
			return null;
		}
	}

}
