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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.ui.model.RestoreCAModel;
import net.sourceforge.dkartaschew.halimede.ui.validators.FileValidator;
import net.sourceforge.dkartaschew.halimede.ui.validators.PathValidator;

public class RestoreCADialog extends Dialog {

	private final RestoreCAModel model;

	private Text textFilename;
	private Text textBaseLocation;
	private Button buttonAddToManager;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell The parent shell
	 * @param model The model to populate.
	 */
	public RestoreCADialog(Shell parentShell, RestoreCAModel model) {
		super(parentShell);
		this.model = model;
		setShellStyle(SWT.SHELL_TRIM | SWT.BORDER | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Restore Certificate Authority");
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
		lblFilename.setText("Backup File:");

		textFilename = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_text.minimumWidth = 250;
		textFilename.setLayoutData(gd_text);
		if (model.getFilename() != null) {
			textFilename.setText(model.getFilename());
		}

		Button buttonFilename = new Button(container, SWT.NONE);
		buttonFilename.setText("...");
		buttonFilename.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog dlg = new FileDialog(getShell(), SWT.OPEN);
				dlg.setText("Filename");
				dlg.setFilterExtensions(new String[] { "*.zip", "*.*" });
				dlg.setFilterNames(new String[] { "Zip File (*.zip)", "All Files (*.*)" });
				if (model.getFilename() != null) {
					dlg.setFileName(model.getFilename());
				}
				String file = dlg.open();
				if (file != null) {
					textFilename.setText(file);
				}
			}
		});

		Label lblBaseLocation = new Label(container, SWT.NONE);
		lblBaseLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblBaseLocation.setText("Restore Location:");

		textBaseLocation = new Text(container, SWT.BORDER);
		GridData gd_textBL = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textBL.minimumWidth = 250;
		textBaseLocation.setLayoutData(gd_textBL);
		if (model.getBaseLocation() != null) {
			textBaseLocation.setText(model.getBaseLocation());
		}

		Button buttonBaseLocation = new Button(container, SWT.NONE);
		buttonBaseLocation.setText("...");
		buttonBaseLocation.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				DirectoryDialog dlg = new DirectoryDialog(getShell());

				dlg.setText("Restore Location");
				dlg.setMessage("Select a directory");
				if (model.getFilename() != null) {
					dlg.setFilterPath(model.getBaseLocation());
				}
				String dir = dlg.open();
				if (dir != null) {
					textBaseLocation.setText(dir);
				}
			}
		});

		new Label(container, SWT.NONE);

		buttonAddToManager = new Button(container, SWT.CHECK);
		buttonAddToManager.setText("Open Certificate Authority on completion");
		buttonAddToManager.setSelection(model.isAddToManager());
		buttonAddToManager.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

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

		initDataBindings();

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
		 * Filename
		 */
		IObservableValue<?> filenameWidget = WidgetProperties.text(SWT.Modify).observe(textFilename);
		IObservableValue<?> filenameModel = PojoProperties.value("filename").observe(model);
		UpdateValueStrategy s = new UpdateValueStrategy().setAfterGetValidator(new FileValidator());
		Binding b = bindingContext.bindValue(filenameWidget, filenameModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * BaseLocation
		 */
		IObservableValue<?> locationWidget = WidgetProperties.text(SWT.Modify).observe(textBaseLocation);
		IObservableValue<?> locationModel = PojoProperties.value("baseLocation").observe(model);
		s = new UpdateValueStrategy().setAfterGetValidator(new PathValidator());
		b = bindingContext.bindValue(locationWidget, locationModel, s, null);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

		/*
		 * Add to Manager
		 */
		IObservableValue<?> addManagerWidget = WidgetProperties.selection().observe(buttonAddToManager);
		IObservableValue<?> addManagerModel = PojoProperties.value("addToManager").observe(model);
		b = bindingContext.bindValue(addManagerWidget, addManagerModel);

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
