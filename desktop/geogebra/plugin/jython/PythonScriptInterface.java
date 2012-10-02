package geogebra.plugin.jython;

import geogebra.common.kernel.geos.GeoElement;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

/**
 * @author arno
 * pyggb.Interface implements this interface so that its methods can be called
 * from PythonBridge (see PythonBridge implementation).
 */
public interface PythonScriptInterface {
	
	/**
	 * @param api the api instance
	 */
	public void init(PythonFlatAPI api);
	
	/**
	 * This method is called every time an event is triggered on a GeoElement
	 * @param eventType string describing the type of the event
	 * @param eventTarget geo that is the target of the event
	 */
	public void handleEvent(String eventType, GeoElement eventTarget);
	
	/**
	 * This method is called every time a geo is selected
	 * @param geo the selected geo
	 * @param addToSelection true if it was added to the current selection
	 */
	public void notifySelected(GeoElement geo, boolean addToSelection);
	
	/**
	 * Open / close the Python window
	 */
	public void toggleWindow();
	
	/**
	 * Check the visibility of the Python window
	 * @return true if the Python window is currently visible
	 */
	public boolean isWindowVisible();
	
	/**
	 * Run a Python script
	 * @param script script to execute
	 */
	public void execute(String script);

	/**
	 * Set Python event listener
	 * @param geo target of the event listener
	 * @param evtType event type ("update", "click"...)
	 * @param code Python code to execute
	 */
	public void setEventHandler(GeoElement geo, String evtType, String code);

	public void removeEventHandler(GeoElement geo, String evtType);
	
	/**
	 * 
	 */
	public void reset();

	/**
	 * @return the value of the script in the editor or null
	 */
	public String getCurrentInitScript();
	
	/**
	 * @return the component for the python dock panel
	 */
	public JComponent getComponent();

	/**
	 * @return the menu bar for the python window
	 */
	public JMenuBar getMenuBar();
	
}
