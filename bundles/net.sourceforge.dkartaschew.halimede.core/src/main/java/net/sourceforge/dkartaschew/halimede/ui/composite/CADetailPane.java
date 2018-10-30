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
import java.util.EnumMap;

import javax.inject.Inject;

import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.CertificateManagerView;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.AbstractDetailsPane;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.IssuedCertificatesPane;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.CRLPane;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.PendingCertificatesPane;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.RevokedCertificatesPane;
import net.sourceforge.dkartaschew.halimede.ui.composite.cadetails.TemplatesPane;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityElement;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.ui.node.ElementType;
import net.sourceforge.dkartaschew.halimede.ui.node.ICertificateTreeNode;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CADetailPane extends Composite implements ISelectionChangedListener, PropertyChangeListener {

	@Inject
	private Logger logger;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * The CA Manager.
	 */
	private final CertificateAuthourityManager manager;
	/**
	 * The table view
	 */
	private final EnumMap<ElementType, AbstractDetailsPane> tableViewerElements;
	/**
	 * Stack layout for tables.
	 */
	private final StackLayout stack;

	/**
	 * The last node we received a selected changed update from.
	 */
	private ICertificateTreeNode lastNode;
	/**
	 * The current top layout type.
	 */
	private ElementType type;

	/**
	 * Create the composite.
	 * 
	 * @param parent  The parent
	 * @param style   The initial style
	 * @param manager The CA manager.
	 * @param view    The primary view.
	 */
	public CADetailPane(Composite parent, int style, CertificateAuthourityManager manager,
			CertificateManagerView view) {
		super(parent, style);
		this.manager = manager;
		this.tableViewerElements = new EnumMap<>(ElementType.class);
		this.stack = new StackLayout();
		this.setLayout(stack);
		tableViewerElements.put(ElementType.Issued, new IssuedCertificatesPane(this, this, view));
		tableViewerElements.put(ElementType.Pending, new PendingCertificatesPane(this, this, view));
		tableViewerElements.put(ElementType.Revoked, new RevokedCertificatesPane(this, this, view));
		tableViewerElements.put(ElementType.CRLs, new CRLPane(this, this, view));
		tableViewerElements.put(ElementType.Template, new TemplatesPane(this, this, view));

		// Set default layout.
		setLayout(ElementType.Issued);
	};

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		if (tableViewerElements.get(ElementType.Issued).isDisposed()) {
			return;
		}
		IStructuredSelection selection = (IStructuredSelection) event.getSelection();
		if (!selection.isEmpty()) {
			Object element = selection.getFirstElement();
			if (element != null && element instanceof ICertificateTreeNode) {
				ICertificateTreeNode node = (ICertificateTreeNode) element;

				if (node.getItems() != null) {
					// CA elements
					Object[] items = node.getItems();
					tableViewerElements.get(node.getType()).setInput(items);
					lastNode = node;
				} else {
					// CA node
					if (element instanceof CertificateAuthorityNode) {
						CertificateAuthorityNode canode = (CertificateAuthorityNode) element;
						Object[] items = ((ICertificateTreeNode) canode.getChildren()[0]).getItems();
						tableViewerElements.get(ElementType.Issued).setInput(items);
						lastNode = node;
					}
				}
				setLayout(lastNode.getType());
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		sync.asyncExec(() -> {
			if (tableViewerElements.get(ElementType.Issued).isDisposed()) {
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
								tableViewerElements.get(lastNode.getType()).setInput(items);
							}
						}
					}
					if (lastNode instanceof CertificateAuthorityNode) {
						CertificateAuthorityNode element = (CertificateAuthorityNode) lastNode;
						if (evt.getSource() == element.getCertificateAuthority()) {
							ICertificateTreeNode node = (ICertificateTreeNode) element.getChildren()[0];
							if (node.getItems() != null) {
								Object[] items = node.getItems();
								tableViewerElements.get(node.getType()).setInput(items);
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
							for (ElementType t : ElementType.values()) {
								tableViewerElements.get(t).setInput(new Object[0]);
							}
							setLayout(ElementType.Issued);
						}
					}
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
	 * Set the column layout based on the element type
	 * 
	 * @param type The element type.
	 */
	private void setLayout(ElementType type) {
		this.type = type;
		this.stack.topControl = tableViewerElements.get(type).getTableViewer().getControl();
		this.requestLayout();
	}

	/**
	 * Signal a refresh is required.
	 */
	public void refresh() {
		sync.asyncExec(() -> tableViewerElements.get(type).getTableViewer().refresh());
	}

}
