# Eclipse Plugin 

## Download

Halimede can be installed from a ZIP archive, or direct from the update p2 site.

### Update Site

Eclipse plugin can be used from within Eclipse via "Install New Software" with
the update site: <https://halimede.sourceforge.io/release/1.0.0>

### ZIP Archive

Alternatively, Halimede Eclipse Plugin can be downloaded from 
[Sourceforge](https://sf.net/p/halimede/files). Download the update site ZIP
file.

Use the "`Install new software`" dialog, and select the update site ZIP file as
the location source.

Select Halimede, and accept the license agreement. Halimede will now install.

## Eclipse Marketplace

Coming soon...

# Eclipse IDE

Halimede is available from the "`Windows`" menu, and utility functions are
available from the "`Halimede Utilies`" menu.

**Missing Certificate**

On installation Eclipse IDE may indicate the Halimede plugins are not signed.
The Halimede plugins are signed using ECDSA NIST P-521 keys/signatures. 
However as of writing, Eclipse IDE currently ignores any signatures that are
not RSA based. The signature of the bundles can be verified with 
`$ jarsigner -verify` from the JDK.



