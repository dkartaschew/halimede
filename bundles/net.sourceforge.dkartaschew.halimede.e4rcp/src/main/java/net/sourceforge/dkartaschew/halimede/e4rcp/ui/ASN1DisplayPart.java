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

package net.sourceforge.dkartaschew.halimede.e4rcp.ui;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.ui.composite.CompositeOutputRenderer;
import net.sourceforge.dkartaschew.halimede.util.ASN1Decoder;

@SuppressWarnings("restriction")
public class ASN1DisplayPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.asn1";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "ASN.1 Details";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority";

	/**
	 * The decoder
	 */
	public static final String ASN1DECODER = "net.sourceforge.dkartaschew.halimede.data.asn1decoder";
	/**
	 * The editor part stack ID.
	 */
	public static final String PARTSTACK_EDITOR = "net.sourceforge.dkartaschew.halimede.data.editor";

	/**
	 * Reference to the containing part.
	 */
	private MPart part;
	/**
	 * The ASN1 Decoder;
	 */
	private ASN1Decoder decoder;
	/**
	 * Primary composite
	 */
	private CompositeOutputRenderer composite;

	@Inject
	private Logger logger;

	@Inject
	private EPartService partService;

	@Inject
	private UISynchronize sync;

	/**
	 * UI Shell
	 */
	@Inject
	@Optional
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;

	/**
	 * Create contents of the view part.
	 * 
	 * @param part The part which this is part of.
	 * @param parent The parent composite
	 */
	@PostConstruct
	public void createControls(MPart part, Composite parent) {
		this.part = part;
		if (shell == null) {
			logger.info("ASN.1 Decoder missing required information. Closing");
			close();
			return;
		}
		if (this.part != null) {
			this.decoder = (ASN1Decoder) this.part.getTransientData().get(ASN1DECODER);
			if (decoder == null) {
				logger.info("Missing ASN.1 Decoder, closing.");
				close();
				return;
			}
		}

		// Now construct the controls.
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 0;
		parent.setLayout(gridLayout);

		this.composite = new CompositeOutputRenderer(parent, SWT.NONE, decoder.getFile().toString());
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		decoder.render(composite);
		composite.finaliseRender();
		composite.redraw();
	}

	@PreDestroy
	public void dispose() {
		// Ensure we set to not be dirty...
		part.setDirty(false);
	}

	@Focus
	public void setFocus() {
		sync.asyncExec(() -> {
			if (this.composite != null && !this.composite.isDisposed()) {
				this.composite.setFocus();
			}
		});
	}

	/**
	 * Close the part.
	 * <p>
	 * Note: This an async request.
	 */
	public void close() {
		sync.asyncExec(() -> {
			// This will call @PreDestroy
			partService.hidePart(part, true);
		});
	}

}
