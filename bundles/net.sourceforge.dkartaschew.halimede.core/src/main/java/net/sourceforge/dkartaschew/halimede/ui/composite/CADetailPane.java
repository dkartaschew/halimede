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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.EnumMap;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.CertificateManagerView;
import net.sourceforge.dkartaschew.halimede.ui.actions.CADetailsDblClickListener;
import net.sourceforge.dkartaschew.halimede.ui.data.CADetailsComparator;
import net.sourceforge.dkartaschew.halimede.ui.labelproviders.CADetailsLabelProvider;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityElement;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.ui.node.ElementType;
import net.sourceforge.dkartaschew.halimede.ui.node.ICertificateTreeNode;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CADetailPane implements ISelectionChangedListener, PropertyChangeListener {

	public final static int COLUMN_DESCRIPTION = 0;
	public final static int COLUMN_CRL_NUMBER = 1;
	public final static int COLUMN_SUBJECT = 2;
	public final static int COLUMN_KEY_TYPE = 3;
	public final static int COLUMN_ISSUE_DATE = 4;
	public final static int COLUMN_START_DATE = 5;
	public final static int COLUMN_EXPIRY_DATE = 6;
	public final static int COLUMN_IMPORT_DATE = 7;
	public final static int COLUMN_REVOKE_DATE = 8;
	public final static int COLUMN_REVOKE_REASON = 9;
	public final static int COLUMN_COMMENTS = 10;
	
	public final static int COLUMN_MAX = 11;
	
	@Inject
	private Logger logger;

	private final String[] COLUMN_NAMES = { "Description", "CRL", "Subject", "Key Type", "Created", "Start Date",
			"Expiry", "Import", "Revoke", "Reason", "Comments" };

	private final int[] COLUMN_SIZES = { 180, 100, 180, 100, 130, 130, 130, 130, 130, 130, 200 };
	private final int[] ISSUED_COLUMN_SIZES = { 180, 0, 180, 100, 130, 130, 130, 0, 0, 0, 200 };
	private final int[] REVOKED_COLUMN_SIZES = { 180, 0, 180, 100, 130, 130, 130, 0, 130, 130, 200 };
	private final int[] PENDING_COLUMN_SIZES = { 0, 0, 580, 120, 0, 0, 0, 130, 0, 0, 200 };
	private final int[] TEMPLATE_COLUMN_SIZES = { 480, 0, 380, 100, 130, 0, 0, 0, 0, 0, 0 };
	private final int[] CRLS_COLUMN_SIZES = { 0, 100, 180, 0, 130, 0, 130, 0, 0, 0, 200 };

	/**
	 * The CA Manager.
	 */
	private final CertificateAuthourityManager manager;
	/**
	 * The table view
	 */
	private final TableViewer tableViewerElement;

	/**
	 * The last node we received a selected changed update from.
	 */
	private ICertificateTreeNode lastNode;
	/**
	 * The column comparator
	 */
	private final CADetailsComparator comparator;
	
	/**
	 * Column layout values.
	 */
	private final EnumMap<ElementType, int[]> columnLayouts;

	/**
	 * Create the composite.
	 * 
	 * @param parent The parent
	 * @param style The initial style
	 * @param manager The CA manager.
	 * @param view The primary view.
	 */
	public CADetailPane(Composite parent, int style, CertificateAuthourityManager manager,
			CertificateManagerView view) {
		this.manager = manager;
		tableViewerElement = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		Table table = tableViewerElement.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		comparator = new CADetailsComparator();
		// set the content provider
		createColumns();
		tableViewerElement.setContentProvider(ArrayContentProvider.getInstance());
		tableViewerElement.setLabelProvider(new CADetailsLabelProvider());
		ColumnViewerToolTipSupport.enableFor(tableViewerElement);
		tableViewerElement.setComparator(comparator);

		// Add double click listener.
		CADetailsDblClickListener dblClickListener = new CADetailsDblClickListener(this, view.getPartStack());
		ContextInjectionFactory.inject(dblClickListener, view.getContext());
		tableViewerElement.addDoubleClickListener(dblClickListener);

		// Add context menu.
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(tableViewerElement.getControl());
		CADetailContextMenu cxtMenu = new CADetailContextMenu(tableViewerElement, this, view.getPartStack());
		ContextInjectionFactory.inject(cxtMenu, view.getContext());
		menuMgr.addMenuListener(cxtMenu);
		menuMgr.setRemoveAllWhenShown(true);
		tableViewerElement.getControl().setMenu(menu);
		
		/*
		 * Set column layouts.
		 */
		columnLayouts = new EnumMap<ElementType, int[]>(ElementType.class);
		columnLayouts.put(ElementType.Issued, ISSUED_COLUMN_SIZES);
		columnLayouts.put(ElementType.Revoked, REVOKED_COLUMN_SIZES);
		columnLayouts.put(ElementType.CRLs, CRLS_COLUMN_SIZES);
		columnLayouts.put(ElementType.Pending, PENDING_COLUMN_SIZES);
		columnLayouts.put(ElementType.Template, TEMPLATE_COLUMN_SIZES);
		
		// Set default layout.
		setLayout(ElementType.Issued);
	}

	/**
	 * Create the columns for the table
	 */
	private void createColumns() {
		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			TableViewerColumn col = new TableViewerColumn(tableViewerElement, SWT.NONE);
			col.getColumn().setWidth(COLUMN_SIZES[i]);
			col.getColumn().setText(COLUMN_NAMES[i]);
			col.getColumn().setMoveable(false);
			col.getColumn().setResizable(true);
			col.getColumn().addSelectionListener(getSelectionAdapter(col.getColumn(), i));
		}
	}

	/**
	 * Create A selection adapter for the column sort selector
	 * @param column The column to sort
	 * @param index The index of the column
	 * @return A selection Adapter.
	 */
	private SelectionAdapter getSelectionAdapter(final TableColumn column,
            final int index) {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
            	if(!tableViewerElement.getTable().isDisposed()) {
                comparator.setColumn(index);
                int dir = comparator.getDirection();
                tableViewerElement.getTable().setSortDirection(dir);
                tableViewerElement.getTable().setSortColumn(column);
                tableViewerElement.refresh();
            	}
            }
        };
    }
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if(tableViewerElement.getTable().isDisposed()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		if (!selection.isEmpty()) {
			Object element = selection.getFirstElement();
			if (element != null && element instanceof ICertificateTreeNode) {
				if(lastNode != null) {
					storeLayout(lastNode.getType());
				}
				ICertificateTreeNode node = (ICertificateTreeNode) element;
				if (node.getItems() != null) {
					// CA elements
					Object[] items = node.getItems();
					Arrays.sort(items, comparator);
					tableViewerElement.setInput(items);
					lastNode = node;
				} else {
					// CA node
					if (element instanceof CertificateAuthorityNode) {
						CertificateAuthorityNode canode = (CertificateAuthorityNode) element;
						Object[] items = ((ICertificateTreeNode) canode.getChildren()[0]).getItems();
						Arrays.sort(items, comparator);
						tableViewerElement.setInput(items);
						lastNode = node;
					}
				}
				comparator.setColumn(CADetailsComparator.DEFAULT_SORT);
				comparator.setDirection(SWT.DOWN);
				setLayout(lastNode.getType());
				tableViewerElement.getTable().setSortColumn(null);
				tableViewerElement.getTable().setSortDirection(SWT.NONE);
				tableViewerElement.refresh();
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		Display.getDefault().asyncExec(() -> {
			if(tableViewerElement.getTable().isDisposed()) {
				return;
			}
			if (lastNode != null) {
				try {
					// If the update came from the
					if (lastNode instanceof CertificateAuthorityElement) {
						CertificateAuthorityElement element = (CertificateAuthorityElement) lastNode;
						if (evt.getSource() == element.getParent().getCertificateAuthority()) {
							if (lastNode.getItems() != null) {
								Object[] items = lastNode.getItems();
								Arrays.sort(items, comparator);
								tableViewerElement.setInput(items);
							}
						}
					}
					if (lastNode instanceof CertificateAuthorityNode) {
						CertificateAuthorityNode element = (CertificateAuthorityNode) lastNode;
						if (evt.getSource() == element.getCertificateAuthority()) {
							ICertificateTreeNode node = (ICertificateTreeNode) element.getChildren()[0];
							if (node.getItems() != null) {
								Object[] items = node.getItems();
								Arrays.sort(items, comparator);
								tableViewerElement.setInput(items);
							}
						}
					}
					if (evt.getSource() == manager) {
						// Ensure the last node's CA still exists. If not, clear the table...
						CertificateAuthority ca = null;
						if (lastNode instanceof CertificateAuthorityElement) {
							ca = ((CertificateAuthorityElement) lastNode).getParent().getCertificateAuthority();
						}
						if (lastNode instanceof CertificateAuthorityNode) {
							ca = ((CertificateAuthorityNode) lastNode).getCertificateAuthority();
						}
						if (!manager.getCertificateAuthorities().contains(ca)) {
							lastNode = null;
							tableViewerElement.setInput(new Object[0]);
							setLayout(ElementType.Issued);
						}
					}
					comparator.setColumn(CADetailsComparator.DEFAULT_SORT);
					comparator.setDirection(SWT.DOWN);
					tableViewerElement.getTable().setSortColumn(null);
					tableViewerElement.getTable().setSortDirection(SWT.NONE);
					tableViewerElement.refresh();
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, ExceptionUtil.getMessage(e));
				}
			}
		});
	}

	public CertificateAuthority getCertificateAuthority() {
		if (lastNode != null && lastNode instanceof CertificateAuthorityElement) {
			CertificateAuthorityElement element = (CertificateAuthorityElement) lastNode;
			return element.getParent().getCertificateAuthority();
		}
		if (lastNode != null && lastNode instanceof CertificateAuthorityNode) {
			CertificateAuthorityNode element = (CertificateAuthorityNode) lastNode;
			return element.getCertificateAuthority();
		}
		return null;
	}

	/**
	 * Force a refresh of the UI.
	 */
	public void refresh() {
		Display.getDefault().asyncExec(() -> {
			if(tableViewerElement.getTable().isDisposed()) {
				return;
			}
			tableViewerElement.refresh();
		});
	}
	
	/**
	 * Set the column layout based on the element type
	 * 
	 * @param type The element type.
	 */
	private void setLayout(ElementType type) {
		int[] widths = columnLayouts.get(type);
		for (int i = 0; i < COLUMN_MAX; i++) {
			tableViewerElement.getTable().getColumn(i).setWidth(widths[i]);
		}
	}
	
	/**
	 * Store the column layout based on the element type
	 * 
	 * @param type The element type.
	 */
	private void storeLayout(ElementType type) {
		int[] widths = columnLayouts.get(type);
		for (int i = 0; i < COLUMN_MAX; i++) {
			widths[i] = tableViewerElement.getTable().getColumn(i).getWidth();
		}
	}
}
