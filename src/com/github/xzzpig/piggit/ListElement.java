package com.github.xzzpig.piggit;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;

import com.github.xzzpig.pigapi.Debuger;
import com.github.xzzpig.pigapi.TClipBoard;
import com.github.xzzpig.pigapi.TData;
import com.github.xzzpig.piggit.gui.GUI_Main;

public class ListElement {
	public enum Type {
		GITFILE,LOCALFILE
	}

	public List<JMenuItem> meunItems = new ArrayList<JMenuItem>();
	private TData data = new TData();
	public Type type;

	public ListElement(final File file){
		if(file instanceof GitFile){
			gitbuild((GitFile)file);
		}
		else{
			type = Type.LOCALFILE;
			if(file.isFile()){
				JMenuItem upload = new JMenuItem("添加到上传列表");
				meunItems.add(upload);		
				upload.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent event) {
//						GUI_Main.gui.print("该功能未完成");
						try {
							GitFile remoteFile = (GitFile)GUI_Main.gui.list_remote.getcurrentFile();
							File localFile =GUI_Main.gui.list_local.getSelectedFile();
							UpLoadElement uploadElement = new UpLoadElement(localFile);
							uploadElement.start(remoteFile);
							GUI_Main.gui.print(uploadElement);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
			else {
				JMenuItem open = new JMenuItem("打开文件夹");
				open.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent event) {
						GUI_Main.gui.list_local.showFile(file);
					}
				});
				meunItems.add(open);	
			}
			
			JMenuItem refresh = new JMenuItem("刷新");
			meunItems.add(refresh);
			refresh.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					GUI_Main.gui.list_local.showFile(GUI_Main.gui.list_local.getcurrentFile());
					GUI_Main.gui.print("刷新:"+GUI_Main.gui.list_local.getcurrentFile().getAbsolutePath());
				}
			});
		}
	}
	public ListElement(final GitFile file) {
		gitbuild(file);
	}
	private void gitbuild(final GitFile file) {
		type = Type.GITFILE;
		data.setObject("gitfile",file);
		if(file.isFile()){
			JMenuItem download = new JMenuItem("下载");
			download.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					try {
						GitFile remoteFile = (GitFile)GUI_Main.gui.list_remote.getSelectedFile();
						File localFile =new File(GUI_Main.gui.list_local.getcurrentFile(),remoteFile.getName());
						DownloadElement downloadElement = new DownloadElement(remoteFile);
						downloadElement.start(localFile);
						GUI_Main.gui.print(downloadElement);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			meunItems.add(download);	
			
			JMenuItem clipboard = new JMenuItem("复制下载链接到剪贴板");
			clipboard.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					try {
						GUI_Main.gui.print("已复制:"+file.getContent().getDownloadUrl());
						TClipBoard.setSysClipboardText(file.getContent().getDownloadUrl());
					} catch (IOException e) {
						Debuger.print(e);
					}
				}
			});
			meunItems.add(clipboard);
			
			JMenuItem delete = new JMenuItem("删除");
			delete.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					try {
						file.getContent().delete("by_PigGit");
						GUI_Main.gui.print("删除:"+file.getPath()+file.getName());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			meunItems.add(delete);
		}
		else{
			JMenuItem open = new JMenuItem("打开文件夹");
			open.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					GUI_Main.gui.list_remote.showFile(file);
				}
			});
			meunItems.add(open);	
		}
		JMenuItem refresh = new JMenuItem("刷新");
		refresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				GUI_Main.gui.list_remote.showFile(GUI_Main.gui.list_remote.getcurrentFile());
				GUI_Main.gui.print("刷新:"+GUI_Main.gui.list_remote.getcurrentFile().getAbsolutePath());
			}
		});
		meunItems.add(refresh);
	}

}
