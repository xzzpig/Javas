package com.github.xzzpig.ftp.gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class Gui_Select extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void show(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui_Select frame = new Gui_Select();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public Gui_Select() {
		this.setAlwaysOnTop(true);
		this.setTitle("GUI\u9009\u62E9");
		this.setType(Type.POPUP);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setBounds(100, 100, 450, 200);
		this.contentPane = new JPanel();
		this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setContentPane(this.contentPane);
		this.contentPane.setLayout(new MigLayout("", "[424px,fill]",
				"[60.00%,top][grow,bottom]"));

		JPanel panel = new JPanel();
		this.contentPane.add(panel, "cell 0 0,grow");
		panel.setLayout(new GridLayout(0, 1, 0, 0));

		JLabel lblgui = new JLabel(
				"\u4F60\u672A\u9009\u62E9\u542F\u52A8\u7684GUI");
		lblgui.setFont(new Font("ËÎÌו", Font.PLAIN, 30));
		lblgui.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblgui);

		JLabel lblgui_1 = new JLabel("\u8BF7\u9009\u62E9GUI");
		lblgui_1.setFont(new Font("ËÎÌו", Font.BOLD, 30));
		lblgui_1.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblgui_1);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		this.contentPane.add(splitPane, "cell 0 1,grow");

		JButton btnNewButton = new JButton("\u5BA2\u6237\u7AEF");
		btnNewButton.setToolTipText("\u6682\u672A\u5B8C\u6210");
		btnNewButton.setEnabled(false);
		splitPane.setLeftComponent(btnNewButton);

		JButton btnNewButton_1 = new JButton("\u670D\u52A1\u7AEF");
		btnNewButton_1.setToolTipText("\u542F\u52A8\u670D\u52A1\u7AEF");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Gui_Server.show(null);
				Gui_Select.this.dispose();
			}
		});
		splitPane.setRightComponent(btnNewButton_1);
	}

}
