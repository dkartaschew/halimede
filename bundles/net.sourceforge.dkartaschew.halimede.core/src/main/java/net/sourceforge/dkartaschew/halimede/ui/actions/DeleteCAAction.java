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

package net.sourceforge.dkartaschew.halimede.ui.actions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class DeleteCAAction extends Action {

	/**
	 * UI Shell
	 */
	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	private Shell shell;
	/**
	 * CA Manager.
	 */
	private final CertificateAuthourityManager manager;
	/**
	 * CA Node
	 */
	private final CertificateAuthorityNode node;

	@Inject
	private Logger logger;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * Create a new action that deletes the CA from the disk.
	 * 
	 * @param manager The CA Manager which owns the CA.
	 * @param node The node to act on
	 */
	public DeleteCAAction(CertificateAuthourityManager manager, CertificateAuthorityNode node) {
		super("Delete Certificate Authority");
		setToolTipText("Delete this Certificate Authority");
		this.manager = manager;
		this.node = node;
	}

	@Override
	public void run() {
		if (MessageDialog.openConfirm(shell, "Delete Certificate Authority",
				"Are you sure you wish to completely remove this Certificate Authority from your system?"
						+ System.lineSeparator() + "This action will delete the Certificate Authority from the filesystem!")) {
			if (logger != null) {
				logger.warn("User selected to delete the CA at location: "
						+ node.getCertificateAuthority().getBasePath().toString());
			}
			Path path = node.getCertificateAuthority().getBasePath();
			// Create the CA as a Job.
			Job job = Job.create("Deleting Certificate Authority - " + path, monitor -> {
				try {
					SubMonitor subMonitor = SubMonitor.convert(monitor, "Deleting Certificate Authority - " + path, 1);
					if (logger != null) {
						logger.info("Deleting:");
						Files.walk(path)//
								.sorted(Comparator.reverseOrder())//
								.map(Path::toFile)//
								.peek(o1 -> logger.info(o1.getName())) //
								.forEach(File::delete);
					} else {
						Files.walk(path)//
								.sorted(Comparator.reverseOrder())//
								.map(Path::toFile)//
								.forEach(File::delete);
					}
					subMonitor.worked(1);
				} catch (Throwable e) {
					if (logger != null)
						logger.error(e, "Deleting the CA Failed");
					sync.asyncExec(() -> {
						MessageDialog.openError(shell, "Deleting the CA Failed",
								"Deleting the CA failed with the following error: " + ExceptionUtil.getMessage(e));
					});
				} finally {
					manager.remove(node.getCertificateAuthority());
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
