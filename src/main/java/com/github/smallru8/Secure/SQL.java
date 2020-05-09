package com.github.smallru8.Secure;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import java.util.Base64;

import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/*
 * SQL Table格式
 * |Name(vchar 128)|UUID(vchar 128)|PASSWD(vchar 128)|Session(vchar 128)|LastLogInTime(int)|
 * 
 * Name : 使用者名稱
 * UUID : SHA512(使用者名稱)
 * PASSWD : 使用者public key
 * Session : 用PASSWD加密(使用者Session key)
 * LastLogInTime : 上次連線日期時間
 * 
 * 當使用者斷線重連，(檢查上次連線時間)，Server直接發送SQL中Session欄位內容給使用者
 * 
 */

/**
 * SQL操作
 * @author smallru8
 *
 */
public class SQL {
	
	private Connection sqlConn;
	
	final Base64.Decoder decoder = Base64.getDecoder();
	final Base64.Encoder encoder = Base64.getEncoder();
	
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
	
	/**
	 * 把SQL中PASSWD轉回public key格式
	 * @param UUID
	 * @return
	 */
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
	
	/**
	 * 取得經public key加密過的sessionKey
	 * @param UUID
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] getSessionKey(String UUID) throws UnsupportedEncodingException {
		String BASE64_Cipher_SessionKey = null;
		try {
			PreparedStatement ps = sqlConn.prepareStatement("SELECT Session FROM USER WHERE UUID == ?;");
			ps.setString(1, UUID);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				BASE64_Cipher_SessionKey = rs.getString(1);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//解BASE64
		String cipher_SessionKey = new String(decoder.decode(BASE64_Cipher_SessionKey), "UTF-8");
		return cipher_SessionKey.getBytes("UTF-8");
	}
	
	
	/**
	 * 取得上次離線時間
	 * @param UUID
	 * @return
	 */
	public int getLastLogInTime(String UUID) {
		int time = 0;
		try {
			PreparedStatement ps = sqlConn.prepareStatement("SELECT LastLogInTime FROM USER WHERE UUID == ?;");
			ps.setString(1, UUID);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				time = rs.getInt(1);
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return time;
	}
	
}
