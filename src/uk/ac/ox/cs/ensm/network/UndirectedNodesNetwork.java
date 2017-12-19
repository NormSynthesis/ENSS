package uk.ac.ox.cs.ensm.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import sun.text.normalizer.Utility;
import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdge;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdgeType;
import uk.ac.ox.cs.ensm.ns.evaluation.SlidingValueWindow;

/**
 * A network is a directed graph whose nodes stand for nodes
 * and whose edges stand for relationships between nodes. Additionally,
 * the network contains the following information about the nodes
 * it contains:
 * <ol>
 * <li>	the state (whether active or inactive) of each node in
 * 			the network; 
 * <li>	the utility of each node, which contains information about how
 * 			the node performs to avoid conflicts with respect to the system
 * 			node evaluation dimensions and goals; and
 * <li>	the generalisation level of each node in the network, which stands
 * 			for the height of the node in the generalisation graph
 * </ol>
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public class UndirectedNodesNetwork<T> {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------
	
	protected EvolutionaryNSM ensm;							// the norm synthesis machine
	protected EvolutionaryNSMSettings ensmSettings;		// the norm synthesis settings
	
	protected UndirectedSparseMultigraph<T, NetworkEdge> graph;		// graph of nodes	
	
//	protected Map<T,T> index;											// index of nodes
	protected Map<T,NetworkNodeState> states;			// state of each node
	protected Map<T,SlidingValueWindow> nsFitness;						// utilities of each node
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Constructor
	 */
	public UndirectedNodesNetwork(EvolutionaryNSM nsm) {
		this.ensm = nsm;
		this.ensmSettings = nsm.getSettings();		
		
//		this.index = new HashMap<T,T>();
		this.graph = new UndirectedSparseMultigraph<T,NetworkEdge>();
		this.states = new HashMap<T, NetworkNodeState>();
		this.nsFitness = new HashMap<T, SlidingValueWindow>();
	}

	/**
	 * Adds a given {@code node} to the network if it does not exist yet
	 * 
	 * @param node the node to add
	 */
	public void add(T node) {
		if(!this.graph.containsVertex(node)) {
			this.graph.addVertex(node);
			
			/* Set node utility */
			int fitnessRangeSz = ensmSettings.getNSFitnessRangeSize();
			SlidingValueWindow fitness = new SlidingValueWindow(fitnessRangeSz);
			this.nsFitness.put(node, fitness);
		}
	}

	/**
	 * Removes a given {@code node} from the network if it exists yet
	 * 
	 * @param node the node to remove
	 */
	public void remove(T node) {
		if(this.graph.containsVertex(node)) {
			this.graph.removeVertex(node);
		}
	}	

	/**
	 * Adds a relationship between a node {@code nA} and a node
	 * {@code nB}, in the direction nA to nB, just in case
	 * the relationship does not exist yet
	 * 
	 * @param nA the child node
	 * @param nB the parent node
	 * @param type the type of the relationship
	 * @see NetworkEdgeType
	 */
	protected void addRelationship(T nA, T nB, NetworkEdge edge) {
		this.graph.addEdge(edge, nA, nB);
	}
	
	/**
	 * Removes a relationship of a certain {@code type} between a node
	 * {@code nA} and a node {@code nB}, just in case the relationship
	 * exists previously
	 * 
	 * @param nA the child node
	 * @param nB the parent node
	 */
	protected void removeRelationship(T nA, T nB, NetworkEdgeType type) {
		List<NetworkEdge> remove = new ArrayList<NetworkEdge>();
		
		for(NetworkEdge edge : this.graph.getIncidentEdges(nA)) {
			if(edge.getType() == type &&
					this.graph.getIncidentVertices(edge).contains(nB))	{
				remove.add(edge);
			}
		}
		for(NetworkEdge edge: remove) {
			this.graph.removeEdge(edge);
		}
	}
	
	/**
	 * Removes a relationship of a certain {@code type} between a node
	 * {@code nA} and a node {@code nB}, just in case the relationship
	 * exists previously
	 * 
	 * @param nA the child node
	 * @param nB the parent node
	 */
	protected void removeRelationships(T nA, T nB) {
		List<NetworkEdge> remove = new ArrayList<NetworkEdge>();
		
		for(NetworkEdge edge : this.graph.getIncidentEdges(nA)) {
			if(this.graph.getIncidentVertices(edge).contains(nB)) {
				remove.add(edge);
			}
		}
		for(NetworkEdge edge: remove) {
			this.graph.removeEdge(edge);
		}
	}

	/**
	 * Returns the {@code List} of all the nodes in the network
	 * 
	 * @return the {@code List} of all the nodes in the network
	 */
	public Collection<T> getNodes() {
		return this.graph.getVertices();
	}
	
	/**
	 * 
	 * @param node
	 * @return
	 */
	public List<NetworkEdge> getRelationships(T node) {
		List<NetworkEdge> edges = new ArrayList<NetworkEdge>(
				this.graph.getIncidentEdges(node));
		
		return edges;
	}
	
	/**
	 * Returns a {@code List} of those network edges (that is, the relationships)
	 * that start at node {@code node1} and finish at node {@code node2}
	 * 
	 * @param node1 the initial node
	 * @param node2 the final node
	 * @return a {@code List} of those network edges (that is, the
	 * 					relationships) that start at node {@code node1} and finish
	 * 					at node {@code node2}
	 */
	public List<NetworkEdge> getRelationships(T node1, T node2) {
		List<NetworkEdge> edges = new ArrayList<NetworkEdge>();
		Collection<NetworkEdge> incEdges = this.graph.getIncidentEdges(node1);
		
		for(NetworkEdge edge : incEdges) {
			
			List<T> incdVertices = new ArrayList<T>(
					this.graph.getIncidentVertices(edge));
			
			T nd1 = incdVertices.get(0);
			T nd2 = incdVertices.get(1);
			
			if((nd1.equals(node1) && nd2.equals(node2)) || 
					(nd1.equals(node2) && nd2.equals(node1))) {
				edges.add(edge);
			}
		}
		return edges;
	}
	
	/**
	 * Returns the utility of a given {@code node} in the network
	 * 
	 * @param node the node
	 * @return an object {@code Utility}, the utility of the node
	 * @see Utility
	 */
	public double getFitness(T node) {
		return this.nsFitness.get(node).getCurrentPunctualValue();
	}
	
	/**
	 * Returns the utility of a given {@code node} in the network
	 * 
	 * @param node the node
	 * @return an object {@code Utility}, the utility of the node
	 * @see Utility
	 */
	public double getAvgFitness(T node) {
		return this.nsFitness.get(node).getCurrentAverage();
	}
	
	/**
	 * Returns the utility of a given {@code node} in the network
	 * 
	 * @param node the node
	 * @return an object {@code Utility}, the utility of the node
	 * @see Utility
	 */
	public SlidingValueWindow getFitnessRange(T node) {
		return this.nsFitness.get(node);
	}
	
	/**
	 * Returns the cardinality of the network
	 * 
	 * @return the cardinality of the network
	 */
	public int getCardinality() {
		return this.graph.getVertexCount();
	}

	/**
	 * 
	 * @param node
	 * @return
	 */
	public NetworkNodeState getState(T node) {
		if(this.contains(node)) {
			return this.states.get(node);
		}
		return null;
	}
	
	/**
	 * Sets the new score in the utility of a given {@code node} for a given
	 * {@code Dimension} and {@code Goal}
	 * 
	 * @param node the node
	 * @param dim the dimension of the score (effectiveness/necessity)
	 * @param goal the goal of the score
	 * @param fitness the new score
	 */
	public void setFitness(T node, double fitness) {
		this.nsFitness.get(node).addValue(fitness);
	}

	/**
	 * 
	 * @param node
	 * @param state
	 */
	public void setState(T node, NetworkNodeState state) {
		if(this.contains(node)) {
			this.states.put(node, state);	
		}
	}

	/**
	 * Returns <tt>true</tt> if the network contains the node
	 * 
	 * @param node the node to search for
	 * @return <tt>true</tt> if the network contains the node
	 */
	public boolean contains(T n)	{
		return this.graph.containsVertex(n);
	}
}
