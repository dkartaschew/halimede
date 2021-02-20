# Halimede Application 

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

* [Oracle Java 11+](https://java.com/en/download/) or
* [OpenJDK 11+](https://www.azul.com/downloads/zulu/)

## Download

Halimede can be downloaded from [Sourceforge](https://sf.net/p/halimede/files)

## Installation

### Microsoft Windows

Download and run the MSI based installer. Halimede will be available from the
start menu.

### Apple OS X / macOS

Download and run the PKG installer. Halimede will be available from 
Applications.

### GNU/Linux

Download the \*.tar.gz file, and extract to `/opt/halimede`. Copy the 
\*.desktop file to `/usr/local/share/applications`. Halimede should now be 
available from your DE application menu.

## Portable Installation

Halimede once installed, can be converted into a Portable Installation.

Note: This is an advanced configuration option. We make NO
guarantees about the following instructions and are provided as best
effort. Always TEST and CONFIRM your portable installation before using
it in the field.

Note: Portable Installations **do not work** on Apple OS X or macOS based
installations. This is due to App Translocation. (There are hacks
around App Translocation, but none are supported).

To convert Halimede to a Portable Installation, perform the following steps:

* Format a new USB Key or External HDD with at least 8GB of available
space.
* Copy the entire contents of the C:\Program Files\Halimede folder to
a new Folder on the USB Key/External HDD.
* On the USB Key/External HDD, open halimede.ini in a text
editor and modify the -data= and -Dosgi.configuration.area= values
to define locations on the USB key. (Hint: Don't use Drive Letters
to define the path, relative paths should work fine).
* Create a folder called `jre` in the Folder which Halimede resides in on the
USB/External HDD.
* Copy our preferred JRE installation into the `jre` folder created in the
previous step.  
* Test the Portable Installation on a machine to ensure that the
preference files, logging files and any temporary files are written
to the location defined on the USB key/External HDD.

