package geogebra.plugin.jython;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventListener;
import geogebra.common.plugin.jython.PythonBridge;
import geogebra.main.AppD;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

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
public class PythonBridgeD extends PythonBridge implements EventListener, GeoElementSelectionListener {

	private AppD application;
	private PythonFlatAPI api;
	private PythonInterpreter interpreter = null;
	private PythonScriptInterface pyInterface = null;
	private boolean ready = false;
	
	/** constructor for dummy implementation */
	public PythonBridgeD() {
		
	}
	
	/**
	 * This constructor actually starts off the python interpreter
	 * so it can take a while...
	 * @param app the GeoGebra application
	 */
	public PythonBridgeD(AppD app) {
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
			application.getEventDispatcher().addEventListener(this);
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
	
	/*
	 * @see geogebra.common.main.GeoElementSelectionListener
	 */
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
	public void setEventHandler(GeoElement geo, String evtType, String code) {
		pyInterface.setEventHandler(geo, evtType, code);
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
		pyInterface.runInitScript();
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
		return pyInterface.getMenuBar();
	}

	@Override
	public void removeEventHandler(GeoElement geo, String evtType) {
		pyInterface.removeEventHandler(geo, evtType);
	}
	
	/*
	 * Implementation of EventListener
	 */
	public void sendEvent(Event evt) {
		pyInterface.handleEvent(evt.type.getName(), evt.target);
	}

	public void reset() {
		pyInterface.reset();
	}

}

