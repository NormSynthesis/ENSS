/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.network.edges;

import uk.ac.ox.cs.ensm.game.Game;
import uk.ac.ox.cs.ensm.game.NormativeGame;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class GNGRelationship implements NetworkEdge {

	private Game game;
	private NormativeGame nGame;
	
	/**
	 * 
	 */
	public GNGRelationship(Game game, NormativeGame nGame) {
		this.game = game;
		this.nGame = nGame;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.network.edges.NetworkEdge#getType()
	 */
	@Override
	public NetworkEdgeType getType() {
		return NetworkEdgeType.Inclusion;
	}
	
	/**
	 * 
	 * @return
	 */
	public Game getGame() {
		return this.game;
	}
	
	/**
	 * 
	 * @return
	 */
	public NormativeGame getNormativeGame() {
		return this.nGame;
	}
}
