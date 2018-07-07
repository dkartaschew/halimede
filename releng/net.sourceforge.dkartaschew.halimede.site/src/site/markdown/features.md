# Features and Technical Specification

## Features

Halimede is available on the following environments:

* Standalone Application (Microsoft Windows, Apple macOS and GNU/Linux).
* Plugin for the Eclipse IDE.
* Integration with Eclipse e4 RCP based applications.

Halimede support multiple CA (Certificate Authorities) from a single interface, 
with each CA is stored within it's own datastore instance.

Halimede supports a large range of public key ciphers, including RSA, DSA, 
ECDSA (NIST/SEC/ANSI X9.62/Brainpool Curves), GOST R34.10, DSTU 4145-2002 
and numerous Post-Quantum Ciphers for X509 Certificate generation. Full 
list of supported ciphers is listed under 
[Supported Asymmetric Ciphers](#Supported_Asymmetric_Ciphers) below.

Halimede supports the following functions:

* Creation of CA with either new or existing keying material.
* Certificate Templates.
* Import and Issuing external Certificate Requests.
* Certificate Revocation and issuing Certificate Revocation Lists (CRLs).
* Export Certificates and keying material in both PEM and DER formats.
* Create self-signed general purpose X509 Certificates.
* Certificate and keying material details exported as either HTML or Plain Text
reports.

Additional features:

* Able to support definition of Key Usage, Extended Key Usage and Subject
Alternative Names for Certificate generation, allowing defined usage of 
issued Certificates.
* Each CA is password protected, with the option that each issued certificate
details are protected by individual independent passwords with the datastore.
* The datastore uses AES-256 cipher by default to protect Certificates and
keying material.

### Planned Features

The following list of features are planned for future versions of Halimede.

* Export keying material and certificates for use with OpenSSH.
* Export keying material and certificates to a SmartCard for authentication. 
* Additional ciphers as they are made available.

## Technical Specification

### Runtime Requirements

Halimede operates as a e4 RCP plugin designed to be operated in an Eclipse 
Oxygen environment.

Halimede requires a minimum of a Java 8 environment and is tested with Oracle 
Java 8 and OpenJDK 8, 9 and 10.

Note: For Oracle Java 8, the
[JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html) 
may be required for some ciphers to be available. Oracle Java 8u161 includes
and activates the JCE by default, therefore it is recommended at least this
version is installed and made available.

### Design Rational

Halimede primarily has been designed for use by Software Developers to operate
a small self-contained CA for use in generation of X509 Certificates and 
related keying material. It is also suitable for use by individuals in managing 
a small CA for personal use.

In searching for methods of running a CA, there are effectively two methods:

1. Run CA software designed for an Enterprise installation, eg Microsoft CA 
Server, DogTag, EJBCA, etc.
2. Build a set of scripts utilising OpenSSL.

The former is typically not suitable for small installations due to the
additional requirements (typically a database server).

The latter is suitable for small installations, however has a steep learning
curve in understanding how OpenSSL can be used to run a CA. Additionally,
using OpenSSL assumes a solid grasp of X509 Certificates and all associated
options.

Halimede has also been designed to use an open and well documented datastore 
format, allowing users to be be able to export any information from the 
datastore without the use of Halimede itself. 

As Halimede has been designed for small installations, some features available
with other CA software are not offered by default.

### Supported Asymmetric Ciphers

Halimede supports the following Asymmetric Ciphers for use with X509 
Certificates:

#### RSA and DSA

| Key Pair Algorithm   | Key Size (bits)       | Signature Algorithm    |
|----------------------|-----------------------|------------------------|
|DSA	               |512 - 3072             |SHA-1 with DSA          |
|                      |                       |SHA-224 with DSA        |
|                      |                       |SHA-256 with DSA        |
|                      |                       |SHA-384 with DSA        |
|                      |                       |SHA-512 with DSA        |
|                      |                       |SHA3-224 with DSA        |
|                      |                       |SHA3-256 with DSA        |
|                      |                       |SHA3-384 with DSA        |
|                      |                       |SHA3-512 with DSA        |
|RSA	               |512 - 16384	           |MD2 with RSA            |
|                      |                       |MD5 with RSA            |
|                      |                       |RIPEMD-128 with RSA     |
|                      |                       |RIPEMD-160 with RSA     |
|                      |                       |RIPEMD-256 with RSA     |
|                      |                       |SHA-1 with RSA          |
|                      |                       |SHA-224 with RSA        |
|                      |                       |SHA-256 with RSA        |
|                      |                       |SHA-384 with RSA \*     |
|                      |                       |SHA-512 with RSA \*\*   |

\* - Requires an RSA key size of at least 624 bits

\*\* - Requires an RSA key size of at least 752 bits

#### EC / ECDSA

| Curve Set      | Curves               |
|----------------|----------------------|
| SEC | secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, secp256r1, secp384r1, secp521r1, sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1 |
| NIST | B-163, B-233, B-283, B-409, B-571, K-163, K-233, K-283, K-409, K-571, P-192, P-224, P-256, P-384, P-521 |
| ANSI X9.62 | c2pnb163v1, c2pnb163v2, c2pnb163v3, c2pnb176w1, c2pnb208w1, c2pnb272w1, c2pnb304w1, c2pnb368w1, c2tnb191v1, c2tnb191v2, c2tnb191v3, c2tnb239v1, c2tnb239v2, c2tnb239v3, c2tnb359v1, c2tnb431r1, prime192v1, prime192v2, prime192v3, prime239v1, prime239v2, prime239v3, prime256v1 |
| TeleTrusT | brainpoolP160r1, brainpoolP160t1, brainpoolP192r1, brainpoolP192t1, brainpoolP224r1, brainpoolP224t1, brainpoolP256r1, brainpoolP256t1, brainpoolP320r1, brainpoolP320t1, brainpoolP384r1, brainpoolP384t1, brainpoolP512r1, brainpoolP512t1 |
| ANSI | FRP256v1 |
| GM | sm2p256v1, wapip192v1 |

All ECDSA can use the following Signature Algorithms:

* ECDSA with SHA1
* ECDSA with SHA224
* ECDSA with SHA256
* ECDSA with SHA384
* ECDSA with SHA512
* ECDSA with SHA3-224
* ECDSA with SHA3-256
* ECDSA with SHA3-384
* ECDSA with SHA3-512

#### GOST R34.10

|	Revision      | Algorithm / Curves     | Signature Algorithm     |
|-----------------|------------------------|-------------------------|
|GOST R34.10-94   | CryptoPro-A            | GOST R34.11 with GOST R34.10 |
|                 | CryptoPro-B            |                         |
|                 | CryptoPro-XchA         |                         | 
|GOST R34.10-2001 | CryptoPro-A            | GOST R34.11 with ECGOST R34.10 |
|                 | CryptoPro-B            |                         |
|                 | CryptoPro-C            |                         | 
|                 | CryptoPro-XchA         |                         |
|                 | CryptoPro-XchB         |                         | 
|GOST R34.10-2012 | Tc26-Gost-3410-12-256-paramSetA | GOST R34.11-2012-256 with ECGOST R34.10-2012-256 |
|                 | Tc26-Gost-3410-12-512-paramSetA | GOST R34.11-2012-512 with ECGOST R34.10-2012-512 |
|                 | Tc26-Gost-3410-12-512-paramSetB |                |
|                 | Tc26-Gost-3410-12-512-paramSetC |                |

#### DSTU 4145-2002

|	Revision      | Algorithm / Curves           | Signature Algorithm       |
|-----------------|------------------------------|---------------------------|
|DSTU 4145-2002   | 1.2.804.2.1.1.1.1.3.1.1.2.0  | GOST R34.11 with DSTU4145 |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.1  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.2  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.3  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.4  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.5  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.6  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.7  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.8  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.9  |                           |
|                 | 1.2.804.2.1.1.1.1.3.1.1.2.10 |                           |


#### Post Quantum Ciphers

Coming soon...

### Datastore Format

Halimede uses a simple datastore format.

All issued Certificates and Keying material are stored in individual PKCS12 
files (DES encoded), utilising AES256 for encipherment.

The base datastore format is:

```
<CA NAME> (Folder)
|- configuration.xml       (CA Configuration Information)
|- ca.p12                  (CA Certificate and Keying material - PKCS12 - DES)
|
|- CRLs                    (Folder containing all issued CRLs).
|   |- <issue num>.crl     (CRL - DES encoding)
|   |- <issue num>.crlprop (CRL Properties)
|   |- ....
|
|- Issued                  (Folder containing all issued Certificates)
|   |- <issue date>.p12    (Issued Certificate and Keying material - PKCS12 - DES)
|   |- <issue date>.prop   (Issued Certificate Properties).
|   |- ....
|
|- Requests                (Folder containing all external Certificate Requests).
|   |- <import num>.csr     (CSR - PKCS10 - DES encoding)
|   |- <import num>.csrprop (CSR Properties)
|   |- ....
|
|- Revoked                 (Folder containing all Revoked Certificates).
|   |- <issue date>.p12    (Revoked Certificate and Keying material - PKCS12 - DES)
|   |- <issue date>.prop   (Revoked Certificate Properties)
|   |- ....
|
|- Templates               (Folder containing all Templates).
    |- <create date>.xml   (Certificate Template)
    |- ....

```

Most items are stored in pairs, a PKCS12 file (or PKCS10 file in the case of
a stored Certificte Request) and associated properties file. The 
properties file contains information that is used to populate the UI with
key information, but does not contain certificates or keying material.

All PKCS12 files contain the full X509 Certificate path, therefore are
largely self contained with keying material.

Each component is stored individually to ensure corruption to any individual
file doesn't affect the datastore as a whole. Additionally it allows each
issued Certificate PKCS12 file to utilise independent passwords for 
protection.

Most XML and Property file formats are subject to change, however the 
properties contained should remain fairly static in nature. 

The contents of the XML and properties file are largely self-documented.

For data stored in byte array formats, these are base64 encoded before storage. 
Some fields in particular Subject, KeyUsage and Subject Alternate Names are 
stored in ASN.1 format when required (base64 encoded).

