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

package net.sourceforge.dkartaschew.halimede.util;

/**
 * Generalised helper methods for dealing with exception messages.
 */
public class ExceptionUtil {
	
	/**
	 * Get a message for the given throwable.
	 * <P>
	 * This method will return the exceptions message if it has one, or the class name of the exception if it does not
	 * contain a message.
	 * 
	 * @param th The exception to extract a message from
	 * @return A message or the exception class name
	 */
	public static String getMessage(Throwable th) {
		if (th == null) {
			return "No exception passed?";
		}
		String msg = th.getMessage();
		if (msg != null && !msg.trim().isEmpty()) {
			return msg.trim();
		}
		return th.getClass().getName();
	}
}
