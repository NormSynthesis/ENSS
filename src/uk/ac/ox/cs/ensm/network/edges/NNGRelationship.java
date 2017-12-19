/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.network.edges;

import uk.ac.ox.cs.ensm.game.NormativeGame;
import uk.ac.ox.cs.ensm.norm.Norm;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NNGRelationship implements NetworkEdge {

	private NormativeGame nGame;
	private Norm norm;
	private int player;
	
	/**
	 * 
	 */
	public NNGRelationship(NormativeGame nGame, Norm norm, int player) {
		this.nGame = nGame;
		this.norm = norm;
		this.player = player;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.network.edges.NetworkEdge#getType()
	 */
	@Override
	public NetworkEdgeType getType() {
		return NetworkEdgeType.Regulation;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getPlayer() {
		return this.player;
	}
	/**
	 * 
	 * @return
	 */
	public Norm getNorm() {
		return this.norm;
	}
	
	/**
	 * 
	 * @return
	 */
	public NormativeGame getNormativeGame() {
		return this.nGame;
	}
}
