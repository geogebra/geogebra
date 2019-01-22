package org.geogebra.web.full.main.activity;

import org.geogebra.common.main.AppConfig;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

/**
 * App-specific behaviors
 */
public interface GeoGebraActivity {

	/**
	 * @return application configuration
	 */
	AppConfig getConfig();

	/**
	 * Initialize the activity
	 * 
	 * @param appW
	 *            app
	 */
	void start(AppW appW);

	/**
	 * @return icon for numeric toggle button
	 */
	SVGResource getNumericIcon();

	/**
	 * @return output prefix icon
	 */
	SVGResource getOutputPrefixIcon();

	/**
	 * Build title bar for a dock panel.
	 * 
	 * @param dockPanelW
	 *            dock panel
	 */
	void initStylebar(DockPanelW dockPanelW);

	/**
	 * @return panel for algebra view
	 */
	DockPanelW createAVPanel();

	/**
	 * @param radioTreeItem
	 *            AV item
	 * @return header for AV item
	 */
	AlgebraItemHeader createAVItemHeader(RadioTreeItem radioTreeItem);

	boolean showObjectSettingsFromAV();
}
