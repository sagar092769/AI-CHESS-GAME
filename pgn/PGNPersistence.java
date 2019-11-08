package com.pgn;

import com.engine.classic.board.Board;
import com.engine.classic.board.Move;
import com.engine.classic.player.Player;

public interface PGNPersistence {

    void persistGame(Game game);

    Move getNextBestMove(Board board, Player player, String gameText);

}
