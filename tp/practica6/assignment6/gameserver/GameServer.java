package es.ucm.fdi.tp.assignment6.gameserver;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import es.ucm.fdi.tp.assignment6.responses.ChangeTurnResponse;
import es.ucm.fdi.tp.assignment6.responses.ErrorResponse;
import es.ucm.fdi.tp.assignment6.responses.GameOverResponse;
import es.ucm.fdi.tp.assignment6.responses.GameStartResponse;
import es.ucm.fdi.tp.assignment6.responses.MoveEndResponse;
import es.ucm.fdi.tp.assignment6.responses.MoveStartResponse;
import es.ucm.fdi.tp.assignment6.responses.Response;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;


public class GameServer extends Controller implements GameObserver {

	JTextArea textArea;

	private int port;
	private int numPlayers;
	private int numOfConnectedPlayers;
	private GameFactory gameFactory;
	private List<Connection> clients;
	// private Board board;
	private boolean firstGame = true;

	volatile private ServerSocket server;
	volatile private boolean stopped;
	volatile private boolean gameOver;

	public GameServer(GameFactory gameFactory, List<Piece> pieces, int port) throws IOException {
		super(new Game(gameFactory.gameRules()), pieces);

		this.port = port;
		this.numPlayers = pieces.size();
		this.numOfConnectedPlayers = 0;
		this.clients = new ArrayList<Connection>();
		this.gameFactory = gameFactory;
		this.stopped = false;
		this.gameOver = false;

		game.addObserver(this);
	}

	@Override
	public synchronized void makeMove(Player player) {
		try {
			super.makeMove(player);
		} catch (GameError e) {
		}

	}

	@Override
	public synchronized void stop() {
		try {
			super.stop();
		} catch (GameError e) {
		}
	}

	@Override
	public synchronized void restart() {
		try {
			super.restart();
		} catch (GameError e) {
		}
	}

	@Override
	public void start() {
		controlGUI();
		try {
			startServer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void controlGUI() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					constructGUI();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			throw new GameError("Something went wrong when constructing the GUI");
		}
	}

	
	private void constructGUI() {
		JFrame window = new JFrame("Game Server");

		// Create text area for printing messages.
		JPanel infoArea = new JPanel();
		infoArea.setBorder(
				new TitledBorder(null, "Status messages", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		infoArea.setLayout(new BorderLayout());
	
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setRows(50);
		infoArea.add(new JScrollPane(textArea), BorderLayout.CENTER);

		JButton quitButton = new JButton("Stop server");
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gameOver = true;
				game.stop();
				log("The game has finished.");
				
				/*
				// Stops the server and exits the app
				for (Connection c : clients) {
					try {
						c.stop();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				stopped = true;
				stop();
				System.exit(0);
				*/
			}
		});
		window.setLayout(new BorderLayout());
		window.add(quitButton, BorderLayout.EAST);
		window.add(infoArea, BorderLayout.CENTER);
		window.setPreferredSize(new Dimension(640, 480));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setVisible(true);
	}

	private void log(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				textArea.append(msg + System.getProperty("line.separator"));
			}
		});
	}

	private void startServer() throws IOException {

		server = new ServerSocket(port);
		stopped = false;

		while (!stopped) {
			try {
				Socket s = server.accept();
				log("Connection was successful: " + s.getInetAddress());
				handleRequest(s);
			} catch (IOException e) {
				if (!stopped) {
					log("Error while waiting for a connection: " + e.getMessage());
				}
			} catch (InterruptedException e) {
				log("Connection was interrupted: " + e.getMessage());
			}
		}
	}

	private void handleRequest(Socket s) throws InterruptedException {
		try {
			Connection c = new Connection(s);

			Object clientRequest = c.getObject();
			if (!(clientRequest instanceof String) && !((String) clientRequest).equalsIgnoreCase("Connect")) {
				c.sendObject(new GameError("Invalid Request"));
				c.stop();
				return;
			}

			// 1. If the number of players already reached the maximum, send
			// back a corresponding error and return
			if (numOfConnectedPlayers == numPlayers) {
				throw new GameError("Error: No more players can connect to the game.");
			}

			// 2. Increase the number of connected clients, and add it to the
			// list of clients
			numOfConnectedPlayers++;
			clients.add(c);

			// 3. send back an “OK” String to the client, followed by the
			// GameFactory and a Piece to be used for initialising the client,
			// etc. The i-th client is assigned the i-th piece
			c.sendObject("OK");
			c.sendObject(gameFactory);
			int i = clients.indexOf(c);
			c.sendObject(pieces.get(i));

			// 4. If we have reached the expected number of players, start the
			// game. First time we call start(pieces), afterwards we use
			// restart()
			if (numOfConnectedPlayers == numPlayers) {
				log("The minimun number of players needed has been reached. The game is about to start...");
				if (firstGame == true) {
					super.start();
					firstGame = false;
				} else {
					restart();
				}
			}

			// 5. Call startClientListener to create a thread that listens to
			// the client messages, etc.
			startClientListener(c);

		} catch (IOException | ClassNotFoundException e) {
		}

	}

	private void startClientListener(Connection c) {
		gameOver = false;
		Thread t = new Thread() { // Start a thread that runs the loop below
									// while the game is not over and the server
									// has not been stopped

			@SuppressWarnings("deprecation")
			public void run() {
				while (!stopped && !gameOver) {
					try {
						Command cmd;
						// 1. Read a command, reading an Object from client and
						// casting it to Command
						Object o = c.getObject();
						cmd = (Command)o;
						
						// 2. Execute the command
						cmd.execute(GameServer.this);
						
					} catch (ClassNotFoundException | IOException e) {
						if (!stopped && !gameOver) {
							// Stops the game (not the server);
							stop();
						}
					}
				}
			}
		};
		t.start();
	}


	public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn) {
		try {
			forwardNotification(new GameStartResponse(board, gameDesc, pieces, turn));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void onGameOver(Board board, State state, Piece winner) {
		try {
			log("Game has ended. The server is now waiting to host another game.");
			forwardNotification(new GameOverResponse(board, state, winner));
			numOfConnectedPlayers = 0;
			this.clients = new ArrayList<Connection>();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stop();
	}

	public void onMoveStart(Board board, Piece turn) {
		try {
			forwardNotification(new MoveStartResponse(board, turn));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onMoveEnd(Board board, Piece turn, boolean success) {
		try {
			forwardNotification(new MoveEndResponse(board, turn, success));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onChangeTurn(Board board, Piece turn) {
		try {
			forwardNotification(new ChangeTurnResponse(board, turn));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void onError(String msg) {
		try {
			forwardNotification(new ErrorResponse(msg));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void forwardNotification(Response r) throws IOException {
		// Call c.sendObject(r) for each client connection ‘c’
		for (Connection c : clients) {
			c.sendObject(r);
		}
	}

}
