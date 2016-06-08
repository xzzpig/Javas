package com.github.xzzpig.piggit;

import java.io.File;

import com.github.xzzpig.pigapi.TDownload;
import com.github.xzzpig.piggit.gui.GUI_Main;

public class DownloadElement extends TDownload{
	private String ghname;
	public DownloadElement(GitFile file) throws Exception{
		super(file.getContent().getDownloadUrl());
		ghname = file.getName();
	}
	
	@Override
	public String toString() {
		return "下载"+ghname+"\t->\t"+getFileName()+"\t["+getDownloadSize()+"/"+getSize()+"("+getPrecent()+"%)\t"+0+"KB/ms]";
	}
	
	@Override
	public TDownload start(File savedFile) {
		super.start(savedFile);
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
				GUI_Main.gui.list_local.showFile(GUI_Main.gui.list_local.getcurrentFile());
				GUI_Main.gui.print(ghname+"下载完成");
			}
		}).start();
		return this;
	}
}
