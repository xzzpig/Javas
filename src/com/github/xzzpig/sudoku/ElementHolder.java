package com.github.xzzpig.sudoku;

import com.github.xzzpig.sudoku.element.Element;

public interface ElementHolder {
	public Element[] getElements();

	public boolean check();
}
