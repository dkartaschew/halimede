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

package net.sourceforge.dkartaschew.halimede.ui.model;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;

import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;

import net.sourceforge.dkartaschew.halimede.data.CertificateAuthority;
import net.sourceforge.dkartaschew.halimede.data.CertificateRequestProperties;
import net.sourceforge.dkartaschew.halimede.data.ICertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateKeyPairTemplate;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;

/**
 * Model for new Certificates/Templates, etc. This model extends the Certificate Request to provide the additional
 * required fields.
 */
public class NewCertificateModel extends CertificateRequest {

	/**
	 * Certificate Authority to sign.
	 */
	private final CertificateAuthority ca;
	/**
	 * Certificate Start Date
	 */
	private ZonedDateTime startDate;
	/**
	 * Certificate Expiry Date
	 */
	private ZonedDateTime expiryDate;
	/**
	 * Store Password
	 */
	private String password;
	/**
	 * Use the CA password for the issued PKCS12 container.
	 */
	private boolean useCAPassword;

	/**
	 * Date which the start date cannot be before.
	 */
	private ZonedDateTime notBefore;
	/**
	 * Date which the expiry date cannot be after.
	 */
	private ZonedDateTime notAfter;
	/**
	 * This model (if true) represents a template only.
	 */
	private boolean representsTemplateOnly;
	/**
	 * The CSR if derived from a CSR.
	 */
	private CertificateRequestProperties csr;

	/**
	 * Create a new model for new certificates/templates.
	 * 
	 * @param ca The issuing CA.
	 */
	public NewCertificateModel(CertificateAuthority ca) {
		super();
		this.ca = ca;
		// set defaults;
		useCAPassword = true;
		representsTemplateOnly = false;
	}

	/**
	 * Create a new model for new certificates/templates.
	 * 
	 * @param ca The issuing CA.
	 * @param template Certificate Request model.
	 */
	public NewCertificateModel(CertificateAuthority ca, ICertificateKeyPairTemplate template) {
		super();
		this.ca = ca;
		// set defaults;
		useCAPassword = true;
		representsTemplateOnly = false;
		/*
		 * Set fields from template
		 */
		if (template instanceof CertificateKeyPairTemplate) {
			CertificateKeyPairTemplate t = (CertificateKeyPairTemplate) template;
			setcARequest(t.isCARequest());
			setCertificatePolicies(t.getCertificatePolicies());
			setDescription(t.getDescription());
			setSubject(t.getSubject());
			setKeyType(t.getKeyType());
			setKeyUsage(t.getKeyUsage());
			setExtendedKeyUsage(t.getExtendedKeyUsageVector());
			setSubjectAlternativeName(t.getSubjectAltNames());
			setCreationDate(t.getCreationDate());
			setCrlIssuer(t.getCrlIssuer());
			setCrlLocation(t.getCrlLocation());

		} else if (template instanceof CertificateRequest) {
			CertificateRequest t = (CertificateRequest) template;
			setcARequest(t.isCARequest());
			setCertificatePolicies(t.getCertificatePolicies());
			setDescription(t.getDescription());
			setSubject(t.getSubject());
			setKeyType(t.getKeyType());
			setKeyUsage(t.getKeyUsage());
			setExtendedKeyUsage(t.getExtendedKeyUsageVector());
			// Safe cast...
			setSubjectAlternativeName((GeneralNames) t.getSubjectAlternativeName());
			setCrlIssuer(t.getCrlIssuer());
			setCrlLocation(t.getCrlLocation());
		}
	}

	/**
	 * Create a new model based on the CSR properties instance.
	 * 
	 * @param ca The issuing CA.
	 * @param element The CSR Properties instance.
	 * @throws IOException Creating the model failed.
	 * @throws NoSuchAlgorithmException Creating the model failed.
	 * @throws InvalidKeyException Creating the model failed.
	 */
	public NewCertificateModel(CertificateAuthority ca, CertificateRequestProperties element)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException {
		this.ca = ca;
		this.csr = element;
		// set defaults;
		useCAPassword = true;
		representsTemplateOnly = false;

		CertificateRequestPKCS10 r = (CertificateRequestPKCS10) this.csr.getCertificateRequest();

		setcARequest(r.isCARequest());
		setCertificatePolicies(r.getCertificatePolicies());
		setDescription(null);
		setSubject(r.getSubject());
		setKeyUsage(r.getKeyUsage());
		setExtendedKeyUsage(r.getExtendedKeyUsageVector());
		setSubjectAlternativeName(r.getSubjectAlternativeNames());
		setCrlIssuer(r.getCrlIssuer());
		setCrlLocation(r.getCrlLocation());

		// Set the keying material.
		JcaPKCS10CertificationRequest jcaHolder = new JcaPKCS10CertificationRequest(r.getEncoded())
				.setProvider(BouncyCastleProvider.PROVIDER_NAME);

		setKeyPair(new KeyPair(jcaHolder.getPublicKey(), null));
		setKeyType(null);
	}

	/**
	 * Get the certificate Start Date
	 * 
	 * @return the startDate
	 */
	public ZonedDateTime getStartDate() {
		return startDate;
	}

	/**
	 * Set the certificate Start Date
	 * 
	 * @param startDate the startDate to set
	 */
	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	/**
	 * Get the certificate Expiry Date
	 * 
	 * @return the expiryDate
	 */
	public ZonedDateTime getExpiryDate() {
		return expiryDate;
	}

	/**
	 * Set the certificate Expiry Date
	 * 
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(ZonedDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get if the user wishes to use the CA's password for the internal PKCS12 password.
	 * 
	 * @return TRUE if on creation should use the CA password for the PKCS12 datastore.
	 */
	public boolean isUseCAPassword() {
		return useCAPassword;
	}

	/**
	 * Get if the user wishes to use the CA's password for the internal PKCS12 password.
	 * 
	 * @param useCAPassword TRUE if on creation should use the CA password for the PKCS12 datastore.
	 */
	public void setUseCAPassword(boolean useCAPassword) {
		this.useCAPassword = useCAPassword;
	}

	/**
	 * The Certificate Authority used to create the issued certificate
	 * 
	 * @return the CA.
	 */
	public CertificateAuthority getCa() {
		return ca;
	}

	/**
	 * Get the not before date
	 * 
	 * @return The date
	 */
	public ZonedDateTime getNotBefore() {
		return notBefore;
	}

	/**
	 * Set the not before date
	 * 
	 * @param notBefore The not before date
	 */
	public void setNotBefore(ZonedDateTime notBefore) {
		this.notBefore = notBefore;
	}

	/**
	 * Get the not after date.
	 * 
	 * @return The not after date
	 */
	public ZonedDateTime getNotAfter() {
		return notAfter;
	}

	/**
	 * Set the not after date
	 * 
	 * @param notAfter The not after date.
	 */
	public void setNotAfter(ZonedDateTime notAfter) {
		this.notAfter = notAfter;
	}

	/**
	 * Is this model representing only a template?
	 * 
	 * @return TRUE if this model represents only a template, and not a full Certificate requeset.
	 */
	public boolean isRepresentsTemplateOnly() {
		return representsTemplateOnly;
	}

	/**
	 * Set if this model represents only a template
	 * 
	 * @param representsTemplateOnly TRUE if this model is to only represent a template.
	 */
	public void setRepresentsTemplateOnly(boolean representsTemplateOnly) {
		this.representsTemplateOnly = representsTemplateOnly;
	}

	/**
	 * Get the currently set CSR
	 * 
	 * @return The currently set CSR
	 */
	public CertificateRequestProperties getCsr() {
		return csr;
	}

	/**
	 * Set the CSR instance.
	 * 
	 * @param csr The CSR instance to set.
	 */
	public void setCsr(CertificateRequestProperties csr) {
		this.csr = csr;
		setRepresentsTemplateOnly(false);
	}

	/**
	 * Is this model representing a Certificate Request?
	 * 
	 * @return TRUE if this model represents an external Certificate Request.
	 */
	public boolean isCertificateRequest() {
		return csr != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
		result = prime * result + ((notAfter == null) ? 0 : notAfter.hashCode());
		result = prime * result + ((notBefore == null) ? 0 : notBefore.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + (representsTemplateOnly ? 1231 : 1237);
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result + (useCAPassword ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
//		if (getClass() != obj.getClass())
//			return false;
		NewCertificateModel other = (NewCertificateModel) obj;
		if (expiryDate == null) {
			if (other.expiryDate != null)
				return false;
		} else if (!expiryDate.equals(other.expiryDate))
			return false;
		if (notAfter == null) {
			if (other.notAfter != null)
				return false;
		} else if (!notAfter.equals(other.notAfter))
			return false;
		if (notBefore == null) {
			if (other.notBefore != null)
				return false;
		} else if (!notBefore.equals(other.notBefore))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (representsTemplateOnly != other.representsTemplateOnly)
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (useCAPassword != other.useCAPassword)
			return false;
		return true;
	}


}
