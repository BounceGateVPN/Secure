package com.github.smallru8.Secure.Config;

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
import com.github.smallru8.Secure.Log.Log;

/**
 * 生成預設Config
 * @author smallru8
 *
 */
public class DefaultConfig extends Config{
	private enum KeyType{
		PUBLIC,PRIVATE
	}
	public String UsrName;
	protected String SQLitePath;// jdbc:sqlite:config/<name>/SQL/Secure.db
	private String sqlstmt = "CREATE TABLE USER"+"(Name VARCHAR(128), UUID VARCHAR(128),PASSWD VARCHAR(512),Session VARCHAR(512),LastLogInTime INT, PRIMARY KEY (UUID));";
	private String publicKeyPath;
	private String privateKeyPath;
	
	private RSAPublicKey publicKey;
	private RSAPrivateKey privateKey;
	
	/**
	 * name傳入"Default"
	 * @param name
	 * @throws IOException
	 */
	public DefaultConfig(String name) {
		super(name);
		try {
			Log.printMsg(ModuleName, Log.MsgType.info, "Loading SQL driver.");
			//Class.forName("com.mysql.jdbc.Driver");
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			Log.printMsg(ModuleName, Log.MsgType.err, "SQL driver not found.");
			e1.printStackTrace();
		}
		SQLitePath = "jdbc:sqlite:"+cfgDirPath+"SQL/Secure.db";
	}
	
	/**
	 * Checking all config file include SQLite db
	 */
	public void checkAll() {
		try {
			createCfgFile();
			createSQLTable();
			createRSAKey();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.printMsg(ModuleName, Log.MsgType.err, "Checking file error.");
			e.printStackTrace();
		}
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
		Log.printMsg(ModuleName, Log.MsgType.info, "Connecting to SQL server...");
		if(UsingSQL) {
			conn = DriverManager.getConnection(host,userName,passwd);
		}else {
			conn = DriverManager.getConnection(host);
		}
		return conn;
	}
	
	@Override
	public void createCfgFile() throws IOException {
		if(!new File(cfgDirPath + cfgName + ".conf").exists()) {
			new File(cfgDirPath + cfgName + ".conf").createNewFile();
			FileWriter cfg = new FileWriter(cfgDirPath + cfgName + ".conf");
			cfg.write("name = usrName\n");
			cfg.write("publicKeyPath = " + cfgDirPath + cfgName + "/key/publicKey.pub\n");
			cfg.write("privateKeyPath = " + cfgDirPath + cfgName + "/key/privateKey.key\n");
			cfg.write("\n");
			cfg.write("SQL = false\n");
			cfg.write("host = " + SQLitePath + "\n");
			cfg.write("username = user\n");
			cfg.write("password = passwd\n");
			cfg.flush();
			cfg.close();
		}
	}
	
	public void createSQLTable() {
		Properties ConfigProperties = new Properties();
		try {
			ConfigProperties.load(new FileInputStream(cfgDirPath + cfgName + ".conf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UsrName = ConfigProperties.getProperty("name","UserName");
		
		if(ConfigProperties.getProperty("SQL","false").startsWith("true")) {//SQL
			UsingSQL = true;
			host = ConfigProperties.getProperty("host","jdbc:mysql://localhost/db");
			userName = ConfigProperties.getProperty("username","user");
			passwd = ConfigProperties.getProperty("password","passwd");
		}else {//SQLite
			host = ConfigProperties.getProperty("host",SQLitePath);
		}
		checkSQLTable();
		ConfigProperties.clear();
	}
	
	public void createRSAKey() throws FileNotFoundException {
		Properties ConfigProperties = new Properties();
		try {
			ConfigProperties.load(new FileInputStream(cfgDirPath + cfgName + ".conf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		publicKeyPath = ConfigProperties.getProperty("publicKeyPath",cfgDirPath + cfgName + "/key/publicKey.pub");
		privateKeyPath = ConfigProperties.getProperty("privateKeyPath",cfgDirPath + cfgName + "/key/privateKey.key");;
		//如果public/private key不存在就生成一組
				if((!new File(publicKeyPath).exists())||(!new File(privateKeyPath).exists())) {
					Log.printMsg(ModuleName, Log.MsgType.warn, "Can not find keys, change key path to default path.");
					//publicKeyPath = "key/publicKey.pub";
					//privateKeyPath = "key/privateKey.key";
					if((!new File(publicKeyPath).exists())||(!new File(privateKeyPath).exists())) {
						Log.printMsg(ModuleName, Log.MsgType.info, "Generating RSA key pair.");
						RSA rsa2048 = new RSA(2048);
						try {
							rsa2048.RSAKeyGen(cfgDirPath + cfgName + "/key/");
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
	 * Table檢查
	 * @return
	 */
	private boolean checkSQLTable() {
		Connection conn = null;
		Statement stmt = null;
		if(UsingSQL) {//SQL
			try {
				conn = DriverManager.getConnection(host,userName,passwd);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				Log.printMsg(ModuleName, Log.MsgType.err, "Can not connect to SQL server.");
				e.printStackTrace();
				return false;
			}
			try {
				DatabaseMetaData dbm = conn.getMetaData();
				ResultSet tables = dbm.getTables(null, null, "USER", null);
				if (!tables.next()) {
					Log.printMsg(ModuleName, Log.MsgType.info, "Creating USER Table.");
					stmt = conn.createStatement();
					stmt.executeUpdate(sqlstmt);
				}
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {//SQLite
			try {
				//建DB
				Log.printMsg(ModuleName, Log.MsgType.info, "Building SQLite...");
				conn = DriverManager.getConnection(SQLitePath);
				stmt = conn.createStatement();
				//建Table
				stmt.executeUpdate(sqlstmt);
				conn.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.err.println( e.getClass().getName() + ": " + e.getMessage());
				return false;
			}
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
            Log.printMsg(ModuleName, Log.MsgType.info, "Getting public key.");
        } catch (NoSuchAlgorithmException e) {
        	Log.printMsg(ModuleName, Log.MsgType.err, "Could not reconstruct the public key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
        	Log.printMsg(ModuleName, Log.MsgType.err, "Could not reconstruct the public key");
        }

        return publicKey;
    }
	
	private PrivateKey getPrivateKey(byte[] keyBytes) {
        PrivateKey privateKey = null;
        try {
            KeyFactory kf = KeyFactory.getInstance("RSA");
            EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            privateKey = kf.generatePrivate(keySpec);
            Log.printMsg(ModuleName, Log.MsgType.info, "Getting private key.");
        } catch (NoSuchAlgorithmException e) {
        	Log.printMsg(ModuleName, Log.MsgType.err, "Could not reconstruct the public key, the given algorithm could not be found.");
        } catch (InvalidKeySpecException e) {
        	Log.printMsg(ModuleName, Log.MsgType.err, "Could not reconstruct the public key");
        }
        return privateKey;
    }
}
