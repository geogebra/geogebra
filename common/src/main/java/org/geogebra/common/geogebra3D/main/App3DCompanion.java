package org.geogebra.common.geogebra3D.main;

import java.util.ArrayList;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianController3DForExport;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3D;
import org.geogebra.common.geogebra3D.euclidian3D.EuclidianView3DForExport;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.Renderer;
import org.geogebra.common.geogebra3D.euclidian3D.openGL.RendererForExport;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.Format;
import org.geogebra.common.geogebra3D.euclidian3D.printer3D.FormatCollada;
import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.kernel3D.GeoFactory3D;
import org.geogebra.common.geogebra3D.kernel3D.Kernel3D;
import org.geogebra.common.geogebra3D.main.settings.EuclidianSettingsForPlane;
import org.geogebra.common.gui.dialog.Export3dDialogInterface;
import org.geogebra.common.gui.dialog.options.model.AxisModel;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.kernel.GeoFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.commands.CommandsConstants;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.AppCompanion;
import org.geogebra.common.main.DialogManager;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.common.main.settings.EuclidianSettings3D;
import org.geogebra.common.main.settings.Settings;
import org.geogebra.common.plugin.Geometry3DGetter;

/**
 * 
 * @author mathieu
 *
 *         Companion for 3D application
 */
public abstract class App3DCompanion extends AppCompanion {
	// id of the first view
	private int viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	/** view for plane companions */
	protected ArrayList<EuclidianViewForPlaneCompanion> euclidianViewForPlaneCompanionList;

	private EuclidianViewForPlaneCompanion euclidianViewForPlaneCompanion;

	/**
	 * Constructor
	 * 
	 * @param app
	 *            application
	 */
	public App3DCompanion(App app) {
		super(app);
	}

	@Override
	public Kernel newKernel() {

		if (app.is3DViewEnabled()) {
			return new Kernel3D(app, new GeoFactory3D());
		}

		return new Kernel(app, new GeoFactory());
	}

	@Override
	protected boolean tableVisible(int table) {
		return super.tableVisible(table)
				|| (table == CommandsConstants.TABLE_3D && app.areCommands3DEnabled());
	}

	// ///////////////////////////////
	// EUCLIDIAN VIEW FOR PLANE
	// ///////////////////////////////

	/**
	 * add euclidian views for plane settings
	 * 
	 * @param sb
	 *            string builder
	 * @param asPreference
	 *            save as preference flag
	 */
	public void addCompleteUserInterfaceXMLForPlane(StringBuilder sb,
			boolean asPreference) {
		if (euclidianViewForPlaneCompanionList != null) {
			for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
				vfpc.getView().getXML(sb, asPreference);
			}
		}
	}

	@Override
	public void getEuclidianViewXML(StringBuilder sb, boolean asPreference) {
		super.getEuclidianViewXML(sb, asPreference);

		if (app.isEuclidianView3Dinited()) {
			// TODO it would be cleaner to use EuclidianSettings here instead
			app.getEuclidianView3D().getXML(sb, asPreference);
		}

		if (euclidianViewForPlaneCompanionList != null) {
			for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
				vfpc.getView().getXML(sb, asPreference);
			}
		}

	}

	/**
	 * create new euclidian view for plane
	 * 
	 * @param plane
	 *            plane
	 * @param evSettings
	 *            settings
	 * @param panelSettings
	 *            whether to set the visibility and size of the panel
	 * @return view companion
	 */
	protected abstract EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
			ViewCreator plane, EuclidianSettings evSettings,
			boolean panelSettings);

	@Override
	public EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
			ViewCreator plane, boolean panelSettings) {
		// create new view for plane and controller
		Settings settings = app.getSettings();
		String name = plane.getLabelSimple();
		EuclidianSettings evSettings = settings.getEuclidianForPlane(name);
		if (evSettings == null) {
			evSettings = new EuclidianSettingsForPlane(app);
			evSettings.setShowGridSetting(false);
			evSettings.setShowAxes(false, false);
			settings.setEuclidianSettingsForPlane(name, evSettings);
		}
		euclidianViewForPlaneCompanion = createEuclidianViewForPlane(plane,
				evSettings, panelSettings);
		evSettings.addListener(euclidianViewForPlaneCompanion.getView());
		euclidianViewForPlaneCompanion.getView().updateFonts();
		euclidianViewForPlaneCompanion.addExistingGeos();

		// add it to list
		if (euclidianViewForPlaneCompanionList == null) {
			euclidianViewForPlaneCompanionList = new ArrayList<>();
		}
		euclidianViewForPlaneCompanionList.add(euclidianViewForPlaneCompanion);

		return euclidianViewForPlaneCompanion;
	}

	/**
	 * remove the view from the list
	 * 
	 * @param vfpc
	 *            view for plane companion
	 */
	public void removeEuclidianViewForPlaneFromList(
			EuclidianViewForPlaneCompanion vfpc) {
		euclidianViewForPlaneCompanionList.remove(vfpc);
		app.getSettings().removeEuclidianSettingsForPlane(
				vfpc.getPlane().getLabelSimple());
	}

	/**
	 * remove all euclidian views for plane
	 */
	public void removeAllEuclidianViewForPlane() {

		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}

		for (EuclidianViewForPlaneCompanion vfpc : euclidianViewForPlaneCompanionList) {
			vfpc.removeFromGuiAndKernel();
		}

		euclidianViewForPlaneCompanionList.clear();
		app.getSettings().clearEuclidianSettingsForPlane();

	}

	@Override
	public DockPanel createEuclidianDockPanelForPlane(int id, String plane) {

		GeoElement geo = app.getKernel().lookupLabel(plane);
		if (geo == null) {
			return null;
		}
		if (!(geo instanceof ViewCreator)) {
			return null;
		}

		ViewCreator vc = (ViewCreator) geo; // getViewCreator(id);
		vc.setEuclidianViewForPlane(createEuclidianViewForPlane(vc, false));
		return getPanelForPlane();
	}

	/**
	 * 
	 * @return current dockpanel for plane
	 */
	abstract public DockPanel getPanelForPlane();

	@Override
	public boolean hasEuclidianViewForPlane() {
		return euclidianViewForPlaneCompanionList != null
				&& euclidianViewForPlaneCompanionList.size() > 0;
	}

	@Override
	public boolean hasEuclidianViewForPlaneVisible() {
		if (!hasEuclidianViewForPlane()) {
			return false;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {

			if (c.isPanelVisible()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public EuclidianView getViewForPlaneVisible() {
		if (!hasEuclidianViewForPlane()) {
			return null;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {
			if (c.getView().isShowing()) {
				return c.getView();
			}
		}

		return null;

	}

	@Override
	public void addToViewsForPlane(GeoElement geo) {
		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {
			c.getView().add(geo);
		}
	}

	@Override
	public void removeFromViewsForPlane(GeoElement geo) {
		if (euclidianViewForPlaneCompanionList == null) {
			return;
		}

		for (EuclidianViewForPlaneCompanion c : euclidianViewForPlaneCompanionList) {
			c.getView().remove(geo);
		}
	}

	@Override
	public final void resetEuclidianViewForPlaneIds() {
		viewId = App.VIEW_EUCLIDIAN_FOR_PLANE_START;
	}

	/**
	 * Increment view ID and return the old one
	 * 
	 * @return next view ID
	 */
	public int incViewID() {
		return viewId++;
	}
	
	@Override
	public void setExport3D(Format format, boolean showDialog) {
		// try first with existing 3D view
		if (format.useSpecificViewForExport()) {
			int width = 0, height = 0;
			boolean use2d = true;
			if (app.isEuclidianView3Dinited()) {
				EuclidianView3DInterface view3D = app.getEuclidianView3D();
				if (view3D.isShowing()) {
					Renderer r = view3D.getRenderer();
					width = r.getWidth();
					height = r.getHeight();
					use2d = false;
				}
			}
			EuclidianSettings3D settings = (EuclidianSettings3D) app.getSettings().getEuclidian(3);
			if (use2d) {
				EuclidianView ev = app.getActiveEuclidianView();
				width = ev.getWidth();
				height = ev.getHeight();
				EuclidianSettings s2d = ev.getSettings();
				settings.setShowAxis(AxisModel.AXIS_X,
						s2d.getShowAxis(AxisModel.AXIS_X));
				settings.setShowAxis(AxisModel.AXIS_Y,
						s2d.getShowAxis(AxisModel.AXIS_Y));
				settings.setShowAxis(AxisModel.AXIS_Z, false);
				settings.setShowPlate(false);
				settings.setShowGridSetting(s2d.getShowGrid() && (s2d
						.getGridType() == EuclidianView.GRID_CARTESIAN
						|| s2d.getGridType() == EuclidianView.GRID_CARTESIAN_WITH_SUBGRID));
				double xscale = s2d.getXscale();
				double yscale = s2d.getYscale();
				double xmin = -s2d.getXZero() / xscale;
				double xmax = (width - s2d.getXZero()) / xscale;
				double ymin = -(height - s2d.getYZero()) / yscale;
				double ymax = s2d.getYZero() / yscale;
				settings.setXscale(s2d.getXscale());
				settings.setYscale(s2d.getYscale());
				settings.setZscale(s2d.getXscale());
				settings.updateOriginFromView(-(xmin + xmax) / 2,
						-(ymin + ymax) / 2, 0);
				settings.setYAxisVertical(true); // this way view height will be
													// used for clipping
			}
			EuclidianView3DForExport exportView3D = new EuclidianView3DForExport(
					new EuclidianController3DForExport(app), settings);
			if (width > 0) {
				RendererForExport renderer = (RendererForExport) exportView3D.getRenderer();
				renderer.setReduceForClipping(!use2d);
				renderer.setView(0, 0, width, height);
			}
			Export3dDialogInterface dialog = null;
			DialogManager dialogManager = app.getDialogManager();
			if (dialogManager != null) {
				dialog = dialogManager.getExport3dDialog(exportView3D);
			}
			if (dialog != null) {
				exportView3D.export3D(format, dialog);
				app.getKernel().detach(exportView3D);
			} else {
				StringBuilder export = exportView3D.export3D(format);
				app.getKernel().detach(exportView3D);
				app.exportStringToFile(format.getExtension(),
						export.toString(), showDialog);
			}
		} else {
			if (app.isEuclidianView3Dinited()) {
				EuclidianView3DInterface view3D = app.getEuclidianView3D();
				if (view3D.isShowing() && view3D.getRenderer().useShaders()) {
					view3D.setExport3D(format, showDialog);
					return;
				}
			}
			// use ad hoc 3D view for export
			EuclidianView3DForExport exportView3D = new EuclidianView3DForExport(
					new EuclidianController3DForExport(app),
					app.getSettings().getEuclidian(3));
			StringBuilder export = exportView3D.export3D(format);
			app.getKernel().detach(exportView3D);
			app.exportStringToFile(format.getExtension(), export.toString(), showDialog);
		}
	}

	@Override
	public String exportCollada(double xmin, double xmax, double ymin,
			double ymax, double zmin, double zmax, double xyScale,
			double xzScale, double xTickDistance, double yTickDistance,
			double zTickDistance) {
		// use ad hoc 3D view for export
		EuclidianSettings3D settings = new EuclidianSettings3D(app);
		EuclidianView3DForExport exportView3D = new EuclidianView3DForExport(
				new EuclidianController3DForExport(app), settings);
		Format format = new FormatCollada();
		exportView3D.updateSettings(xmin, xmax, ymin, ymax, zmin, zmax, xyScale,
				xzScale, xTickDistance, yTickDistance, zTickDistance);
		StringBuilder export = exportView3D.export3D(format);
		app.getKernel().detach(exportView3D);
		return export.toString();
	}

	@Override
	public boolean exportGeometry3D(Geometry3DGetter getter, double xmin,
			double xmax, double ymin, double ymax, double zmin, double zmax,
			double xyScale, double xzScale, double xTickDistance,
			double yTickDistance, double zTickDistance) {
		// use ad hoc 3D view for export
		EuclidianSettings3D settings = new EuclidianSettings3D(app);
		EuclidianView3DForExport exportView3D = new EuclidianView3DForExport(
				new EuclidianController3DForExport(app), settings);

		if (app.isEuclidianView3Dinited()) {
			EuclidianView3D view3D = (EuclidianView3D) app.getEuclidianView3D();
			EuclidianSettings3D viewSettings = view3D.getSettings();
			settings.setShowAxes(viewSettings.axisShown());
			if (xmin > xmax) { // use original view settings
				exportView3D.updateSettings(view3D.getXmin(), view3D.getXmax(),
						view3D.getYmin(), view3D.getYmax(), view3D.getZmin(),
						view3D.getZmax(),
						view3D.getYscale() / view3D.getXscale(),
						view3D.getZscale() / view3D.getXscale(),
						view3D.getAxisNumberingDistance(0),
						view3D.getAxisNumberingDistance(1),
						view3D.getAxisNumberingDistance(2));
			} else {
				exportView3D.updateSettings(xmin, xmax, ymin, ymax, zmin, zmax,
						xyScale, xzScale, xTickDistance, yTickDistance,
						zTickDistance);
			}
		} else {
			exportView3D.updateSettings(xmin, xmax, ymin, ymax, zmin, zmax,
					xyScale, xzScale, xTickDistance, yTickDistance,
					zTickDistance);
		}
		exportView3D.export3D(getter);
		app.getKernel().detach(exportView3D);
		return true;
	}

	public EuclidianViewForPlaneCompanion getEuclidianViewForPlaneCompanion() {
		return euclidianViewForPlaneCompanion;
	}

	@Override
	public void updateFonts3D() {
		if (app.isEuclidianView3Dinited()) {
			((EuclidianView) app.getEuclidianView3D()).updateFonts();
		}
		if (euclidianViewForPlaneCompanion != null) {
			euclidianViewForPlaneCompanion.getView().updateFonts();
		}
	}
}
