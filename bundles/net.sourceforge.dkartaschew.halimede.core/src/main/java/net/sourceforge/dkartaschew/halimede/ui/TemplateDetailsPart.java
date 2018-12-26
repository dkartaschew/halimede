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

package net.sourceforge.dkartaschew.halimede.ui;

import java.time.ZonedDateTime;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
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
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateTemplateListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.EditTemplateListener;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateHeaderComposite;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateTemplateComposite;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.ui.util.MenuUtils;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

public class TemplateDetailsPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.newtemplate";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "Template";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";

	/**
	 * The CA key.
	 */
	public static final String CA = "net.sourceforge.dkartaschew.halimede.data.certificateauthority";
	/**
	 * The template model.
	 */
	public static final String MODEL = "net.sourceforge.dkartaschew.halimede.data.model";

	/**
	 * Reference to the containing part.
	 */
	private MPart part;
	/**
	 * The CA for issuing this certificate.
	 */
	private CertificateAuthority ca;

	/**
	 * The model to use for the UI.
	 */
	private NewCertificateModel model;
	/**
	 * Primary composite
	 */
	private CertificateTemplateComposite composite;

	@Inject
	private EPartService partService;
	
	@Inject
	private IEclipseContext context;
	
	@Inject 
	private UISynchronize sync;

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
				throw new IllegalArgumentException("Missing Certificate Authority");
			}
			this.model = (NewCertificateModel) this.part.getTransientData().get(MODEL);
		}
		boolean editModel = (model != null);
		if (model == null) {
			model = new NewCertificateModel(ca);
			ZonedDateTime startDate = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
			ZonedDateTime expiryDate = startDate.plusDays(ca.getExpiryDays());
			model.setStartDate(startDate);
			model.setExpiryDate(expiryDate);
			model.setCreationDate(startDate);
			model.setRepresentsTemplateOnly(true);
		}

		// Now construct the controls.
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		HeaderCompositeMenuModel headerModel = new HeaderCompositeMenuModel();
		headerModel.setHeader(editModel ? "Edit Template" : "New Template");
		headerModel.setToolItem(editModel ? "Edit an existing Template" : "Create a new Certificate Template");
		headerModel.setToolItemImage(PluginDefaults.IMG_CERTIFICATE);
		headerModel.setToolItemSelectionListener(new EditTemplateListener(model, this));

		headerModel.setDropDownMenu(new Menu(parent.getShell(), SWT.POP_UP));
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Save the template");
		new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Save as a new Certificate Template");
		headerModel.getMenuItems().addAll(Arrays.asList(headerModel.getDropDownMenu().getItems()));

		CertificateHeaderComposite header = new CertificateHeaderComposite(parent, SWT.NONE, headerModel);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.composite = new CertificateTemplateComposite(parent, SWT.NONE, model, header);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		/*
		 * Set the action parameters
		 */
		headerModel.getMenuItems().get(0).addSelectionListener(headerModel.getToolItemSelectionListener());
		headerModel.getMenuItems().get(2).addSelectionListener(new CreateTemplateListener(model));
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
	
	/**
	 * Sets the value of the '{@link org.eclipse.e4.ui.model.application.ui.basic.MPart#isCloseable <em>Closeable</em>}'
	 * attribute.
	 * 
	 * @param value the new value of the '<em>Closeable</em>' attribute.
	 * @see #isCloseable()
	 */
	public void setClosable(boolean value) {
		sync.asyncExec(() -> {
			this.part.setCloseable(value);
		});
	}
}
