package com.github.xzzpig.sudoku;

import com.github.xzzpig.sudoku.element.Element;
import com.github.xzzpig.sudoku.element.PossibleElement;
import com.github.xzzpig.sudoku.element.SureElement;
import com.github.xzzpig.sudoku.location.Location;

public class Board implements ElementHolder {
	private Element[][] pelements = new Element[9][9];

	public Board(int[][] nums) {
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				int num = nums[x][y];
				Element element;
				if (num == 0)
					element = new PossibleElement(PossibleElement.allNumbers,
							new Location(this, x, y));
				else
					element = new SureElement(num, new Location(this, x, y));
				pelements[x][y] = element;
			}
		}
	}

	@Override
	public Element[] getElements() {
		Element[] elements = new Element[81];
		int i = 0;
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				elements[i] = this.pelements[x][y];
				// System.out.print(elements[i]);
				i++;
			}
			// System.out.println();
		}
		return elements;
	}

	public Element[][] getElementss() {
		return pelements;
	}

	public Board show() {
		for (Element element : this.getElements()) {
			DisplayWindow.window.displaytable.getModel().setValueAt(
					((Accountable) element).getNumber(),
					element.getLocation().getRow().getNumber(),
					element.getLocation().getColumn().getNumber());
		}
		return this;
	}

	public Board print() {
		String str = "";
		for (int y = 0; y < 9; y++) {
			for (int x = 0; x < 9; x++) {
				str += this.pelements[x][y];
			}
			str += "\n";
		}
		System.out.println(str);
		return this;
	}

	public Board solve() {
		loop: while (true) {
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < 9; y++) {
					Element element = pelements[x][y];
					if (element instanceof SureElement)
						continue;
					PossibleElement element2 = (PossibleElement) element;
					element2.solveSure();
					if (element2.getNumbers().size() == 1) {
						pelements[x][y] = new SureElement(element2.getNumbers()
								.get(0), element.getLocation());
						x = 0;
						y = 0;
						continue;
					}
				}
			}
			for (int x = 0; x < 9; x++) {
				for (int y = 0; y < 9; y++) {
					Element element = pelements[x][y];
					if (element instanceof SureElement)
						continue;
					PossibleElement element2 = (PossibleElement) element;
					element2.solvePossible();
					if (element2.getNumbers().size() == 1) {
						pelements[x][y] = new SureElement(element2.getNumbers()
								.get(0), element.getLocation());
						x = 0;
						y = 0;
						continue loop;
					}
				}
			}
			break;
		}
		return this;
	}

	@Override
	public boolean check() {
		solve();
		for (Element element : getElements()) {
			if (!element.getLocation().check())
				return false;
		}
		return true;
	}

	public Result checkResult() {
		if (!check())
			return Result.ERROR;
		for (Element element : getElements())
			if (element instanceof PossibleElement)
				return Result.NEXT;
		return Result.PASS;
	}

	@Override
	public Board clone() {
		int[][] intss = new int[9][9];
		for (Element element : getElements()) {
			intss[element.getLocation().getColumn().getNumber()][element
					.getLocation().getRow().getNumber()] = ((Accountable) element)
					.getNumber();
		}
		return new Board(intss);
	}

	public Result trySolve() {
		return trySolve(this);
	}

	private static Result trySolve(Board inboard) {
		Board board = inboard.clone().solve().show();
		for (Element element : board.getElements()) {
			if (element instanceof PossibleElement) {
				PossibleElement element2 = (PossibleElement) element;
				for (int i = 0; i < element2.getNumbers().size(); i++) {
					board.pelements[element.getLocation().getColumn()
							.getNumber()][element.getLocation().getRow()
							.getNumber()] = new SureElement(element2
							.getNumbers().get(i), element.getLocation());
					Result result = board.checkResult();
					if (result == Result.ERROR)
						return Result.ERROR;
					if (result == Result.PASS) {
						board.show();
						return Result.PASS;
					}
					Result result2 = trySolve(board);
					if (result2 == Result.PASS)
						return Result.PASS;
					if (result2 == Result.ERROR)
						continue;
				}
				return Result.ERROR;
			}
		}
		return Result.ERROR;
	}
}

enum Result {
	PASS, ERROR, NEXT
}
