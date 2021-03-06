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

package net.sourceforge.dkartaschew.halimede.ui.dialogs;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.beans.typed.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.Entropy;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.ui.model.ExportInformationModel;
import net.sourceforge.dkartaschew.halimede.ui.validators.PassphraseValidator;

public class ExportPKCS12Dialog extends Dialog {

	private final ExportInformationModel model;

	private Text textBaseLocation;
	private ComboViewer comboViewerCipherType;
	private Combo comboCipherType;
	private Text textPassword1;
	private Text textPassword2;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell The parent shell
	 * @param model       The model to populate.
	 */
	public ExportPKCS12Dialog(Shell parentShell, ExportInformationModel model) {
		super(parentShell);
		this.model = model;
		setShellStyle(SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Export Key Information (PKCS#12)");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		container.setLayout(layout);

		Label lblFilename = new Label(container, SWT.NONE);
		lblFilename.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFilename.setText("Filename:");

		textBaseLocation = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.minimumWidth = 250;
		textBaseLocation.setLayoutData(gd_text);

		Button buttonLocation = new Button(container, SWT.NONE);
		buttonLocation.setText("...");
		buttonLocation.addListener(SWT.Selection, e -> {
			FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
			dlg.setText("File to save Certificate information");
			dlg.setOverwrite(true);
			dlg.setFilterExtensions(new String[] { "*.p12", "*.*" });
			dlg.setFilterNames(new String[] { "PKCS#12 (*.p12)", "All Files (*.*)" });
			String dir = dlg.open();
			if (dir != null) {
				textBaseLocation.setText(dir);
			}
		});

		Label lblEncryptionCipher = new Label(container, SWT.NONE);
		lblEncryptionCipher.setText("Encryption Cipher:");

		comboViewerCipherType = new ComboViewer(container, SWT.READ_ONLY);
		comboCipherType = comboViewerCipherType.getCombo();
		comboCipherType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		comboViewerCipherType.setLabelProvider(new LabelProvider());
		comboViewerCipherType.setContentProvider(new ArrayContentProvider());
		comboViewerCipherType.setInput(PKCS12Cipher.values());
		comboCipherType.setToolTipText("The Keying material type");
		// Set default value.
		model.setPkcs12Cipher(PKCS12Cipher.DES3);
		comboCipherType.select(0);

		new Label(container, SWT.NONE);

		Label lblPassword = new Label(container, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Passphrase:");

		textPassword1 = new Text(container, SWT.BORDER | SWT.PASSWORD);
		textPassword1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblConfirm = new Label(container, SWT.NONE);
		lblConfirm.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConfirm.setText("Confirmation:");

		textPassword2 = new Text(container, SWT.BORDER | SWT.PASSWORD);
		textPassword2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		Label lblStrength = new Label(container, SWT.NONE);
		lblStrength.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStrength.setText("Strength:");

		ProgressBar passwordStrength = new ProgressBar(container, SWT.SMOOTH);
		passwordStrength.setToolTipText(Entropy.MESSAGE_RANDOM);
		passwordStrength.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		passwordStrength.setMaximum(Entropy.MAX);
		textPassword1.addModifyListener(o -> {
			int entropy = (int) Entropy.random(textPassword1.getText());
			passwordStrength.setSelection(Math.min(entropy, passwordStrength.getMaximum()));
		});

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Export", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		initDataBindings();

		// Disable OK button by default.
		getButton(IDialogConstants.OK_ID).setEnabled(false);
	}

	/**
	 * Create the data bindings for controls to the model
	 * 
	 * @return The databinding holder.
	 */
	protected DataBindingContext initDataBindings() {

		DataBindingContext bindingContext = new DataBindingContext();

		/*
		 * Base Location
		 */
		IObservableValue<String> locationWidget = WidgetProperties.text(SWT.Modify).observe(textBaseLocation);
		IObservableValue<String> locationModel = PojoProperties.value("filename", String.class).observe(model);
		UpdateValueStrategy<String, String> s = new UpdateValueStrategy<String, String>()
				.setAfterGetValidator(value -> {
					if (value.isEmpty()) {
						return ValidationStatus.error("Location cannot be empty");
					}
					return ValidationStatus.ok();
				});
		Binding b = bindingContext.bindValue(locationWidget, locationModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Cipher Type
		 */
		IObservableValue<String> cipherTypeWidget = WidgetProperties.comboSelection().observe(comboCipherType);
		IObservableValue<PKCS12Cipher> cipherTypeModel = PojoProperties.value("pkcs12Cipher", PKCS12Cipher.class)
				.observe(model);
		IConverter<String, PKCS12Cipher> convertStringToPKCS12CipherType = IConverter.create(String.class,
				PKCS12Cipher.class, (o1) -> PKCS12Cipher.valueOf(o1));
		UpdateValueStrategy<String, PKCS12Cipher> s1 = UpdateValueStrategy.create(convertStringToPKCS12CipherType)
				.setAfterConvertValidator(value -> {
					if (value != PKCS12Cipher.DES3) {
						return ValidationStatus.warning("PKCS#12 with AES encryption is not widely supported. "
								+ "Please check your tools to ensure that they support this configuration.");
					}
					return ValidationStatus.ok();
				});

		b = bindingContext.bindValue(cipherTypeWidget, cipherTypeModel, s1, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Password field
		 */
		IObservableValue<String> password1Widget = WidgetProperties.text(SWT.Modify).observe(textPassword1);
		IObservableValue<String> password2Widget = WidgetProperties.text(SWT.Modify).observe(textPassword2);

		final IObservableValue<String> pmiddleField1 = new WritableValue<String>(model.getPassword(), String.class);
		final IObservableValue<String> pmiddleField2 = new WritableValue<String>(model.getPassword(), String.class);

		bindingContext.bindValue(password1Widget, pmiddleField1);
		bindingContext.bindValue(password2Widget, pmiddleField2);

		MultiValidator v = new PassphraseValidator(pmiddleField1, pmiddleField2, PluginDefaults.MIN_PASSWORD_LENGTH);
		bindingContext.addValidationStatusProvider(v);

		IObservableValue<String> passwordModel = PojoProperties.value("password", String.class).observe(model);
		bindingContext.bindValue(v.observeValidatedValue(pmiddleField1), passwordModel);

		ControlDecorationSupport.create(v.getValidationStatus(), SWT.TOP | SWT.LEFT, password1Widget);
		ControlDecorationSupport.create(v.getValidationStatus(), SWT.TOP | SWT.LEFT, password2Widget);
		//

		/*
		 * Bind the OK button for enablement.
		 */
		Button okButton = getButton(IDialogConstants.OK_ID);

		IObservableValue<Boolean> buttonEnable = WidgetProperties.enabled().observe(okButton);
		// Create a list of all validators made available via bindings and global
		// validators.
		IObservableList<ValidationStatusProvider> list = new WritableList<>(bindingContext.getValidationRealm());
		list.addAll(bindingContext.getBindings());
		list.addAll(bindingContext.getValidationStatusProviders());
		IObservableValue<IStatus> validationStatus = new AggregateValidationStatus(bindingContext.getValidationRealm(), list,
				AggregateValidationStatus.MAX_SEVERITY);

		bindingContext.bindValue(buttonEnable, validationStatus,
				new UpdateValueStrategy<Boolean, IStatus>(UpdateValueStrategy.POLICY_NEVER),
				new UpdateValueStrategy<IStatus, Boolean>().setConverter(IConverter.create(IStatus.class, Boolean.TYPE, o -> {
					return Boolean.valueOf(((IStatus) o).isOK() || ((IStatus) o).matches(IStatus.WARNING));
				})));
		return bindingContext;
	}

}
