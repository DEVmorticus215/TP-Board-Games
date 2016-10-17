package es.ucm.fdi.tp.assignment5.attt;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import es.ucm.fdi.tp.basecode.attt.AdvancedTTTFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AdvancedTTTFactoryExt extends AdvancedTTTFactory {

	private static final long serialVersionUID = 1L;

	public void createSwingView(final Observable<GameObserver> g, final Controller c, final Piece viewPiece,
			Player random, Player ai) {

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					new AdvancedTTTSwingView(g, c, viewPiece, random, ai);
				}

			});
		} catch (InvocationTargetException e) {
		} catch (InterruptedException e) {
		}

		/*
		 * To have the game options GUI of Main.class working properly, we have
		 * to comment the invokeAndWait method above this comment lines and
		 * discomment the following line of code:
		 */
		// new AdvancedTTTSwingView(g, c, viewPiece, random, ai);
	}

}
