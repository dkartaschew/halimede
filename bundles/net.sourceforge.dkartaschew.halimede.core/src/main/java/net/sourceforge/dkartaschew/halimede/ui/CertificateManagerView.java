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

package net.sourceforge.dkartaschew.halimede.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import net.sourceforge.dkartaschew.halimede.PluginDefaults;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateAuthourityManager;
import net.sourceforge.dkartaschew.halimede.ui.composite.CADetailPane;
import net.sourceforge.dkartaschew.halimede.ui.composite.CAListPane;
import net.sourceforge.dkartaschew.halimede.ui.node.CertificateAuthorityNode;

@SuppressWarnings("restriction")
public class CertificateManagerView implements PropertyChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "net.sourceforge.dkartaschew.halimede.view.primary";

	/**
	 * Primary Label for the Part
	 */
	public static final String LABEL = "Halimede CA";

	/**
	 * Description of the Part
	 */
	public static final String DESCRIPTION = "Halimede Certificate Authority Manager";

	@Inject
	private Logger logger;

	@Inject
	private IEclipseContext context;
	
	@Inject 
	private UISynchronize sync;

	/**
	 * The Certificate Authority manager.
	 */
	private CertificateAuthourityManager manager = new CertificateAuthourityManager();

	/**
	 * The CA Listing Pane (The left side).
	 */
	private CAListPane caList;

	/**
	 * The Details pane (the right side).
	 */
	private CADetailPane caDetails;

	/**
	 * The part stack we add to.
	 */
	private String partStack;

	/**
	 * Create the Part
	 * 
	 * @param part The part which this is part of.
	 * @param parent The parent composite
	 */
	@PostConstruct
	public void createControls(Composite parent, MPart part) {
		// Set the part stack ID
		if (part != null) {
			partStack = part.getPersistedState().get(PluginDefaults.COMMAND_PARAM);
		}
		if (partStack == null || partStack.isEmpty()) {
			partStack = PluginDefaults.EDITOR;
		}

		// Create the UI
		// parent.setLayout(new GridLayout(1, false));
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		parent.setLayout(gridLayout);

		SashForm sashForm = new SashForm(parent, SWT.DOUBLE_BUFFERED);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		caList = new CAListPane(sashForm, this);
		ContextInjectionFactory.inject(caList, context);

		caDetails = new CADetailPane(sashForm, SWT.DOUBLE_BUFFERED, manager, this);
		ContextInjectionFactory.inject(caDetails, context);

		caList.addSelectionListener(caDetails);
		caList.expandAll();

		sashForm.setWeights(new int[] { 1, 4 });
		/*
		 * Load all the active CAs as a separate job.
		 */
		Job job = Job.create("Load Certificate Authorities", monitor -> {

			try {
				// Get list of folders.
				IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(PluginDefaults.ID);
				if (preferences.nodeExists(PluginDefaults.PREFERENCES_NODE_PATHS)) {
					Preferences paths = preferences.node(PluginDefaults.PREFERENCES_NODE_PATHS);
					String[] locations = paths.get(PluginDefaults.PREFERENCES_NODE_PATHS, "").split(File.pathSeparator);
					if (locations.length > 0) {
						SubMonitor subMonitor = SubMonitor.convert(monitor, "Load Certificate Authorities", locations.length);
						for (String location : locations) {
							if (subMonitor.isCanceled()) {
								return Status.CANCEL_STATUS;
							}
							subMonitor.newChild(1);
							try {
								manager.open(Paths.get(location));
							} catch (Throwable e) {
								logger.error(e, "Failed to load CA.");
							}
						}
					}
				}
				return Status.OK_STATUS;
			} catch (BackingStoreException e) {
				logger.error(e, "Restoring Active CAs failed.");
				return Status.OK_STATUS;
			} finally {
				if (monitor != null) {
					monitor.done();
				}
				manager.addPropertyChangeListener(this);
				/*
				 * Ensure the CA Details panes get updates as well to ensure the tables are updated based on actions of
				 * the CA
				 */
				manager.addPropertyChangeListener(caDetails);

				CertificateAuthorityNode[] nodes = manager.getCertificateAuthorities().stream()
						.map(i -> new CertificateAuthorityNode(i))//
						.toArray(CertificateAuthorityNode[]::new);
				sync.asyncExec(() -> {
					caList.setInput(nodes);
				});
			}
		});

		job.schedule();

	}

	@Focus
	public void setFocus() {
		caList.setFocus();
	}

	/**
	 * Get the context of the view.
	 * 
	 * @return The current context.
	 */
	public IEclipseContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getSource() == manager) {
			/*
			 * Get the new values, and populate the CA List
			 */
			@SuppressWarnings("unchecked")
			Collection<CertificateAuthority> cas = (Collection<CertificateAuthority>) event.getNewValue();
			CertificateAuthorityNode[] nodes = cas.stream().map(i -> new CertificateAuthorityNode(i))//
					.toArray(CertificateAuthorityNode[]::new);
			// We need to set the input in the UI thread...
			sync.asyncExec(() -> {
				caList.setInput(nodes);
			});

			/*
			 * And store the base paths of all the nodes in the preference store.
			 */
			try {
				IEclipsePreferences preferences = InstanceScope.INSTANCE.getNode(PluginDefaults.ID);
				Preferences paths = preferences.node(PluginDefaults.PREFERENCES_NODE_PATHS);
				String locations = cas.stream()//
						.map(i -> i.getBasePath().toFile().getAbsolutePath())//
						.collect(Collectors.joining(File.pathSeparator));
				paths.put(PluginDefaults.PREFERENCES_NODE_PATHS, locations);
				preferences.flush();
			} catch (Throwable e) {
				logger.error(e, "Saving Active CAs failed.");
			}
		} else {
			// CA update...
			sync.asyncExec(() -> {
				caList.refresh();
			});
		}
	}

	/**
	 * Get the root CA Manager.
	 * 
	 * @return The root CA Manager.
	 */
	public CertificateAuthourityManager getCAManager() {
		return manager;
	}

	/**
	 * Get the editor part stack we add our children to.
	 * 
	 * @return The part stack ID.
	 */
	public String getPartStack() {
		return partStack;
	}

	/**
	 * Lock all CA's that are present.
	 */
	@PreDestroy
	public void lockAllCAs() {
		if (manager != null) {
			manager.getCertificateAuthorities().stream().forEach(CertificateAuthority::lock);
		}
	}
}
