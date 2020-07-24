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

package net.sourceforge.dkartaschew.halimede.ui.dialogs;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.ui.model.GeneralNameModel;
import net.sourceforge.dkartaschew.halimede.ui.validators.GeneralNameValidator;

public class GeneralNameDialog extends Dialog {

	private final GeneralNameModel model;

	private ComboViewer comboViewerTag;
	private Combo comboTag;
	private Text text;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public GeneralNameDialog(Shell parentShell, GeneralNameModel model) {
		super(parentShell);
		this.model = model;
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Subject Alternate Name");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent The parent composite to add to.
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(2, false);
		// Add additional right margin for the error indicator.
		gl_container.marginRight = 10;
		gl_container.horizontalSpacing = 10;
		container.setLayout(gl_container);

		comboViewerTag = new ComboViewer(container, SWT.READ_ONLY);
		comboTag = comboViewerTag.getCombo();
		comboTag.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewerTag.setLabelProvider(new LabelProvider());
		comboViewerTag.setContentProvider(new ArrayContentProvider());
		comboViewerTag.setInput(GeneralNameTag.values());
		comboTag.setToolTipText("The type of general name");
		// Set default value.
		comboTag.select(model.getTag().getTag());

		text = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.minimumWidth = 250;
		text.setLayoutData(gd_text);
		text.setText(model.getValue());

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Update", true);
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
		 * Name text and tag
		 */
		IObservableValue<?> tagWidget = WidgetProperties.selection().observe(comboTag);
		IObservableValue<?> tagModel = PojoProperties.value("tag").observe(model);

		IObservableValue<?> nameWidget = WidgetProperties.text(SWT.Modify).observe(text);
		IObservableValue<?> nameModel = PojoProperties.value("value").observe(model);

		// Multi or cross validation requires the use of an intermediate value to work.
		final IObservableValue<GeneralNameTag> middleField1 = new WritableValue<GeneralNameTag>(model.getTag(),	GeneralNameTag.class);
		final IObservableValue<String> middleField2 = new WritableValue<String>(model.getValue(), String.class);

		IConverter convertStringToKeyType = IConverter.create(String.class, GeneralNameTag.class,
				(o1) -> GeneralNameTag.forDescription((String) o1));
		UpdateValueStrategy s = UpdateValueStrategy.create(convertStringToKeyType);
		bindingContext.bindValue(tagWidget, middleField1, s, null);
		bindingContext.bindValue(nameWidget, middleField2);

		// We simple set a validator on the intermediate values.
		final MultiValidator validator = new GeneralNameValidator(middleField1, middleField2);
		bindingContext.addValidationStatusProvider(validator);

		bindingContext.bindValue(validator.observeValidatedValue(middleField1), tagModel);
		bindingContext.bindValue(validator.observeValidatedValue(middleField2), nameModel);

		// And tie the decorator on the validator and widgets being watched.
		ControlDecorationSupport.create(validator.getValidationStatus(), SWT.TOP | SWT.RIGHT, nameWidget);

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
