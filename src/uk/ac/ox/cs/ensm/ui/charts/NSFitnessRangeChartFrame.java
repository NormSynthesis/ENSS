package uk.ac.ox.cs.ensm.ui.charts;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.network.NGNNode;

/**
 * A frame that shows the utility of a norm or a norm group
 * with respect to a goal
 * 
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 * @see PerformanceRange
 */
public class NSFitnessRangeChartFrame extends JFrame {

	//---------------------------------------------------------------------------
	// Static attributes
	//---------------------------------------------------------------------------
	
	private static final long serialVersionUID = 8286435099181955453L;

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------
	
	private final int CHART_WIDTH = 550;
	private final int CHART_HEIGHT = 400;

	private List<NSFitnessRangeChart> charts;

	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Constructor
	 * 
	 * @param ensm the norm synthesis machine
	 * @param goal the goal from which to show the utility
	 * @param node the node from which to show the utility
	 */
	public NSFitnessRangeChartFrame(EvolutionaryNSM ensm, NGNNode node) {
		
		super("Norm scores for goal GCols");
		this.charts = new ArrayList<NSFitnessRangeChart>();

		this.charts.add(new NSFitnessRangeChart(ensm, node));
		
		this.initComponents();
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	/**
	 * Initialises the chart's components
	 */
	private void initComponents() {
		JPanel content = new JPanel(new GridLayout());
		ChartPanel chartPanel;

		for(NSFitnessRangeChart chart : this.charts) {
			chartPanel = new ChartPanel(chart.getChart());
			content.add(chartPanel);
			chartPanel.setPreferredSize(new java.awt.Dimension(CHART_WIDTH, CHART_HEIGHT));
		}
		setContentPane(content);
	}

	/**
	 * Refreshes the chart's components
	 */
	public void refresh() {
		for(NSFitnessRangeChart chart : this.charts) {
			chart.refresh();
		}
	}
}
