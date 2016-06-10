package com.github.xzzpig.ftp;

import java.io.File;

import com.github.xzzpig.ftp.gui.Gui_Select;
import com.github.xzzpig.ftp.gui.Gui_Server;
import com.github.xzzpig.pigapi.PigData;

public class Main {

	public static void build(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			Vars.data.setString(arg, "args[]");
			if (!arg.startsWith("-"))
				Vars.data.setString(args[i - 1].replaceFirst("-", ""), arg);
		}
	}

	public static void main(String[] args) throws Exception {
		build(args);
		Vars.dataFile = new File("./data.pigdata");
		try {
			Vars.dataFile.createNewFile();
			Vars.pdata = new PigData(Vars.dataFile);
		} catch (Exception e) {
			Vars.pdata = new PigData();
		}
		//
		// server.addAuthority(server.addUser("xzzpig", "52xzzpig", "./",
		// true),new WritePermission());
		//
		// server.save(Vars.dataFile);args[]
		if ((Vars.data.getString("type") + "").equalsIgnoreCase("Client"))
			;
		else if ((Vars.data.getString("type") + "").equalsIgnoreCase("Server"))
			Gui_Server.show(args);
		else if ((Vars.data.getString("type") + "").equalsIgnoreCase("NoGui")) {
			Voids.noguiLogBuild();
			new FTPServer(Vars.pdata).start();
			while (true) {
			}
		} else
			Gui_Select.show(args);
	}

}
