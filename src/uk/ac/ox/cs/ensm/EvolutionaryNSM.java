package uk.ac.ox.cs.ensm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

import uk.ac.ox.cs.ensm.agent.EvolutionaryAgent;
import uk.ac.ox.cs.ensm.agent.language.NormSynthesisGrammar;
import uk.ac.ox.cs.ensm.agent.language.PredicatesDomains;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.game.GamesManager;
import uk.ac.ox.cs.ensm.metrics.DefaultEvolutionaryNSMetrics;
import uk.ac.ox.cs.ensm.metrics.EvolutionaryNSMetrics;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.norm.reasoning.NSReasoner;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;
import uk.ac.ox.cs.ensm.ns.generation.NSGenerator;
import uk.ac.ox.cs.ensm.ns.replication.NormsReplicator;
import uk.ac.ox.cs.ensm.perception.Monitor;
import uk.ac.ox.cs.ensm.perception.Sensor;
import uk.ac.ox.cs.ensm.perception.ViewTransition;

/**
 * The Norm Synthesis Machine (NSM), containing:
 * <ol>
 * <li>	The norm evaluation criteria (effectiveness and necessity).
 * 			During the norm evaluation phase, norms are evaluated in
 * 			terms of their: (i) effectiveness, based on the outcome of
 * 			their compliances, and (ii) necessity, based on the outcome
 * 			of their infringements;
 * <li>	The configuration <tt>settings</tt> of the norm synthesis machine; 
 * <li>	The monitor, containing sensors to perceive the scenario; 
 * <li>	The normative network, whose nodes stand for norms and whose edges
 * 			stand for relationships between norms; 
 * <li>	The omega function, that computes the normative system from the 
 * 			normative network; 
 * <li>	The norm synthesis <tt>strategy</tt>, which contains a method 
 * 			{@code execute()} that performs the norm synthesis cycle; 
 * <li>	The domain functions <tt>dmFunctions</tt> that allow to perform
 * 			norm synthesis for a specific domain. 
 * <li>	The NSM metrics, that contains information about the metrics of
 * 			different elements in the norm	synthesis process.
 * </ol>
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public class EvolutionaryNSM {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	/* ENSM Settings */
	private Random random;												// Randomiser
	private EvolutionaryNSMSettings settings;			// Norm synthesis settings

	/* ABM perception */
	private Monitor monitor;											// Monitor to perceive the scenario
	private List<ViewTransition> vTransitions;		// View transitions

	/* Information model */
	private Map<Long,EvolutionaryAgent>	agentPopulation;
	private NormativeGamesNetwork ngNetwork;			// Network of normative games
	private PredicatesDomains predDomains;				// Predicates and their domains
	private NormSynthesisGrammar grammar;					// A grammar for norm synthesis

	/* Game detection and outcomes update */
	private GamesManager gamesManager;

	/* Tools to generate, evaluate and replicate normative systems */
	private NSGenerator nsGenerator;
	private NormsReplicator normsReplicator;

	/* Norm reasoning */
	private Map<NormativeSystem, NSReasoner> nsReasoners;

	/* Domain layer */
	private DomainFunctions dmFunctions;

	/* Metrics and visualisation */
	private EvolutionaryNSMetrics metrics;					

	/* Auxiliary variables */
	private boolean useGui;												
	private boolean firstExecution = true;
	private long numAgents;
	private double numExecs;
	private double lastReplication;
	private double numTicksPerRound;


	//---------------------------------------------------------------------------
	// Constructors 
	//---------------------------------------------------------------------------

	/**
	 * The Norm Synthesis Machine constructor.
	 * 
	 * @param 	settings basic settings of the norm synthesis machine
	 * @param 	predDomains the predicates and terms to specify norms
	 * 					for the given domain
	 * @param 	dmFunctions the domain functions, that allow to perform 
	 * 					norm synthesis for a specific domain
	 * @param 	useGui indicates if the user requires a GUI or not
	 * @see 		PredicatesDomains
	 * @see			DomainFunctions
	 */
	public EvolutionaryNSM(EvolutionaryNSMSettings settings, 
			Map<Long,EvolutionaryAgent>	agentPopulation, 
			PredicatesDomains predDomains, DomainFunctions dmFunctions, 
			NormSynthesisGrammar grammar, Random random, boolean useGui, 
			List<NormativeSystem> defNSs) {

		this.settings = settings;
		this.predDomains = predDomains;
		this.dmFunctions = dmFunctions;
		this.useGui = useGui;
		this.random = random;
		this.grammar = grammar;
		this.agentPopulation = agentPopulation;

		this.numAgents = agentPopulation.size();
		this.numTicksPerRound = settings.getNumTicksPerSimulationRound();
		this.lastReplication = 0;
		this.numExecs = 0;

		/* Create MAS sensing tools */
		this.monitor = new Monitor();
		this.vTransitions = new ArrayList<ViewTransition>();

		/* Create data structures */
		this.ngNetwork = new NormativeGamesNetwork(this);

		/* Create NS generator, normative systems and their reasoners */
		this.nsReasoners = this.createNSGenerator();

		/* Create default metrics */
		this.metrics = new DefaultEvolutionaryNSMetrics(this);

		/* Create games manager and norms replicator */
		this.gamesManager = new GamesManager(this);

		this.normsReplicator = new NormsReplicator(this.nsReasoners, 
				this.dmFunctions, this.ngNetwork, settings);

		//		this.stabilityTest = true;
	}

	//---------------------------------------------------------------------------
	// Public methods 
	//---------------------------------------------------------------------------

	/**
	 * Adds a sensor to the monitor of the norm synthesis machine
	 * 
	 * @param sensor the sensor to add to monitor
	 * @see Monitor
	 * @see Sensor
	 */
	public void addSensor(Sensor sensor) {
		this.monitor.addSensor(sensor);
	}

	/**
	 * Performs the norm synthesis cycle by executing
	 * the norm synthesis strategy
	 * 
	 * @return the {@code NormativeSystem} resulting from 
	 * 					the norm synthesis cycle. The normative system
	 * 					is computed by the omega function, from the
	 * 					normative network
	 * @see EvolutionaryNormSynthesisStrategy
	 * @see NormativeSystem
	 */
	public void executeRound(double timeStep) 
			throws IncorrectSetupException {

		boolean converged = this.metrics.hasConverged();
		
		/* First, check that the NSM has been correctly setup */
		if(this.firstExecution) {
			this.firstExecution = false;
			this.checkSetup();
		}

		/* Increase num executions of the ENSM */
		this.numExecs++;

		/* Collect perceptions from the MAS */
		this.monitor.getPerceptions(vTransitions);

		/* Detect games and compute their payoffs */
		this.gamesManager.step(vTransitions);

		/* Replicate norms and generate a new population of normative systems */
		if(!converged && numExecs > lastReplication + numTicksPerRound) {
			lastReplication = this.numExecs; 

			/* Replicator dynamics */ 
			this.normsReplicator.doReplication();

			/* Create new list of normative systems, one for each agent */
			List<NormativeSystem> nss = this.nsGenerator.
					generateNormativeSystems(numAgents);

			this.setAgentsWithNormativeSystems(nss);

			/* Update metrics */
			this.metrics.update(timeStep);
		}
	}

	/**
	 * @param nss
	 */
	private void setAgentsWithNormativeSystems(List<NormativeSystem> nss) {
		Collection<EvolutionaryAgent> colAgs = agentPopulation.values();
		List<EvolutionaryAgent> agents = new ArrayList<EvolutionaryAgent>(colAgs);

		for(int i=0; i<agents.size(); i++) {
			agents.get(i).setNormativeSystem(nss.get(i));
		}
	}

	//---------------------------------------------------------------------------
	// Private methods
	//---------------------------------------------------------------------------


	/**
	 * Creates the NS generator, generates a set of initial random 
	 * normative systems and creates their corresponding reasoners
	 * 
	 *  @return map of reasoners 
	 */
	private Map<NormativeSystem, NSReasoner> createNSGenerator() {
		this.nsGenerator = new NSGenerator(random, ngNetwork,
				grammar, predDomains, dmFunctions, settings);

		/* Generate random normative systems */
		List<NormativeSystem> nss = this.nsGenerator.
				generateEmptyNormativeSystems(this.numAgents);

		/* Set agents with normative systems */
		this.setAgentsWithNormativeSystems(nss);

		return this.nsGenerator.getNSReasoners();
	}

	/**
	 * Checks the initial setup of the norm synthesis machine, ensuring
	 * that the user has added {@code sensors} to perceive the scenario,
	 * an {@code omega function} to compute the normative system from the
	 * normative network, and a norm synthesis {@code strategy}
	 * 
	 * @throws IncorrectSetupException if one of the following conditions hold:
	 * 					(1) no sensors have been added to the monitor; or
	 * 					(2) no omega function has been set; or
	 * 					(3) no strategy has been set
	 * @see IncorrectSetupException
	 */
	private void checkSetup() throws IncorrectSetupException {
		if(this.monitor.getNumSensors() == 0) {
			throw new IncorrectSetupException("No sensors have been added yet");
		}
	}

	//---------------------------------------------------------------------------
	// Getters
	//---------------------------------------------------------------------------

	/**
	 * 
	 * @param metrics
	 */
	public void setMetrics(EvolutionaryNSMetrics metrics) {
		this.metrics = metrics;
	}

	/**
	 * Returns the norm synthesis settings
	 * 
	 * @return the norm synthesis settings
	 * @see EvolutionaryNSMSettings
	 */
	public EvolutionaryNSMSettings getSettings() {
		return this.settings;
	}

	/**
	 * Returns an object {@code PredicatesDomains} that contains:
	 * 
	 * (1) 	all the possible predicates in the agents' contexts; and
	 * (2) 	the domain of each possible predicate, represented as
	 * 			a {@code Taxonomy}.
	 * 
	 * @return an object {@code PredicatesDomains} that contains all the
	 * 					possible predicates in the agents' contexts, and the domain
	 * 					of each possible predicate, represented as a {@code Taxonomy}.
	 * @see PredicatesDomains
	 */
	public PredicatesDomains getPredicatesDomains() {
		return this.predDomains;
	}

	/**
	 * Returns the domain functions that allow to perform
	 * norm synthesis for a specific domain
	 * 
	 * @return the domain functions
	 * @see DomainFunctions
	 */
	public DomainFunctions getDomainFunctions() {
		return this.dmFunctions;
	}

	/**
	 * Returns the norm synthesis metrics
	 * 
	 * @return the norm synthesis metrics
	 * @see EvolutionaryNSMetrics
	 */
	public EvolutionaryNSMetrics getNormSynthesisMetrics() {
		return this.metrics;
	}

	/**
	 * Returns the monitor that perceives the environment
	 * 
	 * @return the monitor that perceives the environment
	 * @see Monitor
	 */
	public Monitor getMonitor() {
		return this.monitor;
	}

	/**
	 * @return
	 */
	public NormativeGamesNetwork getNormativeGamesNetwork() {
		return this.ngNetwork;
	}

	/**
	 * 
	 * @return
	 */
	public Map<Long, EvolutionaryAgent> getAgentPopulation() {
		return this.agentPopulation;
	}

	/**
	 * @return the nsEvaluator
	 */
	public NormsReplicator getNormsEvaluator() {
		return normsReplicator;
	}

	/**
	 * 
	 * @return
	 */
	public GamesManager getGamesManager() {
		return this.gamesManager;
	}

	/**
	 * 
	 * @return
	 */
	public NormSynthesisGrammar getGrammar() {
		return this.grammar;
	}

	/**
	 * Use Graphical User Interface (norms tracer)?
	 * 
	 * @return <tt>true</tt> if the NSM must use GUI 
	 */
	public boolean isGUI() {
		return this.useGui;
	}

	/**
	 * Returns the random values generator
	 * 
	 * @return the random values generator
	 */
	public Random getRandom() {
		return this.random;
	}

	/**
	 * 
	 */
	//	private void doStabilityTestStuff() {
	//		List<NormativeGame> games = this.ngNetwork.getNormativeGames();
	//		
	//		for(int i=0; i<2; i++) { // We do it for three games
	//			int rndGame = random.nextInt(games.size());
	//			NormativeGame game = games.get(rndGame);
	//			
	//			BigDecimal freq = BigDecimal.ZERO;
	//			NormativeProfile np = null;
	//			for(NormativeProfile npr :  game.getNormativeProfiles()) {
	//				BigDecimal npFreq = game.getFrequency(npr);
	//				
	//				if(npFreq.compareTo(freq) > 0) {
	//					np = npr;
	//					freq = npFreq;
	//				}
	//			}
	//			
	//			if(np == null) {
	//			int rndNP = game.getNumPlayers() == 1?
	//					random.nextInt(2) : random.nextInt(4); 
	//				np = game.getNormativeProfiles().get(rndNP);
	//			}
	//			
	//			BigDecimal npFreq = game.getFrequency(np);
	//			BigDecimal sevenHalf = new BigDecimal(0.3);
	//			BigDecimal twoHalf = new BigDecimal(0.1);
	//			BigDecimal newNPFreq = npFreq.subtract(sevenHalf);
	//			
	//			game.setFrequency(np, newNPFreq);
	//			
	//			for(NormativeProfile npr :  game.getNormativeProfiles()) {
	//				if(!npr.equals(np)) {
	//					if(game.getNumPlayers()==1) {
	//						npFreq = game.getFrequency(npr);
	//						newNPFreq = npFreq.add(sevenHalf);
	//					}
	//					else {
	//						npFreq = game.getFrequency(npr);
	//						newNPFreq = npFreq.add(twoHalf);
	//					}
	//					
	//					game.setFrequency(npr, newNPFreq);
	//				}
	//			}
	//		}
	//				
	//		List<NormativeSystem> nss = this.nsGenerator.
	//				generateNormativeSystems(numAgents);
	//
	//		this.setAgentsWithNormativeSystems(nss);
	//		
	//	}


	//	/**
	//	 * 
	//	 */
	//	private void doStabilityTestStuff2() {
	//	
	//		// Random remove of norms
	//		List<NormativeSystem> nss = this.ngNetwork.getNormativeSystems();
	//		
	//		for(int i=0; i<10; i++) {
	//			int rndNS = random.nextInt(nss.size());
	//			NormativeSystem ns = new NormativeSystem(nss.get(rndNS));
	//
	//			this.ngNetwork.setState(ns, NetworkNodeState.Active);
	////		int rnNorm = random.nextInt(ns.size());
	////		ns.remove(rnNorm);
	////		
	////		rnNorm = random.nextInt(ns.size());
	////		ns.remove(rnNorm);
	////		
	////		rnNorm = random.nextInt(ns.size());
	////		ns.remove(rnNorm);
	////		
	////		rnNorm = random.nextInt(ns.size());
	////		ns.remove(rnNorm);
	////		
	////		rnNorm = random.nextInt(ns.size());
	////		ns.remove(rnNorm);
	////		
	////		rnNorm = random.nextInt(ns.size());
	////		ns.remove(rnNorm);
	////		
	////		if(!this.ngNetwork.contains(ns)) {
	////			this.ngNetwork.add(ns);
	////		}
	////		else {
	////			ns = this.ngNetwork.getNormativeSystem(ns);
	////		}
	//		}
	//	}
}
