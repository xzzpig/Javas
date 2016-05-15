package com.github.xzzpig.sudoku;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.github.xzzpig.sudoku.element.Element;

public class DisplayWindow extends JFrame {

	public static DisplayWindow window;
	/**
	 * 
	 */
	private static final long serialVersionUID = 6303390628258709028L;

	private JPanel contentPane;
	protected JTable displaytable;
	private JButton solveButton;
	private JButton clearButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DisplayWindow frame = new DisplayWindow();
					frame.setVisible(true);
					window = frame;
					@SuppressWarnings("unused")
					int[][] example = new int[][] {
							{ 0, 3, 0, 0, 6, 7, 0, 9, 0 },
							{ 5, 0, 0, 8, 2, 0, 7, 0, 1 },
							{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 1, 3, 7, 0, 0, 0, 0 },
							{ 0, 0, 3, 2, 1, 9, 5, 0, 0 },
							{ 0, 0, 0, 0, 8, 4, 2, 0, 0 },
							{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 8, 0, 4, 0, 3, 6, 0, 0, 5 },
							{ 0, 6, 0, 9, 5, 0, 0, 8, 0 } };
					example = new int[][] { { 0, 3, 0, 0, 6, 7, 0, 9, 0 },
							{ 5, 0, 0, 8, 2, 0, 7, 0, 1 },
							{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 0, 0, 1, 0, 7, 0, 0, 0, 0 },
							{ 0, 0, 0, 2, 1, 0, 0, 0, 0 },
							{ 0, 0, 0, 0, 0, 4, 2, 0, 0 },
							{ 0, 0, 0, 0, 0, 0, 0, 0, 0 },
							{ 8, 0, 4, 0, 0, 6, 0, 0, 5 },
							{ 0, 6, 0, 9, 5, 0, 0, 8, 0 } };
					window.getBoard().show();
					// new Board(example).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DisplayWindow() {
		setTitle("\u6570\u72EC(Sudoku)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent event) {
				setBounds(getX(), getY(), getWidth(), getWidth() + 50);

				displaytable.setBounds(0, 0, getWidth() - 20, getWidth() - 20);
				displaytable.setRowHeight(displaytable.getHeight() / 9);
				displaytable.setFont(new Font("\u5B8B\u4F53", Font.PLAIN,
						(getWidth() - 20) * 32 / 230));

				solveButton.setBounds(0, displaytable.getHeight(),
						(getHeight() - 70) / 2, 30);
				clearButton.setBounds(solveButton.getWidth(),
						solveButton.getY(), solveButton.getWidth(),
						solveButton.getHeight());
			}
		});
		setBounds(100, 100, 250, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		displaytable = new JTable(new SudokuModel());
		displaytable.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent event) {
				int row = displaytable.rowAtPoint(event.getPoint());
				int col = displaytable.columnAtPoint(event.getPoint());
				if (row > -1 && col > -1) {
					int value = (int) displaytable.getValueAt(row, col);
					if (value != 0)
						displaytable
								.setToolTipText("(" + col + "," + row + ")");
					else {
						Board board = window.getBoard().solve();
						Element element = board.getElementss()[col][row];
						displaytable.setToolTipText(element.getLocation()
								+ "可能值:" + element.getString());// 关闭提示
					}
				}
			}
		});
		displaytable.setFont(new Font("\u5B8B\u4F53", displaytable.getFont()
				.getStyle(), 32));
		displaytable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int x = displaytable.getSelectedColumn(), y = displaytable
						.getSelectedRow();
				String local = "x=" + x + ";y=" + y;
				Board board = window.getBoard();
				// System.out.println(displaytable.getModel().isCellEditable(y,
				// x));
				Element element = board.getElementss()[x][y];
				// displaytable.setToolTipText(element.getString());
				String ele = "number=" + element.toString();
				String row = "行="
						+ Arrays.toString(element.getLocation().getRow()
								.getElements());
				String column = "列="
						+ Arrays.toString(element.getLocation().getColumn()
								.getElements());
				String block = "区="
						+ element.getLocation().getBlock().getNumber()
						+ Arrays.toString(element.getLocation().getBlock()
								.getElements());
				System.out.println(local + "\n" + ele + "\n" + row + "\n"
						+ column + "\n" + block);

			}
		});
		displaytable.setBounds(0, 0, 230, 230);
		displaytable.setRowHeight(displaytable.getHeight() / 9);
		displaytable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		contentPane.add(displaytable);

		solveButton = new JButton("\u89E3\u51B3");
		solveButton.setBackground(Color.WHITE);
		solveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				solveButton.setBackground(Color.LIGHT_GRAY);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						solveButton.setBackground(Color.WHITE);
						if (getBoard().solve().show().trySolve().toString()
								.equalsIgnoreCase("ERROR"))
							System.err.println("无解");
						;
					}
				}).start();
			}
		});
		solveButton.setBounds(0, 230, 110, 30);
		contentPane.add(solveButton);

		clearButton = new JButton("清空");
		clearButton.setBackground(Color.WHITE);
		clearButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				clearButton.setBackground(Color.LIGHT_GRAY);
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						clearButton.setBackground(Color.WHITE);
						new Board(new int[9][9]).show();
					}
				}).start();

			}
		});
		clearButton.setBounds(110, 230, 110, 30);
		contentPane.add(clearButton);
	}

	public Board getBoard() {
		int[][] nums = new int[9][9];
		for (int i = 0; i < 81; i++) {
			int y = i / 9;
			int x = i - y * 9;
			int num = 0;
			Object ele = displaytable.getModel().getValueAt(y, x);
			try {
				num = (int) ele;
			} catch (Exception e) {
			}
			try {
				num = Integer.valueOf(ele.toString());
			} catch (Exception e) {
			}
			nums[x][y] = num;
		}
		Board board = new Board(nums);
		return board;
	}
}

class SudokuModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	@Override
	public int getColumnCount() {
		return 9;
	}

	@Override
	public int getRowCount() {
		return 9;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (Integer.valueOf(getValueAt(rowIndex, columnIndex).toString()) == 0)
			return true;
		else
			return false;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		int value = 0;
		try {
			value = Integer.valueOf(aValue.toString());
		} catch (Exception e) {
		}
		if (value > 9 || value < 0)
			value = 0;
		super.setValueAt(value, rowIndex, columnIndex);
	}
}