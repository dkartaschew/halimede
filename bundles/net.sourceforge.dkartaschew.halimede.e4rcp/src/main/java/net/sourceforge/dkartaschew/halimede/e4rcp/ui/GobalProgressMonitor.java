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
/*-
 * Based on:
 * Eclipse Jobs and Background Processing - Tutorial
 * Lars Vogel, Simon Scholz (c) 2009, 2016 vogella GmbH
 * Version 3.6, 06.07.2016
 * http://www.vogella.com/tutorials/EclipseJobs/article.html
 * License: EPL
 */
package net.sourceforge.dkartaschew.halimede.e4rcp.ui;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.e4.ui.di.UISynchronize;

class GobalProgressMonitor extends NullProgressMonitor {

	@Inject
	UISynchronize sync;

	/**
	 * Composite to update for job status
	 */
	final JobStatusComposite progressBar;

	/**
	 * Create a progress monitor
	 * 
	 * @param progressBar The progress bar to update as required.
	 */
	GobalProgressMonitor(JobStatusComposite progressBar) {
		this.progressBar = progressBar;
	}

	// thread-Safe via thread confinement of the UI-Thread
	// (means access only via UI-Thread)
	long runningTasks = 0L;

	@Override
	public void beginTask(final String name, final int totalWork) {
		this.sync.syncExec(() -> {
			if (runningTasks <= 0) {
				// --- no task is running at the moment ---
				this.progressBar.getProgressBar().setSelection(0);
				this.progressBar.getProgressBar().setMaximum(totalWork);
				this.progressBar.getLblStatus().setText(name != null && !name.isEmpty() ? name : "Task");
			} else {
				// --- other tasks are running ---
				this.progressBar.getProgressBar().setMaximum(this.progressBar.getProgressBar().getMaximum() + totalWork);
			}

			runningTasks++;
			this.progressBar.getProgressBar().setToolTipText("Currently running " + runningTasks + "\nLast task: " + name);
			this.progressBar.getLblStatus().setText("Currently running " + runningTasks + " Tasks");

		});
	}

	@Override
	public void setTaskName(String name) {
		this.sync.syncExec(() -> {
			this.progressBar.getLblStatus().setText(name);
		});
	}

	@Override
	public void worked(final int work) {
		this.sync.syncExec(() -> {
			this.progressBar.getProgressBar().setSelection(this.progressBar.getProgressBar().getSelection() + work);
		});
	}

	public IProgressMonitor addJob(Job job) {
		if (job != null) {
			job.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					sync.syncExec(() -> {
						if (runningTasks > 0)
							runningTasks--;
						if (runningTasks > 0) {
							// --- some tasks are still running ---
							if (!progressBar.isDisposed()) {
								progressBar.setToolTipText("Currently running " + runningTasks + " Tasks");
								progressBar.getLblStatus().setText("Currently running " + runningTasks + " Tasks");
							}
						} else {
							// --- all tasks are done (a reset of selection could also be done) ---
							if (!progressBar.isDisposed()) {
								progressBar.getProgressBar().setToolTipText("No background progress running.");
								progressBar.getProgressBar().setSelection(0);
								progressBar.getLblStatus().setText("Ready...");
							}
						}
					});
					// clean-up
					event.getJob().removeJobChangeListener(this);
				}
			});
		}
		return this;
	}
}