/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2021 Darran Kartaschew 
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

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateCRLAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateCertificateFromCSRAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateCertificateFromTemplateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateIssuedCertificateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateNewTemplateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.DeleteCertificateRequestAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.DeleteTemplateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.DuplicateTemplateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.EditTemplateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ImportCSRAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.RevokeCertificateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.UpdateCRLCommentsAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.UpdateCertificateCommentsAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.UpdateCertificateRequestsCommentsAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCRLAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCertificateInformationAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCertificateRequestInformationAction;
import net.sourceforge.dkartaschew.halimede.ui.node.ElementType;
import net.sourceforge.dkartaschew.halimede.ui.util.ActionContributionItemEx;

public class CADetailContextMenu implements IMenuListener {

	/**
	 * CA Details pane
	 */
	private final CADetailPane caDetailsPane;
	/**
	 * Parent View.
	 */
	private final TableViewer viewer;
	/**
	 * The editor to add the new part to (if applicable for the action).
	 */
	private final String editor;

	@Inject
	private IEclipseContext context;

	/**
	 * Create a context menu for the given treeviewer.
	 * 
	 * @param viewer The parent viewer
	 * @param manager The parent manager.
	 * @param editor The ID of the partstack to add to.
	 */
	public CADetailContextMenu(TableViewer viewer, CADetailPane manager, String editor) {
		this.viewer = viewer;
		this.caDetailsPane = manager;
		this.editor = editor;
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {

		if (viewer.getSelection().isEmpty()) {
			CertificateAuthority ca = caDetailsPane.getCertificateAuthority();
			ElementType type = caDetailsPane.getElementType();
			if (ca == null || type == null) {
				return;
			}
			switch (type) {
			case Pending:
				manager.add(toACI(new ImportCSRAction(ca)));
				break;
			case CRLs:
			case Revoked:
				manager.add(toACI(new CreateCRLAction(ca, editor)));
				break;
			case Template:
				manager.add(toACI(new CreateNewTemplateAction(ca, editor)));
				break;
			case Issued:
			default:
				manager.add(toACI(new CreateIssuedCertificateAction(ca, editor)));
				break;
			}
			injectMenuItems(manager);
			return;
		}
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			Object element = selection.getFirstElement();
			final CertificateAuthority ca = caDetailsPane.getCertificateAuthority();
			if (element instanceof IssuedCertificateProperties) {
				final IssuedCertificateProperties e = (IssuedCertificateProperties) element;

				String pw = null;
				try {
					pw = !ca.isLocked() ? ca.getPassword() : null;
				} catch (DatastoreLockedException e1) {
					// NOP - should never happen.
				}
				manager.add(toACI(new ViewCertificateInformationAction(e, pw, editor)));
				manager.add(toACI(new UpdateCertificateCommentsAction(e, ca, caDetailsPane)));
				if (e.getProperty(Key.csrStore) != null) {
					manager.add(toACI(new ViewCertificateRequestInformationAction(e, editor)));
				}
				if (e.getProperty(Key.revokeDate) == null) {
					manager.add(toACI(new RevokeCertificateAction(e, ca)));
					manager.add(new Separator());
					manager.add(toACI(new CreateIssuedCertificateAction(ca, editor)));
				}
				if (e.getProperty(Key.revokeDate) != null) {
					manager.add(new Separator());
					manager.add(toACI(new CreateCRLAction(ca, editor)));
				}
			}
			if (element instanceof ICertificateKeyPairTemplate) {
				final ICertificateKeyPairTemplate e = (ICertificateKeyPairTemplate) element;
				manager.add(toACI(new CreateCertificateFromTemplateAction(ca, e, editor)));
				manager.add(toACI(new EditTemplateAction(ca, e, editor)));
				manager.add(toACI(new DuplicateTemplateAction(ca, e)));
				manager.add(toACI(new DeleteTemplateAction(e, ca)));
				manager.add(new Separator());
				manager.add(toACI(new CreateNewTemplateAction(ca, editor)));
			}
			if (element instanceof CertificateRequestProperties) {
				CertificateRequestProperties e = (CertificateRequestProperties) element;
				manager.add(toACI(new ViewCertificateRequestInformationAction(e, editor)));
				manager.add(toACI(new CreateCertificateFromCSRAction(ca, e, editor, null)));
				manager.add(toACI(new UpdateCertificateRequestsCommentsAction(e, ca, caDetailsPane)));
				manager.add(toACI(new DeleteCertificateRequestAction(e, ca, null)));
				manager.add(new Separator());
				manager.add(toACI(new ImportCSRAction(ca)));
			}
			if (element instanceof CRLProperties) {
				manager.add(toACI(new ViewCRLAction((CRLProperties) element, editor)));
				manager.add(toACI(new UpdateCRLCommentsAction((CRLProperties) element, ca, caDetailsPane)));
				manager.add(new Separator());
				manager.add(toACI(new CreateCRLAction(ca, editor)));
			}
			injectMenuItems(manager);
		}
	}

	/**
	 * Construct an Action Contribution Item.
	 * 
	 * @param action The action
	 * @return An Action Contribution Item.
	 */
	private ActionContributionItem toACI(IAction action) {
		return new ActionContributionItemEx(action);
	}

	/**
	 * Force injection of all menu items.
	 * 
	 * @param manager The menu manager.
	 */
	private void injectMenuItems(IMenuManager manager) {
		// Inject all menu items.
		for (IContributionItem i : manager.getItems()) {
			if (i instanceof ActionContributionItem) {
				// Ensure we get the underlying action.
				ContextInjectionFactory.inject(((ActionContributionItem) i).getAction(), context);
			}
			ContextInjectionFactory.inject(i, context);
		}
	}
}
