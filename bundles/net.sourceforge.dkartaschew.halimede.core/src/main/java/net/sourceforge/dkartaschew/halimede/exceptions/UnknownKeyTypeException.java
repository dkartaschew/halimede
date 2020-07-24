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

package net.sourceforge.dkartaschew.halimede.exceptions;

public class UnknownKeyTypeException extends Exception {

	/**
	 * Exception serial number
	 */
	private static final long serialVersionUID = 8218825070591390019L;

	/**
	 * Create a new Unknown Key Type exception
	 * 
	 * @param arg0 The message for the exception.
	 */
	public UnknownKeyTypeException(String message) {
		super(message);
	}
	
}
