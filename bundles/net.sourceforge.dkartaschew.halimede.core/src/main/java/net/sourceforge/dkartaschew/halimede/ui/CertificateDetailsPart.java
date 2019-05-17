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

import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.IIssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties;
import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.data.render.CertificateRenderer;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCertificateAsHTMLListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCertificateAsTextListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportCertificateListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportPKCS12Listener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportPrivateKeyListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.ExportPublicKeyListener;
import net.sourceforge.dkartaschew.halimede.ui.actions.RevokeCertificateAction;
import net.sourceforge.dkartaschew.halimede.ui.actions.ViewCertificateInformationAction;
import net.sourceforge.dkartaschew.halimede.ui.composite.CertificateHeaderComposite;
import net.sourceforge.dkartaschew.halimede.ui.composite.CompositeOutputRenderer;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.ExportPKCS12Dialog;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.PassphraseDialog;
import net.sourceforge.dkartaschew.halimede.ui.model.ExportInformationModel;
import net.sourceforge.dkartaschew.halimede.ui.model.HeaderCompositeMenuModel;
import net.sourceforge.dkartaschew.halimede.ui.util.MenuUtils;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;
import net.sourceforge.dkartaschew.halimede.util.Strings;

@SuppressWarnings("restriction")
public class CertificateDetailsPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.certificate";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "Certificate Details";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";

	/**
	 * The CA key.
	 */
	public static final String CERTIFICATE = "net.sourceforge.dkartaschew.halimede.data.certificate";
	/**
	 * The editor part stack ID.
	 */
	public static final String PARTSTACK_EDITOR = "net.sourceforge.dkartaschew.halimede.data.editor";

	/**
	 * Reference to the containing part.
	 */
	private MPart part;
	/**
	 * The CA for issuing this certificate.
	 */
	private IssuedCertificateProperties certificate;
	/**
	 * Primary composite
	 */
	private CompositeOutputRenderer composite;
	/**
	 * MPartStack to add to.
	 */
	private String editor;

	@Inject
	private Logger logger;

	@Inject
	private EPartService partService;

	@Inject
	private IEclipseContext context;
		
	@Inject 
	private UISynchronize sync;
	
	/**
	 * UI Shell
	 */
	@Inject
	@Optional
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	/**
	 * Create contents of the view part.
	 * 
	 * @param part The part which this is part of.
	 * @param parent The parent composite
	 */
	@PostConstruct
	public void createControls(MPart part, Composite parent) {
		this.part = part;
		if (shell == null) {
			logger.info("View Certificate Details missing required information. Closing");
			close();
			return;
		}
		if (this.part != null) {
			this.editor = (String) this.part.getTransientData().get(PARTSTACK_EDITOR);
			this.certificate = (IssuedCertificateProperties) this.part.getTransientData().get(CERTIFICATE);
			if (certificate == null) {
				logger.info("View Certificate Details missing Certificate information. Closing");
				close();
				return;
			}
			if (!certificate.hasIssuedCertificate()) {
				// prompt for password.
				try {
					certificate.loadIssuedCertificate(null);
				} catch (IOException | KeyStoreException | InvalidPasswordException e) {
					// failed...

				}
				if (!certificate.hasIssuedCertificate()) {
					PassphraseDialog dialog = new PassphraseDialog(shell, "Certificate Passphrase",
							"Enter the passphrase to unlock the Certificate", "");
					while (dialog.open() == IDialogConstants.OK_ID) {
						try {
							certificate.loadIssuedCertificate(dialog.getValue());
							break;
						} catch (KeyStoreException | InvalidPasswordException e) {
							/*
							 * Unlock password failed. IOException/CertificateEncodingException are permanent failures.
							 */
						} catch (Throwable e) {
							if (logger != null)
								logger.error(e, ExceptionUtil.getMessage(e));
							/*
							 * Bad data?
							 */
							MessageDialog.openError(shell, "Certificate Integrity",
									"Failed to unlock the Certificate due to the following error:"
											+ System.lineSeparator() + ExceptionUtil.getMessage(e));
							close();
							return;

						}
						dialog.setErrorMessage("Bad Passphrase Supplied");
					}
					if (!certificate.hasIssuedCertificate()) {
						close();
						return;
					}
				}
			}
		}

		// Now construct the controls.
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		IIssuedCertificate c = null;
		try {
			c = certificate.loadIssuedCertificate(null);
		} catch (KeyStoreException | InvalidPasswordException | IOException e) {
			// ignore
		}

		HeaderCompositeMenuModel headerModel = new HeaderCompositeMenuModel();
		String desc = certificate.getProperty(Key.description);
		if (desc == null) {
			desc = certificate.getProperty(Key.subject);
		}
		desc = Strings.trim(desc, PluginDefaults.PART_HEADER_LENGTH);
		headerModel.setHeader("Certificate '" + desc + "' Information");
		headerModel.setToolItem("Certificate Information");
		headerModel.setToolItemImage(PluginDefaults.IMG_CERTIFICATE);
		headerModel.setToolItemSelectionListener(new ExportCertificateListener(certificate, false));

		boolean hasIssuerCertifcateMenu = false;
		boolean hasRevokeCertifcateMenu = false;
		boolean hasPrivateKey = false;

		headerModel.setDropDownMenu(new Menu(parent.getShell(), SWT.POP_UP));
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the Certificate");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the Certificate Chain");
		new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the Public Key");

		if (c != null && c.getPrivateKey() != null) {
			hasPrivateKey = true;
			new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
			new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export the Private Key");
			new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Export as PKCS#12 Keystore");

		}
		if (certificate.getProperty(Key.revokeDate) == null && certificate.getProperty(Key.filename) != null
				&& certificate.getCertificateAuthority() != null) {
			hasRevokeCertifcateMenu = true;
			new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
			new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Revoke the Certificate");
		}
		Certificate[] certChain = null;
		if (c != null) {
			certChain = c.getCertificateChain();
			if (certChain != null && certChain.length > 1) {
				new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
				new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("View Issuer Certificate");
				hasIssuerCertifcateMenu = true;
			}
			new MenuItem(headerModel.getDropDownMenu(), SWT.SEPARATOR);
			new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Details as TXT");
			new MenuItem(headerModel.getDropDownMenu(), SWT.PUSH).setText("Certificate Details as HTML");
		}
		headerModel.getMenuItems().addAll(Arrays.asList(headerModel.getDropDownMenu().getItems()));

		CertificateHeaderComposite header = new CertificateHeaderComposite(parent, SWT.NONE, headerModel);
		header.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		this.composite = new CompositeOutputRenderer(parent, SWT.NONE, desc);
		CertificateRenderer renderer = new CertificateRenderer(certificate);
		renderer.render(composite);
		composite.finaliseRender();
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		/*
		 * Set the action parameters
		 */
		int menuID = 0;
		headerModel.getMenuItems().get(menuID++).addSelectionListener(headerModel.getToolItemSelectionListener());
		headerModel.getMenuItems().get(menuID++).addSelectionListener(new ExportCertificateListener(certificate, true));
		headerModel.getMenuItems().get(menuID++).addSelectionListener(new ExportPublicKeyListener(certificate));
		menuID++; // separator

		if (hasPrivateKey) {
			headerModel.getMenuItems().get(menuID++).addSelectionListener(new ExportPrivateKeyListener(certificate));
			headerModel.getMenuItems().get(menuID++).addSelectionListener(new ExportPKCS12Listener(certificate, this));

			menuID++; // separator
		}

		if (hasRevokeCertifcateMenu) {
			headerModel.getMenuItems().get(menuID++).addSelectionListener(
					new RevokeCertificateAction(certificate, certificate.getCertificateAuthority(), this));
			menuID++; // separator
		}

		if (hasIssuerCertifcateMenu) {
			if (certChain != null && certChain.length > 1) {
				Certificate[] newCertChain = new Certificate[certChain.length - 1];
				System.arraycopy(certChain, 1, newCertChain, 0, newCertChain.length);
				IssuedCertificate newCert = new IssuedCertificate(null, newCertChain, null, null, null);
				IssuedCertificateProperties newCertProp = new IssuedCertificateProperties(
						certificate.getCertificateAuthority(), newCert);
				newCertProp.setProperty(Key.creationDate,
						DateTimeUtil.toString(((X509Certificate) newCertChain[0]).getNotBefore()));
				newCertProp.setProperty(Key.subject, ((X509Certificate) newCertChain[0]).getSubjectDN().toString());

				headerModel.getMenuItems().get(menuID++)
						.addSelectionListener(new ViewCertificateInformationAction(newCertProp, "", editor));
			}
			menuID++; // separator
		}
		headerModel.getMenuItems().get(menuID++).addSelectionListener(new ExportCertificateAsTextListener(certificate));
		headerModel.getMenuItems().get(menuID++).addSelectionListener(new ExportCertificateAsHTMLListener(certificate));
		MenuUtils.injectMenuItems(headerModel.getMenuItems(), context);
	}

	/**
	 * Persist the certificate as a P12
	 * 
	 * @throws Throwable If the action cancelled.
	 */
	@Persist
	public void save() throws Throwable {
		if (this.part.isDirty()) {
			ExportInformationModel model = new ExportInformationModel();
			ExportPKCS12Dialog dialog = new ExportPKCS12Dialog(shell, model);
			if (dialog.open() == IDialogConstants.OK_ID) {
				String certdesc = certificate.getProperty(Key.description);
				if (certdesc == null) {
					certdesc = certificate.getProperty(Key.subject);
				}
				String certDescription = certdesc;
				try {
					String alias = certDescription + "#" + certificate.getProperty(Key.certificateSerialNumber);
					certificate.loadIssuedCertificate(null)//
							.createPKCS12(Paths.get(model.getFilename()), model.getPassword(), alias,
									model.getPkcs12Cipher());
					sync.asyncExec(() -> {
						MessageDialog.openInformation(shell, "Key Information Exported",
								"The Issued Certificate '" + certDescription
										+ "' Keys in PKCS#12 Container have been exported to '" + model.getFilename()
										+ "'.");
					});
					part.setDirty(false);
				} catch (Throwable ex) {
					if (logger != null)
						logger.error(ex, "Exporting the Keys Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Exporting Keys Failed",
								"Exporting Keys in PKCS#12 Container from Issued Certificate '" + certDescription
										+ "' failed with the following error: " + ExceptionUtil.getMessage(ex));
					});
					// The only way to stop the save is to throw...
					throw ex;
				}
			} else {
				throw new Exception("Action cancelled");
			}
		}

	}

	/**
	 * Set the dirty state of the part.
	 * 
	 * @param dirty The dirty state of the part.
	 */
	public void setDirty(boolean dirty) {
		sync.asyncExec(() -> {
			part.setDirty(dirty);
		});
	}

	@PreDestroy
	public void dispose() {
		// Ensure we set to not be dirty...
		part.setDirty(false);
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
