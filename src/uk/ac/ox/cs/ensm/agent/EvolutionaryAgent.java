package uk.ac.ox.cs.ensm.agent;

import java.util.List;

import uk.ac.ox.cs.ensm.game.Game;
import uk.ac.ox.cs.ensm.norm.reasoning.Constraint;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * An environment agent within the scenario
 *  
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public interface EvolutionaryAgent {

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------
	
	/**
	 * The id of the environment agent, which must be unique.
	 * That is, the NSM requires that two agents never have the
	 * same id, since the norm synthesis cycle may not work correctly
	 * 
	 * @return a {@code long} number, which is the id of the environment agent
	 */
	public long getId();
	
	/**
	 * Returns the normative system of an agent abides by
	 * 
	 * @return the normative system an agent abides by
	 */
	public NormativeSystem getNormativeSystem();
	
	/**
	 * Sets the normative system an agent will abide by
	 * 
	 *  @return the normative system an agent will abide by
	 */
	public void setNormativeSystem(NormativeSystem ns);
	
	/**
	 * Returns the constraints applicable to the agent at the current tick
	 * 
	 * @return the constraints applicable to the agent at the current tick
	 */
	public List<Constraint> getCurrentApplicableConstraints();
	
	/**
	 * Returns the game that the agent is playing at the current tick
	 * 
	 * @return the game that the agent is playing at the current tick
	 */
	public Game getCurrentPlayedGame();
}
