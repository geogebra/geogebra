package org.geogebra.common.main;

import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.settings.Settings;

/**
 * 
 * @author mathieu
 *
 *         Companion for application
 */
public class AppCompanion {

	protected App app;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 */
	public AppCompanion(App app) {
		this.app = app;
	}

	/**
	 * 
	 * @return new kernel
	 */
	public Kernel newKernel() {
		return new Kernel(app);
	}

	/**
	 * return true if commands of this table should be visible in input bar help
	 * and autocomplete
	 * 
	 * @param table
	 *            table number, see CommandConstants.TABLE_*
	 * @return true for visible tables
	 */
	protected boolean tableVisible(int table) {
		return !(table == CommandsConstants.TABLE_CAS
				|| table == CommandsConstants.TABLE_3D || table == CommandsConstants.TABLE_ENGLISH);
	}

	/**
	 * XML settings for both EVs
	 * 
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            whether we need this for preference XML
	 */
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		app.getEuclidianView1().getXML(sb, asPreference);
		if (app.hasEuclidianView2EitherShowingOrNot(1)) {
			app.getEuclidianView2(1).getXML(sb, asPreference);
		}
	}

	/**
	 * @param plane
	 *            plane creator
	 * @param panelSettings
	 *            panel settings
	 * @return create a new euclidian view for the plane
	 */
	public EuclidianViewCompanion createEuclidianViewForPlane(
			ViewCreator plane, boolean panelSettings) {
		return null;
	}

	/**
	 * store view creators (for undo)
	 */
	public void storeViewCreators() {
		// used in 3D
	}

	/**
	 * recall view creators (for undo)
	 */
	public void recallViewCreators() {
		// used in 3D
	}

	/**
	 * reset ids for 2D view created by planes, etc. Used in 3D.
	 */
	public void resetEuclidianViewForPlaneIds() {
		// used in 3D

	}

	/**
	 * @return new EuclidianDockPanelForPlane
	 */
	public DockPanel createEuclidianDockPanelForPlane(int id, String plane) {
		return null;
	}

	/**
	 * 
	 * @return new settings
	 */
	public Settings newSettings() {
		return new Settings(2);
	}

	/**
	 * Update font sizes of all components to match current GUI font size
	 */
	public void resetFonts() {
		app.getFontManager().setFontSize(app.getGUIFontSize());
		if (app.euclidianView != null) {
			app.euclidianView.updateFonts();
		}

		if (app.getGuiManager() != null) {
			app.getGuiManager().updateFonts();
			if (app.hasEuclidianView2(1)) {
				app.getEuclidianView2(1).updateFonts();
			}
		}
	}

	/**
	 * 
	 * @return true if some view for plane exists
	 */
	public boolean hasEuclidianViewForPlane() {
		return false;
	}

	/**
	 * add to views for plane (if any)
	 * 
	 * @param geo
	 *            geo
	 */
	public void addToViewsForPlane(GeoElement geo) {
		// implemented in App3DCompanion
	}

	/**
	 * remove to views for plane (if any)
	 * 
	 * @param geo
	 *            geo
	 */
	public void removeFromViewsForPlane(GeoElement geo) {
		// implemented in App3DCompanion
	}

}
