package uk.ac.ox.cs.ensm.network.edges;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.ensm.network.NetworkNode;
import uk.ac.ox.cs.ensm.ns.evaluation.SlidingValueWindow;

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
public class ConcurrencyRelationship implements NetworkEdge {

	//---------------------------------------------------------------------------
	// Attributes 
	//---------------------------------------------------------------------------

	private SlidingValueWindow conflictRatioRange;
	private SlidingValueWindow payoffRange;
	private List<NetworkNode> nodes;
	private NetworkNode nodeA, nodeB;
	
	//---------------------------------------------------------------------------
	// Methods 
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param relationship the type of relationship that the edge represents
	 * @param predicate
	 */
	public ConcurrencyRelationship(NetworkNode nodeA, NetworkNode nodeB,
			long conflictRangeSz) {
		
		this.setNodeA(nodeA);
		this.setNodeB(nodeB);

		this.conflictRatioRange = new SlidingValueWindow(conflictRangeSz);
		this.payoffRange = new SlidingValueWindow(conflictRangeSz);
		this.nodes = new ArrayList<NetworkNode>();
		
		this.nodes.add(nodeA);
		this.nodes.add(nodeB);
	}

	/**
	 * 
	 * @return
	 */
	public List<NetworkNode> getNodes(){
		return this.nodes;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getCurrentConflictRatio() {
		return this.conflictRatioRange.getCurrentAverage();
	}
	
	/**
	 * 
	 * @return
	 */
	public SlidingValueWindow getConflictRatioRange() {
		return this.conflictRatioRange;
	}
	
	/**
	 * 
	 */
	public void addConflictRatioValue(double value) {
		this.conflictRatioRange.addValue(value);
	}
	
	/**
	 * 
	 * @param value
	 */
	public void updatePayoff(double value) {
		this.payoffRange.addValue(value);
	}
	
	/**
	 * @return
	 */
	public double getCurrentPayoff() {
		return this.payoffRange.getCurrentPunctualValue();
	}
	
	/**
	 * Returns a {@code String} that describes the edge
	 * 
	 * @return a {@code String} that describes the edge
	 */
	public String toString() {
		return "Nodes: " + this.nodes.toString() + 
				" / Conflict ratio: " + this.conflictRatioRange;
	}

	/* (non-Javadoc)
	 * @see uk.ac.ox.cs.ensm.ns.network.NetworkEdge#getType()
	 */
	@Override
	public NetworkEdgeType getType() {
		return NetworkEdgeType.Concurrency;
	}

	/**
	 * @return the nodeA
	 */
	public NetworkNode getNodeA() {
		return nodeA;
	}

	/**
	 * @param nodeA the nodeA to set
	 */
	public void setNodeA(NetworkNode nodeA) {
		this.nodeA = nodeA;
	}

	/**
	 * @return the nodeB
	 */
	public NetworkNode getNodeB() {
		return nodeB;
	}

	/**
	 * @param nodeB the nodeB to set
	 */
	public void setNodeB(NetworkNode nodeB) {
		this.nodeB = nodeB;
	}
}
