package org.geogebra.web.full.main.activity;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.AppConfig;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.web.full.css.ResourceIconProvider;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.view.algebra.AlgebraItemHeader;
import org.geogebra.web.full.gui.view.algebra.AlgebraViewW;
import org.geogebra.web.full.gui.view.algebra.MenuItemCollection;
import org.geogebra.web.full.gui.view.algebra.RadioTreeItem;
import org.geogebra.web.full.main.HeaderResizer;
import org.geogebra.web.html5.gui.GeoGebraFrameW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.ExportedApi;
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

	/**
	 * @param algebraView
	 *            algebra view
	 * @return actions for the row
	 */
	MenuItemCollection<GeoElement> getAVMenuItems(AlgebraViewW algebraView);

	/**
	 * @param radioTreeItem parent item
	 * @param valid         previous input valid
	 * @param allowSliders  whether to allow sliders at all
	 * @param withSliders   whether to allow slider creation without asking
	 * @return error handler for algebra input.
	 */
	ErrorHandler createAVErrorHandler(RadioTreeItem radioTreeItem, boolean valid,
			boolean allowSliders, boolean withSliders);

	/**
	 * Show settings
	 *
	 * @param app
	 *            application
	 */
	void showSettingsView(AppW app);

	/**
	 * @return icon for menu
	 */
	SVGResource getIcon();

	/**
	 *
	 * @return use valid input
	 */
	boolean useValidInput();

	/**
	 *
	 * @param frame
	 *  			  application frame
	 * @return resizer class for the external header.
	 */
	HeaderResizer getHeaderResizer(GeoGebraFrameW frame);

	/**
	 * Returns with an icon provider which handles the vendor specific icons
	 *
	 * @return vendor specific icon provider
	 */
	ResourceIconProvider getResourceIconProvider();

	/**
	 * Please try to avoid if(isWhiteboard), use polymorphism instead
	 * 
	 * @return whether this is whiteboard activity
	 */
	boolean isWhiteboard();

	ExportedApi getExportedApi();

	/**
	 * @return icon for menu
	 */
	SVGResource getExamIcon();

	/**
	 * mark search view has opened
	 */
	void markSearchOpen();

	/**
	 * mark search view has opened
	 */
	void markSaveOpen();
}
