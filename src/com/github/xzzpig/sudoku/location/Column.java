package com.github.xzzpig.sudoku.location;

import java.util.ArrayList;
import java.util.List;

import com.github.xzzpig.sudoku.Accountable;
import com.github.xzzpig.sudoku.Board;
import com.github.xzzpig.sudoku.ElementHolder;
import com.github.xzzpig.sudoku.element.Element;
import com.github.xzzpig.sudoku.element.PossibleElement;
import com.github.xzzpig.sudoku.element.SureElement;

//列
public class Column implements ElementHolder, Accountable {
	private int px;
	private Board pBoard;

	public Column(Board board, int x) {
		px = x;
		pBoard = board;
	}

	@Override
	public int getNumber() {
		return px;
	}

	@Override
	public Element[] getElements() {
		Element[] es = new Element[9];
		for (int i = 0; i < es.length; i++) {
			es[i] = pBoard.getElementss()[px][i];
		}
		return es;
	}

	@Override
	public boolean check() {
		List<Integer> nums = new ArrayList<Integer>();
		for (Element element : getElements()) {
			if (element instanceof SureElement) {
				if (nums.contains(((SureElement) element).getNumber())) {
					System.err.println("c:" + element.getLocation() + "重复");
					return false;
				}

				nums.add(((SureElement) element).getNumber());
			} else {
				if (((PossibleElement) element).getNumbers().size() == 0) {
					System.err.println("c:" + element.getLocation() + "无解");
					return false;
				}
			}
		}
		return true;
	}
}
