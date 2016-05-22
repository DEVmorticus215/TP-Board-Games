package es.ucm.fdi.tp.assignment5.ataxx;

import es.ucm.fdi.tp.assignment4.ataxx.AtaxxFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxFactoryExt extends AtaxxFactory{

	private static final long serialVersionUID = 1L;

	public AtaxxFactoryExt() {
        super();
    }
    
    public AtaxxFactoryExt(int dim, int obstacles) {
        super(dim, obstacles);
    }
    
    @Override
    public void createSwingView(Observable<GameObserver> game, Controller ctrl, Piece viewPiece, Player randPlayer,
            Player aiPlayer) {
        new AtaxxSwingView(game, ctrl, viewPiece, randPlayer, aiPlayer);
    }
    
}
