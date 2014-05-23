package geogebra.common.gui.layout;

import geogebra.common.awt.Component;

/**
 * @author judit
 * interface for geogebra.gui.layout.DockPanel
 * 
 */
public interface DockPanel extends Component{

	String getToolbarString();

	boolean isOpenInFrame();

	String getDefaultToolbarString();

	int getViewId();
	
	/**
	 * Close this panel permanently.
	 */
	public void closePanel();

}
