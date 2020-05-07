package com.github.smallru8.Secure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import com.github.smallru8.Secure.KeyGen.RSA;

public class ConfigReader {
	public enum KeyType{
		PUBLIC,PRIVATE
	}
	
	private final String SQLPropertiesPath = "config/SQL/SQL.properties";
	private final String KeyPropertiesPath = "config/key.properties";
	
	public boolean UsingSQL = false;
	private String userName = "";
	private String passwd = "";
	private String host = "";
	
	private String publicKeyPath;
	private String privateKeyPath;
	
	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;
	
	/**
	 * 檢查Config檔案、DB，若不存在就建立
	 * 完成檢查後載入config
	 * @throws IOException
	 */
	public ConfigReader() throws IOException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			System.err.println("SQL driver not found.");
			e1.printStackTrace();
		}
		if(!new File("config").exists()) {
			new File("config").mkdirs();
		}
		if(!new File("config/SQL").exists()) {
			new File("config/SQL").mkdirs();
		}
		Connection c = null;
		Statement stmt = null;
		if(!new File(SQLPropertiesPath).exists()) {
			new File(SQLPropertiesPath).createNewFile();
			FileWriter SQLProperties = new FileWriter(SQLPropertiesPath);
			SQLProperties.write("SQL = false\n");
			SQLProperties.write("host = jdbc:mysql://localhost/db\n");
			SQLProperties.write("username = user\n");
			SQLProperties.write("password = passwd\n");
			SQLProperties.flush();
			SQLProperties.close();

			try {
				//建DB
				c = DriverManager.getConnection("jdbc:sqlite:config/SQL/Secure.db");
				stmt = c.createStatement();
				//建Table
				String sqlStmt1 = "CREATE TABLE USER"+"(UUID VARCHAR(128),PASSWD VARCHAR(500),Session VARCHAR(128),LastLogInTime INT)charset=utf8;";
				stmt.executeUpdate(sqlStmt1);
				c.close();
				host = "jdbc:sqlite:config/SQL/Secure.db";
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.err.println( e.getClass().getName() + ": " + e.getMessage());
			}
		}else {
			Properties SQLproperties = new Properties();
			SQLproperties.load(new FileInputStream(SQLPropertiesPath));
			host = "jdbc:sqlite:config/SQL/Secure.db";
			//有啟用mysql模式
			if(SQLproperties.getProperty("SQL","false").startsWith("true")){
				UsingSQL = true;
				host = SQLproperties.getProperty("host","jdbc:mysql://localhost/db");
				userName = SQLproperties.getProperty("username","user");
				passwd = SQLproperties.getProperty("password","passwd");
				checkSQLTable();
				SQLproperties.clear();
			}
		}
		if(!new File("key").exists()) {
			new File("key").mkdir();
		}
		if(!new File(KeyPropertiesPath).exists()) {
			new File(KeyPropertiesPath).createNewFile();
			FileWriter KeyProperties = new FileWriter(KeyPropertiesPath);
			KeyProperties.write("publicKeyPath = key/publicKey.pub\n");
			KeyProperties.write("privateKeyPath = key/privateKey.key\n");
			KeyProperties.flush();
			KeyProperties.close();
			publicKeyPath = "key/publicKey.pub";
			privateKeyPath = "key/privateKey.key";
		}else {
			Properties KeyProperties = new Properties();
			KeyProperties.load(new FileInputStream(KeyPropertiesPath));
			publicKeyPath = KeyProperties.getProperty("publicKeyPath","key/publicKey.pub");
			privateKeyPath = KeyProperties.getProperty("privateKeyPath","key/privateKey.key");
			KeyProperties.clear();
		}
		
		//如果public/private key不存在就生成一組
		if((!new File(publicKeyPath).exists())||(!new File(privateKeyPath).exists())) {
			System.err.println("Can not find keys, change key path to default path.");
			publicKeyPath = "key/publicKey.pub";
			privateKeyPath = "key/privateKey.key";
			if((!new File(publicKeyPath).exists())||(!new File(privateKeyPath).exists())) {
				RSA rsa2048 = new RSA(2048);
				try {
					rsa2048.RSAKeyGen();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		publicKey = (RSAPublicKey) getLocalRSAKey(KeyType.PUBLIC);
		privateKey = (RSAPrivateKey) getLocalRSAKey(KeyType.PRIVATE);
	}
	
	/**
	 * 取得本機publicKey
	 * @return
	 */
	public RSAPublicKey getPublicKey() {
		return publicKey;
	}
	
	/**
	 * 取得本機privateKey
	 * @return
	 */
	public RSAPrivateKey getPrivateKey() {
		return privateKey;
	}
	
	/**
	 * 取得SQL連線
	 * @return
	 * @throws SQLException
	 */
	public Connection getSQLConnection() throws SQLException {
		Connection conn = null;
		if(UsingSQL) {
			conn = DriverManager.getConnection(host,userName,passwd);
		}else {
			conn = DriverManager.getConnection("jdbc:sqlite:config/SQL/Secure.db");
		}
		return conn;
	}
	
	/**
	 * Table檢查
	 * @return
	 */
	private boolean checkSQLTable() {
		Connection conn;
		try {
			conn = DriverManager.getConnection(host,userName,passwd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("Can not connect to SQL server.");
			e.printStackTrace();
			return false;
		}
		try {
			DatabaseMetaData dbm = conn.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "USER", null);
			if (!tables.next()) {
				String sqlStmt1 = "CREATE TABLE USER"+"(UUID VARCHAR(128),PASSWD VARCHAR(500),Session VARCHAR(128),LastLogInTime INT)charset=utf8;";
				Statement stmt = conn.createStatement();
				stmt.executeUpdate(sqlStmt1);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	
	/**
	 * Get this client/server's RSAKey
	 * @param keyType
	 * @return
	 * @throws FileNotFoundException
	 */
	private RSAKey getLocalRSAKey(KeyType keyType) throws FileNotFoundException {
		File pemFile = null;
		if(keyType == KeyType.PUBLIC)
			pemFile = new File(publicKeyPath);
		else if(keyType == KeyType.PRIVATE)
			pemFile = new File(privateKeyPath);
		
		if (!pemFile.isFile() || !pemFile.exists()) {
            throw new FileNotFoundException(String.format("The file '%s' doesn't exist.", pemFile.getAbsolutePath()));
		}
        PemReader reader = new PemReader(new FileReader(pemFile));
        PemObject pemObject = null;
		
        try {
			pemObject = reader.readPemObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		byte[] content = pemObject.getContent();
        
        try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        if(keyType == KeyType.PUBLIC)
        	return (RSAKey) getPublicKey(content);
        else if(keyType == KeyType.PRIVATE)
        	return (RSAKey) getPrivateKey(content);
        
		return null;
	}
	
	private PublicKey getPublicKey(byte[] keyBytes) {
        PublicKey publicKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            publicKey = kf.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not reconstruct the public key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            System.out.println("Could not reconstruct the public key");
        }

        return publicKey;
    }
	
	private PrivateKey getPrivateKey(byte[] keyBytes) {
        PrivateKey privateKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = kf.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Could not reconstruct the private key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
            System.out.println("Could not reconstruct the private key");
        }

        return privateKey;
    }
	
}
