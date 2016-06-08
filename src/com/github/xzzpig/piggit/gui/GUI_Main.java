package com.github.xzzpig.piggit.gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataListener;

import net.miginfocom.swing.MigLayout;

import org.kohsuke.github.GitHub;

import com.github.xzzpig.pigapi.Debuger;
import com.github.xzzpig.pigapi.TData;
import com.github.xzzpig.pigapi.json.JSONObject;
import com.github.xzzpig.pigapi.json.JSONTokener;
import com.github.xzzpig.piggit.DownloadElement;
import com.github.xzzpig.piggit.GitFile;
import com.github.xzzpig.piggit.ListElement;
import com.github.xzzpig.piggit.Var;

public class GUI_Main extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6346341661932197911L;

	public static GUI_Main gui;

	private JTextField textField_zh;
	private JPasswordField passwordField_mm;
	private JButton button;
	private JTabbedPane tabbedPane_main;
	private JPanel panel_config;
	public GitJList<String> list_local;
	public GitJList<String> list_remote;
	private JScrollPane scrollPane_local;
	private JScrollPane scrollPane_remote;
	private JList<Object> list_print;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Debuger.debug = true;
					JSONTokener tokener = new JSONTokener(new FileReader(Var.config));
					JSONObject jsonObject = new JSONObject(tokener);
					Var.data = new TData(jsonObject.getString("data"));
					GUI_Main frame = new GUI_Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUI_Main() {
		gui = this;
		setType(Type.NORMAL);
		setTitle("PigGit");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 480);
		getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		tabbedPane_main = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane_main.setBorder(null);
		getContentPane().add(tabbedPane_main);

		JPanel panel_accounter = new JPanel();
		tabbedPane_main.addTab("文件", null, panel_accounter, null);
		panel_accounter.setLayout(new MigLayout("", "[100.00%,leading]", "[72%][grow]"));
		
		splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		splitPane.setOneTouchExpandable(true);
		panel_accounter.add(splitPane, "cell 0 0,grow");
		
		scrollPane_local = new JScrollPane();
		splitPane.setLeftComponent(scrollPane_local);
		
				list_local = new GitJList<String>();
				scrollPane_local.setViewportView(list_local);
				
				popupMenu = new JPopupMenu();
				addPopup(list_local, popupMenu);
				
				scrollPane_remote = new JScrollPane();
				splitPane.setRightComponent(scrollPane_remote);
				
						list_remote = new GitJList<String>();
						scrollPane_remote.setViewportView(list_remote);
						
						popupMenu_1 = new JPopupMenu();
						addPopup(list_remote, popupMenu_1);
						list_remote.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent event) {
								if(event.getClickCount()==2&&event.getButton()==MouseEvent.BUTTON1)
									doubleClick(event);
								else if (event.getButton()==MouseEvent.BUTTON3)
									rightClick(event);
									
							}

							private void doubleClick(MouseEvent event) {
								System.err.println("double");
								File file = list_remote.getSelectedFile();
								if (file.isDirectory())
									list_remote.showFile(file);
								else{
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
							}
							private void rightClick(MouseEvent event){
								JPopupMenu menu = new JPopupMenu();
								for(JMenuItem element:new ListElement(list_remote.getSelectedFile()).meunItems){
									menu.add(element);
								}
								menu.show(event.getComponent(), event.getX(), event.getY());
							}
						});
				list_local.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent event) {
						if(event.getClickCount()==2&&event.getButton()==MouseEvent.BUTTON1)
							doubleClick(event);
						else if (event.getButton()==MouseEvent.BUTTON3)
							rightClick(event);
							
					}

					private void doubleClick(MouseEvent event) {
						System.err.println("double");
						File file = list_local.getSelectedFile();
						if (file.isDirectory())
							list_local.showFile(file);
					}
					private void rightClick(MouseEvent event){
						JPopupMenu menu = new JPopupMenu();
						for(JMenuItem element:new ListElement(list_local.getSelectedFile()).meunItems){
							menu.add(element);
						}
						menu.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent event) {
							}
						});
						menu.show(event.getComponent(), event.getX(), event.getY());
					}
				});
				
				scrollPane = new JScrollPane();
				panel_accounter.add(scrollPane, "cell 0 1,grow");
				scrollPane.setAutoscrolls(true);
				list_print = new JList<Object>();
				scrollPane.setViewportView(list_print);

		panel_config = new JPanel();
		tabbedPane_main.addTab("设置", null, panel_config, null);
		panel_config.setLayout(new MigLayout("", "[100.35%,grow]",
				"[80.00%][grow]"));

		JTabbedPane tabbedPane_zhmm = new JTabbedPane(JTabbedPane.TOP);
		panel_config.add(tabbedPane_zhmm, "cell 0 0,grow");

		JPanel panel_zhmm = new JPanel();
		tabbedPane_zhmm.addTab("账号信息", null, panel_zhmm, null);
		panel_zhmm.setLayout(new MigLayout("", "[20:20.00%][grow]", "[50.00px][50.00px]"));

		JLabel label_zh = new JLabel("\u8D26\u53F7:");
		label_zh.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_zhmm.add(label_zh, "cell 0 0,alignx trailing");

		textField_zh = new JTextField();
		panel_zhmm.add(textField_zh, "cell 1 0,growx");
		textField_zh.setColumns(10);

		JLabel label_mm = new JLabel("\u5BC6\u7801:");
		label_mm.setHorizontalAlignment(SwingConstants.RIGHT);
		panel_zhmm.add(label_mm, "cell 0 1,alignx trailing");

		passwordField_mm = new JPasswordField();
		panel_zhmm.add(passwordField_mm, "cell 1 1,growx");

		button = new JButton("\u4FDD\u5B58");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				Var.data.setString("user", textField_zh.getText());
				Var.data.setString("password",
						new String(passwordField_mm.getPassword()));
				
//				JSONWriter writer;
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("data",Var.data);
					jsonObject.write(new FileWriter(Var.config,false)).close();
//					writer = new JSONWriter(new FileWriter(Var.config,false));
//					writer.object().key("data").value(Var.data).endObject().end();;
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				print("开始尝试登录");
				start();
			}
		});
		panel_config.add(button, "cell 0 1,grow");
		list_print.setAutoscrolls(true);
		start();
	}

	private void start() {
		String user = Var.data.getString("user");
		String password = Var.data.getString("password");
		if (user == null || password == null) {
			tabbedPane_main.setSelectedIndex(1);
			print("用户名或密码为空,自动转跳至设置页面");
		} else if (login()) {
			list_local.showFile(new File("./"));
			try {
				list_remote.showFile(new GitFile(Var.git.getMyself()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			tabbedPane_main.setSelectedIndex(0);
		}
	}

	private boolean login() {
		try {
			Var.git = GitHub.connectUsingPassword(Var.data.getString("user"),
					Var.data.getString("password"));
			Var.git.checkApiUrlValidity();
		} catch (Exception e) {
			Debuger.print(e);
			tabbedPane_main.setSelectedIndex(1);
			print("登录失败");
			print(e);
			return false;
		}
		return true;
	}
	private List<Object> printList = new ArrayList<Object>();
	private JPopupMenu popupMenu;
	private JPopupMenu popupMenu_1;
	private JSplitPane splitPane;
	public GUI_Main print(Object message){
		if(!message.equals(""))
			printList.add(0,message);
		list_print.setListData(printList.toArray());
		list_print.setSelectedIndex(list_print.getModel().getSize()-1);
		return this;
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
	public class GitJList<T> extends JList<T> {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4671812035818284088L;
		private TData data = new TData();
		private File current;
		
		@SuppressWarnings("unchecked")
		public void showFile(File file) {
			if (file == null)
				return;
			file = file.getAbsoluteFile();
			if (file.isFile())
				file = file.getParentFile();
			final File finfile = file;
			GUI_Main.gui.print("进入文件夹"+file.getAbsolutePath());
			data.getObjects().clear();
			data.setObject("...", file.getParentFile());
			data.setObject("当前:" + file.getAbsolutePath(), file);
			current = file;
			for (File file2 : file.listFiles()) {
				String type = "●";
				if (file2.isDirectory())
					type = "○";
				data.setObject(
						type + file2.getName(),
						file2);
			}
			ListModel<String> model = new ListModel<String>() {
				List<String> list = new ArrayList<String>();
				{
					list.add("当前:" + finfile.getAbsolutePath());
					list.add("...");
					for (String str : data.getObjects().keySet()) {
						if (str.startsWith("○")) {
							list.add(str);
						}
					}
					for (String str2 : data.getObjects().keySet()) {
						if (str2.startsWith("●")) {
							list.add(str2);
						}
					}
				}
				
				public void addListDataListener(ListDataListener l) {
				}
				
				public String getElementAt(int index) {
					return list.get(index);
				}
				
				public int getSize() {
					return list.size();
				}
				
				public void removeListDataListener(ListDataListener l) {
				}
			};
			// this.setListData((T[]) data.getObjects().keySet().toArray(new
			// String[0]));
			this.setModel((ListModel<T>) model);
			this.setVisibleRowCount(this.getModel().getSize());
			this.updateUI();
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public T getSelectedValue() {
			return (T) ((File) data.getObject((String) super.getSelectedValue()))
					.getAbsolutePath();
		}
		
		public File getSelectedFile() {
			return ((File) data.getObject((String) super.getSelectedValue()));
		}

		public File getcurrentFile() {
			return current;
		}
	}
}
