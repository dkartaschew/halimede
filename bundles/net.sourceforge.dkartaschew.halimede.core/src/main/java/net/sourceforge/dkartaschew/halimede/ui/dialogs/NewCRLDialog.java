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

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

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
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.internal.databinding.swt.SWTObservableValueDecorator;
import org.eclipse.nebula.jface.cdatetime.CDateTimeObservableValue;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
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
import net.sourceforge.dkartaschew.halimede.ui.model.CACRLModel;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

@SuppressWarnings("restriction")
public class NewCRLDialog extends Dialog {

	/**
	 * The next update date.
	 */
	private CDateTime CANextUpdate;

	/**
	 * The CRL model.
	 */
	private final CACRLModel model;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public NewCRLDialog(Shell parentShell, CACRLModel model) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.model = model;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Create CRL");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		container.setLayout(layout);

		Label lblCertificateAuthority = new Label(container, SWT.NONE);
		lblCertificateAuthority.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCertificateAuthority.setText("Certificate Authority:");

		Text CADexecription = new Text(container, SWT.BORDER);
		CADexecription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		CADexecription.setText(model.getCa());
		CADexecription.setEditable(false);

		Label lblCrlNuserial = new Label(container, SWT.NONE);
		lblCrlNuserial.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCrlNuserial.setText("CRL Serial:");

		Text CASerial = new Text(container, SWT.BORDER);
		CASerial.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		CASerial.setText(model.getSerial().toString());
		CASerial.setEditable(false);

		Label lblCrlIssuedDate = new Label(container, SWT.NONE);
		lblCrlIssuedDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCrlIssuedDate.setText("CRL Issued Date:");

		Text CAIssueDate = new Text(container, SWT.BORDER);
		CAIssueDate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		CAIssueDate.setText(DateTimeUtil.toString(model.getIssueDate()));
		CAIssueDate.setEditable(false);

		Label lblNextCrlDate = new Label(container, SWT.NONE);
		lblNextCrlDate.setText("Next CRL Date:");
		lblNextCrlDate.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		CANextUpdate = new CDateTime(container, CDT.BORDER | CDT.TAB_FIELDS);
		CANextUpdate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		CANextUpdate.setPattern(DateTimeUtil.DEFAULT_FORMAT);
		CANextUpdate.setTimeZone(TimeZone.getTimeZone(DateTimeUtil.DEFAULT_ZONE));
		if (model.getNextDate() != null) {
			CANextUpdate.setSelection(DateTimeUtil.toDate(model.getNextDate()));
		} else {
			CANextUpdate.setSelection(new Date());
		}
		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Create CRL", true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		initDataBindings();
	}

	/**
	 * Create the data bindings for controls to the model
	 * 
	 * @return The databinding holder.
	 */
	protected DataBindingContext initDataBindings() {

		DataBindingContext bindingContext = new DataBindingContext();

		/*
		 * Next Update
		 */
		@SuppressWarnings("unchecked")
		IObservableValue<Date> nextDateWidget = new SWTObservableValueDecorator<Date>(
				new CDateTimeObservableValue(CANextUpdate), CANextUpdate);
		IObservableValue<ZonedDateTime> nextDateModel = PojoProperties.value("nextDate", ZonedDateTime.class)
				.observe(model);
		UpdateValueStrategy<Date, ZonedDateTime> s = new UpdateValueStrategy<Date, ZonedDateTime>().setConverter(
				IConverter.create(Date.class, ZonedDateTime.class, (date) -> DateTimeUtil.toZonedDateTime((Date) date)))
				.setBeforeSetValidator(value -> {
					ZonedDateTime o = (ZonedDateTime) value;
					if (o.isBefore(model.getIssueDate())) {
						return ValidationStatus.error("Next Expected Update cannot be before Issue Date");
					}
					return ValidationStatus.ok();
				});
		UpdateValueStrategy<ZonedDateTime, Date> s2 = new UpdateValueStrategy<ZonedDateTime, Date>()
				.setConverter(IConverter.create(ZonedDateTime.class, Date.class, (date) -> {
					return date == null ? null : Date.from(((ZonedDateTime) date).toInstant());
				}));
		Binding b = bindingContext.bindValue(nextDateWidget, nextDateModel, s, s2);
		ControlDecorationSupport.create(b, SWT.TOP | SWT.LEFT);

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
