package uk.ac.ox.cs.ensm.network;

/**
 * A node in the normative network. It may be a {@code Norm}
 * or a {@code NormGroup}
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public interface NetworkNode {
	
	/**
	 * Returns the id of the node
	 *  
	 * @return the id of the node
	 */
	public long getId();

	/**
	 * Returns a description of the node
	 * 
	 * @return a description of the node
	 */
	public String getDescription();
	
	/**
	 * 
	 * @return
	 */
	public String getName();
}
