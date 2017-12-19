/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.game;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import uk.ac.ox.cs.ensm.agent.Combination;

/**
 * 1-role and 2-role asymmetric payoff matrix
 * 
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class AsymmetricPayoffMatrix<T>
extends HashMap<Combination<T>, Double[]> {

	/* Serial ID */
	private static final long serialVersionUID = 9064839189162052563L;

	/* List of available strategies (either actions or norms) for each role */
	private Map<Integer, LinkedHashSet<T>> strategies;

	/**
	 * Constructor
	 */
	public AsymmetricPayoffMatrix() {
		this.strategies = new TreeMap<Integer,LinkedHashSet<T>>();
	}

	/**
	 * Constructor
	 */
	public AsymmetricPayoffMatrix(Combination<T> combination) {
		this();
		this.add(combination);
	}

	/**
	 * Constructor
	 */
	public AsymmetricPayoffMatrix(List<Combination<T>> combinations) {
		this();		
		this.addAll(combinations);
	}

	/**
	 * @param combinations
	 */
	public void addAll(List<Combination<T>> combinations) {
		for(Combination<T> combination : combinations) {
			this.add(combination);
		}
	}

	/**
	 * @param combination
	 */
	public void add(Combination<T> combination) {
		int numRoles = combination.size();

		/* Create the data structures for the combination and add it */
		if(!this.containsKey(combination)) {
			this.put(combination, new Double[numRoles]);

			/* Initialise to zero */
			for(int role=0; role<numRoles; role++) {
				this.get(combination)[role] = 0.0;
			}
		}

		/* Add each strategy to the strategy space of its
		 * corresponding role in the matrix */
		for(int role=0; role<numRoles; role++) {
			if(!strategies.containsKey(role)) {
				strategies.put(role, new LinkedHashSet<T>());
			}
			strategies.get(role).add(combination.get(role));
		}
	}

	/**
	 * @param role
	 * @return
	 */
	public List<T> getStrategySpace(int role) {
		return new ArrayList<T>(this.strategies.get(role));
	}

	/**
	 * Returns a list of all the action combinations of the game
	 * 
	 * @return
	 */
	public List<Combination<T>> getCombinations() {
		List<Combination<T>> list = new ArrayList<Combination<T>>(this.keySet());
		
		/* Sort elements with a comparator */
		Comparator<Combination<T>> comparator = new Comparator<Combination<T>>() {
			@Override
			public int compare(Combination<T> c1, Combination<T> c2) {
				return c1.toString().compareTo(c2.toString()); 
			}
		};
		Collections.sort(list, comparator);
		
		return list;
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
	public double getPayoff(int role, Combination<T> combination) {
		return this.get(combination)[role];
	}

	/**
	 * Sets the payoff of an agent B that performs action 'B',
	 * given that agent A performs action 'A'
	 * 
	 * @param strA the action of agent A
	 * @param strB the action of agent B
	 * @param payoff the payoff
	 */
	public void setPayoff(int role, Combination<T> comb, double payoff) {
		this.get(comb)[role] = payoff;
	}

	/**
	 * 
	 */
	public String toString() {
		DecimalFormat df = new DecimalFormat("####0.00");
		int numRoles = this.getCombinations().get(0).size();
		String s = "";

		/* Get the length of the longest action name */
		int maxLength = 0;
		for(T str : this.getStrategySpace(0)) {
			int lenAc = str.toString().length();
			if(lenAc > maxLength) {
				maxLength = lenAc;
			}
		}

		/* Write header (with actions of agent B) */
		if(numRoles == 2) {
			s += "";		
			for(T str : this.getStrategySpace(1)) {
				String acName = str.toString();
				int blankSpaces = maxLength - acName.length();
				s += "\t" + acName;

				/* Add blank spaces if necessary */
				if(blankSpaces > 0) {
					s += String.format("%" + blankSpaces +"s", "");
				}
			}
			s += "\n";
		}

		/* Write conflict ratios */
		for(T str1 : this.getStrategySpace(0)) {
			String strName = str1.toString();
			int blankSpaces = maxLength - strName.length();
			s += strName;

			/* Add blank spaces if necessary */
			if(blankSpaces > 0) {
				s += String.format("%" + blankSpaces +"s", "");
			}

			/* 1-role game */
			if(numRoles == 1) {
				Combination<T> ac = new Combination<T>(str1);
				double cr = this.getPayoff(0, ac);
				s += "\t" + (Double.isNaN(cr) ?
						"???" : df.format(cr));
			}

			/* 2-role game */
			else {
				for(T str2 : this.getStrategySpace(1)) {
					Combination<T> sc = new Combination<T>(str1);
					sc.add(str2);

					double p1 = this.getPayoff(0,sc);
					double p2 = this.getPayoff(1,sc);

					s += "\t" + (Double.isNaN(p1) ?
							"???" : df.format(p1)) +

							", " + (Double.isNaN(p2) ? 
									"???" : df.format(p2));
				}
			}
			s += "\n";
		}
		return s;
	}
}
