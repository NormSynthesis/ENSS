/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.network;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.game.Game;
import uk.ac.ox.cs.ensm.network.edges.ConcurrencyRelationship;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdge;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdgeType;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;
import uk.ac.ox.cs.ensm.ns.replication.NSAttribute;

/**
 * A normative network is a directed graph whose nodes stand for norms
 * and whose edges stand for relationships between norms. Additionally,
 * the normative network contains the following information about the norms
 * it contains:
 * <ol>
 * <li>	the state (whether active or inactive) of each norm in
 * 			the normative network; 
 * <li>	the utility of each norm, which contains information about how
 * 			the norm performs to avoid conflicts with respect to the system
 * 			norm evaluation dimensions and goals; and
 * <li>	the generalisation level of each norm in the network, which stands
 * 			for the height of the norm in the generalisation graph
 * </ol>
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 * @see NormativeSystem
 * @see NSFitness
 */
public class NSNetwork extends UndirectedNodesNetwork<NormativeSystem> {

	//---------------------------------------------------------------------------
	// Static attributes
	//---------------------------------------------------------------------------
	
	private long NS_COUNT = 0;	// number of normative systems in the network

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private Map<NormativeSystem, List<NSAttribute>> attributes; // NS attributes
	private Map<Long,NormativeSystem> index;											// indexed NSs
	private Map<Game,Map<NormativeSystem,BigDecimal>> nsUtilitiesForGame;
	
	private long conflictRatioWindowSize;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * @param ensm
	 */
	public NSNetwork(EvolutionaryNSM ensm) {
		super(ensm);
		
		this.attributes = new HashMap<NormativeSystem, List<NSAttribute>>();
		this.index = new HashMap<Long,NormativeSystem>();
		
		this.conflictRatioWindowSize = ensm.getSettings().getRewardWindowSize();
		
		this.nsUtilitiesForGame = new HashMap<Game,Map<NormativeSystem,BigDecimal>>();
	}

	/**
	 * Adds a given {@code norm} to the normative network if it does
	 * not exist yet. Additionally, it sets the utility for the new norm
	 * and sets its generalisation level
	 * 
	 * @param ns the norm to add
	 */
	public void add(NormativeSystem ns) {
		if(!this.contains(ns)) {
			if(ns.getId() == 0) {
				ns.setId(++NS_COUNT);
			}			
			super.add(ns);

			this.attributes.put(ns, new ArrayList<NSAttribute>());
			this.index.put(ns.getId(), ns);
		}
	}
	
	/**
	 * Adds a relationship between a node {@code nA} and a node
	 * {@code nB}, in the direction nA to nB, just in case
	 * the relationship does not exist yet
	 * 
	 * @param nsa the child node
	 * @param nsb the parent node
	 * @param type the type of the relationship
	 * @see NetworkEdgeType
	 */
	public void addRelationship(NormativeSystem nsa, NormativeSystem nsb) {
		super.addRelationship(nsa, nsb, new ConcurrencyRelationship(nsa, nsb, 
				conflictRatioWindowSize));
	}
	
	/**
	 * 
	 * @param ns
	 * @param tag
	 */
	public void addAttribute(NormativeSystem ns, NSAttribute attribute) {
		if(!this.contains(ns)) {
			this.add(ns);
		}
		if(!this.attributes.containsKey(ns)) {
			this.attributes.put(ns, new ArrayList<NSAttribute>());
		}
		List<NSAttribute> attributes = this.attributes.get(ns);
		if(!attributes.contains(attribute)) {
			attributes.add(attribute);
		}
	}
	
	/**
	 * 
	 * @param norm
	 * @param attributes
	 */
	public void addAttributes(NormativeSystem norm, List<NSAttribute> attributes) {
		for(NSAttribute attr : attributes) {
			this.addAttribute(norm, attr);
		}
	}

	/**
	 * 
	 * @param norm
	 * @param tag
	 */
	public void removeAttribute(NormativeSystem norm, NSAttribute attribute) {
		if(this.attributes.containsKey(norm)) {
			List<NSAttribute> attributes = this.attributes.get(norm);
			if(attributes.contains(attribute)) {
				int idx = attributes.indexOf(attribute);
				attributes.remove(idx);
			}
		}
	}
	
	/**
	 * 
	 * @param norm
	 * @param attributes
	 */
	public void removeAttributes(NormativeSystem norm, List<NSAttribute> attributes) {
		for(NSAttribute attr : attributes) {
			this.removeAttribute(norm, attr);
		}
	}
	
	/**
	 * 
	 * @param norm
	 */
	public void resetAttributes(NormativeSystem norm) {
		if(this.attributes.containsKey(norm)) {
			this.attributes.get(norm).clear();
		}
	}

	
	/**
	 * Sets the state of a norm to the given state in the normative
	 * network, and updates the omega function to update
	 * the normative system
	 * 
	 * @param norm the norm to activate
	 * @see OmegaFunction
	 */
	@Override
	public void setState(NormativeSystem norm, NetworkNodeState state) {
		super.setState(norm, state);
	}
	
	/**
	 * Returns the {@code List} of all the norms in the network
	 * 
	 * @return the {@code List} of all the norms in the network
	 */
	public List<NormativeSystem> getAllNormativeSystems() {
		return new ArrayList<NormativeSystem>(super.getNodes());
	}
	
	/**
	 * 
	 * @return
	 */
	public List<NormativeSystem> getFollowedNormativeSystems() {
		List<NormativeSystem> followedNSs = new ArrayList<NormativeSystem>();
		List<NormativeSystem> nss = this.getAllNormativeSystems();
		 
		for(NormativeSystem ns : nss) {
			if(ns.getNumFollowers() > 0) {
				followedNSs.add(ns);
			}
		}
		return followedNSs;
	}
	
	/**
	 * Returns the norm with the given {@code id}
	 * 
	 * @param id the id of the norm
	 * @return the norm with the given id
	 */
	public NormativeSystem getNSWithId(long id)  {
		return this.index.get(id);
	}

	/**
	 * Returns the norm with the given {@code id}
	 * 
	 * @param id the id of the norm
	 * @return the norm with the given id
	 */
	public List<NormativeSystem> getNormativeSystemsWithNorm(Norm norm )  {
		List<NormativeSystem> nss = new ArrayList<NormativeSystem>();
		for(NormativeSystem ns : this.getNodes()) {
			if(ns.contains(norm)) {
				nss.add(ns);
			}
		}
		return nss;
	}
	
	/**
	 * Returns the list of tags assigned to the {@code norm} received 
	 * by parameter
	 *  
	 * @param ns the norm 
	 * @return the list of tags assigned to the {@code norm} received 
	 * 					by parameter
	 */
	public List<NSAttribute> getAttributes(NormativeSystem ns) {
		return this.attributes.get(ns);
	}

	/**
	 * 
	 * @param ns
	 * @param attribute
	 * @return
	 */
	public boolean hasAttribute(NormativeSystem ns, NSAttribute attribute) {
		List<NSAttribute> attributes = this.getAttributes(ns);
		if(attributes == null) {
			return false;
		}
		return attributes.contains(attribute);
	}
	
	/**
	 * Returns a list containing all the relationships of a type
	 * that the normative network contains 
	 *  
	 * @param type the relationship type
	 * 
	 * @return a list containing all the relationships of a type
	 * that the normative network contains
	 */
	public List<NetworkEdge> getRelationships() {
		List<NetworkEdge> edges = new ArrayList<NetworkEdge>();
		
		for(NetworkEdge edge : this.graph.getEdges()) {
			edges.add(edge);
		}
		return edges;
	}
		
	/**
	 * 
	 * @param na
	 * @param nb
	 * @return
	 */
	public boolean areConcurrent(NormativeSystem nsa, NormativeSystem nsb) {
		if(!this.contains(nsa) || !this.contains(nsb)) {
			return false;
		}
		List<NetworkEdge> edges = this.getRelationships(nsa, nsb);
		
		for(NetworkEdge edge : edges) {
			if(edge.getType() == NetworkEdgeType.Concurrency) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	public void updateConflictRatio(NormativeSystem nsa, NormativeSystem nsb,
			double conflictRatio) {
		
		if(!this.areConcurrent(nsa, nsb)) {
			this.addRelationship(nsa, nsb);
		}
		ConcurrencyRelationship conc = (ConcurrencyRelationship)
				this.getConcurrencyRelationship(nsa, nsb);
		
		conc.addConflictRatioValue(conflictRatio);
	}
	
	/**
	 * 
	 */
	public void updatePayoff(NormativeSystem nsa, NormativeSystem nsb,
			double payoff) {
		
		if(!this.areConcurrent(nsa, nsb)) {
			this.addRelationship(nsa, nsb);
		}
		ConcurrencyRelationship conc = (ConcurrencyRelationship)
				this.getConcurrencyRelationship(nsa, nsb);
		
		conc.updatePayoff(payoff);
	}
	

	/**
	 * 
	 * @param n1
	 * @param n2
	 */
	public ConcurrencyRelationship getConcurrencyRelationship(
			NormativeSystem nsa, NormativeSystem nsb) {
		
		List<NetworkEdge> rels = this.getRelationships(nsa, nsb);
		for(NetworkEdge rel : rels) {
			if(rel.getType() == NetworkEdgeType.Concurrency) {
				return (ConcurrencyRelationship)rel;
			}
		}
		return null;
	}

	/**
	 * @param ns
	 * @return
	 */
	public List<NormativeSystem> getConcurrentNormativeSystems(
			NormativeSystem ns) {
		
		Set<NormativeSystem> setOfNSs = new HashSet<NormativeSystem>();
		
		for(NetworkEdge rel : this.getRelationships(ns)) {
			if(rel.getType() == NetworkEdgeType.Concurrency) {
				ConcurrencyRelationship cRel = (ConcurrencyRelationship)rel;
				
				NormativeSystem ns2 = (NormativeSystem)cRel.getNodeA();
				if(ns2.getId() == ns.getId()) {
					ns2 = (NormativeSystem)cRel.getNodeB();
				}
				
				/* IMPORTANT. Only consider those normative systems that are active
				 * (those that are followed by at least one agent) */
				if(this.getState(ns2) == NetworkNodeState.Active) {
					setOfNSs.add(ns2);	
				}
			}
		}
		return new ArrayList<NormativeSystem>(setOfNSs);
	}

	/**
	 * @param ns
	 * @return
	 */
	public List<ConcurrencyRelationship> getConcurrencyRelationships(
			NormativeSystem ns) {
		
		List<ConcurrencyRelationship> cRels = new ArrayList<ConcurrencyRelationship>();
		List<NetworkEdge> rels = this.getRelationships(ns);
		
		for(NetworkEdge rel : rels) {
			if(rel.getType() == NetworkEdgeType.Concurrency) {
				cRels.add((ConcurrencyRelationship)rel);
			}
		}
		return cRels;
	}
	
	/**
	 * 
	 * @param nsa
	 * @param nsb
	 * @return
	 */
	public double getConflictRatio(NormativeSystem nsa, NormativeSystem nsb) {
		ConcurrencyRelationship rel = this.getConcurrencyRelationship(nsa, nsb);
		if(rel != null) {
			return rel.getCurrentConflictRatio();
		}
		return 0.0;
	}
	
	/**
	 * 
	 * @param nsa
	 * @param nsb
	 * @return
	 */
	public double getPayoff(NormativeSystem nsa, NormativeSystem nsb) {
		ConcurrencyRelationship rel = this.getConcurrencyRelationship(nsa, nsb);
		if(rel != null) {
			return rel.getCurrentPayoff();
		}
		return 0.0;
	}
	
	/**
	 * Returns <tt>true</tt> if the normative network contains the norm
	 * 
	 * @param norm the norm to search for
	 * @return <tt>true</tt> if the normative network contains the norm
	 */
	public boolean contains(NormativeSystem ns)	{
		for(NormativeSystem otherns : this.getNodes()) {
			if(ns.equals(otherns)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 */
	public NormativeSystem getNormativeSystem(NormativeSystem ns)	{
		for(NormativeSystem otherns : this.getNodes()) {
			if(ns.equals(otherns)) {
				return ns;
			}
		}
		return null;
	}
	

	/**
	 * @param nsA
	 * @param fitness
	 */
	public void setUtilityForGame(Game game, NormativeSystem ns, 
			BigDecimal utility) {
		
		if(!this.nsUtilitiesForGame.containsKey(game)) {
			this.nsUtilitiesForGame.put(game, 
					new HashMap<NormativeSystem,BigDecimal>());
		}
		this.nsUtilitiesForGame.get(game).put(ns, utility);
	}
	
	/**
	 * @param nsA
	 * @param fitness
	 */
	public BigDecimal getUtilityForGame(Game game, NormativeSystem ns) {
		return this.nsUtilitiesForGame.get(game).get(ns);
	}
}
