package com.github.smallru8.Secure;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * Client連線資料
 * @author smallru8
 *
 */
public class LocalUsrData extends UsrData{

	public byte[] IPaddr;
	public int port;
	
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
	
}
