package uk.ac.ox.cs.ensm.ns.evaluation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.game.Game;

/**
 * Class containing information about the norm groups that each agent has 
 * complied/infringed, and the agent contexts in which those norms
 * are applicable. Furthermore, the class has information about the number
 * of conflicts that arose after agents complied/infringed the norms
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public class GamesOutcomes {

	private List<Game> gamesPlayed;  
	private Map<Game, Map<AgentAction,Integer>> timesPlayedForNS;
	private Map<Game, Map<AgentAction,Integer>> numConflictsForNS;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 */
	public GamesOutcomes() {
		this.timesPlayedForNS = new HashMap<Game, Map<AgentAction,Integer>>();
		this.numConflictsForNS = new HashMap<Game, Map<AgentAction,Integer>>();
	}
	
	/**
	 * Adds new outcomes for a pair of actions in the game (in terms of 
	 * whether they lead to conflicts to the agents that have 
	 * performed these actions) 
	 * 
	 * @param game
	 * @param agent
	 * @param conflict
	 */
	public void addNewOutcome(Game game, AgentAction agAAction, boolean agAConflict, 
			AgentAction agBAction, boolean agBConflict) {
		
		/* Initialise data structures if the game has not 
		 * been played during the current tick */
		if(!this.timesPlayedForNS.containsKey(game)) {
			this.timesPlayedForNS.put(game, new HashMap<AgentAction,Integer>());
			this.numConflictsForNS.put(game, new HashMap<AgentAction,Integer>());
			
			/* Initialise game to zero times played and zero conflicts */
			this.timesPlayedForNS.get(game).put(agAAction, 0);
			this.timesPlayedForNS.get(game).put(agBAction, 0);
			this.numConflictsForNS.get(game).put(agAAction, 0);
			this.numConflictsForNS.get(game).put(agBAction, 0);
		}
		
		/* Increase number of times that each NS has been used by some agent 
		 * to interact in the game, and the number of conflicts resulting 
		 * from the interaction of these agents */
		this.timesPlayedForNS.get(game).put(agAAction, 
				this.timesPlayedForNS.get(game).get(agAAction)+1);
		
		this.timesPlayedForNS.get(game).put(agBAction, 
				this.timesPlayedForNS.get(game).get(agBAction)+1);
		
		this.numConflictsForNS.get(game).put(agAAction, 
				this.numConflictsForNS.get(game).get(agAAction) + (agAConflict ? 1 : 0));
		
		this.numConflictsForNS.get(game).put(agBAction, 
				this.numConflictsForNS.get(game).get(agBAction) + (agBConflict ? 1 : 0));
	}
	
	/**
	 * Clears games outcomes to let it ready for the next tick 
	 */
	public void clear() {
		this.gamesPlayed.clear();
		this.timesPlayedForNS.clear();
		this.numConflictsForNS.clear();
	}
}
