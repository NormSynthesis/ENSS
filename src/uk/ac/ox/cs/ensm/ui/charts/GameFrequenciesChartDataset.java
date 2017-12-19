package uk.ac.ox.cs.ensm.ui.charts;

import org.jfree.data.xy.XYSeries;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.network.NetworkNode;
import uk.ac.ox.cs.ensm.ns.evaluation.SlidingValueWindow;

/**
 * A series of the performance range chart. The series may have one of
 * the following types:
 * <ol>
 * <li> Punctual value: Shows the punctual values of a norm/norm group's
 * 			utility along time.
 * <li> Average: Shows the average of punctual values of a norm/norm group's
 * 			utility along time. 
 * <li> Top boundary: Shows the top boundary (average + standard deviation)
 * 			of a norm/norm group's utility along time.
 * <li> Bottom boundary: Shows the bottom boundary (average - standard
 * 			deviation) of a norm/norm group's utility along time. 
 * <li> Alpha spec: Shows the specialisation threshold.
 * </ol> Alpha gen: Shows the generalisation threshold.
 * 
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 */
public class GameFrequenciesChartDataset extends XYSeries {

	//---------------------------------------------------------------------------
	// Static attributes
	//---------------------------------------------------------------------------

	private static final long serialVersionUID = 523066952790946015L;

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private UtilityChartSeriesType type;
	private SlidingValueWindow perfRange;
	private NetworkNode node;

	private int x;

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor that specifies the {@code type} of the series
	 * 
	 * @param ensm the norm synthesis machine
	 * @param network the network of norms or norm groups
	 * @param node the norm or norm group
	 * @param dim the norm evaluation dimension
	 * @param goal the system goal 
	 */
	public GameFrequenciesChartDataset(EvolutionaryNSM ensm, String name, 
			SlidingValueWindow perfRange, UtilityChartSeriesType type) {

		super(name, true, true);

		this.perfRange = perfRange;
		this.type = type;
		this.x = 0;
				
		this.initValues();
	}

	/**
	 * Updates the series
	 */
	public void update() {
		if(perfRange.hasNewValue()) {
			int numSlidingValues = perfRange.getNumSlidingPunctualValues();
			this.addValue((numSlidingValues-1));
		}
	}

	/**
	 * Initialises the values of the series
	 */
	private void initValues() {
		for(int i=0; i<perfRange.getNumSlidingPunctualValues(); i++) {
			this.addValue(i);
		}
	}

	/**
	 * Adds to the series the value which is in the index
	 * {@code valueIndex} in the performance range of the node
	 * 
	 * @param valueIndex the value index
	 */
	private void addValue(int valueIndex) {
		double num = 0.0;
		x++;

		if(this.type == UtilityChartSeriesType.PunctualValue) {
			num = perfRange.getSlidingPunctualValues().get(valueIndex);
		}
		else if(this.type == UtilityChartSeriesType.Average) {
			num = perfRange.getSlidingAverage().get(valueIndex);	
		}
		else if(this.type == UtilityChartSeriesType.TopBoundary)	{
			num = perfRange.getSlidingTopBoundary().get(valueIndex);
		}
		else if(this.type == UtilityChartSeriesType.BottomBoundary) {
			num = perfRange.getSlidingBottomBoundary().get(valueIndex);
		}

		// Add value to the series
		this.add(x, num);
	}

	/**
	 * 
	 * @return
	 */
	public NetworkNode getNormativeNetworkNode() {
		return this.node;
	}

	/**
	 * The type of a series in a performance range chart:
	 * <ol>
	 * <li> Punctual value: Shows the punctual values of a norm/norm group's
	 * 			utility along time.
	 * <li> Average: Shows the average of punctual values of a norm/norm group's
	 * 			utility along time. 
	 * <li> Top boundary: Shows the top boundary (average + standard deviation)
	 * 			of a norm/norm group's utility along time.
	 * <li> Bottom boundary: Shows the bottom boundary (average - standard
	 * 			deviation) of a norm/norm group's utility along time. 
	 * <li> Alpha spec: Shows the specialisation threshold.
	 * </ol> Alpha gen: Shows the generalisation threshold.
	 * 
	 * @author "Javier Morales (jmorales@iiia.csic.es)"
	 */
	public enum UtilityChartSeriesType {
		PunctualValue,
		Average,
		TopBoundary,
		BottomBoundary,
//		AlphaSpec,
		ActThreshold,
		DeactThreshold,
		AlphaGen;
	}
}
