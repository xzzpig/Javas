package com.github.xzzpig.ftp;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.log4j.Logger;

import com.github.xzzpig.pigapi.PigData;

public class FTPServer {
	private static String authToString(Authority auth) {
		if (auth instanceof WritePermission)
			try {
				Field field = auth.getClass()
						.getDeclaredField("permissionRoot");
				field.setAccessible(true);
				return "Write．" + field.get(auth);
			} catch (Exception e) {
			}
		else if (auth instanceof ConcurrentLoginPermission)
			try {
				Field field1 = auth.getClass().getDeclaredField(
						"maxConcurrentLogins");
				field1.setAccessible(true);
				Field field2 = auth.getClass().getDeclaredField(
						"maxConcurrentLoginsPerIP");
				field2.setAccessible(true);
				return "ConcurrentLogin．" + field1.get(auth) + "．"
				+ field2.get(auth);
			} catch (Exception e) {
			}
		else if (auth instanceof TransferRatePermission)
			try {
				Field field1 = auth.getClass().getDeclaredField(
						"maxDownloadRate");
				field1.setAccessible(true);
				Field field2 = auth.getClass()
						.getDeclaredField("maxUploadRate");
				field2.setAccessible(true);
				return "TransferRate．" + field1.get(auth) + "．"
				+ field2.get(auth);
			} catch (Exception e) {
			}
		return null;
	}

	private static Authority stringToAuthority(String auth) {
		String[] auths = auth.split("．");
		if (auth.startsWith("Write"))
			return new WritePermission(auth.split("．")[1]);
		else if (auth.startsWith("ConcurrentLogin"))
			return new ConcurrentLoginPermission(Integer.valueOf(auths[1]),
					Integer.valueOf(auths[2]));
		else if (auth.startsWith("TransferRate"))
			return new TransferRatePermission(Integer.valueOf(auths[1]),
					Integer.valueOf(auths[2]));
		return null;
	}

	private FtpServer server;

	private FtpServerFactory serverFactory;

	private List<BaseUser> users = new ArrayList<BaseUser>();

	public FTPServer() {
		this.serverFactory = new FtpServerFactory();
	}

	public FTPServer(File file) throws FileNotFoundException {
		this(new PigData(file));
	}

	public FTPServer(PigData data) {
		this();
		Logger.getRootLogger().info("开始加载data");
		List<PigData> userinfos = data.getSubList("user");
		for (PigData userinfo : userinfos) {
			BaseUser user = this.addUser(userinfo.getString("name"),
					userinfo.getString("password"),
					userinfo.getString("directory").replace('_', ':'),
					userinfo.getBoolean("enabled"));
			Logger.getRootLogger().info("成功加载账号信息:" + user.getName());
			List<String> auths = userinfo.getList("auth");
			for (String auth : auths)
				try {
					this.addAuthority(user, stringToAuthority(auth));
				} catch (Exception e) {
					continue;
				}
		}
		Logger.getRootLogger().info("data加载完成");
	}

	public FTPServer addAuthority(BaseUser user, Authority authority) {
		if ((user == null) || (authority == null))
			throw new NullPointerException("user或authroity不可为空");
		List<Authority> authoritys = user.getAuthorities();
		if (authoritys == null)
			authoritys = new ArrayList<Authority>();
		else
			authoritys = new ArrayList<Authority>(authoritys);
		authoritys.add(authority);
		user.setAuthorities(authoritys);
		return this;
	}

	public BaseUser addUser(String user, String password, String directory,
			boolean enable) {
		BaseUser baseUser = new BaseUser();
		if (user == null)
			throw new NullPointerException("user不可为null");
		baseUser.setName(user);
		if (password != null)
			baseUser.setPassword(password);
		if (directory != null)
			baseUser.setHomeDirectory(directory);
		baseUser.setEnabled(enable);
		this.users.add(baseUser);
		return baseUser;
	}

	public List<BaseUser> getUsers() {
		return this.users;
	}

	public FTPServer removeUser(String user) {
		List<BaseUser> dellist = new ArrayList<BaseUser>();
		for (BaseUser baseUser : this.users)
			if (baseUser.getName().equalsIgnoreCase(user))
				dellist.add(baseUser);
		this.users.removeAll(dellist);
		return this;
	}

	public FTPServer save(File file) {
		PigData pigData = new PigData();
		for (BaseUser baseUser : this.users) {
			pigData.set("user." + baseUser.getName() + ".name",
					baseUser.getName());
			pigData.set("user." + baseUser.getName() + ".password",
					baseUser.getPassword());
			pigData.set("user." + baseUser.getName() + ".directory",
					baseUser.getHomeDirectory());
			pigData.set("user." + baseUser.getName() + ".enabled",
					baseUser.getEnabled());
			List<Authority> authoritys = baseUser.getAuthorities();
			if (authoritys.size() != 0) {
				StringBuffer auth = new StringBuffer("[");
				for (Authority authority : authoritys)
					auth.append(authToString(authority) + ",");
				auth.deleteCharAt(auth.lastIndexOf(",")).append("]");
				pigData.set("user." + baseUser.getName() + ".auth",
						auth.toString());
			}
		}
		pigData.saveToFile(file);
		Logger.getRootLogger().info("data保存成功");
		return this;
	}

	public FTPServer start() throws Exception {
		Logger.getRootLogger().info("启动服务器ing");
		for (BaseUser baseUser : this.users)
			this.serverFactory.getUserManager().save(baseUser);
		this.server = this.serverFactory.createServer();
		this.server.start();
		Logger.getRootLogger().info("服务器启动完成");
		return this;
	}

	public FTPServer stop() {
		this.server.stop();
		Logger.getRootLogger().info("服务器停止成功");
		return this;
	}
}