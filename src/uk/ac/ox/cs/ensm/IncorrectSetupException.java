package uk.ac.ox.cs.ensm;

/**
 * Exception thrown whenever the Norm Synthesis Machine (NSM) has not
 * been setup correctly. To perform norm synthesis, the NSM must have:
 * <ol>
 * <li>	A {@code monitor} with some {@code sensors} to perceive the scenario;
 * <li>	An {@code omega function} to compute the normative system from 
 * 			the normative network; and 
 * <li>	A norm synthesis {@code strategy}.
 * </ol>
 * @author "Javier Morales (javier.morales@cs.ox.ac.uk)"
 */
public class IncorrectSetupException extends Exception {

	//---------------------------------------------------------------------------
	// Static attributes
	//---------------------------------------------------------------------------
  
	private static final long serialVersionUID = -2780976534840647617L;

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Constructor with message
	
	 * @param message the exception message
	 */
	public IncorrectSetupException(String message) {
		super(message);
	}
}
