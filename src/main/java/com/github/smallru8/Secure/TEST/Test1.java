package com.github.smallru8.Secure.TEST;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;

import com.github.smallru8.Secure.Secure;
import com.github.smallru8.Secure.KeyGen.RSA;

/**
 * RSA2048 key generate test
 * @author smallru8
 *
 */
public class Test1 {
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		Secure sec = new Secure();
		String in = "RSAtest";
		
		System.out.println("加密前 : "+in);
		byte[] Ciphertext = sec.encryption_RSA(in.getBytes("utf-8"), PublicKeyFactory.createKey(sec.cr.getPublicKey().getEncoded()));
		System.out.println("加密後 : "+new String(Ciphertext,"utf-8"));
		byte[] Plaintext = sec.decryptData_RSA(Ciphertext, PrivateKeyFactory.createKey(sec.cr.getPrivateKey().getEncoded()));
		System.out.println("解密後 : "+new String(Plaintext,"utf-8"));
		
	}
	
}
