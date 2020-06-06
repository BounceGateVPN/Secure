package com.github.smallru8.Secure;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 使用者連線基本資料
 * @author smallru8
 *
 */
public class UsrData {
	public String Name;
	public String UUID;
	
	//首次連線用
	public RSAPublicKey publicKey;//RSA2048 加密
	public RSAPrivateKey privateKey;//RSA2048 解密
	
	//資料傳輸用
	protected Cipher cipher_encrypt;//AES256 CTR 加密
	protected Cipher cipher_decrypt;//AES256 CTR 解密
	
	
	/**
	 * 將從server端收到的session key設定進去(256-bit)，IV = MD5(session key)
	 * @param key
	 */
	public void setSessionKey(byte[] key) {
		Key secretKey = new SecretKeySpec(key,"AES");
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(key);
			IvParameterSpec ips = new IvParameterSpec(md5.digest());
			cipher_encrypt.init(Cipher.ENCRYPT_MODE, secretKey, ips);
			cipher_decrypt.init(Cipher.ENCRYPT_MODE, secretKey, ips);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 加密訊息
	 * @param plaintext
	 * @return
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	public byte[] encryption_AES(byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException {
		return cipher_encrypt.doFinal(plaintext);
	}
	
	/**
	 * 解密訊息
	 * @param ciphertext
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] decryption_AES(byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException {
		return cipher_decrypt.doFinal(ciphertext);
	}
}
