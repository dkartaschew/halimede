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

package net.sourceforge.dkartaschew.halimede.ui.composite;

public interface IColumnComparator<V> {

	/**
	 * Returns a negative, zero, or positive number depending on whether the first element is less than, equal to, or
	 * greater than the second element.
	 *
	 * @param columnIndex the column index, or -1 for default ordering.
	 * @param e1 the first element
	 * @param e2 the second element
	 * @return a negative number if the first element is less than the second element; the value <code>0</code> if the
	 *         first element is equal to the second element; and a positive number if the first element is greater than
	 *         the second element
	 */
	int compare(int columnIndex, V e1, V e2);
}
