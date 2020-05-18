package com.github.smallru8.Secure.TEST;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.github.smallru8.Secure.LocalUsrData;
import com.github.smallru8.Secure.Secure;
import com.github.smallru8.Secure.KeyGen.AES;

/**
 * RSA2048 key generate test
 * @author smallru8
 *
 */
public class Test1 {
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException {
		Secure sec = new Secure();
		String in = "AES_CTR_test";
		LocalUsrData lud = new LocalUsrData("smallru8");
		
		lud.setSessionKey(AES.KeytoByteArray(new AES(256).AESKeyGen()));
		
		System.out.println("加密前 : "+in);
		byte[] Ciphertext = lud.encryption_AES(in.getBytes("utf-8"));
		System.out.println("加密後 : "+new String(Ciphertext,"utf-8"));
		byte[] Plaintext = lud.decryption_AES(Ciphertext);
		System.out.println("解密後 : "+new String(Plaintext,"utf-8"));
		
	}
	
}
