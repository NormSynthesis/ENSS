package uk.ac.ox.cs.ensm.network.edges;

import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * An edge between two norms in the normative network. Since norms may have
 * different types of relationships, the {@code TativeNetworkEdge} contains
 * a attribute {@code relationship} that describes the type of relationship
 * that the edge represents
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 * @see NetworkEdgeType
 */
public class InclusionRelationship implements NetworkEdge {

	//---------------------------------------------------------------------------
	// Attributes 
	//---------------------------------------------------------------------------

	private NormativeSystem ns;
	private Norm norm;
	
	//---------------------------------------------------------------------------
	// Methods 
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param relationship the type of relationship that the edge represents
	 * @param predicate
	 */
	public InclusionRelationship(NormativeSystem ns, Norm norm) {
		this.norm = norm;
		this.ns = ns;
	}

	/**
	 * 
	 * @return
	 */
	public NormativeSystem getNormativeSystem(){
		return this.ns;
	}
	
	/**
	 * 
	 * @return
	 */
	public Norm getNorm(){
		return this.norm;
	}
	
	/**
	 * Returns a {@code String} that describes the edge
	 * 
	 * @return a {@code String} that describes the edge
	 */
	public String toString() {
		return this.ns.getName().toString() + " ->" + this.norm.getName();
	}
	
	/**
	 * 
	 */
	public NetworkEdgeType getType() {
		return NetworkEdgeType.Inclusion;
	}
}
