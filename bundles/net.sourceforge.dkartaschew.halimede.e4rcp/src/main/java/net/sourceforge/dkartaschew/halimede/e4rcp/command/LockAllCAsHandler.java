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
package net.sourceforge.dkartaschew.halimede.e4rcp.command;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.log.Logger;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.util.ExceptionUtil;

@SuppressWarnings("restriction")
public class LockAllCAsHandler {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.handler.lockall";

	@Inject
	private Logger logger;

	@Inject
	private CertificateAuthourityManager manager;

	@Execute
	public void execute() {
		Job job = Job.create("Locking all Certificate Authorities", monitor -> {
			List<CertificateAuthority> cas = new ArrayList<>(manager.getCertificateAuthorities());
			cas.forEach(ca -> {
				try {
					ca.lock();
				} catch (Throwable e) {
					logger.error(e, ExceptionUtil.getMessage(e));
				}
			});
			if (monitor != null) {
				monitor.done();
			}
			return Status.OK_STATUS;
		});
		job.schedule();
	}

}
