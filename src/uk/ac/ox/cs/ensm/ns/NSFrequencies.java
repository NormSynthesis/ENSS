/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.ns;

import java.math.BigDecimal;
import java.util.HashMap;

import uk.ac.ox.cs.ensm.game.Game;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NSFrequencies
extends HashMap<Game, HashMap<NormativeSystem, BigDecimal>> {

	/* Serial ID */
	private static final long serialVersionUID = 48465237499685970L;

	/**
	 * 
	 * @param game
	 * @param ns
	 * @param frequency
	 */
	public void put(Game game, NormativeSystem ns, BigDecimal frequency) {
		
		if(!this.containsKey(game)) {
			this.put(game, new HashMap<NormativeSystem,BigDecimal>());
		}
		this.get(game).put(ns, frequency);
	}
	
	/**
	 * 
	 * @param game
	 * @param ns
	 * @return
	 */
	public BigDecimal getFrequency(Game game, NormativeSystem ns) {
		return this.get(game).get(ns);
	}
}
