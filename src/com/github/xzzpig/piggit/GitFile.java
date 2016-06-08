package com.github.xzzpig.piggit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.github.GHBranch;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;

public class GitFile extends File {

	public enum Type {
		USER, REPOSITORY, CONTENT, BANCH
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5001834277203509350L;

	private GHUser user;
	private GHRepository repository;
	private GHContent content;

	private Type type;

	private File parent;

	private GHBranch branch;

	public GitFile(GHContent content, File parent) {
		super("./");
		this.content = content;
		this.parent = parent;
		type = Type.CONTENT;
	}

	public GitFile(GHUser user) {
		super("./");
		this.user = user;
		type = Type.USER;
	}

	public GitFile(GHRepository repository) {
		super("./");
		this.repository = repository;
		type = Type.REPOSITORY;
	}

	public GitFile(GHBranch banch) {
		super("./");
		this.branch = banch;
		type = Type.BANCH;
	}

	@Override
	public File getAbsoluteFile() {
		return this;
	}

	@Override
	public boolean isFile() {
		if (type == Type.CONTENT) {
			return content.isFile();
		} else if (type == Type.BANCH) {
			return false;
		} else if (type == Type.REPOSITORY) {
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isDirectory() {
		if (type == Type.CONTENT) {
			return content.isDirectory();
		} else if (type == Type.BANCH) {
			return true;
		} else if (type == Type.REPOSITORY) {
			return true;
		} else {
			return true;
		}
	}
	
	@Override
	public File getParentFile() {
		if (type == Type.CONTENT) {
			return (GitFile) parent;
		} else if (type == Type.BANCH) {
			return new GitFile(branch.getOwner());
		} else if (type == Type.REPOSITORY) {
			try {
				return new GitFile(repository.getOwner());
			} catch (IOException e) {
				e.printStackTrace();
				return this;
			}
		} else {
			return this;
		}
	}

	@Override
	public String getAbsolutePath() {
		String path = this.getName();
		if (type == Type.USER) {
			return path;
		}
		String name = getParentFile().getAbsolutePath();
		return name+"\\"+path;
	}

	@Override
	public File[] listFiles() {
		List<File> files = new ArrayList<File>();
		try {
			if (type == Type.CONTENT) {
				for (GHContent cont : content.listDirectoryContent())
					files.add(new GitFile(cont, this));
				return files.toArray(new File[0]);
			} else if (type == Type.BANCH) {
				for (GHContent cont : branch.getOwner().getDirectoryContent("",branch.getName()))
					files.add(new GitFile(cont, this));
				return files.toArray(new File[0]);
			} else if (type == Type.REPOSITORY) {
				for (GHBranch ban : repository.getBranches().values())
					files.add(new GitFile(ban));
				return files.toArray(new File[0]);
			} else {
				for (GHRepository res : user.getRepositories().values())
					files.add(new GitFile(res));
				return files.toArray(new File[0]);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new GitFile[] {};
		}
	}
	
	@Override
	public String getName() {
		if (type == Type.CONTENT) {
			return content.getName();
		} else if (type == Type.BANCH) {
			return branch.getName();
		} else if (type == Type.REPOSITORY) {
			return repository.getName();
		} else {
			try {
				return user.getName();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}
	}
	
	public GHContent getContent(){
		return content;
	}
	public GHBranch getBranch(){
		if(getType()==Type.BANCH)
			return branch;
		return ((GitFile) getParentFile()).getBranch();
	}
	public Type getType() {
		return type;
	}
	@Override
	public String getPath() {
		if (type == Type.CONTENT) {
			return content.getPath();
		} else if (type == Type.BANCH) {
			return getParentFile().getAbsolutePath();
		} else if (type == Type.REPOSITORY) {
			return getParentFile().getAbsolutePath();
		} else {
			try {
				return user.getName();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}
	}
	
}
