/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.agent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class Combination<T> extends ArrayList<T> {

	/** Serial ID */
	private static final long serialVersionUID = -8294777106459796336L;

	/**
	 * 
	 */
	public Combination() {
		super();
	}

	/**
	 * 
	 * @param norm
	 */
	public Combination(T strategy) {
		super();
		this.add(strategy);
	}

	/**
	 * 
	 * @param norm
	 */
	public Combination(T str1, T str2) {
		super();
		this.add(str1);
		this.add(str2);
	}

	/**
	 * 
	 * @param strategies
	 */
	public Combination(List<T> strategies) {
		super();

		for(T strategy : strategies) {
			this.add(strategy);
		}
	}

	/**
	 * Compares two normative profiles
	 * 
	 * @return true if the normative profiles are equal. False otherwise
	 */
	public boolean equals(Combination<T> strCombination) {

		if(this.size() != strCombination.size()) {
			return false;
		}

		for(int i=0; i<this.size(); i++) {

			/* If both norms are different from null, compare norms */
			if(this.get(i) != null && strCombination.get(i) != null) {
				if(!this.get(i).equals(strCombination.get(i))) {
					return false;
				}
			}	

			/* In case one of the strategies is null and the other 
			 * is not, then return false */
			else {
				if((this.get(i) != null && strCombination.get(i) == null) ||
						(this.get(i) == null && strCombination.get(i) != null)) {
					return false;
				}
			}
		}
		return true;
	}
}
