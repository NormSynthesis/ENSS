package uk.ac.ox.cs.ensm.ui;

/**
 * The thread that creates and updates the NSM norms tracer
 * 
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 */
public class GamesInspectorManager extends Thread {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------
	
//	private NormsTracerFrame traceFrame;
	private GamesInspectorFrame inspectorFrame;
	private boolean refresh;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Constructor
	 * 
	 * @param trafficInstitutions
	 */
	public GamesInspectorManager(GamesInspector manager) {
		
		this.inspectorFrame = new GamesInspectorFrame(manager);
		this.refresh = false;
	}

	/**
	 * Allow the GUI to be updated
	 */
	public void allowRefreshing() {
		synchronized(this) {
			this.refresh = true;
		}
	}
	
	/**
	 * Updates the trace frame to the infinite
	 */
	@Override
	public void run() {
		this.inspectorFrame.setVisible(true);
		
		while(true) {
			if(this.refresh) {
//				this.inspectorFrame.refresh();
			}
			
			try {
	      Thread.currentThread();
				Thread.sleep(Integer.MAX_VALUE);

      } catch (InterruptedException e) {
      }
		}
	}
}
