package uk.ac.ox.cs.ensm.ui.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import uk.ac.ox.cs.ensm.EvolutionaryNSM;
import uk.ac.ox.cs.ensm.network.NGNNode;
import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;
import uk.ac.ox.cs.ensm.ns.evaluation.SlidingValueWindow;
import uk.ac.ox.cs.ensm.ui.charts.NSFitnessRangeChartSeries.UtilityChartSeriesType;

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
public class NSFitnessRangeChart {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------

	private final int ACTIVE_NORM_WIDTH = 2;

	private EvolutionaryNSM ensm;
	private NormativeGamesNetwork ngNnetwork;
	private SlidingValueWindow slidingFitness;
	private NGNNode node;
	
	private BasicStroke dottedStroke;
	private BasicStroke continuousStroke;
	
	private JFreeChart chart;
	private XYPlot plot;
	private List<NSFitnessRangeChartSeries> series;
	private XYSeriesCollection dataset;
	private XYLineAndShapeRenderer renderer;
	private List<Color> seriesColors;
	
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
	public NSFitnessRangeChart(EvolutionaryNSM ensm, NGNNode node) {

		this.series = new ArrayList<NSFitnessRangeChartSeries>();
		this.ensm = ensm;
		this.node = node;
		
		this.ngNnetwork = ensm.getNormativeGamesNetwork();
		
		this.slidingFitness = ngNnetwork.getFitnessRange(node);
		
		this.dottedStroke = new BasicStroke(ACTIVE_NORM_WIDTH + 3,
				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, 
				new float[] {2f, 100f}, 0.0f);
		
		this.continuousStroke = new BasicStroke(ACTIVE_NORM_WIDTH);
		
//		this.thresholdStroke = new BasicStroke(ACTIVE_NORM_WIDTH + 1,
//				BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, 
//				new float[] {4f, 50f}, 0.0f);
		
		this.seriesColors = new ArrayList<Color>();
		this.seriesColors.add(Color.DARK_GRAY);
		this.seriesColors.add(Color.BLUE);
		this.seriesColors.add(Color.ORANGE);
		this.seriesColors.add(Color.YELLOW);
		this.seriesColors.add(Color.BLACK);
		
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
		String xLabel, yLabel;
		xLabel = "Num Evaluation";
		yLabel = "Score";
		
		NSFitnessRangeChartSeries punctualValuesSeries = 
				this.createSeries(UtilityChartSeriesType.PunctualValue);
		
		/* Add punctual values series */
		this.dataset = new XYSeriesCollection(punctualValuesSeries);
		this.series.add(punctualValuesSeries);
		
		this.chart = ChartFactory.createXYLineChart("Fitness range", xLabel, yLabel,
				dataset, PlotOrientation.VERTICAL, true, true, false);		

		this.chart.setBackgroundPaint(Color.white);
		this.plot = chart.getXYPlot();
		this.plot.setBackgroundPaint(Color.lightGray);
		this.plot.setDomainGridlinePaint(Color.white);
		this.plot.setRangeGridlinePaint(Color.white);
		
		this.renderer = new XYLineAndShapeRenderer(true, false);
		this.plot.setRenderer(renderer);

		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();   
		rangeAxis.setAutoRangeIncludesZero(false);

		NumberAxis axis = (NumberAxis) plot.getDomainAxis();
		axis.setFixedAutoRange(this.ensm.getSettings().getNSFitnessRangeSize());
		
		/* Add extra series depending on the norm synthesis method */
		this.addPunctualAndAvgSeries(node);
//		this.addBBSeries(node);
//			this.addQMASeries(node);
	}

	/**
	 * @param punctualvalue
	 * @return
	 */
  private NSFitnessRangeChartSeries createSeries(
      UtilityChartSeriesType type) {

  	NSFitnessRangeChartSeries series;
  	if(type == UtilityChartSeriesType.PunctualValue) {
  		series = new NSFitnessRangeChartSeries(ensm, node.getName(),
  				slidingFitness, type);
  	}
  	else {
  		series = new NSFitnessRangeChartSeries(ensm, type.toString(),
  				slidingFitness, type);
  	}
  	return series;
  }

  /**
   * 
   * @param node
   */
  private void addPunctualAndAvgSeries(NGNNode node) {
  	this.addSeries(this.createSeries(UtilityChartSeriesType.Average));

		/* Set punctual values series style */
		renderer.setSeriesStroke(0, this.dottedStroke, false);
		renderer.setSeriesPaint(0, Color.DARK_GRAY);
		
		/* Set average and bollinger bands series style */
		renderer.setSeriesStroke(1, this.continuousStroke, false);
		renderer.setSeriesPaint(1, Color.BLUE);
  }
  
	/**
	 * Adds a new series to the chart
	 * 
	 * @param node the node
	 * @param type the series type
	 */
	public void addSeries(NSFitnessRangeChartSeries series) {
		this.dataset.addSeries(series);	
		this.series.add(series);
	}

	/**
	 * Refreshes the chart, updating the last values of each series in it,
	 * and showing these values in the GUI
	 */
	public void refresh() {
		for(NSFitnessRangeChartSeries s : series) {
			s.update();
		}
		slidingFitness.setNewValue(false);
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
