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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;

import net.sourceforge.dkartaschew.halimede.ui.CertificateManagerView;
import net.sourceforge.dkartaschew.halimede.ui.actions.CADetailsDblClickListener;
import net.sourceforge.dkartaschew.halimede.ui.composite.CADetailContextMenu;
import net.sourceforge.dkartaschew.halimede.ui.composite.CADetailPane;
import net.sourceforge.dkartaschew.halimede.ui.data.CADetailsComparator;
import net.sourceforge.dkartaschew.halimede.ui.node.ElementType;

/**
 * Common implementation components for CA Details Pane stack items.
 */
public abstract class AbstractDetailsPane {

	/**
	 * The type of element this pane is showing.
	 */
	private final ElementType type;

	/**
	 * Create a new Details Pane instance
	 * 
	 * @param type The element type to show.
	 */
	AbstractDetailsPane(ElementType type) {
		this.type = type;
	}

	/**
	 * Get the type to show.
	 * 
	 * @return The element type this pane services.
	 */
	public ElementType getType() {
		return type;
	}

	/**
	 * Get the underlying table viewer instance
	 * 
	 * @return The underlying table viewer instance
	 */
	public abstract TableViewer getTableViewer();

	/**
	 * Get the comparator instance for the table
	 * 
	 * @return The comparator instance for the table
	 */
	public abstract CADetailsComparator<?> getTableComparator();

	/**
	 * Create the columns for the table
	 */
	protected void createColumns(String[] names, int[] sizes) {
		for (int i = 0; i < sizes.length; i++) {
			TableViewerColumn col = new TableViewerColumn(getTableViewer(), SWT.NONE);
			col.getColumn().setWidth(sizes[i]);
			col.getColumn().setText(names[i]);
			col.getColumn().setMoveable(false);
			col.getColumn().setResizable(true);
			col.getColumn().addSelectionListener(getSelectionAdapter(col.getColumn(), i));
		}
	}

	/**
	 * Create A selection adapter for the column sort selector
	 * 
	 * @param column The column to sort
	 * @param index  The index of the column
	 * @return A selection Adapter.
	 */
	protected SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!column.getParent().isDisposed()) {
					getTableComparator().setColumn(index);
					int dir = getTableComparator().getDirection();
					column.getParent().setSortDirection(dir);
					column.getParent().setSortColumn(column);
					getTableViewer().refresh();
				}
			}
		};
	}

	/**
	 * Add a double click listener.
	 * 
	 * @param pane The parent pane
	 * @param view The parent view instance
	 */
	protected void addDoubleClickListener(CADetailPane pane, CertificateManagerView view) {
		CADetailsDblClickListener dblClickListener = new CADetailsDblClickListener(pane, view.getPartStack());
		ContextInjectionFactory.inject(dblClickListener, view.getContext());
		getTableViewer().addDoubleClickListener(dblClickListener);
	}

	/**
	 * Add the default context menu for the pane.
	 * 
	 * @param pane The parent pane
	 * @param view The parent view instance.
	 */
	protected void addContextMenu(CADetailPane pane, CertificateManagerView view) {
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(getTableViewer().getControl());
		CADetailContextMenu cxtMenu = new CADetailContextMenu(getTableViewer(), pane, view.getPartStack());
		ContextInjectionFactory.inject(cxtMenu, view.getContext());
		menuMgr.addMenuListener(cxtMenu);
		menuMgr.setRemoveAllWhenShown(true);
		getTableViewer().getControl().setMenu(menu);
	}

	/**
	 * Set the input for this pane
	 * 
	 * @param items The input items.
	 */
	public void setInput(Object[] items) {
		getTableViewer().setInput(items);
	}

	/**
	 * Returns <code>true</code> if the widget has been disposed, and
	 * <code>false</code> otherwise.
	 * <p>
	 * This method gets the dispose state for the widget. When a widget has been
	 * disposed, it is an error to invoke any other method (except
	 * {@link #dispose()}) using the widget.
	 * </p>
	 *
	 * @return <code>true</code> when the widget is disposed and <code>false</code>
	 *         otherwise
	 */
	public boolean isDisposed() {
		return getTableViewer().getTable().isDisposed();
	}
}
