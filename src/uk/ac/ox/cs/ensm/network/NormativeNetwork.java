/**
 * 
 * @author Javier Morales (jmorales@iiia.csic.es)
 */
package uk.ac.ox.cs.ensm.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.impl.util.Utility;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.network.edges.ComplementarityRelationship;
import uk.ac.ox.cs.ensm.network.edges.ConcurrencyRelationship;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdge;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdgeType;
import uk.ac.ox.cs.ensm.network.edges.SubstitutabilityRelationship;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.norm.NormAttribute;

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
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 * @see Norm
 * @see Utility
 */
public class NormativeNetwork extends GeneralisationNetwork<Norm> {

	//---------------------------------------------------------------------------
	// Static attributes
	//---------------------------------------------------------------------------
	
	private int NORM_COUNT = 0;	// number of norms in the network

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private Map<Norm, List<NormAttribute>> attributes; 	// norm attributes
	private Map<Long,Norm> ids;											// indexed norms
	private long conflictRatioWindowSize;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * @param ensm
	 */
	public NormativeNetwork(EvolutionaryNSM ensm) {
		super(ensm);
		
		this.attributes = new HashMap<Norm, List<NormAttribute>>();
		this.ids = new HashMap<Long,Norm>();
		
		this.conflictRatioWindowSize = ensm.getSettings().getRewardWindowSize();

	}

	/**
	 * Adds a given {@code norm} to the normative network if it does
	 * not exist yet. Additionally, it sets the utility for the new norm
	 * and sets its generalisation level
	 * 
	 * @param norm the norm to add
	 */
	public void add(Norm norm) {
		if(!this.contains(norm)) {
			if(norm.getId() == 0) {
				norm.setId(++NORM_COUNT);	
			}			
			super.add(norm);

			this.attributes.put(norm, new ArrayList<NormAttribute>());
			this.ids.put(norm.getId(), norm);
		}
	}
	
	/**
	 * 
	 * @param norm
	 * @param tag
	 */
	public void addAttribute(Norm norm, NormAttribute attribute) {
		if(!this.contains(norm)) {
			this.add(norm);
		}
		if(!this.attributes.containsKey(norm)) {
			this.attributes.put(norm, new ArrayList<NormAttribute>());
		}
		List<NormAttribute> attributes = this.attributes.get(norm);
		if(!attributes.contains(attribute)) {
			attributes.add(attribute);
		}
	}
	
	/**
	 * 
	 * @param norm
	 * @param attributes
	 */
	public void addAttributes(Norm norm, List<NormAttribute> attributes) {
		for(NormAttribute attr : attributes) {
			this.addAttribute(norm, attr);
		}
	}

	/**
	 * 
	 * @param norm
	 * @param tag
	 */
	public void removeAttribute(Norm norm, NormAttribute attribute) {
		if(this.attributes.containsKey(norm)) {
			List<NormAttribute> attributes = this.attributes.get(norm);
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
	public void removeAttributes(Norm norm, List<NormAttribute> attributes) {
		for(NormAttribute attr : attributes) {
			this.removeAttribute(norm, attr);
		}
	}
	
	/**
	 * 
	 * @param norm
	 */
	public void resetAttributes(Norm norm) {
		if(this.attributes.containsKey(norm)) {
			this.attributes.get(norm).clear();
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
	public void addConcurrency(Norm na, Norm nb) {
		super.addRelationship(na, nb, new ConcurrencyRelationship(na, nb, 
				conflictRatioWindowSize));
	}
	
	/**
	 * 
	 * @param na
	 * @param nb
	 * @return
	 */
	public boolean areConcurrent(Norm na, Norm nb) {
		if(!this.contains(na) || !this.contains(nb)) {
			return false;
		}
		List<NetworkEdge> edges = this.getRelationships(na, nb);
		
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
	public void updateConflictRatio(Norm n1, Norm n2, double conflictRatio) {
		if(!this.areConcurrent(n1, n2)) {
			this.addConcurrency(n1, n2);
		}
		ConcurrencyRelationship conc = 
				(ConcurrencyRelationship)this.getConcurrencyRelationship(n1, n2);
		
		conc.addConflictRatioValue(conflictRatio);
	}
	
	/**
	 * Adds a substitutability relationship between two norms
	 * 
	 * @param nA the first norm
	 * @param nB the second norm
	 */
	public void addSubstitutability(Norm nA, Norm nB) {
		if(!this.contains(nA)) {
			this.add(nA);
		}
		if(!this.contains(nB)) {
			this.add(nB);
		}
		super.addRelationship(nA, nB, 
				new SubstitutabilityRelationship<Norm>(nA, nB));
		super.addRelationship(nB, nA, 
				new SubstitutabilityRelationship<Norm>(nB, nA));
		
		this.removeComplementarity(nA, nB);
	}
	
	/**
	 * Removes a substitutability relationship between two norms
	 * 
	 * @param nA the first norm
	 * @param nB the second norm
	 */
	public void removeSubstitutability(Norm nA, Norm nB) {
		super.removeRelationship(nA, nB, NetworkEdgeType.Substitutability);
		super.removeRelationship(nB, nA, NetworkEdgeType.Substitutability);
	}
	
	/**
	 * 
	 * @param child
	 * @param parent
	 */
	public void addComplementarity(Norm nA, Norm nB) {
		if(!this.contains(nA)) {
			this.add(nA);
		}
		if(!this.contains(nB)) {
			this.add(nB);
		}
		super.addRelationship(nA, nB, 
				new ComplementarityRelationship<Norm>(nA, nB));
		super.addRelationship(nB, nA, 
				new ComplementarityRelationship<Norm>(nB, nA));
		
		this.removeSubstitutability(nA, nB);
	}
	
	/**
	 * 
	 * @param child
	 * @param parent
	 */
	public void removeComplementarity(Norm nA, Norm nB) {
		super.removeRelationship(nA, nB, NetworkEdgeType.Complementarity);
		super.removeRelationship(nB, nA, NetworkEdgeType.Complementarity);
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
	public void setState(Norm norm, NetworkNodeState state) {
		super.setState(norm, state);
	}
	
	/**
	 * Returns the {@code List} of all the norms in the network
	 * 
	 * @return the {@code List} of all the norms in the network
	 */
	public Collection<Norm> getNorms() {
		return super.getNodes();
	}
	
	/**
	 * 
	 * @param norm
	 * @return
	 */
	public List<Norm> getSubstitutableNorms(Norm norm) {
		List<Norm> ret = new ArrayList<Norm>();
		
		/* The node has no parents (no outgoing generalisation relationships) */
		if(this.graph.getOutEdges(norm) == null) {
			return ret;
		}
		/* Check relationships with other nodes */
		for(NetworkEdge edge : this.graph.getOutEdges(norm)) {
			
			/* If it is a generalisation relationship, retrieve
			 * its destination (the parent, general node) */ 
			if(edge.getType() == NetworkEdgeType.Substitutability) {
				ret.add(this.graph.getDest(edge));
			}
		}
		return ret;
	}
	
//	/**
//	 * Returns a norm in the normative network with the given
//	 * {@code precondition}, {@code modality} and {@code action}
//	 * 
//	 * @param precondition the norm precondition
//	 * @param modality the norm modality
//	 * @param action the regulated action
//	 * @param goal the goal regulated by the norm
//	 * @return the norm with the given elements
//	 */
//	public Norm getNorm(SetOfPredicatesWithTerms precondition,
//			NormModality modality, AgentAction action) {
//		
////		Norm n = new Norm(precondition, modality, action);
////		for(Norm norm : this.getNodes()) {
////			if(n.equals(norm)) {
////				return norm;
////			}
////		}
//		return null;
//	}

	/**
	 * Returns a norm in the normative network with the 
	 * {@code precondition}, {@code modality} and {@code action}
	 * of the norm passed by parameter
	 * 
	 * @param the norm to retrieve from the normative network
	 * @return the norm with the given elements
	 */
	public Norm retrieveNorm(Norm n) {
		for(Norm norm : this.getNodes()) {
			if(n.equals(norm)) {
				return norm;
			}
		}
		return null;
	}
	
	/**
	 * Returns the norm with the given {@code id}
	 * 
	 * @param id the id of the norm
	 * @return the norm with the given id
	 */
	public Norm getNormWithId(int id)  {
		return this.ids.get(id);
	}
	
	/**
	 * Returns the list of tags assigned to the {@code norm} received 
	 * by parameter
	 *  
	 * @param norm the norm 
	 * @return the list of tags assigned to the {@code norm} received 
	 * 					by parameter
	 */
	public List<NormAttribute> getAttributes(Norm norm) {
		return this.attributes.get(norm);
	}

	/**
	 * 
	 * @param norm
	 * @param attribute
	 * @return
	 */
	public boolean hasAttribute(Norm norm, NormAttribute attribute) {
		List<NormAttribute> attributes = this.getAttributes(norm);
		if(attributes == null) {
			return false;
		}
		return attributes.contains(attribute);
	}
	
	/**
	 * Returns <tt>true</tt> if the state of the norm is
	 * active in the network
	 * 
	 * @return <tt>true</tt> if the state of the norm is
	 * 					active in the network
	 */
	@Override
	public NetworkNodeState getState(Norm norm) {
		if(!this.contains(norm)) {
			return null;
		}
		norm = this.retrieveNorm(norm);
		return this.states.get(norm);
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
	public List<NetworkEdge> getRelationships(NetworkEdgeType type) {
		List<NetworkEdge> rels = new ArrayList<NetworkEdge>();
		for(NetworkEdge edge : this.graph.getEdges()) {
			if(edge.getType() == type) {
				rels.add(edge);
			}
		}
		return rels;
	}
	
	/**
	 * 
	 * @param n1
	 * @param n2
	 */
	public NetworkEdge getConcurrencyRelationship(Norm n1, Norm n2) {
		List<NetworkEdge> rels = this.getRelationships(n1, n2);
		for(NetworkEdge rel : rels) {
			if(rel.getType() == NetworkEdgeType.Concurrency) {
				return rel;
			}
		}
		return null;
	}
	
	/**
	 * Returns <tt>true</tt> if one of the following conditions hold:
	 * <ol>
	 * <li> the given {@code norm} is active in the network; or
	 * <li>	the given {@code norm} is inactive but some of its ancestors
	 * 			(the nodes that generalise and represent it) are active in
	 * 			the network
	 * </ol>
	 * 
	 * @param norm the norm
	 * @return <tt>true</tt> if the norm is active in the network,
	 * or it is inactive but some of its ancestors are active in the network
	 */
	public boolean isRepresented(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		norm = this.retrieveNorm(norm);
//		
//		if(this.getState(norm) == NetworkNodeState.ACTIVE) {
//			return true;
//		}
//		else {
//			List<Norm> parents = this.getParents(norm);
//			
//			for(Norm parent : parents) {
//				if(this.isRepresented(parent)) {
//					return true;
//				}
//			}
//		}
//		return false;
		
		return super.isRepresented(norm);
	}
	
	/**
	 * 
	 * @param ancestor
	 * @param norm
	 * @return
	 */
	public boolean isAncestor(Norm ancestor, Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		norm = this.retrieveNorm(norm);
		
		List<Norm> parents = this.getParents(norm);
		for(Norm parent : parents) {
			if(parent.equals(ancestor)) {
				return true;
			}
			else {
				if(this.isAncestor(ancestor, parent)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns <tt>true</tt> if the given {@code norm} has been assigned
	 * the tag <i>Ineffective</i>
	 * 
	 * @param norm the norm to check
	 * @return <tt>true</tt> if the given {@code norm} has been assigned
	 * 					the tag <i>Ineffective</i>
	 */
	public boolean isIneffective(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		return this.attributes.get(norm).contains(NormAttribute.Ineffective);
	}
	
	/**
	 * Returns <tt>true</tt> if the given {@code norm} has been assigned
	 * the tag <i>Unnecessary</i>
	 * 
	 * @param norm the norm to check
	 * @return <tt>true</tt> if the given {@code norm} has been assigned
	 * 					the tag <i>Unnecessary</i>
	 */
	public boolean isUnnecessary(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		return this.attributes.get(norm).contains(NormAttribute.Unnecessary);
	}

	/**
	 * Returns <tt>true</tt> if the given {@code norm} has been assigned
	 * the tag <i>Generalisable</i>
	 * 
	 * @param norm the norm to check
	 * @return <tt>true</tt> if the given {@code norm} has been assigned
	 * 					the tag <i>Generalisable</i>
	 */
	public boolean isGeneralisable(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		return this.attributes.get(norm).contains(NormAttribute.Generalisable);
	}
	
	/**
	 * Returns <tt>true</tt> if the given {@code norm} has been assigned
	 * the tag <i>Substitutable</i>
	 * 
	 * @param norm the norm to check
	 * @return <tt>true</tt> if the given {@code norm} has been assigned
	 * 					the tag <i>Substitutable</i>
	 */
	public boolean isSubstitutable(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		return this.attributes.get(norm).contains(NormAttribute.Substitutable);
	}
	
	/**
	 * Returns <tt>true</tt> if the given {@code norm} has been assigned
	 * the tag <i>Substituter</i>
	 * 
	 * @param norm the norm to check
	 * @return <tt>true</tt> if the given {@code norm} has been assigned
	 * 					the tag <i>Substituter</i>
	 */
	public boolean isSubstituter(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		return this.attributes.get(norm).contains(NormAttribute.Substituter);
	}
	
	/**
	 * Returns <tt>true</tt> if the given {@code norm} has been assigned
	 * the tag <i>Exclussive</i>
	 * 
	 * @param norm the norm to check
	 * @return <tt>true</tt> if the given {@code norm} has been assigned
	 * 					the tag <i>Exclussive</i>
	 */
	public boolean isExclussive(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		return this.attributes.get(norm).contains(NormAttribute.Exclusive);
	}
	
	/**
	 * Returns <tt>true</tt> if the given {@code norm} has been assigned
	 * the tag <i>Complementary</i>
	 * 
	 * @param norm the norm to check
	 * @return <tt>true</tt> if the given {@code norm} has been assigned
	 * 					the tag <i>Complementary</i>
	 */
	public boolean isComplementary(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		return this.attributes.get(norm).contains(NormAttribute.Complementary);
	}
	
	/**
	 * Returns <tt>true</tt> if the node is a leaf in the
	 * network, namely it has generalisation level = 0
	 * 
	 * @param node the node
	 * @return <tt>true</tt> if the node is a leaf in the
	 * 					network, namely it has generalisation level = 0
	 */
	public boolean isLeaf(Norm norm) {
		if(!this.contains(norm)) {
			return false;
		}
		Norm n = this.retrieveNorm(norm);
		return this.getChildren(n).size() <= 0;
	}
	
	/**
	 * Returns <tt>true</tt> if a given pair of norms {@code n1} and {@code n2}
	 * have a substitutability relationship in this normative network
	 * 
	 * @param n1 the first norm
	 * @param n2 the second norm
	 * @return <tt>true</tt> if a given pair of norms {@code n1} and {@code n2}
	 * 					have a substitutability relationship in this normative network
	 */
	public boolean areSubstitutable(Norm n1, Norm n2) {
		if(!this.contains(n1) || !this.contains(n2)) {
			return false;
		}
		
		List<NetworkEdge> edges = this.getRelationships(n1, n2);
		
		for(NetworkEdge edge : edges) {
			if(edge.getType() == NetworkEdgeType.Substitutability) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns <tt>true</tt> if a given pair of norms {@code n1} and {@code n2}
	 * have a complementarity relationship in this normative network
	 * 
	 * @param n1 the first norm
	 * @param n2 the second norm
	 * @return <tt>true</tt> if a given pair of norms {@code n1} and {@code n2}
	 * 					have a complementarity relationship in this normative network
	 */
	public boolean areComplementary(Norm n1, Norm n2) {
		if(!this.contains(n1) || !this.contains(n2)) {
			return false;
		}
		
		List<NetworkEdge> edges = this.getRelationships(n1, n2);
		
		for(NetworkEdge edge : edges) {
			if(edge.getType() == NetworkEdgeType.Complementarity) {
				return true;
			}
		}
		return false;
	}

//	/**
//	 * Returns <tt>true</tt> if the normative network contains a norm 
//	 * with the given {@code precondition}, {@code modality} and {@code action}
//	 * 
//	 * @param precondition the norm precondition
//	 * @param modality the norm modality
//	 * @param action the regulated action
//	 * @param goal the goal regulated by the norm
//	 * @return <tt>true</tt> if the normative network contains a norm 
//	 * 					with the given {@code precondition}, {@code modality}
//	 * 					and {@code action}
//	 */
//	public boolean containsNormWith(SetOfPredicatesWithTerms precondition, 
//			NormModality modality, AgentAction action) {
//		
//		Norm n = new Norm(precondition, modality, action);
//		for(Norm norm : this.getNodes()) {
//			if(n.equals(norm)) {
//				return true;
//			}
//		}
//		return false;
//	}
	
	/**
	 * Returns <tt>true</tt> if the normative network contains the norm
	 * 
	 * @param norm the norm to search for
	 * @return <tt>true</tt> if the normative network contains the norm
	 */
	public boolean contains(Norm n)	{
		for(Norm norm : this.getNodes()) {
			if(n.equals(norm)) {
				return true;
			}
		}
		return false;
	}
}
