/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.network;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public interface NGNNode {

	/**
	 * Returns the id of the node
	 *  
	 * @return the id of the node
	 */
	public long getId();

	/**
	 * 
	 * @param id
	 */
	public void setId(long id);
	
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
	
	/**
	 * 
	 * @param oNode
	 * @return
	 */
	public boolean equals(NGNNode oNode);
}
