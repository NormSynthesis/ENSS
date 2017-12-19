/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.deprecated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.game.NormCombination;
import uk.ac.ox.cs.ensm.game.NormativeGame;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NormativeProfilesFrequencies 
extends HashMap<NormativeGame, Map<NormCombination, BigDecimal>> {

	/** Serial ID */
	private static final long serialVersionUID = -5292997470090737133L;

	/**
	 * 
	 * @param game
	 * @param np
	 * @param freq
	 */
	public void addFrequency(NormativeGame game, NormCombination np,
			BigDecimal freq) {
		
		if(!this.containsKey(game)) {
			this.put(game, new HashMap<NormCombination, BigDecimal>());
		}
		this.get(game).put(np, freq);
	}
	
	/**
	 * 
	 * @param game
	 * @param np
	 * @return
	 */
	public BigDecimal getFrequency(NormativeGame game, NormCombination np) {
		return this.get(game).get(np);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<NormativeGame> getGames() {
		return new ArrayList<NormativeGame>(this.keySet());
	}
	
	/**
	 * 
	 * @param game
	 * @return
	 */
	public List<NormCombination> getNormativeProfiles(NormativeGame game) {
		return new ArrayList<NormCombination>(this.get(game).keySet());
	}
}
