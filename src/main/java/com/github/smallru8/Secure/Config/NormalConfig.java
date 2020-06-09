package com.github.smallru8.Secure.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class NormalConfig extends DefaultConfig{

	public boolean isDefault = false;
	/**
	 * name傳入switch名稱
	 * @param name
	 * @throws IOException
	 */
	public NormalConfig(String name) throws IOException {
		super(name);
	}

	@Override
	public void createCfgFile() throws IOException {
		if(!new File(cfgDirPath + cfgName + ".conf").exists()) {
			new File(cfgDirPath + cfgName + ".conf").createNewFile();
			FileWriter cfg = new FileWriter(cfgDirPath + cfgName + ".conf");
			cfg.write("Default = true\n");//一般的config多出這行
			cfg.write("\n");
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
		if(!new File(cfgDirPath + "dest.conf").exists()) {
			FileWriter des = new FileWriter(cfgDirPath + "dest.conf");
			des.write("IP = ws://127.0.0.1\n");
			des.write("Port = 80\n");
			des.write("Switch = destSwitchName\n");
			des.flush();
			des.close();
		}
		Properties ConfigProperties = new Properties();
		ConfigProperties.load(new FileInputStream(cfgDirPath + cfgName + ".conf"));
		isDefault = ConfigProperties.getProperty("Default", "true").startsWith("true");
		ConfigProperties.clear();
	}
	
}
