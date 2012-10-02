package geogebra.common.plugin.script;

import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.plugin.Event;
import geogebra.common.plugin.EventType;
import geogebra.common.plugin.ScriptError;
import geogebra.common.plugin.ScriptType;

/**
 * @author arno
 * Parent class for script objects.  There is one script class for each
 * type of script.  They must be added to the enum ScriptType.
 */
public abstract class Script {
	
	/**
	 * Application the script belongs to
	 */
	protected final App app;
	/**
	 * source code for the script
	 */
	protected final String text;
	
	/**
	 * @param app the script's application
	 * @param text the script's source code
	 */
	public Script(App app, String text) {
		super();
		this.app = app;
		this.text = text;
	}
	
	/**
	 * Get the script's source code
	 * @return the source code as a string
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Get the script's internal text (which could be different from the 
	 * localized text)
	 * @return the internal text
	 */
	public String getInternalText () {
		return text;
	}
	
	/**
	 * Perform actions necessary (if any) to bind the script to
	 * a GeoElement via a given EventType
	 * @param geo the geo
	 * @param evtType the event type
	 */
	public void bind(GeoElement geo, EventType evtType) {
		// Do nothing by default here
	}
	
	/**
	 * Perform actions necessary (if any) to unbind the script from
	 * a GeoElement via a given EventType
	 * @param geo the geo
	 * @param evtType the event type
	 */
	public void unbind(GeoElement geo, EventType evtType) {
		// Do nothing by default here
	}
	
	/**
	 * Run the script
	 * @param evt the event that triggered the script
	 * @throws ScriptError error thrown if the script cannot be run
	 */
	public abstract void run(Event evt) throws ScriptError;
	
	/**
	 * Get the script's type
	 * @return the script's type
	 */
	public abstract ScriptType getType();
	
	/**
	 * Get the language name of the script (convenience function)
	 * @return the language name
	 */
	public String getLanguageName() {
		return this.getType().getName();
	}

	/**
	 * @return the XML attribute name for serialization to file
	 */
	public Object getXMLName() {
		return this.getType().getXMLName();
	}
}
