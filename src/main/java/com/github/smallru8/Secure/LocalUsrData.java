package com.github.smallru8.Secure;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Client連線資料
 * @author smallru8
 *
 */
public class LocalUsrData {

	public String Name;
	public String UUID;
	public RSAPublicKey publicKey;
	public RSAPrivateKey privateKey;
	
	private Cipher cipher_encrypt;//AES256 CTR 加密
	private Cipher cipher_decrypt;//AES256 CTR 解密
	
	public LocalUsrData(String name) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		//把Name做sha512生成UUID
		this.Name = name;
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(Name.getBytes("utf-8"));
		UUID = String.format("%0128x", new BigInteger(1, digest.digest()));
		publicKey = Secure.cr.getPublicKey();
		privateKey = Secure.cr.getPrivateKey();
		try {
			cipher_encrypt = Cipher.getInstance("AES/CTR/NoPadding", "BC");
			cipher_decrypt = Cipher.getInstance("AES/CTR/NoPadding", "BC");
		} catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
