/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.ns.evaluation;

import uk.ac.ox.cs.ensm.network.NSNetwork;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * @author Javi
 *
 */
public interface NSFitnessFunction {

	/**
	 * 
	 * @param dim
	 * @param goal
	 * @param nCompliance
	 * @param nNetwork
	 * @return
	 */
	public void evaluate(NormativeSystem ns, NSNetwork nsNetwork);
}
