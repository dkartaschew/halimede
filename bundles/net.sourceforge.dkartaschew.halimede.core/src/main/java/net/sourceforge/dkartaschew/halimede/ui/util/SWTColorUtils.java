/*-
 * Based on attachment to https://bugs.eclipse.org/bugs/show_bug.cgi?id=48055
 * No license defined. Assuming EPL
 */
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
package net.sourceforge.dkartaschew.halimede.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * {@link SWT} colour related utility methods.
 */
public class SWTColorUtils {

	/**
	 * Determine if the given Color is light or dark
	 * 
	 * @param color The Color to check
	 * @return TRUE if the colour is dark, or FALSE if the colour is light.
	 */
	public static boolean isDarkColour(Color color) {
		double darkness = 1 - (0.299 * (double) color.getRed() //
				+ 0.587 * (double) color.getGreen() //
				+ 0.114 * (double) color.getBlue())//
				/ 255;
		return (darkness >= 0.5);
	}

}
