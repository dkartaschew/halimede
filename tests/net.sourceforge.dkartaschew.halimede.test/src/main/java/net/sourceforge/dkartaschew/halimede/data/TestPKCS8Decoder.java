/*-
 * Halimede Certificate Manager Plugin for Eclipse 
 * Copyright (C) 2017-2019 Darran Kartaschew 
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v20.html
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 */

package net.sourceforge.dkartaschew.halimede.data;

import static org.junit.Assert.assertNotNull;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateKey;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import net.sourceforge.dkartaschew.halimede.TestUtilities;
import net.sourceforge.dkartaschew.halimede.exceptions.InvalidPasswordException;
import net.sourceforge.dkartaschew.halimede.util.ProviderUtil;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPKCS8Decoder {

	private final String PASSWORD = "changeme";

	@BeforeClass
	public static void setup() {
		ProviderUtil.setupProviders();
	}

	@Test
	public void test_RSA_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key.der", null);
	}

	@Test
	public void test_RSA_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key.pem", null);
	}

	@Test
	public void test_RSA_P8_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key_der.p8", null);
	}

	@Test
	public void test_RSA_P8_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key_pem.p8", null);
	}

	@Test
	public void test_RSA_P8_AES_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes_der.p8", PASSWORD);
	}

	@Test
	public void test_RSA_P8_AES_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes_pem.p8", PASSWORD);
	}

	@Test
	public void test_RSA_P8_DES_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key_des_der.p8", PASSWORD);
	}

	@Test
	public void test_RSA_P8_DES_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key_des_pem.p8", PASSWORD);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_P8_DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_des_der.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_P8_DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_des_pem.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_P8_AES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes_der.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_P8_AES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes_pem.p8", null);
	}

	@Test
	public void test_RSA_AES256_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes256.der", PASSWORD);
	}

	@Test
	public void test_RSA_AES256_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes256.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_AES256_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes256.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_AES256_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes256.pem", null);
	}

	@Test
	public void test_RSA_AES128_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes128.der", PASSWORD);
	}

	@Test
	public void test_RSA_AES128_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes128.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_AES128_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes128.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_AES128_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_aes128.pem", null);
	}

	@Test
	public void test_RSA_DES_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key_des.der", PASSWORD);
	}

	@Test
	public void test_RSA_DES_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key_des.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_des.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_des.pem", null);
	}

	@Test
	public void test_RSA_3DES_DER() throws IOException, InvalidPasswordException {
		test("rsa4096key_des3.der", PASSWORD);
	}

	@Test
	public void test_RSA_3DES_PEM() throws IOException, InvalidPasswordException {
		test("rsa4096key_des3.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_3DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_des3.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_RSA_3DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("rsa4096key_des3.pem", null);
	}

	@Test
	public void test_DSA_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key.der", null);
	}

	@Test
	public void test_DSA_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key.pem", null);
	}

	@Test
	public void test_DSA_P8_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_der.p8", null);
	}

	@Test
	public void test_DSA_P8_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_pem.p8", null);
	}

	@Test
	public void test_DSA_P8_AES_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes_der.p8", PASSWORD);
	}

	@Test
	public void test_DSA_P8_AES_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes_pem.p8", PASSWORD);
	}

	@Test
	public void test_DSA_P8_DES_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_des_der.p8", PASSWORD);
	}

	@Test
	public void test_DSA_P8_DES_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_des_pem.p8", PASSWORD);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_P8_DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_des_der.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_P8_DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_des_pem.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_P8_AES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes_der.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_P8_AES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes_pem.p8", null);
	}

	@Test
	public void test_DSA_AES256_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes256.der", PASSWORD);
	}

	@Test
	public void test_DSA_AES256_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes256.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_AES256_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes256.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_AES256_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes256.pem", null);
	}

	@Test
	public void test_DSA_AES128_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes128.der", PASSWORD);
	}

	@Test
	public void test_DSA_AES128_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes128.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_AES128_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes128.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_AES128_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_aes128.pem", null);
	}

	@Test
	public void test_DSA_DES_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_des.der", PASSWORD);
	}

	@Test
	public void test_DSA_DES_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_des.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_des.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_des.pem", null);
	}

	@Test
	public void test_DSA_3DES_DER() throws IOException, InvalidPasswordException {
		test("dsa4096key_des3.der", PASSWORD);
	}

	@Test
	public void test_DSA_3DES_PEM() throws IOException, InvalidPasswordException {
		test("dsa4096key_des3.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_3DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_des3.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_DSA_3DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("dsa4096key_des3.pem", null);
	}

	@Test
	public void test_EC521_DER() throws IOException, InvalidPasswordException {
		test("ec521key.der", null);
	}

	@Test
	public void test_EC521_PEM() throws IOException, InvalidPasswordException {
		test("ec521key.pem", null);
	}

	@Test
	public void test_EC521_P8_DER() throws IOException, InvalidPasswordException {
		test("ec521key_der.p8", null);
	}

	@Test
	public void test_EC521_P8_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_pem.p8", null);
	}

	@Test
	public void test_EC521_P8_AES_DER() throws IOException, InvalidPasswordException {
		test("ec521key_aes_der.p8", PASSWORD);
	}

	@Test
	public void test_EC521_P8_AES_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_aes_pem.p8", PASSWORD);
	}

	@Test
	public void test_EC521_P8_DES_DER() throws IOException, InvalidPasswordException {
		test("ec521key_des_der.p8", PASSWORD);
	}

	@Test
	public void test_EC521_P8_DES_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_des_pem.p8", PASSWORD);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_P8_DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_des_der.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_P8_DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_des_pem.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_P8_AES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_aes_der.p8", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_P8_AES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_aes_pem.p8", null);
	}

	@Test
	public void test_EC521_AES256_DER() throws IOException, InvalidPasswordException {
		test("ec521key_aes256.der", PASSWORD);
	}

	@Test
	public void test_EC521_AES256_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_aes256.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_AES256_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_aes256.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_AES256_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_aes256.pem", null);
	}

	@Test
	public void test_EC521_AES128_DER() throws IOException, InvalidPasswordException {
		test("ec521key_aes128.der", PASSWORD);
	}

	@Test
	public void test_EC521_AES128_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_aes128.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_AES128_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_aes128.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_AES128_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_aes128.pem", null);
	}

	@Test
	public void test_EC521_DES_DER() throws IOException, InvalidPasswordException {
		test("ec521key_des.der", PASSWORD);
	}

	@Test
	public void test_EC521_DES_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_des.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_des.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_des.pem", null);
	}

	@Test
	public void test_EC521_3DES_DER() throws IOException, InvalidPasswordException {
		test("ec521key_des3.der", PASSWORD);
	}

	@Test
	public void test_EC521_3DES_PEM() throws IOException, InvalidPasswordException {
		test("ec521key_des3.pem", PASSWORD);
	}

	@Ignore // DER encoded PKCS1 doesn't use passwords
	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_3DES_DER_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_des3.der", null);
	}

	@Test(expected = InvalidPasswordException.class)
	public void test_EC521_3DES_PEM_BADPASSWORD() throws IOException, InvalidPasswordException {
		test("ec521key_des3.pem", null);
	}
	
	@Test(expected=IOException.class)
	public void testPKCS7Open() throws IOException, InvalidPasswordException {
		test("dsacert.p7b", null);
	}
	
	@Test(expected=IOException.class)
	public void testPKCS12Open() throws IOException, InvalidPasswordException {
		test("dsa4096.p12", null);
	}

	private void test(String filename, String password) throws IOException, InvalidPasswordException {
		Path file = TestUtilities.getFile(filename);
		PKCS8Decoder decoder = PKCS8Decoder.open(file, password);
		KeyPair keys = decoder.getKeyPair();
		assertNotNull(keys);
		if (keys.getPublic() != null)
			System.out.println(keys.getPublic().getAlgorithm());
		System.out.println(keys.getPrivate().getAlgorithm());
		PrivateKey p = keys.getPrivate();
		if (p instanceof RSAPrivateKey) {
			System.out.println(((RSAPrivateKey) p).getModulus().toString(16));
			System.out.println(((RSAPrivateKey) p).getPrivateExponent().toString(16));
		} else if (p instanceof DSAPrivateKey) {
			System.out.println(((DSAPrivateKey) p).getX().toString(16));
		} else if (p instanceof ECPrivateKey) {
			System.out.println(((ECPrivateKey) p).getS().toString(16));
		}
	}
}
