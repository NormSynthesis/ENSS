package uk.ac.ox.cs.ensm.config;

import java.util.List;

/**
 * Basic settings of the Norm Synthesis Machine. For instance,
 * the default utility of norms, or the number of ticks of stability
 * for the NSM to converge
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public interface EvolutionaryNSMSettings {
		
	/**
	 * The goals of the NSM 
	 * 
	 * @return a {@code List} containing the system goals
	 * @see Goal
	 */
	public List<Goal> getSystemGoals();
	
	/**
	 * Returns the punishment considered when conflicts arise immediately 
	 * after an agent complies with the norms of a normative system
	 * 
	 * @return the punishment considered when conflicts arise immediately 
	 * 				 after an agent complies with the norms of a normative system
	 */
	public double getPunishmentForHavingConflicts();
	
	/**
	 * Returns the reward considered when no conflicts arise immediately 
	 * after an agent complies with the norms of a normative system
	 * 
	 * @return  the reward considered when no conflicts arise immediately 
	 * 					after an agent complies with the norms of a normative system
	 */
	public double getRewardForAvoidingConflicts();
	
	/**
	 * Returns the size of the window to conflict ratios between groups of norms
	 * 
	 * @return the size of the window to conflict ratios between groups of norms
	 */
	public int getNSFitnessRangeSize();
	
	
	/**
	 * Returns the size of the window to conflict ratios between groups of norms
	 * 
	 * @return the size of the window to conflict ratios between groups of norms
	 */
	public int getRewardWindowSize();
	
	/**
	 * Returns the number of ticks to consider that the norm synthesis machine 
	 * has converged to a normative system. It is considered to have converged
	 * whenever, during N ticks, the normative system remains without changes.
	 * 
	 * @return the number of ticks of stability
	 */
	public long getNumTicksOfStabilityForConvergence();
	
	/**
	 * Returns the size of the population of normative systems
	 * 
	 *  @return the size of the population of normative systems
	 */
	public long getNumberOfNormativeSystems();

	/**
	 * @return
	 */
	public long getNumTicksPerSimulationRound();
	
	/**
	 * 
	 * @return
	 */
	public double getInitialNullNPFreq();

	/**
	 * @return
	 */
	public double getExplorationRate();
}
