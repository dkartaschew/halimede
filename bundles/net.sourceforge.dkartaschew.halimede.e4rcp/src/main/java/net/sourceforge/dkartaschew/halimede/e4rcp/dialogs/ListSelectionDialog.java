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

/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 * 	   Sebastian Davids <sdavids@gmx.de> - Fix for bug 90273 - [Dialogs]
 * 			ListSelectionDialog dialog alignment
 *******************************************************************************/
package net.sourceforge.dkartaschew.halimede.e4rcp.dialogs;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * A standard dialog which solicits a list of selections from the user. This
 * class is configured with an arbitrary data model represented by content and
 * label provider objects. The <code>getResult</code> method returns the
 * selected elements.
 * <p>
 * This class is derived from the eclipse {@link org.eclipse.ui.dialogsListSelectionDialog}
 */
public class ListSelectionDialog extends SelectionDialog {
    // the root element to populate the viewer with
    private Object inputElement;

    // providers for populating this dialog
    private ILabelProvider labelProvider;

    private IStructuredContentProvider contentProvider;

    // the visual selection widget group
    private CheckboxTableViewer listViewer;

    // sizing constants
    private static final int SIZING_SELECTION_WIDGET_HEIGHT = 150;

    private static final int SIZING_SELECTION_WIDGET_WIDTH = 450;

    /**
     * Creates a list selection dialog.
     *
     * @param parentShell the parent shell
     * @param input	the root element to populate this dialog with
     * @param contentProvider the content provider for navigating the model
     * @param labelProvider the label provider for displaying model elements
     * @param title The dialog title
     * @param message the message to be displayed at the top of this dialog
     */
    public ListSelectionDialog(Shell parentShell, Object input,
            IStructuredContentProvider contentProvider,
            ILabelProvider labelProvider, String title, String message) {
        super(parentShell);
        setTitle(title);
        inputElement = input;
        this.contentProvider = contentProvider;
        this.labelProvider = labelProvider;
		setMessage(message);
    }

    /**
     * Add the selection and deselection buttons to the dialog.
     * @param composite org.eclipse.swt.widgets.Composite
     */
    private void addSelectionButtons(Composite composite) {
        Composite buttonComposite = new Composite(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        buttonComposite.setLayout(layout);
        buttonComposite.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));

        Button selectButton = createButton(buttonComposite,
                IDialogConstants.SELECT_ALL_ID, "Select All", false);

        selectButton.addListener(SWT.Selection, e -> listViewer.setAllChecked(true));

        Button deselectButton = createButton(buttonComposite,
                IDialogConstants.DESELECT_ALL_ID, "Unselect All", false);

        deselectButton.addListener(SWT.Selection, e -> listViewer.setAllChecked(false));
    }

    /**
     * Visually checks the previously-specified elements in this dialog's list
     * viewer.
     */
    @SuppressWarnings("unchecked")
	private void checkInitialSelections() {
    	getInitialElementSelections().forEach(i -> listViewer.setChecked(i, true));
    }

    @Override
	protected void configureShell(Shell shell) {
        super.configureShell(shell);
    }

    @Override
	protected Control createDialogArea(Composite parent) {
        // page group
        Composite composite = (Composite) super.createDialogArea(parent);

        initializeDialogUnits(composite);

        createMessageArea(composite);

        listViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
        data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
        listViewer.getTable().setLayoutData(data);

        listViewer.setLabelProvider(labelProvider);
        listViewer.setContentProvider(contentProvider);

        addSelectionButtons(composite);

        initializeViewer();

        // initialize page
        if (!getInitialElementSelections().isEmpty()) {
			checkInitialSelections();
		}

        Dialog.applyDialogFont(composite);

        return composite;
    }

    /**
     * Returns the viewer used to show the list.
     *
     * @return the viewer, or <code>null</code> if not yet created
     */
    protected CheckboxTableViewer getViewer() {
        return listViewer;
    }

    /**
     * Initializes this dialog's viewer after it has been laid out.
     */
    private void initializeViewer() {
        listViewer.setInput(inputElement);
    }

    /**
     * The <code>ListSelectionDialog</code> implementation of this
     * <code>Dialog</code> method builds a list of the selected elements for later
     * retrieval by the client and closes this dialog.
     */
    @Override
	protected void okPressed() {

        // Get the input children.
        Object[] children = contentProvider.getElements(inputElement);

        // Build a list of selected children.
        if (children != null) {
            setResult(Arrays.stream(children)//
            		.filter(e -> listViewer.getChecked(e))//
            		.collect(Collectors.toList()));
        }

        super.okPressed();
    }
}
