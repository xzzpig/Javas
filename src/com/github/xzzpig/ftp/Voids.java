package com.github.xzzpig.ftp;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import com.github.xzzpig.ftp.gui.Gui_Server;

class FTPAppener extends ConsoleAppender {

	public FTPAppener(PatternLayout layout) {
		super(layout);
	}

	@Override
	public synchronized void doAppend(LoggingEvent event) {
		if (event.getLevel() == Level.INFO)
			super.doAppend(event);
	}
}

public class Voids {

	private static String authToString(Authority auth) {
		if (auth instanceof WritePermission)
			try {
				Field field = auth.getClass()
						.getDeclaredField("permissionRoot");
				field.setAccessible(true);
				return "д��(����·��:" + field.get(auth) + ")";
			} catch (Exception e) {
			}
		else if (auth instanceof ConcurrentLoginPermission)
			try {
				Field field1 = auth.getClass().getField("maxConcurrentLogins");
				field1.setAccessible(true);
				Field field2 = auth.getClass().getField(
						"maxConcurrentLoginsPerIP");
				field2.setAccessible(true);
				return "����������(�������������:" + field1.get(auth) + "|��IP���������:"
				+ field2.get(auth) + ")";
			} catch (Exception e) {
			}
		else if (auth instanceof TransferRatePermission)
			try {
				Field field1 = auth.getClass().getField("maxDownloadRate");
				field1.setAccessible(true);
				Field field2 = auth.getClass().getField("maxUploadRate");
				field2.setAccessible(true);
				return "�ٶ�����(�������:" + field1.get(auth) + "|����ϴ�:"
				+ field2.get(auth) + ")";
			} catch (Exception e) {
			}
		return null;
	}

	private static JMenu getAuthMenu(BaseUser user) {
		JMenu menu = new JMenu("Ȩ��");
		for (Authority authority : user.getAuthorities())
			menu.add(new JMenuItem(authToString(authority)));
		return menu;
	}

	public static JPopupMenu getUserMenu(final BaseUser user) {
		if (user == null)
			return new JPopupMenu();
		JPopupMenu menu = new JPopupMenu(user.getName());
		menu.add(new JMenuItem("����:" + user.getPassword()));
		menu.add(new JMenuItem("Ĭ��·��:" + user.getHomeDirectory()));
		menu.add(new JCheckBoxMenuItem("����", user.getEnabled()));
		JMenuItem edit = new JMenuItem("�༭");
		edit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Gui_Server.self.configUser(user);
			}
		});
		menu.add(edit);
		JMenuItem remove = new JMenuItem("ɾ��");
		remove.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				Gui_Server.self.server.removeUser(user.getName());
			}
		});
		menu.add(remove);
		menu.add(getAuthMenu(user));
		return menu;
	}

	public static void noguiLogBuild() throws IOException {
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("[%5p][%d][%X{userName}][%X{remoteIp}]%m%n");
		FTPAppener appender = new FTPAppener(layout);
		appender.setImmediateFlush(true);
		appender.setWriter(new Writer() {
			FileWriter fileWriter = new FileWriter(new File("./log.txt"));
			PrintWriter printWriter = new PrintWriter(System.out);

			@Override
			public void close() throws IOException {
				this.fileWriter.close();
				this.printWriter.close();
			}

			@Override
			public void flush() throws IOException {
				this.fileWriter.flush();
				this.printWriter.flush();
			}

			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				this.fileWriter.write(cbuf, off, len);
				this.printWriter.write(cbuf, off, len);
			}
		});
		Logger.getRootLogger().addAppender(appender);
	}

	public static void printServer(Object object) {
		Logger.getRootLogger().info(object);
	}

	public static void serverLogBuild() throws IOException {
		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("[%5p][%d][%X{userName}][%X{remoteIp}]%m%n");
		FTPAppener appender = new FTPAppener(layout);
		appender.setImmediateFlush(true);
		appender.setWriter(new Writer() {
			FileWriter fileWriter = new FileWriter(new File("./log.txt"));
			PrintWriter printWriter = new PrintWriter(System.out);
			StringBuffer sb = new StringBuffer();

			@Override
			public void close() throws IOException {
				this.fileWriter.close();
				this.printWriter.close();
			}

			@Override
			public void flush() throws IOException {
				this.fileWriter.flush();
				this.printWriter.flush();
				Gui_Server.self.textPane_print
				.setText(Gui_Server.self.textPane_print.getText()
						+ this.sb.toString());
				this.sb = new StringBuffer();
			}

			@Override
			public void write(char[] cbuf, int off, int len) throws IOException {
				this.fileWriter.write(cbuf, off, len);
				this.printWriter.write(cbuf, off, len);
				this.sb.append(cbuf, off, len);
			}
		});
		Logger.getRootLogger().addAppender(appender);
	}
}