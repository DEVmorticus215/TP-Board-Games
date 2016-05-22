package es.ucm.fdi.tp.assignment5.connectn;

import es.ucm.fdi.tp.assignment5.RectBoardSwingView;
import es.ucm.fdi.tp.assignment6.swingplayers.ConnectNSwingPlayer;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class ConnectNSwingView extends RectBoardSwingView {

	private static final long serialVersionUID = 1L;
	private Controller ctrl;

	public ConnectNSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
		ctrl = c;
		String title = "Connect N";
		if (localPiece != null)
			title += ": " + localPiece;
		setTitle(title);
	}

	@Override
	public void selectedCoordinates(int row, int col) {
		// TODO Auto-generated method stub
		try {
			ctrl.makeMove(new ConnectNSwingPlayer(row, col, getTurn()));
			/*
			ctrl.makeMove(new Player() {

				private static final long serialVersionUID = 1L;

				@Override
				public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
					return new ConnectNMove(row, col, getTurn());
				}

			});
			*/
		} catch (GameError e) {
		} catch (Exception e) {
		}

	}
}
