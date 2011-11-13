package geogebra.plugin.jython;

import geogebra.kernel.GeoElement;
import geogebra.kernel.View;
import geogebra.main.Application;
import geogebra.main.GeoElementSelectionListener;

import org.python.util.PythonInterpreter;

public class PythonBridge implements View, GeoElementSelectionListener {
	private Application application;
	private PythonInterpreter interpreter;
	private PythonScriptInterface pyInterface;
	
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
	
	public void toggleWindow() {
		pyInterface.toggleWindow();
	}
	
	public boolean isWindowVisible() {
		return pyInterface.isWindowVisible();
	}
	
	public void click(GeoElement geo) {
		pyInterface.handleEvent("click", geo);
	}
	
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
	}
	
	public void reset() {
	} 
	
	public void clearView() {
	}
	
	public void setMode(int mode) {
	}
	
	public int getViewID() {
		return 0;
	}

	public void geoElementSelected(GeoElement geo, boolean addToSelection) {
		pyInterface.notifySelected(geo, addToSelection);
	}

}

