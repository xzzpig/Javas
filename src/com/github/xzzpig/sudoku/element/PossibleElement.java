package com.github.xzzpig.sudoku.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.xzzpig.sudoku.Accountable;
import com.github.xzzpig.sudoku.location.Location;

public class PossibleElement implements Element, Accountable {
	public static final int[] allNumbers = new int[] { 1, 2, 3, 4, 5, 6, 7, 8,
			9 };
	List<Integer> nums = new ArrayList<Integer>();
	private Location location;

	public PossibleElement(int[] numbers, Location loc) {
		location = loc;
		for (int i = 0; i < numbers.length; i++) {
			nums.add(numbers[i]);
		}
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public String getType() {
		return "PossibleElement";
	}

	public List<Integer> getNumbers() {
		return nums;
	}

	@Override
	public int getNumber() {
		return 0;
	}

	@Override
	public String toString() {
		return getNumber() + "";
	}

	public PossibleElement solveSure() {
		for (Element e : this.getLocation().getBlock().getElements()) {
			if (e instanceof SureElement)
				this.getNumbers()
						.remove((Object) ((SureElement) e).getNumber());
		}
		for (Element e : this.getLocation().getRow().getElements()) {
			if (e instanceof SureElement)
				this.getNumbers()
						.remove((Object) ((SureElement) e).getNumber());
		}
		for (Element e : this.getLocation().getColumn().getElements()) {
			if (e instanceof SureElement)
				this.getNumbers()
						.remove((Object) ((SureElement) e).getNumber());
		}
		return this;
	}

	public PossibleElement solvePossible() {
		List<Integer>

		cnums = new ArrayList<Integer>();
		cnums.addAll(nums);
		for (Element e : this.getLocation().getBlock().getElements()) {
			if (e instanceof PossibleElement) {
				if (e == this)
					continue;
				cnums.removeAll(((PossibleElement) e).getNumbers());
			}
		}
		if (cnums.size() == 1) {
			nums = cnums;
			return this;
		}

		cnums = new ArrayList<Integer>();
		cnums.addAll(nums);
		for (Element e : this.getLocation().getRow().getElements()) {
			if (e instanceof PossibleElement) {
				if (e == this)
					continue;
				cnums.removeAll(((PossibleElement) e).getNumbers());
			}
		}
		if (cnums.size() == 1) {
			nums = cnums;
			return this;
		}

		cnums = new ArrayList<Integer>();
		cnums.addAll(nums);
		for (Element e : this.getLocation().getColumn().getElements()) {
			if (e instanceof PossibleElement) {
				if (e == this)
					continue;
				cnums.removeAll(((PossibleElement) e).getNumbers());
			}
		}
		if (cnums.size() == 1) {
			nums = cnums;
			return this;
		}
		return this;
	}

	@Override
	public String getString() {
		return Arrays.toString(nums.toArray());
	}
}