package uk.ac.ox.cs.ensm.ui;

import java.util.ArrayList;
import java.util.List;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;
import uk.ac.ox.cs.ensm.ui.charts.NSFitnessRangeChartThread;
import uk.ac.ox.cs.ensm.ui.charts.NSFrequenciesChartThread;

/**
 * This tool manages builds and starts a new thread for each frame 
 * that is showing relevant information of the system 
 * 
 * @author Javier Morales (jmorales@iiia.csic.es)
 *
 */
public class NSInspector {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private EvolutionaryNSM ensm;		// the norm synthesis machine
	private Thread nsInspectorThread;		// thread of norms inspector
	private boolean converged;			// has the norm synthesis process converged?
	private List<NSFitnessRangeChartThread> tNSFitnessCharts;
	private NSFrequenciesChartThread tNSFreqsChart;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor
	 * 
	 * @param ensm the norm synthesis machine
	 */
	public NSInspector(EvolutionaryNSM ensm)  {
		this.ensm = ensm;
		this.converged= false;

		/* Construct threads to show norms charts and frames */
		if(ensm.isGUI()) {
			this.tNSFitnessCharts = new ArrayList<NSFitnessRangeChartThread>();
			this.nsInspectorThread = new NSInspectorManager(this);
		}
		this.allowRefreshing();
	}

	/**
	 * This method is called when the thread runs
	 */
	public void show() {
		if(ensm.isGUI())
			this.nsInspectorThread.start();
	}

	/**
	 * Allows the GUI to be refreshed
	 */
	public void allowRefreshing() {
//		this.tracerThread.allowRefreshing();
	}

	/**
	 * Refresh the GUI
	 */
	public void refresh() {
		if(ensm.isGUI()) {
			nsInspectorThread.interrupt();

			// Update norm score charts
			for(NSFitnessRangeChartThread nScChart : tNSFitnessCharts) {
				nScChart.interrupt();
			}
			
			// Update NS frequencies chart
			if(this.tNSFreqsChart != null) {
				this.tNSFreqsChart.interrupt();	
			}
		}
	}

	/**
	 * Adds and shows a new utility chart for a norm group
	 * 
	 * @param ns the norm group
	 */
	public synchronized void addNSFitnessChart(NormativeSystem ns) {
		NSFitnessRangeChartThread tChart = new NSFitnessRangeChartThread(ensm, ns);
		this.tNSFitnessCharts.add(tChart);

		tChart.allowRefreshing();
		tChart.start();
	}

	/**
	 * Adds and shows a new utility chart for a norm group
	 * 
	 * @param ns the norm group
	 */
	public synchronized void showNSFrequenciesChart() {
		tNSFreqsChart = new NSFrequenciesChartThread(ensm);
		tNSFreqsChart.allowRefreshing();
		tNSFreqsChart.start();
	}
	
	/**
	 * Adds and shows a new utility chart for a norm group
	 * 
	 * @param ns the norm group
	 */
	public synchronized void addNSInteractionConflictRatioChart(NormativeSystem ns1, 
			NormativeSystem ns2) {
		
//		NSFitnessRangeChartThread tChart = new NSFitnessRangeChartThread(ensm, ns1);
//		this.nsFitnessCharts.add(tChart);
//
//		tChart.allowRefreshing();
//		tChart.start();
	}
	
	/**
	 * 
	 * @param converged
	 */
	public void setConverged(boolean converged) {
		this.converged = converged;
	}

	/**
	 * Returns <tt>true</tt> if the norm synthesis machine has converged
	 * to a normative system
	 * 
	 * @return <tt>true</tt> if the norm synthesis machine has converged
	 * to a normative system
	 */
	public boolean hasConverged() {
		return this.converged;
	}

	/**
	 * Returns the norm synthesis machine
	 * 
	 * @return the norm synthesis machine
	 */
	public EvolutionaryNSM getNormSynthesisMachine() {
		return this.ensm;
	}
}
