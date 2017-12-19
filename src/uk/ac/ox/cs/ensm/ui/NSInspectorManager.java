package uk.ac.ox.cs.ensm.ui;

/**
 * The thread that creates and updates the NSM norms tracer
 * 
 * @author "Javier Morales (jmorales@iiia.csic.es)"
 */
public class NSInspectorManager extends Thread {

	//---------------------------------------------------------------------------
	// Attributes
	//---------------------------------------------------------------------------
	
//	private NormsTracerFrame traceFrame;
	private NSInspectorFrame inspectorFrame;
	private boolean refresh;
	
	//---------------------------------------------------------------------------
	// Methods
	//---------------------------------------------------------------------------
	
	/**
	 * Constructor
	 * 
	 * @param trafficInstitutions
	 */
	public NSInspectorManager(NSInspector manager) {
		
		this.inspectorFrame = new NSInspectorFrame(manager);
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
//		this.traceFrame.setVisible(true);
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
