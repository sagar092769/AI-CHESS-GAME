package com.engine.classic.player.ai;

import com.engine.classic.board.Board;

public interface BoardEvaluator {

    int evaluate(Board board, int depth);

}
