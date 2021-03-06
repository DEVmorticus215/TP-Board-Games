package es.ucm.fdi.tp.assignment6.swingplayers;

import java.util.List;

import es.ucm.fdi.tp.assignment4.ataxx.AtaxxMove;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxSwingPlayer extends Player{

    private static final long serialVersionUID = 1L;
    private int originRow;
    private int originCol;
    private int finalRow;
    private int finalCol;
    private Piece p;
    
    public AtaxxSwingPlayer(int originRow, int originCol, int finalRow, int finalCol, Piece p) {
        this.originRow = originRow;
        this.originCol = originCol;
        this.finalRow = finalRow;
        this.finalCol = finalCol;
        this.p = p;
    }

    @Override
    public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
        return new AtaxxMove(originRow, originCol, finalRow, finalCol, this.p);
    }

}