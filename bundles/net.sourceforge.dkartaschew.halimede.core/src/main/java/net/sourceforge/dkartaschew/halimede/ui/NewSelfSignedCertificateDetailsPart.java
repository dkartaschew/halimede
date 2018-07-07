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

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
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
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateSelfSignedCertificateListener;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateHeaderComposite;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateTemplateComposite;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

public class NewSelfSignedCertificateDetailsPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.newselfcertificate";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "Certificate";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";
	/**
	 * The data model key.
	 */
	public static final String MODEL = "net.sourceforge.dkartaschew.halimede.data.model";
	/**
	 * The editor part stack ID.
	 */
	public static final String PARTSTACK_EDITOR = "net.sourceforge.dkartaschew.halimede.data.editor";

	/**
	 * Reference to the containing part.
	 */
	private MPart part;
	/**
	 * The model to use for the UI.
	 */
	private NewCertificateModel model;
	/**
	 * MPartStack to add to.
	 */
	private String editor;

	/**
	 * Primary composite
	 */
	private CertificateTemplateComposite composite;

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
			this.model = (NewCertificateModel) this.part.getTransientData().get(MODEL);
			this.editor = (String) this.part.getTransientData().get(PARTSTACK_EDITOR);
		}
		if (model == null) {
			model = new NewCertificateModel(null);
			ZonedDateTime startDate = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
			ZonedDateTime expiryDate = startDate.plusDays(365);

			model.setStartDate(startDate);
			model.setExpiryDate(expiryDate);
			model.setCreationDate(startDate);
		}

		// Now construct the controls.
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		HeaderCompositeMenuModel headerModel = new HeaderCompositeMenuModel();
		headerModel.setHeader("New Self Signed Certificate");
		headerModel.setToolItem("Create the certificate");
		headerModel.setToolItemImage(PluginDefaults.IMG_CERTIFICATE);
		headerModel.setToolItemSelectionListener(new CreateSelfSignedCertificateListener(model, this, editor));

		headerModel.setDropDownMenu(new Menu(parent.getShell(), SWT.POP_UP));
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Create the certificate");
		headerModel.getMenuItems().addAll(Arrays.asList(headerModel.getDropDownMenu().getItems()));

		CertificateHeaderComposite header = new CertificateHeaderComposite(parent, SWT.NONE, headerModel);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.composite = new CertificateTemplateComposite(parent, SWT.NONE, model, header);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		((CreateSelfSignedCertificateListener) headerModel.getToolItemSelectionListener())
				.setBindingContext(composite.getBindingContext());

		/*
		 * Set the action parameters
		 */
		headerModel.getMenuItems().get(0).addSelectionListener(headerModel.getToolItemSelectionListener());

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

	/**
	 * Sets the value of the '{@link org.eclipse.e4.ui.model.application.ui.basic.MPart#isCloseable <em>Closeable</em>}'
	 * attribute.
	 * 
	 * @param value the new value of the '<em>Closeable</em>' attribute.
	 * @see #isCloseable()
	 */
	public void setClosable(boolean value) {
		Display.getDefault().asyncExec(() -> {
			this.part.setCloseable(value);
		});
	}

}
