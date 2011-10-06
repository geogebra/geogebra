package geogebra.plugin.jython;

import geogebra.kernel.GeoElement;
import geogebra.kernel.View;
import geogebra.main.Application;

import org.python.core.Py;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class PythonBridge implements View {
	private Application application;
	private PythonInterpreter interpreter;
	private String eventType;
	private GeoElement eventTarget;
	private PythonScriptInterface pyInterface;
	
	public PythonBridge(Application app) {
		application = app;
		interpreter = null;
	}
	
	private void init() {
		if (interpreter == null) {
			
			//final PySystemState engineSys = new PySystemState();
			//engineSys.path.append( Py.newString( "__pyclasspath__/Lib" ) );
			//Py.setSystemState(engineSys);

			interpreter = new PythonInterpreter();
			interpreter.exec("import sys; sys.path.append('__pyclasspath__/geogebra/plugin/jython')");
			interpreter.exec("import sys; sys.path.append('__pyclasspath__/Lib')");
			interpreter.exec("import sys; print sys.path");
			interpreter.exec("from pyggb import interface");
			pyInterface = (PythonScriptInterface)interpreter.get("interface").__tojava__(PythonScriptInterface.class);
			pyInterface.init(application);
			application.getKernel().attach(this);
		}
	}
	
	public void exec(String code) {
		init();
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

}

