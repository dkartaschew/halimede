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

package net.sourceforge.dkartaschew.halimede.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.layout.GridData;

public class JobStatusComposite extends Composite {

	private final Label lblStatus;
	private final ProgressBar progressBar;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public JobStatusComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));

		lblStatus = new Label(this, SWT.NONE);
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblStatus.setText("New Label");

		Label label = new Label(this, SWT.SEPARATOR | SWT.VERTICAL);
		GridData gd_label = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_label.heightHint = 16;
		label.setLayoutData(gd_label);

		progressBar = new ProgressBar(this, SWT.SMOOTH);
		GridData gd_progressBar = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_progressBar.widthHint = 120;
		progressBar.setLayoutData(gd_progressBar);
	}

	/**
	 * Get the status label
	 * 
	 * @return The statius label
	 */
	public Label getLblStatus() {
		return lblStatus;
	}

	/**
	 * Get the progress bar
	 * @return The progress bar.
	 */
	public ProgressBar getProgressBar() {
		return progressBar;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
