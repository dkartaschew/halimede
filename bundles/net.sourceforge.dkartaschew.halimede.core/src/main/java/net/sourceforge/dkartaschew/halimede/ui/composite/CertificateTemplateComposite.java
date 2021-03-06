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

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECParameterSpec;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.nebula.jface.cdatetime.CDateTimeObservableValue;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.Entropy;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;
import net.sourceforge.dkartaschew.halimede.enumeration.ExtendedKeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyUsageEnum;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.GeneralNameDialog;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.KeyTypeLabelProvider;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.X500NameBuilder;
import net.sourceforge.dkartaschew.halimede.ui.labelproviders.GeneralNameLabelProvider;
import net.sourceforge.dkartaschew.halimede.ui.model.GeneralNameModel;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCertificateModel;
import net.sourceforge.dkartaschew.halimede.ui.model.X500NameModel;
import net.sourceforge.dkartaschew.halimede.ui.util.SWTColorUtils;
import net.sourceforge.dkartaschew.halimede.ui.validators.DatePeriodValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.PassphraseValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.URIValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.X500NameValidator;
import net.sourceforge.dkartaschew.halimede.util.CertificateUtil;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CertificateTemplateComposite extends Composite {

	private DataBindingContext m_bindingContext;

	private final NewCertificateModel model;

	private Text textCertDescription;
	private Text textX500Name;
	private Button btnUseCAPassword;
	private Text textCRLLocation;
	private Text textPassword1;
	private Text textPassword2;
	private CDateTime startDate;
	private CDateTime expiryDate;
	private ComboViewer comboViewerKeyType;
	private Combo comboKeyType;
	private Button btnIsCertAuthority;
	private CheckboxTableViewer chkBoxKeyUsage;
	private CheckboxTableViewer chkBoxExtKeyUsage;
	private ListViewer listSubjectAltNames;
	private ToolItem addSubjectAltName;
	private ToolItem editSubjectAltName;
	private ToolItem remSubjectAltName;

	/**
	 * Create the composite.
	 * 
	 * @param parent The parent composite.
	 * @param style The applied style.
	 * @param model The data model to bind to.
	 * @param header The header element
	 */
	public CertificateTemplateComposite(Composite parent, int style, NewCertificateModel model,
			CertificateHeaderComposite header) {
		super(parent, SWT.BORDER);
		this.model = model;
		setLayout(new GridLayout(1, false));

		ScrolledComposite scrolledArea = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledArea.setExpandHorizontal(true);
		scrolledArea.setExpandVertical(true);

		Composite allSubComponents = new Composite(scrolledArea, SWT.NONE);
		allSubComponents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		allSubComponents.setLayout(new GridLayout(1, false));

		Group grpCertificateAndKeying = new Group(allSubComponents, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.horizontalSpacing = 10;
		grpCertificateAndKeying.setLayout(layout);
		grpCertificateAndKeying.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpCertificateAndKeying.setText("Certificate and Keying Material");

		if (model.getCa() != null) {
			Label lblDescription = new Label(grpCertificateAndKeying, SWT.NONE);
			lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblDescription.setText("Description:");

			textCertDescription = new Text(grpCertificateAndKeying, SWT.BORDER);
			textCertDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			textCertDescription
					.setToolTipText("The descriptive name for this Certificate (only used if saving as a template).");
		}

		Label lblX500Name = new Label(grpCertificateAndKeying, SWT.NONE);
		lblX500Name.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblX500Name.setText("Subject:");

		Composite composite = new Composite(grpCertificateAndKeying, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		gl_composite.horizontalSpacing = 10;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));

		textX500Name = new Text(composite, SWT.BORDER);
		textX500Name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textX500Name.setToolTipText("The Subject should be a X500Name");
		if (model.getSubject() != null) {
			textX500Name.setText(model.getSubject().toString());
		}
		// Don't allow editing if request.
		if (model.isCertificateRequest()) {
			textX500Name.setEditable(false);
			textX500Name.setToolTipText("Value is as set in Certificate Signing Request");
			// textX500Name.setEnabled(false);
		} else {
			Button btnX500 = new Button(composite, SWT.NONE);
			btnX500.setText("...");
			btnX500.setToolTipText("X500 Name Assistant");
			btnX500.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			btnX500.addListener(SWT.Selection, e -> {
				X500NameModel xmodel = null;
				try {
					xmodel = X500NameModel.create(new X500Name(textX500Name.getText()));
				} catch (Throwable e1) {
					xmodel = new X500NameModel();
				}
				X500NameBuilder dialog = new X500NameBuilder(getShell(), xmodel);
				if (dialog.open() == IDialogConstants.OK_ID) {
					textX500Name.setText(xmodel.asX500Name().toString());
					m_bindingContext.updateModels();
				}
			});
		}

		Label lblKeyType = new Label(grpCertificateAndKeying, SWT.NONE);
		lblKeyType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKeyType.setText("Key Type:");

		if (!model.isCertificateRequest()) {
			comboViewerKeyType = new ComboViewer(grpCertificateAndKeying, SWT.READ_ONLY);
			comboKeyType = comboViewerKeyType.getCombo();
			comboKeyType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
			comboViewerKeyType.setLabelProvider(new KeyTypeLabelProvider());
			comboViewerKeyType.setContentProvider(new ArrayContentProvider());
			comboViewerKeyType.setInput(KeyType.getAllowedValues());
			comboKeyType.setToolTipText("The Keying material type");
			// Set default value.
			if (model.getKeyType() == null) {
				model.setKeyType(KeyType.getDefaultKeyType());
			} else {
				// ensure key type is allowed by policy.
				KeyType key = model.getKeyType();
				boolean contains = Arrays.stream(KeyType.getAllowedValues()).anyMatch(key::equals);
				if (!contains) {
					// reset according to policy.
					model.setKeyType(KeyType.getDefaultKeyType());
				}
			}
			comboKeyType.select(KeyType.getIndex(model.getKeyType()));

			new Label(grpCertificateAndKeying, SWT.NONE);
			new Label(grpCertificateAndKeying, SWT.NONE);
		} else {
			// Extract the key type from the CSR.
			CertificateRequestProperties csr = model.getCsr();
			StringBuilder key = new StringBuilder();
			try {
				CertificateRequestPKCS10 info = (CertificateRequestPKCS10) csr.getCertificateRequest();
				if (info != null) {
					JcaPKCS10CertificationRequest jcaHolder = new JcaPKCS10CertificationRequest(info.getEncoded())
							.setProvider(BouncyCastleProvider.PROVIDER_NAME);
					PublicKey pkey = jcaHolder.getPublicKey();

					key.append("Algorithm: ");
					key.append(pkey.getAlgorithm());
					key.append(" Length/Size: ");
					key.append(Integer.toString(KeyPairFactory.getKeyLength(new KeyPair(pkey, null))));
					if (pkey instanceof ECPublicKey) {
						ECPublicKey eckey = (ECPublicKey) pkey;
						ECParameterSpec spec = eckey.getParams();
						if (spec != null && spec instanceof ECNamedCurveSpec) {
							ECNamedCurveSpec ecspec = (ECNamedCurveSpec) spec;
							key.append(" Curve Name: ");
							key.append(ecspec.getName());
						}
					}
					if (pkey instanceof GOST3410PrivateKey) {
						GOST3410PrivateKey gostKey = (GOST3410PrivateKey) pkey;
						String algID = gostKey.getParameters().getPublicKeyParamSetOID();
						for (KeyType t : KeyType.values()) {
							if (t.getParameters() != null && t.getParameters().equals(algID)) {
								key.append(" GOST 34.10: ");
								key.append(t.getDescription());
							}
						}
					}
				}
			} catch (Throwable e) {
				key = new StringBuilder("Error:" + ExceptionUtil.getMessage(e));
			}
			Text keyType = new Text(grpCertificateAndKeying, SWT.BORDER);
			keyType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			keyType.setText(key.toString());
			keyType.setEditable(false);
			keyType.setToolTipText("Key Type is as set in Certificate Signing Request");
			// keyType.setEnabled(false);
		}

		if (!model.isRepresentsTemplateOnly()) {
			Label lblStartDate = new Label(grpCertificateAndKeying, SWT.NONE);
			lblStartDate.setText("Start Date:");
			lblStartDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

			startDate = new CDateTime(grpCertificateAndKeying, CDT.BORDER | CDT.TAB_FIELDS);
			startDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			startDate.setPattern(DateTimeUtil.DEFAULT_FORMAT);
			startDate.setTimeZone(TimeZone.getTimeZone(DateTimeUtil.DEFAULT_ZONE));
			// startDate.setToolTipText("The start date of the issuing Certificate for the Certificate Authority");
			startDate.setSelection(DateTimeUtil.toDate(model.getStartDate()));

			Label lblNewLabel = new Label(grpCertificateAndKeying, SWT.NONE);
			lblNewLabel.setText("Expiry Date:");
			lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

			expiryDate = new CDateTime(grpCertificateAndKeying, CDT.BORDER | CDT.TAB_FIELDS);
			expiryDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			expiryDate.setPattern(DateTimeUtil.DEFAULT_FORMAT);
			expiryDate.setTimeZone(TimeZone.getTimeZone(DateTimeUtil.DEFAULT_ZONE));
			// expiryDate.setToolTipText("The expiry date of the issuing Certificate for the Certificate Authority");
			expiryDate.setSelection(DateTimeUtil.toDate(model.getExpiryDate()));
		}

		Group grpCertificateProperties = new Group(allSubComponents, SWT.NONE);
		layout = new GridLayout(2, true);
		layout.horizontalSpacing = 10;
		grpCertificateProperties.setLayout(layout);
		grpCertificateProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpCertificateProperties.setText("Certificate Properties");

		btnIsCertAuthority = new Button(grpCertificateProperties, SWT.CHECK);
		btnIsCertAuthority.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		if (model.getCa() != null) {
			// CA signed.
			btnIsCertAuthority.setText("Construct as Intermediate Certificate Authority");
		} else {
			// Self signed
			btnIsCertAuthority.setText("Construct as Certificate Authority");
		}
		btnIsCertAuthority.setSelection(model.isCARequest());

		Composite grpCRLLocation = new Composite(grpCertificateProperties, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		grpCRLLocation.setLayout(layout);
		grpCRLLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));

		Label lblCRLLocation = new Label(grpCRLLocation, SWT.NONE);
		lblCRLLocation.setText("CRL Location:");
		lblCRLLocation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));

		textCRLLocation = new Text(grpCRLLocation, SWT.BORDER);
		textCRLLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		if (model.getCrlLocation() != null) {
			textCRLLocation.setText(model.getCrlLocation());
		}
		textCRLLocation.setEnabled(model.isCARequest());
		btnIsCertAuthority.addListener(SWT.Selection, o -> {
			textCRLLocation.setEnabled(btnIsCertAuthority.getSelection());
			m_bindingContext.updateModels();
		});

		Group grpKeyUsage = new Group(grpCertificateProperties, SWT.NONE);
		grpKeyUsage.setText("Key Usage");
		grpKeyUsage.setLayout(new GridLayout(1, true));
		grpKeyUsage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		chkBoxKeyUsage = CheckboxTableViewer.newCheckList(grpKeyUsage, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		chkBoxKeyUsage.setContentProvider(new ArrayContentProvider());
		chkBoxKeyUsage.setInput(KeyUsageEnum.values());
		// Set input
		for (KeyUsageEnum e : KeyUsageEnum.asKeyUsageEnum(model.getKeyUsage())) {
			chkBoxKeyUsage.setChecked(e, true);
		}
		Table tblKeyUsage = chkBoxKeyUsage.getTable();
		tblKeyUsage.addListener(SWT.Selection, o -> {
			if (o.detail == SWT.CHECK) {
				Object[] elements = chkBoxKeyUsage.getCheckedElements();
				if (elements != null && elements.length > 0) {
					KeyUsageEnum[] checked = Arrays.stream(elements).map(obj -> (KeyUsageEnum) obj)
							.toArray(KeyUsageEnum[]::new);
					model.setKeyUsage(KeyUsageEnum.asKeyUsage(checked));
				}
			}
		});

		// Set the table height.
		int h = tblKeyUsage.getItemHeight();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = (h << 2) + h + (h >> 1); // h * 5.5;
		tblKeyUsage.setLayoutData(gd_table);

		Group grpSubAltNames = new Group(grpCertificateProperties, SWT.NONE);
		grpSubAltNames.setText("Subject Alternate Names");
		grpSubAltNames.setLayout(new GridLayout(1, false));
		grpSubAltNames.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 2));

		Composite sanTable = new Composite(grpSubAltNames, SWT.BORDER);
		GridLayout sanTableLayout = new GridLayout(1, false);
		sanTableLayout.horizontalSpacing = 0;
		sanTableLayout.marginHeight = 0;
		sanTableLayout.marginWidth = 0;
		sanTableLayout.verticalSpacing = 0;
		sanTable.setLayout(sanTableLayout);
		sanTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		listSubjectAltNames = new ListViewer(sanTable, SWT.NONE);
		listSubjectAltNames.setContentProvider(new ArrayContentProvider());
		listSubjectAltNames.setLabelProvider(new GeneralNameLabelProvider());

		List tblSubjectAltNames = listSubjectAltNames.getList();
		tblSubjectAltNames.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		if (model.getSubjectAlternativeName() != null) {
			listSubjectAltNames.setInput(((GeneralNames) model.getSubjectAlternativeName()).getNames());
		} else {
			listSubjectAltNames.setInput(new GeneralName[0]);
		}

		listSubjectAltNames.getList().addListener(SWT.MouseDoubleClick, l -> editSANItem(model));
		listSubjectAltNames.getList().addListener(SWT.Selection, l -> updateSANToolbar());
		
		ToolBar toolbar = new ToolBar(sanTable, SWT.FLAT | SWT.HORIZONTAL);
		toolbar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		addSubjectAltName = new ToolItem(toolbar, SWT.FLAT);
		final boolean isDark = SWTColorUtils.isDarkColour(grpSubAltNames.getBackground());
		addSubjectAltName.setImage(PluginDefaults.createImageDescriptor(isDark //
				? PluginDefaults.IMG_LIST_ADD_DARK
				: PluginDefaults.IMG_LIST_ADD).createImage());
		addSubjectAltName.addListener(SWT.Selection, (l) -> addSANItem(model));

		editSubjectAltName = new ToolItem(toolbar, SWT.FLAT);
		editSubjectAltName.setImage(PluginDefaults.createImageDescriptor(isDark //
				? PluginDefaults.IMG_LIST_EDIT_DARK
				: PluginDefaults.IMG_LIST_EDIT).createImage());
		editSubjectAltName.addListener(SWT.Selection, (l) -> editSANItem(model));
		
		remSubjectAltName = new ToolItem(toolbar, SWT.FLAT);
		remSubjectAltName.setImage(PluginDefaults.createImageDescriptor(isDark //
				? PluginDefaults.IMG_LIST_REMOVE_DARK
				: PluginDefaults.IMG_LIST_REMOVE).createImage());
		remSubjectAltName.addListener(SWT.Selection, (l) -> removeSANItem(model));
		
		editSubjectAltName.setEnabled(false);
		remSubjectAltName.setEnabled(false);

		Group grpExtKeyUsage = new Group(grpCertificateProperties, SWT.NONE);
		grpExtKeyUsage.setText("Extended Key Usage");
		grpExtKeyUsage.setLayout(new GridLayout(1, true));
		grpExtKeyUsage.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		chkBoxExtKeyUsage = CheckboxTableViewer.newCheckList(grpExtKeyUsage,
				SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		chkBoxExtKeyUsage.setContentProvider(new ArrayContentProvider());
		chkBoxExtKeyUsage.setInput(ExtendedKeyUsageEnum.values());
		// Set input
		for (ExtendedKeyUsageEnum e : ExtendedKeyUsageEnum.asExtendedKeyUsageEnum(model.getExtendedKeyUsageVector())) {
			chkBoxExtKeyUsage.setChecked(e, true);
		}
		// Add check state listener.
		Table tblExtKeyUsage = chkBoxExtKeyUsage.getTable();
		tblExtKeyUsage.addListener(SWT.Selection, o -> {
			if (o.detail == SWT.CHECK) {
				Object[] elements = chkBoxExtKeyUsage.getCheckedElements();
				if (elements != null && elements.length > 0) {
					ExtendedKeyUsageEnum[] checked = Arrays.stream(elements).map(obj -> (ExtendedKeyUsageEnum) obj)
							.toArray(ExtendedKeyUsageEnum[]::new);
					model.setExtendedKeyUsage(ExtendedKeyUsageEnum.asExtKeyUsage(checked));
				}
			}
		});
		// Set the table height.
		h = tblKeyUsage.getItemHeight();
		gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = (h << 2) + h + (h >> 1); // h * 5.5;
		tblExtKeyUsage.setLayoutData(gd_table);

		if (!model.isRepresentsTemplateOnly() && model.getCa() != null) {

			Group grpPassword = new Group(allSubComponents, SWT.NONE);
			grpPassword.setText("Passphrase Details");
			layout = new GridLayout(2, false);
			layout.horizontalSpacing = 10;
			grpPassword.setLayout(layout);
			grpPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

			btnUseCAPassword = new Button(grpPassword, SWT.CHECK);
			btnUseCAPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
			btnUseCAPassword.setText("Use CA Passphrase");
			btnUseCAPassword.setSelection(model.isUseCAPassword());

			Label lblPassword = new Label(grpPassword, SWT.NONE);
			lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblPassword.setText("Passphrase:");

			textPassword1 = new Text(grpPassword, SWT.BORDER | SWT.PASSWORD);
			textPassword1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			textPassword1.setEnabled(!model.isUseCAPassword());
			if (model.getPassword() != null) {
				textPassword1.setText(model.getPassword());
			}
			Label lblConfirm = new Label(grpPassword, SWT.NONE);
			lblConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblConfirm.setText("Confirmation:");

			textPassword2 = new Text(grpPassword, SWT.BORDER | SWT.PASSWORD);
			textPassword2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			textPassword2.setEnabled(!model.isUseCAPassword());
			if (model.getPassword() != null) {
				textPassword2.setText(model.getPassword());
			}

			// Set if checked, then disable textPassword1/2
			btnUseCAPassword.addListener(SWT.Selection, (l) -> {
				textPassword1.setEnabled(!btnUseCAPassword.getSelection());
				textPassword2.setEnabled(!btnUseCAPassword.getSelection());
				if (btnUseCAPassword.getSelection()) {
					textPassword1.setText("");
					textPassword2.setText("");
				}
			});

			Label lblStrength = new Label(grpPassword, SWT.NONE);
			lblStrength.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
			lblStrength.setText("Strength:");

			ProgressBar passwordStrength = new ProgressBar(grpPassword, SWT.SMOOTH);
			passwordStrength.setToolTipText(Entropy.MESSAGE_RANDOM);
			passwordStrength.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			passwordStrength.setMaximum(Entropy.MAX);
			textPassword1.addModifyListener(o -> {
				int entropy = (int) Entropy.random(textPassword1.getText());
				passwordStrength.setSelection(Math.min(entropy, passwordStrength.getMaximum()));
			});

		}

		scrolledArea.setContent(allSubComponents);
		scrolledArea.setMinSize(allSubComponents.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		m_bindingContext = initDataBindings();
	}

	/**
	 * Update the SAN toolbar buttons.
	 */
	private void updateSANToolbar() {
		IStructuredSelection selection = listSubjectAltNames.getStructuredSelection();
		boolean hasSelection = (selection != null & !selection.isEmpty());
		editSubjectAltName.setEnabled(hasSelection);
		remSubjectAltName.setEnabled(hasSelection);
	}

	/**
	 * Add a new Subject Alt Name item
	 * 
	 * @param model The model.
	 */
	private void addSANItem(NewCertificateModel model) {
		GeneralNameModel gmodel = new GeneralNameModel();
		GeneralNameDialog d = new GeneralNameDialog(getShell(), gmodel);
		if (d.open() == IDialogConstants.OK_ID) {
			// update collection...
			GeneralName[] names = (GeneralName[]) listSubjectAltNames.getInput();
			names = appendArray(names, gmodel.createGeneralNameFromModel());
			listSubjectAltNames.setInput(names);
			listSubjectAltNames.refresh();
			// don't use databinding due to complexity...
			model.setSubjectAlternativeName(new GeneralNames(names));
		}
		updateSANToolbar();
	}

	/**
	 * Remove the selected Subject Alt Name item
	 * 
	 * @param model The model.
	 */
	private void removeSANItem(NewCertificateModel model) {
		IStructuredSelection selection = listSubjectAltNames.getStructuredSelection();
		if (selection != null & !selection.isEmpty()) {
			Object name = selection.getFirstElement();
			if (name instanceof GeneralName) {
				GeneralName gname = (GeneralName) name;
				GeneralName[] names = (GeneralName[]) listSubjectAltNames.getInput();
				listSubjectAltNames.setInput(removeArray(names, gname));
				listSubjectAltNames.refresh();
				// don't use databinding due to complexity...
				model.setSubjectAlternativeName(new GeneralNames(names));
			}
		}
		updateSANToolbar();
	}

	/**
	 * Edit the selected Subject Alt Name item
	 * 
	 * @param model The model.
	 */
	private void editSANItem(NewCertificateModel model) {
		IStructuredSelection selected = listSubjectAltNames.getStructuredSelection();
		if (selected != null && !selected.isEmpty()) {
			Object element = selected.getFirstElement();
			if (element instanceof GeneralName) {
				GeneralName name = (GeneralName) element;
				GeneralNameModel gmodel = new GeneralNameModel(name);
				GeneralNameDialog d = new GeneralNameDialog(getShell(), gmodel);
				if (d.open() == IDialogConstants.OK_ID) {
					// update collection...
					GeneralName[] names = (GeneralName[]) listSubjectAltNames.getInput();
					for (int i = 0; i < names.length; i++) {
						if (names[i].equals(name)) {
							names[i] = gmodel.createGeneralNameFromModel();
							break;
						}
					}
					listSubjectAltNames.setInput(names);
					listSubjectAltNames.refresh();
					// don't use databinding due to complexity...
					model.setSubjectAlternativeName(new GeneralNames(names));
				}
			}
		}
		updateSANToolbar();
	}

	/**
	 * Append the item to the general name array
	 * 
	 * @param names The existing array
	 * @param addName The new element to add
	 * @return The new array, with the new element added to the end.
	 */
	private GeneralName[] appendArray(GeneralName[] names, GeneralName addName) {
		GeneralName[] newNames = new GeneralName[names.length + 1];
		System.arraycopy(names, 0, newNames, 0, names.length);
		newNames[names.length] = addName;
		return newNames;
	}

	/**
	 * Remove the item from the general name array
	 * 
	 * @param names The existing names array
	 * @param removeName The name to remove
	 * @return The resulting array with the element removed.
	 */
	private GeneralName[] removeArray(GeneralName[] names, GeneralName removeName) {
		return Arrays.stream(names).filter(o -> o != removeName).toArray(GeneralName[]::new);
	}

	@Override
	protected void checkSubclass() {
		/* Do nothing - Subclassing is allowed */
	}

	/**
	 * Get the binding context that has been setup.
	 * 
	 * @return The binding context.
	 */
	public DataBindingContext getBindingContext() {
		return m_bindingContext;
	}

	/**
	 * Create the data bindings for controls to the model
	 * 
	 * @return The databinding holder.
	 */
	@SuppressWarnings("unchecked")
	protected DataBindingContext initDataBindings() {

		DataBindingContext bindingContext = new DataBindingContext();

		/*
		 * Description
		 */
		if (textCertDescription != null) {
			IObservableValue<String> descriptionWidget = WidgetProperties.text(SWT.Modify).observe(textCertDescription);
			IObservableValue<String> descriptionModel = PojoProperties.value("description", String.class).observe(model);
			Binding b = bindingContext.bindValue(descriptionWidget, descriptionModel, null, null);
			ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);
		}
		/*
		 * X500 Name
		 */
		if (!model.isCertificateRequest()) {
			IObservableValue<String> x500NameWidget = WidgetProperties.text(SWT.Modify).observe(textX500Name);
			IObservableValue<X500Name> x500NameModel = PojoProperties.value("subject", X500Name.class).observe(model);
			IConverter<String, X500Name> convertStringToX500 = IConverter.create(String.class, X500Name.class,
					(o1) -> new X500Name(o1));
			UpdateValueStrategy<String, X500Name> s = new UpdateValueStrategy<String, X500Name>()//
					.setAfterGetValidator(new X500NameValidator(CertificateUtil.getIssuers(model.getCa())))//
					.setConverter(convertStringToX500);
			Binding b = bindingContext.bindValue(x500NameWidget, x500NameModel, s, null);
			ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);
		}
		/*
		 * Key Type
		 */
		if (comboKeyType != null) {
			// This will be NULL if this is a CSR.
			IObservableValue<String> keyTypeWidget = WidgetProperties.comboSelection().observe(comboKeyType);
			IObservableValue<KeyType> keyTypeModel = PojoProperties.value("keyType", KeyType.class).observe(model);
			IConverter<String, KeyType> convertStringToKeyType = IConverter.create(String.class, KeyType.class,
					(o1) -> KeyType.forDescription((String) o1));
			UpdateValueStrategy<String, KeyType> s = UpdateValueStrategy.create(convertStringToKeyType)//
					.setAfterConvertValidator(new KeyTypeWarningValidator());

			Binding b = bindingContext.bindValue(keyTypeWidget, keyTypeModel, s, null);
			ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);
		}

		/*
		 * is CA field
		 */
		IObservableValue<Boolean> caWidget = WidgetProperties.buttonSelection().observe(btnIsCertAuthority);
		IObservableValue<Boolean> caModel = PojoProperties.value("cARequest", Boolean.class).observe(model);
		Binding b = bindingContext.bindValue(caWidget, caModel, null, null);

		/*
		 * CRL Location
		 */
		IObservableValue<String> crlLocationWidget = WidgetProperties.text(SWT.Modify).observe(textCRLLocation);
		IObservableValue<String> cRLLocationModel = PojoProperties.value("crlLocation", String.class).observe(model);
		UpdateValueStrategy<String, String> s = new UpdateValueStrategy<String, String>().setAfterGetValidator(new URIValidator(caModel));
		b = bindingContext.bindValue(crlLocationWidget, cRLLocationModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * The following only apply if this is a full certificate request.
		 */
		if (!model.isRepresentsTemplateOnly()) {

			/*
			 * Start Date and End Date
			 */
			IObservableValue<Date> startDateWidget = new SWTObservableValueDecorator<Date>(new CDateTimeObservableValue(startDate), startDate);
			IObservableValue<ZonedDateTime> startDateModel = PojoProperties.value("startDate", ZonedDateTime.class).observe(model);

			IObservableValue<Date> expiryDateWidget = new SWTObservableValueDecorator<Date>(new CDateTimeObservableValue(expiryDate), expiryDate);
			IObservableValue<ZonedDateTime> expiryDateModel = PojoProperties.value("expiryDate", ZonedDateTime.class).observe(model);

			// Multi or cross validation requires the use of an intermediate value to work.
			final IObservableValue<ZonedDateTime> middleField1 = new WritableValue<ZonedDateTime>(model.getStartDate(), ZonedDateTime.class);
			final IObservableValue<ZonedDateTime> middleField2 = new WritableValue<ZonedDateTime>(model.getExpiryDate(), ZonedDateTime.class);
			UpdateValueStrategy<Date, ZonedDateTime> s1 = new UpdateValueStrategy<Date, ZonedDateTime>()
					.setConverter(IConverter.create(Date.class, ZonedDateTime.class,
					(date) -> DateTimeUtil.toZonedDateTime((Date) date)));
			UpdateValueStrategy<ZonedDateTime, Date>  s2 = new UpdateValueStrategy<ZonedDateTime, Date>()
					.setConverter(IConverter.create(ZonedDateTime.class, Date.class, (date) -> {
						return date == null ? null : Date.from(((ZonedDateTime) date).toInstant());
					}));
			bindingContext.bindValue(startDateWidget, middleField1, s1, s2);
			bindingContext.bindValue(expiryDateWidget, middleField2, s1, s2);

			// We simple set a validator on the intermediate values.
			ZonedDateTime notBefore = null;
			ZonedDateTime notAfter = null;
			try {
				X509Certificate cert = (X509Certificate) model.getCa().getCertificate();
				notBefore = DateTimeUtil.toZonedDateTime(cert.getNotBefore());
				notAfter = DateTimeUtil.toZonedDateTime(cert.getNotAfter());
			} catch (DatastoreLockedException | NullPointerException e) {
				// Ignore...
			}
			final MultiValidator validator = new DatePeriodValidator(middleField1, middleField2, notBefore, notAfter);
			bindingContext.addValidationStatusProvider(validator);

			bindingContext.bindValue(validator.observeValidatedValue(middleField1), startDateModel);
			bindingContext.bindValue(validator.observeValidatedValue(middleField2), expiryDateModel);

			// And tie the decorator on the validator and widgets being watched.
			ControlDecorationSupport.create(validator.getValidationStatus(), SWT.TOP | SWT.LEFT, startDateWidget);
			ControlDecorationSupport.create(validator.getValidationStatus(), SWT.TOP | SWT.LEFT, expiryDateWidget);
		}
		if (!model.isRepresentsTemplateOnly() && model.getCa() != null) {
			/*
			 * Use CA Password field
			 */
			IObservableValue<Boolean> capasswordWidget = WidgetProperties.buttonSelection().observe(btnUseCAPassword);
			IObservableValue<Boolean> capasswordModel = PojoProperties.value("useCAPassword", Boolean.class).observe(model);
			bindingContext.bindValue(capasswordWidget, capasswordModel, null, null);

			/*
			 * Password field
			 */
			IObservableValue<String> password1Widget = WidgetProperties.text(SWT.Modify).observe(textPassword1);
			IObservableValue<String> password2Widget = WidgetProperties.text(SWT.Modify).observe(textPassword2);

			final IObservableValue<String> pmiddleField1 = new WritableValue<String>(model.getPassword(), String.class);
			final IObservableValue<String> pmiddleField2 = new WritableValue<String>(model.getPassword(), String.class);

			bindingContext.bindValue(password1Widget, pmiddleField1);
			bindingContext.bindValue(password2Widget, pmiddleField2);

			MultiValidator v = new PassphraseValidator(pmiddleField1, pmiddleField2, PluginDefaults.MIN_PASSWORD_LENGTH, capasswordModel);
			bindingContext.addValidationStatusProvider(v);

			IObservableValue<String> passwordModel = PojoProperties.value("password", String.class).observe(model);
			bindingContext.bindValue(v.observeValidatedValue(pmiddleField1), passwordModel);

			ControlDecorationSupport.create(v.getValidationStatus(), SWT.TOP | SWT.LEFT, password1Widget);
			ControlDecorationSupport.create(v.getValidationStatus(), SWT.TOP | SWT.LEFT, password2Widget);
			//

		}

		return bindingContext;
	}

}
