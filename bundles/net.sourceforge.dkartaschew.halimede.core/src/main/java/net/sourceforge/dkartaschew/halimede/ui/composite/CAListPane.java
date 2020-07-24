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

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.ui.CertificateManagerView;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCACertificateInformationAction;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.ui.node.INodeDecorationProvider;

public class CAListPane {

	/**
	 * The header string.
	 */
	private static final String HEADER = "Certificate Authorities";

	private final CertificateManagerView view;
	private final TreeViewer caList;
	private final Tree tree;

	/**
	 * Create the composite.
	 * 
	 * @param parent The parent
	 * @param certificateManagerView The parent view.
	 */
	public CAListPane(Composite parent, CertificateManagerView certificateManagerView) {
		this.view = certificateManagerView;

		caList = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		tree = caList.getTree();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(false);

		TreeViewerColumn viewerColumn = new TreeViewerColumn(caList, SWT.NONE);
		TreeColumn column = viewerColumn.getColumn();
		column.setImage(PluginDefaults.getResourceManager().createImage(//
				PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
		column.setAlignment(SWT.CENTER);
		column.setText(HEADER);
		column.setWidth(400);

		// Set our data and label providers.
		caList.setContentProvider(new CAContentProvider());
		caList.setLabelProvider(
				new DecoratingStyledCellLabelProvider(new CALabelProvider(), new INodeDecorationProvider(), null));
		caList.setComparator(new ViewerComparator());
		caList.setUseHashlookup(true);
		ColumnViewerToolTipSupport.enableFor(caList, ToolTip.NO_RECREATE);

		// Add context menu.
		MenuManager menuMgr = new MenuManager();
		Menu menu = menuMgr.createContextMenu(caList.getControl());
		CAContextMenu cxtMenu = new CAContextMenu(caList, view.getCAManager(), view.getPartStack());
		ContextInjectionFactory.inject(cxtMenu, view.getContext());
		menuMgr.addMenuListener(cxtMenu);
		menuMgr.setRemoveAllWhenShown(true);
		caList.getControl().setMenu(menu);

		// Add double click listener to view the CA Details if the selected element was the CA Node.
		caList.addDoubleClickListener(e -> {
			if (e.getSelection() instanceof IStructuredSelection) {
				IStructuredSelection s = (IStructuredSelection) e.getSelection();
				if (s.isEmpty()) {
					return;
				}
				if (s.getFirstElement() != null && s.getFirstElement() instanceof CertificateAuthorityNode) {
					CertificateAuthorityNode node = (CertificateAuthorityNode) s.getFirstElement();
					CertificateAuthority ca = node.getCertificateAuthority();
					if (!ca.isLocked()) {
						IAction act = new ViewCACertificateInformationAction(ca, view.getPartStack());
						ContextInjectionFactory.inject(act, view.getContext());
						act.run();
					}
				}
			}
		});
	}

	/**
	 * The <code>ContentViewer</code> implementation of this <code>Viewer</code> method invokes
	 * <code>inputChanged</code> on the content provider and then the <code>inputChanged</code> hook method. This method
	 * fails if this viewer does not have a content provider. Subclassers are advised to override
	 * <code>inputChanged</code> rather than this method, but may extend this method if required.
	 * 
	 * @param input The input object.
	 */
	public void setInput(Object input) {
		if (!caList.getTree().isDisposed()) {
			caList.setInput(input);
			caList.refresh();
		}
	}

	/**
	 * Expands all nodes of the viewer's tree, starting with the root. This method is equivalent to
	 * <code>expandToLevel(ALL_LEVELS)</code>.
	 */
	public void expandAll() {
		if (!caList.getTree().isDisposed()) {
			caList.expandAll();
		}
	}

	/**
	 * Causes the receiver to have the <em>keyboard focus</em>, such that all keyboard events will be delivered to it.
	 * Focus reassignment will respect applicable platform constraints.
	 *
	 * @return <code>true</code> if the control got focus, and <code>false</code> if it was unable to.
	 *
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
	 *                </ul>
	 *
	 */
	public boolean setFocus() {
		if (!caList.getTree().isDisposed()) {
			return caList.getControl().setFocus();
		}
		return false;
	}

	/**
	 * Adds a listener to this list. This method has no effect if the <a href="ListenerList.html#same">same</a> listener
	 * is already registered.
	 * 
	 * @param listener the non-<code>null</code> listener to add
	 */
	public void addSelectionListener(ISelectionChangedListener listener) {
		caList.addSelectionChangedListener(listener);
	}

	/**
	 * Force a UI refresh of the List.
	 */
	public void refresh() {
		if (!caList.getTree().isDisposed()) {
			caList.refresh();
		}
	}

}
