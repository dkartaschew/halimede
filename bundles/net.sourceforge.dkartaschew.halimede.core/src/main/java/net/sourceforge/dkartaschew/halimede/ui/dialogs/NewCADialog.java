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

package net.sourceforge.dkartaschew.halimede.ui.dialogs;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import org.bouncycastle.asn1.x500.X500Name;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.nebula.jface.cdatetime.CDateTimeObservableValue;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.Entropy;
import net.sourceforge.dkartaschew.halimede.enumeration.KeyType;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCAModel;
import net.sourceforge.dkartaschew.halimede.ui.model.X500NameModel;
import net.sourceforge.dkartaschew.halimede.ui.validators.DatePeriodValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.KeyTypeWarningValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.PassphraseValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.PathValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.URIValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.X500NameValidator;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

import org.eclipse.swt.widgets.ProgressBar;

@SuppressWarnings("restriction")
public class NewCADialog extends Dialog {

	private DataBindingContext m_bindingContext;

	private Text textCADescription;
	private Text textBaseLocation;
	private Text textX500Name;
	private Text textCRLLocation;
	private Text textPassword1;
	private Text textPassword2;
	private CDateTime startDate;
	private CDateTime expiryDate;
	private ComboViewer comboViewerKeyType;
	private Combo comboKeyType;

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
	public NewCADialog(Shell parentShell, NewCAModel model) {
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
		textCADescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
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
		layout = new GridLayout(4, false);
		layout.horizontalSpacing = 10;
		grpCertificateAndKeying.setLayout(layout);
		grpCertificateAndKeying.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		grpCertificateAndKeying.setText("Certificate and Keying Material");

		Label lblX500Name = new Label(grpCertificateAndKeying, SWT.NONE);
		lblX500Name.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblX500Name.setText("Subject:");

		Composite composite = new Composite(grpCertificateAndKeying, SWT.NONE);
		GridLayout gl_composite = new GridLayout(2, false);
		gl_composite.verticalSpacing = 0;
		gl_composite.marginWidth = 0;
		gl_composite.marginHeight = 0;
		composite.setLayout(gl_composite);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1));

		textX500Name = new Text(composite, SWT.BORDER);
		textX500Name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textX500Name.setToolTipText("The Subject should be a X500Name");

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
			if(dialog.open() == IDialogConstants.OK_ID) {
				textX500Name.setText(xmodel.asX500Name().toString());
				m_bindingContext.updateModels();
			}
		});

		Label lblKeyType = new Label(grpCertificateAndKeying, SWT.NONE);
		lblKeyType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKeyType.setText("Key Type:");

		comboViewerKeyType = new ComboViewer(grpCertificateAndKeying, SWT.READ_ONLY);
		comboKeyType = comboViewerKeyType.getCombo();
		comboKeyType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewerKeyType.setLabelProvider(new KeyTypeLabelProvider());
		comboViewerKeyType.setContentProvider(new ArrayContentProvider());
		comboViewerKeyType.setInput(KeyType.getAllowedValues());
		comboKeyType.setToolTipText("The Keying material type");
		// Set default value.
		model.setKeyType(KeyType.getDefaultKeyType());
		comboKeyType.select(KeyType.getIndex(model.getKeyType()));

		new Label(grpCertificateAndKeying, SWT.NONE);
		new Label(grpCertificateAndKeying, SWT.NONE);

		Label lblStartDate = new Label(grpCertificateAndKeying, SWT.NONE);
		lblStartDate.setText("Start Date:");
		lblStartDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		startDate = new CDateTime(grpCertificateAndKeying, CDT.BORDER | CDT.TAB_FIELDS);
		startDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		startDate.setPattern(DateTimeUtil.DEFAULT_FORMAT);
		startDate.setTimeZone(TimeZone.getTimeZone(DateTimeUtil.DEFAULT_ZONE));
		// CDATETIME DOESN'T SUPPORT TOOLTIP IN SPINNER MODE.
		// startDate.setToolTipText("The start date of the issuing Certificate for the Certificate Authority");
		startDate.setSelection(DateTimeUtil.toDate(model.getStartDate()));

		Label lblNewLabel = new Label(grpCertificateAndKeying, SWT.NONE);
		lblNewLabel.setText("Expiry Date:");

		expiryDate = new CDateTime(grpCertificateAndKeying, CDT.BORDER | CDT.TAB_FIELDS);
		expiryDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		expiryDate.setPattern(DateTimeUtil.DEFAULT_FORMAT);
		expiryDate.setTimeZone(TimeZone.getTimeZone(DateTimeUtil.DEFAULT_ZONE));
		// expiryDate.setToolTipText("The expiry date of the issuing Certificate for the Certificate Authority");
		expiryDate.setSelection(DateTimeUtil.toDate(model.getExpiryDate()));

		Label lblCRL = new Label(grpCertificateAndKeying, SWT.NONE);
		lblCRL.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCRL.setText("CRL Location:");

		textCRLLocation = new Text(grpCertificateAndKeying, SWT.BORDER);
		textCRLLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		//textCRLLocation.setToolTipText("CRL Location is currently not implemented");

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

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		m_bindingContext = initDataBindings();

		// Disable OK button by default.
		getButton(IDialogConstants.OK_ID).setEnabled(false);
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
			if (o.isEmpty()) {
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

		/*
		 * X500 Name
		 */
		IObservableValue<?> x500NameWidget = WidgetProperties.text(SWT.Modify).observe(textX500Name);
		IObservableValue<?> x500NameModel = PojoProperties.value("x500Name").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(new X500NameValidator(null));
		b = bindingContext.bindValue(x500NameWidget, x500NameModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Key Type
		 */
		IObservableValue<?> keyTypeWidget = WidgetProperties.selection().observe(comboKeyType);
		IObservableValue<?> keyTypeModel = PojoProperties.value("keyType").observe(model);
		IConverter convertStringToKeyType = IConverter.create(String.class, KeyType.class,
				(o1) -> KeyType.forDescription((String) o1));
		s = UpdateValueStrategy.create(convertStringToKeyType)//
				.setAfterConvertValidator(new KeyTypeWarningValidator());

		b = bindingContext.bindValue(keyTypeWidget, keyTypeModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * CRL Location
		 */
		IObservableValue<?> crlLocationWidget = WidgetProperties.text(SWT.Modify).observe(textCRLLocation);
		IObservableValue<?> cRLLocationModel = PojoProperties.value("cRLLocation").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(new URIValidator());
		b = bindingContext.bindValue(crlLocationWidget, cRLLocationModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Start Date and End Date
		 */
		IObservableValue<?> startDateWidget = new SWTObservableValueDecorator(new CDateTimeObservableValue(startDate),
				startDate);
		IObservableValue<?> startDateModel = PojoProperties.value("startDate").observe(model);

		IObservableValue<?> expiryDateWidget = new SWTObservableValueDecorator(new CDateTimeObservableValue(expiryDate),
				expiryDate);
		IObservableValue<?> expiryDateModel = PojoProperties.value("expiryDate").observe(model);

		// Multi or cross validation requires the use of an intermediate value to work.
		final IObservableValue<ZonedDateTime> middleField1 = new WritableValue<ZonedDateTime>(model.getStartDate(),
				ZonedDateTime.class);
		final IObservableValue<ZonedDateTime> middleField2 = new WritableValue<ZonedDateTime>(model.getExpiryDate(),
				ZonedDateTime.class);
		s = new UpdateValueStrategy().setConverter(IConverter.create(Date.class, ZonedDateTime.class,
				(date) -> DateTimeUtil.toZonedDateTime((Date) date)));
		UpdateValueStrategy s2 = new UpdateValueStrategy()
				.setConverter(IConverter.create(ZonedDateTime.class, Date.class, (date) -> {
					return date == null ? null : Date.from(((ZonedDateTime) date).toInstant());
				}));
		bindingContext.bindValue(startDateWidget, middleField1, s, s2);
		bindingContext.bindValue(expiryDateWidget, middleField2, s, s2);

		// We simple set a validator on the intermediate values.
		final MultiValidator validator = new DatePeriodValidator(middleField1, middleField2, null, null);
		bindingContext.addValidationStatusProvider(validator);

		bindingContext.bindValue(validator.observeValidatedValue(middleField1), startDateModel);
		bindingContext.bindValue(validator.observeValidatedValue(middleField2), expiryDateModel);

		// And tie the decorator on the validator and widgets being watched.
		ControlDecorationSupport.create(validator.getValidationStatus(), SWT.TOP | SWT.LEFT, startDateWidget);
		ControlDecorationSupport.create(validator.getValidationStatus(), SWT.TOP | SWT.LEFT, expiryDateWidget);

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

	@Override
	public boolean close() {
		if (getReturnCode() == IDialogConstants.OK_ID) {
			if (model.getPassword() == null || model.getPassword().length() < PluginDefaults.MIN_PASSWORD_LENGTH) {
				if (!MessageDialog.openConfirm(getParentShell(), "Weak Passphrase",
						"The Passphrase provided is weak. Are you sure you wish to proceed with a weak passphrase?")) {
					return false;
				}
			}
		}
		return super.close();
	}
}
