package com.github.smallru8.Secure.UserData;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.interfaces.RSAPrivateKey;
import java.util.Properties;

import com.github.smallru8.Secure.Secure;
import com.github.smallru8.Secure.Config.NormalConfig;
import com.github.smallru8.util.SHA;

/**
 * 這個Class會被包在WS client的Class中
 * WS client發出請求給server時需要的資料
 * AES256加密解密
 * RSA2048私鑰
 * @author smallru8
 *
 */
public class ClientData extends UsrData{
	
	public String sessionName;//這個連線的名稱 使用者輸入(作為config命名用)
	
	public String destSwitchName;//目標Switch名稱
	public String IPaddr;//server ip
	public int port;//server port
	private NormalConfig nc;//讀config拿到private key
	
	public ClientData(String sessionName) throws IOException {
		this.sessionName = sessionName;
		nc = new NormalConfig(this.sessionName);
		nc.checkAll();
		readDestCfg();
		try {
			setUserNameUUID();//設定Name跟UUID
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public RSAPrivateKey getPrivateKey() {
		if(nc.isDefault)//使用Default設定
			return Secure.dc.getPrivateKey();
		return nc.getPrivateKey();
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
	
	private void readDestCfg() {
		Properties ConfigProperties = new Properties();
		try {
			ConfigProperties.load(new FileInputStream(nc.cfgDirPath + "dest.conf"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IPaddr = ConfigProperties.getProperty("IP","ws://127.0.0.1");
		port = Integer.parseInt(ConfigProperties.getProperty("Port","80"));
		destSwitchName = ConfigProperties.getProperty("Switch","desSwitchName");
		ConfigProperties.clear();
	}
	
}
