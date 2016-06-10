package com.github.xzzpig.ftp.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

import net.miginfocom.swing.MigLayout;

import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.apache.ftpserver.usermanager.impl.TransferRatePermission;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import com.github.xzzpig.ftp.FTPServer;
import com.github.xzzpig.ftp.Vars;
import com.github.xzzpig.ftp.Voids;
import com.github.xzzpig.pigapi.PigData;

public class Gui_Server extends JFrame {
	public static Gui_Server self;
	private static final long serialVersionUID = 1L;
	public static void show(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Voids.serverLogBuild();
					Gui_Server frame = new Gui_Server();
					frame.setVisible(true);
					Voids.printServer("FTP服务器GUI已加载完毕");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	private JButton button_dir;
	private JButton button_save;
	private JButton button_write;
	private JCheckBox checkBox_connect;
	private JCheckBox checkBox_connect_edit;
	private JCheckBox checkBox_enable;
	private JCheckBox checkBox_rate;
	private JCheckBox checkBox_rate_download_edit;
	private JCheckBox checkBox_rate_upload_edit;
	private JCheckBox checkBox_write;
	private JComboBox<String> comboBox_rate_download;
	private JComboBox<String> comboBox_rate_upload;
	private JPanel contentPane;
	private JLabel label_account;
	private JLabel label_dir;
	private JLabel label_password;
	private JLayeredPane layeredPane;
	private CardLayout layout_card;
	private JList<BaseUser> list_user;

	private FocusAdapter nostr = new FocusAdapter() {
		@Override
		public void focusLost(FocusEvent arg0) {
			String text = ((JTextField) arg0.getComponent()).getText();
			if ((text == null) || (text.equalsIgnoreCase("")))
				((JTextField) arg0.getComponent()).setText("0");
		}
	};
	private KeyAdapter nubonly = new KeyAdapter() {
		@Override
		public void keyTyped(KeyEvent e) {
			int keyChar = e.getKeyChar();
			if ((keyChar < 48) || (keyChar > 57))
				e.consume();
		}
	};
	private JPanel panel;
	private JPanel panel_start;
	private JPanel panel_user;
	private JPanel panel_userconfig;
	private JPasswordField passwordField_password;
	private JScrollPane scrollPane_print;
	private JScrollPane scrollPane_user;
	public FTPServer server;
	private JSlider slider_connect_all;
	private JSlider slider_connect_single;
	private JSlider slider_rate_download;
	private JSlider slider_rate_upload;
	private JSplitPane splitPane;
	private JTextField textField_account;
	private JTextField textField_connect_all;
	private JTextField textField_connect_single;
	private JTextField textField_dir;
	private JTextField textField_rate_download;
	private JTextField textField_rate_upload;
	private JTextField textField_write;
	public JTextPane textPane_print;

	private JToggleButton toggleButton_start;

	public Gui_Server() {
		self = this;
		this.setTitle("FTP服务器");
		this.setDefaultCloseOperation(3);
		this.setBounds(100, 100, 640, 480);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(new BoxLayout(this.contentPane, 3));

		this.splitPane = new JSplitPane();
		this.splitPane.setOrientation(0);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setResizeWeight(0.36D);
		this.contentPane.add(this.splitPane);

		this.scrollPane_print = new JScrollPane();
		this.splitPane.setRightComponent(this.scrollPane_print);

		this.textPane_print = new JTextPane();
		this.textPane_print.setEditable(false);
		DefaultCaret caret = (DefaultCaret) this.textPane_print.getCaret();
		caret.setUpdatePolicy(2);
		this.scrollPane_print.setViewportView(this.textPane_print);

		this.layeredPane = new JLayeredPane();
		this.splitPane.setLeftComponent(this.layeredPane);
		this.layout_card = new CardLayout(0, 0);
		this.layeredPane.setLayout(this.layout_card);

		this.panel_user = new JPanel();
		this.layeredPane.add(this.panel_user, "userpanel");
		this.panel_user.setLayout(new MigLayout("", "[435.00,grow,fill]",
				"[77.66%,grow][grow,bottom]"));

		this.scrollPane_user = new JScrollPane();
		this.panel_user.add(this.scrollPane_user, "cell 0 0,grow");

		this.list_user = new JList<BaseUser>();

		this.list_user.addMouseListener(new MouseAdapter() {
			private void doubleClick(MouseEvent event) {
				Gui_Server.this.configUser(Gui_Server.this.list_user
						.getSelectedValue());
			}

			@Override
			public void mouseClicked(MouseEvent event) {
				if ((event.getClickCount() == 2) && (event.getButton() == 1))
					this.doubleClick(event);
				else if ((event.getClickCount() == 1)
						&& (event.getButton() == 1))
					Gui_Server.this.list_user.setToolTipText("(右键获取更多信息)");
				else if (event.getButton() == 3)
					this.rightClick(event);
			}

			private void rightClick(MouseEvent event) {
				BaseUser user = Gui_Server.this.list_user
						.getSelectedValue();
				JPopupMenu menu = Voids.getUserMenu(user);
				JMenuItem item_addNewUser = new JMenuItem("添加账号");
				item_addNewUser.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseReleased(MouseEvent e) {
						Gui_Server.this.configUser(null);
					}
				});
				menu.add(item_addNewUser);
				menu.show(event.getComponent(), event.getX(), event.getY());
			}
		});
		this.list_user.setBorder(new LineBorder(new Color(0, 0, 0)));
		this.list_user.setSelectionMode(0);
		this.list_user.setToolTipText("FPT账号");
		this.scrollPane_user.setViewportView(this.list_user);

		this.panel_start = new JPanel();
		this.panel_user.add(this.panel_start, "cell 0 1,grow");
		this.panel_start.setLayout(new GridLayout(0, 1, 0, 0));
		this.toggleButton_start = new JToggleButton("启动服务器");
		this.panel_start.add(this.toggleButton_start);
		this.toggleButton_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					if (Gui_Server.this.toggleButton_start.getText().contains(
							"启动")) {
						Gui_Server.this.server.start();
						Gui_Server.this.toggleButton_start.setText("关闭服务器");
					} else {
						Gui_Server.this.server.stop();
						Gui_Server.this.toggleButton_start.setText("启动服务器");
					}
				} catch (Exception e) {
					e.printStackTrace();
					Voids.printServer("服务器启动/关闭失败");
				}
			}
		});
		this.panel_userconfig = new JPanel();
		this.layeredPane.add(this.panel_userconfig, "userconfigpanel");
		this.panel_userconfig
		.setLayout(new MigLayout("", "[][grow]",
				"[12.5%][12.5%][12.5%][12.5%][12.5%][12.5%][12.5%][12.5%][12.5%]"));

		this.label_account = new JLabel("账号:");
		this.panel_userconfig.add(this.label_account,
				"cell 0 0,alignx trailing");

		this.textField_account = new JTextField();
		this.textField_account.setText("anonymous");
		this.textField_account.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String text = Gui_Server.this.textField_account.getText();
				if ((text == null) || (text.equalsIgnoreCase("")))
					Gui_Server.this.textField_account.setText("anonymous");
			}
		});
		this.textField_account.setToolTipText("密码");
		this.panel_userconfig.add(this.textField_account, "cell 1 0,growx");
		this.textField_account.setColumns(10);

		this.label_password = new JLabel("密码:");
		this.panel_userconfig.add(this.label_password,
				"cell 0 1,alignx trailing");

		this.passwordField_password = new JPasswordField();
		this.panel_userconfig
		.add(this.passwordField_password, "cell 1 1,growx");

		this.label_dir = new JLabel("默认目录:");
		this.panel_userconfig.add(this.label_dir, "cell 0 2,alignx trailing");

		this.textField_dir = new JTextField();
		this.textField_dir.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String text = Gui_Server.this.textField_dir.getText();
				if ((text == null) || (text.equalsIgnoreCase("")))
					Gui_Server.this.textField_dir.setText("./");
			}
		});
		this.textField_dir.setText("./");
		this.panel_userconfig.add(this.textField_dir, "flowx,cell 1 2,growx");
		this.textField_dir.setColumns(10);

		this.button_dir = new JButton("浏览");
		this.button_dir.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String dir = Gui_Server.this.textField_dir.getText();
				if ((dir == null) || (dir.equalsIgnoreCase("")))
					dir = "./";
				JFileChooser chooser = new JFileChooser(dir);
				chooser.setFileSelectionMode(1);
				chooser.showDialog(Gui_Server.self, "确认");
				if ((chooser.getSelectedFile() != null)
						&& (chooser.getSelectedFile().isDirectory()))
					dir = chooser.getSelectedFile().getAbsolutePath();
				Gui_Server.this.textField_dir.setText(dir);
			}
		});
		this.panel_userconfig.add(this.button_dir, "cell 1 2");

		this.checkBox_enable = new JCheckBox("启用");
		this.checkBox_enable.setSelected(true);
		this.panel_userconfig.add(this.checkBox_enable, "cell 0 3");

		this.checkBox_write = new JCheckBox("写入权限:");
		this.checkBox_write.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (Gui_Server.this.checkBox_write.isSelected()) {
					Gui_Server.this.textField_write.setEnabled(true);
					Gui_Server.this.button_write.setEnabled(true);
				} else {
					Gui_Server.this.textField_write.setEnabled(false);
					Gui_Server.this.button_write.setEnabled(false);
				}
			}
		});
		this.panel_userconfig.add(this.checkBox_write, "cell 0 4");

		this.textField_write = new JTextField();
		this.textField_write.setEnabled(false);
		this.textField_write
		.setToolTipText("\u5141\u8BB8\u5199\u5165\u7684\u76F8\u5BF9\u6587\u4EF6\u5939\u8DEF\u5F84");
		this.panel_userconfig.add(this.textField_write, "flowx,cell 1 4,growx");
		this.textField_write.setColumns(10);

		this.button_write = new JButton("浏览");
		this.button_write.setEnabled(false);
		this.button_write.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!Gui_Server.this.button_write.isEnabled())
					return;
				String dir = Gui_Server.this.textField_write.getText();
				if ((dir == null) || (dir.equalsIgnoreCase("")))
					dir = "./";
				JFileChooser chooser = new JFileChooser(dir);
				chooser.setFileSelectionMode(1);
				chooser.showDialog(Gui_Server.self, "确认");
				if ((chooser.getSelectedFile() != null)
						&& (chooser.getSelectedFile().isDirectory()))
					dir = chooser.getSelectedFile().getAbsolutePath();
				if (dir.contains(Gui_Server.this.textField_dir.getText()))
					Gui_Server.this.textField_write.setText(dir.replaceFirst(
							Gui_Server.this.textField_dir.getText(), ""));
				else
					Gui_Server.this.textField_write.setText("");
			}
		});
		this.panel_userconfig.add(this.button_write, "cell 1 4");

		this.checkBox_connect = new JCheckBox("限制连接数:");
		this.checkBox_connect.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (Gui_Server.this.checkBox_connect.isSelected()) {
					Gui_Server.this.textField_connect_all.setEnabled(true);
					Gui_Server.this.slider_connect_all.setEnabled(true);
					Gui_Server.this.textField_connect_single.setEnabled(true);
					Gui_Server.this.slider_connect_single.setEnabled(true);
					Gui_Server.this.checkBox_connect_edit.setEnabled(true);
				} else {
					Gui_Server.this.textField_connect_all.setEnabled(false);
					Gui_Server.this.slider_connect_all.setEnabled(false);
					Gui_Server.this.textField_connect_single.setEnabled(false);
					Gui_Server.this.slider_connect_single.setEnabled(false);
					Gui_Server.this.checkBox_connect_edit.setEnabled(false);
				}
			}
		});
		this.panel_userconfig.add(this.checkBox_connect, "cell 0 5");

		this.textField_connect_all = new JTextField();
		this.textField_connect_all.setEnabled(false);
		this.textField_connect_all.setToolTipText("总体最大连接数");
		this.textField_connect_all.setEditable(false);
		this.textField_connect_all.setText("10");
		this.panel_userconfig.add(this.textField_connect_all,
				"flowx,cell 1 5,growx");
		this.textField_connect_all.setColumns(10);

		this.slider_connect_all = new JSlider();
		this.slider_connect_all.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Gui_Server.this.textField_connect_all
				.setText(Gui_Server.this.slider_connect_all.getValue()
						+ "");
			}
		});
		this.slider_connect_all.setEnabled(false);
		this.slider_connect_all.setToolTipText("总体最大连接数");
		this.slider_connect_all.setValue(10);
		this.panel_userconfig.add(this.slider_connect_all, "cell 1 5");

		this.textField_connect_single = new JTextField();
		this.textField_connect_single.setEnabled(false);
		this.textField_connect_single.setToolTipText("单IP最大连接数");
		this.textField_connect_single.setEditable(false);
		this.textField_connect_single.setText("10");
		this.panel_userconfig.add(this.textField_connect_single, "cell 1 5");
		this.textField_connect_single.setColumns(10);

		this.slider_connect_single = new JSlider();
		this.slider_connect_single.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Gui_Server.this.textField_connect_single
				.setText(Gui_Server.this.slider_connect_single
						.getValue() + "");
			}
		});
		this.slider_connect_single.setEnabled(false);
		this.slider_connect_single.setToolTipText("单IP最大连接数");
		this.slider_connect_single.setValue(10);
		this.panel_userconfig.add(this.slider_connect_single, "cell 1 5");

		this.checkBox_connect_edit = new JCheckBox("文本修改");
		this.checkBox_connect_edit.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (Gui_Server.this.checkBox_connect_edit.isSelected()) {
					Gui_Server.this.textField_connect_all.setEditable(true);
					Gui_Server.this.textField_connect_single.setEditable(true);
				} else {
					Gui_Server.this.textField_connect_all.setEditable(false);
					Gui_Server.this.textField_connect_single.setEditable(false);
				}
			}
		});
		this.checkBox_connect_edit.setEnabled(false);
		this.checkBox_connect_edit.setToolTipText("是否启用文本框修改");
		this.panel_userconfig.add(this.checkBox_connect_edit, "cell 1 5");

		this.checkBox_rate = new JCheckBox("限制速度:");
		this.checkBox_rate.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (Gui_Server.this.checkBox_rate.isSelected()) {
					Gui_Server.this.textField_rate_download.setEnabled(true);
					Gui_Server.this.textField_rate_upload.setEnabled(true);
					Gui_Server.this.comboBox_rate_download.setEnabled(true);
					Gui_Server.this.comboBox_rate_upload.setEnabled(true);
					Gui_Server.this.slider_rate_download.setEnabled(true);
					Gui_Server.this.slider_rate_upload.setEnabled(true);
					Gui_Server.this.checkBox_rate_download_edit
					.setEnabled(true);
					Gui_Server.this.checkBox_rate_upload_edit.setEnabled(true);
				} else {
					Gui_Server.this.textField_rate_download.setEnabled(false);
					Gui_Server.this.textField_rate_upload.setEnabled(false);
					Gui_Server.this.comboBox_rate_download.setEnabled(false);
					Gui_Server.this.comboBox_rate_upload.setEnabled(false);
					Gui_Server.this.slider_rate_download.setEnabled(false);
					Gui_Server.this.slider_rate_upload.setEnabled(false);
					Gui_Server.this.checkBox_rate_download_edit
					.setEnabled(false);
					Gui_Server.this.checkBox_rate_upload_edit.setEnabled(false);
				}
			}
		});
		this.panel_userconfig.add(this.checkBox_rate, "cell 0 6");

		this.textField_rate_download = new JTextField();
		this.textField_rate_download.setText("100");
		this.textField_rate_download.setEnabled(false);
		this.textField_rate_download.setToolTipText("最大下载速度");
		this.textField_rate_download.setEditable(false);
		this.panel_userconfig.add(this.textField_rate_download,
				"flowx,cell 1 6,growx");
		this.textField_rate_download.setColumns(10);

		this.comboBox_rate_download = new JComboBox<String>();
		this.comboBox_rate_download.setEnabled(false);
		this.comboBox_rate_download.setToolTipText("单位");
		this.comboBox_rate_download.setModel(new DefaultComboBoxModel<String>(
				new String[] { "KB/s", "MB/s" }));
		this.panel_userconfig.add(this.comboBox_rate_download, "cell 1 6");

		this.slider_rate_download = new JSlider();
		this.slider_rate_download.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Gui_Server.this.textField_rate_download
				.setText(Gui_Server.this.slider_rate_download
						.getValue() + "");
			}
		});
		this.slider_rate_download.setEnabled(false);
		this.slider_rate_download.setToolTipText("最大下载速度");
		this.slider_rate_download.setValue(100);
		this.slider_rate_download.setMaximum(1024);
		this.panel_userconfig.add(this.slider_rate_download, "cell 1 6");

		this.checkBox_rate_download_edit = new JCheckBox("文本修改");
		this.checkBox_rate_download_edit.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (Gui_Server.this.checkBox_rate_download_edit.isSelected())
					Gui_Server.this.textField_rate_download.setEditable(true);
				else
					Gui_Server.this.textField_rate_download.setEditable(false);
			}
		});
		this.checkBox_rate_download_edit.setEnabled(false);
		this.checkBox_rate_download_edit.setToolTipText("是否启用文本框修改");
		this.panel_userconfig.add(this.checkBox_rate_download_edit, "cell 1 6");

		this.textField_rate_upload = new JTextField();
		this.textField_rate_upload.setText("100");
		this.textField_rate_upload.setEnabled(false);
		this.textField_rate_upload.setToolTipText("最大上传速度");
		this.textField_rate_upload.setEditable(false);
		this.textField_rate_upload.setColumns(10);

		this.textField_connect_all.addKeyListener(this.nubonly);
		this.textField_connect_single.addKeyListener(this.nubonly);
		this.textField_rate_download.addKeyListener(this.nubonly);
		this.textField_rate_upload.addKeyListener(this.nubonly);

		this.textField_connect_all.addFocusListener(this.nostr);
		this.textField_connect_single.addFocusListener(this.nostr);
		this.textField_rate_download.addFocusListener(this.nostr);
		this.textField_rate_upload.addFocusListener(this.nostr);

		this.panel_userconfig.add(this.textField_rate_upload,
				"flowx,cell 1 7,growx");

		this.comboBox_rate_upload = new JComboBox<String>();
		this.comboBox_rate_upload.setEnabled(false);
		this.comboBox_rate_upload.setToolTipText("单位");
		this.comboBox_rate_upload.setModel(new DefaultComboBoxModel<String>(
				new String[] { "KB/s", "MB/s" }));
		this.panel_userconfig.add(this.comboBox_rate_upload, "cell 1 7");

		this.slider_rate_upload = new JSlider();
		this.slider_rate_upload.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Gui_Server.this.textField_rate_upload
				.setText(Gui_Server.this.slider_rate_upload.getValue()
						+ "");
			}
		});
		this.slider_rate_upload.setEnabled(false);
		this.slider_rate_upload.setToolTipText("最大上传速度");
		this.slider_rate_upload.setValue(100);
		this.slider_rate_upload.setMaximum(1024);
		this.panel_userconfig.add(this.slider_rate_upload, "cell 1 7");

		this.checkBox_rate_upload_edit = new JCheckBox("文本修改");
		this.checkBox_rate_upload_edit.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (Gui_Server.this.checkBox_rate_upload_edit.isSelected())
					Gui_Server.this.textField_rate_upload.setEditable(true);
				else
					Gui_Server.this.textField_rate_upload.setEditable(false);
			}
		});
		this.checkBox_rate_upload_edit.setEnabled(false);
		this.checkBox_rate_upload_edit.setToolTipText("是否启用文本框修改");
		this.panel_userconfig.add(this.checkBox_rate_upload_edit, "cell 1 7");

		this.panel = new JPanel();
		this.panel_userconfig.add(this.panel, "cell 0 8 2 1,grow");
		this.panel.setLayout(new GridLayout(0, 1, 0, 0));

		this.button_save = new JButton("保存");
		this.button_save.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BaseUser user = new BaseUser();
				String name = Gui_Server.this.textField_account.getText();
				String password = new String(
						Gui_Server.this.passwordField_password.getPassword());
				String dir = Gui_Server.this.textField_dir.getText();
				if ((name == null) || (name.equalsIgnoreCase("")))
					name = "anonymous";
				if ((dir == null) || (dir.equalsIgnoreCase("")))
					dir = "./";
				Gui_Server.this.server.removeUser(name);
				user = Gui_Server.this.server.addUser(name, password, dir,
						Gui_Server.this.checkBox_enable.isSelected());
				if (Gui_Server.this.checkBox_write.isSelected())
					Gui_Server.this.server.addAuthority(
							user,
							new WritePermission(Gui_Server.this.textField_write
									.getText()));
				if (Gui_Server.this.checkBox_connect.isSelected()) {
					String all = Gui_Server.this.textField_connect_all
							.getText();
					String single = Gui_Server.this.textField_connect_single
							.getText();
					Gui_Server.this.server.addAuthority(user,
							new ConcurrentLoginPermission(Integer.valueOf(all)
									.intValue(), Integer.valueOf(single)
									.intValue()));
				}
				if (Gui_Server.this.checkBox_rate.isSelected()) {
					String down = Gui_Server.this.textField_rate_download
							.getText();
					String up = Gui_Server.this.textField_rate_upload.getText();
					Gui_Server.this.server
					.addAuthority(user, new TransferRatePermission(
							Integer.valueOf(down).intValue(), Integer
							.valueOf(up).intValue()));
				}
				Gui_Server.this.updateUserList();
				Gui_Server.this.layout_card.show(Gui_Server.this.layeredPane,
						"userpanel");
			}
		});
		this.panel.add(this.button_save);
		this.button_save.setHorizontalTextPosition(2);
		this.button_save.setCursor(Cursor.getPredefinedCursor(0));

		this.loadUser();
	}

	public void configUser(BaseUser user) {
		this.layout_card.show(this.layeredPane, "userconfigpanel");
		if (user == null) {
			user = new BaseUser();
			user.setName("anonymous");
			user.setHomeDirectory("./");
		}
		this.textField_account.setText(user.getName());
		this.passwordField_password.setText(user.getPassword());
		this.textField_dir.setText(user.getHomeDirectory());
		this.checkBox_enable.setSelected(user.getEnabled());
		this.checkBox_connect.setSelected(false);
		this.checkBox_connect_edit.setSelected(false);
		this.checkBox_rate.setSelected(false);
		this.checkBox_rate_download_edit.setSelected(false);
		this.checkBox_rate_upload_edit.setSelected(false);
		this.checkBox_write.setSelected(false);
		for (Authority auth : user.getAuthorities())
			if ((auth instanceof WritePermission))
				try {
					Field field = auth.getClass().getDeclaredField(
							"permissionRoot");
					field.setAccessible(true);
					this.checkBox_write.setSelected(true);
					this.textField_write.setText(field.get(auth) + "");
				} catch (Exception localException) {
				}
			else if ((auth instanceof ConcurrentLoginPermission))
				try {
					Field field1 = auth.getClass().getDeclaredField(
							"maxConcurrentLogins");
					field1.setAccessible(true);
					Field field2 = auth.getClass().getDeclaredField(
							"maxConcurrentLoginsPerIP");
					field2.setAccessible(true);
					this.checkBox_connect.setSelected(true);
					this.textField_connect_all.setText(field1.get(auth) + "");
					this.textField_connect_single
					.setText(field2.get(auth) + "");
				} catch (Exception localException1) {
				}
			else if ((auth instanceof TransferRatePermission))
				try {
					Field field1 = auth.getClass().getDeclaredField(
							"maxDownloadRate");
					field1.setAccessible(true);
					Field field2 = auth.getClass().getDeclaredField(
							"maxUploadRate");
					field2.setAccessible(true);
					this.checkBox_rate.setSelected(true);
					this.textField_rate_download.setText((((Integer) field1
							.get(auth)).intValue() / 1024) + "");
					this.textField_rate_upload.setText((((Integer) field2
							.get(auth)).intValue() / 1024) + "");
				} catch (Exception localException2) {
				}
	}

	private void loadUser() {
		this.server = new FTPServer(Vars.pdata);
		List<BaseUser> users = this.server.getUsers();
		this.list_user.clearSelection();
		this.list_user.setListData(users
				.toArray(new BaseUser[users.size()]));
		this.list_user.updateUI();
		Voids.printServer("FTP账号加载完毕");
	}

	public void updateUserList() {
		this.server.save(Vars.dataFile);
		try {
			Vars.pdata = new PigData(Vars.dataFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		this.loadUser();
	}
}