package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import es.ucm.fdi.tp.basecode.bgame.control.ConsolePlayer;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.DummyAIPlayer;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.AIAlgorithm;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.bgame.views.GenericConsoleView;

public class AtaxxFactory implements GameFactory {

	private static final long serialVersionUID = 1L;

	// The dimension of the board (same rows than columns)
	private int dim;

	// The number of obstacles located in each quadrant
	private int obstacles;

	/**
	 * AtaxxFactory constructor (without arguments)
	 * <p>
	 * It initialises a factory for the Ataxx game with 7 rows, 7 columns and no
	 * obstacles.
	 */
	public AtaxxFactory() {
		this.dim = 7;
		this.obstacles = 0;
	}

	/**
	 * AtaxxFactory constructor (with arguments)
	 * <p>
	 * It initialises a factory for the Ataxx game with dim rows, dim columns
	 * and obstacles number of obstacles in each quadrant randomly located.
	 * 
	 * @param dim
	 *            The dimension of the board (same rows than columns).
	 * @param obstacles
	 *            The number of obstacles located in each quadrant.
	 */
	public AtaxxFactory(int dim, int obstacles) {
		if (dim < 5) {
			throw new GameError("Dimension must be at least 5: " + dim);
		} else if (dim % 2 != 1) {
			throw new GameError("Dimension must be an odd number: " + dim);
		} else {
			this.dim = dim;
			this.obstacles = obstacles;
		}
	}

	@Override
	public GameRules gameRules() {
		return new AtaxxRules(dim, obstacles);
	}

	@Override
	public Player createConsolePlayer() {
		ArrayList<GameMove> possibleMoves = new ArrayList<GameMove>();
		possibleMoves.add(new AtaxxMove());
		return new ConsolePlayer(new Scanner(System.in), possibleMoves);
	}

	@Override
	public Player createRandomPlayer() {
		return new AtaxxRandomPlayer();
	}

	@Override
	public Player createAIPlayer(AIAlgorithm alg) {
		return new DummyAIPlayer(createRandomPlayer(), 1000);
	}

	/**
	 * By default, we have two players, X and O.
	 * <p>
	 * Por defecto, hay dos jugadores, X y O.
	 */
	@Override
	public List<Piece> createDefaultPieces() {
		List<Piece> pieces = new ArrayList<Piece>();
		pieces.add(new Piece("X"));
		pieces.add(new Piece("O"));
		return pieces;
	}

	@Override
	public void createConsoleView(Observable<GameObserver> game, Controller ctrl) {
		new GenericConsoleView(game, ctrl);
	}

	@Override
	public void createSwingView(Observable<GameObserver> game, Controller ctrl, Piece viewPiece, Player randPlayer,
			Player aiPlayer) {
		throw new UnsupportedOperationException("There is no swing view.");

	}

}
