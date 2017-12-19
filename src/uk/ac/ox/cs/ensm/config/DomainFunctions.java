package uk.ac.ox.cs.ensm.config;

import java.math.BigDecimal;
import java.util.List;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.AgentContext;
import uk.ac.ox.cs.ensm.agent.AgentInteraction;
import uk.ac.ox.cs.ensm.agent.EvolutionaryAgent;
import uk.ac.ox.cs.ensm.perception.Conflict;
import uk.ac.ox.cs.ensm.perception.View;
import uk.ac.ox.cs.ensm.perception.ViewTransition;

/**
 * Domain functions that allow the Norm Synthesis Machine (NSM) to perform
 * norm synthesis for a particular domain. Specifically, the norm synthesis
 * cycle requires the following domain functions:
 * 
 * (1)	agents' language functions, that allow to create descriptions
 * 			of the environment by means of an agent's language;
 * (2) 	conflict detection functions, that allow to define what situations
 * 			represent a conflict in the particular domain. 
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public interface DomainFunctions {

	//---------------------------------------------------------------------------
	// Agents' language functions
	//---------------------------------------------------------------------------

	/**
	 * Returns the local context of a reference agent with id {@code agentId}
	 * in a given {@code view}. The context of an agent in a view is a
	 * description of the view from the agent's local perspective, in terms
	 * of the agent language. In other words, the agent context describes
	 * how the agent is perceiving the view, by means of its words
	 * 
	 * @param agentId the id of the reference agent
	 * @param view the view in which the agent perceives its local context
	 * @return a {@code SetOfPredicatesWithTerms} that describes 
	 * 						the agent's local context 
	 */
	public AgentContext getContext(long agentId, View view);
	
	/**
	 * Returns the {@code List} of actions that the reference agent
	 * with id {@code agentId} performed in the transition of views
	 * {@code viewTransition}
	 * 
	 * @param 	agentId the id of the reference agent
	 * @param 	viewTransition the transition of views in which
	 * 					the agent performed the actions
	 * @return the {@code List} of actions that the reference agent
	 * 					with id {@code agentId} performed in the transition of views
	 */
	public List<AgentAction> getAction(long agentId, ViewTransition viewTransition);

	//---------------------------------------------------------------------------
	// Agents' synergies functions
	//---------------------------------------------------------------------------
	
	/**
	 * Returns the games that are played in a system state 
	 * 
	 * @param vTrans the state transition containing the state to check  
	 * @return
	 */
	public List<AgentInteraction> getAgentInteractions(ViewTransition vTrans);
	
	/**
	 * Returns a description of a game played in a view
	 * 
	 * @param view the view in which the game is played
	 * @param agA agent A of the game
	 * @param agB agent B of the game 
	 * 
	 * @return a description of a game played in a view
	 */
	public View getGameDescription(View view, EvolutionaryAgent agA,
	    EvolutionaryAgent agB);
	
	//---------------------------------------------------------------------------
	// Conflict detection functions
	//---------------------------------------------------------------------------
	
	/**
	 * Returns a {@code List} containing the new, non-regulated conflicts
	 * that have arisen during a transition of views {@code viewTransition}.
	 * That is, those conflicts that have been originated  during the 
	 * {@code viewTransition} by some agents to which no norms
	 * applied before the conflict.
	 * 
	 * @return a {@code List} containing the new, non-regulated conflicts
	 * 					that have arisen during a transition of views
	 * 					{@code viewTransition}.
	 * @see Goal
	 * @see Conflict
	 */
	public List<Conflict> getConflicts(Goal goal,	ViewTransition viewTransition);
	
	/**
	 * Returns <tt>true</tt> if the agent with id {@code agentId} is in
	 * conflict in a {@code view} with respect to a system {@code goal}
	 * 
	 * @param vTrans the view in which to check if the agent is in conflict
	 * @param agentId the id of the reference agent 
	 * @param goal the goal 
	 * @return <tt>true</tt> if the agent with id {@code agentId}
	 * 					is in conflict in the view
	 * @see Goal
	 * @see Conflict
	 */
	public double getReward(ViewTransition vTrans, long agentId, Goal goal);
	
}
