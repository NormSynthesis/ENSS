package uk.ac.ox.cs.ensm.norm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.AgentContext;
import uk.ac.ox.cs.ensm.agent.Combination;
import uk.ac.ox.cs.ensm.game.Game;
import uk.ac.ox.cs.ensm.network.NGNNode;
import uk.ac.ox.cs.ensm.network.NetworkNode;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class Norm implements Comparable<Norm>, NetworkNode, NGNNode {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------
	
	/** The id of the norm */
	private long id; 

	/** Coordination game the norm belongs to */
	private Game game;

	/** Agent contexts (one for each role) */
	private List<AgentContext> contexts;
	
	/** Action combination prescribed to coordinate the players  */
	private Combination<AgentAction> actionCombination;

	private Map<AgentContext,List<AgentAction>> prohibitions;
	
	//---------------------------------------------------------------------------
	// Public methods
	//---------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public Norm(Game game, List<AgentContext> contexts, 
			Combination<AgentAction> actionCombination) {

		this.game = game;
		this.contexts = contexts;
		this.actionCombination = actionCombination;
		this.prohibitions = new HashMap<AgentContext,List<AgentAction>>();
	}

	/**
	 * Returns the action prescribed for a role
	 * 
	 * @param role
	 * @return
	 */
	public Combination<AgentAction> getActionCombination() {
		return this.actionCombination;
	}

	/**
	 * Returns the id of the norm
	 * 
	 * @return the id of the norm
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * Sets the new {@code id} of the norm
	 * 
	 * @param id the new constraint id
	 */
	public void setId(long id)	{
		this.id = id;
	}

	/** 
	 * 
	 * @return
	 */
	public Game getGame() {
		return this.game;
	}

	/**
	 * Returns the name of the norm
	 * 
	 * @return the name of the norm
	 */
	public String getName() {
		return "n" + id;
	}

	/**
	 * 
	 */
	public String toString() {
		return this.getName();
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDetailedDescription() {
		String s = "";
		
//		Collections.sort(contexts);
		for(AgentContext c : this.contexts) {
			s += c.toString() + "\n";
		}
		s += this.actionCombination.toString();
		
		return s;
	}
	
	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.network.
	 * NGNNode#equals(uk.ac.ox.cs.ensm.network.NGNNode)
	 */
	@Override
	public boolean equals(NGNNode oNode) {
		if(!(oNode instanceof Norm)){
			return false;
		}
		return this.equals((Norm)oNode);
	}

	/**
	 * 
	 * @param otherNorm
	 * @return
	 */
	private boolean equals(Norm otherNorm) {
		if(this.game.getId() != otherNorm.getGame().getId()) {
			return false;
		}

		if(this.actionCombination.equals(otherNorm.getActionCombination())) {
			return true; 
		}
		else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.network.NetworkNode#getDescription()
	 */
	@Override
	public String getDescription() {
		return this.actionCombination.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Norm otherNorm) {
		if(otherNorm.getId() < this.id) {
			return 1;
		}
		else if(otherNorm.getId() > this.id) {
			return -1;
		}
		return 0;
	}

	/**
	 * Returns the agents' contexts
	 * 
	 * @return
	 */
	public List<AgentContext> getContexts() {
		return this.contexts;
	}

	/**
	 * 
	 * @param contexts
	 */
	public void setContexts(List<AgentContext> contexts) {
		this.contexts = contexts;
	}	
}
