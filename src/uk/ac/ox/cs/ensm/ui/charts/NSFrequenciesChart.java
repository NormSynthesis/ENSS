package uk.ac.ox.cs.ensm.ui.charts;

import java.awt.Font;
import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.ns.NormativeSystem;

/**
 * A performance range chart shows the performance range of a norm or a 
 * norm group in terms of a system {@code Goal}. In the case of a {@code Norm},
 * the chart shows the performance range of the norm in terms of two dimension: 
 * effectiveness and necessity. In the case of a {@code NormGroup}, the chart
 * shows the performance range in terms of the effectiveness of the group
 * of norms to avoid conflicts
 * 
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 * @see PerformanceRange
 */
public class NSFrequenciesChart {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private NormativeGamesNetwork ngNetwork;
	
	private JFreeChart chart;
	private DefaultPieDataset dataset;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------

	/**
	 * Constructor with parameters
	 * 
	 * @param ensm the norm synthesis machine
	 * @param dim the norm evaluation dimension
	 * @param goal the system goal
	 * @param node the node of which to show the utility
	 */
	public NSFrequenciesChart(NormativeGamesNetwork ngNetwork) {
		this.ngNetwork = ngNetwork;
		this.dataset = new DefaultPieDataset();
		
		/* Create chart's elements */
		this.createChart();
	}

	/**
	 * Creates the chart that shows the performance range
	 * of the given {@code node}
	 * @param nEvMechanism 
	 * 
	 * @param node the given node
	 */
	private void createChart() {
		
		for(NormativeSystem ns : ngNetwork.getActiveNormativeSystems()) {
			this.dataset.setValue(ns.getName(), ns.getFrequency());
		}
		
    this.chart = ChartFactory.createPieChart("Frequencies of each NS",
    		dataset, false, true,false);

    PiePlot plot = (PiePlot) chart.getPlot();
    plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 12));
    plot.setNoDataMessage("No data available");
    plot.setCircular(false);
    plot.setLabelGap(0.02);
    
        
    PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(
        "{0} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
    plot.setLabelGenerator(gen);
    
    
    
    
    /* Paint Omega* in white */
//    plot.setSectionPaint("NS1", Color.white);
	}
	
	/**
	 * 
	 */
	private void updateDataSet() {
//		this.dataset.clear();
//		this.chart.removeLegend();
		for(NormativeSystem ns : ngNetwork.getActiveNormativeSystems()) {
			this.dataset.setValue(ns.getName(), ns.getFrequency());
		}
	}

	/**
	 * Refreshes the chart, updating the last values of each series in it,
	 * and showing these values in the GUI
	 */
	public void refresh() {
		this.updateDataSet();
	}

	/**
	 * Returns the chart
	 * 
	 * @return the chart
	 * @see JFreeChart
	 */
	public JFreeChart getChart() {
		return this.chart;
	}
}
