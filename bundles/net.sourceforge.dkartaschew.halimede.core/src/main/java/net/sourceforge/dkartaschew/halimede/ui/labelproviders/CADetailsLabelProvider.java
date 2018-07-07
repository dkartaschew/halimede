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

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.ui.composite.IColumnLabelProvider;

public class CADetailsLabelProvider extends CellLabelProvider {
	
	/**
	 * The parent viewer.
	 */
	private ColumnViewer viewer = null;

	/**
	 * Collection of current providers.
	 */
	private IColumnLabelProvider<IssuedCertificateProperties> icProvider = new IssuedCertificateColumnLabelProvider();
	private IColumnLabelProvider<CertificateKeyPairTemplate> templateProvider = new TemplateColumnLabelProvider();
	private IColumnLabelProvider<CertificateRequestProperties> csrProvider = new CSRColumnLabelProvider();
	private IColumnLabelProvider<CRLProperties> crlProvider = new CRLColumnLabelProvider();
	// TODO: Put these in a map...

	@Override
	public void update(ViewerCell cell) {
		final int col = cell.getColumnIndex();
		cell.setText(getColumnText(cell.getElement(), col));
		cell.setImage(getColumnImage(cell.getElement(), col));
	}

	@Override
	protected void initialize(ColumnViewer viewer, ViewerColumn column) {
		super.initialize(viewer, column);
		this.viewer = viewer;
	}

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
		return getColumnTooltipText(element, column);
	}

	/**
	 * Get the text for the given element and column.
	 * 
	 * @param element The element to get information from.
	 * @param columnIndex The column which the text is for.
	 * @return A string with the textual contents.
	 */
	private String getColumnText(Object element, int columnIndex) {
		if (element instanceof IssuedCertificateProperties) {
			return icProvider.getColumnText((IssuedCertificateProperties) element, columnIndex);
		}
		if (element instanceof CertificateKeyPairTemplate) {
			return templateProvider.getColumnText((CertificateKeyPairTemplate) element, columnIndex);
		}

		if (element instanceof CertificateRequestProperties) {
			return csrProvider.getColumnText((CertificateRequestProperties) element, columnIndex);
		}

		if (element instanceof CRLProperties) {
			return crlProvider.getColumnText((CRLProperties) element, columnIndex);
		}
		return null;
	}

	/**
	 * Get the tooltip text for the given element and column.
	 * 
	 * @param element The element to get information from.
	 * @param columnIndex The column which the text is for.
	 * @return A string with the textual contents.
	 */
	private String getColumnTooltipText(Object element, int columnIndex) {
		if (element instanceof IssuedCertificateProperties) {
			return icProvider.getColumnTooltipText((IssuedCertificateProperties) element, columnIndex);
		}
		if (element instanceof CertificateKeyPairTemplate) {
			return templateProvider.getColumnTooltipText((CertificateKeyPairTemplate) element, columnIndex);
		}

		if (element instanceof CertificateRequestProperties) {
			return csrProvider.getColumnTooltipText((CertificateRequestProperties) element, columnIndex);
		}

		if (element instanceof CRLProperties) {
			return crlProvider.getColumnTooltipText((CRLProperties) element, columnIndex);
		}
		return null;
	}

	/**
	 * Get the image for the cell.
	 * 
	 * @param element The element to get information from.
	 * @param columnIndex The column which the text is for.
	 * @return The image to be used in the cell.
	 */
	private Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof IssuedCertificateProperties) {
			return icProvider.getColumnImage((IssuedCertificateProperties) element, columnIndex);
		}
		if (element instanceof CertificateKeyPairTemplate) {
			return templateProvider.getColumnImage((CertificateKeyPairTemplate) element, columnIndex);
		}

		if (element instanceof CertificateRequestProperties) {
			return csrProvider.getColumnImage((CertificateRequestProperties) element, columnIndex);
		}

		if (element instanceof CRLProperties) {
			return crlProvider.getColumnImage((CRLProperties) element, columnIndex);
		}
		return null;
	}

}
