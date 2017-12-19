/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.agent.language;

import java.util.Set;

import uk.ac.ox.cs.ensm.agent.AgentAction;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NormSynthesisGrammar {

	private Set<AgentAction> actions;
	private SetOfStrings predicates;
	private SetOfStrings terms;
	
	/**
	 * 
	 */
	public NormSynthesisGrammar(SetOfStrings predicates, SetOfStrings terms, 
			Set<AgentAction> actions) {

		this.setPredicates(predicates);
		this.setTerms(terms);
		this.actions = actions;
	}

	public Set<AgentAction> getActions() {
			return actions;
	}
	
	/**
	 * 
	 * @param actions
	 */
	public void setActions(Set<AgentAction> actions) {
		this.actions = actions;
	}

	/**
	 * @return the predicates
	 */
	public SetOfStrings getPredicates() {
		return predicates;
	}

	/**
	 * @param predicates the predicates to set
	 */
	public void setPredicates(SetOfStrings predicates) {
		this.predicates = predicates;
	}

	/**
	 * @return the terms
	 */
	public SetOfStrings getTerms() {
		return terms;
	}

	/**
	 * @param terms the terms to set
	 */
	public void setTerms(SetOfStrings terms) {
		this.terms = terms;
	}
}
