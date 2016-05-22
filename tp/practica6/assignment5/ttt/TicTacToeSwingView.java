package es.ucm.fdi.tp.assignment5.ttt;

import es.ucm.fdi.tp.assignment5.connectn.ConnectNSwingView;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class TicTacToeSwingView extends ConnectNSwingView {

	private static final long serialVersionUID = 1L;

	public TicTacToeSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
            Player aiPlayer) {
        super(g, c, localPiece, randPlayer, aiPlayer);
        // TODO Auto-generated constructor stub
        String title = "TicTacToe";
        if (localPiece != null) title += ": " + localPiece;
        setTitle(title);
    }

}
