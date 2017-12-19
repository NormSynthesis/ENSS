package uk.ac.ox.cs.ensm.network.edges;

import java.util.ArrayList;
import java.util.List;

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
public class SubstitutabilityRelationship<T> implements NetworkEdge {

	//---------------------------------------------------------------------------
	// Attributes 
	//---------------------------------------------------------------------------

	private T nA, nB;
	private List<T> nodes;
	
	//---------------------------------------------------------------------------
	// Methods 
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param relationship the type of relationship that the edge represents
	 * @param predicate
	 */
	public SubstitutabilityRelationship(T nA, T nB) {
		this.nA = nA;
		this.nB = nB;
		
		this.nodes = new ArrayList<T>();
		this.nodes.add(nA);
		this.nodes.add(nB);
	}

	/**
	 * 
	 * @return
	 */
	public T getNormA(){
		return this.nA;
	}
	
	/**
	 * 
	 * @return
	 */
	public T getNormB(){
		return this.nB;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<T> getNodes() {
		return this.nodes;
	}
	
	/**
	 * Returns a {@code String} that describes the edge
	 * 
	 * @return a {@code String} that describes the edge
	 */
	public String toString() {
		return this.nA.toString() + " <->" + this.nB.toString();
	}
	
	/**
	 * 
	 */
	public NetworkEdgeType getType() {
		return NetworkEdgeType.Generalisation;
	}
}
