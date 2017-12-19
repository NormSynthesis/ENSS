/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class AgentInteraction {

	private List<EvolutionaryAgent> agents;
	private List<AgentContext> contexts;
	
	/**
	 * 
	 */
	public AgentInteraction() {
		this.agents = new ArrayList<EvolutionaryAgent>();
		this.contexts = new ArrayList<AgentContext>();
	}
	
	/**
	 * 
	 */
	public AgentInteraction(EvolutionaryAgent ag1, AgentContext ag1Context) {
		this();
		this.agents.add(ag1);
		this.contexts.add(ag1Context);
	}

	/**
	 * 
	 */
	public AgentInteraction(EvolutionaryAgent ag1, AgentContext ag1Context,
			EvolutionaryAgent ag2, AgentContext ag2Context) {
		this();
		
		this.agents.add(ag1);
		this.agents.add(ag2);
		this.contexts.add(ag1Context);
		this.contexts.add(ag2Context);
	}
	
	/**
	 * 
	 */
	public AgentInteraction(List<EvolutionaryAgent> agents, 
			List<AgentContext> contexts) {
		this();
		
		for(EvolutionaryAgent agent : agents) {
			this.agents.add(agent);	
		}
		for(AgentContext context: contexts) {
			this.contexts.add(context);	
		}
	}
		
	
	/**
	 * 
	 * @return
	 */
	public List<EvolutionaryAgent> getAgents() {
		return this.agents;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<AgentContext> getContexts() {
		return this.contexts;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumRoles() {
		return this.agents.size();
	}
}
