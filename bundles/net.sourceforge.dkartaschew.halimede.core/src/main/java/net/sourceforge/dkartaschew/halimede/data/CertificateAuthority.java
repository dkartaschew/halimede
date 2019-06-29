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

package net.sourceforge.dkartaschew.halimede.data;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CRLException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8EncryptorBuilder;
import org.bouncycastle.operator.OperatorCreationException;

import net.sourceforge.dkartaschew.halimede.data.IssuedCertificateProperties.Key;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequest;
import net.sourceforge.dkartaschew.halimede.data.impl.CertificateRequestPKCS10;
import net.sourceforge.dkartaschew.halimede.data.impl.IssuedCertificate;
import net.sourceforge.dkartaschew.halimede.enumeration.EncodingType;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS12Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.PKCS8Cipher;
import net.sourceforge.dkartaschew.halimede.enumeration.RevokeReasonCode;
import net.sourceforge.dkartaschew.halimede.enumeration.SignatureAlgorithm;
import net.sourceforge.dkartaschew.halimede.exceptions.DatastoreLockedException;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.log.IActivityLogger;
import net.sourceforge.dkartaschew.halimede.util.DateTimeUtil;

/**
 * A basic Certificate Authority
 */
public class CertificateAuthority {

	/**
	 * The default folder name where the certificate templates are stored.
	 */
	final static String TEMPLATES_PATH = "Templates";
	/**
	 * The default folder name where Issued Certificates are stored.
	 */
	public final static String ISSUED_PATH = "Issued";
	/**
	 * The default folder name where incoming requests are stored.
	 */
	final static String REQUESTS_PATH = "Requests";
	/**
	 * The default folder name where Revoked Certificates are stored.
	 */
	public final static String REVOKED_PATH = "Revoked";
	/**
	 * The default folder name where X509CRLs are stored.
	 */
	final static String X509CRL_PATH = "CRLs";
	/**
	 * The default folder name where logs are stored.
	 */
	public final static String LOG_PATH = "Log";
	/**
	 * The default filename for the Issuers Certificate.
	 */
	private final static String CA_PKCS12_FILENAME = "ca.p12";
	/**
	 * The emitted property for change of templates.
	 */
	public static final String PROPERTY_TEMPLATE = TEMPLATES_PATH;
	/**
	 * The emitted property for change of issued certificates.
	 */
	public static final String PROPERTY_ISSUED = ISSUED_PATH;
	/**
	 * The emitted property for change of revoked certificates.
	 */
	public static final String PROPERTY_REVOKED = REVOKED_PATH;
	/**
	 * The emitted property for change of requested certificates.
	 */
	public static final String PROPERTY_REQUESTS = REQUESTS_PATH;
	/**
	 * The emitted property for change of CRLs.
	 */
	public static final String PROPERTY_CRLS = X509CRL_PATH;
	/**
	 * The emitted property for change in lock status.
	 */
	public static final String PROPERTY_UNLOCK = "unlocked";
	/**
	 * The emitted property for change in description.
	 */
	public static final String PROPERTY_DESCRIPTION = "description";
	/**
	 * The emitted property for change in default signature.
	 */
	public static final String PROPERTY_SIGNATURE = "signature";
	/**
	 * The emitted property for change in default signature.
	 */
	public static final String PROPERTY_EXPIRY = "expiry";
	/**
	 * The emitted property for change in incrementalSerial.
	 */
	public static final String PROPERTY_INCREMENTAL_SERIAL = "incrementalSerial";
	/**
	 * The emitted property for change in enable log.
	 */
	public static final String PROPERTY_ENABLE_LOG = "enableLog";

	/**
	 * The base path for the CA
	 */
	private final Path basePath;
	/**
	 * The issuer certificate information.
	 */
	private IIssuedCertificate issuerInformation;
	/**
	 * CA Setting holder.
	 */
	private CertificateAuthoritySettings settings;

	/**
	 * Property Support helper.
	 */
	private final PropertyChangeSupport propertySupport;

	/**
	 * Collection of templates
	 */
	private final Map<Path, ICertificateKeyPairTemplate> templates;
	/**
	 * Collection of requests to fulfilled.
	 */
	private final Map<Path, CertificateRequestProperties> requests;
	/**
	 * Collection of issued certificates and keypairs.
	 */
	private final Map<Path, IssuedCertificateProperties> issuedCertificates;
	/**
	 * Collection of issued certificates and keypairs that have been revoked.
	 */
	private final Map<Path, IssuedCertificateProperties> revokedCertificates;
	/**
	 * Collection of CRLs that have been generated.
	 */
	private final Map<Path, CRLProperties> crls;
	/**
	 * Search paths.
	 */
	private final List<Path> searchPaths = new ArrayList<>();
	/**
	 * The activity logger.
	 */
	private final IActivityLogger logger;

	/**
	 * Create a new Certificate Authority
	 * 
	 * @param basePath The base path of the authority.
	 * @return A new Certificate Authority.
	 * @throws IOException If opening/creating the CA fails.
	 * @throws CertificateEncodingException The certificate for signing is invalid.
	 */
	public static CertificateAuthority open(Path basePath) throws IOException, CertificateEncodingException {
		if (basePath == null || basePath.getNameCount() == 0 || getPathFilenameAsString(basePath).isEmpty()) {
			throw new IOException("Path is not valid");
		}
		if (!Files.exists(basePath)) {
			throw new IOException("Path is not valid");
		}
		if (!Files.isDirectory(basePath)) {
			throw new IOException("Path is not a directory");
		}
		if (!Files.isReadable(basePath) && !Files.isWritable(basePath)) {
			throw new IOException("Path is not accessible");
		}
		return new CertificateAuthority(basePath);
	}

	/**
	 * Create a new Certificate Authority
	 * 
	 * @param basePath The base path of the authority.
	 * @param certificate The CA Certificate, must have a public key, private key AND signed certificate.
	 * 
	 * @return A new Certificate Authority.
	 * @throws IOException If opening/creating the CA fails.
	 * @throws CertificateEncodingException The certificate for signing is invalid.
	 */
	public static CertificateAuthority create(Path basePath, IIssuedCertificate certificate)
			throws CertificateEncodingException, IOException {
		return create(basePath, certificate, null);
	}

	/**
	 * Create a new Certificate Authority
	 * 
	 * @param basePath The base path of the authority.
	 * @param certificate The CA Certificate, must have a public key, private key AND signed certificate.
	 * @param description The textual description of the CA.
	 * @return A new Certificate Authority.
	 * @throws IOException If opening/creating the CA fails.
	 * @throws CertificateEncodingException The certificate for signing is invalid.
	 */
	public static CertificateAuthority create(Path basePath, IIssuedCertificate certificate, String description)
			throws IOException, CertificateEncodingException {
		if (basePath == null) {
			throw new IOException("Path is not valid");
		}
		if (!Files.exists(basePath)) {
			throw new IOException("Path is not valid");
		}
		if (!Files.isDirectory(basePath)) {
			throw new IOException("Path is not a directory");
		}
		if (!Files.isReadable(basePath) && !Files.isWritable(basePath)) {
			throw new IOException("Path is not accessible");
		}
		if (certificate.getCertificateChain() == null || certificate.getCertificateChain().length == 0) {
			throw new IOException("Missing Certificate");
		}
		if (certificate.getPublicKey() == null) {
			throw new IOException("Missing Public Key");
		}
		if (certificate.getPrivateKey() == null) {
			throw new IOException("Missing Private Key");
		}
		return new CertificateAuthority(basePath, certificate, description);
	}

	/**
	 * Open an existing CA for the given base path, reading the settings contained in the base path.
	 * 
	 * @param path The base Path.
	 * @throws IOException If opening/creating the CA fails.
	 * @throws CertificateEncodingException The supplied certificate is invalid.
	 */
	protected CertificateAuthority(Path path) throws IOException, CertificateEncodingException {
		this(path, null, null);
	}

	/**
	 * Create a new CA for the given base path, and certificate datastore.
	 * 
	 * @param path The base Path.
	 * @param certificate The Certificate information to use.
	 * @param description The description of the CA.
	 * @throws IOException If opening/creating the CA fails.
	 * @throws CertificateEncodingException The certificate is invalid.
	 * @throws IllegalArgumentException If the supplied issuer information is not a CA.
	 */
	protected CertificateAuthority(Path path, IIssuedCertificate certificate, String description)
			throws IOException, CertificateEncodingException {
		propertySupport = new PropertyChangeSupport(this);
		this.basePath = path;
		this.issuerInformation = certificate;
		this.issuedCertificates = new ConcurrentHashMap<>();
		this.revokedCertificates = new ConcurrentHashMap<>();
		this.requests = new ConcurrentHashMap<>();
		this.templates = new ConcurrentHashMap<>();
		this.crls = new ConcurrentHashMap<>();
		// check to see if this is a CA certificate
		if (this.issuerInformation != null && !CertificateFactory.isCACertificate(issuerInformation)) {
			throw new IllegalArgumentException("Supplied Issuer Information is not a Certificate Authority");
		}

		createSubFolder(basePath.resolve(ISSUED_PATH));
		createSubFolder(basePath.resolve(REQUESTS_PATH));
		createSubFolder(basePath.resolve(TEMPLATES_PATH));
		createSubFolder(basePath.resolve(REVOKED_PATH));
		createSubFolder(basePath.resolve(X509CRL_PATH));
		createSubFolder(basePath.resolve(LOG_PATH));
		searchPaths.add(basePath);
		searchPaths.add(basePath.resolve(ISSUED_PATH));
		searchPaths.add(basePath.resolve(REVOKED_PATH));
		searchPaths.add(basePath.resolve(X509CRL_PATH));
		searchPaths.add(basePath.resolve(REQUESTS_PATH));
		loadOrCreateSettings();
		// This is a create event, so store the based element to disk.
		if (issuerInformation != null) {
			issuerInformation.createPKCS12(path.resolve(CA_PKCS12_FILENAME), issuerInformation.getPassword(), null,
					PKCS12Cipher.AES256);
			settings.setPkcs12Filename(CA_PKCS12_FILENAME);
			settings.setDescription(description);
			saveSettings();

		}
		this.logger = IActivityLogger.createLogger(this);
		this.logger.log(Level.INFO, "Open Certificate Authority");
		refresh();
	}

	/**
	 * Load or create the settings file for this CA
	 * 
	 * @throws IOException If reading/writing the configuration fails.
	 */
	private void loadOrCreateSettings() throws IOException {
		Path settingsFile = basePath.resolve(CertificateAuthoritySettings.DEFAULT_NAME);
		if (Files.exists(settingsFile)) {
			if (Files.isReadable(settingsFile) && Files.isDirectory(settingsFile)) {
				throw new IOException("Settings File is unable to read");
			}
			try {
				this.settings = CertificateAuthoritySettings.read(settingsFile);
			} catch (Exception e) {
				throw new IOException(e);
			}
		} else {
			// Create event.
			this.settings = new CertificateAuthoritySettings(UUID.randomUUID());
			this.settings.setSerial(System.currentTimeMillis());
			this.settings.setSignatureAlgorithm(//
					SignatureAlgorithm.getDefaultSignature(issuerInformation.getPublicKey()));
			this.settings.setExpiryDays(365);
			saveSettings();
		}
	}

	/**
	 * Create a folder.
	 * 
	 * @param path The path to create.
	 * @throws IOException If creating the path fails.
	 */
	private void createSubFolder(Path path) throws IOException {
		if (!Files.exists(path)) {
			Files.createDirectories(path);
			// throw new IOException("Unable to materialise " + f.getAbsolutePath());
		}
	}

	/**
	 * Save settings to the configuration file.
	 * 
	 * @throws IOException If writing the configuration fails.
	 */
	private synchronized void saveSettings() throws IOException {
		try {
			CertificateAuthoritySettings.write(settings, basePath.resolve(CertificateAuthoritySettings.DEFAULT_NAME));
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	/**
	 * Get the base path
	 * 
	 * @return The base path of the certificate authority.
	 */
	public Path getBasePath() {
		return basePath;
	}

	/**
	 * Is this datastore locked?
	 * 
	 * @return TRUE if the datastore is locked.
	 */
	public boolean isLocked() {
		return issuerInformation == null;
	}

	/**
	 * Lock the datastore
	 */
	public synchronized void lock() {
		boolean islocked = isLocked();
		issuerInformation = null;
		this.logger.log(Level.INFO, "Locking Certificate Authority");
		propertySupport.firePropertyChange(PROPERTY_UNLOCK, islocked, false);
	}

	/**
	 * Unlock this datastore.
	 * 
	 * @param password The password to use to unlock or NULL if no password is required.
	 * @throws IOException The File does not exist or is unreadable
	 * @throws InvalidPasswordException The password is invalid
	 * @throws KeyStoreException Loading the keystore container failed.
	 * @throws CertificateEncodingException The certificate is invalid.
	 */
	public synchronized void unlock(String password)
			throws KeyStoreException, InvalidPasswordException, IOException, CertificateEncodingException {
		if (!isLocked()) {
			return;
		}
		this.logger.log(Level.INFO, "Requesting Unlock of Certificate Authority");
		issuerInformation = IssuedCertificate.openPKCS12(basePath.resolve(settings.getPkcs12Filename()), password);
		if (!CertificateFactory.isCACertificate(issuerInformation)) {
			issuerInformation = null;
			this.logger.log(Level.INFO, "Unlock of Certificate Authority failed.");
			throw new IllegalArgumentException("Supplied Issuer Information is not a Certificate Authority");
		}
		this.logger.log(Level.INFO, "Unlocked Certificate Authority");
		propertySupport.firePropertyChange(PROPERTY_UNLOCK, false, true);
	}

	/**
	 * Get the description text of this CA
	 * 
	 * @return The description text of this CA
	 */
	public synchronized String getDescription() {
		return settings.getDescription();
	}

	/**
	 * Set the description text of this CA
	 * 
	 * @param description The new description
	 * @throws IOException If saving the configuration fails.
	 */
	public synchronized void setDescription(String description) throws IOException {
		String oldValue = settings.getDescription();
		settings.setDescription(description);
		saveSettings();
		this.logger.log(Level.INFO, "Updating Certificate Authority Description {0}", description);
		propertySupport.firePropertyChange(PROPERTY_DESCRIPTION, oldValue, description);
	}

	/**
	 * Get the default days for expiry from start date
	 * 
	 * @return The default expiry
	 */
	public synchronized int getExpiryDays() {
		return settings.getExpiryDays();
	}

	/**
	 * Get the default days for expiry from start date
	 * 
	 * @param expiry The default expiry
	 * @throws IllegalArgumentException The value was 0 or negative.
	 * @throws IOException If saving the configuration fails.
	 */
	public synchronized void setExpiryDays(int expiry) throws IOException, IllegalArgumentException {
		if (expiry <= 0) {
			throw new IllegalArgumentException("The expiry day count is invalid");
		}
		int oldValue = settings.getExpiryDays();
		settings.setExpiryDays(expiry);
		saveSettings();
		this.logger.log(Level.INFO, "Updating Certificate Authority Expiry days {0}", expiry);
		propertySupport.firePropertyChange(PROPERTY_EXPIRY, oldValue, expiry);
	}

	/**
	 * Is Incremental serial number generation set.
	 * 
	 * @return TRUE if incremental serial number generation is set.
	 */
	public synchronized boolean isIncrementalSerial() {
		return settings.isIncrementalSerial();
	}

	/**
	 * Set incremental serial number generation
	 * 
	 * @param incrementalSerial TRUE to enable incremental serial numbers or FALSE
	 *                          for serial number to be timestamp.
	 *
	 * @throws IllegalArgumentException The value was 0 or negative.
	 * @throws IOException If saving the configuration fails.
	 */
	public synchronized void setIncrementalSerial(boolean incrementalSerial) throws IOException {
		boolean oldValue = settings.isIncrementalSerial();
		settings.setIncrementalSerial(incrementalSerial);
		saveSettings();
		this.logger.log(Level.INFO, "Updating Certificate Authority Incremental Serial {0}", incrementalSerial);
		propertySupport.firePropertyChange(PROPERTY_INCREMENTAL_SERIAL, oldValue, incrementalSerial);
	}
	
	/**
	 * Return if the activity log for this CA is enabled.
	 * 
	 * @return TRUE if the log is enabled.
	 */
	public synchronized boolean isEnableLog() {
		return settings.isEnableLog();
	}

	/**
	 * Set if the CA activity log is enabled or not.
	 * 
	 * @param enable TRUE to enable the log.
	 * @throws IOException If saving the configuration fails.
	 */
	public synchronized void setEnableLog(boolean enable) throws IOException {
		boolean oldValue = settings.isEnableLog();
		settings.setEnableLog(enable);
		saveSettings();
		this.logger.log(Level.INFO, "Setting Activity Log {0}", enable);
		propertySupport.firePropertyChange(PROPERTY_ENABLE_LOG, oldValue, enable);
	}
	
	/**
	 * Get the CA's UUID
	 * 
	 * @return The CA's UUID.
	 */
	public UUID getCertificateAuthorityID() {
		return settings.getUuid();
	}

	/**
	 * Get the Certificate of this authority
	 * 
	 * @return The X509 Certificate of this authority.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public Certificate getCertificate() throws DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Access Certificate Authority Certificate");
		return issuerInformation.getCertificateChain()[0];
	}

	/**
	 * Get the Certificate Chain of this authority
	 * 
	 * @return The X509 Certificate Chain of this authority.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public Certificate[] getCertificateChain() throws DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Access Certificate Authority Certificate Chain");
		return issuerInformation.getCertificateChain();
	}

	/**
	 * Save the client certificate to the given file.
	 * 
	 * @param filename The filename to use
	 * @param encoding The encoding to use.
	 * @throws IOException If writing the file failed.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public void exportCertificate(Path filename, EncodingType encoding) throws IOException, DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Export Certificate Authority Certificate to {0}", filename);
		issuerInformation.createCertificate(filename, encoding);
	}

	/**
	 * Save the complete certificate chain to the given file.
	 * 
	 * @param filename The filename to use
	 * @param encoding The encoding to use.
	 * @throws IOException If writing the file failed.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public void exportCertificateChain(Path filename, EncodingType encoding)
			throws IOException, DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Export Certificate Authority Certificate Chain to {0}", filename);
		issuerInformation.createCertificateChain(filename, encoding);
	}

	/**
	 * Create PKCS8 or equivalent private key file.
	 * 
	 * @param filename The filename to use
	 * @param password The password to encrypt with. (may be NULL for no password).
	 * @param encoding The type of encoding.
	 * @param encryptionAlg The encryption algorithm. (may be NULL if no password). see
	 *            {@link JceOpenSSLPKCS8EncryptorBuilder} constants for available names.
	 * @throws IOException If writing the file failed.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public void exportPrivateKey(Path filename, String password, EncodingType encoding, PKCS8Cipher encryptionAlg)
			throws IOException, DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Export Certificate Authority Private Key to {0}", filename);
		issuerInformation.createPKCS8(filename, password, encoding, encryptionAlg);
	}

	/**
	 * Save this Issued Certificate in a PKCS12 container
	 * 
	 * @param filename The filename to use
	 * @param password The password to use. (may be NULL for no password)
	 * @param alias The alias to use within the PKCS12 container.
	 * @param cipher The Cipher to use to encrypt the PKCS12 contents with. (Note: AES is not widely supported). If
	 *            password is NULL, this value is ignored.
	 * @throws IOException If writing the file failed.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public void exportPKCS12(Path filename, String password, String alias, PKCS12Cipher cipher)
			throws IOException, DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Export Certificate Authority Certificate and Keying as PKCS#12 to {0}", filename);
		issuerInformation.createPKCS12(filename, password, alias, cipher);
	}

	/**
	 * Save the CA Public Key
	 * 
	 * @param filename The filename to use
	 * @param encoding The file encoding to use
	 * @throws IOException If writing the file failed.
	 * @throws IllegalStateException The internal state is invalid for key creation
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public void createPublicKey(Path filename, EncodingType encoding)
			throws IOException, IllegalStateException, DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Export Certificate Authority Public Key to {0}", filename);
		issuerInformation.createPublicKey(filename, encoding);
	}

	/**
	 * Sign the given certificate request, and return the signed certificate
	 * 
	 * @param certRequest The certificate request.
	 * @param startDate The certificate start date.
	 * @param expiryDate The certificate expiry date.
	 * @return The signed certificate.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 * @throws CertificateException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertIOException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 */
	public Certificate signCertificateRequest(ICertificateRequest certRequest, ZonedDateTime startDate,
			ZonedDateTime expiryDate) throws IOException, DatastoreLockedException, CertIOException,
			OperatorCreationException, CertificateException {
		checkDatastoreLock();
		if (certRequest == null) {
			throw new IllegalArgumentException("No Certificate Request Provided");
		}
		if (startDate == null || expiryDate == null) {
			throw new IllegalArgumentException("Missing Certificate Date information");
		}
		this.logger.log(Level.INFO, "Request Certificate Authority Sign Certificate Request  {0}", certRequest.getSubject());
		return CertificateFactory.signCertificateRequest(this, certRequest, startDate, expiryDate);
	}

	/**
	 * Sign the given certificate request, and store as a PKCS12 container.
	 * 
	 * @param certRequest The certificate request.
	 * @param startDate The certificate start date.
	 * @param expiryDate The certificate expiry date.
	 * @param password The password to use for the PKCS12 container.
	 * @return The properties of the issued certificate.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 * @throws CertificateException Creation of the certificate fails
	 * @throws OperatorCreationException Creation of the certificate fails
	 * @throws CertIOException Creation of the certificate fails
	 * @throws IOException Creation of the certificate fails
	 * @throws InvalidAlgorithmParameterException Generation of the keying material failed
	 * @throws NoSuchProviderException Generation of the keying material failed
	 * @throws NoSuchAlgorithmException Generation of the keying material failed
	 */
	public IssuedCertificateProperties signAndStoreCertificateRequest(ICertificateRequest certRequest,
			ZonedDateTime startDate, ZonedDateTime expiryDate, String password) throws IOException,
			DatastoreLockedException, CertIOException, OperatorCreationException, CertificateException,
			NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		// Sign the request.
		Certificate cert = signCertificateRequest(certRequest, startDate, expiryDate);
		this.logger.log(Level.INFO, "Storing Certificate  {0}", certRequest.getSubject());
		// Create a new certificate chain, prepending the new cert to the start of the chain.
		Certificate[] issuerChain = issuerInformation.getCertificateChain();
		Certificate[] chain = new Certificate[issuerChain.length + 1];
		chain[0] = cert;
		System.arraycopy(issuerChain, 0, chain, 1, issuerChain.length);

		// Create an issued certificate properties instance.
		IssuedCertificateProperties properties = new IssuedCertificateProperties(this);
		properties.setProperty(Key.subject, certRequest.getSubject().toString());
		properties.setProperty(Key.startDate, DateTimeUtil.toString(startDate));
		properties.setProperty(Key.endDate, DateTimeUtil.toString(expiryDate));
		properties.setProperty(Key.certificateSerialNumber, ((X509Certificate) cert).getSerialNumber().toString());
		if (certRequest instanceof CertificateRequest) {
			properties.setProperty(Key.creationDate,
					DateTimeUtil.toString(((CertificateRequest) certRequest).getCreationDate()));
			if (((CertificateRequest) certRequest).getDescription() != null
					&& !((CertificateRequest) certRequest).getDescription().isEmpty())
				properties.setProperty(Key.description, ((CertificateRequest) certRequest).getDescription());
		}

		IssuedCertificate ic = null;
		Path filename = null;
		if (certRequest instanceof CertificateRequest
				&& ((CertificateRequest) certRequest).getKeyPair().getPrivate() != null) {
			CertificateRequest cr = (CertificateRequest) certRequest;
			filename = generateFilename(((X509Certificate) cert).getSerialNumber(), ISSUED_PATH, ".p12");
			// Self generated
			ic = new IssuedCertificate(cr.getKeyPair(), chain, filename, null, password);
			String certdesc = properties.getProperty(Key.description);
			if (certdesc == null) {
				certdesc = properties.getProperty(Key.subject);
			}
			String alias = certdesc + "#" + properties.getProperty(Key.certificateSerialNumber);
			ic.createPKCS12(filename, password, alias, PKCS12Cipher.AES256);

			properties.setProperty(Key.keyType, cr.getKeyType().name());
			properties.setProperty(Key.pkcs12store, getPathFilenameAsString(filename));

		} else {
			filename = generateFilename(((X509Certificate) cert).getSerialNumber(), ISSUED_PATH, ".p7b");
			// Certificates only.
			ic = new IssuedCertificate(null, chain, filename, null, password);
			ic.createCertificateChain(filename, EncodingType.DER);

			properties.setProperty(Key.pkcs7store, getPathFilenameAsString(filename));
		}
		// Generate the properties file.
		Path propertiesPath = filename.getParent();
		String propertiesFilename = getPathFilenameAsString(filename);
		propertiesFilename = propertiesFilename.substring(0, propertiesFilename.lastIndexOf('.'))
				+ IssuedCertificateProperties.DEFAULT_EXTENSION;
		if(propertiesPath == null) {
			throw new IllegalStateException("Properties Parent Path is NULL?");
		}
		propertiesPath = propertiesPath.resolve(propertiesFilename);
		properties.setProperty(Key.filename, propertiesFilename);

		// And store.
		try (FileOutputStream out = new FileOutputStream(propertiesPath.toFile())) {
			properties.store(out);
		}
		ConcurrentHashMap<Path, IssuedCertificateProperties> oldValue = new ConcurrentHashMap<>(issuedCertificates);
		issuedCertificates.put(propertiesPath, properties);
		propertySupport.firePropertyChange(PROPERTY_ISSUED, oldValue.values(), issuedCertificates.values());
		return properties;
	}

	/**
	 * Add or update the given template.
	 * 
	 * @param template The template to add or update
	 * @throws Exception If storing the template fails.
	 */
	public void addTemplate(ICertificateKeyPairTemplate template) throws Exception {
		this.logger.log(Level.INFO, "Storing Template  {0}", template);
		ZonedDateTime creationDate = template.getCreationDate();
		String filename = String.format("08%x%s", creationDate.toInstant().getEpochSecond(),
				ICertificateKeyPairTemplate.DEFAULT_EXTENSION);
		Path path = basePath.resolve(TEMPLATES_PATH).resolve(filename);
		template.store(path);

		/*
		 * Add in the template instance, but ALWAYS reload from disk to ensure it's stored correctly AND has nothing
		 * shared with the template passed in.
		 */
		Map<Path, ICertificateKeyPairTemplate> oldValue = new ConcurrentHashMap<>(templates);
		templates.put(path, ICertificateKeyPairTemplate.open(path));
		propertySupport.firePropertyChange(PROPERTY_TEMPLATE, oldValue, templates);
	}

	/**
	 * Remove the given template.
	 * 
	 * @param template The template to delete
	 * @throws Exception If removing the template fails.
	 */
	public void removeCertificateTemplate(ICertificateKeyPairTemplate template) throws Exception {
		this.logger.log(Level.INFO, "Removing Template {0}", template);
		// Find the item in the map of templates.
		Path path = templates.entrySet().stream()//
				.filter(e -> e.getValue() == template)//
				.findFirst()//
				.map(e -> e.getKey())//
				.orElse(null);
		if (path == null) {
			throw new NoSuchElementException("The template doesn't exist");
		}
		Files.delete(path);

		Map<Path, ICertificateKeyPairTemplate> oldValue = new ConcurrentHashMap<>(templates);
		templates.remove(path);
		propertySupport.firePropertyChange(PROPERTY_TEMPLATE, oldValue, templates);
	}

	/**
	 * Add a certificate Signing request
	 * 
	 * @param filename The filename of the request file.
	 * @return The ICertificateRequest instance.
	 * @throws IOException if decoding or sitting the CSR in the backing store fails.
	 */
	public CertificateRequestProperties addCertificateSigningRequest(Path filename) throws IOException {
		if (filename == null) {
			throw new IllegalArgumentException("Path is null");
		}
		// Ensure we can open the CSR
		ICertificateRequest csr = CertificateRequestPKCS10.create(filename);
		
		this.logger.log(Level.INFO, "Adding CSR to Certificate Authority {0}", csr.getSubject());

		// It opened fine, so let's copy the file to the required location
		Path destFilename = generateFilename(BigInteger.valueOf(System.currentTimeMillis()), //
				REQUESTS_PATH, ICertificateRequest.DEFAULT_EXTENSION);
		Path path = basePath.resolve(destFilename);
		Files.copy(filename, path, StandardCopyOption.REPLACE_EXISTING);

		/*
		 * Save our own instance.
		 */
		CertificateRequestProperties properties = new CertificateRequestProperties(this, csr);
		properties.setProperty(CertificateRequestProperties.Key.subject, //
				csr.getSubject().toString());
		properties.setProperty(CertificateRequestProperties.Key.csrFilename, //
				getPathFilenameAsString(path));
		properties.setProperty(CertificateRequestProperties.Key.importDate, //
				DateTimeUtil.toString(ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE)));
		try {
			properties.setProperty(CertificateRequestProperties.Key.keyType, //
					KeyPairFactory.forKeyInformation(csr.getSubjectPublicKeyInfo()).name());
		} catch (IOException e) {
			// ignore for foreign type that are not native for us.
		}

		// Generate the properties file.
		Path propertiesPath = path.getParent();
		if(propertiesPath == null) {
			throw new IllegalStateException("Properties Parent Path is NULL?");
		}
		String propertiesFilename = getPathFilenameAsString(path);
		propertiesFilename = propertiesFilename.substring(0, propertiesFilename.lastIndexOf('.'))
				+ CertificateRequestProperties.DEFAULT_EXTENSION;
		propertiesPath = propertiesPath.resolve(propertiesFilename);

		properties.setProperty(CertificateRequestProperties.Key.filename, propertiesFilename);
		try (FileOutputStream out = new FileOutputStream(propertiesPath.toFile())) {
			properties.store(out);
		}
		// Add it to the map and let any listeners know...
		Map<Path, CertificateRequestProperties> oldValue = new ConcurrentHashMap<>(requests);
		requests.put(path, properties);
		propertySupport.firePropertyChange(PROPERTY_REQUESTS, oldValue, requests);
		return properties;
	}

	/**
	 * Remove the given CSR
	 * 
	 * @param request The request to remove
	 * @throws IOException Failed to delete the CSR.
	 */
	public void removeCertificateSigningRequest(CertificateRequestProperties request) throws IOException {
		if (request == null) {
			throw new IllegalArgumentException("Missing certificate request details");
		}
		
		this.logger.log(Level.INFO, "Removing CSR {0}", request.getProperty(CertificateRequestProperties.Key.subject));
		// Find the item in the map of CSRs.
		Path path = requests.entrySet().stream()//
				.filter(e -> e.getValue() == request)//
				.findFirst()//
				.map(e -> e.getKey())//
				.orElse(null);
		if (path == null) {
			throw new NoSuchElementException("The Certificate Request doesn't exist");
		}
		Files.delete(path);
		Files.delete(basePath.resolve(PROPERTY_REQUESTS)
				.resolve(request.getProperty(CertificateRequestProperties.Key.filename)));

		Map<Path, CertificateRequestProperties> oldValue = new ConcurrentHashMap<>(requests);
		requests.remove(path);
		propertySupport.firePropertyChange(PROPERTY_REQUESTS, oldValue, requests);
	}
	
	/**
	 * Copy the CSR from the CSR location to the Issued Certificates location, named
	 * as
	 * 
	 * @param request The original CSR properties
	 * @param newCert The New Certificate Properties to match the CSR with.
	 * @throws IOException The Copy operation failed.
	 */
	public void moveCertificateSigningRequest(CertificateRequestProperties request, IssuedCertificateProperties newCert)
			throws IOException {
		if (newCert == null) {
			throw new IllegalArgumentException("Missing issued certificate details");
		}
		if (request == null) {
			throw new IllegalArgumentException("Missing certificate request details");
		}
		this.logger.log(Level.INFO, "Moving CSR {0} for Certificate {1}", 
				new Object[] {request.getProperty(CertificateRequestProperties.Key.subject), 
						newCert.getProperty(IssuedCertificateProperties.Key.subject)});
		// Find the item in the map of CSRs.
		Path path = requests.entrySet().stream()//
				.filter(e -> e.getValue() == request)//
				.findFirst()//
				.map(e -> e.getKey())//
				.orElse(null);
		if (path == null) {
			throw new NoSuchElementException("The Certificate Request doesn't exist");
		}
		String filename = newCert.getProperty(IssuedCertificateProperties.Key.pkcs12store);
		if (filename == null) {
			filename = newCert.getProperty(IssuedCertificateProperties.Key.pkcs7store);
		}
		filename = filename.substring(0, filename.lastIndexOf('.')) + ICertificateRequest.DEFAULT_EXTENSION;
		Path target = basePath.resolve(ISSUED_PATH).resolve(filename);
		Files.copy(path, target, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
		newCert.setProperty(IssuedCertificateProperties.Key.csrStore, target.getFileName().toString());
		try (FileOutputStream out = new FileOutputStream(basePath.resolve(ISSUED_PATH)
				.resolve(newCert.getProperty(IssuedCertificateProperties.Key.filename)).toFile())) {
			newCert.store(out);
		}
	}

	/**
	 * Revoke an issued certificate
	 * 
	 * @param certificateToRevoke The certificate to revoke
	 * @param revokeDate The revoke date (null to set now).
	 * @param code The code to revoke the certificate. (null to set unspecified).
	 * @return The updated details
	 * @throws IOException Relocation of files failed.
	 */
	public IssuedCertificateProperties revokeCertificate(IssuedCertificateProperties certificateToRevoke,
			ZonedDateTime revokeDate, RevokeReasonCode code) throws IOException {
		if (certificateToRevoke == null) {
			throw new IllegalArgumentException("Missing certificate details");
		}
		if (revokeDate == null) {
			revokeDate = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE);
		}
		if (code == null) {
			code = RevokeReasonCode.UNSPECIFIED;
		}
		this.logger.log(Level.INFO, "Revoke Certificate  {0} for {1}", 
				new Object[] {certificateToRevoke.getProperty(IssuedCertificateProperties.Key.subject), code});
		if (certificateToRevoke.getProperty(Key.revokeDate) != null) {
			throw new IllegalArgumentException("Certificate already revoked?");
		}
		certificateToRevoke.setProperty(Key.revokeDate, DateTimeUtil.toString(revokeDate));
		certificateToRevoke.setProperty(Key.revokeCode, code.name());
		certificateToRevoke.clearIssuedCertificate();

		// Move the underlying files to the new location.
		if (certificateToRevoke.getProperty(Key.pkcs12store) != null) {
			Path src = basePath.resolve(ISSUED_PATH).resolve(certificateToRevoke.getProperty(Key.pkcs12store));
			Path dest = basePath.resolve(REVOKED_PATH).resolve(certificateToRevoke.getProperty(Key.pkcs12store));
			Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
		} else {
			Path src = basePath.resolve(ISSUED_PATH).resolve(certificateToRevoke.getProperty(Key.pkcs7store));
			Path dest = basePath.resolve(REVOKED_PATH).resolve(certificateToRevoke.getProperty(Key.pkcs7store));
			Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
		}
		if (certificateToRevoke.getProperty(Key.csrStore) != null) {
			Path src = basePath.resolve(ISSUED_PATH).resolve(certificateToRevoke.getProperty(Key.csrStore));
			Path dest = basePath.resolve(REVOKED_PATH).resolve(certificateToRevoke.getProperty(Key.csrStore));
			Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
		}
		Path src = basePath.resolve(ISSUED_PATH).resolve(certificateToRevoke.getProperty(Key.filename));
		Path dest = basePath.resolve(REVOKED_PATH).resolve(certificateToRevoke.getProperty(Key.filename));
		Files.move(src, dest, StandardCopyOption.REPLACE_EXISTING);
		try (FileOutputStream out = new FileOutputStream(dest.toFile())) {
			certificateToRevoke.store(out);
		}

		/*
		 * Update the stored internal lists.
		 */
		Map<Path, IssuedCertificateProperties> oldValue = new ConcurrentHashMap<>(issuedCertificates);
		issuedCertificates.remove(src);
		propertySupport.firePropertyChange(PROPERTY_ISSUED, oldValue.values(), issuedCertificates.values());

		Map<Path, IssuedCertificateProperties> oldValue2 = new ConcurrentHashMap<>(revokedCertificates);
		revokedCertificates.put(dest, certificateToRevoke);
		propertySupport.firePropertyChange(PROPERTY_REVOKED, oldValue2, revokedCertificates);
		return certificateToRevoke;
	}

	/**
	 * Update the certificate properties backing store
	 * 
	 * @param properties The properties to store.
	 * @throws IOException If updating the backing store fails.
	 */
	public void updateIssuedCertificateProperties(IssuedCertificateProperties properties) throws IOException {
		this.logger.log(Level.INFO, "Update Certificate Properties {0}", 
				properties.getProperty(IssuedCertificateProperties.Key.subject));
		
		// Find which element this one represents.
		Path p = issuedCertificates.entrySet().stream()//
				.filter(e -> e.getValue().equals(properties))//
				.findFirst()//
				.map(e -> e.getKey())//
				.orElse(null);
		if (p == null) {
			p = revokedCertificates.entrySet().stream()//
					.filter(e -> e.getValue().equals(properties))//
					.findFirst()//
					.map(e -> e.getKey())//
					.orElse(null);
		}
		if (p != null) {
			try (FileOutputStream out = new FileOutputStream(p.toFile())) {
				properties.store(out);
			}
		}
	}

	/**
	 * Update the certificate request properties backing store
	 * 
	 * @param properties The properties to store.
	 * @throws IOException If updating the backing store fails.
	 */
	public void updateCertificateRequestProperties(CertificateRequestProperties properties) throws IOException {
		this.logger.log(Level.INFO, "Update Certificate Request Properties {0}", 
				properties.getProperty(CertificateRequestProperties.Key.subject));
		// Find which element this one represents.
		Path p = requests.entrySet().stream()//
				.filter(e -> e.getValue().equals(properties))//
				.findFirst()//
				.map(e -> e.getKey())//
				.orElse(null);
		if (p != null) {
			try (FileOutputStream out = new FileOutputStream(p.toFile())) {
				properties.store(out);
			}
		}
	}

	/**
	 * Update the certificate CRL properties backing store
	 * 
	 * @param properties The properties to store.
	 * @throws IOException If updating the backing store fails.
	 */
	public void updateCRLProperties(CRLProperties properties) throws IOException {
		this.logger.log(Level.INFO, "Update CRL Properties {0}", properties.getProperty(CRLProperties.Key.crlSerialNumber));
		// Find which element this one represents.
		Path p = crls.entrySet().stream()//
				.filter(e -> e.getValue().equals(properties))//
				.findFirst()//
				.map(e -> e.getKey())//
				.orElse(null);
		if (p != null) {
			try (FileOutputStream out = new FileOutputStream(p.toFile())) {
				properties.store(out);
			}
		}
	}

	/**
	 * Generate a CRL
	 * 
	 * @param crlExpiryDate The expiry date of the CRL or the next expected update of the CRL
	 * @return The generated CRL.
	 * @throws IOException Writing to the file failed
	 * @throws CertificateEncodingException Unable to create the signing information.
	 * @throws OperatorCreationException Unable to create the DER encoded CRL
	 * @throws DatastoreLockedException The issuers information is currently locked.
	 * @throws CRLException If generation of the CRL fails.
	 */
	public CRLProperties createCRL(ZonedDateTime crlExpiryDate) throws DatastoreLockedException, IOException,
			CRLException, CertificateEncodingException, OperatorCreationException {
		checkDatastoreLock();
		if (crlExpiryDate == null) {
			crlExpiryDate = ZonedDateTime.now(DateTimeUtil.DEFAULT_ZONE).plusDays(getExpiryDays());
		}
		X509CRL crl = CertificateFactory.generateCRL(this, crlExpiryDate);
		this.logger.log(Level.INFO, "Create CRL {0}", crlExpiryDate);
		X509CRLHolder holder = new X509CRLHolder(crl.getEncoded());

		X500Name issuer = holder.getIssuer();
		Extension ext = holder.getExtension(Extension.cRLNumber);
		CRLNumber serial = CRLNumber.getInstance(ext.getParsedValue());
		Path filename = generateFilename(serial.getCRLNumber(), X509CRL_PATH, ".crl");

		/*
		 * Save our own instance.
		 */
		CRLProperties crlProp = new CRLProperties(this, crl);
		crlProp.setProperty(CRLProperties.Key.issuer, //
				issuer.toString());
		crlProp.setProperty(CRLProperties.Key.crlSerialNumber, //
				serial.getCRLNumber().toString());
		crlProp.setProperty(CRLProperties.Key.issueDate, //
				DateTimeUtil.toString(crl.getThisUpdate()));
		crlProp.setProperty(CRLProperties.Key.nextExpectedDate, //
				DateTimeUtil.toString(crl.getNextUpdate()));
		crlProp.setProperty(CRLProperties.Key.crlFilename, //
				getPathFilenameAsString(filename));

		// Generate the properties file.
		Path propertiesPath = filename.getParent();
		String propertiesFilename = getPathFilenameAsString(filename);
		propertiesFilename = propertiesFilename.substring(0, propertiesFilename.lastIndexOf('.'))
				+ CRLProperties.DEFAULT_EXTENSION;
		if(propertiesPath == null) {
			throw new IllegalStateException("Properties Parent Path is NULL?");
		}
		propertiesPath = propertiesPath.resolve(propertiesFilename);
		crlProp.setProperty(CRLProperties.Key.filename, propertiesFilename);

		// And store.
		X509CRLEncoder.create(filename, EncodingType.DER, crl);
		try (FileOutputStream out = new FileOutputStream(propertiesPath.toFile())) {
			crlProp.store(out);
		}
		ConcurrentHashMap<Path, CRLProperties> oldValue = new ConcurrentHashMap<>(crls);
		crls.put(propertiesPath, crlProp);
		propertySupport.firePropertyChange(PROPERTY_CRLS, oldValue.values(), crls.values());
		return crlProp;
	}

	/**
	 * Check the lock on the data store
	 * 
	 * @throws DatastoreLockedException The datastore is locked
	 */
	private void checkDatastoreLock() throws DatastoreLockedException {
		if (isLocked()) {
			throw new DatastoreLockedException(
					"The Certificate Authority Datastore is locked. Unable to complete requested operation.");
		}
	}

	/**
	 * Generate a suitable filename for the storing the issued certificate.
	 * <p>
	 * Available for unit testing only.
	 * 
	 * @param serial The certificate serial number being stored.
	 * @param element The type of element to generate.
	 * @param suffix The filename suffix
	 * @return An absolute filename to use to store the issued certificate.
	 */
	protected Path generateFilename(BigInteger serial, String element, String suffix) {
		String value = String.format("%016x%s", serial, suffix);
		return basePath.resolve(element).resolve(value);
	}

	/**
	 * Can this CA sign intermediate CA certificates.
	 * 
	 * @return TRUE if Intermediate certificates may be signed.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public boolean canCreateIntermediateCA() throws DatastoreLockedException {
		X509Certificate cert = (X509Certificate) getCertificate();
		return cert.getBasicConstraints() >= 1;
	}

	/**
	 * Get the default signature algorithm for this CA
	 * 
	 * @return The default signature algorithm.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public synchronized SignatureAlgorithm getSignatureAlgorithm() throws DatastoreLockedException {
		checkDatastoreLock();
		return settings.getSignatureAlgorithm();
	}

	/**
	 * Set the default signature algorithm
	 * 
	 * @param signatureAlg The default signature algorithm
	 * @throws IOException If storing the CA state fails.
	 */
	public synchronized void setSignatureAlgorithm(SignatureAlgorithm signatureAlg) throws IOException {
		SignatureAlgorithm oldValue = settings.getSignatureAlgorithm();
		settings.setSignatureAlgorithm(signatureAlg);
		saveSettings();
		this.logger.log(Level.INFO, "Updating Certificate Authority Signature Algorithm {0}", signatureAlg);
		propertySupport.firePropertyChange(PROPERTY_SIGNATURE, oldValue, signatureAlg);
	}

	/**
	 * Get the Signing KeyPair for this CA
	 * 
	 * @return The Signing CA keypair.
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public KeyPair getKeyPair() throws DatastoreLockedException {
		checkDatastoreLock();
		this.logger.log(Level.INFO, "Accessing Certificate Authority Keying Material");
		return new KeyPair(issuerInformation.getPublicKey(), issuerInformation.getPrivateKey());
	}

	/**
	 * Get the CA's PKCS12 password
	 * 
	 * @return The password
	 * @throws DatastoreLockedException If the datastore is currently locked.
	 */
	public String getPassword() throws DatastoreLockedException {
		checkDatastoreLock();
		return issuerInformation.getPassword();
	}

	/**
	 * Get the next serial number for issued certificates
	 * 
	 * @return The next serial number
	 * @throws IOException If storing the CA state fails.
	 */
	public synchronized BigInteger getNextSerialNumber() throws IOException {
		BigInteger bint = settings.getAndIncrementSerial();
		saveSettings();
		return bint;
	}

	/**
	 * Get the next serial number for CRL
	 * 
	 * @return The next serial number
	 * @throws IOException If storing the CA state fails.
	 */
	public synchronized BigInteger getNextSerialCRLNumber() throws IOException {
		BigInteger bint = settings.getAndIncrementCRLSerial();
		saveSettings();
		return bint;
	}

	/**
	 * Peek the next serial number for CRL
	 * 
	 * @return The next serial number
	 */
	public BigInteger peekNextSerialCRLNumber() {
		return settings.getCRLSerial();
	}

	/**
	 * Get the collection of all Certificate Key Pair Templates.
	 * 
	 * @return A Collection of all Certificate Key Pair Templates.
	 */
	public Collection<ICertificateKeyPairTemplate> getCertificateKeyPairTemplates() {
		return templates.values();
	}

	/**
	 * Get the collection of all Certificate Requests.
	 * 
	 * @return A Collection of all Certificate Requests.
	 */
	public Collection<CertificateRequestProperties> getCertificateRequests() {
		return requests.values();
	}

	/**
	 * Get the collection of all Certificates Created/Issued.
	 * 
	 * @return A Collection of all Certificate Created/Issued.
	 */
	public Collection<IssuedCertificateProperties> getIssuedCertificates() {
		return issuedCertificates.values();
	}

	/**
	 * Get the collection of all Certificates Created/Issued that have been revoked.
	 * 
	 * @return A Collection of all Certificate Created/Issued that have been revoked.
	 */
	public Collection<IssuedCertificateProperties> getRevokedCertificates() {
		return revokedCertificates.values();
	}

	/**
	 * Get the collection of all CRLs.
	 * 
	 * @return A Collection of all CRLs
	 */
	public Collection<CRLProperties> getCRLs() {
		return crls.values();
	}

	/**
	 * Refresh from the underlying datastore. (Since this uses the filesystem to store all objects, the underlying
	 * filesystem may have changed).
	 * 
	 * @throws IOException If reading from the backing store fails.
	 */
	public synchronized void refresh() throws IOException {
		this.logger.log(Level.INFO, "Refreshing Certificate Authority Datastore");
		/*
		 * Issued...
		 */
		final AtomicReference<BigInteger> maxCertSerial = new AtomicReference<>(BigInteger.ZERO);
		Path path = basePath.resolve(ISSUED_PATH);
		List<IssuedCertificateProperties> oldValues = new ArrayList<>(issuedCertificates.values());
		Set<Path> oldPaths = new HashSet<>(issuedCertificates.keySet());
		final Set<Path> seenPaths = new HashSet<>();
		Files.list(path).parallel()//
				.filter(Files::isRegularFile)//
				.filter(p -> p.getFileName().toString().toLowerCase()
						.endsWith(IssuedCertificateProperties.DEFAULT_EXTENSION))
				.forEach(p -> issuedCertificates.computeIfAbsent(p, (x -> {
					try {
						seenPaths.add(x);
						IssuedCertificateProperties icp =  IssuedCertificateProperties.create(this, x);
						BigInteger serial = new BigInteger(icp.getProperty(IssuedCertificateProperties.Key.certificateSerialNumber));
						synchronized (maxCertSerial) {
							if (maxCertSerial.get().compareTo(serial) <= 0) {
								maxCertSerial.set(serial);
							}
						}
						return icp;
					} catch (IOException e) {
						return null;
					}
				})));
		// if seenPaths != oldPaths, we have an update.
		if (!seenPaths.equals(oldPaths)) {
			issuedCertificates.keySet().retainAll(seenPaths);
			propertySupport.firePropertyChange(PROPERTY_ISSUED, oldValues, issuedCertificates.values());
		}
		/*
		 * Revoked
		 */
		path = basePath.resolve(REVOKED_PATH);
		oldValues = new ArrayList<>(revokedCertificates.values());
		oldPaths = new HashSet<>(revokedCertificates.keySet());
		seenPaths.clear();
		Files.list(path).parallel()//
				.filter(Files::isRegularFile)//
				.filter(p -> p.getFileName().toString().toLowerCase()
						.endsWith(IssuedCertificateProperties.DEFAULT_EXTENSION))
				.forEach(p -> revokedCertificates.computeIfAbsent(p, (x -> {
					try {
						seenPaths.add(x);
						IssuedCertificateProperties icp =  IssuedCertificateProperties.create(this, x);
						BigInteger serial = new BigInteger(icp.getProperty(IssuedCertificateProperties.Key.certificateSerialNumber));
						synchronized (maxCertSerial) {
							if (maxCertSerial.get().compareTo(serial) <= 0) {
								maxCertSerial.set(serial);
							}
						}
						return icp;
					} catch (IOException e) {
						return null;
					}
				})));
		// if seenPaths != oldPaths, we have an update.
		if (!seenPaths.equals(oldPaths)) {
			revokedCertificates.keySet().retainAll(seenPaths);
			propertySupport.firePropertyChange(PROPERTY_REVOKED, oldValues, revokedCertificates.values());
		}
		// If our next  serial is less than what we have seen update the internal settings value.
		if (settings.getSerial() == null || settings.getSerial().compareTo(maxCertSerial.get()) <= 0) {
			settings.setSerial(maxCertSerial.get().add(BigInteger.ONE));
			saveSettings();
		}
		/*
		 * Requests
		 */
		path = basePath.resolve(REQUESTS_PATH);
		List<CertificateRequestProperties> requestsOldValues = new ArrayList<>(requests.values());
		oldPaths = new HashSet<>(requests.keySet());
		seenPaths.clear();
		Files.list(path).parallel()//
				.filter(Files::isRegularFile)//
				.filter(p -> p.getFileName().toString().toLowerCase()
						.endsWith(CertificateRequestProperties.DEFAULT_EXTENSION))
				.forEach(p -> requests.computeIfAbsent(p, (x -> {
					try {
						seenPaths.add(x);
						return CertificateRequestProperties.create(this, x);
					} catch (IOException e) {
						return null;
					}
				})));
		// if seenPaths != oldPaths, we have an update.
		if (!seenPaths.equals(oldPaths)) {
			requests.keySet().retainAll(seenPaths);
			propertySupport.firePropertyChange(PROPERTY_REQUESTS, requestsOldValues, requests.values());
		}

		/*
		 * Templates
		 */
		path = basePath.resolve(TEMPLATES_PATH);
		List<ICertificateKeyPairTemplate> templatesOldValues = new ArrayList<>(templates.values());
		oldPaths = new HashSet<>(requests.keySet());
		seenPaths.clear();
		Files.list(path).parallel()//
				.filter(Files::isRegularFile)//
				.filter(p -> p.getFileName().toString().toLowerCase()
						.endsWith(ICertificateKeyPairTemplate.DEFAULT_EXTENSION))
				.forEach(p -> templates.computeIfAbsent(p, (x -> {
					try {
						seenPaths.add(x);
						return ICertificateKeyPairTemplate.open(x);
					} catch (IOException e) {
						return null;
					}
				})));
		// if seenPaths != oldPaths, we have an update.
		if (!seenPaths.equals(oldPaths)) {
			templates.keySet().retainAll(seenPaths);
			propertySupport.firePropertyChange(PROPERTY_TEMPLATE, templatesOldValues, templates.values());
		}

		/*
		 * CRLs
		 */
		// Keep a tally of the largest CRL value...
		final AtomicReference<BigInteger> maxCRLSerial = new AtomicReference<>(BigInteger.ZERO);
		path = basePath.resolve(X509CRL_PATH);
		List<CRLProperties> crlsOldValues = new ArrayList<>(crls.values());
		oldPaths = new HashSet<>(crls.keySet());
		seenPaths.clear();
		Files.list(path).parallel()//
				.filter(Files::isRegularFile)//
				.filter(p -> p.getFileName().toString().toLowerCase().endsWith(CRLProperties.DEFAULT_EXTENSION))
				.forEach(p -> crls.computeIfAbsent(p, (x -> {
					try {
						seenPaths.add(x);
						CRLProperties crl = CRLProperties.create(this, x);
						BigInteger crlSerial = new BigInteger(crl.getProperty(CRLProperties.Key.crlSerialNumber));
						synchronized (maxCRLSerial) {
							if (maxCRLSerial.get().compareTo(crlSerial) <= 0) {
								maxCRLSerial.set(crlSerial);
							}
						}
						return crl;
					} catch (IOException e) {
						return null;
					}
				})));
		// If our next CRL serial is less than what we have seen update the internal settings value.
		if (settings.getCRLSerial() == null || settings.getCRLSerial().compareTo(maxCRLSerial.get()) <= 0) {
			settings.setCRLSerial(maxCRLSerial.get().add(BigInteger.ONE));
			saveSettings();
		}
		
		// if seenPaths != oldPaths, we have an update.
		if (!seenPaths.equals(oldPaths)) {
			crls.keySet().retainAll(seenPaths);
			propertySupport.firePropertyChange(PROPERTY_CRLS, crlsOldValues, crls.values());
		}
	}

	/**
	 * Get an iterator for search paths when looking for artefacts.
	 * 
	 * @return An iterator for search paths when looking for artefacts.
	 */
	public Iterator<Path> getSearchPaths() {
		return searchPaths.iterator();
	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescription();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((basePath == null) ? 0 : basePath.hashCode());
		result = prime * result + ((settings == null) ? 0 : settings.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CertificateAuthority other = (CertificateAuthority) obj;
		if (basePath == null) {
			if (other.basePath != null)
				return false;
		} else if (!basePath.equals(other.basePath))
			return false;
		if (settings == null) {
			if (other.settings != null)
				return false;
		} else if (!settings.equals(other.settings))
			return false;
		return true;
	}
	
	/**
	 * Get the given path as a string value.
	 * 
	 * @param path The path
	 * @return The path represented as a string. (If any elements are null, "" is returned).
	 */
	private static String getPathFilenameAsString(Path path) {
		if (path == null) {
			return "";
		}
		Path filename = path.getFileName();
		if (filename == null) {
			return "";
		}
		return filename.toString();
	}

	/**
	 * Get the activity logger instance.
	 * 
	 * @return The activity logger.
	 */
	public IActivityLogger getActivityLogger() {
		return logger;
	}

}
