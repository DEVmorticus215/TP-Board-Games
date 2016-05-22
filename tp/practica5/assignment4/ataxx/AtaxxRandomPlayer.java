package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxRandomPlayer extends Player {

	private static final long serialVersionUID = 1L;

	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
		// Creates a list of GameMoves with the available moves for the current
		// player.
		List<GameMove> availableMoves = rules.validMoves(board, pieces, p);

		if (availableMoves.isEmpty()) {
			throw new GameError("There are not available moves!");
		} else {
			// Selects randomly one movement from the list:
			int n = Utils.randomInt(availableMoves.size());

			return availableMoves.get(n);
		}
	}
}
