
# Halimede Certificate Authority

Halimede is a simple to use Certificate Authority. It supports multiple CA (Certificate Authorities)
from a single interface, with each CA is stored within it's own datastore instance.

Halimede supports a large range of public key ciphers, including RSA, DSA, ECDSA (NIST/SEC/ANSI 
X9.62/Brainpool Curves), EdDSA (ED25519/ED448), GOST R34.10, DSTU 4145-2002 and numerous Post-Quantum 
Ciphers including Rainbow, SPHINCS-256, XMSS/XMSS-MT and qTESLA for X509 Certificate generation.

Halimede is available as either a Standalone Application, as a Plugin for the Eclipse IDE, or 
can be integrated with any other Eclipse e4 RCP based application.

## Features

* X509 Certificate Management
* Wide range of key ciphers, including RSA, DSA, EC, ED25519/ED448, GOST R34.10, DSTU4145, Rainbow, SPHINCS-256, XMSS/XMSS-MT and qTESLA
* X509 Certificate Templates
* Export Certificates and keying material in both PEM and DER formats.
* Create self-signed general purpose X509 Certificates.
* Certificate and keying material details exported as either HTML or Plain Text reports.
* Able to support definition of Key Usage, Extended Key Usage and Subject Alternative Names for Certificate generation, allowing defined usage of issued Certificates.
* Each CA is password protected, with the option that each issued certificate details are protected by individual independent passwords within the datastore.
* The datastore uses AES-256 cipher by default to protect Certificates and keying material.

## Standalone Requirements

* Operating Systems
    * Microsoft Windows 7, 8, 8.1 or 10 x64.
    * Apple macOS
    * GNU/Linux - Ubuntu 16.04LTS+, RHEL 7+, Debian 9+, Arch Linux
* Harwdare
    * 1GHz x86 64bit CPU
    * 2GB RAM
    * 100MB free HDD space

## Standalone Installation

* Windows - Use the MSI installer, or extract the application from the zip file.
* macOS - Use the PKG installer, or extract the App from the zip file and copy to Applications.
* GNU/Linux - Download the *.tar.gz file and extract to /opt/halimede

Stable downloads are available from <https://sourceforge.net/projects/halimede/files/1.0.0-20191217/>


## Eclipse IDE Plugin Requirements

* Eclipse Oxygen
* OpenJDK8 or Java 8 with the JCE Installed

Oracle Java 8 JCE: [Java JCE Download](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

Eclipse plugin can be used from within Eclipse via "Install New Software" with
the update site: <https://halimede.sourceforge.io/release/1.0.0>

**Missing Certificate**

On installation Eclipse IDE may indicate the Halimede plugins are not signed.
The Halimede plugins are signed using ECDSA NIST P-521 keys/signatures. 
However as of writing, Eclipse IDE currently ignores any signatures that are
not RSA based. The signature of the bundles can be verified with 
`$ jarsigner -verify` from the JDK.

## Eclipse IDE Plugin Installation

Simply place the plugin jars file into the /dropins or /plugins folder of your Eclipse 
installation, and restart Eclipse. Use the Window > CA Manager to open it.

Alternative, use the Install New Software dialog to install from the *.zip p2 bundle.

## Build

Run `$ mvn clean install` to build. The resulting JAR file is in the target folder.

Deployment artefacts will be located in: 

* `releng/net.sourceforge.dkartaschew.halimede.update/target` and 
* `releng/net.sourceforge.dkartaschew.halimede.product/target/products`

Note: This project will utilise the required Bouncy Castle Bundles directly 
from the local maven repository. If the required JARs are not available, build
errors may occur. To download the required JARs/Dependencies use:
`$ mvn dependency:get -DgroupId=org.bouncycastle -DartifactId=bcpkix-jdk15on -Dversion=1.64`

This is done, as while Eclipse Orbit has the required bundles, these tend to lag 3+ months behind releases made by The Legion of the Bouncy Castle.

## Java 9+

The plugin works fine with Java 9 (both Oracle Java 9 and OpenJDK-9), however the 
'java.se.ee' module needs to be available. (This is true for any Eclipse plugin running with Java 9). 

For Java 11+, the required bundles are automatically included in the build.
