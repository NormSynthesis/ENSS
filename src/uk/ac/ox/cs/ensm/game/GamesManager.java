/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.agent.AgentContext;
import uk.ac.ox.cs.ensm.agent.AgentInteraction;
import uk.ac.ox.cs.ensm.agent.Combination;
import uk.ac.ox.cs.ensm.agent.EvolutionaryAgent;
import uk.ac.ox.cs.ensm.agent.language.NormSynthesisGrammar;
import uk.ac.ox.cs.ensm.config.DomainFunctions;
import uk.ac.ox.cs.ensm.config.EvolutionaryNSMSettings;
import uk.ac.ox.cs.ensm.config.Goal;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.norm.Norm;
import uk.ac.ox.cs.ensm.norm.generation.NormsGenerator;
import uk.ac.ox.cs.ensm.perception.Conflict;
import uk.ac.ox.cs.ensm.perception.View;
import uk.ac.ox.cs.ensm.perception.ViewTransition;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class GamesManager {

	/* Number of games played during the current tick */
	private List<Game> gamesPlayedThisTick;

	/* Normative games network */
	private NormativeGamesNetwork ngNetwork;

	/* Norms generator */
	private NormsGenerator normsGenerator;

	/* Agent population*/
	private DomainFunctions dmFunctions;

	/* Actions available to the agents */
	private List<AgentAction> actions;

	/* Settings of the Evolutionary NSM */
	private EvolutionaryNSMSettings ensmSettings;

	/* Overall number of games played so far */
	private double numGamesPlayed;

	/* Conflict ratio window size */
	private int rwWindowSize;

	/* Agent population */
	private Map<Long, EvolutionaryAgent> agentPopulation;

	/* Evoutionary norm synthesis machine */
	private EvolutionaryNSM ensm;
	
	/**
	 * Constructor 
	 * 
	 * @param agentPopulation
	 */
	public GamesManager(EvolutionaryNSM ensm) {

		this.ensm = ensm;
		this.gamesPlayedThisTick = new ArrayList<Game>();
		this.numGamesPlayed = 0.0;
		
		this.agentPopulation = ensm.getAgentPopulation();
		this.ensmSettings = ensm.getSettings();
		this.dmFunctions = ensm.getDomainFunctions();
		this.ngNetwork = ensm.getNormativeGamesNetwork();
		
		this.normsGenerator = new NormsGenerator(ensmSettings, 
				dmFunctions, ngNetwork, ensm.getGrammar());

		this.rwWindowSize = ensmSettings.getRewardWindowSize();

		/* Sort action spaces */
		this.retrieveActionSpaces(ensm.getGrammar());
	}

	/**
	 * Detects new coordination games that agents can play and 
	 * updates the conflict rates of each game played by the agents 
	 * during the current time step  
	 * 
	 * @param vTranss a list of views of the scenario
	 */
	public List<NormativeGame> step(List<ViewTransition> vTranss) {
		List<NormativeGame> newNormativeGames = new ArrayList<NormativeGame>();

		/* Clear list of games played during this tick */
		this.gamesPlayedThisTick.clear();

		/* Add new games, create their corresponding normative games,
		 * and evaluate the outcomes of each game played in the last tick */
		for(ViewTransition vTrans : vTranss) {
			newNormativeGames.addAll(this.detectNewGames(vTrans));
			this.updatePlayedGames(vTrans);
		}

		/* Return a list of the new normative games tracked during this step */
		return newNormativeGames;
	}

	/**
	 * Performs conflict-based detection of new games that agents can play. 
	 * This approach will work only in scenarios in which conflicting agents 
	 * are responsible for the conflict. This should change if we consider a 
	 * scenario in which the agents responsible for a conflict may avoid being  
	 * involved in a conflict after performing an action combination 
	 * 
	 * @param vTrans a perception (view) of the scenario
	 */
	private List<NormativeGame> detectNewGames(ViewTransition vTrans) {
		List<NormativeGame> newNormativeGames = new ArrayList<NormativeGame>();

		Goal g = ensmSettings.getSystemGoals().get(0); // Single goal by now 
		View pView = vTrans.getView(-1);

		Set<Game> games = new HashSet<Game>();
		
		/* Detect conflicts and create their games */
		List<Conflict> conflicts = this.dmFunctions.getConflicts(g, vTrans);
		
		/* Create a new game for each detected conflict */
		for(Conflict conflict : conflicts) {

			/* Retrieve action combination perform by the agents 
			 * (and their contexts). Then, update the conflict rate
			 * of that action combination to 1 */
			List<AgentContext> contexts = new ArrayList<AgentContext>();
			Combination<AgentAction> ac = this.getActionCombination(
					vTrans, conflict, contexts);

			List<Combination<AgentAction>> combinations =
					this.generateActionCombinations(contexts.size());

			/* Create coordination game */
			Game game = new Game(pView, contexts, combinations, rwWindowSize);

			/* If the game is new, do nothing */
			if(!this.gameExists(game)) {

				/* Add conflict rate for each role */
				for(int role=0; role<game.getNumRoles(); role++) {
					game.addReward(ac, role, 0.0);
				}

				/* If the game does not exist yet, add it to the NGN, create its
				 * corresponding normative game, and add the norms that 
				 * can regulate the game */
				this.ngNetwork.add(game);
				games.add(game);

				/* Create its corresponding normative game */
				NormativeGame nGame = this.createNormativeGame(game);
				newNormativeGames.add(nGame);
			}
		}
		return newNormativeGames;
	}

	/**
	 * Creates all the possible action combinations of the game 
	 * 
	 * @param the number of roles of the game
	 * @return a list of all the possible action combinations of the game
	 */
	private List<Combination<AgentAction>> generateActionCombinations(
			int numRoles) {

		List<Combination<AgentAction>> combinations = 
				new ArrayList<Combination<AgentAction>>(); 

		/* Generate action combinations for role 0 */
		for(AgentAction action : this.actions) {
			Combination<AgentAction> ac = new Combination<AgentAction>(action);
			combinations.add(ac);
		}

		/* Create action combinations for the remaining roles (up to N) */
		for(int numRole=1; numRole<numRoles; numRole++) {
			List<Combination<AgentAction>> newCombinations = 
					new ArrayList<Combination<AgentAction>>();

			for(Combination<AgentAction> combination : combinations) {
				for(AgentAction action : this.actions) {

					/* Create a new combination in which an action 
					 * for a new role has been added */
					Combination<AgentAction> newCombination = 
							new Combination<AgentAction>(combination);

					newCombination.add(action);
					newCombinations.add(newCombination);
				}				
			}

			/* Replace the original action combination to update it */
			combinations = newCombinations;
		}
		return combinations;
	}

	/**
	 * Checks pairwise combinations of agents in the view, checking 
	 * if they have a joint context. In that case, it adds a new
	 * interaction between the normative systems they abide by,
	 * and updates their respective conflict ratio 
	 * 
	 * @param vTrans
	 * @param agentsWithAppNorms
	 */
	private void updatePlayedGames(ViewTransition vTrans) {
		List<EvolutionaryAgent> checkedAgents = new ArrayList<EvolutionaryAgent>();

		/* Retrieve potential interactions */
		List<AgentInteraction> potentialInteractions = 
				this.getPotentialInteractions(vTrans);

		/* For each potential interaction, check whether there is a game that 
		 * describes the interaction between its agents. If so, update the 
		 * payoff matrix of the game */
		for(AgentInteraction pInteraction : potentialInteractions) {
			List<EvolutionaryAgent> iAgents = pInteraction.getAgents();

			/* Check that none of the agents has been already assigned a game */
			boolean assigned = false;
			for(EvolutionaryAgent agent : iAgents) {
				if(checkedAgents.contains(agent)) {
					assigned = true;
				}
			}

			/* If some agent is already playing a different game, we cannot
			 * assign it another game (agents only play one game at a time) */
			if(assigned == true) {
				continue;
			}

			/* Retrieve the game played by the agents */
			Game game = this.retrieveGame(pInteraction.getContexts());

			/* Continue if the game does exist (has been previously tracked) */
			if(game == null) {
				continue;
			}

			/* Increase the number of times that any game has been played */
			this.numGamesPlayed++;
			
			/* Set the agents as already playing a game */
			checkedAgents.addAll(iAgents);

			/* Update the conflict ratio of the coordination game based on 
			 * the information tracked in the agent interaction */
			this.updatePayoff(game, pInteraction, vTrans);

			/* Add the game to the list of games played during the current tick 
			 * (necessary for normative systems evaluation purposes) */
			if(!this.gamesPlayedThisTick.contains(game)) {
				this.gamesPlayedThisTick.add(game);
			}

			/* Update game metrics: increase the number of times 
			 * the game has been played */
			game.incTimesPlayed();
		}
		
		/* Update the frequencies of each tracked game */
		for(Game game : this.ngNetwork.getGames()) {
			double gFreq = game.getNumTimesPlayed();
			gFreq /= numGamesPlayed;
			game.setFrequency(gFreq);
		}
	}

	/**
	 * 
	 */
	private List<AgentInteraction> getPotentialInteractions(ViewTransition vTrans) {
		List<AgentInteraction> pInteractions = new ArrayList<AgentInteraction>();

		/* Get view of the system at time t-1, which actually is the one in 
		 * which we need to check for agent interactions */
		View pView = vTrans.getView(-1);

		/* 1. First, retrieve a list with the agents to check */
		List<EvolutionaryAgent> agents = this.getAgentsToCheck(vTrans);

		/* 2. Check pairwise interactions */
		for(int i=0; i < agents.size(); i++) {
			for(int j=i+1; j < agents.size(); j++) {

				/* Get the pair of agents */
				EvolutionaryAgent ag1 = agents.get(i);
				EvolutionaryAgent ag2 = agents.get(j);
				AgentContext ag1Context = dmFunctions.getContext(ag1.getId(), pView);
				AgentContext ag2Context = dmFunctions.getContext(ag2.getId(), pView);

				List<EvolutionaryAgent> iAgents = new ArrayList<EvolutionaryAgent>();
				List<AgentContext> iContexts = new ArrayList<AgentContext>();

				if(ag1Context != null) {
					iAgents.add(ag1);
					iContexts.add(ag1Context);
				}

				if(ag2Context != null) {
					iAgents.add(ag2);
					iContexts.add(ag2Context);
				}

				if(!iAgents.isEmpty()) {
					AgentInteraction interaction = new AgentInteraction(iAgents, iContexts);
					pInteractions.add(interaction);	
				}
			}
		}

		/* 3. Check single interactions */
		for(int i=0; i < agents.size(); i++) {
			EvolutionaryAgent ag = agents.get(i);
			AgentContext agContext = dmFunctions.getContext(ag.getId(), pView);

			if(agContext != null) {
				AgentInteraction interaction = new AgentInteraction(ag, agContext);
				pInteractions.add(interaction);	
			}
		}
		return pInteractions;
	}	


	/**
	 * Updates the conflict rate of a coordination game based on the 
	 * information provided by the interaction between a group of agents 
	 * that have just played the game
	 * 
	 * @param game
	 * @param interaction
	 * @param vTrans
	 */
	private void updatePayoff(Game game, AgentInteraction interaction, 
			ViewTransition vTrans) {

		Goal g = ensmSettings.getSystemGoals().get(0); // Single goal by now 

		/* Assign a correspondence between the agents playing the game 
		 * and the roles they enact */
		List<EvolutionaryAgent> agentsByRole = this.
				sortAgentsByRole(game, interaction);

		/* Retrieve the action combination performed by the 
		 * agents playing the game */
		List<AgentAction> pActions = new ArrayList<AgentAction>();
		for(EvolutionaryAgent agent : agentsByRole) {
			AgentAction action = this.dmFunctions.getAction( 
					agent.getId(), vTrans).get(0);

			pActions.add(action);
		}
		Combination<AgentAction> ac =	new Combination<AgentAction>(pActions);

		/* Compute and set the payoff */
		for(int role=0; role<agentsByRole.size(); role++) {

			/* Retrieve the agent and her id */
			EvolutionaryAgent agent = agentsByRole.get(role);
			long agId = agent.getId();

			/* Get the reward of the agent */
			double reward = this.dmFunctions.getReward(vTrans, agId, g);

			/* Retrieve the necessary parameters and compute the new payoff */
			double explorationRate = this.ensmSettings.getExplorationRate();
			double exploitationRate = 1 - explorationRate;
			double oldPayoff = game.getPayoff(ac, role);

			double newPayoff = explorationRate * reward + 
					exploitationRate * oldPayoff;

			/* Set the new payoff for the given role */
			game.setPayoff(ac, role, newPayoff);
			game.addReward(ac, role, reward);
			
			/* Add game reward to compute metrics */ 
			this.ensm.getNormSynthesisMetrics().addGameReward(game, reward);
		}
	}

	/**
	 * Returns the action combination performed by a group of agents
	 * involved in a conflict  
	 * 
	 * @param vTrans
	 * @param conflict
	 * @param contexts
	 * @return
	 */
	private Combination<AgentAction> getActionCombination(ViewTransition vTrans, 
			Conflict conflict, List<AgentContext> contexts) {

		View pView = vTrans.getView(-1);
		List<Long> agIds = conflict.getConflictingAgents();
		List<AgentAction> actions = new ArrayList<AgentAction>();

		/* Only if there is at least one conflicting agent */
		if(!agIds.isEmpty()) {

			/* Retrieve agent contexts and create the game */
			for(long agId : agIds) {
				AgentContext context = this.dmFunctions.getContext(agId, pView);
				AgentAction action = this.dmFunctions.getAction(agId, vTrans).get(0);
				contexts.add(context);
				actions.add(action);
			}
		}
		return new Combination<AgentAction>(actions);
	}

	/**
	 * Creates the normative game of a game 
	 * 
	 * @param game the game for which to create the normative game
	 */
	private NormativeGame createNormativeGame(Game game) {

		/* Create norms to regulate the behaviours 
		 * of the players of the game */
		List<Combination<Norm>> normCombinations =
				this.normsGenerator.generateNormCombinations(game);

		/* Create normative game along with its normative profiles */
		NormativeGame nGame = new NormativeGame(game, normCombinations);

		/* Set the initial frequency of each norm of the game */
		double numNorms = nGame.getNormSpace().size();
		double nFreq = 1 / numNorms;
		double gogoFreq = nFreq;

		int i=0;
		for(Norm norm : nGame.getNormSpace()) {
			if(i==0) {
				nGame.setFrequency(norm, gogoFreq);
				nGame.setFitness(norm, 0.0);
			}
			else {
				nGame.setFrequency(norm, nFreq);
				nGame.setFitness(norm, 0.0);	
			}
			i++;
		}

		/* Add the game to the list of games and update the frequencies 
		 * of each norm in the normative games network */
		this.ngNetwork.add(nGame);
		this.ngNetwork.updateNormsFrequencies(nGame);

		return nGame;
	}

	/**
	 * Assigns each role of a game to an agent playing the game
	 * This is the equivalent to our (pi) function in the paper
	 *  
	 * @param game an m-role game
	 * @param interaction an interaction between m agents
	 */
	private List<EvolutionaryAgent> sortAgentsByRole(Game game,
			AgentInteraction interaction) {

		List<EvolutionaryAgent> roles = new ArrayList<EvolutionaryAgent>();
		List<AgentContext> plContexts = game.getAgentContexts();
		List<EvolutionaryAgent> iAgents = interaction.getAgents();
		List<AgentContext> iContexts = interaction.getContexts();

		for(int i=0; i<interaction.getNumRoles(); i++) {
			AgentContext plCtxt = plContexts.get(i); // Search player 

			for(int j=0; j<interaction.getNumRoles(); j++) {
				EvolutionaryAgent agent = iAgents.get(j);
				AgentContext agCtxt = iContexts.get(j);

				if(agCtxt.equals(plCtxt)) {
					roles.add(agent);
					break;
				}
			}
		}
		return roles;
	}

	/**
	 * @param pView
	 * @param interaction
	 * @param actions
	 * @return
	 */
	private Game retrieveGame(List<AgentContext> contexts) {

		Collections.sort(contexts);
		String desc = Game.generateDescription(contexts);

		/* Check if the game already exists */
		
		return this.ngNetwork.getGameWithDesc(desc);
		

		//		View gameDesc;
		//
		//		/* Assess whether the interaction is between one or two agents */
		//		if(contexts.size() == 1) {
		//			EvolutionaryAgent ag1 = interaction.getAgents().get(0);
		//			gameDesc = this.dmFunctions.getGameDescription(pView, ag1, ag1);
		//		}
		//		else {
		//			EvolutionaryAgent ag1 = interaction.getAgents().get(0);
		//			EvolutionaryAgent ag2 = interaction.getAgents().get(1);
		//			gameDesc = this.dmFunctions.getGameDescription(pView, ag1, ag2);
		//		}
		//
		//		/* Create coordination game */
		//		Game game = new Game(gameDesc,
		//				interaction.getContexts(), rwWindowSize);
		//
		//		/* Check if the game already exists */
		//		for(Game oGame : this.ngNetwork.getGames()) {
		//			if(game.equals(oGame)) {
		//				return oGame;
		//			}
		//		}
		//		return null;
	}

	/**
	 * @return
	 */
	private boolean gameExists(Game game) {
		String desc = game.getDescription();
		Game fGame = this.ngNetwork.getGameWithDesc(desc);
		return  fGame != null;
	}

	/**
	 * 
	 */
	private void retrieveActionSpaces(NormSynthesisGrammar grammar) {
		this.actions = new ArrayList<AgentAction>(grammar.getActions());

		/* Sort actions */
		Comparator<AgentAction> comparator = new Comparator<AgentAction>() {
			@Override
			public int compare(AgentAction ac1, AgentAction ac2) {
				return ac1.toString().compareTo(ac2.toString()); 
			}
		};
		Collections.sort(actions, comparator);
	}


	/**
	 * @param vTrans
	 */
	private List<EvolutionaryAgent> getAgentsToCheck(ViewTransition vTrans) {

		/* Retrieve the id's of the agents in the view transition */
		Set<Long> agentIds = this.getAgentsInView(vTrans);

		/* Generate a set of agents that existed in previous state */
		Set<EvolutionaryAgent> agents = new HashSet<EvolutionaryAgent>();
		for(long agId : agentIds) {
			EvolutionaryAgent agent = this.agentPopulation.get(agId);
			agents.add(agent);	
		}

		/* Return set as a list for access purposes */
		return new ArrayList<EvolutionaryAgent>(agents);
	}

	/**
	 * 
	 * @param vTrans
	 * @return
	 */
	private Set<Long> getAgentsInView(ViewTransition vTrans) {
		Set<Long> agentIds = new HashSet<Long>();

		View pView = vTrans.getView(-1);
		View view = vTrans.getView(0);

		/* Just check norm applicability for those agents that
		 * exist in all views of the stream */
		List<Long> pViewAgentIds = pView.getAgentIds();
		List<Long> viewAgentIds = view.getAgentIds();
		for(Long agentId : pViewAgentIds)	{
			if(viewAgentIds.contains(agentId)) {
				agentIds.add(agentId);	
			}
		}
		return agentIds;
	}
}
