package com.github.xzzpig.sudoku.location;

import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.sudoku.Accountable;
import com.github.xzzpig.sudoku.Board;
import com.github.xzzpig.sudoku.ElementHolder;
import com.github.xzzpig.sudoku.element.Element;
import com.github.xzzpig.sudoku.element.PossibleElement;
import com.github.xzzpig.sudoku.element.SureElement;

//行
public class Row implements ElementHolder, Accountable {
	private int py;
	private Board pBoard;

	public Row(Board board, int y) {
		py = y;
		pBoard = board;
	}

	@Override
	public int getNumber() {
		return py;
	}

	@Override
	public Element[] getElements() {
		Element[] es = new Element[9];
		for (int i = 0; i < 9; i++) {
			es[i] = pBoard.getElementss()[i][py];
		}
		return es;
	}

	@Override
	public boolean check() {
		List<Integer> nums = new ArrayList<Integer>();
		for (Element element : getElements()) {
			if (element instanceof SureElement) {
				if (nums.contains(((SureElement) element).getNumber())) {
					System.err.println("r:" + element.getLocation() + "重复");
					return false;
				}

				nums.add(((SureElement) element).getNumber());
			} else {
				if (((PossibleElement) element).getNumbers().size() == 0) {
					System.err.println("r:" + element.getLocation() + "无解");
					return false;
				}
			}
		}
		return true;
	}
}
