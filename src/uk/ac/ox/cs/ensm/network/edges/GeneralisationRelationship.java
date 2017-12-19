package uk.ac.ox.cs.ensm.network.edges;

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
public class GeneralisationRelationship<T> implements NetworkEdge {

	//---------------------------------------------------------------------------
	// Attributes 
	//---------------------------------------------------------------------------

	private T child, parent;
	
	//---------------------------------------------------------------------------
	// Methods 
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param relationship the type of relationship that the edge represents
	 * @param predicate
	 */
	public GeneralisationRelationship(T child, T parent) {
		this.child = child;
		this.parent = parent;
	}

	/**
	 * 
	 * @return
	 */
	public T getChild(){
		return this.child;
	}
	
	/**
	 * 
	 * @return
	 */
	public T getParent(){
		return this.parent;
	}
	
	/**
	 * Returns a {@code String} that describes the edge
	 * 
	 * @return a {@code String} that describes the edge
	 */
	public String toString() {
		return this.child.toString() + " ->" + this.parent.toString();
	}
	
	/**
	 * 
	 */
	public NetworkEdgeType getType() {
		return NetworkEdgeType.Generalisation;
	}
}
