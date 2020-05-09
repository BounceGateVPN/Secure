package com.github.smallru8.Secure.KeyGen;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Generate session key.
 * @author smallru8
 *
 */
public class AES {

	private int aesSize;
	
	/**
	 * 
	 * @param aesSize 128,192,256
	 */
	public AES(int aesSize) {
		this.aesSize = aesSize;
	}
	
	public SecretKey AESKeyGen() throws NoSuchAlgorithmException {
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(aesSize);
		SecretKey secretKey = keyGen.generateKey();
		return secretKey;
	}
	
	public static byte[] KeytoByteArray(SecretKey key) {
		return key.getEncoded();
	}
	
	public static SecretKey byteArraytoKey(byte[] keyByte) {
		return new SecretKeySpec(keyByte,"AES");
	}
	
}
