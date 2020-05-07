package com.github.smallru8.Secure.TEST;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import com.github.smallru8.Secure.KeyGen.RSA;

/**
 * RSA2048 key generate test
 * @author smallru8
 *
 */
public class Test1 {
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		RSA rsa2048 = new RSA(2048);
		rsa2048.RSAKeyGen();
	}
	
}
