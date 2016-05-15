package com.github.xzzpig.sudoku.location;

import com.github.xzzpig.sudoku.Board;

public class Location {
	private Board pboard;
	private Row prow;
	private Column pcolumn;
	private Block pblock;

	public Location(Board board, int x, int y) {
		pboard = board;
		prow = new Row(board, y);
		pcolumn = new Column(board, x);
		pblock = new Block(board, Block.idOfxy(x, y));
	}

	public Board getBoard() {
		return pboard;
	}

	public Row getRow() {
		return prow;
	}

	public Column getColumn() {
		return pcolumn;
	}

	public Block getBlock() {
		return pblock;
	}

	public boolean check() {
		return getBlock().check() && getRow().check() && getColumn().check();
	}

	@Override
	public String toString() {
		return "(" + pcolumn.getNumber() + "," + prow.getNumber() + ")";
	}
}
