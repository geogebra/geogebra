package org.geogebra.common.main;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidianForPlane.EuclidianViewForPlaneCompanionInterface;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.plugin.Geometry3DGetter;

import com.google.j2objc.annotations.Weak;

/**
 * 
 * @author mathieu
 *
 *         Companion for application
 */
public class AppCompanion {
	/** application */
	@Weak
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
		return new Kernel(app, new GeoFactory());
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
				|| table == CommandsConstants.TABLE_3D
				|| table == CommandsConstants.TABLE_ENGLISH);
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
	public EuclidianViewForPlaneCompanionInterface createEuclidianViewForPlane(
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
	 * @param id
	 *            view ID
	 * @param plane
	 *            plane label
	 * @return new EuclidianDockPanelForPlane
	 */
	public DockPanel createEuclidianDockPanelForPlane(int id, String plane) {
		return null;
	}

	/**
	 * @deprecated The Settings instance is built by the SettingsBuilder in the App.
	 *
	 * @return new settings
	 */
	@Deprecated
	public Settings newSettings() {
		return app.newSettingsBuilder().newSettings();
	}

	/**
	 * 
	 * @return true if some view for plane exists
	 */
	public boolean hasEuclidianViewForPlane() {
		return false;
	}

	/**
	 * 
	 * @return true if some view for plane is visible
	 */
	public boolean hasEuclidianViewForPlaneVisible() {
		return false;
	}

	/**
	 * 
	 * @return a visible view for plane if one, or null
	 */
	public EuclidianView getViewForPlaneVisible() {
		return null;
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
	
	/**
	 * set export will be done on next 3D frame
	 * 
	 * @param format
	 *            export format
	 */
	public void setExport3D(Format format) {
		// implemented in App3DCompanion
	}

	/**
	 * 
	 * @param xmin
	 *            x min
	 * @param xmax
	 *            x max
	 * @param ymin
	 *            y min
	 * @param ymax
	 *            y max
	 * @param zmin
	 *            z min
	 * @param zmax
	 *            z max
	 * @param xyScale
	 *            x:y scale
	 * @param xzScale
	 *            x:z scale
	 * @param xTickDistance
	 *            x axis tick distance
	 * @param yTickDistance
	 *            y axis tick distance
	 * @param zTickDistance
	 *            z axis tick distance
	 * @return string describing model in collada (.dae) format
	 */
	public String exportCollada(double xmin, double xmax, double ymin,
			double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance) {
		// implemented in App3DCompanion
		return null;
	}

	/**
	 * export geometry to getter
	 * 
	 * @param getter
	 *            geometry getter
	 * 
	 * @param xmin
	 *            x min
	 * @param xmax
	 *            x max
	 * @param ymin
	 *            y min
	 * @param ymax
	 *            y max
	 * @param zmin
	 *            z min
	 * @param zmax
	 *            z max
	 * @param xyScale
	 *            x:y scale
	 * @param xzScale
	 *            x:z scale
	 * @param xTickDistance
	 *            x axis tick distance
	 * @param yTickDistance
	 *            y axis tick distance
	 * @param zTickDistance
	 *            z axis tick distance
	 */
	public void exportGeometry3D(Geometry3DGetter getter, double xmin,
			double xmax, double ymin, double ymax, double zmin, double zmax,
			double xyScale, double xzScale, double xTickDistance,
			double yTickDistance, double zTickDistance) {
		// implemented in App3DCompanion
	}

	public void updateFonts3D() {
		// no 3D views to update fonts
	}

}
