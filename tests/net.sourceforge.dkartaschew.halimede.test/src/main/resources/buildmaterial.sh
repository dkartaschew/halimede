#!/bin/bash

# Keying material generation.

# basic script to recreate keying material under test.

SUBJECT="/C=AU/ST=Queensland/L=Gold Coast/O=Internet Widgits Pty Ltd/CN=CA Manager"

PASSWORD="changeme"

# RSA Material - RSA 4096 Private Key + Self signed certificates

openssl req -x509 -newkey rsa:4096 -keyout rsa4096key.pem -out rsacert.pem -nodes -days 3650 -subj "${SUBJECT}"
openssl x509 -inform pem -in rsacert.pem -outform der -out rsacert.cer
openssl crl2pkcs7 -nocrl -certfile rsacert.pem -out rsacert.p7b
openssl crl2pkcs7 -nocrl -certfile rsacert.pem -out rsacert_der.p7b -outform der
openssl rsa -in rsa4096key.pem -outform DER -out rsa4096key.der

openssl rsa -in rsa4096key.pem -outform PEM -out rsa4096key_aes128.pem -aes128 -passout pass:${PASSWORD}
openssl rsa -in rsa4096key.pem -outform DER -out rsa4096key_aes128.der -aes128 -passout pass:${PASSWORD}
openssl rsa -in rsa4096key.pem -outform PEM -out rsa4096key_aes256.pem -aes256 -passout pass:${PASSWORD}
openssl rsa -in rsa4096key.pem -outform DER -out rsa4096key_aes256.der -aes256 -passout pass:${PASSWORD}
openssl rsa -in rsa4096key.pem -outform PEM -out rsa4096key_des.pem -des -passout pass:${PASSWORD}
openssl rsa -in rsa4096key.pem -outform DER -out rsa4096key_des.der -des -passout pass:${PASSWORD}
openssl rsa -in rsa4096key.pem -outform PEM -out rsa4096key_des3.pem -des3 -passout pass:${PASSWORD}
openssl rsa -in rsa4096key.pem -outform DER -out rsa4096key_des3.der -des3 -passout pass:${PASSWORD}

openssl pkcs8 -topk8 -inform PEM -outform DER -v2 aes-128-cbc -in rsa4096key.pem -out rsa4096key_aes_der.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform PEM -v2 aes-128-cbc -in rsa4096key.pem -out rsa4096key_aes_pem.p8 -passout pass:${PASSWORD}

openssl pkcs8 -topk8 -inform PEM -outform DER -in rsa4096key.pem -out rsa4096key_des_der.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform PEM -in rsa4096key.pem -out rsa4096key_des_pem.p8 -passout pass:${PASSWORD}

openssl pkcs8 -topk8 -inform PEM -outform DER -in rsa4096key.pem -out rsa4096key_der.p8 -nocrypt
openssl pkcs8 -topk8 -inform PEM -outform PEM -in rsa4096key.pem -out rsa4096key_pem.p8 -nocrypt

openssl pkcs12 -export -out rsa4096key.p12 -inkey rsa4096key.pem -in rsacert.pem -nodes -passout pass:
openssl pkcs12 -export -out rsa4096.p12 -inkey rsa4096key.pem -in rsacert.pem -aes256 -password pass:${PASSWORD} -name "alias"
openssl pkcs12 -export -out rsa4096_2.p12 -inkey rsa4096key.pem -in rsacert.pem -aes256 -password pass:${PASSWORD}

openssl pkcs12 -export -out rsa4096_aes.p12 -inkey rsa4096key.pem -in rsacert.pem -aes256 -certpbe AES-256-CBC -keypbe AES-256-CBC -password pass:${PASSWORD} -name "alias"
openssl pkcs12 -export -out rsa4096_aes_2.p12 -inkey rsa4096key.pem -in rsacert.pem -aes256 -certpbe AES-256-CBC -keypbe AES-256-CBC -password pass:${PASSWORD}

# DSA Material - DSA 4096 Private Key + Self signed certificates

openssl dsaparam 4096 < /dev/random > dsaparam.pem
openssl gendsa dsaparam.pem -out dsa4096key.pem
openssl req -new -x509 -key dsa4096key.pem -nodes -days 3650 -out dsacert.pem -subj "${SUBJECT}"
openssl x509 -inform pem -in dsacert.pem -outform der -out dsacert.cer
openssl crl2pkcs7 -nocrl -certfile dsacert.pem -out dsacert.p7b
openssl crl2pkcs7 -nocrl -certfile dsacert.pem -out dsacert_der.p7b -outform der
openssl dsa -in dsa4096key.pem -outform DER -out dsa4096key.der

openssl dsa -in dsa4096key.pem -outform PEM -out dsa4096key_aes256.pem -aes256 -passout pass:${PASSWORD}
openssl dsa -in dsa4096key.pem -outform DER -out dsa4096key_aes256.der -aes256 -passout pass:${PASSWORD}
openssl dsa -in dsa4096key.pem -outform PEM -out dsa4096key_aes128.pem -aes128 -passout pass:${PASSWORD}
openssl dsa -in dsa4096key.pem -outform DER -out dsa4096key_aes128.der -aes128 -passout pass:${PASSWORD}
openssl dsa -in dsa4096key.pem -outform PEM -out dsa4096key_des.pem -des -passout pass:${PASSWORD}
openssl dsa -in dsa4096key.pem -outform DER -out dsa4096key_des.der -des -passout pass:${PASSWORD}
openssl dsa -in dsa4096key.pem -outform PEM -out dsa4096key_des3.pem -des3 -passout pass:${PASSWORD}
openssl dsa -in dsa4096key.pem -outform DER -out dsa4096key_des3.der -des3 -passout pass:${PASSWORD}

openssl pkcs8 -topk8 -inform PEM -outform DER -v2 aes-128-cbc -in dsa4096key.pem -out dsa4096key_aes_der.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform PEM -v2 aes-128-cbc -in dsa4096key.pem -out dsa4096key_aes_pem.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform DER -in dsa4096key.pem -out dsa4096key_des_der.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform PEM -in dsa4096key.pem -out dsa4096key_des_pem.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform DER -in dsa4096key.pem -out dsa4096key_der.p8 -nocrypt
openssl pkcs8 -topk8 -inform PEM -outform PEM -in dsa4096key.pem -out dsa4096key_pem.p8 -nocrypt

openssl pkcs12 -export -out dsa4096key.p12 -inkey dsa4096key.pem -in dsacert.pem -nodes -passout pass:
openssl pkcs12 -export -out dsa4096.p12 -inkey dsa4096key.pem -in dsacert.pem -aes256 -password pass:${PASSWORD} -name "alias"
openssl pkcs12 -export -out dsa4096_2.p12 -inkey dsa4096key.pem -in dsacert.pem -aes256 -password pass:${PASSWORD} 

openssl pkcs12 -export -out dsa4096_aes.p12 -inkey dsa4096key.pem -in dsacert.pem -aes256 -certpbe AES-256-CBC -keypbe AES-256-CBC -password pass:${PASSWORD} -name "alias"
openssl pkcs12 -export -out dsa4096_aes_2.p12 -inkey dsa4096key.pem -in dsacert.pem -aes256 -certpbe AES-256-CBC -keypbe AES-256-CBC -password pass:${PASSWORD} 

# EC Material - EC NIST p521 Private Key + Self signed certificates

# Note: Java EC implementation MUST used 'named_curve', and note explicit curve for the private key.
openssl ecparam -name secp521r1 -genkey -out ec521key.pem 
openssl req -new -x509 -key ec521key.pem -nodes -days 3650 -out eccert.pem -subj "${SUBJECT}"
openssl x509 -inform pem -in eccert.pem -outform der -out eccert.cer
openssl crl2pkcs7 -nocrl -certfile eccert.pem -out eccert.p7b
openssl crl2pkcs7 -nocrl -certfile eccert.pem -out eccert_der.p7b -outform der

openssl ec -in ec521key.pem -outform DER -out ec521key.der

openssl ec -in ec521key.pem -outform PEM -out ec521key_aes256.pem -aes256 -passout pass:${PASSWORD}
openssl ec -in ec521key.pem -outform DER -out ec521key_aes256.der -aes256 -passout pass:${PASSWORD}
openssl ec -in ec521key.pem -outform PEM -out ec521key_aes128.pem -aes128 -passout pass:${PASSWORD}
openssl ec -in ec521key.pem -outform DER -out ec521key_aes128.der -aes128 -passout pass:${PASSWORD}
openssl ec -in ec521key.pem -outform PEM -out ec521key_des.pem -des -passout pass:${PASSWORD}
openssl ec -in ec521key.pem -outform DER -out ec521key_des.der -des -passout pass:${PASSWORD}
openssl ec -in ec521key.pem -outform PEM -out ec521key_des3.pem -des3 -passout pass:${PASSWORD}
openssl ec -in ec521key.pem -outform DER -out ec521key_des3.der -des3 -passout pass:${PASSWORD}

openssl pkcs8 -topk8 -inform PEM -outform PEM -in ec521key.pem -out ec521key_pem.p8 -nocrypt
openssl pkcs8 -topk8 -inform PEM -outform DER -in ec521key.pem -out ec521key_der.p8 -nocrypt
openssl pkcs8 -topk8 -inform PEM -outform PEM -in ec521key.pem -out ec521key_des_pem.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform DER -in ec521key.pem -out ec521key_des_der.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform PEM -v2 aes-128-cbc -in ec521key.pem -out ec521key_aes_pem.p8 -passout pass:${PASSWORD}
openssl pkcs8 -topk8 -inform PEM -outform DER -v2 aes-128-cbc -in ec521key.pem -out ec521key_aes_der.p8 -passout pass:${PASSWORD}

openssl pkcs12 -export -out ec521key.p12 -inkey ec521key.pem -in eccert.pem -nodes -passout pass:
openssl pkcs12 -export -out ec521.p12 -inkey ec521key.pem -in eccert.pem -aes256 -password pass:${PASSWORD} -name "alias"
openssl pkcs12 -export -out ec521_2.p12 -inkey ec521key.pem -in eccert.pem -aes256 -password pass:${PASSWORD}

openssl pkcs12 -export -out ec521_aes.p12 -inkey ec521key.pem -in eccert.pem -aes256 -certpbe AES-256-CBC -keypbe AES-256-CBC -password pass:${PASSWORD} -name "alias"
openssl pkcs12 -export -out ec521_aes_2.p12 -inkey ec521key.pem -in eccert.pem -aes256 -certpbe AES-256-CBC -keypbe AES-256-CBC -password pass:${PASSWORD}

# Create signing requests.
openssl req -new -sha1 -key rsa4096key.pem -out rsa4096_pem.csr -days 3650 -subj "${SUBJECT}"
openssl req -new -sha512 -key rsa4096key.pem -out rsa4096_sha512_pem.csr -days 3650 -subj "${SUBJECT}"
openssl req -new -sha1 -key rsa4096key.pem -out rsa4096_der.csr -outform DER -days 3650 -subj "${SUBJECT}"
openssl req -new -sha512 -key rsa4096key.pem -out rsa4096_sha512_der.csr -outform DER -days 3650 -subj "${SUBJECT}"

openssl req -new -sha1 -key dsa4096key.pem -out dsa4096key_pem.csr -days 3650 -subj "${SUBJECT}"
openssl req -new -sha256 -key dsa4096key.pem -out dsa4096key_sha256_pem.csr -days 3650 -subj "${SUBJECT}"
openssl req -new -sha1 -key dsa4096key.pem -out dsa4096key_der.csr -outform DER -days 3650 -subj "${SUBJECT}"
openssl req -new -sha256 -key dsa4096key.pem -out dsa4096key_sha256_der.csr -outform DER -days 3650 -subj "${SUBJECT}"

openssl req -new -sha1 -key ec521key.pem -out ec521key_pem.csr -days 3650 -subj "${SUBJECT}"
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_pem.csr -days 3650 -subj "${SUBJECT}"
openssl req -new -sha1 -key ec521key.pem -out ec521key_der.csr -outform DER -days 3650 -subj "${SUBJECT}"
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_der.csr -outform DER -days 3650 -subj "${SUBJECT}"

# Intermediate CA
openssl req -new -sha1 -key ec521key.pem -out ec521key_pem_ca.csr -days 3650 -subj "${SUBJECT}" -config req_ca.cnf
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_pem_ca.csr -days 3650 -subj "${SUBJECT}" -config req_ca.cnf
openssl req -new -sha1 -key ec521key.pem -out ec521key_der_ca.csr -outform DER -days 3650 -subj "${SUBJECT}" -config req_ca.cnf
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_der_ca.csr -outform DER -days 3650 -subj "${SUBJECT}" -config req_ca.cnf

# Intermediate CA # 2 (no basic constraints listed).
openssl req -new -sha1 -key ec521key.pem -out ec521key_pem_ca2.csr -days 3650 -subj "${SUBJECT}" -config req_ca2.cnf
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_pem_ca2.csr -days 3650 -subj "${SUBJECT}" -config req_ca2.cnf
openssl req -new -sha1 -key ec521key.pem -out ec521key_der_ca2.csr -outform DER -days 3650 -subj "${SUBJECT}" -config req_ca2.cnf
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_der_ca2.csr -outform DER -days 3650 -subj "${SUBJECT}" -config req_ca2.cnf

# End user
openssl req -new -sha1 -key ec521key.pem -out ec521key_pem_key.csr -days 3650 -subj "${SUBJECT}"  -config req_cert.cnf
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_pem_key.csr -days 3650 -subj "${SUBJECT}"  -config req_cert.cnf
openssl req -new -sha1 -key ec521key.pem -out ec521key_der_key.csr -outform DER -days 3650 -subj "${SUBJECT}"  -config req_cert.cnf
openssl req -new -sha512 -key ec521key.pem -out ec521key_sha512_der_key.csr -outform DER -days 3650 -subj "${SUBJECT}" -config req_cert.cnf

# Email SMIME Certificates. (PKCS#12)

# CA for SMIME signing
openssl req -x509 -newkey rsa:4096 -keyout cakey.pem -out cacert.pem -nodes -days 3650 -subj "${SUBJECT}"

openssl req -new -key rsa4096key.pem -out rsa_email.csr -config email.cnf
openssl x509 -req -days 3650 -in rsa_email.csr -CA cacert.pem -CAkey cakey.pem -set_serial 10 -out rsa_email.cer
openssl pkcs12 -export -in rsa_email.cer -inkey rsa4096key.pem -out rsa_email.p12 -nodes -passout pass:

openssl req -new -key ec521key.pem -out ec_email.csr -config email.cnf
openssl x509 -req -days 3650 -in ec_email.csr -CA cacert.pem -CAkey cakey.pem -set_serial 11 -out ec_email.cer
openssl pkcs12 -export -in ec_email.cer -inkey ec521key.pem -out ec_email.p12 -nodes -passout pass:
