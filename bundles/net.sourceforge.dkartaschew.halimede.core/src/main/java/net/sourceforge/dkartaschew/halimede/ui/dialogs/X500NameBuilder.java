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
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.ui.model.X500NameModel;

public class X500NameBuilder extends Dialog {

	/**
	 * The limit for number of chars in each field. 
	 * (Most fields should be less than 64 characters).
	 */
	private final static int TEXT_FIELD_LIMIT = 64;
	
	private X500NameModel model;
	private Text textCN;
	private Text textE;
	private Text textOU;
	private Text textO;
	private Text textL;
	private Text textSTR;
	private Text textST;
	private Text textC;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell The parent shell
	 * @param model The name model.
	 */
	public X500NameBuilder(Shell parentShell, X500NameModel model) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.model = model;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("X500 Name Assistant");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}
	
	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent The parent composite.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		container.setLayout(layout);
		
		Label lblCN = new Label(container, SWT.NONE);
		lblCN.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCN.setText("Common Name (CN):");
		
		textCN = new Text(container, SWT.BORDER);
		GridData gd_textCN = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textCN.widthHint = 320;
		textCN.setLayoutData(gd_textCN);
		textCN.setTextLimit(TEXT_FIELD_LIMIT);
		
		Label lblE = new Label(container, SWT.NONE);
		lblE.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblE.setText("Email Address (E):");
		
		textE = new Text(container, SWT.BORDER);
		textE.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textE.setTextLimit(TEXT_FIELD_LIMIT);
		
		Label lblOU = new Label(container, SWT.NONE);
		lblOU.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblOU.setText("Organizational Unit Name (OU):");
		
		textOU = new Text(container, SWT.BORDER);
		textOU.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textOU.setTextLimit(TEXT_FIELD_LIMIT);
		
		Label LblO = new Label(container, SWT.NONE);
		LblO.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		LblO.setText("Organisation (O):");
		
		textO = new Text(container, SWT.BORDER);
		textO.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textO.setTextLimit(TEXT_FIELD_LIMIT);
		
		Label lblL = new Label(container, SWT.NONE);
		lblL.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblL.setText("Locality Name (L):");
		
		textL = new Text(container, SWT.BORDER);
		textL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textL.setTextLimit(TEXT_FIELD_LIMIT);
		
		Label lblSTR = new Label(container, SWT.NONE);
		lblSTR.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSTR.setText("Street Address (STREET):");
		
		textSTR = new Text(container, SWT.BORDER);
		textSTR.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textSTR.setTextLimit(TEXT_FIELD_LIMIT);
		
		Label lblST = new Label(container, SWT.NONE);
		lblST.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblST.setText("State or Province Name (ST):");
		
		textST = new Text(container, SWT.BORDER);
		textST.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textST.setTextLimit(TEXT_FIELD_LIMIT);

		Label lblC = new Label(container, SWT.NONE);
		lblC.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblC.setText("Country Name (C):");
		
		textC = new Text(container, SWT.BORDER);
		textC.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textC.setTextLimit(TEXT_FIELD_LIMIT);
		
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		initDataBindings();
	}
	
	@SuppressWarnings("unchecked")
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue<String> observeTextTextCNObserveWidget = WidgetProperties.text(SWT.Modify).observe(textCN);
		IObservableValue<String> commonNameModelObserveValue = PojoProperties.value("commonName").observe(model);
		UpdateValueStrategy s = new UpdateValueStrategy().setAfterGetValidator(value -> {
			String o = (String) value;
			if (o.trim().isEmpty()) {
				return ValidationStatus.error("Common Name cannot be empty");
			}
			return ValidationStatus.ok();
		});
		Binding b = bindingContext.bindValue(observeTextTextCNObserveWidget, commonNameModelObserveValue, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		//
		IObservableValue<String> observeTextTextEObserveWidget = WidgetProperties.text(SWT.Modify).observe(textE);
		IObservableValue<String> emailAddressModelObserveValue = PojoProperties.value("emailAddress").observe(model);
		bindingContext.bindValue(observeTextTextEObserveWidget, emailAddressModelObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextOUObserveWidget = WidgetProperties.text(SWT.Modify).observe(textOU);
		IObservableValue<String> organisationUnitModelObserveValue = PojoProperties.value("organisationUnit").observe(model);
		bindingContext.bindValue(observeTextTextOUObserveWidget, organisationUnitModelObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextOObserveWidget = WidgetProperties.text(SWT.Modify).observe(textO);
		IObservableValue<String> organisationModelObserveValue = PojoProperties.value("organisation").observe(model);
		bindingContext.bindValue(observeTextTextOObserveWidget, organisationModelObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextLObserveWidget = WidgetProperties.text(SWT.Modify).observe(textL);
		IObservableValue<String> locationModelObserveValue = PojoProperties.value("location").observe(model);
		bindingContext.bindValue(observeTextTextLObserveWidget, locationModelObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextSTRObserveWidget = WidgetProperties.text(SWT.Modify).observe(textSTR);
		IObservableValue<String> streetModelObserveValue = PojoProperties.value("street").observe(model);
		bindingContext.bindValue(observeTextTextSTRObserveWidget, streetModelObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextSTObserveWidget = WidgetProperties.text(SWT.Modify).observe(textST);
		IObservableValue<String> stateModelObserveValue = PojoProperties.value("state").observe(model);
		bindingContext.bindValue(observeTextTextSTObserveWidget, stateModelObserveValue, null, null);
		//
		IObservableValue<String> observeTextTextCObserveWidget = WidgetProperties.text(SWT.Modify).observe(textC);
		IObservableValue<String> countryModelObserveValue = PojoProperties.value("country").observe(model);
		bindingContext.bindValue(observeTextTextCObserveWidget, countryModelObserveValue, null, null);
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
}
