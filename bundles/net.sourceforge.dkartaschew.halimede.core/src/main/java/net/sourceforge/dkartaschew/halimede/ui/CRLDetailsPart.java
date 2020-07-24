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

package net.sourceforge.dkartaschew.halimede.ui;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties;
import net.sourceforge.dkartaschew.halimede.data.CRLProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.render.CRLRenderer;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCRLAsHTMLListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCRLAsTextListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCRLListener;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateHeaderComposite;
import net.sourceforge.dkartaschew.halimede.ui.composite.CompositeOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.ui.util.MenuUtils;

@SuppressWarnings("restriction")
public class CRLDetailsPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.crl";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "CRL";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";
	/**
	 * The CRL Properties model.
	 */
	public static final String MODEL = "net.sourceforge.dkartaschew.halimede.data.model";

	/**
	 * Reference to the containing part.
	 */
	private MPart part;

	/**
	 * The model to use for the UI.
	 */
	private CRLProperties model;
	/**
	 * Primary composite
	 */
	private CompositeOutputRenderer composite;

	@Inject
	private EPartService partService;

	@Inject
	private IEclipseContext context;
	
	@Inject 
	private UISynchronize sync;

	@Inject
	private Logger logger;

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
			this.model = (CRLProperties) this.part.getTransientData().get(MODEL);
			if (model == null) {
				logger.info("View CRL Details missing CRL information. Closing");
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

		HeaderCompositeMenuModel headerModel = new HeaderCompositeMenuModel();
		headerModel.setHeader("CRL #" + model.getProperty(Key.crlSerialNumber) + " : " //
				+ model.getProperty(Key.issueDate));
		headerModel.setToolItem("Export the CRL");
		headerModel.setToolItemImage(PluginDefaults.IMG_CERTIFICATE);
		headerModel.setToolItemSelectionListener(new ExportCRLListener(model));

		headerModel.setDropDownMenu(new Menu(parent.getShell(), SWT.POP_UP));
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the CRL");
		new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Revocation as TXT");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Revocation as HTML");
		headerModel.getMenuItems().addAll(Arrays.asList(headerModel.getDropDownMenu().getItems()));

		CertificateHeaderComposite header = new CertificateHeaderComposite(parent, SWT.NONE, headerModel);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.composite = new CompositeOutputRenderer(parent, SWT.NONE, //
				"CRL #" + model.getProperty(Key.crlSerialNumber) + " : " //
				+ model.getProperty(Key.issueDate));
		CRLRenderer renderer = new CRLRenderer(model, 64);
		renderer.render(composite);
		composite.finaliseRender();
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.redraw();
		/*
		 * Set the action parameters
		 */
		headerModel.getMenuItems().get(0).addSelectionListener(headerModel.getToolItemSelectionListener());
		headerModel.getMenuItems().get(2).addSelectionListener(new ExportCRLAsTextListener(model));
		headerModel.getMenuItems().get(3).addSelectionListener(new ExportCRLAsHTMLListener(model));
		MenuUtils.injectMenuItems(headerModel.getMenuItems(), context);
	}

	@PreDestroy
	public void dispose() {
		// NOP
	}

	@Focus
	public void setFocus() {
		if (this.composite != null) {
			sync.asyncExec(() -> {
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
		sync.asyncExec(() -> {
			// This will call @PreDestroy
			partService.hidePart(part, true);
		});
	}
}
