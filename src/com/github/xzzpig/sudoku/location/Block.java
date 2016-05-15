package com.github.xzzpig.sudoku.location;

import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.sudoku.Accountable;
import com.github.xzzpig.sudoku.Board;
import com.github.xzzpig.sudoku.ElementHolder;
import com.github.xzzpig.sudoku.element.Element;
import com.github.xzzpig.sudoku.element.PossibleElement;
import com.github.xzzpig.sudoku.element.SureElement;

public class Block implements ElementHolder, Accountable {
	private int pid;
	private Board pBoard;

	public Block(Board board, int id) {
		pid = id;
		pBoard = board;
	}

	@Override
	public Element[] getElements() {
		Element[] eles = new Element[9];
		int x = 0, y = 0;
		switch (pid) {
		case 0:
			x = 0;
			y = 0;
			break;
		case 1:
			x = 3;
			y = 0;
			break;
		case 2:
			x = 6;
			y = 0;
			break;
		case 3:
			x = 0;
			y = 3;
			break;
		case 4:
			x = 3;
			y = 3;
			break;
		case 5:
			x = 6;
			y = 3;
			break;
		case 6:
			x = 0;
			y = 6;
			break;
		case 7:
			x = 3;
			y = 6;
			break;
		case 8:
			x = 6;
			y = 6;
			break;
		default:
			break;
		}
		int i = 0;
		for (int i1 = 0; i1 < 3; i1++) {
			for (int i2 = 0; i2 < 3; i2++) {
				eles[i] = pBoard.getElementss()[x + i1][y + i2];
				i++;
			}
		}
		return eles;
	}

	@Override
	public int getNumber() {
		return pid;
	}

	public static int idOfxy(int x, int y) {
		return x / 3 + y / 3 * 3;
	}

	@Override
	public boolean check() {
		List<Integer> nums = new ArrayList<Integer>();
		for (Element element : getElements()) {
			if (element instanceof SureElement) {
				if (nums.contains(((SureElement) element).getNumber())) {
					System.err.println("b:" + element.getLocation() + "重复");
					return false;
				}

				nums.add(((SureElement) element).getNumber());
			} else {
				if (((PossibleElement) element).getNumbers().size() == 0) {
					System.err.println("b:" + element.getLocation() + "无解"
							+ element.getString());
					return false;
				}

			}
		}
		return true;
	}
}
