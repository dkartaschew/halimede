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

package net.sourceforge.dkartaschew.halimede.ui.composite;

import org.eclipse.swt.graphics.Image;

public interface IColumnLabelProvider<V> {

	/**
	 * Get the text for the given element and column.
	 * 
	 * @param element The element to get information from.
	 * @param columnIndex The column which the text is for.
	 * @return A string with the textual contents.
	 */
	String getColumnText(V element, int columnIndex);

	/**
	 * Get the tooltip text for the given element and column.
	 * 
	 * @param element The element to get information from.
	 * @param columnIndex The column which the text is for.
	 * @return A string with the textual contents.
	 */
	String getColumnTooltipText(V element, int columnIndex);

	/**
	 * Get the image for the cell.
	 * 
	 * @param element The element to get information from.
	 * @param columnIndex The column which the text is for.
	 * @return The image to be used in the cell.
	 */
	Image getColumnImage(V element, int columnIndex);
}
