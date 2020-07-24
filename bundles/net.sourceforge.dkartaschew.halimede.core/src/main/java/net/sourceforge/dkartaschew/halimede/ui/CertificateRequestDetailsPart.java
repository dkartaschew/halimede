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
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.render.CSRRenderer;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateCertificateFromCSRAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.DeleteCertificateRequestAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCSRAsHTMLListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCSRAsTextListener;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateHeaderComposite;
import net.sourceforge.dkartaschew.halimede.ui.composite.CompositeOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.ui.util.MenuUtils;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@SuppressWarnings("restriction")
public class CertificateRequestDetailsPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.csr";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "Certificate Request";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";
	/**
	 * The CA key.
	 */
	public static final String CA = "net.sourceforge.dkartaschew.halimede.data.certificateauthority";
	/**
	 * The CSR Properties model.
	 */
	public static final String MODEL = "net.sourceforge.dkartaschew.halimede.data.model";
	/**
	 * The Editor Properties model.
	 */
	public static final String EDITOR = "net.sourceforge.dkartaschew.halimede.data.editor";

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
	private CertificateRequestProperties model;
	/**
	 * The editor to add the new part to (if applicable for the action).
	 */
	private String editor;
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
			this.ca = (CertificateAuthority) this.part.getTransientData().get(CA);
			// if (ca == null) {
			// logger.info("View CSR Details missing CA information. Closing");
			// close();
			// return;
			// }
			this.model = (CertificateRequestProperties) this.part.getTransientData().get(MODEL);
			if (model == null) {
				logger.info("View CSR Details missing CSR information. Closing");
				close();
				return;
			}
			this.editor = (String) this.part.getTransientData().get(EDITOR);
			if (editor == null) {
				logger.info("View CSR Details missing View information. Closing");
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

		String desc = Strings.trim(model.getProperty(Key.subject), PluginDefaults.PART_HEADER_LENGTH);
		HeaderCompositeMenuModel headerModel = new HeaderCompositeMenuModel();
		headerModel.setHeader("CSR: " + desc);
		headerModel.setToolItem("Issue CSR");
		headerModel.setToolItemImage(PluginDefaults.IMG_CERTIFICATE);
		if(ca != null) {
			headerModel.setToolItemSelectionListener(new CreateCertificateFromCSRAction(ca, model, editor, this));
		} else {
			headerModel.setToolItemSelectionListener(new ExportCSRAsTextListener(model));
		}
		headerModel.setDropDownMenu(new Menu(parent.getShell(), SWT.POP_UP));
		if (ca != null) {
			new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Issue Certificate");
			new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Delete Certificate Request");
			new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
		}
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Request as TXT");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Request as HTML");
		headerModel.getMenuItems().addAll(Arrays.asList(headerModel.getDropDownMenu().getItems()));

		CertificateHeaderComposite header = new CertificateHeaderComposite(parent, SWT.NONE, headerModel);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.composite = new CompositeOutputRenderer(parent, SWT.NONE, desc);
		CSRRenderer renderer = new CSRRenderer(model);
		renderer.render(composite);
		composite.finaliseRender();
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.redraw();

		/*
		 * Set the action parameters
		 */
		if (ca != null) {
			headerModel.getMenuItems().get(0).addSelectionListener(headerModel.getToolItemSelectionListener());
			headerModel.getMenuItems().get(1).addSelectionListener(new DeleteCertificateRequestAction(model, ca, this));
			headerModel.getMenuItems().get(3).addSelectionListener(new ExportCSRAsTextListener(model));
			headerModel.getMenuItems().get(4).addSelectionListener(new ExportCSRAsHTMLListener(model));
		} else {
			headerModel.getMenuItems().get(0).addSelectionListener(headerModel.getToolItemSelectionListener());
			headerModel.getMenuItems().get(1).addSelectionListener(new ExportCSRAsHTMLListener(model));
		}
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
