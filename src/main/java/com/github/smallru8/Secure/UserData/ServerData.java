package com.github.smallru8.Secure.UserData;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;

import com.github.smallru8.Secure.SQL;
import com.github.smallru8.Secure.Secure;
import com.github.smallru8.Secure.Config.NormalConfig;

/**
 * Switch使用
 * @author smallru8
 *
 */
public class ServerData extends UsrData{
	
	private NormalConfig nc;
	private SQL sql;
	
	public ServerData(String switchName) {
		try {
			nc = new NormalConfig(switchName);//使用預設值可能會出問題
			nc.checkAll();
			setUserNameUUID();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SQL getSQL() {
		return sql;
	}
	
	/**
	 * 搭配getSQL用
	 * @return
	 * @throws SQLException
	 */
	public Connection getSQLConn() throws SQLException {
		if(nc.isDefault)
			return Secure.dc.getSQLConnection();
		else
			return nc.getSQLConnection();
	}
	
	private void setUserNameUUID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		//看Name是用Default.conf或<sessionName>.conf
		if(nc.isDefault) 
			Name = Secure.dc.UsrName;
		else
			Name = nc.UsrName;
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		MessageDigest digest = MessageDigest.getInstance("SHA-512");
		digest.reset();
		digest.update(Name.getBytes("utf-8"));
		UUID = String.format("%0128x", new BigInteger(1, digest.digest()));
	}
}
