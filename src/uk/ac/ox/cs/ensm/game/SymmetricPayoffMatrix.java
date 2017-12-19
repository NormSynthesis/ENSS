/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.game;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import uk.ac.ox.cs.ensm.deprecated.AsymmetricPayoffMatrix;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class SymmetricPayoffMatrix<T> {

	/* We use an asymmetric payoff matrix as symmetric */
	private AsymmetricPayoffMatrix<T> matrix;

	/* List of available actions */
	private Map<Integer, List<T>> strategies;

	/**
	 * Constructor
	 */
	public SymmetricPayoffMatrix(int numRoles, Map<Integer, List<T>> strSpace) {
		this.matrix = new AsymmetricPayoffMatrix<T>(strSpace);
	}

	/**
	 * @param player
	 * @return
	 */
	public List<T> getStrategySpace(int player) {
		return this.strategies.get(player);
	}

	/**
	 * 
	 * @param player
	 * @param strategy
	 * @return
	 */
	public boolean playerHasStrategy(int player, T strategy) {
		return this.strategies.get(player).contains(strategy);
	}

	/**
	 * Returns the payoff of an agent A that performs action 'A' 
	 * given that agent B performs action 'B' 
	 *  
	 * @return
	 */
	public BigDecimal getPayoff(T strA, T strB) {
		return this.matrix.get(strA).get(strB)[0];
	}

	/**
	 * Sets the payoff of an agent B that performs action 'B',
	 * given that agent A performs action 'A'
	 * 
	 * @param strA the action of agent A
	 * @param strB the action of agent B
	 * @param payoff the payoff
	 */
	public void setPayoff(T strA, T strB, BigDecimal payoff) {
		this.matrix.get(strA).get(strB)[0] = payoff;
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
			int lenStr = 0; 
			if(strategy == null) {
				lenStr  = 2;
			}
			else {
				lenStr = strategy.toString().length();
			}
			if(lenStr > maxLength) {
				maxLength = lenStr;
			}
		}

		/* Write header (with actions of player 2) */
		//		s += "\t";
		for(T strategy : strategies.get(2)) {
			String strName;

			if(strategy == null) {
				strName = "--";
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
			String acName;
			if(strA == null) {
				acName = "--";
			}
			else {
				acName = strA.toString();
			}
			int blankSpaces =  maxLength - acName.length();
			s += acName;

			/* Add blank spaces if necessary */
			if(blankSpaces > 0) {
				s += String.format("%" + blankSpaces +"s", "");
			}

			/* Write conflict ratios */
			for(T strB : strategies.get(2)) {
				BigDecimal payoff = this.getPayoff(strA, strB);

				s += "\t" + (Double.isNaN(payoff.doubleValue()) ?
						"???" : df.format(payoff.doubleValue()));
			}
			s += "\n";
		}
		return s;
	}
}
