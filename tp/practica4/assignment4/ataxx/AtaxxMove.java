package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.List;
import java.lang.Math;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxMove extends GameMove {

	private static final long serialVersionUID = 1L;

	/**
	 * The row from where we took the piece return by
	 * {@link GameMove#getPiece()}.
	 * <p>
	 * Fila de la que cogemos la ficha devuelta por {@link GameMove#getPiece()}.
	 */
	protected int originRow;

	/**
	 * The column from where we took the piece return by
	 * {@link GameMove#getPiece()} .
	 * <p>
	 * Columna de la que cogemos la ficha devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int originCol;

	/**
	 * The row where to place the piece return by {@link GameMove#getPiece()}.
	 * <p>
	 * Fila en la que se coloca la ficha devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int finalRow;

	/**
	 * The column where to place the piece return by {@link GameMove#getPiece()}
	 * <p>
	 * Columna en la que se coloca la ficha devuelta por
	 * {@link GameMove#getPiece()}.
	 */
	protected int finalCol;

	/**
	 * AtaxxMove constructor (without arguments)
	 */
	public AtaxxMove() {
	}

	/**
	 * AtaxxMove constructor (with arguments)
	 * 
	 * @param originRow
	 *            The row from where we took the piece.
	 * @param originCol
	 *            The column from where we took the piece.
	 * @param finalRow
	 *            The row where to place the piece.
	 * @param finalCol
	 *            The column where to place the piece
	 * @param p
	 *            The piece we want to move.
	 */
	public AtaxxMove(int originRow, int originCol, int finalRow, int finalCol, Piece p) {
		super(p);
		this.originRow = originRow;
		this.originCol = originCol;
		this.finalRow = finalRow;
		this.finalCol = finalCol;
	}

	@Override
	public void execute(Board board, List<Piece> pieces) {
		// This integer tell us if we will (1) create a new piece or (2) move an
		// existing piece.
		int d = Math.max(Math.abs(originRow - finalRow), Math.abs(originCol - finalCol));

		// Checks if the piece we want to move belongs to the current player:
		if (getPiece().equals(board.getPosition(originRow, originCol))) {
			// Checks if the final position where we want to move the piece does
			// exist:
			if (finalRow >= 0 && finalCol >= 0 && finalRow < board.getRows() && finalCol < board.getCols()) {
				// Checks if there is a piece in the initial position and if the
				// final position is free:
				if (board.getPosition(originRow, originCol) != null && board.getPosition(finalRow, finalCol) == null) {
					// We can only move up to two spaces:
					if (d == 2) {
						// Moves the piece if the distance is 2:
						board.setPosition(finalRow, finalCol, getPiece());
						// And deletes the piece from its original position:
						board.setPosition(originRow, originCol, null);
					} else if (d == 1) {
						// Moves the piece if the distance is 1:
						board.setPosition(finalRow, finalCol, getPiece());
						board.setPieceCount(getPiece(), board.getPieceCount(getPiece()) + 1);
					} else {
						throw new GameError("Piece cannot move further than 2 spaces!");
					}
				} else if (board.getPosition(finalRow, finalCol) != null) {
					throw new GameError("Position (" + finalRow + "," + finalCol + ") is already occupied!");
				} else {
					throw new GameError("Please, select a valid destination cell.");
				}
			}
		} else if (board.getPosition(originRow, originCol) == null) {
			throw new GameError("There is no piece at position (" + originRow + "," + originCol + ")!");
		} else {
			throw new GameError("Piece at position (" + originRow + "," + originCol + ") is not yours!");
		}

		// To check that a piece is an obstacle from outside we simply check
		// that it is not in the list of pieces. We flip the pieces around us
		// that belong to our opponents and are not obstacles:
		for (int i = finalRow - 1; i <= finalRow + 1; i++) {
			for (int j = finalCol - 1; j <= finalCol + 1; j++) {
				if (i >= 0 && j >= 0 && i < board.getRows() && j < board.getCols()) {
					if (board.getPosition(i, j) != null && !isObstacle(board, pieces, i, j)) {
						board.setPieceCount(board.getPosition(i, j), board.getPieceCount(board.getPosition(i, j)) - 1);
						board.setPosition(i, j, getPiece());
						board.setPieceCount(getPiece(), board.getPieceCount(getPiece()) + 1);
					}
				}
			}
		}
	}

	@Override
	public GameMove fromString(Piece p, String str) {
		String[] words = str.split(" ");

		if (words.length != 4) {
			return null;
		}

		try {
			int orRow, orCol, finRow, finCol;
			orRow = Integer.parseInt(words[0]);
			orCol = Integer.parseInt(words[1]);
			finRow = Integer.parseInt(words[2]);
			finCol = Integer.parseInt(words[3]);
			return createMove(orRow, orCol, finRow, finCol, p);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * Creates a new move.
	 * 
	 * @param originRow
	 *            The row from where we took the piece.
	 * @param originCol
	 *            The column from where we took the piece.
	 * @param finalRow
	 *            The row where to place the piece.
	 * @param finalCol
	 *            The column where to place the piece
	 * @param p
	 *            The piece we want to move.
	 * @return The new move created with the arguments provided.
	 */
	protected GameMove createMove(int originRow, int originCol, int finalRow, int finalCol, Piece p) {
		return new AtaxxMove(originRow, originCol, finalRow, finalCol, p);
	}

	@Override
	public String help() {
		return "Row and column for origin and for destination, separated by spaces (four numbers).";
	}

	@Override
	public String toString() {
		if (getPiece() == null) {
			return help();
		} else {
			return "Move the piece from (" + originRow + "," + originCol + ") '" + getPiece() + "' to (" + finalRow
					+ "," + finalCol + ")";
		}
	}

	public boolean isObstacle(Board board, List<Piece> pieces, int row, int col) {
		return !pieces.contains(board.getPosition(row, col));
	}

}
