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

package net.sourceforge.dkartaschew.halimede.ui;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TypedListener;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.render.CertificateRenderer;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCACertificateAsHTMLListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCACertificateAsTextListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCACertificateListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCAPKCS12Listener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCAPrivateKeyListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCAPublicKeyListener;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateHeaderComposite;
import net.sourceforge.dkartaschew.halimede.ui.composite.CompositeOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@SuppressWarnings("restriction")
public class CACertificateDetailsPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.cacertificate";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "Halimede CA Details";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";

	/**
	 * The CA key.
	 */
	public static final String CA = "net.sourceforge.dkartaschew.halimede.data.certificateauthority";

	/**
	 * Reference to the containing part.
	 */
	private MPart part;
	/**
	 * The CA for issuing this certificate.
	 */
	private CertificateAuthority ca;
	/**
	 * Primary composite
	 */
	private CompositeOutputRenderer composite;

	@Inject
	private Logger logger;

	@Inject
	private EPartService partService;

	@Inject
	private IEclipseContext context;

	/**
	 * Create contents of the view part.
	 * 
	 * @param part The part which this is part of.
	 * @param parent The parent composite
	 */
	@PostConstruct
	public void createControls(MPart part, Composite parent) {
		this.part = part;
		if (this.part != null) {
			this.ca = (CertificateAuthority) this.part.getTransientData().get(CA);
			if (ca == null) {
				logger.info("View CA Certificate Details missing CA information. Closing");
				close();
				return;
			}
		}

		// Now construct the controls.
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		String desc = Strings.trim(ca.getDescription(), PluginDefaults.PART_HEADER_LENGTH);
		HeaderCompositeMenuModel headerModel = new HeaderCompositeMenuModel();
		headerModel.setHeader("Certificate Authority '" + desc + "' Information");
		headerModel.setToolItem("Certificate Authority '" + ca.getDescription() + "' Information");
		headerModel.setToolItemImage(PluginDefaults.IMG_CERTIFICATE);
		headerModel.setToolItemSelectionListener(new ExportCACertificateListener(ca, false));

		headerModel.setDropDownMenu(new Menu(parent.getShell(), SWT.POP_UP));
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the CA Certificate");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the CA Certificate Chain");
		new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the CA Private Key");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export as PKCS#12 Keystore");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the CA Public Key");
		new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Details as TXT");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Details as HTML");
		headerModel.getMenuItems().addAll(Arrays.asList(headerModel.getDropDownMenu().getItems()));

		CertificateHeaderComposite header = new CertificateHeaderComposite(parent, SWT.NONE, headerModel);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.composite = new CompositeOutputRenderer(parent, SWT.NONE);
		CertificateRenderer renderer = new CertificateRenderer(ca);
		renderer.render(composite);
		composite.finaliseRender();
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		/*
		 * Set the action parameters
		 */
		headerModel.getMenuItems().get(0).addSelectionListener(headerModel.getToolItemSelectionListener());
		headerModel.getMenuItems().get(1).addSelectionListener(new ExportCACertificateListener(ca, true));
		headerModel.getMenuItems().get(3).addSelectionListener(new ExportCAPrivateKeyListener(ca));
		headerModel.getMenuItems().get(4).addSelectionListener(new ExportCAPKCS12Listener(ca));
		headerModel.getMenuItems().get(5).addSelectionListener(new ExportCAPublicKeyListener(ca));
		headerModel.getMenuItems().get(7).addSelectionListener(new ExportCACertificateAsTextListener(ca));
		headerModel.getMenuItems().get(8).addSelectionListener(new ExportCACertificateAsHTMLListener(ca));
		injectMenuItems(headerModel.getMenuItems());
	}

	/**
	 * Inject all menu items.
	 * 
	 * @param menuItems The collection of menu items to inject.
	 */
	private void injectMenuItems(List<MenuItem> menuItems) {
		for (MenuItem menu : menuItems) {
			Listener[] listeners = menu.getListeners(SWT.Selection);
			if (listeners != null && listeners.length > 0) {
				for (Listener l : listeners) {
					if (l instanceof TypedListener) {
						TypedListener tl = (TypedListener) l;
						ContextInjectionFactory.inject(tl.getEventListener(), context);
					}
					ContextInjectionFactory.inject(l, context);
				}
			}
		}
	}

	@PreDestroy
	public void dispose() {
		// NOP
	}

	@Focus
	public void setFocus() {
		if (this.composite != null) {
			Display.getDefault().asyncExec(() -> {
				if (!this.composite.isDisposed()) {
					this.composite.setFocus();
				}
			});
		}
	}

	/**
	 * Close the part.
	 * <p>
	 * Note: This an async request.
	 */
	public void close() {
		Display.getDefault().asyncExec(() -> {
			// This will call @PreDestroy
			partService.hidePart(part, true);
		});
	}

}
