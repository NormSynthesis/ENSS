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

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.AgentContext;
import uk.ac.ox.cs.ensm.agent.Combination;
import uk.ac.ox.cs.ensm.network.NGNNode;
import uk.ac.ox.cs.ensm.ns.evaluation.SlidingValueWindow;
import uk.ac.ox.cs.ensm.perception.View;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class Game implements NGNNode {

	/** Number of players of the game */
	private int numPlayers;

	/** Description of a game in a state */
	private View description;

	/** Identifier of the game */
	private long id;
	
	/** Number of times that the game has been played */
	private double timesPlayed;

	/** Frequency with which the game is played */
	private double frequency;

	/** Context of each agent in the game */
	private List<AgentContext> contexts;

	/** Payoff matrix */
	private AsymmetricPayoffMatrix<AgentAction> payoffMatrix;

	/** Conflict rates sliding windows */
	private Map<Combination<AgentAction>,SlidingValueWindow[]> rewards;

	/** Size of the sliding window to compute conflict rates */
	private int crWdwSize;

	/** Minimum number of values for each action combination and each role 
	 * to consider the game as valid */
	private int minNumValues = 3;
	
	/**
	 * Constructor for two players
	 * 
	 * @param view
	 * @param ctxtA
	 * @param ctxtB
	 * @param actions
	 * @param crWindowSize
	 * @param numPlayers
	 */
	public Game(View view, List<AgentContext> contexts, 
			int crWindowSize) {

		this.numPlayers = contexts.size();
		this.description = view;
		this.contexts = contexts;
		this.crWdwSize = crWindowSize;
		this.timesPlayed = 0.0;
		this.frequency = 0.0;

		/* Generate lists with the agents of the games, their contexts and
		 * the strategies available to these agents */
		this.contexts = new ArrayList<AgentContext>();
		this.contexts.addAll(contexts);
	
		/* Generate and initialise payoff and conflict ratio matrices and
		 * the matrix that keeps track of the probability for each action */
		this.payoffMatrix = new AsymmetricPayoffMatrix<AgentAction>();
		this.rewards = new HashMap<Combination<AgentAction>,
				SlidingValueWindow[]>();
		
		/* Sort agent contexts */
		Collections.sort(this.contexts);
		
		if(contexts.contains(null)) {
			System.out.println();
		}
	}
	
	/**
	 * Constructor for two players
	 * 
	 * @param view
	 * @param ctxtA
	 * @param ctxtB
	 * @param actions
	 * @param crWindowSize
	 * @param numPlayers
	 */
	public Game(View view, List<AgentContext> contexts, 
			List<Combination<AgentAction>> actionCombinations, int crWindowSize) {

		this(view, contexts, crWindowSize);
		for(Combination<AgentAction> ac : actionCombinations) {
			this.addActionCombination(ac);	
		}
	}
	
	/**
	 * Check that the action combination exists. Otherwise, create
	 * the necessary structure to keep its conflict rate history
	 * 
	 * @param ac
	 */
	public void addActionCombination(Combination<AgentAction> ac) {

		/* Just in case the action combination does not exist, add it to both
		 * data structures */
		this.payoffMatrix.add(ac);

		/* Doing the same with conflict rate windows is a bit more elaborate */
		if(!this.rewards.containsKey(ac)) {
			int numRoles = ac.size();
			this.rewards.put(ac, new SlidingValueWindow[numRoles]);

			/* Create conflict rate sliding windows */
			for(int role=0; role<numRoles; role++) {
				this.rewards.get(ac)[role] = new SlidingValueWindow(crWdwSize);
			}
		}
	}
	
	/**
	 * Returns the payoff of a role given that all players have performed 
	 * an action combination. The payoff is expressed as the degree of success
	 * to avoid conflicts
	 *   
	 * @return
	 */
	public double getPayoff(Combination<AgentAction> ac, int role) {
		return this.payoffMatrix.getPayoff(role, ac);
	}

	/**
	 * Sets the payoff of a given action combination for a given role
	 * 
	 * @param ac
	 * @param role
	 */
	public void setPayoff(Combination<AgentAction> ac, int role, 
			double payoff) {
		
		this.payoffMatrix.setPayoff(role, ac, payoff);
	}
	
	/**
	 * Adds a reward to the agent enacting a given role after a group 
	 * of agents have played the game by performing an action combination 
	 * 
	 * @param ac the action combination performed by the agents
	 * @param role the role of the agent that got the reward  
	 * @param reward the reward obtained by the agent playing the role 
	 */
	public void addReward(Combination<AgentAction> ac,
			int role, double reward) {
		
		this.addActionCombination(ac);

		/* Add value to the historic */
		this.rewards.get(ac)[role].addValue(reward);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumRoles() {
		return this.numPlayers;
	}

	/**
	 * 
	 * @return
	 */
	public List<AgentContext> getAgentContexts() {
		return this.contexts;
	}

	/**
	 * Returns the description of the game (its view) 
	 * 
	 * @return the view
	 */
	public View getView() {
		return description;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public double getNumTimesPlayed() {
		return timesPlayed;
	}

	/**
	 * 
	 */
	public void incTimesPlayed() {
		this.timesPlayed++;
	}

	/**
	 * 
	 * @return
	 */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * 
	 * @param frequency
	 */
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	/**
	 * @return
	 */
	public List<AgentAction> getActionSpace(int role) {
		return this.payoffMatrix.getStrategySpace(role);
	}
	
	/**
	 * Returns true if the payoff matrix of the game has enough 
	 * information to assess its payoffs with a minimum guarantee 
	 *  
	 * @return
	 */
	public boolean isValid() {
		
		/* For each action combination and each role of the game, check 
		 * whether its corresponding sliding window has enough information */
		for(Combination<AgentAction> ac : this.payoffMatrix.getCombinations()) {
			for(int role=0; role<this.getNumRoles(); role++) {
				
				/* Retrieve number of values and check the condition mentioned above */
				int numValues = this.rewards.get(ac)[role].getNumPunctualValues();
				if(numValues < this.minNumValues) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Returns all the action combinations of the game 
	 * 
	 * @return
	 */
	public List<Combination<AgentAction>> getActionCombinations() {
		return this.payoffMatrix.getCombinations();
	}

	/**
	 * Returns a description of the game in text format
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return generateDescription(this.contexts);
	}
	
	/**
	 * 
	 */
	public String toString () {
		DecimalFormat df = new DecimalFormat("####0.00000");
		String s = "Game " + this.getUniqueId() + " (Freq. " + 
				df.format(this.frequency) + ")\n";

		s += this.getDescription();
		
		s += "\n\nPayoff matrix:\n";
		s += this.payoffMatrix.toString();
		return s;
	}

	/**
	 * 
	 * @return
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @return
	 */
	public long getUniqueId() {
		return this.getDescription().hashCode();
	}
	
	/**
	 * 
	 */
	@Override
	public String getName() {
		return "G" + this.getId();
	}

	/**
	 * Compares the game with another game based on the 
	 * similarities of their agents' contexts 
	 */
	public boolean equals(Game oGame) {
		if(this.getNumRoles() != oGame.getNumRoles()) {
			return false;
		}
		
		List<AgentContext> ctxts = new ArrayList<AgentContext>(
				this.getAgentContexts());

		List<AgentContext> oCtxts = new ArrayList<AgentContext>(
				oGame.getAgentContexts());

		AgentContext oCtxtToRemove = null;

		for(AgentContext context : ctxts) {
			boolean ctxtFound = false;

			for(AgentContext oContext : oCtxts) {

				if(context.equals(oContext)) {
					oCtxtToRemove = oContext;
					ctxtFound = true;
					break;
				}
			}

			/* If the context has been found, remove it from the 
			 * list of other contexts (to not compare them again) */
			if(ctxtFound) {
				oCtxts.remove(oCtxtToRemove);
			}
			else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 */
	@Override
	public boolean equals(NGNNode oNode) {
		if(!(oNode instanceof Game)){
			return false;
		}

		Game oGame = (Game) oNode;
		if(this.numPlayers != oGame.getNumRoles()) {
			return false;
		}
		
		int numContexts = contexts.size();
		for(int i=0; i<numContexts; i++) {
			AgentContext ctxt = this.contexts.get(i);
			AgentContext oCtxt = oGame.getAgentContexts().get(i);
			if(!ctxt.equals(oCtxt)) {
				return false;
			}
		}
		return true;
	}
	

	/**
	 * @param contexts2
	 * @return
	 */
	public static String generateDescription(List<AgentContext> contexts) {
		String s = "\n\nContext of role 1:\n" + contexts.get(0);
		if(contexts.size() == 2) {
			s += "\nContext of role 2:\n " + contexts.get(1);	
		}
		return s;
	}
}
