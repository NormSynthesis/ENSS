/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.deprecated;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1-role and 2-role asymmetric payoff matrix
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class AsymmetricPayoffMatrix<T> extends HashMap<T, Map<T, BigDecimal[]>> {

	/* Serial ID */
	private static final long serialVersionUID = 9064839189162052563L;

	/* List of available strategies (either actions or norms) for each role */
	private Map<Integer, List<T>> strategies;
	
	/**
	 * Constructor
	 */
	public AsymmetricPayoffMatrix(Map<Integer, List<T>> strSpace) {
		this.strategies = new HashMap<Integer,List<T>>();
		
		int numRoles = strSpace.keySet().size();
		
		/* Set strategy space for each possible role */
		for(int role=1; role<=numRoles; role++) {
			this.strategies.put(role, new ArrayList<T>());
			
			for(T action : strSpace.get(role)) {
				this.strategies.get(role).add(action);
			}
		}
		/* Generate empty payoff matrix */
		this.generateMatrix();
	}

	/**
	 * @param role
	 * @return
	 */
	public List<T> getStrategySpace(int role) {
		return this.strategies.get(role);
	}

	/**
	 * 
	 * @param player
	 * @param strategy
	 * @return
	 */
	public boolean roleHasStrategy(int player, T strategy) {
		return this.strategies.get(player).contains(strategy);
	}

	/**
	 * Returns the payoff of an agent A that performs action 'A' 
	 * given that agent B performs action 'B' 
	 *  
	 * @return
	 */
	public BigDecimal getPayoff(int role, T strA, T strB) {
		return this.get(strA).get(strB)[role-1];
	}

	/**
	 * Sets the payoff of an agent B that performs action 'B',
	 * given that agent A performs action 'A'
	 * 
	 * @param strA the action of agent A
	 * @param strB the action of agent B
	 * @param payoff the payoff
	 */
	public void setPayoff(int role, T strA, T strB, BigDecimal payoff) {
		this.get(strA).get(strB)[role-1] = payoff;
	}
	
	/**
	 * 
	 */
	public String toString() {
		DecimalFormat df = new DecimalFormat("####0.00");
		String s = "";

		/* Get the length of the longest action name */
		int maxLength = 0;
		for(T strategy : strategies.get(2)) {
			String strName;
			if(strategy == null) {
				strName = "No norm";
			}
			else {
				strName = strategy.toString();
			}
			int lenAc = strName.length();
			if(lenAc > maxLength) {
				maxLength = lenAc;
			}
		}

		/* Write header (with actions of player 2) */
//		s += "\t";
		for(T strategy : strategies.get(2)) {
			String strName;
			if(strategy == null) {
				strName = "No norm";
			}
			else {
				strName = strategy.toString();
			}
			int blankSpaces = maxLength - strName.length();
			s += "\t" + strName;

			/* Add blank spaces if necessary */
			if(blankSpaces > 0) {
				s += String.format("%" + blankSpaces +"s", "");
			}
		}
		s += "\n";

		/* Write conflict ratios */
		for(T strA : strategies.get(1)) {
			String strAName;
			if(strA == null) {
				strAName = "No norm";
			}
			else {
				strAName = strA.toString();
			}
			
			int blankSpaces = maxLength - strAName.length();
			s += strAName;

			/* Add blank spaces if necessary */
			if(blankSpaces > 0) {
				s += String.format("%" + blankSpaces +"s", "");
			}

			/* Write conflict ratios */
			for(T strB : strategies.get(2)) {
				BigDecimal payoffA = this.get(strA).get(strB)[0];
				BigDecimal payoffB = this.get(strA).get(strB)[1];

				s += "\t" + (Double.isNaN(payoffA.doubleValue()) ?
						"???" : df.format(payoffA.doubleValue())) +

						", " + (Double.isNaN(payoffB.doubleValue()) ? 
								"???" : df.format(payoffB));
			}
			s += "\n";
		}
		return s;
	}
	
	//---------------------------------------------------------------------------
	// Private methods
	//---------------------------------------------------------------------------
	
	/**
	 * Generates an empty payoff matrix for the given roles and the
	 * action spaces of each role  
	 * 
	 * TODO: Extend for more than 2 players 
	 */
	private void generateMatrix() {
		for(T strA : strategies.get(0)) {
			this.put(strA, new HashMap<T, BigDecimal[]>());

			for(T strB : strategies.get(1)) {
				this.get(strA).put(strB, new BigDecimal[2]);

				/* Initialise to 0 by default */
				this.get(strA).get(strB)[0] = BigDecimal.ZERO;
				this.get(strA).get(strB)[1] = BigDecimal.ZERO;
			}
		}
	}

}
