package uk.ac.ox.cs.ensm.ui.charts;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;

import uk.ac.ox.cs.ensm.network.NormativeGamesNetwork;

/**
 * A frame that shows the utility of a norm or a norm group
 * with respect to a goal
 * 
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 * @see PerformanceRange
 */
public class NSFrequenciesChartFrame extends JFrame {

	//---------------------------------------------------------------------------
	// Static attributes
	//---------------------------------------------------------------------------
	
	private static final long serialVersionUID = 8286435099181955453L;

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------
	
	private final int CHART_WIDTH = 550;
	private final int CHART_HEIGHT = 400;

	private NSFrequenciesChart chart;
	private ChartPanel chartPanel;

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
	public NSFrequenciesChartFrame(NormativeGamesNetwork ngNetwork) {
		
		super("NS frequencies");
		this.chart = new NSFrequenciesChart(ngNetwork);
		
		this.initComponents();
		this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
	}

	/**
	 * Initialises the chart's components
	 */
	private void initComponents() {
		this.createChartPanel();
	}

	/**
	 * Refreshes the chart's components
	 */
	public void refresh() {
		chartPanel.removeAll();
		chartPanel.revalidate();
		
		chart.refresh();
		chart.getChart().removeLegend();
		
		this.createChartPanel();
		chartPanel.repaint();
	}
	
	/**
	 * 
	 */
	private void createChartPanel() {
		JPanel content = new JPanel(new GridLayout());
		this.chartPanel = new ChartPanel(chart.getChart());
		content.add(chartPanel);
		chartPanel.setPreferredSize(new java.awt.Dimension(CHART_WIDTH, CHART_HEIGHT));
		setContentPane(content);
	}
//	private void refreshChart() {
//    jPanel_GraphicsTop.removeAll();
//    jPanel_GraphicsTop.revalidate(); // This removes the old chart 
//    aChart = createChart(); 
//    aChart.removeLegend(); 
//    ChartPanel chartPanel = new ChartPanel(aChart); 
//    jPanel_GraphicsTop.setLayout(new BorderLayout()); 
//    jPanel_GraphicsTop.add(chartPanel); 
//    jPanel_GraphicsTop.repaint(); // This method makes the new chart appear
//}
}
