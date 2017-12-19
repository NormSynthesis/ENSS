package uk.ac.ox.cs.ensm.network;

/**
 * Enumeration that defines the different states of a norm in the normative 
 * network. A norm in the normative network may be either active or inactive.
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 * @see NormGroupNetwork
 */
public enum NetworkNodeState {
	Created, 
	
	Candidate, 
	
	Active, 
	
	Inactive, 
	
	Discarded, 
	
	Represented, 
	
	Specialised, 
	
	Substituted, 
	
	Excluded; 
}

