package com.github.xzzpig.sudoku.element;

import com.github.xzzpig.sudoku.Accountable;
import com.github.xzzpig.sudoku.location.Location;

public class SureElement implements Element, Accountable {
	private Location location;
	private int num;

	public SureElement(int number, Location loc) {
		num = number;
		location = loc;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getType() {
		return "SureElement";
	}

	@Override
	public int getNumber() {
		return num;
	}

	@Override
	public String toString() {
		return getNumber() + "";
	}

	@Override
	public String getString() {
		return toString();
	}
}
