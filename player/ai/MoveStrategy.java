package com.engine.classic.player.ai;

import com.engine.classic.board.Board;
import com.engine.classic.board.Move;

public interface MoveStrategy {

    long getNumBoardsEvaluated();

    Move execute(Board board);

}
