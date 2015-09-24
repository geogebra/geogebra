package org.geogebra.desktop.geogebra3D.gui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import org.geogebra.common.euclidian3D.EuclidianView3DInterface;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.euclidianND.EuclidianViewInterfaceDesktop;
import org.geogebra.desktop.geogebra3D.App3D;
import org.geogebra.desktop.geogebra3D.euclidianFor3D.EuclidianControllerFor3DD;
import org.geogebra.desktop.geogebra3D.euclidianFor3D.EuclidianViewFor3DD;
import org.geogebra.desktop.geogebra3D.gui.dialogs.DialogManager3D;
import org.geogebra.desktop.geogebra3D.gui.layout.panels.EuclidianDockPanel3DD;
import org.geogebra.desktop.geogebra3D.gui.view.algebra.AlgebraView3D;
import org.geogebra.desktop.geogebra3D.gui.view.properties.PropertiesView3DD;
import org.geogebra.desktop.gui.ContextMenuChooseGeoD;
import org.geogebra.desktop.gui.ContextMenuGeoElementD;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.view.algebra.AlgebraControllerD;
import org.geogebra.desktop.gui.view.algebra.AlgebraViewD;
import org.geogebra.desktop.gui.view.properties.PropertiesViewD;
import org.geogebra.desktop.main.AppD;

/**
 * Extending DefaultGuiManager class for 3D
 * 
 * @author matthieu
 * 
 */
public class GuiManager3D extends GuiManagerD {

	private AbstractAction showAxes3DAction, showGrid3DAction, showPlaneAction;

	/**
	 * default constructor
	 * 
	 * @param app
	 *            application
	 */
	public GuiManager3D(AppD app) {
		super(app);
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false); // popups
																			// over
																			// the
																			// 3D
																			// canvas
		javax.swing.ToolTipManager.sharedInstance().setLightWeightPopupEnabled(
				false); // tooltips over the 3D canvas

		dialogManagerFactory = new DialogManager3D.Factory();
	}

	@Override
	public void initialize() {
		super.initialize();
	}

	/**
	 * Add 3D euclidian view to layout.
	 */
	@Override
	protected void initLayoutPanels() {
		super.initLayoutPanels();
		if (app.supportsView(App.VIEW_EUCLIDIAN3D)) {
			getLayout().registerPanel(new EuclidianDockPanel3DD(getApp()));
		}
	}

	// ////////////////////////////
	// ACTIONS
	// ////////////////////////////

	@Override
	protected boolean initActions() {

		if (!super.initActions())
			return false;
		showAxes3DAction = new AbstractAction(getApp().getMenu("Axes"),
				(getApp()).getScaledIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle axes
				((App3D) getApp()).toggleAxis3D();
				// getApp().getEuclidianView().repaint();
				getApp().storeUndoInfo();
				getApp().updateMenubar();

			}
		};

		showGrid3DAction = new AbstractAction(getApp().getMenu("Grid"),
				getApp().getScaledIcon("grid.gif")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle grid
				((App3D) getApp()).toggleGrid3D();
				// getApp().getEuclidianView().repaint();
				getApp().storeUndoInfo();
				getApp().updateMenubar();

			}
		};

		showPlaneAction = new AbstractAction(getApp().getMenu("Plane"),
				(getApp()).getScaledIcon("plane.gif")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle plane
				((App3D) getApp()).togglePlane();
				getApp().storeUndoInfo();
				getApp().updateMenubar();
			}
		};

		return true;

	}

	public AbstractAction getShowAxes3DAction() {
		initActions();
		return showAxes3DAction;
	}

	public AbstractAction getShowGrid3DAction() {
		initActions();
		return showGrid3DAction;
	}

	public AbstractAction getShowPlaneAction() {
		initActions();
		return showPlaneAction;
	}

	// ////////////////////////////
	// POPUP MENU
	// ////////////////////////////

	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 */
	/*
	 * public void showDrawingPadPopup(Component invoker, Point p) { // clear
	 * highlighting and selections in views app.getEuclidianView().resetMode();
	 * 
	 * // menu for drawing pane context menu ContextMenuGraphicsWindow3D
	 * popupMenu = new ContextMenuGraphicsWindow3D( app, p.x, p.y);
	 * popupMenu.show(invoker, p.x, p.y); }
	 */

	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 * 
	 * @param view
	 *            view
	 * @param p
	 *            zoom point
	 */
	@Override
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view,
			org.geogebra.common.awt.GPoint p) {
		// clear highlighting and selections in views
		((App3D) getApp()).getEuclidianView3D().resetMode();

		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3DD popupMenu = new ContextMenuGraphicsWindow3DD(
				getApp(), p.x, p.y);
		popupMenu.getWrappedPopup().show(
				((EuclidianViewInterfaceDesktop) view).getJPanel(), p.x, p.y);
	}

	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 * 
	 * @param selectedGeos
	 *            first geos
	 * @param geos
	 *            list of geos
	 * @param view
	 *            view calling
	 * @param p
	 *            place to show the popup menue
	 */
	@Override
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianView view,
			org.geogebra.common.awt.GPoint p) {

		if (selectedGeos == null || selectedGeos.get(0) == null)
			return;

		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();

		Component invoker = ((EuclidianViewInterfaceDesktop) view).getJPanel();

		Point screenPos = (invoker == null) ? new Point(0, 0) : invoker
				.getLocationOnScreen();
		screenPos.translate(p.x, p.y);

		ContextMenuGeoElementD popupMenu = new ContextMenuChooseGeoD(getApp(),
				view, selectedGeos, geos, screenPos, p);
		popupMenu.getWrappedPopup().show(invoker, p.x, p.y);

	}

	// ////////////////////////////
	// ALGEBRA VIEW
	// ////////////////////////////

	@Override
	protected AlgebraViewD newAlgebraView(AlgebraControllerD algc) {
		return new AlgebraView3D(algc);
	}

	@Override
	protected EuclidianViewD newEuclidianView(boolean[] showAxis,
			boolean showGrid, int viewId) {

		EuclidianSettings settings = app.getSettings().getEuclidian(viewId);

		return new EuclidianViewFor3DD(new EuclidianControllerFor3DD(kernel),
				showAxis, showGrid, viewId, settings);
	}

	// ////////////////////////////
	// 3D VIEW
	// ////////////////////////////

	@Override
	public void attachView(int viewId) {

		if (App.isView3D(viewId)) {
			App.debug("TODO: attach 3D view?");
		} else {
			super.attachView(viewId);
		}
	}

	@Override
	protected PropertiesViewD newPropertiesViewD(AppD appD) {
		return new PropertiesView3DD(appD);
	}

	@Override
	public void setLabels() {

		super.setLabels();

		if (app.isEuclidianView3Dinited()) {
			EuclidianView3DInterface view = app.getEuclidianView3D();
			if (view != null && ((EuclidianView) view).hasStyleBar())
				((EuclidianView) view).getStyleBar().setLabels();

		}
	}

	@Override
	public boolean loadURL(String urlString, boolean suppressErrorMsg) {
		((App3DCompanion) app.getCompanion()).removeAllEuclidianViewForPlane();
		return super.loadURL(urlString, suppressErrorMsg);
	}
}
