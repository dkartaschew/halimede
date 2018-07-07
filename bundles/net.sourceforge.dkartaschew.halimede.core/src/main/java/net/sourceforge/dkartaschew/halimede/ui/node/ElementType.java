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

package net.sourceforge.dkartaschew.halimede.ui.node;

/**
 * The type of node element.
 */
public enum ElementType {
	/**
	 * The UI node represents Issued Certificates
	 */
	Issued,
	/**
	 * The UI node represents Revoked Certificates
	 */
	Revoked,
	/**
	 * The UI node represents Certificate Requests that are pending to be signed.
	 */
	Pending,
	/**
	 * The UI node represent Templates for Key/Certificate pairs.
	 */
	Template,
	/**
	 * The UI node represent a generated CRL.
	 */
	CRLs;
}
