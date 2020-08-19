package de.jlo.talend.tweak.context.passwd;

/**
 * This class is a copy of the Talend class routines.system.PasswordEncryptUtil from Version 6.1.1
 * 
 * It encrypts and decrypts a password in the Talend way.
 */
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * DOC chuang class global comment. Detailed comment
 */
public class PasswordEncryptUtil {

	public static String ENCRYPT_KEY = "Encrypt";
	private static final String DEFAULT_KEY = "Talend-Key";
	private static String rawKey = DEFAULT_KEY;
	private static SecretKey key = null;
	private static SecureRandom secureRandom = new SecureRandom();
	private static String CHARSET = "UTF-8";

	private static SecretKey getSecretKey() throws Exception {
		if (key == null) {
			byte rawKeyData[] = rawKey.getBytes(CHARSET);
			DESKeySpec dks = new DESKeySpec(rawKeyData);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			key = keyFactory.generateSecret(dks);
		}
		return key;
	}

	public static String encryptPassword(String input) throws Exception {
		if (input == null) {
			return input;
		}
		SecretKey key = getSecretKey();
		Cipher c = Cipher.getInstance("DES");
		c.init(Cipher.ENCRYPT_MODE, key, secureRandom);
		byte[] cipherByte = c.doFinal(input.getBytes(CHARSET));
		String dec = Hex.encodeHexString(cipherByte);
		return dec;
	}

	public static String decryptPassword(String input) {
		if (input == null || input.length() == 0) {
			return input;
		}
		try {
			byte[] dec = Hex.decodeHex(input.toCharArray());
			SecretKey key = getSecretKey();
			Cipher c = Cipher.getInstance("DES");
			c.init(Cipher.DECRYPT_MODE, key, secureRandom);
			byte[] clearByte = c.doFinal(dec);
			return new String(clearByte, CHARSET);
		} catch (Exception e) {
			// do nothing
		}
		return input;
	}

	public static String getRawKey() {
		return rawKey;
	}

	public static void setRawKey(String rawKey) {
		if (rawKey != null && rawKey.trim().isEmpty() == false) {
			PasswordEncryptUtil.rawKey = rawKey;
		} else {
			PasswordEncryptUtil.rawKey = DEFAULT_KEY;
		}
	}

}
