/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.agent.AgentContext;
import uk.ac.ox.cs.ensm.agent.Combination;
import uk.ac.ox.cs.ensm.network.NGNNode;
import uk.ac.ox.cs.ensm.norm.Norm;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NormativeGame implements NGNNode {

	/** The coordination game to regulate */
	private Game game;

	/** Context of each agent in the game */
	private List<AgentContext> contexts;
	
	/** Utility matrix */
	private AsymmetricPayoffMatrix<Norm> uMatrix;
	
	/** Norms' fitness */
	private Map<Norm,Double> normsFitness;
	
	/** Norms' frequencies */
	private Map<Norm,Double> normsFreqs;
	
//	/** Norms' fitness */
//	private Map<Norm,BigDecimal> normsFitness;
//	
//	/** Norms' frequencies */
//	private Map<Norm,BigDecimal> normsFreqs;

	/** Did the game converge to a unique norm? */
	private boolean converged;

	/** Norm to which the agent population has converged */
	private Norm convergedNorm;

	/** Number of times that the norms of the game have been replicated */
	private int numReplications;

	/**
	 * Constructor
	 */
	public NormativeGame(Game game, List<Combination<Norm>> norms) {
		this.game = game;
		this.numReplications = 0;
		
		this.normsFitness	= new HashMap<Norm,Double>();
		this.normsFreqs  	= new HashMap<Norm,Double>();
		
		/* Generate utility matrix */
		this.uMatrix = new AsymmetricPayoffMatrix<Norm>(norms);
	}

	/**
	 * Returns the norm space of the normative game
	 * 
	 * @return a list with all the norms of the norm space of the game
	 */
	public List<Norm> getNormSpace() {
		List<Norm> norms = new ArrayList<Norm>(
				this.uMatrix.getStrategySpace(0));
		
		Collections.sort(norms);
		return norms;
	}

	/**
	 * Returns all the possible norm combinations of the game
	 * 
	 * @return a list of all the possible norm combinations of the game
	 */
	public List<Combination<Norm>> getNormCombinations() {
		return this.uMatrix.getCombinations();
	}

	/**
	 * Returns the utility of a norm combination for a given role
	 * 
	 * @param numRole a given role 
	 * @param nc a norm combination 
	 * @return the utility of a norm combination for a given role
	 */
	public double getUtility(int numRole, Combination<Norm> nc) {
		if(this.getNumRoles() == 1) {
			Norm n1 = nc.get(0);
			return this.uMatrix.getPayoff(numRole, new Combination<Norm>(n1));
		}
		else {
			Norm n1 = nc.get(0);
			Norm n2 = nc.get(1);
			return this.uMatrix.getPayoff(numRole, new Combination<Norm>(n1,n2));
		}
	}

	/**
	 * Sets the utility of a norm combination for a given role
	 * 
	 * @param numRole a given role 
	 * @param nc a norm combination
	 * @param utility the utility of the norm combination for the given role
	 */
	public void setUtility(int numRole, Combination<Norm> nc, double utility) {
		this.uMatrix.setPayoff(numRole, nc, utility);
	}

	/**
	 * Returns the fitness of a norm
	 * 
	 * @param norm the norm
	 * @return the fitness of a norm
	 */
	public double getFitness(Norm norm) {
		return this.normsFitness.get(norm);
	}

	/**
	 * 
	 * @param n1
	 * @param n2
	 * @param payoff
	 */
	public void setFitness(Norm norm, double fitness) {
		this.normsFitness.put(norm, fitness);
	}

	/**
	 * 
	 * @param np
	 * @return
	 */
	public double getFrequency(Norm norm) {
		return this.normsFreqs.get(norm);
	}

	/**
	 * 
	 * @return
	 */
	public double getFrequency() {
		return this.game.getFrequency();
	}

	/**
	 * 
	 * @param np
	 * @param freq
	 */
	public void setFrequency(Norm norm, double freq) {
		this.normsFreqs.put(norm, freq);
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
	public List<AgentContext> getAgentContexts() {
		return this.game.getAgentContexts();
	}
	
	/**
	 * 
	 */
	public int getNumRoles() {
		return this.game.getNumRoles();
	}

	/**
	 * 
	 * @param id
	 */
	@Override
	public void setId(long id) {
		this.game.setId(id);
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public String getDescription() {
		return this.game.getDescription();
	}

	/**
	 * 
	 * @return
	 */
	public long getId() {
		return this.game.getId();
	}

	/**
	 * 
	 * @return
	 */
	public long getUniqueId() {
		return this.game.getUniqueId();
	}
	
	/**
	 * 
	 */
	@Override
	public String getName() {
		return "G" + this.getId();
	}

	/**
	 * 
	 */
	@Override
	public boolean equals(NGNNode oNode) {
		if(!(oNode instanceof NormativeGame)){
			return false;
		}

		NormativeGame oGame = (NormativeGame) oNode;
		return this.game.equals(oGame.getGame());
	}

	/**
	 * 
	 */
	public String toString() {
		DecimalFormat df = new DecimalFormat("####0.000");
		String s = "";
		
		/* Show payoff and utility matrices */
		s = this.game.toString() + " \n";
		
		s += "Norms of the game:\n"; 
		for(Norm norm: this.getNormSpace()) {
			s += norm.getName() + ":" + norm.getDescription() + "\n";	
		}
		
//		s += "\nUtility matrix: \n " + this.uMatrix.toString() + "\n";
		
		/* Show fitness and frequencies of each norm*/
		s += "\nFitness (and frequencies):\n"; 
		for(Norm norm: this.getNormSpace()) {
			double fitness = this.normsFitness.get(norm);
			double freq = this.normsFreqs.get(norm);
			
			s += norm.getName() + ": " + df.format(fitness) +
					"(" + df.format(freq) + ")\n";
		}
		return s;
	}
	
	/**
	 * 
	 * @param role
	 * @return
	 */
	public AgentContext getContext(int role) {
		return this.contexts.get(role);
	}

	/**
	 * @param b
	 */
	public void setConverged(boolean b) {
		this.converged = b;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasConverged() {
		return this.converged;
	}

	/**
	 * @return
	 */
	public Norm getConvergedNorm() {
		return this.convergedNorm;
	}

	/**
	 * @param norm
	 */
	public void setConvergedNorm(Norm norm) {
		this.convergedNorm = norm;
	}
	
	/**
	 * 
	 */
	public void incNumReplications() {
		this.numReplications++;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumReplications() {
		return this.numReplications;
	}
}
