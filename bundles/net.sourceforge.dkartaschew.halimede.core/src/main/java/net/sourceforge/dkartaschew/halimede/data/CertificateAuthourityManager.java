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

package net.sourceforge.dkartaschew.halimede.data;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Path;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Manager class for handling Certificate Authourities.
 *
 */
public class CertificateAuthourityManager implements PropertyChangeListener {

	/*
	 * Setup BC crypto provider.
	 */
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	/**
	 * The default emitted property.
	 */
	public static final String PROPERTY = "certificateAuthorities";

	/**
	 * Collection of Certificate Authorities.
	 */
	public Set<CertificateAuthority> certificateAuthorities = new HashSet<>();

	/**
	 * Property Support helper.
	 */
	private final PropertyChangeSupport propertySupport;

	/**
	 * Create a new Certificate Authority Manager
	 */
	public CertificateAuthourityManager() {
		propertySupport = new PropertyChangeSupport(this);
	}

	/**
	 * Get the collection of all Certificate Authorities.
	 * 
	 * @return A Collection of all Certificate Authorities.
	 */
	public Collection<CertificateAuthority> getCertificateAuthorities() {
		return certificateAuthorities;
	}

	/**
	 * Open an exiting instance of CA data store.
	 * 
	 * @param path The filepath of the CAs datastore.
	 * @return The Certificate Authority instance.
	 * @throws IOException If opening/reading the datastore failed.
	 */
	public CertificateAuthority open(Path path) throws IOException {
		CertificateAuthority ca = null;
		try {
			ca = CertificateAuthority.open(path);
		} catch (CertificateEncodingException e) {
			// Should NEVER happen.
		}
		if (ca != null) {
			ca.addPropertyChangeListener(this);
			Set<CertificateAuthority> old = new HashSet<>(certificateAuthorities);
			certificateAuthorities.add(ca);
			this.propertySupport.firePropertyChange(PROPERTY, old, certificateAuthorities);
		}
		return ca;
	};

	/**
	 * Open an exiting instance of CA data store.
	 * 
	 * @param path The filepath of the CAs datastore.
	 * @param certificate The issuers certificate information.
	 * @param description The CA Description
	 * @return The Certificate Authority instance.
	 * @throws IOException If opening/reading the datastore failed.
	 * @throws CertificateEncodingException The supplied certificate information is not valid.
	 */
	public CertificateAuthority create(Path path, IIssuedCertificate certificate, String description)
			throws IOException, CertificateEncodingException {
		CertificateAuthority ca = CertificateAuthority.create(path, certificate, description);
		ca.addPropertyChangeListener(this);
		Set<CertificateAuthority> old = new HashSet<>(certificateAuthorities);
		certificateAuthorities.add(ca);
		this.propertySupport.firePropertyChange(PROPERTY, old, certificateAuthorities);
		return ca;
	};

	/**
	 * Remove the CA from the list of authorities.
	 * 
	 * @param ca The CA to Remove.
	 * @return TRUE if the element was removed.
	 */
	public boolean remove(CertificateAuthority ca) {
		Set<CertificateAuthority> old = new HashSet<>(certificateAuthorities);
		ca.removePropertyChangeListener(this);
		boolean res = certificateAuthorities.remove(ca);
		this.propertySupport.firePropertyChange(PROPERTY, old, certificateAuthorities);
		return res;
	};

	/**
	 * Add a property change listener
	 * 
	 * @param listener The listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a property change listener
	 * 
	 * @param listener The listener to remove.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertySupport.removePropertyChangeListener(listener);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		this.propertySupport.firePropertyChange(evt);
	}
}
