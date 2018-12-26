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

package net.sourceforge.dkartaschew.halimede.ui.labelproviders;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import net.sourceforge.dkartaschew.halimede.ui.composite.IColumnLabelProvider;

public abstract class CADetailsLabelProvider<V> extends CellLabelProvider implements IColumnLabelProvider<V>{
	
	/**
	 * The parent viewer.
	 */
	private ColumnViewer viewer = null;

	@SuppressWarnings("unchecked")
	@Override
	public void update(ViewerCell cell) {
		final int col = cell.getColumnIndex();
		cell.setText(getColumnText((V)cell.getElement(), col));
		cell.setImage(getColumnImage((V)cell.getElement(), col));
	}

	@Override
	protected void initialize(ColumnViewer viewer, ViewerColumn column) {
		super.initialize(viewer, column);
		this.viewer = viewer;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getToolTipText(Object element) {
		if (viewer == null) {
			return null;
		}
		/*
		 * OK, the CellLabelProvider interface doesn't provide the parent ViewerCell for obtaining the tooltip,
		 * therefore we need to get the cursor position, and request the parent viewer for the cell based on the cursor
		 * position, and from that we can get the column we are servicing, not just the entire row...
		 */
		Point pt = viewer.getControl().toControl(Display.getDefault().getCursorLocation());

		ViewerCell cell = this.viewer.getCell(pt);
		int column = cell != null ? cell.getColumnIndex() : -1;
		return getColumnTooltipText((V)element, column);
	}

}
