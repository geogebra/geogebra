package geogebra.common.plugin.script;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventType;
import geogebra.common.plugin.ScriptError;
import geogebra.common.plugin.ScriptType;

/**
 * @author arno
 *
 */
public abstract class Script {
	
	protected final App app;
	protected final String text;
	
	public Script(App app, String text) {
		super();
		this.app = app;
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public String getInternalText () {
		return text;
	}
	
	public void bind(GeoElement Geo, EventType evtType) {
		// Do nothing by default here
	}
	
	public void unbind(GeoElement geo, EventType evtType) {
		// Do nothing by default here
	}
	
	/**
	 * @param evt
	 * @throws ScriptError
	 */
	public abstract void run(Event evt) throws ScriptError;
	
	public abstract ScriptType getType();
	
	public String getLanguageName() {
		return this.getType().getName();
	}
}
