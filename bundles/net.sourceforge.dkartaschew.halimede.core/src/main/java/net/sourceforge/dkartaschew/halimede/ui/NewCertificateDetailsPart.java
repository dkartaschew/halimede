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

import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateCertificateListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.CreateTemplateListener;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateHeaderComposite;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateTemplateComposite;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.ui.util.MenuUtils;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@SuppressWarnings("restriction")
public class NewCertificateDetailsPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.newcertificate";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "Certificate";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";

	/**
	 * The CA key.
	 */
	public static final String CA = "net.sourceforge.dkartaschew.halimede.data.certificateauthority";
	/**
	 * The CA key.
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
	/**
	 * Header model
	 */
	private CertificateHeaderComposite header;

	@Inject
	private Logger logger;

	@Inject
	private EPartService partService;

	@Inject
	private IEclipseContext context;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * Create contents of the view part.
	 * 
	 * @param part   The part which this is part of.
	 * @param parent The parent composite
	 */
	@PostConstruct
	public void createControls(MPart part, Composite parent) {
		this.part = part;
		if (this.part != null) {
			this.ca = (CertificateAuthority) this.part.getTransientData().get(CA);
			if (ca == null) {
				logger.info("View Certificate Details missing Certificate information. Closing");
				close();
				return;
			}
			this.model = (NewCertificateModel) this.part.getTransientData().get(MODEL);
		}
		if (model == null) {
			model = new NewCertificateModel(ca);
			ZonedDateTime startDate = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
			ZonedDateTime expiryDate = startDate.plusDays(ca.getExpiryDays());
			// Check to ensure the expiry date is before the end date of the ca.
			try {
				X509Certificate cert = (X509Certificate) ca.getCertificate();
				ZonedDateTime CAExpiry = DateTimeUtil.toZonedDateTime(cert.getNotAfter());
				if (CAExpiry.isBefore(expiryDate)) {
					expiryDate = CAExpiry;
				}
				model.setNotBefore(DateTimeUtil.toZonedDateTime(cert.getNotBefore()));
				model.setNotAfter(DateTimeUtil.toZonedDateTime(cert.getNotAfter()));
			} catch (DatastoreLockedException e) {
				if (logger != null) {
					logger.error(e, "Unable to get CA certificate?");
				}
			} catch (Throwable e) {
				if (logger != null) {
					logger.error(e, "Unhandled error?");
				}
				MultiStatus s = new MultiStatus(PluginDefaults.ID, IStatus.ERROR, ExceptionUtil.getMessage(e), e);
				ErrorDialog.openError(parent.getShell(), "CA Error",
						"An unhandled error occurred attempting to access the CA.", s);
				return;
			}

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
		if (!model.isCertificateRequest()) {
			headerModel.setHeader("New Certificate");
		} else {
			String desc = Strings.trim(model.getCsr().getProperty(CertificateRequestProperties.Key.subject),
					PluginDefaults.PART_HEADER_LENGTH);
			headerModel.setHeader("Issue Certificate: " + desc);
		}
		headerModel.setToolItem("Create and Issue the certificate");
		headerModel.setToolItemImage(PluginDefaults.IMG_CERTIFICATE);
		headerModel.setToolItemSelectionListener(new CreateCertificateListener(model, this));

		headerModel.setDropDownMenu(new Menu(parent.getShell(), SWT.POP_UP));
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Create and Issue the certificate");
		new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Save as Certificate Template");
		headerModel.getMenuItems().addAll(Arrays.asList(headerModel.getDropDownMenu().getItems()));

		header = new CertificateHeaderComposite(parent, SWT.NONE, headerModel);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.composite = new CertificateTemplateComposite(parent, SWT.NONE, model, header);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		((CreateCertificateListener) headerModel.getToolItemSelectionListener())
				.setBindingContext(composite.getBindingContext());

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
	 * Sets the value of the
	 * '{@link org.eclipse.e4.ui.model.application.ui.basic.MPart#isCloseable
	 * <em>Closeable</em>}' attribute. 
	 * <p>
	 * This call also has the side effect of disabling the header buttons as well to
	 * stop multiple calls to various actions.
	 * 
	 * @param value the new value of the '<em>Closeable</em>' attribute.
	 * @see #isCloseable()
	 */
	public void setClosable(boolean value) {
		sync.asyncExec(() -> {
			this.part.setCloseable(value);
			this.header.setEnabled(value);
		});
	}

}
