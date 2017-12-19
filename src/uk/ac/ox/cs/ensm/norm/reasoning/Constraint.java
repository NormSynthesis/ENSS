/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.norm.reasoning;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.AgentContext;
import uk.ac.ox.cs.ensm.norm.Norm;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class Constraint implements Comparable<Constraint> {

	private Norm norm;
	private int role;
	private AgentContext context;
	private AgentAction action;
	
	/**
	 * 
	 */
	public Constraint(int role, AgentContext context, 
			AgentAction action, Norm norm) {
		
		this.role = role;
		this.norm = norm;
		this.context = context;
		this.action = action;
	}
	
	/**
	 * 
	 * @return
	 */
	public AgentContext getContext() {
		return this.context;
	}
	
	/**
	 * 
	 * @return
	 */
	public AgentAction getAction() {
		return this.action;
	}
	
	/**
	 * 
	 * @return
	 */
	public Norm getNorm() {
		return this.norm;
	}

	/**
	 * 
	 * @return
	 */
	public int getRole() {
		return this.role;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getId() {
		return this.role;
	}
	
	/**
	 * 
	 */
	public String getName() {
		long normId = norm.getId();
		return "n" + normId + "-" + role;
	}
	
	/**
	 * 
	 */
	@Override
	public int compareTo(Constraint otherConstraint) {
		if(otherConstraint.getNorm().getId() < this.getNorm().getId() ||
				otherConstraint.getId() < this.role) {
			return 1;
		}
		else if(otherConstraint.getNorm().getId() > this.getNorm().getId() ||
				otherConstraint.getId() > this.role) {
			return -1;
		}
		return 0;
	}
	
	/**
	 * 
	 */
	public String toString() {
		return norm.getName() + "-" + this.getId() + 
				":(" + this.context + "," + this.action.toString() + ")";
	}
}
