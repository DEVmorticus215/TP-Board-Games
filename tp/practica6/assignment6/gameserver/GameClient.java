package es.ucm.fdi.tp.assignment6.gameserver;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.tp.assignment6.responses.Response;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.control.commands.Command;
import es.ucm.fdi.tp.basecode.bgame.control.commands.PlayCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.QuitCommand;
import es.ucm.fdi.tp.basecode.bgame.control.commands.RestartCommand;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class GameClient extends Controller implements Observable<GameObserver> {

	private String host;
	private int port;
	protected List<GameObserver> observers;
	private Piece localPiece;
	private GameFactory gameFactory;
	private Connection connectionToServer;
	private boolean gameOver;

	public GameClient(String host, int port) throws Exception {
		super(null, null);
		// Initialise the fields
		this.host = host;
		this.port = port;
		this.observers = new ArrayList<GameObserver>();
		this.gameOver = true;
		connect();
	}

	public GameFactory getGameFactory() {
		return gameFactory;
	}

	public Piece getPlayerPiece() {
		return localPiece;
	}

	public Connection getConnection() {
		return connectionToServer;
	}

	public void addObserver(GameObserver o) {
		observers.add(o);
	}

	public void removeObserver(GameObserver o) {
		int i = observers.indexOf(o);
		observers.remove(i);
	}

	@Override
	public void makeMove(Player p) {
		forwardCommand(new PlayCommand(p));
	}

	@Override
	public void stop() {
		forwardCommand(new QuitCommand());
	}

	@Override
	public void restart() {
		forwardCommand(new RestartCommand());
	}

	private void forwardCommand(Command cmd) {
		// If the game is over do nothing, otherwise
		// send the object cmd to the server
		if (!gameOver) {
			try {
				connectionToServer.sendObject(cmd);
			} catch (IOException e) {

			}
		}
	}

	private void connect() throws Exception {

		// Sends the String “Connect” to express its desire to play
		connectionToServer = new Connection(new Socket(host, port));
		connectionToServer.sendObject("Connect");

		// Read the first object in the server’s response, and if it is an
		// exception just throw it.
		Object response = connectionToServer.getObject();
		if (response instanceof Exception) {
			throw (Exception) response;
		}

		try {
			// Otherwise, the server should send back a GameFactory followed by
			// a Piece to be used by this client
			gameFactory = (GameFactory) connectionToServer.getObject();
			localPiece = (Piece) connectionToServer.getObject();

		} catch (Exception e) {
			throw new GameError("Unknown server response: " + e.getMessage());
		}

	}

	public void start() {

		// Create an Anonymous instance of GameObserver, and add it to the list
		// of observers, that all it does is to set gameOver to true and closes
		// the connection to the server when it receives onGameOver
		// notification. This way, we can exit the loop below when the game is
		// over.
		this.observers.add(new GameOver());
		gameOver = false;
		while (!gameOver) {
			try {
				Object obj = connectionToServer.getObject();
				Response r = (Response) obj;

				// While the game is not over, read a response from the server,
				// and pass it to all observers, i.e., execute run of the
				// response passing it the observer as a parameter …
				for (GameObserver o : observers) {
					// Execute the response on the observer o
					r.run(o);
				}

			} catch (ClassNotFoundException | IOException e) {

			}
		}
	}

	private class GameOver implements GameObserver {

		@Override
		public void onGameStart(Board board, String gameDesc, List<Piece> pieces, Piece turn) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onGameOver(Board board, State state, Piece winner) {
			gameOver = true;
			try {
				stop();
				connectionToServer.stop();
				forwardCommand(new QuitCommand());
			} catch (IOException e) {
				
			}

		}

		@Override
		public void onMoveStart(Board board, Piece turn) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMoveEnd(Board board, Piece turn, boolean success) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onChangeTurn(Board board, Piece turn) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(String msg) {
			// TODO Auto-generated method stub

		}
	}
}
