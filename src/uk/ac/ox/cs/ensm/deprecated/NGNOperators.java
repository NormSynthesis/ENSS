package uk.ac.ox.cs.ensm.deprecated;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.agent.language.PredicatesDomains;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.metrics.EvolutionaryNSMetrics;
import uk.ac.ox.cs.ensm.network.NetworkNodeState;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * A set of generic normative network operators employed by a generic 
 * norm synthesis strategy to perform norm synthesis 
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public class NGNOperators {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	protected NormReasoner normReasoner;					// norm reasoner
	protected DomainFunctions dmFunctions;				// domain functions
	protected PredicatesDomains predDomains;			// predicates and their domains
	protected NormativeGamesNetwork ngNetwork;	// the norm groups network
	protected EvolutionaryNSMSettings ensmSettings;	// norm synthesis settings
	protected EvolutionaryNSMetrics nsMetrics;			// norm synthesis metrics
	protected boolean pursueCompactness;					// perform norm generalisations?
	protected boolean pursueLiberality;						// exploit norm synergies?

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param 	strategy the SIMON norm synthesis strategy
	 * @param 	nsReasoner the norm reasoner, to reason about norm
	 * 					applicability	and compliance
	 * @param 	ensm the norm synthesis machine
	 */
	public NGNOperators(EvolutionaryNSM ensm) {

		this.dmFunctions = ensm.getDomainFunctions();
		this.predDomains = ensm.getPredicatesDomains();
		this.ngNetwork = ensm.getNormativeGamesNetwork();
		this.ensmSettings = ensm.getSettings();
		this.nsMetrics = ensm.getNormSynthesisMetrics();
	}

	/**
	 * Adds a norm to the normative network if the normative network
	 * does not contain it yet. Also, if the NSM is performing
	 * conservative norm generalisations, it generates and keeps
	 * track of the potential generalisations of the norm 
	 * 
	 * @param norm the norm to add
	 */
	public void add(NormativeSystem ns) {
		if(!ngNetwork.contains(ns)) {
			this.ngNetwork.add(ns);
		}
	}

	/**
	 * Activates a given {@code norm} in the normative network by setting 
	 * its state to "active". Also, if the NSM is exploiting norm synergies
	 * to pursue liberality, it activates the norm groups containing the norm
	 * 
	 * @param ns the norm to activate
	 * @see {@code NormGroup}
	 */	
	public void activate(NormativeSystem ns) {
		ngNetwork.setState(ns, NetworkNodeState.Active);
	}

	/**
	 * Activates a given {@code norm} in the normative network by setting 
	 * its state to "active". Also, if the NSM is exploiting norm synergies
	 * to pursue liberality, it activates the norm groups containing the norm
	 * 
	 * @param ns the norm to activate
	 * @see {@code NormGroup}
	 */	
	public void deactivate(NormativeSystem ns) {
		ngNetwork.setState(ns, NetworkNodeState.Inactive);
	}
	
}
