package uk.ac.ox.cs.ensm.agent;

import java.util.List;

import uk.ac.ox.cs.ensm.agent.language.SetOfPredicatesWithTerms;

/**
 * The context of an environment agent, which is a description of the
 * environment from the agent's local point of view
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public interface AgentContext extends Comparable<AgentContext>{

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Returns a set of predicates with terms that describes the agent context
	 * 
	 * @return a set of predicates with terms that describes the agent context
	 */
	public SetOfPredicatesWithTerms getDescription();
	
	/**
	 * Returns a list with the ids of the agents that are perceived
	 * in the context
	 * 
	 * @return a list with the ids of the agents that are perceived
	 *  in the context
	 */
	public List<Long> getPerceivedAgentsIds();
	
	/**
	 * Returns <tt>true</tt> if this context is equal
	 * to {@code otherContext}
	 * 
	 * @param otherContext the other context to compare this with
	 * @return <tt>true</tt> if the two contexts are equal,
	 * 					and returns <tt>false</tt> otherwise
	 */
	public boolean equals(AgentContext otherContext);
	
	
//	public int compareTo(AgentContext oContext);
}
