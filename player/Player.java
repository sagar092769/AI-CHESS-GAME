package com.engine.classic.player;

import com.engine.classic.board.Board;
import com.engine.classic.board.Move;
import com.engine.classic.board.Move.MoveStatus;
import com.engine.classic.board.MoveTransition;
import com.engine.classic.pieces.Alliance;
import com.engine.classic.pieces.King;
import com.engine.classic.pieces.Piece;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;

public abstract class Player {

    protected final Board board;
    protected final King playerKing;
    protected final Collection<Move> legalMoves;
    protected final boolean isInCheck;

    Player(final Board board,
           final Collection<Move> playerLegals,
           final Collection<Move> opponentLegals) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !calculateAttacksOnTile(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
        playerLegals.addAll(calculateKingCastles(playerLegals, opponentLegals));
        this.legalMoves = Collections.unmodifiableCollection(playerLegals);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    public boolean isInCheckMate() {
       return this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return this.playerKing.isCastled();
    }

    public boolean isKingSideCastleCapable() {
        return this.playerKing.isKingSideCastleCapable();
    }

    public boolean isQueenSideCastleCapable() {
        return this.playerKing.isQueenSideCastleCapable();
    }

    public King getPlayerKing() {
        return this.playerKing;
    }

    private King establishKing() {
        return (King) getActivePieces().stream()
                                       .filter(piece -> piece.getPieceType().isKing())
                                       .findAny()
                                       .orElseThrow(RuntimeException::new);
    }

    private boolean hasEscapeMoves() {
        return this.legalMoves.stream()
                              .anyMatch(move -> makeMove(move)
                              .getMoveStatus().isDone());
    }

    public Collection<Move> getLegalMoves() {
        return this.legalMoves;
    }

    static Collection<Move> calculateAttacksOnTile(final int tile,
                                                   final Collection<Move> moves) {
        return moves.stream()
                    .filter(move -> move.getDestinationCoordinate() == tile)
                    .collect(collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public MoveTransition makeMove(final Move move) {
        if (!this.legalMoves.contains(move)) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionedBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile(
                transitionedBoard.currentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                transitionedBoard.currentPlayer().getLegalMoves());
        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(this.board, transitionedBoard, move, MoveStatus.DONE);
    }

    public MoveTransition unMakeMove(final Move move) {
        return new MoveTransition(this.board, move.undo(), move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateKingCastles(Collection<Move> playerLegals,
                                                             Collection<Move> opponentLegals);

}
