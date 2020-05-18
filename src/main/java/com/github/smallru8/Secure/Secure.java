package com.github.smallru8.Secure;

import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.RSAEngine;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

/**
 * 身分驗證
 * @author smallru8
 *
 */
public class Secure {

	public static ConfigReader cr;
	public static SQL sql;
	public static LocalUsrData localUsr;
	
	public Secure() {
		try {
			cr = new ConfigReader();
			sql = new SQL();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * RSA2048加密
	 * @param plaintext
	 * @param key
	 * @return
	 */
	public byte[] encryption_RSA(byte[] plaintext,AsymmetricKeyParameter key) {
		byte[] ciphertext = null;
		AsymmetricBlockCipher cipher = new RSAEngine();
		cipher.init(true, key);//true表示加密
		try {
			ciphertext = cipher.processBlock(plaintext, 0, plaintext.length);
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ciphertext;
	}
	
	/**
	 * RSA2048解密
	 * @param ciphertext
	 * @param key
	 * @return
	 */
	public byte[] decryption_RSA(byte[] ciphertext,AsymmetricKeyParameter key) {
		byte[] plaintext = null;
        AsymmetricBlockCipher cipher = new RSAEngine();
        cipher.init(false, key);//false表示解密
        try {
			plaintext = cipher.processBlock(ciphertext, 0, ciphertext.length);
		} catch (InvalidCipherTextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return plaintext;
	}
	
	/*以下Server才會用到===================================================================================*/
	
	/**
	 * 驗證身分
	 * @param sessionK : server送給client的
	 * @param sessionKplus1 : Client回送sessionK+1
	 * @return
	 */
	public boolean verifyUsr(byte[] sessionK,byte[] sessionKplus1) {
		
		BigInteger s1 = new BigInteger(1,sessionK);
		BigInteger s2 = new BigInteger(1,sessionKplus1);
		s1.add(BigInteger.ONE);
		if(s1.equals(s2))
			return true;
		return false;
	}
	
}
