/**
 * 
 * @author Javier Morales (javier.morales@cs.ox.ac.uk)
 */
package uk.ac.ox.cs.ensm.ns.evaluation;

import java.math.BigDecimal;
import java.util.Map;

import uk.ac.ox.cs.ensm.network.NSNetwork;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 *
 */
public interface NSReplicationFunction {

	/**
	 * 
	 * @param nsNetwork
	 * @return
	 */
	public Map<NormativeSystem,BigDecimal> computeNSReplicationRates(
			NSNetwork nsNetwork);
}
