/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.ns.generation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.ac.ox.cs.ensm.agent.language.NormSynthesisGrammar;
import uk.ac.ox.cs.ensm.agent.language.PredicatesDomains;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.game.NormativeGame;
import uk.ac.ox.cs.ensm.network.NetworkNodeState;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.norm.reasoning.NSReasoner;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NSGenerator {

	//---------------------------------------------------------------------------
	// Attributes methods
	//---------------------------------------------------------------------------

	private Random random;
	private NormativeGamesNetwork ngNetwork;
	//	private DomainFunctions dmFunctions;
	//	private PredicatesDomains predDomains;

	/* Map of NS reasoners, one for each normative system */
	private Map<NormativeSystem,NSReasoner> nsReasoners;


	//---------------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------------


	/**
	 * 
	 */
	public NSGenerator(Random random, NormativeGamesNetwork ngNetwork,
			NormSynthesisGrammar grammar, PredicatesDomains predDomains, 
			DomainFunctions dmFunctions, EvolutionaryNSMSettings ensmSettings) {

		this.random = random;
		this.ngNetwork = ngNetwork;

		/* Create map to keep track the reasoner of each normative system */
		this.nsReasoners = new HashMap<NormativeSystem,NSReasoner>();
	}

	/**
	 * 
	 * 
	 * @param numAgs
	 * @param npFreqs
	 * @return
	 */
	public List<NormativeSystem> generateNormativeSystems(long numAgs) {
		List<NormativeGame> nGames = this.ngNetwork.getNormativeGames();
		List<NormativeSystem> nss = new ArrayList<NormativeSystem>();
		BigDecimal numAgents = new BigDecimal(numAgs);

		/* Create list of new, empty  normative systems 
		 * to be filled with norms */
		for(int i=0; i<numAgs; i++) {
			nss.add(new NormativeSystem());
		}
		
		/* Obviates the normative games whose frequency is 
		 * lower than 0.0001 (unused) */
		nGames = this.removeUnusedGames(nGames, 0.0001);

		/* Iterate over each agent and each game, choosing a norm 
		 * to coordinate in the game with probability equal to 
		 * the actual frequency of the norm in that game */
		for(NormativeGame game : nGames) {
			List<Norm> norms = this.chooseNormsProportionally(game, numAgs); 

			for(int agId=0; agId<numAgs; agId++) {
				Norm norm = norms.get(agId); // get the norm chosen for the agent
				nss.get(agId).add(norm); // Add it to the normative system of the agent	
			}
		}

		/* Create normative systems */
		this.ngNetwork.clearNormativeSystems();

		BigDecimal totalFreq = BigDecimal.ZERO;
		for(int nsIdx=0; nsIdx<numAgs; nsIdx++) {
			NormativeSystem ns = nss.get(nsIdx);
			
			/* Add the normative system to the NGN */
			ns = this.useNormativeSystem(ns);
			nss.set(nsIdx, ns);
			
			ns.incNumFollowers();
			BigDecimal numFollowers = new BigDecimal(ns.getNumFollowers());
			ns.setFrequency(numFollowers.divide(numAgents));
			totalFreq = totalFreq.add(new BigDecimal(0.001));
		}
		return nss;
	}
	
	/**
	 * @param nGames
	 * @param d
	 */
	private List<NormativeGame> removeUnusedGames(List<NormativeGame> nGames,
			double minFreq) {
		
		List<NormativeGame> ret = new ArrayList<NormativeGame>();
		
		for(NormativeGame nGame : nGames) {
			if(nGame.getFrequency() > minFreq) {
				ret.add(nGame);
			}
		}
		return ret;
	}

	/**
	 * @param game
	 * @param numAgs
	 * @return
	 */
	private List<Norm> chooseNormsProportionally(NormativeGame game, 
			long numAgents) {
		
		/* 1. Generate a data structure that helps us choose a norm for
		 * the game based on its frequency */
		List<Norm> norms = new ArrayList<Norm>(); 
		for(Norm norm : game.getNormSpace()) {

			double normFreq = game.getFrequency(norm);
			int intNormFreq = (int) ((int)numAgents * normFreq);
			
			/* Copy the norm n times in the data structure */
			norms.addAll(Collections.nCopies(intNormFreq, norm));
		}
		
		/* 2. Shuffle the list of norms. If the list of norms is not full yet 
		 * (due to cast from big decimal to integers), then fill it 
		 * with random norms */
//		Collections.shuffle(norms);
		
		int numAddNorms = (int)numAgents - norms.size();
		for(int i=0; i<numAddNorms; i++) {
			int rndNorm = this.random.nextInt(norms.size());
			Norm norm = norms.get(rndNorm);
			norms.add(norm);
		}
		return norms;
	}

	/**
	 * Chooses a norm out of those of a game based on its frequency 
	 * 
	 * @param game a game
	 * @param numAgents the number of agents in the population
	 * @return
	 */
	public List<Norm> chooseNormsRandomly(NormativeGame game, long numAgents) {
		List<Norm> agNorms = new ArrayList<Norm>();
		
		/* 1. Generate a data structure that helps us choose a norm for
		 * the game based on its frequency */
		List<Norm> gameNorms = new ArrayList<Norm>(); 
		for(Norm norm : game.getNormSpace()) {

			double normFreq = game.getFrequency(norm);
			int intNormFreq = (int) (numAgents * normFreq);

			/* Copy the norm n times in the data structure */
			gameNorms.addAll(Collections.nCopies(intNormFreq, norm));
		}
		
		/* 2. For each agent, randomly choose a norm and add it to the list */
		for(int agId=0; agId<numAgents; agId++) {
			int rndNorm = this.random.nextInt(gameNorms.size());	
			agNorms.add(gameNorms.get(rndNorm));
		}
		return agNorms;
	}

	/**
	 * Adds a normative system to the NGN if it does not exist, or retrieves 
	 * it if the NGN already contains a normative system with the same norms 
	 * 
	 * @param ns
	 * @return
	 */
	public NormativeSystem useNormativeSystem(NormativeSystem ns) {
		if(!this.ngNetwork.contains(ns)) {
			this.ngNetwork.add(ns);
		}
		else {
			ns = this.ngNetwork.getNormativeSystem(ns);
		}
		
		/* Activate the normative system */
		this.ngNetwork.setState(ns, NetworkNodeState.Active);
		for(Norm norm : ns) {
			this.ngNetwork.setState(norm, NetworkNodeState.Active);
		}
		return ns;
	}

	/**
	 * @param numAgents
	 * @return
	 */
	public List<NormativeSystem> generateEmptyNormativeSystems(long numAgents) {
		List<NormativeSystem> nss = new ArrayList<NormativeSystem>();
		NormativeSystem ns = new NormativeSystem();

		/* Add empty normative system to the NGN */
		this.ngNetwork.add(ns);

		/* Create and return list */
		for(int i=0; i<numAgents; i++) {
			nss.add(ns);	
		}
		return nss;
	}

	/**
	 * @return
	 */
	public Map<NormativeSystem, NSReasoner> getNSReasoners() {
		return this.nsReasoners;
	}
}
