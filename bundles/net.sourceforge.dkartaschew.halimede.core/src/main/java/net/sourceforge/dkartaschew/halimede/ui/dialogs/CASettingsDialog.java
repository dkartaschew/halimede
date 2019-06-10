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

import java.util.Arrays;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.ui.model.CASettingsModel;

public class CASettingsDialog extends Dialog {

	private Text textCADescription;
	private Spinner spinnerExpiry;
	private ComboViewer comboViewerSigAlg;
	private Combo comboSigAlg;
	private Button chkIncremental;
	private Button chkEnableLog;
	
	private final CASettingsModel model;
	
	/**
	 * Create the dialog.
	 * @param parentShell The parent shell
	 * @param model The model to complete.
	 */
	public CASettingsDialog(Shell parentShell, CASettingsModel model) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.model = model;
	}
	
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Certificate Authority Settings");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	/**
	 * Create contents of the dialog.
	 * @param parent The parent of this dialog.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumWidth = 450;
		container.setLayoutData(gd);
		
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		container.setLayout(layout);

		Label lblDescription = new Label(container, SWT.NONE);
		lblDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDescription.setText("Description:");

		textCADescription = new Text(container, SWT.BORDER);
		textCADescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textCADescription.setToolTipText("The descriptive name for this Certificate Authority.");
		textCADescription.setText(model.getDescription());
		
		Label lblSubject = new Label(container, SWT.NONE);
		lblSubject.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSubject.setText("Certificate Subject:");
		
		Text lblSubject2 = new Text(container, SWT.BORDER);
		lblSubject2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblSubject2.setText(model.getSubject());
		lblSubject2.setEditable(false);
		//lblSubject2.setEnabled(false);
		
		Label lblLocation = new Label(container, SWT.NONE);
		lblLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocation.setText("Location:");
		
		Text lblLocation2 = new Text(container, SWT.BORDER);
		lblLocation2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		lblLocation2.setText(model.getBasePath().toString());
		lblLocation2.setEditable(false);
		//lblLocation2.setEnabled(false);
		
		Label lblExpiryDays = new Label(container, SWT.NONE);
		lblExpiryDays.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblExpiryDays.setText("Expiry period (days):");
		
		spinnerExpiry = new Spinner(container, SWT.BORDER);
		spinnerExpiry.setToolTipText("Default expiry period (days)");
		spinnerExpiry.setSelection(model.getExpiryDays());
		spinnerExpiry.setPageIncrement(31);
		spinnerExpiry.setMaximum(36500);
		spinnerExpiry.setMinimum(1);
		spinnerExpiry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblSignature = new Label(container, SWT.NONE);
		lblSignature.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSignature.setText("Signature Algorithm:");
		
		
		comboViewerSigAlg = new ComboViewer(container, SWT.READ_ONLY);
		comboSigAlg = comboViewerSigAlg.getCombo();
		comboSigAlg.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewerSigAlg.setLabelProvider(new LabelProvider());
		comboViewerSigAlg.setContentProvider(new ArrayContentProvider());
		comboViewerSigAlg.setInput(model.getSignatureAlgorithms());
		comboSigAlg.setToolTipText("The signature algorithm for signing issued certificates");
		// Set default value.
		int index = Arrays.asList(model.getSignatureAlgorithms()).indexOf(model.getSignatureAlgorithm());
		comboSigAlg.select(index >=0 ? index : 0);
		
		Label lblIncSerial = new Label(container, SWT.NONE);
		lblIncSerial.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblIncSerial.setText("Incremental Serial Numbers:");
		
		chkIncremental = new Button(container, SWT.CHECK);
		chkIncremental.setSelection(model.isIncrementalSerial());
		chkIncremental.setToolTipText("Use incremental serial numbers for issued Certificates");
		
		Label lblEnableLog = new Label(container, SWT.NONE);
		lblEnableLog.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEnableLog.setText("Enable Activity Log:");
		
		chkEnableLog = new Button(container, SWT.CHECK);
		chkEnableLog.setSelection(model.isEnableLog());
		chkEnableLog.setToolTipText("Enable Acivity Log for Certificate Authority.");
		
		return area;
	}

	/**
	 * Create contents of the button bar.
	 * @param parent The parent 
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		initDataBindings();
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
		IObservableValue<?> caDescriptionModel = PojoProperties.value("description").observe(model);
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
		 * Default Expiry Period.
		 */
		IObservableValue<?> expiryDaysWidget = WidgetProperties.selection().observe(spinnerExpiry);
		IObservableValue<?> cexpiryDaysModel = PojoProperties.value("expiryDays").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(value -> {
			int o = (int) value;
			if (o <= 0) {
				return ValidationStatus.error("Expiry period must be 1 or greater");
			}
			return ValidationStatus.ok();
		});
		b = bindingContext.bindValue(expiryDaysWidget, cexpiryDaysModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);
		
		/*
		 * Sig Algorithm Type
		 */
		IObservableValue<?> keyTypeWidget = WidgetProperties.selection().observe(comboSigAlg);
		IObservableValue<?> keyTypeModel = PojoProperties.value("signatureAlgorithm").observe(model);
		IConverter convertStringToKeyType = IConverter.create(String.class, SignatureAlgorithm.class,
				(o1) -> SignatureAlgorithm.forAlgID((String) o1));
		s = UpdateValueStrategy.create(convertStringToKeyType);

		bindingContext.bindValue(keyTypeWidget, keyTypeModel, s, null);
		
		/*
		 * Incremental Serial
		 */
		IObservableValue<?> serialWidget = WidgetProperties.selection().observe(chkIncremental);
		IObservableValue<Boolean> serialModel = PojoProperties.value("incrementalSerial").observe(model);
		bindingContext.bindValue(serialWidget, serialModel, null, null);

		/*
		 * Enable log
		 */
		IObservableValue<?> logWidget = WidgetProperties.selection().observe(chkEnableLog);
		IObservableValue<Boolean> logModel = PojoProperties.value("enableLog").observe(model);
		bindingContext.bindValue(logWidget, logModel, null, null);
		
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
