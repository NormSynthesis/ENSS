package uk.ac.ox.cs.ensm.metrics;

import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.game.Game;

/**
 * Metrics of the norm synthesis machine
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public interface EvolutionaryNSMetrics {
	
	/**
	 * Returns <tt>true</tt> if the NSM has converged to a normative system
	 * 
	 * @return <tt>true</tt> if the NSM has converged to a normative system
	 */
	public boolean hasConverged();
	
	/**
	 * Returns the norm synthesis settings
	 * 
	 * @return the norm synthesis settings
	 * @see EvolutionaryNSMSettings
	 */
	public EvolutionaryNSMSettings getNormSynthesisSettings();
		
	/**
	 * Updates the metrics
	 */
	public void update(double timeStep);
	
	/**
	 * 
	 * @param compTime
	 */
	public void addNewComputationTime(double compTime);
		
	/**
	 * 
	 */
	public void addGameReward(Game game, double reward);
}
