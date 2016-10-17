package es.ucm.fdi.tp.assignment5;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public abstract class RectBoardSwingView extends SwingView {

	private static final long serialVersionUID = 1L;
	private BoardComponent boardComponent;

	public RectBoardSwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer,
			Player aiPlayer) {
		super(g, c, localPiece, randPlayer, aiPlayer);
	}

	public class BoardComponent extends JComponent {

		private static final long serialVersionUID = 1L;
		private int _CELL_HEIGHT = 50;
		private int _CELL_WIDTH = 50;
		private int rows;
		private int cols;
		private Color[][] board;
		private boolean active;

		public BoardComponent(int rows, int cols) {
			initBoard(rows, cols);
			initGUI();
			active = true;
		}

		private void initBoard(int rows, int cols) {
			this.rows = rows;
			this.cols = cols;
			board = new Color[rows][cols];
		}

		public void setBoardColors(int rows, int cols, Color[][] colors) {
			this.rows = rows;
			this.cols = cols;
			board = colors;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		private void initGUI() {
			addMouseListener(new MouseListener() {

				@Override
				public void mouseReleased(MouseEvent e) {
					// System.out.println(" [DEBUG]Mouse Released");
				}

				@Override
				public void mousePressed(MouseEvent e) {
					// System.out.println(" [DEBUG]Mouse Pressed");
				}

				@Override
				public void mouseExited(MouseEvent e) {
					// System.out.println(" [DEBUG]Mouse Exited Component");
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					// System.out.println(" [DEBUG]Mouse Entered Component");
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					int col = e.getX() / _CELL_WIDTH;
					int row = e.getY() / _CELL_HEIGHT;
					// System.out.println(" [DEBUG]Button "+e.getButton()+ "
					// Clicked at "+"("+row+","+col+"): " +
					// getBoard().getPosition(row, col));
					if (active) {
						selectedCoordinates(row, col);
					}
				}
			});
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			_CELL_WIDTH = this.getWidth() / cols;
			_CELL_HEIGHT = this.getHeight() / rows;
			for (int i = 0; i < cols; i++)
				for (int j = 0; j < rows; j++)
					drawCell(j, i, g);
		}

		private void drawCell(int row, int col, Graphics g) {
			int x = col * _CELL_WIDTH;
			int y = row * _CELL_HEIGHT;
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(x + 2, y + 2, _CELL_WIDTH - 4, _CELL_HEIGHT - 4);
			g.setColor(board[row][col]);
			g.fillOval(x + 4, y + 4, _CELL_WIDTH - 8, _CELL_HEIGHT - 8);
			g.setColor(Color.black);
			g.drawOval(x + 4, y + 4, _CELL_WIDTH - 8, _CELL_HEIGHT - 8);
		}
	}

	@Override
	protected void initBoardGui() {
		boardComponent = new BoardComponent(7, 7);
		setBoardArea(boardComponent);
	}

	@Override
	protected void activateBoard() {
		boardComponent.setActive(true);
	}

	@Override
	protected void deActivateBoard() {
		boardComponent.setActive(false);
	}

	@Override
	protected void redrawBoard() {
		int rows = getBoard().getRows();
		int cols = getBoard().getCols();

		Color[][] boardColors = new Color[rows][cols];
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				Piece piece = getBoard().getPosition(j, i);
				if (piece != null) {
					boardColors[j][i] = getPieceColor(piece);
				}
			}
		}

		boardComponent.setBoardColors(rows, cols, boardColors);

		boardComponent.repaint();
	}

	public abstract void selectedCoordinates(int row, int col);

}
