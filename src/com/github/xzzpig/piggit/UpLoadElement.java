package com.github.xzzpig.piggit;

import java.io.File;
import java.io.FileInputStream;

import org.kohsuke.github.GHContentUpdateResponse;

import com.github.xzzpig.pigapi.Debuger;
import com.github.xzzpig.piggit.GitFile.Type;
import com.github.xzzpig.piggit.gui.GUI_Main;

public class UpLoadElement {
	public int BUFFER_SIZE = 1024;

	private long uploadsize;
	private Exception error;
	private boolean finish;
	private long size;
	private File file;
	private String name;

	public UpLoadElement(File file) throws Exception {
		this.file = file;
	}

	public long getUploadSize() {
		return uploadsize;
	}

	public Exception getError() {
		return error;
	}

	public short getPrecent() {
		if (size == 0)
			return 0;
		return (short) (((float) uploadsize / (float) size) * 100);
	}

	public long getSize() {
		return size;

	}

	public long getSpeed(int logtime) {
		long start = uploadsize;
		try {
			Thread.sleep(logtime);
		} catch (InterruptedException e) {
		}
		return (long) ((double) (uploadsize - start) / (double) logtime);
	}

	public File getFile() {
		return file;
	}

	public boolean hasError() {
		return this.error != null;
	}

	public boolean isFinished() {
		return finish;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	public String getFileName(){
		return name;
	}
	
	public UpLoadElement start(final GitFile gfile) {
		new Thread(new Runnable() {
			public void run() {
				try {
					GitFile outFile = gfile;
					if(!(outFile.getType()==Type.CONTENT||outFile.getType()==Type.BANCH))
						return;
					if(outFile.isFile())
						outFile = (GitFile) outFile.getParentFile();
					String path;
					if(((GitFile) outFile.getParentFile()).getType() == Type.CONTENT)
						path = outFile.getParentFile().getPath();
					else
						path = "";
					GitFile upFile = null;
					for(File subFile:outFile.listFiles()){
						if(file.getName().equalsIgnoreCase(subFile.getName()))
							upFile = (GitFile) subFile;
					}
					if(upFile==null){
						if(outFile.getContent()!=null){
							GHContentUpdateResponse up = outFile.getContent().getOwner().createContent(file.getName(),"by_PigGit",path,outFile.getBranch().getName());
							upFile = new GitFile(up.getContent(),outFile);
						}
						else{
							GHContentUpdateResponse up = outFile.getBranch().getOwner().createContent(file.getName(),"by_PigGit",path,outFile.getBranch().getName());
							upFile = new GitFile(up.getContent(),outFile);
						}
					}
					name = upFile.getName();
					FileInputStream reader = new FileInputStream(file);
					byte[] buffer = new byte[BUFFER_SIZE];
					byte[] data = new byte[0];
					while (true) {
						int length = reader.read(buffer);
						if (length == -1)
							break;
						size += length;
						byte[] buffer2 = new byte[buffer.length + data.length];
						System.arraycopy(buffer, 0, buffer2, 0, buffer.length);
						System.arraycopy(data, 0, buffer2, buffer.length,
								data.length);
						data = buffer2;
					}
					upFile.getContent().update(data, "by_PigGit");
					reader.close();
//					URLConnection conn = url.openConnection();
//					size = conn.getContentLength();
//					InputStream in = conn.getInputStream();
//					if (outFile.isDirectory()) {
//						outFile = new File(outFile, url.getFile());
//						outFile.createNewFile();
//					} else if (outFile.isFile()) {
//						if (!outFile.exists())
//							outFile.createNewFile();
//					}
//					FileOutputStream out = new FileOutputStream(outFile, false);
//					byte[] buffer = new byte[BUFFER_SIZE];
//					while (true) {
//						int length = in.read(buffer);
//						if (length == -1)
//							break;
//						uploadsize += length;
//						out.write(buffer, 0, length);
//					}
//					in.close();
//					out.close();
				} catch (Exception e) {
					Debuger.print(e);
					error = e;
				} finally {
					finish = true;
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				while (!isFinished()) {
					GUI_Main.gui.print("");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				GUI_Main.gui.list_remote.showFile(GUI_Main.gui.list_remote.getcurrentFile());
				GUI_Main.gui.print(name+"上传完成");
			}
		}).start();
		return this;
	}
	
	@Override
	public String toString() {
		return "上传"+file.getName()+"\t->\t"+name+"\t["+getSize()+"]";
	}
}