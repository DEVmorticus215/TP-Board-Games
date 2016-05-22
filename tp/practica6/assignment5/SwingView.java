package es.ucm.fdi.tp.assignment5;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import es.ucm.fdi.tp.assignment5.Main.PlayerMode;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.extra.jcolor.ColorChooser;

public abstract class SwingView extends JFrame implements GameObserver {

	private static final long serialVersionUID = 1L;

	private Controller ctrl;
	private Piece localPiece;
	private Piece turn;
	private Board board;
	private List<Piece> pieces;
	private Map<Piece, Color> pieceColors;
	private Map<Piece, PlayerMode> playerTypes;

	private Player randPlayer;
	private Player aiPlayer;

	private JPanel boardPanel;
	private JTextArea textArea;
	private JComboBox<Piece> playerComboBox;
	private JComboBox<Piece> playerComboBox2;
	private MyTableModel tModel;

	public SwingView(Observable<GameObserver> g, Controller c, Piece localPiece, Player randPlayer, Player aiPlayer) {
		ctrl = c;
		this.localPiece = localPiece;
		this.randPlayer = randPlayer;
		this.aiPlayer = aiPlayer;
		pieceColors = new HashMap<Piece, Color>();
		playerTypes = new HashMap<Piece, PlayerMode>();

		initGUI();
		g.addObserver(this);
	}

	private void initGUI() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(900, 620);
		JPanel window = new JPanel(new BorderLayout());

		boardPanel = new JPanel(new BorderLayout(0, 0));
		boardPanel.setPreferredSize(new Dimension(600, 600));
		window.add(boardPanel, BorderLayout.CENTER);

		JPanel lateralPanel = new JPanel();
		lateralPanel.setPreferredSize(new Dimension(300, 600));
		window.add(lateralPanel, BorderLayout.LINE_END);

		JPanel textAreaPanel = new JPanel(new BorderLayout(0, 0));
		textAreaPanel.setPreferredSize(new Dimension(300, 100));
		textAreaPanel.setBorder(BorderFactory.createTitledBorder("Status messages"));
		textArea = new JTextArea();
		textArea.setEditable(false);
		textAreaPanel.add(new JScrollPane(textArea));
		lateralPanel.add(textAreaPanel, BorderLayout.CENTER);

		JPanel playerTablePanel = new JPanel(new BorderLayout(0, 0));
		playerTablePanel.setPreferredSize(new Dimension(300, 100));
		playerTablePanel.setBorder(BorderFactory.createTitledBorder("Players info"));
		tModel = new MyTableModel();
		tModel.getRowCount();

		JTable table = new JTable(tModel) {
			private static final long serialVersionUID = 1L;

			// This is how we change the color of each row
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);

				Piece p = (Piece) tModel.getValueAt(row, 0);
				comp.setBackground(getPieceColor(p));

				return comp;
			}
		};

		playerTablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
		lateralPanel.add(playerTablePanel, BorderLayout.CENTER);

		JPanel chooseColorPanel = new JPanel();
		chooseColorPanel.setPreferredSize(new Dimension(300, 100));
		chooseColorPanel.setBorder(BorderFactory.createTitledBorder("Change player color"));
		playerComboBox = new JComboBox<Piece>();
		chooseColorPanel.add(playerComboBox);
		JButton changeColorButton = new JButton("Change color");

		changeColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Piece piece = playerComboBox.getItemAt(playerComboBox.getSelectedIndex());
				ColorChooser colorChooser = new ColorChooser(null, "Choose a color", getPieceColor(piece));
				if (piece.equals(localPiece) || localPiece == null) {
					if (colorChooser.getColor() != null) {
						setPieceColor(piece, colorChooser.getColor());
						tModel.refresh();
						redrawBoard();
					}
				} else {
					addMsg("You cannot change other players piece's color.");
				}
			}
		});

		chooseColorPanel.add(changeColorButton);
		lateralPanel.add(chooseColorPanel, BorderLayout.CENTER);

		JPanel chooseModePanel = new JPanel();
		chooseModePanel.setPreferredSize(new Dimension(300, 100));
		chooseModePanel.setBorder(BorderFactory.createTitledBorder("Change player mode"));
		playerComboBox2 = new JComboBox<Piece>();
		chooseModePanel.add(playerComboBox2);
		JComboBox<String> playerModesComboBox = new JComboBox<String>();
		for (PlayerMode playerMode : PlayerMode.values())
			playerModesComboBox.addItem(playerMode.getDesc());
		chooseModePanel.add(playerModesComboBox);
		JButton playerModeSetButton = new JButton("Set");

		playerModeSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Piece piece = playerComboBox2.getItemAt(playerComboBox2.getSelectedIndex());
				String playerMode = playerModesComboBox.getItemAt(playerModesComboBox.getSelectedIndex());
				if (piece.equals(localPiece) || localPiece == null) {
					if (playerMode.equals(PlayerMode.MANUAL.getDesc())) {
						playerTypes.put(piece, PlayerMode.MANUAL);
						tModel.setMode(piece, PlayerMode.MANUAL);
					} else if (playerMode.equals(PlayerMode.AI.getDesc())) {
						playerTypes.put(piece, PlayerMode.AI);
						tModel.setMode(piece, PlayerMode.AI);
						ctrl.makeMove(aiPlayer);
					} else if (playerMode.equals(PlayerMode.RANDOM.getDesc())) {
						playerTypes.put(piece, PlayerMode.RANDOM);
						tModel.setMode(piece, PlayerMode.RANDOM);
						decideMakeAutomaticMove();
					}
				} else {
					addMsg("You cannot change other players piece's mode.");
				}
			}
		});

		chooseModePanel.add(playerModeSetButton, BorderLayout.CENTER);
		lateralPanel.add(chooseModePanel, BorderLayout.CENTER);

		JPanel automaticMovesPanel = new JPanel();
		automaticMovesPanel.setPreferredSize(new Dimension(300, 100));
		automaticMovesPanel.setBorder(BorderFactory.createTitledBorder("Automatic moves"));
		if (randPlayer != null) {
			JButton randomButton = new JButton("RANDOM");

			randomButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					Piece piece = getTurn();
					if (piece.equals(localPiece) || localPiece == null) {
						decideMakeAutomaticMove();
					} else {
						addMsg("You cannot make an automatic move if it is not your turn.");
					}
				}
			});

			automaticMovesPanel.add(randomButton);
		}

		if (aiPlayer != null) {
			JButton intelligentButton = new JButton("INTELLIGENT");

			intelligentButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					Piece piece = playerComboBox.getItemAt(playerComboBox.getSelectedIndex());
					if (piece.equals(localPiece) || localPiece == null) {
						try {
							ctrl.makeMove(aiPlayer);
						} catch (GameError e1) {

						} catch (Exception e1) {

						}
					} else {
						addMsg("You cannot make an automatic move if it is not your turn.");
					}
				}
			});

			automaticMovesPanel.add(intelligentButton);
		}

		lateralPanel.add(automaticMovesPanel, BorderLayout.CENTER);

		JPanel quitAndRestartPanel = new JPanel();
		quitAndRestartPanel.setPreferredSize(new Dimension(300, 50));
		JButton quitButton = new JButton("QUIT");

		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				int n = JOptionPane.showConfirmDialog(null, "Are you sure?", "Quit", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);

				if (n == 0) {
					ctrl.stop();
					System.exit(0);
				}
			}
		});

		quitAndRestartPanel.add(quitButton);
		JButton restartButton = new JButton("RESTART");

		restartButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				playerComboBox.removeAllItems();
				playerComboBox2.removeAllItems();
				textArea.setText(null);
				ctrl.restart();
			}
		});

		quitAndRestartPanel.add(restartButton);
		lateralPanel.add(quitAndRestartPanel, BorderLayout.CENTER);

		initBoardGui();
		this.setContentPane(window);
		this.setVisible(true);
	}

	final protected Piece getTurn() {
		return turn;
	}

	final protected Board getBoard() {
		return board;
	}

	final protected List<Piece> getPieces() {
		return pieces;
	}

	final protected Color getPieceColor(Piece p) {
		return pieceColors.get(p);
	}

	final protected Color setPieceColor(Piece p, Color c) {
		return pieceColors.put(p, c);
	}

	final protected void setBoardArea(JComponent c) {
		boardPanel.add(c);
	}

	final protected void addMsg(String msg) {
		textArea.append(msg + "\n");
	}

	final protected void decideMakeManualMove(Player manualPlayer) {

	}

	private void decideMakeAutomaticMove() {
		try {
			ctrl.makeMove(randPlayer);
		} catch (GameError e) {

		} catch (Exception e) {

		}
	}

	protected abstract void initBoardGui();

	protected abstract void activateBoard();

	protected abstract void deActivateBoard();

	protected abstract void redrawBoard();

	// GameObserver methods
	@Override
	public void onGameStart(Board receivedBoard, String gameDesc, List<Piece> receivedPieces, Piece receivedTurn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addMsg(gameDesc);
				board = receivedBoard;
				pieces = receivedPieces;
				turn = receivedTurn;
				Iterator<Color> colorsGenerator = niceColorsGenerator();
				for (Piece piece : receivedPieces) {
					setPieceColor(piece, colorsGenerator.next());
					playerTypes.put(piece, PlayerMode.MANUAL);
					playerComboBox.addItem(piece);
					playerComboBox2.addItem(piece);
					tModel.addPiece(piece);
					tModel.setNumberPiece(piece, receivedBoard.getPieceCount(piece));
					tModel.setMode(piece, PlayerMode.MANUAL);
				}
				redrawBoard();

				if (localPiece != null) {
					if (!localPiece.equals(receivedTurn)) {
						deActivateBoard();
						addMsg("Turn for " + receivedTurn);
					} else {
						activateBoard();
						addMsg("Turn for you, " + receivedTurn);
					}
				} else {
					addMsg("Turn for " + receivedTurn);
				}
			}
		});
	}

	@Override
	public void onGameOver(Board receivedBoard, State state, Piece winner) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				turn = winner;
				board = receivedBoard;
				redrawBoard();
				if (state == State.Won) {
					addMsg(turn + " is the winner!");
				} else if (state == State.Draw) {
					addMsg("The game ended in a draw.");
				} else if (state == State.Stopped) {
					addMsg("The game was stopped.");
				}
			}
		});
	}

	@Override
	public void onMoveStart(Board receivedBoard, Piece receivedTurn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				turn = receivedTurn;
				board = receivedBoard;
			}
		});
	}

	@Override
	public void onMoveEnd(Board board, Piece turn, boolean success) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//
			}
		});
	}

	@Override
	public void onChangeTurn(Board receivedBoard, Piece receivedTurn) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				turn = receivedTurn;
				board = receivedBoard;

				if (playerTypes.get(receivedTurn).equals(PlayerMode.RANDOM)) {
					decideMakeAutomaticMove();
				} else if (playerTypes.get(receivedTurn).equals(PlayerMode.AI)) {
					ctrl.makeMove(aiPlayer);
				} else if (playerTypes.get(receivedTurn).equals(PlayerMode.MANUAL)) {
					//
				}
				for (Piece piece : getPieces()) {
					tModel.setNumberPiece(piece, receivedBoard.getPieceCount(piece));
				}

				if (localPiece != null) {
					if (!localPiece.equals(receivedTurn)) {
						deActivateBoard();
						addMsg("Turn for " + receivedTurn);
					} else {
						activateBoard();
						addMsg("Turn for you, " + receivedTurn);
					}
				} else {
					activateBoard();
					addMsg("Turn for " + receivedTurn);
				}
				redrawBoard();
			}
		});
	}

	@Override
	public void onError(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addMsg(msg);
			}
		});
	}

	class MyTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;

		private String[] colNames;
		List<Piece> pieces;
		List<PlayerMode> playerModes;
		List<Integer> numberPieces;

		MyTableModel() {
			this.colNames = new String[] { "Piece", "Mode", "# Pieces" };
			pieces = new ArrayList<Piece>();
			playerModes = new ArrayList<PlayerMode>();
			numberPieces = new ArrayList<Integer>();
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public String getColumnName(int col) {
			return colNames[col];
		}

		@Override
		public int getColumnCount() {
			return colNames.length;
		}

		@Override
		public int getRowCount() {
			return pieces != null ? pieces.size() : 0;
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return pieces.get(rowIndex);
			} else if (columnIndex == 1) {
				return playerModes.get(rowIndex).getDesc();
			} else {
				return board.getPieceCount(pieces.get(rowIndex));
			}
		}

		public void addPiece(Piece piece) {
			if (!pieces.contains(piece))
				pieces.add(piece);
			refresh();
		}

		public void setMode(Piece piece, PlayerMode mode) {
			if (playerModes.size() > pieces.indexOf(piece))
				playerModes.set(pieces.indexOf(piece), mode);
			else
				playerModes.add(mode);
			refresh();
		}

		public void setNumberPiece(Piece piece, Integer number) {
			if (playerModes.size() > pieces.indexOf(piece))
				numberPieces.set(pieces.indexOf(piece), number);
			else
				numberPieces.add(number);
			refresh();
		}

		public void refresh() {
			fireTableDataChanged();
		}
	};

	public static Iterator<Color> niceColorsGenerator() {

		Iterator<Color> i = new Iterator<Color>() {
			// since we use a fixed seed, we always get the same sequence of
			// colors
			//
			private Random r = new Random(208);

			@Override
			public Color next() {
				return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), r.nextInt(256));
			}

			@Override
			public boolean hasNext() {
				return true;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException("read-only iterator");
			}
		};

		return i;
	}
}
