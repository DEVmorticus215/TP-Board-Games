package es.ucm.fdi.tp.extra.jboard;

import es.ucm.fdi.tp.assignment5.SwingView;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

@SuppressWarnings("serial")
public class HexagonBoardSwingView extends SwingView {

	public HexagonBoardSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
	}

	@Override
	protected void initBoardGui() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void activateBoard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deActivateBoard() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void redrawBoard() {
		// TODO Auto-generated method stub
		
	}
	
}
