package uk.ac.ox.cs.ensm.config;

import java.util.List;

/**
 * Basic settings of the Norm Synthesis Machine. For instance,
 * the default utility of norms, or the number of ticks of stability
 * for the NSM to converge
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public interface NormSynthesisSettings {
		
	/* Goals and dimensions to synthesise and evaluate norms */
	
	/**
	 * The goals of the NSM 
	 * 
	 * @return a {@code List} containing the system goals
	 * @see Goal
	 */
	public List<Goal> getSystemGoals();
	
	/* ------------------------
	 * NORM EVALUATION SETTINGS
	 * ------------------------ */
	
	/**
	 * 
	 * @return
	 */
	public double getRewardForNotHavingConflicts();
	
	/**
	 * 
	 * @return
	 */
	public double getPunishmentForHavingConflicts();
	
	/**
	 * 
	 * @return
	 */
	public int getNormGroupsConflictRatioRangesSize();
	
	/**
	 * 
	 * @return
	 */
	public int getNormsPerformanceRangesSize();
	
	/**
	 * Returns the number of ticks to consider that the norm synthesis machine 
	 * has converged to a normative system. It is considered to have converged
	 * whenever, during N ticks, the normative system remains without changes.
	 * 
	 * @return the number of ticks of stability
	 */
	public long getNumTicksOfStabilityForConvergence();
}
