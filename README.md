
# Certificate Authority

A simple Certificate Authority Plugin for Eclipse Oxygen or Eclipse e4 RCP applications.

## Requirements

* Eclipse Oxygen
* OpenJDK8 or Java 8 with the JCE Installed

Oracle Java 8 JCE: [Java JCE Download](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

## Installation

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
`$ mvn dependency:get -DgroupId=org.bouncycastle -DartifactId=bcpkix-jdk15on -Dversion=1.62`

This is done, as while Eclipse Orbit has the required bundles, these tend to lag 3+ months behind releases made by The Legion of the Bouncy Castle.

## Java 9+

The plugin works fine with Java 9 (both Oracle Java 9 and OpenJDK-9), however the 
'java.se.ee' module needs to be available. (This is true for any Eclipse plugin running with Java 9). 

For Java 11+, the required bundles are automatically included in the build.
