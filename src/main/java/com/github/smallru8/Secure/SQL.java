package com.github.smallru8.Secure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * SQL操作
 * @author smallru8
 *
 */
public class SQL {
	
	private Connection sqlConn;
	
	public SQL() {
		try {
			Security.addProvider(new BouncyCastleProvider());
			Class.forName("com.mysql.jdbc.Driver");
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			System.err.println("SQL driver not found.");
			e1.printStackTrace();
		}
		try {
			sqlConn = Secure.cr.getSQLConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 取得使用者名稱
	 * @param UUID
	 * @return
	 */
	public String getUserName(String UUID) {
		String name = null;
		
		try {
			PreparedStatement ps = sqlConn.prepareStatement("SELECT Name FROM USER WHERE UUID == ?;");
			ps.setString(1, UUID);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				name = rs.getString(1);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return name;
	}
	
	public RSAPublicKey getUserPublicKey(String UUID) {
		RSAPublicKey publicKey = null;

		try {
			PreparedStatement ps = sqlConn.prepareStatement("SELECT PASSWD FROM USER WHERE UUID == ?;");
			ps.setString(1, UUID);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				Reader keyString = new StringReader(rs.getString(1));
				PemReader pemReader = new PemReader(keyString);
				PemObject pemObject = pemReader.readPemObject();
				byte[] keyBytes = pemObject.getContent();
				pemReader.close();
				try {
		            KeyFactory kf = KeyFactory.getInstance("RSA");
		            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		            publicKey = (RSAPublicKey) kf.generatePublic(keySpec);
		        } catch (NoSuchAlgorithmException e) {
		            System.out.println("Could not reconstruct the public key, the given algorithm could not be found.");
		        } catch (InvalidKeySpecException e) {
		            System.out.println("Could not reconstruct the public key");
		        }
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return publicKey;
	}
	
}
