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
import org.eclipse.jface.viewers.TreeViewer;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.actions.CloseCAAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateCRLAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateIssuedCertificateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateNewCAAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateNewCAExistingMaterialAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateNewTemplateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.DeleteCAAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ImportCSRAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.LockUnlockAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.OpenCAAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.RestoreCAAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCACertificateInformationAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.BackupCAAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.CASettingsAction;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityElement;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.ui.util.ActionContributionItemEx;

public class CAContextMenu implements IMenuListener {

	/**
	 * CA Manager
	 */
	private final CertificateAuthourityManager manager;
	/**
	 * Parent View.
	 */
	private final TreeViewer viewer;
	/**
	 * The part stack to add to.
	 */
	private final String editor;

	@Inject
	private IEclipseContext context;

	/**
	 * Create a context menu for the given treeviewer.
	 * 
	 * @param viewer The parent viewer
	 * @param manager The parent manager.
	 * @param editor The part stack to add to.
	 */
	public CAContextMenu(TreeViewer viewer, CertificateAuthourityManager manager, String editor) {
		this.viewer = viewer;
		this.manager = manager;
		this.editor = editor;
	}

	@Override
	public void menuAboutToShow(IMenuManager manager) {

		if (viewer.getSelection().isEmpty()) {
			manager.add(toACI(new CreateNewCAAction(this.manager)));
			manager.add(toACI(new CreateNewCAExistingMaterialAction(this.manager)));
			manager.add(toACI(new OpenCAAction(this.manager)));
			manager.add(toACI(new RestoreCAAction(this.manager)));
			injectMenuItems(manager);
			return;
		}
		if (viewer.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
			Object object = selection.getFirstElement();
			if (object instanceof CertificateAuthorityNode) {
				CertificateAuthorityNode element = (CertificateAuthorityNode) object;
				if (!element.getCertificateAuthority().isLocked()) {
					manager.add(toACI(new ViewCACertificateInformationAction(element.getCertificateAuthority(), editor)));
					manager.add(toACI(new CASettingsAction(element)));
				}
				manager.add(toACI(new LockUnlockAction(viewer, element)));
				manager.add(toACI(new BackupCAAction(element)));
				manager.add(toACI(new CloseCAAction(this.manager, element)));
				manager.add(toACI(new DeleteCAAction(this.manager, element)));
				manager.add(new Separator());
				manager.add(toACI(new CreateNewCAAction(this.manager)));
				manager.add(toACI(new CreateNewCAExistingMaterialAction(this.manager)));
				manager.add(toACI(new OpenCAAction(this.manager)));
				manager.add(toACI(new RestoreCAAction(this.manager)));
				manager.add(new Separator());
				if (!element.getCertificateAuthority().isLocked()) {
					manager.add(toACI(new CreateIssuedCertificateAction(element.getCertificateAuthority(), editor)));
				}
				manager.add(toACI(new CreateNewTemplateAction(element.getCertificateAuthority(), editor)));
				if (!element.getCertificateAuthority().isLocked()) {
					manager.add(toACI(new CreateCRLAction(element.getCertificateAuthority(), editor)));
				}
			}
			if (object instanceof CertificateAuthorityElement) {
				CertificateAuthorityElement element = (CertificateAuthorityElement) object;
				CertificateAuthority ca = element.getParent().getCertificateAuthority();
				switch (element.getType()) {
				case Issued:
					if (!element.getParent().getCertificateAuthority().isLocked()) {
						manager.add(toACI(new CreateIssuedCertificateAction(ca, editor)));
					}
					break;
				case Pending:
					manager.add(toACI(new ImportCSRAction(ca)));
					break;
				case Template:
					manager.add(toACI(new CreateNewTemplateAction(ca, editor)));
					break;
				case Revoked:
				case CRLs:
					if (!ca.isLocked()) {
						manager.add(toACI(new CreateCRLAction(ca, editor)));
					}
					break;
				}
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
				ContextInjectionFactory.inject(((ActionContributionItem) i).getAction(), context);
			}
			ContextInjectionFactory.inject(i, context);
		}
	}
}
