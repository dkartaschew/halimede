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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;

import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.ui.composite.CADetailPane;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CADetailsDblClickListener implements IDoubleClickListener {

	@Inject
	private IEclipseContext context;

	@Inject
	private Logger logger;
	/**
	 * CA Details pane
	 */
	private final CADetailPane caDetailsPane;
	/**
	 * The editor to add the new part to (if applicable for the action).
	 */
	private final String editor;

	/**
	 * Create a new dbl click listener
	 * 
	 * @param caDetailsPane The parent pane.
	 * @param editor The editor to add the new part to (if applicable for the action).
	 */
	public CADetailsDblClickListener(CADetailPane caDetailsPane, String editor) {
		this.caDetailsPane = caDetailsPane;
		this.editor = editor;
	}

	@Override
	public void doubleClick(DoubleClickEvent event) {
		if (event.getSelection().isEmpty()) {
			return;
		}
		if (event.getSelection() instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			Object element = selection.getFirstElement();
			if (element instanceof IssuedCertificateProperties) {
				final IssuedCertificateProperties e = (IssuedCertificateProperties) element;
				final CertificateAuthority ca = caDetailsPane.getCertificateAuthority();
				String pw = null;
				try {
					pw = !ca.isLocked() ? ca.getPassword() : null;
				} catch (DatastoreLockedException e1) {
					// NOP - should never happen.
					logger.error(e1, ExceptionUtil.getMessage(e1));
				}
				IAction act = new ViewCertificateInformationAction(e, pw, editor);
				ContextInjectionFactory.inject(act, context);
				act.run();
			}
			if (element instanceof ICertificateKeyPairTemplate) {
				final ICertificateKeyPairTemplate e = (ICertificateKeyPairTemplate) element;
				final CertificateAuthority ca = caDetailsPane.getCertificateAuthority();
				if (!caDetailsPane.getCertificateAuthority().isLocked()) {
					IAction act = new CreateCertificateFromTemplateAction(ca, e, editor);
					ContextInjectionFactory.inject(act, context);
					act.run();
				}
			}
			if (element instanceof CertificateRequestProperties) {
				IAction act = new ViewCertificateRequestInformationAction((CertificateRequestProperties) element,
						editor);
				ContextInjectionFactory.inject(act, context);
				act.run();
			}
			if (element instanceof CRLProperties) {
				IAction act = new ViewCRLAction((CRLProperties) element, editor);
				ContextInjectionFactory.inject(act, context);
				act.run();
			}
		}

	}

}
