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

package net.sourceforge.dkartaschew.halimede.ui.dialogs;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.Entropy;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCAModel;
import net.sourceforge.dkartaschew.halimede.ui.util.Dialogs;
import net.sourceforge.dkartaschew.halimede.ui.validators.ExistingCAValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.FileValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.PassphraseValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.PathValidator;

public class NewCAExistingMaterialDialog extends Dialog {

	private DataBindingContext m_bindingContext;

	private Text textCADescription;
	private Text textBaseLocation;
	private Text textPKCS12Filename;
	private Button btnCertificateFile;
	private Button btnPKCS12File;
	private Button btnKeyFile;
	private Text textCertificateFilename;
	private Text textPrivateKeyFilename;
	private Text textPassword1;
	private Text textPassword2;

	private Button btnPKCS12;
	private Button btnCertKeyPair;

	/**
	 * The data model which will be used to provide details to the system.
	 */
	private final NewCAModel model;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell The parent shell
	 * @param model The data for the new CA.
	 */
	public NewCAExistingMaterialDialog(Shell parentShell, NewCAModel model) {
		super(parentShell);
		this.model = model;
		setShellStyle(SWT.DIALOG_TRIM | SWT.MIN | SWT.MAX | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Create Certificate Authority");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		Group grpDescription = new Group(container, SWT.NONE);
		grpDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		grpDescription.setText("CA Details");
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		grpDescription.setLayout(layout);

		Label lblDescription = new Label(grpDescription, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDescription.setText("Description:");

		textCADescription = new Text(grpDescription, SWT.BORDER);
		GridData gd_textCADescription = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textCADescription.widthHint = 480;
		textCADescription.setLayoutData(gd_textCADescription);
		textCADescription.setToolTipText("The descriptive name for this Certificate Authority.");
		new Label(grpDescription, SWT.NONE);

		Label lblLocation = new Label(grpDescription, SWT.NONE);
		lblLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocation.setText("Location:");

		textBaseLocation = new Text(grpDescription, SWT.BORDER);
		textBaseLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textBaseLocation.setToolTipText("The base location to store the Certificate Authority. The CA will be stored in"
				+ " a folder of the same name as the description in this location.");

		Button buttonLocation = new Button(grpDescription, SWT.NONE);
		buttonLocation.setText("...");
		buttonLocation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(getShell());
				dlg.setFilterPath(textBaseLocation.getText());
				dlg.setText("Set Base Location for CA Datastore");
				dlg.setMessage("Select a directory");
				String dir = dlg.open();
				if (dir != null) {
					textBaseLocation.setText(dir);
				}
			}
		});

		Group grpCertificateAndKeying = new Group(container, SWT.NONE);
		layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		grpCertificateAndKeying.setLayout(layout);
		grpCertificateAndKeying.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpCertificateAndKeying.setText("Certificate and Keying Material");

		btnPKCS12 = new Button(grpCertificateAndKeying, SWT.RADIO);
		btnPKCS12.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		btnPKCS12.setText("PKCS#12");
		btnPKCS12.addListener(SWT.Selection, o -> {
			setPKCS12FieldsEnabled(btnPKCS12.getSelection());
			m_bindingContext.updateModels();
		});

		Label lblPKCS12Filename = new Label(grpCertificateAndKeying, SWT.NONE);
		lblPKCS12Filename.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPKCS12Filename.setText("Filename:");

		textPKCS12Filename = new Text(grpCertificateAndKeying, SWT.BORDER);
		textPKCS12Filename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnPKCS12File = new Button(grpCertificateAndKeying, SWT.NONE);
		btnPKCS12File.setText("...");
		btnPKCS12File.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setText("Open PKCS#12 File");
				dlg.setOverwrite(true);
				dlg.setFilterExtensions(new String[] { "*.p12", "*.*" });
				dlg.setFilterNames(new String[] { "PKCS#12 (*.p12)", "All Files (*.*)" });
				String dir = dlg.open();
				if (dir != null) {
					textPKCS12Filename.setText(dir);
				}
			}
		});

		btnCertKeyPair = new Button(grpCertificateAndKeying, SWT.RADIO);
		btnCertKeyPair.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		btnCertKeyPair.setText("Certificate/Key Pair");
		btnCertKeyPair.setSelection(false);
		btnCertKeyPair.addListener(SWT.Selection, o -> {
			setPKCS12FieldsEnabled(!btnCertKeyPair.getSelection());
			m_bindingContext.updateModels();
		});

		Label lblNewLabel = new Label(grpCertificateAndKeying, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Certificate:");

		textCertificateFilename = new Text(grpCertificateAndKeying, SWT.BORDER);
		textCertificateFilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnCertificateFile = new Button(grpCertificateAndKeying, SWT.NONE);
		btnCertificateFile.setText("...");
		btnCertificateFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setText("Open Certificate File");
				dlg.setOverwrite(true);
				dlg.setFilterExtensions(new String[] { "*.cer", "*.*" });
				dlg.setFilterNames(new String[] { "Certificate (*.cer)", "All Files (*.*)" });
				String dir = dlg.open();
				if (dir != null) {
					textCertificateFilename.setText(dir);
				}
			}
		});

		Label lblKeyFIle = new Label(grpCertificateAndKeying, SWT.NONE);
		lblKeyFIle.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKeyFIle.setText("Private Key:");

		textPrivateKeyFilename = new Text(grpCertificateAndKeying, SWT.BORDER);
		textPrivateKeyFilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		btnKeyFile = new Button(grpCertificateAndKeying, SWT.NONE);
		btnKeyFile.setText("...");
		btnKeyFile.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setText("Open Private Key File");
				dlg.setOverwrite(true);
				dlg.setFilterExtensions(new String[] { "*.key", "*.*" });
				dlg.setFilterNames(new String[] { "Private Key (*.key)", "All Files (*.*)" });
				String dir = dlg.open();
				if (dir != null) {
					textPrivateKeyFilename.setText(dir);
				}
			}
		});

		Group grpPassword = new Group(container, SWT.NONE);
		grpPassword.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpPassword.setText("Passphrase");
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		grpPassword.setLayout(layout);

		Label lblPassword = new Label(grpPassword, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Passphrase:");

		textPassword1 = new Text(grpPassword, SWT.BORDER | SWT.PASSWORD);
		textPassword1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblConfirm = new Label(grpPassword, SWT.NONE);
		lblConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConfirm.setText("Confirmation:");

		textPassword2 = new Text(grpPassword, SWT.BORDER | SWT.PASSWORD);
		textPassword2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

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

		// Set the default state.
		btnPKCS12.setSelection(true);
		btnPKCS12.redraw();
		btnPKCS12.update();
		setPKCS12FieldsEnabled(true);
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Create", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		m_bindingContext = initDataBindings();

		// Disable OK button by default.
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	@Override
	public boolean close() {
		if (getReturnCode() == IDialogConstants.OK_ID) {
			if (model.getPassword() == null || model.getPassword().length() < PluginDefaults.MIN_PASSWORD_LENGTH) {
				if (!Dialogs.openConfirm(getParentShell(), "Weak Passphrase",
						"The Passphrase provided is weak. Are you sure you wish to proceed with a weak passphrase?",
						"Proceed", "Cancel")) {
					return false;
				}
			}
		}
		return super.close();
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
		 * CA Description
		 */
		IObservableValue<?> caDescriptionWidget = WidgetProperties.text(SWT.Modify).observe(textCADescription);
		IObservableValue<?> caDescriptionModel = PojoProperties.value("cADescription").observe(model);
		UpdateValueStrategy s = new UpdateValueStrategy().setAfterGetValidator(value -> {
			String o = (String) value;
			if (o.isEmpty() || o.trim().isEmpty()) {
				return ValidationStatus.error("CA Description cannot be empty");
			}
			return ValidationStatus.ok();
		});
		Binding b = bindingContext.bindValue(caDescriptionWidget, caDescriptionModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Base Location
		 */
		IObservableValue<?> locationWidget = WidgetProperties.text(SWT.Modify).observe(textBaseLocation);
		IObservableValue<?> locationModel = PojoProperties.value("baseLocation").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(new PathValidator());
		b = bindingContext.bindValue(locationWidget, locationModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		// Ensure CA Description and Base Location don't make up something already in use...
		MultiValidator locCAValidator = new ExistingCAValidator(caDescriptionModel, locationModel);
		bindingContext.addValidationStatusProvider(locCAValidator);
		ControlDecorationSupport.create(locCAValidator.getValidationStatus(), SWT.TOP | SWT.LEFT, caDescriptionWidget);
		ControlDecorationSupport.create(locCAValidator.getValidationStatus(), SWT.TOP | SWT.LEFT, locationWidget);
		
		/*
		 * PKCS12 Enable
		 */
		IObservableValue<?> PKCS12EnableWidget = WidgetProperties.selection().observe(btnPKCS12);
		IObservableValue<?> PKCS12EnableModel = PojoProperties.value("pkcs12").observe(model);
		b = bindingContext.bindValue(PKCS12EnableWidget, PKCS12EnableModel, null, null);

		final FileValidator fv = new FileValidator();

		/*
		 * PKCS12 Location
		 */
		IObservableValue<?> PKCS12Widget = WidgetProperties.text(SWT.Modify).observe(textPKCS12Filename);
		IObservableValue<?> PKCS12Model = PojoProperties.value("pkcs12Filename").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(value -> {
			if (btnPKCS12.getSelection()) {
				return fv.validate(value);
			}
			return ValidationStatus.ok();
		});
		b = bindingContext.bindValue(PKCS12Widget, PKCS12Model, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Cert/KeyPair Enable
		 */
		IObservableValue<?> CertEnableWidget = WidgetProperties.selection().observe(btnCertKeyPair);
		IObservableValue<?> CertEnableModel = PojoProperties.value("certPrivateKey").observe(model);
		b = bindingContext.bindValue(CertEnableWidget, CertEnableModel, null, null);

		/*
		 * Certificate Location
		 */
		IObservableValue<?> CertificateWidget = WidgetProperties.text(SWT.Modify).observe(textCertificateFilename);
		IObservableValue<?> CertificateModel = PojoProperties.value("certFilename").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(value -> {
			if (btnCertKeyPair.getSelection()) {
				return fv.validate(value);
			}
			return ValidationStatus.ok();
		});
		b = bindingContext.bindValue(CertificateWidget, CertificateModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Private Key Location
		 */
		IObservableValue<?> PrivateKeyWidget = WidgetProperties.text(SWT.Modify).observe(textPrivateKeyFilename);
		IObservableValue<?> PrivateKeyModel = PojoProperties.value("privateKeyFilename").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(value -> {
			if (btnCertKeyPair.getSelection()) {
				return fv.validate(value);
			}
			return ValidationStatus.ok();
		});
		b = bindingContext.bindValue(PrivateKeyWidget, PrivateKeyModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		//
		/*
		 * Password field
		 */
		IObservableValue<?> password1Widget = WidgetProperties.text(SWT.Modify).observe(textPassword1);
		IObservableValue<?> password2Widget = WidgetProperties.text(SWT.Modify).observe(textPassword2);

		final IObservableValue<String> pmiddleField1 = new WritableValue<String>(model.getPassword(), String.class);
		final IObservableValue<String> pmiddleField2 = new WritableValue<String>(model.getPassword(), String.class);

		bindingContext.bindValue(password1Widget, pmiddleField1);
		bindingContext.bindValue(password2Widget, pmiddleField2);

		MultiValidator v = new PassphraseValidator(pmiddleField1, pmiddleField2, PluginDefaults.MIN_PASSWORD_LENGTH);
		bindingContext.addValidationStatusProvider(v);

		IObservableValue<?> passwordModel = PojoProperties.value("password").observe(model);
		bindingContext.bindValue(v.observeValidatedValue(pmiddleField1), passwordModel);

		ControlDecorationSupport.create(v.getValidationStatus(), SWT.TOP | SWT.LEFT, password1Widget);
		ControlDecorationSupport.create(v.getValidationStatus(), SWT.TOP | SWT.LEFT, password2Widget);
		//

		/*
		 * Bind the OK button for enablement.
		 */
		Button okButton = getButton(IDialogConstants.OK_ID);

		IObservableValue<?> buttonEnable = WidgetProperties.enabled().observe(okButton);
		// Create a list of all validators made available via bindings and global validators.
		IObservableList list = new WritableList<>(bindingContext.getValidationRealm());
		list.addAll(bindingContext.getBindings());
		list.addAll(bindingContext.getValidationStatusProviders());
		IObservableValue<?> validationStatus = new AggregateValidationStatus(bindingContext.getValidationRealm(), list,
				AggregateValidationStatus.MAX_SEVERITY);

		bindingContext.bindValue(buttonEnable, validationStatus,
				new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER),
				new UpdateValueStrategy().setConverter(IConverter.create(IStatus.class, Boolean.TYPE, o -> {
					return Boolean.valueOf(((IStatus) o).isOK() || ((IStatus) o).matches(IStatus.WARNING));
				})));
		return bindingContext;
	}

	/**
	 * Set the PKCS12 fields to be in an enabled state.
	 * 
	 * @param enable TRUE to enable the PKCS12 fields, or FALSE to enable the cert/key pair fields.
	 */
	private void setPKCS12FieldsEnabled(boolean enable) {
		textPKCS12Filename.setEnabled(enable);
		btnPKCS12File.setEnabled(enable);

		textCertificateFilename.setEnabled(!enable);
		textPrivateKeyFilename.setEnabled(!enable);
		btnCertificateFile.setEnabled(!enable);
		btnKeyFile.setEnabled(!enable);
	}
}
