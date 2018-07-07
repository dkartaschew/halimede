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

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * Confirmation Dialog that lets the user select an item from a list of objects.
 */
public class QuestionDialogWithOptions extends Dialog {

	private final String title;
	private final String message;
	private final List<Object> elements;
	private Object selectedElement;

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell The parent shell.
	 * @param title The title to display
	 * @param message The message to ask.
	 * @param elements The collection of elements to select from.
	 */
	public QuestionDialogWithOptions(Shell parentShell, String title, String message, List<Object> elements) {
		super(parentShell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL);
		this.title = title;
		this.message = message;
		this.elements = elements;
		this.selectedElement = elements.get(0);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_CERTIFICATE)));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gl_container = new GridLayout(2, false);
		gl_container.horizontalSpacing = 10;
		gl_container.verticalSpacing = 10;
		gl_container.marginTop = 10;
		gl_container.marginRight = 10;
		gl_container.marginLeft = 10;
		container.setLayout(gl_container);
		
		Label lblImage = new Label(container, SWT.NONE);
		lblImage.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false, 1, 2));
		lblImage.setImage(getShell().getDisplay().getSystemImage(SWT.ICON_QUESTION));
		
		Label lblMessage = new Label(container, SWT.WRAP);
		lblMessage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblMessage.setText(message);
		
		ComboViewer comboViewer = new ComboViewer(container, SWT.READ_ONLY);
		Combo comboElements = comboViewer.getCombo();
		comboElements.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		comboViewer.setLabelProvider(new LabelProvider());
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setInput(elements.toArray());
		comboViewer.addSelectionChangedListener(o -> {
			selectedElement = elements.get(comboElements.getSelectionIndex());
		});
		comboElements.select(0);
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Get the selected element.
	 * 
	 * @return The selected element.
	 */
	public Object getSelectedElement() {
		return selectedElement;
	}

}
