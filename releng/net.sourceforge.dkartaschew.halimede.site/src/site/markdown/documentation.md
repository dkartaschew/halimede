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

### Create a new Certificate Authority

### Settings

### Create Certificate

### Export Certificate

### Revoke Certificate and CRLs

### Certificate Requests

### Certificate Templates

### Backup and Restoration

## General Utilities

### Create Self-Signed Certificate

### View Certificate

