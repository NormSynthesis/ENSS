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
import uk.ac.ox.cs.ensm.game.NormCombination;
import uk.ac.ox.cs.ensm.game.NormativeGame;
import uk.ac.ox.cs.ensm.network.edges.ConcurrencyRelationship;
import uk.ac.ox.cs.ensm.network.edges.GNGRelationship;
import uk.ac.ox.cs.ensm.network.edges.InclusionRelationship;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdge;
import uk.ac.ox.cs.ensm.network.edges.NetworkEdgeType;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

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
public class NormativeGamesNetwork extends UndirectedNodesNetwork<NGNNode> {

	//---------------------------------------------------------------------------
	// Static attributes
	//---------------------------------------------------------------------------

	private long NORM_COUNT = 0;	// number of games in the network
	private long GAME_COUNT = 0;	// number of norms in the network
	private long NS_COUNT = 0;		// number of normative systems in the network

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	/* Indexes for games, norms and normative systems */
	//	private Map<Long,NormativeSystem> nsIndex;
	private Map<String,NormativeSystem> nsIndex;
	private Map<Long,NormativeGame> 		nGamesIndex;
	private Map<Long,Game> 							gamesIndex;
	private Map<String,Game>						gamesIndexedByDesc;
	private Map<Long,Norm> 							normsIndex;
	
	/* Frequency of each normative profile */
	private Map<Norm, Double> normFrequencies;
	private Map<NormCombination, Double> npFrequencies;
	

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * @param ensm
	 */
	public NormativeGamesNetwork(EvolutionaryNSM ensm) {
		super(ensm);

		this.nsIndex 			= new HashMap<String,NormativeSystem>();
		this.nGamesIndex 	= new HashMap<Long,NormativeGame>();
		this.gamesIndex	 	= new HashMap<Long,Game>();
		this.normsIndex		= new HashMap<Long,Norm>();
		
		this.gamesIndexedByDesc = new HashMap<String,Game>();
		
		this.normFrequencies 	= new HashMap<Norm,Double>();
		this.npFrequencies 		= new HashMap<NormCombination,Double>();
	}

	/**
	 * Adds a given {@code norm} to the normative network if it does
	 * not exist yet. Additionally, it sets the utility for the new norm
	 * and sets its generalisation level
	 * 
	 * @param ns the norm to add
	 */
	public void add(NGNNode node) {
		if(this.contains(node)) {
			return;
		}

		/* Add the node */
		super.add(node);

		/* Game settings */
		if(node instanceof Game) {
			Game game = (Game)node;

			if(node.getId() == 0) {
				node.setId(++GAME_COUNT);
			}
			this.gamesIndex.put(node.getId(), game);
			this.gamesIndexedByDesc.put(game.getDescription(), game);
		}

		else if(node instanceof NormativeGame) {
			NormativeGame nGame = (NormativeGame)node;
			Game game = nGame.getGame();

			/* Add corresponding normative game and
			 * establish relationships with the game */
			super.add(nGame);
			super.addRelationship(game, nGame, 
					new GNGRelationship(game, nGame));

			this.nGamesIndex.put(game.getId(), nGame);
		}

		/* Norm settings */
		else if(node instanceof Norm) {
			if(node.getId() == 0) {
				node.setId(++NORM_COUNT);
			}			
			this.normsIndex.put(node.getId(), (Norm)node);
		}

		/* Normative system settings */
		else if(node instanceof NormativeSystem) {
			NormativeSystem ns = (NormativeSystem)node;
			if(!this.contains(ns)) {
				if(node.getId() == 0) {
					node.setId(++NS_COUNT);
				}
				this.nsIndex.put(ns.getDescription(), 
						(NormativeSystem)node);
			}
		}
	}

	/**
	 * 
	 * @param game
	 */
	public void updateNormsFrequencies(NormativeGame game) {
		for(Norm norm : game.getNormSpace()) {
			double freq = game.getFrequency(norm);
			this.setFrequency(norm, freq);
		}
	}

	/**
	 * 
	 * @param nsa
	 * @param nsb
	 */
	public void addRelationship(NormativeSystem ns, Norm norm) {
		super.addRelationship(ns, norm, new InclusionRelationship(ns, norm));
	}

	/**
	 * @param game
	 * @return
	 */
	public NormativeGame getNormativeGame(Game game) {
		return this.nGamesIndex.get(game.getId());
	}

	/**
	 * Sets the state of a norm to the given state in the normative
	 * network, and updates the omega function to update
	 * the normative system
	 * 
	 * @param norm the norm to activate
	 * @see OmegaFunction
	 */
	public void setState(NormativeSystem ns, NetworkNodeState state) {
		super.setState(ns, state);
	}

	/**
	 * Returns the {@code List} of all the norms in the network
	 * 
	 * @return the {@code List} of all the norms in the network
	 */
	public List<Game> getGames() {
		return new ArrayList<Game>(this.gamesIndex.values());
	}

	/**
	 * @param game
	 * @return
	 */
	public List<NormativeGame> getNormativeGames() {
		List<NormativeGame> nGames = new ArrayList<NormativeGame>();
		for(Game game : this.getGames()) {
			nGames.add(this.getNormativeGame(game));
		}
		return nGames;
	}

	/**
	 * Returns a list of the normative games with enough information
	 * to assess their utility and replicate their norms 
	 * 
	 * @param game
	 * @return
	 */
	public List<NormativeGame> getValidNormativeGames() {
		List<NormativeGame> nGames = new ArrayList<NormativeGame>();
		for(Game game : this.getGames()) {
			if(game.isValid()) {
				nGames.add(this.getNormativeGame(game));
			}
		}
		return nGames;
	}

	/**
	 * 
	 * @return
	 */
	public List<Norm> getNorms() {
		return new ArrayList<Norm>(this.normsIndex.values());
	}

	/**
	 * Returns the {@code List} of all the norms in the network
	 * 
	 * @return the {@code List} of all the norms in the network
	 */
	public List<NormativeSystem> getNormativeSystems() {
		return new ArrayList<NormativeSystem>(this.nsIndex.values());
	}

	/**
	 * 
	 * @return
	 */
	public List<NormativeSystem> getActiveNormativeSystems() {
		List<NormativeSystem> activeNSs = new ArrayList<NormativeSystem>();
		List<NormativeSystem> nss = this.getNormativeSystems();

		for(NormativeSystem ns : nss) {
			if(this.getState(ns) == NetworkNodeState.Active) {
				activeNSs.add(ns);
			}
		}
		return activeNSs;
	}

	/**
	 * 
	 * @return
	 */
	public List<Norm> getActiveNorms() {
		List<Norm> activeNorms = new ArrayList<Norm>();
		List<Norm> norms = this.getNorms();

		for(Norm ns : norms) {
			if(this.getState(ns) == NetworkNodeState.Active) {
				activeNorms.add(ns);
			}
		}
		return activeNorms;
	}

	/**
	 * 
	 * @return
	 */
	public List<NormativeSystem> getFollowedNormativeSystems() {
		List<NormativeSystem> followedNSs = new ArrayList<NormativeSystem>();
		List<NormativeSystem> nss = this.getNormativeSystems();

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
	public Game getGameWithId(long id) {
		return this.gamesIndex.get(id);
	}


	/**
	 * @param desc
	 * @return
	 */
	public Game getGameWithDesc(String desc) {
		return this.gamesIndexedByDesc.get(desc);
	}
	
	/**
	 * Returns the norm with the given {@code id}
	 * 
	 * @param id the id of the norm
	 * @return the norm with the given id
	 */
	public Norm getNormWithId(long id) {
		return this.normsIndex.get(id);
	}

	//	/**
	//	 * Returns the norm with the given {@code id}
	//	 * 
	//	 * @param id the id of the norm
	//	 * @return the norm with the given id
	//	 */
	//	public NormativeSystem getNSWithId(long id) {
	//		return this.nsIndex.get(id);
	//	}

	/**
	 * 
	 * @param np
	 * @return
	 */
	public double getFrequency(NormCombination np) {
		return this.npFrequencies.get(np);
	}

	/**
	 * 
	 * @param np
	 * @return
	 */
	public double getFrequency(Norm norm) {
		return this.normFrequencies.get(norm);
	}

	/**
	 * 
	 * @param norm
	 * @param freq
	 */
	public void setFrequency(Norm norm, double freq) {
		this.normFrequencies.put(norm, freq);
	}

	/**
	 * 
	 * @param np
	 * @param npFreq
	 */
	public void setFrequency(NormCombination np, double npFreq) {
		this.npFrequencies.put(np, npFreq);
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
	 * Returns <tt>true</tt> if the normative network contains the norm
	 * 
	 * @param norm the norm to search for
	 * @return <tt>true</tt> if the normative network contains the norm
	 */
	public boolean contains(Game game)	{
		for(Game oGame : this.gamesIndex.values()) {
			if(game.equals(oGame)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns <tt>true</tt> if the normative network contains the norm
	 * 
	 * @param norm the norm to search for
	 * @return <tt>true</tt> if the normative network contains the norm
	 */
	public boolean contains(NormativeSystem ns)	{
		String desc = ns.getDescription();
		return this.nsIndex.containsKey(desc);

		//		for(NormativeSystem oNs : this.nsIndex.values()) {
		//			if(ns.equals(oNs)) {
		//				return true;
		//			}
		//		}
		//		return false;
	}


	/**
	 * Returns <tt>true</tt> if the normative network contains the norm
	 * 
	 * @param norm the norm to search for
	 * @return <tt>true</tt> if the normative network contains the norm
	 */
	public boolean contains(Norm norm)	{
		for(Norm oNorm: this.normsIndex.values()) {
			if(norm.equals(oNorm)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * 
	 */
	public NormativeSystem getNormativeSystem(NormativeSystem ns)	{
		String desc = ns.getDescription();
		if(this.nsIndex.containsKey(desc)) {
			return this.nsIndex.get(desc);
		}
		return ns;

		//	for(NormativeSystem oNS: this.nsIndex.values()) {
		//	if(ns.equals(oNS)) {
		//		return oNS;
		//	}
		//}

	}

	/**
	 * 
	 */
	public void clearNormativeSystems() {
		for(NormativeSystem ns : this.getNormativeSystems()) {
			this.setState(ns, NetworkNodeState.Inactive);
			ns.setFrequency(BigDecimal.ZERO);
			ns.resetNumFollowers();
		}

		for(Norm norm : this.getNorms()) {
			this.setState(norm, NetworkNodeState.Inactive);
		}
	}

	/**
	 * @param norm
	 * @return
	 */
	public Norm getNorm(Norm norm) {
		for(Norm oNorm: this.normsIndex.values()) {
			if(norm.equals(oNorm)) {
				return oNorm;
			}
		}
		return norm;
	}

	/**
	 * @return
	 */
	public List<NormCombination> getNormativeProfiles() {
		return new ArrayList<NormCombination> (this.npFrequencies.keySet());
	}

	/**
	 * @param np
	 * @return
	 */
	public boolean contains(NormCombination np) {
		for(NormCombination enp : this.getNormativeProfiles()) {
			if(enp.equals(np)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param np
	 * @return
	 */
	public NormCombination retrieveNormativeProfile(NormCombination np) {
		for(NormCombination enp : this.getNormativeProfiles()) {
			if(enp.equals(np)) {
				return enp;
			}
		}
		return null;
	}

	public int getNumMatches(Norm norm) {
		int matches = 0;
		for(Norm oNorm: this.normsIndex.values()) {
			if(norm.equals(oNorm)) {
				matches++;
			}
		}
		return matches;
	}

}
