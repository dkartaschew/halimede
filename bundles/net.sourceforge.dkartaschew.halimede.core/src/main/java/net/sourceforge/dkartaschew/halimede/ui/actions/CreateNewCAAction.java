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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;

import javax.inject.Inject;
import javax.inject.Named;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.GeneralName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.data.CertificateFactory;
import net.sourceforge.dkartaschew.halimede.data.KeyPairFactory;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.GeneralNameTag;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.ui.dialogs.NewCADialog;
import net.sourceforge.dkartaschew.halimede.ui.model.NewCAModel;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class CreateNewCAAction extends Action implements Runnable {

	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	/**
	 * CA Manager
	 */
	private final CertificateAuthourityManager manager;

	@Inject
	private Logger logger;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * Create an Action for creating a new CA
	 * 
	 * @param manager The root CA Manager.
	 */
	public CreateNewCAAction(CertificateAuthourityManager manager) {
		super("Create a New Certificate Authority");
		setToolTipText("Create a new Certificate Authority");
		this.manager = manager;
	}

	@Override
	public void run() {
		NewCAModel model = new NewCAModel();
		ZonedDateTime startDate = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
		ZonedDateTime expiryDate = startDate.plusYears(1);

		model.setStartDate(startDate);
		model.setExpiryDate(expiryDate);

		NewCADialog dialog = new NewCADialog(shell, model);
		if (dialog.open() == IDialogConstants.OK_ID) {
			// Create the CA as a Job.
			Job job = Job.create("Create Certificate Authority - " + model.getcADescription(), monitor -> {

				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Create Certificate Authority - " + model.getcADescription(), 5);
					KeyPair key = KeyPairFactory.generateKeyPair(model.getKeyType());
					subMonitor.worked(1);
					GeneralName crlLocation = null;
					if (model.getcRLLocation() != null && !model.getcRLLocation().isEmpty()) {
						crlLocation = GeneralNameTag.uniformResourceIdentifier.asGeneralName(model.getcRLLocation());
					}
					X509Certificate cert = CertificateFactory.generateSelfSignedCertificate(
							new X500Name(model.getX500Name()), //
							model.getStartDate(), model.getExpiryDate(), key,
							SignatureAlgorithm.getDefaultSignature(key.getPublic()), true, crlLocation);
					subMonitor.worked(1);
					IssuedCertificate ic = new IssuedCertificate(key, new X509Certificate[] { cert }, null, null,
							model.getPassword());
					subMonitor.worked(1);
					Path path = Paths.get(model.getBaseLocation(), model.getcADescription());
					Files.createDirectories(path);
					subMonitor.worked(1);
					manager.create(path, ic, model.getcADescription());
					subMonitor.worked(1);
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, "Creating the CA Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Creating the CA Failed",
								"Creating the CA failed with the following error: " + ExceptionUtil.getMessage(e));
					});
				}
				if(monitor != null) {
					monitor.done();
				}
				return Status.OK_STATUS;

			});

			job.schedule();
		}
	}
}
