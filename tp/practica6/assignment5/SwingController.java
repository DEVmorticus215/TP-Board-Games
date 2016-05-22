package es.ucm.fdi.tp.assignment5;

import java.util.List;
import java.util.Scanner;

import es.ucm.fdi.tp.basecode.bgame.control.ConsoleCtrl;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class SwingController extends ConsoleCtrl {

    public SwingController(Game game, List<Piece> pieces, List<Player> players, Scanner in) {
        super(game, pieces, players, in);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void makeMove(Player p) {
        game.makeMove(p);
    };

}
