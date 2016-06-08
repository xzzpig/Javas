package com.github.xzzpig.piggit;

import java.io.File;
import java.io.IOException;

import org.kohsuke.github.GitHub;

import com.github.xzzpig.pigapi.Debuger;
import com.github.xzzpig.pigapi.TData;

public class Var {
	public static TData data = new TData();

	public static GitHub git;
	
	public static File config = new File("./config.json");
	static{
		if(!config.exists())
			try {
				config.createNewFile();
			} catch (IOException e) {
				Debuger.print(e);
			}
	}
}
