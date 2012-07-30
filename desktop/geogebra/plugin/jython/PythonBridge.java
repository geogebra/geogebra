package geogebra.plugin.jython;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.main.AppD;

import org.python.util.PythonInterpreter;

/**
 * This class manages the Python interpreter.
 * 
 * It also listens to GeoGebra events (click, update, etc) and passes them
 * on to a PythonScriptInterface object (implemented in Python in pyggb.py)
 * which handles them.
 * 
 * It also listens to selection events on GeoElements and passes them on to
 * the PythonScriptInterface object as well.
 * @author arno
 */
public class PythonBridge extends geogebra.common.plugin.jython.PythonBridge implements View, GeoElementSelectionListener {
	private AppD application;
	private PythonFlatAPI api;
	private PythonInterpreter interpreter = null;
	private PythonScriptInterface pyInterface = null;
	private boolean ready = false;
	
	/** constructor for dummy implementation */
	public PythonBridge() {
		
	}
	
	/**
	 * This constructor actually starts off the python interpreter
	 * so it can take a while...
	 * @param app the GeoGebra application
	 */
	public PythonBridge(AppD app) {
		application = app;
		// FLAT
		api = new PythonFlatAPI(app);
	}
	
	/**
	 * Initialise PythonBridge
	 */
	public synchronized void init() {
		forceInit();
	}
	
	/**
	 * called directly 
	 */
	public synchronized void forceInit() {
		if (!ready) {
			App.debug("Initialising Python interpreter...");
			System.setProperty("python.cachedir.skip", "true");
			interpreter = new PythonInterpreter();
			interpreter.exec("import sys; sys.path.extend(['__pyclasspath__/geogebra/plugin/jython', '__pyclasspath__/Lib'])");
			interpreter.exec("from pyggb import interface");
			pyInterface = (PythonScriptInterface)interpreter.get("interface").__tojava__(PythonScriptInterface.class);
			pyInterface.init(api);
			application.getKernel().attach(this);
			ready = true;
			App.debug("Done Initialising Python interpreter.");
		}
	}
	
	/**
	 * @return true if the python bridge is ready for use
	 */
	public boolean isReady() {
		return ready;
	}
	
	/**
	 * Open / close the Python window
	 */
	public void toggleWindow() {
		
		// for applets
		forceInit();
		
		pyInterface.toggleWindow();
	}
	
	/**
	 * Check the visibility of the Python window
	 * @return true if the Python window is currently visible
	 */
	public boolean isWindowVisible() {
		return pyInterface.isWindowVisible();
	}
	
	private void handleEvent(String evt, GeoElement geo) {
		// AbstractApplication.debug("event: " + evt);
		pyInterface.handleEvent(evt, geo);
	}
	/**
	 * This should be called when a geo is clicked.
	 * (For now this is done in EuclidianController.switchModeForMouseReleased)
	 * @param geo the clicked GeoElement
	 */
	@Override
	public void click(GeoElement geo) {
		handleEvent("click", geo);
	}
	
	/*
  	 * Implementation of View
  	 * used to dispatch events to the PythonScriptInterface object
	 */
	
	public void add(GeoElement geo) {
		handleEvent("add", geo);
	}
	
	public void remove(GeoElement geo) {
		handleEvent("remove", geo);
	}
	
	public void rename(GeoElement geo) {
		handleEvent("rename", geo);
	}
	
	public void update(GeoElement geo) {
		handleEvent("update", geo);
	}
	
	public void updateVisualStyle(GeoElement geo) {
		//pyInterface.handleEvent("updateVisualStyle", geo);
	}
	
	public void updateAuxiliaryObject(GeoElement geo) {
		//pyInterface.handleEvent("updateAuxiliaryObject", geo);
	}
	
	public void repaintView() {
		/* not needed */
	}
	
	public void reset() {
		//pyInterface.reset();
	} 
	
	public void clearView() {
		//pyInterface.reset();
	}
	
	public void setMode(int mode) {
		/* not needed */
	}
	
	public int getViewID() {
		return 10;
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		pyInterface.notifySelected(geo, addToSelection);
	}
	
	/**
	 * Set Python event listener
	 * @param geo target of the event listener
	 * @param evtType event type ("update", "click"...)
	 * @param code Python code to execute
	 */
	@Override
	public void setEventListener(GeoElement geo, String evtType, String code) {
		pyInterface.setEventListener(geo, evtType, code);
	}
	
	/**
	 * Evaluate a Python script
	 * @param script script to evaluate
	 */
	@Override
	public void eval(String script) {
		pyInterface.execute(script);
	}

	/**
	 * @return the value of the currently edited script or null
	 */
	public String getCurrentPythonScript() {
		if (isReady()) {
			return pyInterface.getCurrentInitScript();
		}
		return null;
	}
	/**
	 * Execute the Python Script
	 */
	public void execScript() {
		pyInterface.reset();
	}
	public boolean hasFocus() {
		return false;
	}
	
	/**
	 * @return the JComponent for the python dock panel
	 */
	public JComponent getComponent() {
		return pyInterface.getComponent();
	}

	/**
	 * @return the JMenuBar for the python window
	 */
	public JMenuBar getMenuBar() {
		// TODO Auto-generated method stub
		return pyInterface.getMenuBar();
	}
}

