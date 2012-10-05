package geogebra.common.plugin.script;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventType;
import geogebra.common.plugin.ScriptError;
import geogebra.common.plugin.ScriptType;
import geogebra.common.plugin.jython.PythonBridge;


/**
 * @author arno
 * Script class for Python scripts
 */
public class PythonScript extends Script {

	private PythonBridge bridge;

	/**
	 * @param app the script's application
	 * @param text the script's source code
	 */
	public PythonScript(App app, String text) {
		super(app, text);
		bridge = app.getPythonBridge();
	}

	
	@Override
	public void bind(GeoElement geo, EventType evtType) {
		bridge.setEventHandler(geo, evtType.getName(), text);
	}


	@Override
	public void unbind(GeoElement geo, EventType evtType) {
		bridge.removeEventHandler(geo, evtType.getName());
	}


	@Override
	public void run(Event evt) throws ScriptError {
		// Do nothing because events are handled in the python code
	}

	@Override
	public ScriptType getType() {
		return ScriptType.PYTHON;
	}

}
