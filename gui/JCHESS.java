package com.gui;

import com.engine.classic.board.Board;

public class JCHESS {
	public static void main(String[] args) {
		Board board = Board.createStandardBoard();
		System.out.println(board);
		Table.get().show();
	}

}
