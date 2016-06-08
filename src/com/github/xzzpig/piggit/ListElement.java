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
				JMenuItem upload = new JMenuItem("��ӵ��ϴ��б�");
				meunItems.add(upload);		
				upload.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent event) {
//						GUI_Main.gui.print("�ù���δ���");
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
				JMenuItem open = new JMenuItem("���ļ���");
				open.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent event) {
						GUI_Main.gui.list_local.showFile(file);
					}
				});
				meunItems.add(open);	
			}
			
			JMenuItem refresh = new JMenuItem("ˢ��");
			meunItems.add(refresh);
			refresh.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					GUI_Main.gui.list_local.showFile(GUI_Main.gui.list_local.getcurrentFile());
					GUI_Main.gui.print("ˢ��:"+GUI_Main.gui.list_local.getcurrentFile().getAbsolutePath());
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
			JMenuItem download = new JMenuItem("����");
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
			
			JMenuItem clipboard = new JMenuItem("�����������ӵ�������");
			clipboard.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					try {
						GUI_Main.gui.print("�Ѹ���:"+file.getContent().getDownloadUrl());
						TClipBoard.setSysClipboardText(file.getContent().getDownloadUrl());
					} catch (IOException e) {
						Debuger.print(e);
					}
				}
			});
			meunItems.add(clipboard);
			
			JMenuItem delete = new JMenuItem("ɾ��");
			delete.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					try {
						file.getContent().delete("by_PigGit");
						GUI_Main.gui.print("ɾ��:"+file.getPath()+file.getName());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			meunItems.add(delete);
		}
		else{
			JMenuItem open = new JMenuItem("���ļ���");
			open.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent event) {
					GUI_Main.gui.list_remote.showFile(file);
				}
			});
			meunItems.add(open);	
		}
		JMenuItem refresh = new JMenuItem("ˢ��");
		refresh.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent event) {
				GUI_Main.gui.list_remote.showFile(GUI_Main.gui.list_remote.getcurrentFile());
				GUI_Main.gui.print("ˢ��:"+GUI_Main.gui.list_remote.getcurrentFile().getAbsolutePath());
			}
		});
		meunItems.add(refresh);
	}

}
