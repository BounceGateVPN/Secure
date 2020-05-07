package com.github.smallru8.Secure.KeyGen;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;


/**
 * Generate RAS keypair.
 * client產生的public key要傳到server的db
 * @author smallru8
 *
 */
public class RSA {
	
	private PemObject pemObject;
	private int rsaSize;
	
	/**
	 * 
	 * @param rsaSize 512,1024,2048,4096
	 */
	public RSA (int rsaSize) {
		this.rsaSize = rsaSize;
	}
	
	/**
	 * Generate RSA key.
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public void RSAKeyGen() throws NoSuchAlgorithmException, IOException {
		Security.addProvider(new BouncyCastleProvider());
		
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(rsaSize);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();
		
		pemObject = new PemObject("RSA PUBLIC KEY", publicKey.getEncoded());
		writePem("key/publicKey.pub");
		pemObject = new PemObject("RSA PRIVATE KEY", privateKey.getEncoded());
		writePem("key/privateKey.key");
	}
	
	/**
	 * Write key to file.
	 * @param fileName
	 * @throws IOException
	 */
	private void writePem(String fileName) throws IOException {
		PemWriter pemWriter = null;
		try {
			pemWriter = new PemWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
			pemWriter.writeObject(pemObject);
		} finally {
			pemWriter.close();
		}
	}
	
}
