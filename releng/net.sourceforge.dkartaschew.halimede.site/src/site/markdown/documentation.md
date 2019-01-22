# Halimede Certificate Authority Documentation

INSERT LOGO.

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

Most functions require the Certificate Authority to be unlocked, therefore if
a function is not available, please check the *lock* status of the Certificate
Authority. 

INSERT EXAMPLE IMAGE OF LOCK AND UNLOCK.

To unlock a Certificate Authority, right-click on the Certificate Authority,
and select "Unlock". Halimede will prompt for the Certificate Authority's 
password.

### Create a new Certificate Authority

Halimede supports creating a new Certificate Authority either by creating a 
new Certificate and Keying Material *or* using existing Keying Material.

#### New Certificate Authority

To Create a new Certificate Authority, perform:

1. Right-Click on the left pane, and select "Create New Certificate Authority".
 
 INSERT IMAGE

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

 INSERT IMAGE OF DIALOG.
 
3. Click OK to create the Certificate Authority instance. This may take some
time depending on the keying material type choosen.

On completion the Certificate Authority will be listed in the left pane of the
application.
 
INSERT IMAGE OF COMPLETED.

By default, the newly created Certificate Authority will be unlocked.

#### New Certificate Authority from Existing Certificate

To Create a new Certificate Authority based on an Existing Certificate, perform:

1. Right-click on the left pane, and select "Create New Certificate Authority
based on Existing Certificate".

 INSERT IMAGE

2. Enter the Certificate Authority details:
  1. The Name/Description.
	2. The Base Location for the Certificate Authority. This location will have
	the name of the Certificate Authority created as a folder, and the contents
	of the Certificate will reside in this sub-folder.
	3. Select either the existing Certificate from a PKCS#12 store, or the X509
	Certificate and match Private Key.
	4. The Passphrase (entered twice).

  INSERT IMAGE OF DIALOG

3. Click OK to create the Certificate Authority instance. It may prompt for the
passphrase to access the PKCS#12 or private key to complete the operation.
 
INSERT IMAGE OF COMPLETED.

By default, the newly created Certificate Authority will be unlocked.

#### Open Certificate Authority

To Open an existing Certificate Authority, perform:

1. Right-click on the left pane, and select "Open a Certificate Authority".

INSERT IMAGE

2. Select the base location of the Certificate Authority in the dialog.
3. Click OK to open the Certificate Authority.

By default, the newly opened Certificate Authority will be locked. (Only limited
operations are permitted when the Certificate Authority is locked).

### Settings

### Create Certificate

### Export Certificate

### Revoke Certificate and CRLs

### Certificate Requests

### Certificate Templates

### Backup and Restoration

All data files for each Certificate Authority are contained with the single
nominated folder which hosts the Certificate Authority.

Note: Recovery of passwords for any keying material for a Certificate 
Authority is currently not possible. (Unless you know of a method of 
decrypting a PKCS#12 container utilising AES256).

## General Utilities

### Create Self-Signed Certificate

### View Certificate

