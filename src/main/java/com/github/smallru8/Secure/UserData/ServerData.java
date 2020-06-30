package com.github.smallru8.Secure.UserData;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;

import com.github.smallru8.Secure.SQL;
import com.github.smallru8.Secure.Secure;
import com.github.smallru8.Secure.Config.NormalConfig;
import com.github.smallru8.util.SHA;

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
			sql = new SQL();
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
		UUID = SHA.SHA512(Name);
	}
}
