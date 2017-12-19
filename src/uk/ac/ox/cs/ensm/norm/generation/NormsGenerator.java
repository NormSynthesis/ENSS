package uk.ac.ox.cs.ensm.norm.generation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.Combination;
import uk.ac.ox.cs.ensm.agent.language.NormSynthesisGrammar;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.game.Game;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.norm.Norm;

/**
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 *
 */
public class NormsGenerator {
	
	protected NormativeGamesNetwork ngNetwork;
	protected DomainFunctions dmFunctions;
	protected NormSynthesisGrammar grammar;
	protected Set<Norm> createdNorms;
	
	private int NUM_NORMS = 0;
	
	/**
	 * Constructor 
	 */
	public NormsGenerator(EvolutionaryNSMSettings ensmSettings,  
			DomainFunctions dmFunctions, NormativeGamesNetwork ngNetwork,
			NormSynthesisGrammar grammar) {

		this.ngNetwork = ngNetwork;
		this.dmFunctions = dmFunctions;
		this.grammar = grammar;
		
		this.createdNorms = new HashSet<Norm>();
	}

	/**
	 * Generates all the norms that can regulate a coordination game 
	 * 
	 * @return
	 */
	public List<Combination<Norm>> generateNormCombinations(
			Game game) {
		
		/* Norms for each role */
		List<Norm> norms = new ArrayList<Norm>();
		List<Combination<AgentAction>> aCombinations = 
				game.getActionCombinations();
		
		/* Generate a norm for each possible action combination of the game */
		for(Combination<AgentAction> ac : aCombinations) {
			Norm norm = new Norm(game, game.getAgentContexts(), ac);
			norms.add(norm);
			norm.setId(++NUM_NORMS);
		}
		
		/* Generate all the possible norm combinations of the game */
		List<Combination<Norm>> normCombinations = 
				new ArrayList<Combination<Norm>>();
		
		/* Generate norm combinations for role 1 */
		for(Norm norm : norms) {
			Combination<Norm> nc = new Combination<Norm>(norm);
			normCombinations.add(nc);
		}
		
		/* Create action combinations for the remaining roles (up to N) */
		for(int numRole=1; numRole<game.getNumRoles(); numRole++) {
			List<Combination<Norm>> newCombinations = 
					new ArrayList<Combination<Norm>>();
			
			for(Combination<Norm> nc : normCombinations) {
				for(Norm norm : norms) {
					
					/* Create a new combination in which an action 
					 * for a new role has been added */
					Combination<Norm> newCombination = 
							new Combination<Norm>(nc);
					
					newCombination.add(norm);
					newCombinations.add(newCombination);
				}				
			}
			/* Replace the original action combination to update it */
			normCombinations = newCombinations;
		}
		return normCombinations;
	}
}
