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
package net.sourceforge.dkartaschew.halimede.ui.composite.cadetails;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.ui.CertificateManagerView;
import net.sourceforge.dkartaschew.halimede.ui.composite.CADetailPane;
import net.sourceforge.dkartaschew.halimede.ui.data.CADetailsComparator;
import net.sourceforge.dkartaschew.halimede.ui.data.CRLColumnComparator;
import net.sourceforge.dkartaschew.halimede.ui.labelproviders.CRLColumnLabelProvider;
import net.sourceforge.dkartaschew.halimede.ui.node.ElementType;

public class CRLPane extends AbstractDetailsPane<CRLProperties> {

	public static final int COLUMN_CRL_NUMBER = 0;
	public static final int COLUMN_SUBJECT = 1;
	public static final int COLUMN_START_DATE = 2;
	public static final int COLUMN_EXPIRY_DATE = 3;
	public static final int COLUMN_COMMENTS = 4;

	private final String[] COLUMN_NAMES = { "CRL", "Subject", "Start Date", "Expiry", "Comments" };

	private final int[] COLUMN_SIZES = { 100, 180, 130, 130, 200 };

	/**
	 * Table view for the CA's Issued Certificates
	 */
	private TableViewer tableViewer;

	/**
	 * The column comparator
	 */
	private final CADetailsComparator<CRLProperties> comparator;

	public CRLPane(Composite parent, CADetailPane pane, CertificateManagerView view) {
		super(ElementType.CRLs);
		this.comparator = new CADetailsComparator<CRLProperties>(new CRLColumnComparator());
		tableViewer = new TableViewer(parent,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION | SWT.DOUBLE_BUFFERED);
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		// set the content provider
		createColumns(COLUMN_NAMES, COLUMN_SIZES);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setLabelProvider(new CRLColumnLabelProvider());
		ColumnViewerToolTipSupport.enableFor(tableViewer);
		tableViewer.setComparator(comparator);

		// Add double click listener.
		addDoubleClickListener(pane, view);

		// Add context menu.
		addContextMenu(pane, view);
	}

	@Override
	public TableViewer getTableViewer() {
		return tableViewer;
	}

	@Override
	public CADetailsComparator<CRLProperties> getTableComparator() {
		return comparator;
	}

}
