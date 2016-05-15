package com.github.xzzpig.sudoku.element;

import com.github.xzzpig.sudoku.location.Location;

public interface Element {
	public Location getLocation();

	public String getType();

	@Override
	public String toString();

	public String getString();
}
