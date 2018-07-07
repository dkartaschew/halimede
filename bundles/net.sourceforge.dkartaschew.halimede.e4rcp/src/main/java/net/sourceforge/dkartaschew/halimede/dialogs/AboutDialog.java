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

package net.sourceforge.dkartaschew.halimede.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
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

public class AboutDialog extends TitleAreaDialog {

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
		setTitle(PluginDefaults.APPLICATION_NAME);
		setTitleImage(PluginDefaults.createImageDescriptor(PluginDefaults.IMG_APPLICATION).createImage());
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
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblECertificateAuthority = new Label(container, SWT.NONE);
		lblECertificateAuthority.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblECertificateAuthority.setAlignment(SWT.CENTER);
		lblECertificateAuthority.setText(PluginDefaults.APPLICATION_FULLNAME);
		FontData[] fD = lblECertificateAuthority.getFont().getFontData();
		fD[0].setHeight(fD[0].getHeight() + 4);
		fD[0].setStyle(SWT.BOLD);
		lblECertificateAuthority.setFont(new Font(getShell().getDisplay(), fD[0]));

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
		lblCopyright.setText("Copyright 2017-2018");

		Label lblDarranKartaschew = new Label(container, SWT.NONE);
		lblDarranKartaschew.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblDarranKartaschew.setText("Darran Kartaschew");

		Link lblHttpscertmamangerdkartaschewgithubio = new Link(container, SWT.NONE);
		lblHttpscertmamangerdkartaschewgithubio.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblHttpscertmamangerdkartaschewgithubio.setText("<A>" + PluginDefaults.APPLICATION_WEBSITE + "</A>");
		lblHttpscertmamangerdkartaschewgithubio.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Program.launch(PluginDefaults.APPLICATION_WEBSITE);
			}

		});
		Label lblProductIsLicensed = new Label(container, SWT.NONE);
		lblProductIsLicensed.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 1, 1));
		lblProductIsLicensed.setText("Product is licensed under the EPL v2.0" + System.lineSeparator() + 
				"w/GPL v2+ w/CE secondary license");

		return area;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.CLOSE_LABEL, true);
	}

}
