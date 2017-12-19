/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.game;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.ensm.norm.Norm;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public class NormCombination extends ArrayList<Norm> {

	/** Serial ID */
	private static final long serialVersionUID = -401157251424562785L;

	/**
	 * 
	 */
	public NormCombination() {
		super();
	}

	/**
	 * 
	 * @param norm
	 */
	public NormCombination(Norm n1) {
		super();
		this.add(n1);
	}

	/**
	 * 
	 * @param norm
	 */
	public NormCombination(Norm n1, Norm n2) {
		super();
		this.add(n1);
		this.add(n2);
	}

	/**
	 * 
	 * @param norms
	 */
	public NormCombination(List<Norm> norms) {
		super();

		for(Norm norm : norms) {
			this.add(norm);
		}
	}

	/**
	 * Compares two normative profiles
	 * 
	 * @return true if the normative profiles are equal. False otherwise
	 */
	public boolean equals(NormCombination oProfile) {
		if(this.size() != oProfile.size()) {
			return false;
		}

		for(int i=0; i<this.size(); i++) {

			/* If both norms are different from null, compare norms */
			if(this.get(i) != null && oProfile.get(i) != null) {
				if(!this.get(i).equals(oProfile.get(i))) {
					return false;
				}
			}	

			/* In case one of the norms is null and the other 
			 * is not, then return false */
			else {
				if((this.get(i) != null && oProfile.get(i) == null) ||
						(this.get(i) == null && oProfile.get(i) != null)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * @return
	 */
	public boolean isNull() {
		for(Norm norm : this) {
			if(norm != null)
				return false;
		}
		return true;
	}

	/**
	 * @return
	 */
	public int getNumberOfNorms() {
		int numNorms = 0;
		for(Norm norm : this) {
			if(norm != null) 
				numNorms++;
		}
		return numNorms;
	}
}
