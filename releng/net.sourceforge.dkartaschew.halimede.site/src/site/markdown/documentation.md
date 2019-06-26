# Halimede Certificate Authority Documentation

<img src="images/application-icon.svg" alt="Application Icon" width="20%" 
style="max-width:50px" />

Darran Kartaschew

## Table of Contents

1. [System Requirements](#System_Requirements)
2. [Installation](#Installation)
3. [Certificate Authority](#Certificate_Authority)
	1. [Create a new Certificate Authority](#Create_a_new_Certificate_Authority)
	2. [Settings](#Settings)
	3. [Create Certificate](#Create_Certificate)
	4. [Export Certificate](#Export_Certificate)
	5. [Revoke Certificate and CRLs](#Revoke_Certificate_and_CRLs)
	6. [Certificate Requests](#Certificate_Requests)
	7. [Certificate Templates](#Certificate_Templates)
	8. [Backup and Restoration](#Backup_and_Restoration)
4. [General Utilities](#General_Utilities)
	1. [Create Self-Signed Certificate](#Create_Self-Signed_Certificate)
	2. [View Certificate](#View_Certificate)

## System Requirements

The following are the system requirements for using Halimede:

| Operating System   | Minimum Version
|--------------------|---------------
| Microsoft Windows  | Windows 7, 8, 8.1 or 10 64bit\*
| Apple OS X / macOS | OS X 10.10 or newer
| GNU/Linux          | Ubuntu 16.04LTS or newer
|                    | Red Hat Enterprise Linux 7 or newer
|                    | Debian 9 or newer
|                    | Arch Linux

\* Note: 32bit OSes are **not** supported.

Minimum hardware requirements:

* 1GHz x86 64bit CPU
* 4GB RAM
* 100MB free HDD space

All systems:

* [Oracle Java 8u171+](https://java.com/en/download/) or
* [OpenJDK 8u171+](https://www.azul.com/downloads/zulu/)
* Java 9, 10 and 11 are supported as well.

Notes: Older versions of Oracle Java 8 are supported, however the 
[JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) 
may be required to be installed to use some ciphers. It is therefore
recommended that the latest version is installed and available.

Notes: Java 11 direct from Oracle requires a support agreement when used in
production environments. If you are to utilise Oracle Java 11, please you 
have the appropriate licenses to do so. Otherwise it is recommended to use 
OpenJDK from [Azul](https://www.azul.com/downloads/zulu/) or 
[AdoptOpenJDK](https://adoptopenjdk.net/).


## Installation

### Microsoft Windows

Download and run the MSI based installer. Halimede will be available from the
start menu. The MSI based installer includes OpenJDK 11 (from Azul).

### Apple OS X / macOS

Download and run the PKG installer. Halimede will be available from 
Applications. The PKG installer includes OpenJDK 11 (from Azul).

### GNU/Linux

Download the \*.tar.gz file, and extract to `/opt/halimede`. Copy the 
\*.desktop file to `/usr/local/share/applications`. Halimede should now be 
available from your DE application menu.

The \*.tar.gz file does **not** include a Java runtime environment. It is 
recommended to install OpenJDK 8 or OpenJDK 11 via your distributions package 
manager.

### Eclipse IDE Plugin

Download the \*.zip of the P2 Update Site, and Use the "`Install new software`" 
dialog, and select the update site ZIP file as the location source.

Select Halimede, and accept the license agreement. Halimede will now install.

## Certificate Authority

Halimede supports running one or Certificate Authorities (CA) at a time, using
a wide variety of ciphers for X509 Certificate generation.

Most functions are made available via right-click context menu interface.

<img src="images/doc_application.png" alt="Application Window" class="w3-card-4" width="100%" />

Most functions require the Certificate Authority to be unlocked, therefore if
a function is not available, please check the *lock* status of the Certificate
Authority. 

<img src="images/doc_lock.png" alt="Lock Icon" class="center w3-card-4" width="50%" />

To unlock a Certificate Authority, right-click on the Certificate Authority,
and select "Unlock". Halimede will prompt for the Certificate Authority's 
password.

### Create a new Certificate Authority

Halimede supports creating a new Certificate Authority either by creating a 
new Certificate and Keying Material *or* using existing Keying Material.

#### New Certificate Authority

To Create a new Certificate Authority, perform:

1. Right-Click on the left pane, and select "Create New Certificate Authority".
    <img src="images/doc_new_ca.png" alt="New CA Menu" width="100%" class="center w3-card-4" />
2. Enter the Certificate Authority details:
	1. The Name/Description.
	2. The Base Location for the Certificate Authority. This location will have
	the name of the Certificate Authority created as a folder, and the contents
	of the Certificate will reside in this sub-folder.
	3. The CA's X500 Name (or Common Name).
	4. The keying material type for the CA's Certificate.
	5. The Start and Expiry Date for the CA's Certificate.
	6. The public location for obtaining the latest CRL. (Optional, if not using
	or exporting CRLs).
	7. The Passphrase (entered twice).
    <img src="images/doc_new_ca_dialog.png" alt="New CA Dialog" width="100%" class="center w3-card-4" />
3. Click OK to create the Certificate Authority instance. This may take some
time depending on the keying material type choosen.

On completion the Certificate Authority will be listed in the left pane of the
application.
 
<img src="images/doc_ca.png" alt="CA" class="w3-card-4" width="100%" />
 
By default, the newly created Certificate Authority will be unlocked.

#### New Certificate Authority from Existing Certificate

To Create a new Certificate Authority based on an Existing Certificate, perform:

1. Right-click on the left pane, and select "Create New Certificate Authority
based on Existing Certificate".
    <img src="images/doc_new_ca2.png" alt="New CA Existing Menu" width="100%" class="center w3-card-4" />
2. Enter the Certificate Authority details:
  1. The Name/Description.
	2. The Base Location for the Certificate Authority. This location will have
	the name of the Certificate Authority created as a folder, and the contents
	of the Certificate will reside in this sub-folder.
	3. Select either the existing Certificate from a PKCS#12 store, or the X509
	Certificate and match Private Key.
	4. The Passphrase (entered twice).
     <img src="images/doc_new_ca2_dialog.png" alt="New CA Existing Dialog" width="100%" class="center w3-card-4" />
3. Click OK to create the Certificate Authority instance. It may prompt for the
passphrase to access the PKCS#12 or private key to complete the operation.
 
<img src="images/doc_ca2.png" alt="CA2" class="w3-card-4" width="100%" />

By default, the newly created Certificate Authority will be unlocked.

#### Open Certificate Authority

To Open an existing Certificate Authority, perform:

1. Right-click on the left pane, and select "Open a Certificate Authority".

    <img src="images/doc_open.png" alt="Open CA Menu" width="100%" class="center w3-card-4" />
2. Select the base location of the Certificate Authority in the dialog.
3. Click OK to open the Certificate Authority.

By default, the newly opened Certificate Authority will be locked. (Only limited
operations are permitted when the Certificate Authority is locked).

#### Close Certificate Authority

To close a Certificate Authority, perform:

1. Right-click on the Certificate Authority and select "Close Certificate 
Authority".
2. Click "OK" to perform the action.

The Certificate Authority will now be removed from the left pane.

#### Delete Certificate Authority

To delete a Certificate Authority, perform:

1. Right-click on the Certificate Authority and select "Delete Certificate 
Authority".
2. Click "OK" to perform the action.

The Certificate Authority will now be removed from the left pane, and deleted
from the filesystem. This action cannot be undone.

### Settings

Each Certificate Authority has a number of settings which may be altered by the
user. The following settings are available:

1. Certificate Authority Description.
    * The Description will be displayed in the Left Pane as the name of the
   Certificate Authority.
2. Default Certificate Expiry (for issued certificates).
    * This defines the number of days any issued Certificate will expire in 
	 from the start date. The user may freely adjust the Not Before and Not After
	 Dates during Certificate creation.
3. Default Signature Algorithm to utilise when signing Certificates.
    * Defines the Signature Algorithm to use when signing Certificates by this
	 Authority.
4. User Incremental or Timestamp based Serial numbers.
    * Halimede supports both Incremental and Timestamp based Serial Numbers for
	 signed Certificates. The Timestamp used is milliseconds from UNIX epoch 
	 (01-Jan-1970 00:00:00.000 UTC).

To modify the settings for each Certificate Authority, perform:

1. Unlock the Certificate Authority if locked.
2. Right-click on the Certificate Authority, and select "Certificate Authority
Settings".
3. Modify the Settings as required.
4. Click OK to update the settings. Or Cancel to cancel the operation.

<img src="images/doc_ca_settings.png" alt="CA Settings" class="center w3-card-4" />

### Create Certificate

Halimede supports creation of Certificates and associated keying material 
directly, or via a Certificate Signing Request. This section covers creation
of Certificates directly. See [Certificate Requests](#Certificate_Requests) for
Certificate creation via a Certificate Request.

To create a new Issued Certificate with associated keying material, perform:

1. Unlock the Certificate Authority if locked.
2. Right-click on the Certificate Authority or Issued node, and select
   "Create New Client Key/Certificate Pair".

	 <img src="images/doc_new_cert_menu.png" alt="CA2" width="100%" class="w3-card-4" />
	
This will open a new pane allowing entry of all required parameters for the
X509 Cerificate.

These parameters are:

1. Description. A simple description of X509 Certificate.
2. Subject. The Certificate's X500 Subject. A helper is available via the "..."
   button to the right of the field.
3. Key Type. The keying material to create for the certificate. This includes 
   creation of both a private and public key pair. Note: Some keying material 
	 may take significant time to generate. If this is the case, a warning will
	 be displayed on the field.
4. Start and Expiry Date. These are the Not Before and Not After Dates for the
   X509 Certificate. All date / times are UTC.
5. Flag to indicate to create a Certificate for an Intermediate Certificate
   Authority. Note: This Intermediate Certificate Authority will have a chain
	 length of 0, indicating that the Intermediate Certificate Authority may
	 sign Certificates, but may **not** create additional child Intermediate
	 Certificate Authorities.
6. CRL Location. The CRL Location of the Intermediate Certificate Authority 
   if the Certificate to be created is for an Intermediate Certificate 
   Authority.
7. Flags to indicate the Key Usage of the X509 Certificate. (If the Certificate
   to be created is for an Intermediate Certificate Authority, some of the Key
	 Usage flags will be automatically selected during Certificate Creation).
8. Flags to indicate the Extended Key Usage of the X509 Certificate.
9. Subject Alternate Names. 
    1. Click on the "+" icon to add a new Subject Alternate Name.
    2. Click on the "-" icon to remove a selected Subject Alternate Name.
    3. Double click on a Subject Alternate Name to edit.
    4. Each Subject Alternate Name needs the type selected, and the appropriate
		entry added. The most common types are:
        1. DNS. Primarily used for defining additional Domain Names for when
		    the Certificate is to be used for a HTTPS or similar service.
        2. Email. Primarily used to defined additional email addresses when
		    the Certificate is to be used for email signing.
        3. Directory Name. Primarily used to define additional objects located
		    in LDAP services for user authentication.
        4. IP Address. Used to define IP address of server offering services.
10. Flag to indicate if the Keying material to be stored with the same 
    passphrase as the Certificate Authority or is to use its own passphrase.
		If it's to be stored with its own passphrase, the passphrase must be
		entered twice.
11. To create the Keying Material and associated X509 Certificate click on the
    certificate icon in the top right corner, or from the drop down menu next 
		to the icon, select "Create and Issue the certificate".
12. The drop down menu, also allows you to save this certificate information as
    a template to be utilised later.

<img src="images/doc_new_certpane.png" alt="New Certificate Pane" class="w3-card-4" width="100%" />

Once the Keying Material and Certificate have been created, it will available
in the "Issued" node of the Certificate Authority which create/issued the
Certificate.

On the "Issued" node of the Certificate Authority, the Certificate/Keying 
Material can be view, the Certificate may be revoked, or additional comments
about the Keying Material/Certificate may be added/updated. (The comments/notes
are not exported as part of the X509 Certificate).

To view the contents of the Certificate/Keying Material, perform:

1. Right-click on the Certificate and select "View Certificate Details", or
2. Double-click on the Certificate.

<img src="images/doc_cert_view.png" alt="Certificate View" class="w3-card-4" width="100%" />

To view/edit the comment attached to a Certificate, perform:

1. Right-click on the Certificate and select "Update Comment".

<img src="images/doc_cert_comment.png" alt="Certificate Comment" class="center w3-card-4" />

### Export Certificate

Certificate and associated keying material can be exported from the Certificate
key store when viewing the Ceritifcate Information. The following actions are
available:

* Export the Certificate. 
* Export the Certificate and the complete Certificate Chain.
* Export the Public Key.
* Export the Private Key (if present).
* Export the Certificate, complete Certificate Chain and Private Key in a 
PKCS#12 file.
* Export the Certificate Information as either a Text or HTML file.

The Certificate and Keying material can be exported as either DER or PEM 
formats (where applicable).

All the above actions are available via the drop-down menu from the 
Certificate Icon when viewing the Certificate Information.

<img src="images/doc_cert_menu.png" alt="Certificate Menu" class="w3-card-4" width="100%" /> 

#### Export Certificate, Certificate Chain or Public Key

To export the Certificate, Certificate Chain or Public Key perform:

1. From the drop down menu, select the appropriate action.
2. Enter the filename to export to, or use the "..." to select the file name.
3. Select the encoding format, either PEM or DER format.
4. Select OK to save/export the information.

<img src="images/doc_cert_export1.png" alt="Certificate Export" class="center w3-card-4" />

#### Export Private Key

To export the Private Key (if present) perform:

1. From the drop down menu, select "Export the Private Key".
2. Enter the filename to export to, or use the "..." to select the file name.
3. Select the encoding format, either PEM or DER format.
4. Select the cipher to use to encrypt the private key with.
5. Enter the passphrase to protect the private key. (Note: If no passphrase
is entered, the private key will not be encrypted).
6. Select OK to save/export the private key.

<img src="images/doc_cert_export2.png" alt="Certificate Export" class="center w3-card-4" />

#### Export PKCS#12 Keystore

To export the Certificate, complete Certificate Chain and Private Key in a 
PKCS#12 file, perform:

1. From the drop down menu, select "Export as PKCS#12 Keystore".
2. Enter the filename to export to, or use the "..." to select the file name.
3. Select the cipher to use to encrypt the Keystore with. (Note: whilst the
use of 3DES for PKCS#12 is widely supported, support for using AES with 
PKCS#12 is limited).
4. Enter the passphrase to protect the keystore. (Note: If no passphrase
is entered, the keystore will not be encrypted).
5. Select OK to save/export the information into the keystore.

<img src="images/doc_cert_export3.png" alt="Certificate Export" class="center w3-card-4" />

### Revoke Certificate and CRLs

#### Revoke Certificate

Any Issued Certificate may be revoked at any time. There are two methods to
revoke the Certificate, either:

1. Right-click on the Issued Certificate from the Issued Certificates pane.
2. Use "Revoke the Certificate" menu option from the drop down menu when
viewing the Certificate.

On revoking the Certificate, a Revocation Reason is required. Select the 
reason from the drop down menu.

<img src="images/doc_cert_revoke.png" alt="Certificate Export" class="center w3-card-4" />

On completion the Certificate will be moved from "Issued" certificates to
"Revoked" certificates.

<img src="images/doc_cert_revoked.png" alt="Revoked Certificate View" class="w3-card-4" width="100%" />

#### Certification Revocation List (CRL)

Halimede can produce a Certificate Revocation List (CRL) on demand. To produce
a CRL for the selected Certificate Authority, perform:

1. Right-click on the Certificate Authority or CRLs node, and select "Create
CRL".
2. Update or confirm the next CRL date.
3. Click OK to generate the CRL.

<img src="images/doc_new_crl.png" alt="New CRL" class="center w3-card-4" />

Once the CRL has been generate the CRL View will open displaying the CRL just
generated.

Note: Halimede currently has no support for generating delta CRLs. Only
complete CRLs can be generated.

<img src="images/doc_crl_view.png" alt="CRL View" class="w3-card-4"  width="100%" />

To export the CRL, perform:

1. Click on the certificate button (top right), or via the drop down, select 
"Export the CRL".
2. Enter the filename to export to, or use the "..." to select the file name.
3. Select the encoding format, either PEM or DER format.
4. Select OK to save/export the information.
5. Upload / copy the file to the location as defined in the CRL Location
defined when the Certificate Authority was create.

<img src="images/doc_crl_export.png" alt="CRL Export" class="center w3-card-4" />

The CRL can also be saved as a Text or HTML file from the drop down menu on
the certificate icon when viewing the CRL.

Once a CRL has been generated, it will be available via the CRLs node in
the Certificate Authority. To open the CRL, right-click on the CRL, and select
"View CRL". Additionally, a CRL may have a comment attached, to update the
comment, right-click on the CRL, and select "Update Comment".

<img src="images/doc_crl_comment.png" alt="CRL Comment" class="center w3-card-4" />

**CRL Policy**

IETF [RFC 5280](https://tools.ietf.org/html/rfc5280) covers expected policy
on the usage of CRLs with Certificate Authorities. It is expected that the user
follow the policies and guidelines within RFC 5280. 

In the general case, it is expected that:

1. On creation of the Certificate Authority, a CRL location is defined.
2. On creation of the Certificate Authority, create a CRL and publish to the
CRL location defined. This will set the expected next update time.
3. On or before the next update time, generate a new CRL and publish. CRLs
can be generated at any time, on provision that it is always before the next
update time noted in the prior CRL.

### Certificate Requests

Halimede supports the import of external Requests for Certificate (also known
as a Certificate Signing Request (CSR)). Once the CSR has been imported, a
Certificate can be issued based on the information in the CSR.

To import a CSR, perform:

1. Right-click on the "Pending" node under the Certificate Authority and select
"Import a Certificate Request".
2. Select the Certificate Signing Request (CSR).
3. The CSR will be imported.

<img src="images/doc_new_csr.png" alt="New CSR" class="center w3-card-4" />

To view a CSR once imported, perform:

1. Double-click on the CSR in the Pending Pane, or
2. Right-click on the CSR, and select "View Certificate Details".

A new pane will open displaying the Certificate Signing Request details.

<img src="images/doc_csr_view.png" alt="CSR View" class="w3-card-4"  width="100%" />

To issue a Certificate based on the CSR, perform:

1. Right-click on the CSR, and select "Create new Certificate", or
2. if viewing the CSR, click on the certificate icon, and select "Issue
Certificate".
3. This will open a pane very similar to the information pane used when 
creating a new certificate.
4. Set or add any additional information, and click on the Certificate Icon
in the top right to issue the certificate. The resulting certificate can
be exported as per instructions above.

<img src="images/doc_csr_menu.png" alt="CSR Menu" class="w3-card-4"  width="100%" />

Note: Not all X509 Certificate extensions that are defined within the CSR may
be supported by Halimede, therefore may not be present in the resulting issued
Certificate.

### Certificate Templates

Halimede during Certificate Creation, can create a Template based on the current
input in Certificate Details pane.

To create a new Template, perform:

1. Right-click on the "Templates" node under the Certificate Authority, and 
select "Create a new Client Key/Certificate Template".
2. This will open a new Certificate Template pane.

<img src="images/doc_new_template.png" alt="Template Menu" class="w3-card-4" width="100%" /> 

Enter details as needed, and to save, click on the Certificate Icon in the 
top right corner.

To issue a new Certificate, based on a Template, perform:

1. Double-click on the Template, or
2. Right-click on the Template and select "Create New Certificate"

This will open a New Certificate pane, pre-filled from details from the
template.

<img src="images/doc_template_pane.png" alt="Template Pane" class="w3-card-4" width="100%" /> 

To edit or delete a Template, right-click on the template, and select
the appropriate option from the menu.

### Backup and Restoration

Halimede provides simple methods to backup and restore the Certificate
Authority to/from ZIP files. 

To backup the Certificate Authority, perform:

1. Right-click on the Certificate Authority, and select "Backup Certificate Authority".
2. Select the file to backup to, and click on "Save".

A progress dialog will be displayed during the operation.

To restore a Certificate Authority, perform:

1. Right-click on the left pane, and select "Restore Certificate Authority".
2. On the Restore Dialog, select the backup file name, the location to restore to,
and if you wish to automatically open the Certificate Authority on completion.
3. Click OK to restore from the Backup File.

A progress dialog will be displayed during the operation.

#### Alternative method

All data files for each Certificate Authority are contained with the single
nominated folder which hosts the Certificate Authority.

Therefore backup can be performed by simply archiving/copying the folder.

Note: Recovery of passwords for any keying material for a Certificate 
Authority is currently not possible. (Unless you know of a method of 
decrypting a PKCS#12 container utilising AES256).

## General Utilities

Halimede includes a number of general purpose utility functions. These include
and not limited to, viewing X509 Certificates, viewing keying material, 
viewing external CRLs and creating self signed Certificates.

### Create Self-Signed Certificate

Halimede can create a Self-Signed Certificate for general purpose use.

To create a self-signed Certificate, perform:

1. From the "Tools" menu, select "Create Self-Signed Certificate".
2. Enter details for the Certificate.
3. Click on the Certificate Icon (top right corner) and select "Create
Certificate".
4. Once created, a Certificate View will be displayed.
5. The keying material and Certificate have **not** been saved to disk.
6. To save the new Certificate and Keying Material, click on the context menu
of the Certificate Icon (top right corner) and select "Export as PKCS#12
Keystore". 
    1. Enter the filename to export to, or use the "..." to select the file 
		name.
    2. Select the cipher to use to encrypt the Keystore with. (Note: whilst the
    use of 3DES for PKCS#12 is widely supported, support for using AES with 
    PKCS#12 is limited).
    3. Enter the passphrase to protect the keystore. (Note: If no passphrase
    is entered, the keystore will not be encrypted).
    4. Select OK to save/export the information into the keystore.
7. You can export other elements of the generated Certificate and Keying 
material from the Certificate Icon drop down menu.

<img src="images/doc_new_selfcert.png" alt="Self Signed Pane" class="w3-card-4" width="100%" /> 
<br />
<br />
<img src="images/doc_selfcert_view2.png" alt="Self Signed View" class="w3-card-4" width="100%" /> 

### View Certificate

To view, a X509 Certificate, keying material (contained in a PKCS#12 container),
a CRL or CSR, select the applicable menu option from the "Tools" menu and 
select the desired file. If a passphrase is required, a prompt for the 
passphrase will be displayed.

The resulting file will be displayed in the applicable view pane. Once open,
various options are made available via the Certificate Icon in the top right 
corner, including:

1. Export as HTML or TXT report file.
2. Export keying material.
3. Export Certificate or Certificate Chain
4. If a CRL, re-export the CRL in a different format. (Useful for converting
between PEM and DER formats).

Note: Options available will be based on the file being opened.

