/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.ns.replication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.Combination;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.game.Game;
import uk.ac.ox.cs.ensm.game.NormativeGame;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.norm.reasoning.NSReasoner;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * Class to evaluate norms after agents fulfil and infringe them. This
 * class contains all the necessary methods to compute norm applicability,
 * norm compliance, and to evaluate norms based on their compliance outcomes.
 * To this aim it employs a utility function that evaluates a norm in terms
 * of some evaluation criteria. This function, which must be provided as a
 * parameter in the class' constructor, may vary depending on the synthesis
 * strategy that is to be executed.
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NormsReplicator {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private NormativeGamesNetwork ngNetwork;
	//	private BigDecimal reward;
	//	private BigDecimal punishment;

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * 
	 * @param nsReasoners
	 * @param dmFunctions
	 * @param ngNetwork
	 * @param ensmSettings
	 */
	public NormsReplicator(Map<NormativeSystem,NSReasoner> nsReasoners,
			DomainFunctions dmFunctions, NormativeGamesNetwork ngNetwork, 
			EvolutionaryNSMSettings ensmSettings) {

		this.ngNetwork = ngNetwork;

		//		double dReward = ensmSettings.getRewardForAvoidingConflicts();
		//		double dPunishment = ensmSettings.getPunishmentForHavingConflicts();

		//		this.reward = new BigDecimal(dReward);
		//		this.punishment = new BigDecimal(dPunishment);
	}

	/**
	 * 
	 * @param viewTransitions
	 * @param normApplicability
	 * @param normCompliance
	 * @param normGroupCompliance
	 */
	public void doReplication() {

		/* Loop of all games played so long */
		List<NormativeGame> nGames = this.ngNetwork.getValidNormativeGames();
		for(NormativeGame nGame : nGames) {

			/* Compute the utility and the fitness of each
			 * norm of the game */
			this.computeUtilities(nGame);
			this.computeFitness(nGame);
			this.replicateNorms(nGame);

			/* Update the frequencies of each norm of the game */
			this.ngNetwork.updateNormsFrequencies(nGame);
		}
	}

	/**
	 * 
	 */
	private void computeUtilities(NormativeGame nGame) {
		Game game = nGame.getGame();
		int numRoles = game.getNumRoles();

		/* Iterate over each norm combination, computing its utility to 
		 * coordinate each role of the corresponding coordination game */
		for(Combination<Norm> nc : nGame.getNormCombinations()) {

			/* Retrieve action combination dictated by the norms of 
			 * the norm combination to each one of the roles */
			Combination<AgentAction> ac = new Combination<AgentAction>();

			for(int role=0; role<numRoles; role++) {
				AgentAction action = nc.get(role).getActionCombination().get(role);
				ac.add(action);
			}

			/* For each role of the game... */
			for(int role=0; role<numRoles; role++) {
				double utility = computeEffectiveness(ac, role, nGame);

				/* Update the utility of the norm combination */
				nGame.setUtility(role, nc, utility);
			}
		}
	}

	/**
	 * @param role
	 * @param nGame
	 * @param np
	 */
	private double computeEffectiveness(Combination<AgentAction> ac,
			int role, NormativeGame nGame) {

		Game game = nGame.getGame();
		return game.getPayoff(ac, role);
	}

	/**
	 * @param ns
	 * @param game
	 * @return
	 */
	private void computeFitness(NormativeGame nGame) {

		/* Compute the fitness of each norm of the normative game */
		for(Norm norm : nGame.getNormSpace()) {
			double fitness = 0.0;

			/* Iterate over each role of the game */
			for(int role=0; role<nGame.getNumRoles(); role++) {

				/* Retrieve all the norm combinations in which 
				 * the norm applies to the given role */
				List<Combination<Norm>> nCombinations = 
						new ArrayList<Combination<Norm>>();

				for(Combination<Norm> nc : nGame.getNormCombinations()) {
					if(nc.get(role).equals(norm)) {
						nCombinations.add(nc);
					}
				}

				/* Compute the fitness of the norm in terms of the utility
				 * that an agent to whom the norm applies derives for any
				 * possible role and norm combination */
				for(Combination<Norm> nc : nCombinations) {

					/* Compute the probability that a group of agents interact while 
					 * having the norm combination (computed as the joint frequency 
					 * of each one of the norms in the norm combination */
					double normFreq = 1;
					for(int i=0; i<nGame.getNumRoles(); i++) {

						/* We do not consider the frequency of the norm applicable to 
						 * the role that is being evaluated, only the frequency of 
						 * the norms applicable to the other roles */
						if(i == role) {
							continue;
						}
						Norm n = nc.get(i);
						normFreq = normFreq * nGame.getFrequency(n);
					}

					/* If the norm is applicable to a given role in the
					 * norm combination, then consider the utility of the 
					 * role by using its applicable norm */
					double utility = nGame.getUtility(role, nc);
					double weightedUtility = utility * normFreq;
					fitness += weightedUtility;						
				}	
			}

			/* The new fitness of the norm averages the utility of the norm
			 * for each possible role and for each action combination in which
			 * the norm applies to an agent enacting that role */
			nGame.setFitness(norm, fitness);
		}
	}


	/**
	 * 
	 * @param nsApplicability
	 * @param normsActivatedDuringGeneration
	 */
	public void replicateNorms(NormativeGame game) {

		/* Compute average fitness of all the normative profiles of the game */
		double avgFitness = this.computeAvgRelativeFitness(game);

		for(Norm norm : game.getNormSpace()) {

			/* Update frequency of the norm based on its fitness with 
			 * respect to the average fitness of all the norms of the game */
			double freq = game.getFrequency(norm);
			double fitness = game.getFitness(norm);
			double newFreq = freq + freq * (fitness - avgFitness);
			
			/* Add new frequency of the norm */
			game.setFrequency(norm, newFreq);
			this.ngNetwork.setFrequency(norm, newFreq);

			/* If the frequency of the norm is 100%, then the game has converged */
			if(newFreq > 0.9) {
				game.setConverged(true);
				game.setConvergedNorm(norm);
			}
		}

		/* Increase the number of times that the norms of 
		 * the norm space of the game have been replicated */
		game.incNumReplications();
	}

	/**
	 * @param ns
	 * @param ngNetwork
	 * @return
	 */
	private double computeAvgRelativeFitness(NormativeGame game) {
		double avgFitness = 0.0;

		for(Norm norm : game.getNormSpace()) {
			double normFitness = game.getFitness(norm);
			double normFreq = game.getFrequency(norm);
			avgFitness += normFreq * normFitness;
		}
		return avgFitness;
	}
}
