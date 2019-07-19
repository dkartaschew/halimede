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

package net.sourceforge.dkartaschew.halimede.e4rcp.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;

public class AboutDialog extends Dialog {

	/**
	 * Create the dialog.
	 * 
	 * @param parentShell The parent shell.
	 */
	public AboutDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	public void create() {
		super.create();
		//setTitle(PluginDefaults.APPLICATION_NAME);
		//setTitleImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_APPLICATION).createImage());
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("About");
		shell.setImage(PluginDefaults.getResourceManager()
				.createImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_APPLICATION)));
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent The parent composite
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		GridLayout gridLayout = (GridLayout) area.getLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		Composite container = new Composite(area, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		container.setLayout(layout);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label lblSplashIcon = new Label(container, SWT.BORDER);
		lblSplashIcon.setAlignment(SWT.CENTER);
		lblSplashIcon.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		lblSplashIcon.setImage(PluginDefaults.getResourceManager().createImage(
				PluginDefaults.createImageDescriptor(FrameworkUtil.getBundle(getClass()), "icons/splash.png")));

		Label lblApplicationName = new Label(container, SWT.NONE);
		GridData gd_lblApplicationName = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_lblApplicationName.verticalIndent = 8;
		lblApplicationName.setLayoutData(gd_lblApplicationName);
		lblApplicationName.setAlignment(SWT.CENTER);
		lblApplicationName.setText(PluginDefaults.APPLICATION_FULLNAME);
		FontData[] fD = lblApplicationName.getFont().getFontData();
		FontDescriptor descriptor = FontDescriptor.createFrom(lblApplicationName.getFont())//
				.setStyle(SWT.BOLD)//
				.setHeight(fD[0].getHeight() + 4);
		lblApplicationName.setFont(PluginDefaults.getResourceManager().createFont(descriptor));

		Label lblVersion = new Label(container, SWT.NONE);
		lblVersion.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblVersion.setAlignment(SWT.CENTER);
		lblVersion.setText("Version SNAPSHOT");
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		if (bundle != null) {
			Version version = bundle.getVersion();
			lblVersion.setText("Version: " + version.toString());
		}

		Label lblCopyright = new Label(container, SWT.NONE);
		lblCopyright.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblCopyright.setText("Copyright 2017-2019");

		Label lblAuthor = new Label(container, SWT.NONE);
		lblAuthor.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblAuthor.setText("Darran Kartaschew");

		Link lblSite = new Link(container, SWT.NONE);
		lblSite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblSite.setText("<A>" + PluginDefaults.APPLICATION_WEBSITE + "</A>");
		lblSite.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(PluginDefaults.APPLICATION_WEBSITE);
			}

		});
		Label lblLicense = new Label(container, SWT.NONE);
		lblLicense.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblLicense.setText("Product is licensed under the EPL v2.0" + System.lineSeparator() + 
				"w/GPL v2+ w/CE secondary license");
		lblLicense.setAlignment(SWT.CENTER);

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent The parent composite.
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

}
