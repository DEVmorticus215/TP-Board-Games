package es.ucm.fdi.tp.assignment5.ataxx;

import java.util.List;

import es.ucm.fdi.tp.assignment4.ataxx.AtaxxMove;
import es.ucm.fdi.tp.assignment5.RectBoardSwingView;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxSwingView extends RectBoardSwingView {

	private static final long serialVersionUID = 1L;
	private int firstRow, firstCol;
	private boolean isSecondSelection;
	private Controller ctrl;

	public AtaxxSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
		// TODO Auto-generated constructor stub
		isSecondSelection = false;
		this.ctrl = c;
		String title = "Ataxx";
		if (localPiece != null)
			title += ": " + localPiece;
		setTitle(title);
	}

	@Override
	public void selectedCoordinates(final int row, final int col) {
		// TODO Auto-generated method stub
		if (isSecondSelection) {
			isSecondSelection = false;
			try {
				ctrl.makeMove(new Player() {

					private static final long serialVersionUID = 1L;

					@Override
					public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
						return new AtaxxMove(firstRow, firstCol, row, col, getTurn());
					}
				});
			} catch (GameError e) {
			} catch (Exception e) {
			}
		} else {
			this.firstRow = row;
			this.firstCol = col;
			if (getTurn().equals(getBoard().getPosition(firstRow, firstCol))) {
				isSecondSelection = true;
				addMsg("Clic on the destination...");
			}
		}
	}

}
