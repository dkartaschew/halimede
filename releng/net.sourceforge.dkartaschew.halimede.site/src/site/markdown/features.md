# Features and Technical Specification

## Features

Halimede is available on the following environments:

* Standalone Application (Microsoft Windows, Apple macOS and GNU/Linux).
* Plugin for the Eclipse IDE.
* Integration with Eclipse e4 RCP based applications.

Halimede support multiple CA (Certificate Authorities) from a single interface, 
with each CA is stored within it's own datastore instance.

Halimede supports a large range of public key ciphers, including RSA, DSA, 
ECDSA (NIST/SEC/ANSI X9.62/Brainpool Curves), GOST R34.10, DSTU 4145-2002,
EdDSA Ed25519 and Ed448 and numerous Post-Quantum Ciphers for X509 
Certificate generation. Full list of supported ciphers is listed under 
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
2020-12 environment.

Halimede requires a minimum of a Java 11 environment and is tested with OpenJDK 11.

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

* SHA1 with ECDSA
* SHA224with ECDSA
* SHA256 with ECDSA
* SHA384 with ECDSA
* SHA512 with ECDSA
* SHA3-224 with ECDSA
* SHA3-256 with ECDSA
* SHA3-384 with ECDSA
* SHA3-512 with ECDSA

NOTE: sm2p256v1 uses the "SM3 with SM2" Signature Algorithm.

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

#### EdDSA

| Revision | Signature Algorithms |
|----------|----------------------|
| Ed25519  | Ed25519              |
| Ed448    | Ed448                |

#### Rainbow

| Revision | Signature Algorithms |
|----------|----------------------|
| Rainbow  | SHA224 with RAINBOW  |
|          | SHA256 with RAINBOW  |
|          | SHA384 with RAINBOW  |
|          | SHA512 with RAINBOW  |

#### SPHINCS-256

| Revision               | Signature Algorithms      |
|------------------------|---------------------------|
| SPHINCS-256 - SHA512   | SHA512 with SPHINCS-256   |
| SPHINCS-256 - SHA3-512 | SHA3-512 with SPHINCS-256 |

#### XMSS and XMSS-MT

Halimede supports all configurations as noted in IETF 
[RFC 8391](https://tools.ietf.org/html/rfc8391).

| Revision               | Name                   | Signature Algorithms      |
|------------------------|------------------------|---------------------------|
| XMSS                   | XMSS-SHA2_10_256       | SHA256 with XMSS          |
|                        | XMSS-SHA2_16_256       | SHA512 with XMSS          |
|                        | XMSS-SHA2_20_256       | SHAKE128 with XMSS        |
|                        | XMSS-SHA2_10_512       | SHAKE256 with XMSS        |
|                        | XMSS-SHA2_16_512       |                           |
|                        | XMSS-SHA2_20_512       |                           |
|                        | XMSS-SHAKE_10_256      |                           |
|                        | XMSS-SHAKE_16_256      |                           |
|                        | XMSS-SHAKE_20_256      |                           |
|                        | XMSS-SHAKE_10_512      |                           |
|                        | XMSS-SHAKE_16_512      |                           |
|                        | XMSS-SHAKE_20_512      |                           |
| XMSS-MT                | XMSSMT-SHA2_20/2_256   | SHA256 with XMSS-MT       |
|                        | XMSSMT-SHA2_20/4_256   | SHA512 with XMSS-MT       |
|                        | XMSSMT-SHA2_40/2_256   | SHAKE128 with XMSS-MT     |
|                        | XMSSMT-SHA2_40/4_256   | SHAKE256 with XMSS-MT     |
|                        | XMSSMT-SHA2_40/8_256   | |
|                        | XMSSMT-SHA2_60/3_256   | |
|                        | XMSSMT-SHA2_60/6_256   | |
|                        | XMSSMT-SHA2_60/12_256  | |
|                        | XMSSMT-SHA2_20/2_512   | |
|                        | XMSSMT-SHA2_20/4_512   | |
|                        | XMSSMT-SHA2_40/2_512   | |
|                        | XMSSMT-SHA2_40/4_512   | |
|                        | XMSSMT-SHA2_40/8_512   | |
|                        | XMSSMT-SHA2_60/3_512   | |
|                        | XMSSMT-SHA2_60/6_512   | |
|                        | XMSSMT-SHA2_60/12_512  | |
|                        | XMSSMT-SHAKE_20/2_256  | |
|                        | XMSSMT-SHAKE_20/4_256  | |
|                        | XMSSMT-SHAKE_40/2_256  | |
|                        | XMSSMT-SHAKE_40/4_256  | |
|                        | XMSSMT-SHAKE_40/8_256  | |
|                        | XMSSMT-SHAKE_60/3_256  | |
|                        | XMSSMT-SHAKE_60/6_256  | |
|                        | XMSSMT-SHAKE_60/12_256 | |
|                        | XMSSMT-SHAKE_20/2_512  | |
|                        | XMSSMT-SHAKE_20/4_512  | |
|                        | XMSSMT-SHAKE_40/2_512  | |
|                        | XMSSMT-SHAKE_40/4_512  | |
|                        | XMSSMT-SHAKE_40/8_512  | | 
|                        | XMSSMT-SHAKE_60/3_512  | |
|                        | XMSSMT-SHAKE_60/6_512  | |
|                        | XMSSMT-SHAKE_60/12_512 | |

#### qTESLA

| Revision          | Signature Algorithms      |
|-------------------|---------------------------|
| qTESLA-p-I        | qTESLA-p-I                |
| qTESLA-p-III      | qTESLA-p-III              |

Note: qTESLA was recently updated as per Round2 revisions. (qTESLA-I was removed).

#### Cipher Restriction

Users and System Administrators can limit the types of Asymmetric Ciphers made
available to users based on corporate policies. This is achieved via setting a 
Java System Property.

The System Property used to define the allowed ciphers is 
"```net.sourceforge.dkartaschew.halimede.keytype.allow```". Ciphers can be 
defined either by the type/family of cipher, or by the name of the cipher. An 
asterisk can be appended to the name to select all starting with, and a '-' 
can be prepended to the name to act as a negation option.

For example:

* "```EC*```" will select all ECDSA ciphers to be made available.
* "```* -RSA* -DSA*```" will select all ciphers, except all RSA and DSA ciphers.
* "```RSA -RSA_512 -RSA_1024```" will select all RSA ciphers except RSA 512 and 
RSA 1024. (Allow all RSA ciphers with a key strength of at least 2028 bits).

Note: If using the name, this is based on the internal name of the cipher.
For the complete list, see KeyType enumeration for all values.

The System Property used to define the default cipher is 
"```net.sourceforge.dkartaschew.halimede.keytype.default```". This should be 
set to one of the ciphers defined in the Allow set. If this property is not 
set, then the first value from the allowed set is selected.

Note: These settings are system global and apply to all Certificate Authorities
currently in use.

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

### Backup File Format

The default Backup file format is a simple Zip container with the Certificate
Authority contents included. (All entries are compressed).

An additional `manifest.xml` file is included containing a complete
manifest of all files, including file sizes and SHA512 digests for integrity.

The Zip container will also have a Zip Comment equal to the UUID of the 
Certificate Authority to add as an additional marker as a valid backup
file.

