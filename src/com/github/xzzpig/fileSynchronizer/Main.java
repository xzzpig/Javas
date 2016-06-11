package com.github.xzzpig.fileSynchronizer;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.jgit.internal.storage.file.FileRepository;

public class Main {

	public static void main(String[] args) throws Exception {
		if (args.length < 2)
			return;
//		ClassLoader.getSystemClassLoader().loadClass("org.slf4j.impl.StaticLoggerBinder");
		String localPath = args[0];
		String remotePath = args[1];
		try {
			Git.cloneRepository().setURI(remotePath)
					.setDirectory(new File(localPath))
					.setCloneAllBranches(true).call();
		} catch (Exception e) {
		}
		FileRepository localRepo = new FileRepository(localPath + "/.git");
		Git git = new Git(localRepo);
		git.reset().setMode(ResetType.HARD).call();
		git.clean().setIgnore(true).setCleanDirectories(true).call();
		git.pull().call();
		git.close();
		System.out.println("Finish");
	}
}
