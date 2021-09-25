# FAQ

**Q. Halimede**

A. Halimede is named after one of the moons of Neptune.

-----------------

**Q. License**

A. Halimede is distributed under the EPL 2.0 license with GPL v2+ w/Class Path 
Exception Secondary License. Please see [License](license.html) for full 
details.

-----------------

**Q. You mention the GPL (GNU General Public License) as a secondary license?**

A. The EPL 2.0 and GPL have been deemed 
[incompatible licenses](https://www.eclipse.org/legal/epl-2.0/faq.php), 
therefore Halimede is provided with a GPL v2+ w/Class Path Exception Secondary 
license to allow Applications licensed under the GPL to utilise Halimede freely 
and easily. 

-----------------

**Q. Supported Java Versions?**

A. Halimede is built/tested against OpenJDK 11.

-----------------

**Q. How can I get XXX cipher added/supported by Halimede?**

A. Halimede utilises the excellent 
[Bouncycastle](https://www.bouncycastle.org/java.html) library for most
cryptographic based operations. The best and prefered method would be to
get Bouncycastle to support the cipher in question.

-----------------

**Q. Halimede doesn't seem to support DSA key lengths greater than 3072bit?**

A. [NIST FIPS 186-4](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.186-4.pdf)
Section 4.2 only defines bit lengths upto 3072bits. Whilst other crypto 
libraries support DSA key lengths greater than 3072, support for those lengths
is considered non-standard. (Bouncycastle have chosen to only offer
supported lengths with DSA).

Note: OpenSSH has deprecated the use of DSA/DSS keys in favour of ECDSA.
Continued support for DSA in the wider infosec community may have a short life
moving forward.

-----------------

**Q. The generated certificates don't seem to work with my application?**

A. When generating Certificates and Keying material you need to very mindful
of the application which they are to be used. For example many applications
don't support GOST R34.10 or any of the Post Quantum Ciphers Halimede supports.

Additional some applications don't support all options for certain ciphers as
well. For example:

* Google Chrome (as of writing) only supports NIST P-192, NIST P-256 and NIST 
P-384 ECDSA keys for TLS 1.2 support.   
* Eclipse Oxygen (4.7.3) only supports RSA and DSA for SSH/GIT access. (As a 
workaround, you need to tell Eclipse use an external git/ssh provider).

-----------------

**Q. If applications don't support all these options, why offer them?**

A. Essentially to help drive adoption of these alternate ciphers. (To assist 
in solving the "Chicken or Egg" problem). For example, getting applications
to adopt higher strength EC based ciphers for a start.

Additionally, with the advancement of computers (especially Quantum Computers)
and mathematics will see many current in use ciphers made obsolete within a few
years. Wikipedia has some information on 
[Post Quantum Cryptography](https://en.wikipedia.org/wiki/Post-quantum_cryptography)
and the need to move beyond RSA, DSA and EC based ciphers.

-----------------

**Q. GOST R34.10?**

A. From wikipedia:

> Historically, GOST R system originated from GOST system developed in the 
> Soviet Union and later adopted by the CIS. Thus, the GOST standards are 
> used across all CIS countries, including Russia while GOST R standards are 
> valid only within the territory of the Russian Federation.

GOST R34.10 defines a set of ciphers used by the Russian Federation.
GOST R34.11 defines a set of hash functions.

-----------------

**Q. DSTU 4145-2002?**

A. DSTU 4145-2002 is a set of Ukrainian cipher standards.

-----------------

**Q. Post Quantum Ciphers**

A. Halimede supports Rainbow, SPHINCS-256, XMSS and XMSS-MT Post Quantum
Ciphers.

