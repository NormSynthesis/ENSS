package uk.ac.ox.cs.ensm.norm.applicability;

/**
 * A norm compliance represents the action of fulfilling or infringing the
 * deontic operator of a norm. For instance, if a a norm prohibits an agent
 * to go forward in a certain situation, and the agent does not go forward
 * in that situation, then the agent has fulfilled the norm. By contrast, if
 * the agent goes forward in that particular situation, then the agent
 * has infringed the norm 
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public enum NormCompliance {
	Fulfilment, Infringement;
}
