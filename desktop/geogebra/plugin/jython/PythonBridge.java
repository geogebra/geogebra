package geogebra.plugin.jython;

import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.AbstractApplication;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.main.Application;

import org.python.util.PythonInterpreter;

/**
 * @author arno
 * This class manages the Python interpreter.
 * 
 * It also listens to GeoGebra events (click, update, etc) and passes them
 * on to a PythonScriptInterface object (implemented in Python in pyggb.py)
 * which handles them.
 * 
 * It also listens to selection events on GeoElements and passes them on to
 * the PythonScriptInterface object as well.
 */
public class PythonBridge extends geogebra.common.plugin.jython.PythonBridge implements View, GeoElementSelectionListener {
	private Application application;
	private PythonInterpreter interpreter;
	private PythonScriptInterface pyInterface;
	
	/**
	 * This constructor actually starts off the python interpreter
	 * so it can take a while...
	 * @param app the GeoGebra application
	 */
	public PythonBridge(Application app) {
		application = app;
		interpreter = null;
		init();
	}
	
	private void init() {
		if (interpreter == null) {
			interpreter = new PythonInterpreter();
			interpreter.exec("import sys; sys.path.extend(['__pyclasspath__/geogebra/plugin/jython', '__pyclasspath__/Lib'])");
			interpreter.exec("from pyggb import interface");
			pyInterface = (PythonScriptInterface)interpreter.get("interface").__tojava__(PythonScriptInterface.class);
			pyInterface.init(application);
			application.getKernel().attach(this);
		}
	}
	
	/**
	 * Open / close the Python window
	 */
	public void toggleWindow() {
		pyInterface.toggleWindow();
	}
	
	/**
	 * Check the visibility of the Python window
	 * @return true if the Python window is currently visible
	 */
	public boolean isWindowVisible() {
		return pyInterface.isWindowVisible();
	}
	
	/**
	 * This should be called when a geo is clicked.
	 * (For now this is done in EuclidianController.switchModeForMouseReleased)
	 * @param geo the clicked GeoElement
	 */
	public void click(GeoElement geo) {
		pyInterface.handleEvent("click", geo);
	}
	
	/*
  	 * Implementation of View
  	 * used to dispatch events to the PythonScriptInterface object
	 */
	
	public void add(GeoElement geo) {
		pyInterface.handleEvent("add", geo);
	}
	
	public void remove(GeoElement geo) {
		pyInterface.handleEvent("remove", geo);
	}
	
	public void rename(GeoElement geo) {
		pyInterface.handleEvent("rename", geo);
	}
	
	public void update(GeoElement geo) {
		pyInterface.handleEvent("update", geo);
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
		/* not needed */		
	} 
	
	public void clearView() {
		/* not needed */
	}
	
	public void setMode(int mode) {
		/* not needed */
	}
	
	public int getViewID() {
		return 0;
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		pyInterface.notifySelected(geo, addToSelection);
	}

	/**
	 * Evaluate a Python script
	 * @param script script to evaluate
	 */
	public void eval(String script) {
		AbstractApplication.debug("python: evaluating:: "+script);
		//interpreter.exec(script);
		
		pyInterface.execute(script);
	}
}

