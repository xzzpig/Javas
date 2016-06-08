package com.github.xzzpig.piggit;

import java.io.IOException;

import com.github.xzzpig.piggit.gui.GUI_Main;

public class Main {

	public static void main(String[] args) throws IOException {
//		 GitHub git = GitHub.connectUsingPassword("xzzpig", "52xzzpig");
//		
//		  GHRepository res = git.getMyself().getRepository("Files");
//		 System.out.println(res.getDirectoryContent(""));
		build(args);
		if (Var.data.getStrings().containsKey("-gui"))
			GUI_Main.main(args);
	}

	public static void build(String[] args) {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			Var.data.setString(arg, "args[]");
			if (!arg.startsWith("-"))
				Var.data.setString(args[i - 1].replaceFirst("-", ""), arg);
		}
	}
}
