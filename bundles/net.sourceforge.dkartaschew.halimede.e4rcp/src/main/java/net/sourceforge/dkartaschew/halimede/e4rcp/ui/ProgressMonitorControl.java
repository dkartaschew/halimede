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
/*-
 * Based on:
 * Eclipse Jobs and Background Processing - Tutorial
 * Lars Vogel, Simon Scholz (c) 2009, 2016 vogella GmbH
 * Version 3.6, 06.07.2016
 * http://www.vogella.com/tutorials/EclipseJobs/article.html
 * License: EPL
 */
package net.sourceforge.dkartaschew.halimede.e4rcp.ui;

import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.ProgressProvider;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class ProgressMonitorControl {

	/**
	 * Progress provider.
	 */
	private static class HalimedeProgressProvider extends ProgressProvider {
		/**
		 * Progress monitor
		 */
		private final GobalProgressMonitor monitor;

		/**
		 * Create a progress provider tied to the given monitor
		 * 
		 * @param monitor The monitor to tie to
		 */
		private HalimedeProgressProvider(GobalProgressMonitor monitor) {
			Objects.requireNonNull(monitor, "Monitor is null");
			this.monitor = monitor;
		}

		@Override
		public IProgressMonitor createMonitor(Job job) {
			return monitor.addJob(job);
		}
	}

	@Inject
	private IEclipseContext context;

	/**
	 * Progress bar composite.
	 */
	private JobStatusComposite progressBar;
	/**
	 * Progress monitor
	 */
	private GobalProgressMonitor monitor;

	@PostConstruct
	public void createControls(Composite parent) {
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);

		progressBar = new JobStatusComposite(parent, SWT.BORDER);
		progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		progressBar.getLblStatus().setText("Ready...");
		monitor = new GobalProgressMonitor(progressBar);
		ContextInjectionFactory.inject(monitor, context);

		Job.getJobManager().setProgressProvider(new HalimedeProgressProvider(monitor));
	}
}
