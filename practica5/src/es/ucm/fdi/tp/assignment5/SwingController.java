package es.ucm.fdi.tp.assignment5;

import java.util.List;
import java.util.Scanner;

import es.ucm.fdi.tp.basecode.bgame.control.ConsoleCtrl;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class SwingController extends ConsoleCtrl {

    public SwingController(Game game, List<Piece> pieces, List<Player> players, Scanner in) {
        super(game, pieces, players, in);
        // TODO Auto-generated constructor stub
    }
    
    public void start() { 
    	if (game == null || pieces == null) {
			throw new GameError("There is no game or pieces to start");
		}

		// Start the game
		game.start(pieces);
    }
    
    @Override
    public void makeMove(Player p) {
        game.makeMove(p);
    };

}
