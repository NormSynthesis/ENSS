/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.deprecated;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.agent.AgentAction;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class ActionsForNSMatrix 
extends HashMap<NormativeSystem,Map<AgentAction,BigDecimal[]>> {

	/* Serial ID */
	private static final long serialVersionUID = 2765107400861146891L;

	/* List of available actions */
	private List<AgentAction> actions;

	/**
	 * Constructor
	 */
	public ActionsForNSMatrix(List<AgentAction> actions) {
		this.actions = actions;
	}

	/**
	 * 
	 * @param action
	 * @param ns
	 * @param prob
	 */
	public void addActionPerformedByA(NormativeSystem ns, AgentAction action) {
		this.add(ns, action, 0);
	}

	/**
	 * 
	 * @param action
	 * @param ns
	 * @param prob
	 */
	public void addActionPerformedByB(NormativeSystem ns, AgentAction action) {
		this.add(ns, action, 1);
	}

	/**
	 * 
	 * @param action
	 * @param ns
	 * @return
	 */
	public BigDecimal getProbabilityThatAgentAPerforms(NormativeSystem ns,
			AgentAction action) {
		return this.getProbability(ns, action, 0);
	}

	/**
	 * 
	 * @param action
	 * @param ns
	 * @return
	 */
	public BigDecimal getProbabilityThatAgentBPerforms(NormativeSystem ns,
			AgentAction action) {
		
		return this.getProbability(ns, action, 1);
	}

	/**
	 * 
	 * @param action
	 * @param ns
	 * @param index
	 * @return
	 */
	private BigDecimal getProbability(NormativeSystem ns, 
			AgentAction action, int index) {

		BigDecimal matchesA = this.get(ns).get(action)[0];
		BigDecimal matchesB = this.get(ns).get(action)[1];
		BigDecimal totalMatches = matchesA.add(matchesB);

		return index == 1? 	
				matchesA.divide(totalMatches) : 
					matchesB.divide(totalMatches);
	}

	/**
	 * 
	 * @param action
	 * @param ns
	 * @param index
	 */
	private void add(NormativeSystem ns, AgentAction action, int index) {

		/* If the normative system does not exist,
		 * then create the sub-map to keep track of it */ 
		if(!this.containsKey(ns)) {
			this.put(ns, new HashMap<AgentAction, BigDecimal[]>());
			
			for(AgentAction ac : actions) {
				this.get(ns).put(ac, new BigDecimal[2]);

				/* Initialise to 0 by default */
				this.get(ns).get(ac)[0] = BigDecimal.ZERO;
				this.get(ns).get(ac)[1] = BigDecimal.ZERO;
			}
		}

		BigDecimal oldValue = this.get(ns).get(action)[index];
		this.get(ns).get(action)[index] = oldValue.add(BigDecimal.ONE);
	}
	
	/**
	 * 
	 */
	public String toString() {
		DecimalFormat df = new DecimalFormat("####0.00");
		String s = "";

		/* Get the length of the longest action name */
		int maxLength = 0;
		for(AgentAction action : actions) {
			int lenAc = action.toString().length();
			if(lenAc > maxLength) {
				maxLength = lenAc;
			}
		}

		/* Write header (with actions of agent B) */
		for(AgentAction action : actions) {
			String acName = action.toString();
			s += "\t\t\t" + acName;
		}
		s += "\n";

		/* Write conflict ratios */
		for(NormativeSystem ns : this.keySet()) {
			s += ns.getName(); 

			/* Write conflict ratios */
			for(AgentAction ac : actions) {
				BigDecimal probA = this.get(ns).get(ac)[0];
				BigDecimal probB = this.get(ns).get(ac)[1];

				s += "\t\t" + (Double.isNaN(probA.doubleValue()) ?
								"???" : df.format(probA.doubleValue())) +

						", " + (Double.isNaN(probB.doubleValue()) ? 
								"???" : df.format(probB));
			}
			s += "\n";
		}
		return s;
	}
}
