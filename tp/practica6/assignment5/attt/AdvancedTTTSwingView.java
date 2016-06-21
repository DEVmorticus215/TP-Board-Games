package es.ucm.fdi.tp.assignment5.attt;

import java.util.List;

import es.ucm.fdi.tp.assignment5.RectBoardSwingView;
import es.ucm.fdi.tp.basecode.attt.AdvancedTTTMove;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AdvancedTTTSwingView extends RectBoardSwingView {

	private static final long serialVersionUID = 1L;
	private int firstRow, firstCol;
	private boolean isSecondSelection;
	private Controller ctrl;

	public AdvancedTTTSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);

		isSecondSelection = false;
		ctrl = c;
		String title = "Advanced TicTacToe";
		if (localPiece != null)
			title += ": " + localPiece;
		setTitle(title);
	}

	public void selectedCoordinates(final int row, final int col) {
		if (getBoard().getPieceCount(getTurn()) > 0) {
			// Connect N move
			try {
				ctrl.makeMove(new Player() {

					private static final long serialVersionUID = 1L;

					@Override
					public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
						return new AdvancedTTTMove(-1, -1, row, col, getTurn());
					}

				});
			} catch (GameError e) {
			} catch (Exception e) {
			}
		} else {
			// Ataxx move
			if (isSecondSelection) {
				isSecondSelection = false;
				try {
					ctrl.makeMove(new Player() {

						private static final long serialVersionUID = 1L;

						@Override
						public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
							return new AdvancedTTTMove(firstRow, firstCol, row, col, getTurn());
						}
					});
				} catch (GameError e) {
				} catch (Exception e) {
				}
			} else {
				this.firstRow = row;
				this.firstCol = col;
				isSecondSelection = true;
				if (getBoard().getPosition(firstRow, firstCol).equals(getTurn())) {
					addMsg("Clic on the destination...");
				}
			}
		}
	}

}
