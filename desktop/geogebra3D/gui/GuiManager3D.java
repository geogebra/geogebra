package geogebra3D.gui;

import geogebra.common.euclidian.EuclidianViewInterfaceCommon;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.euclidian.EuclidianViewD;
import geogebra.euclidianND.EuclidianViewND;
import geogebra.gui.ContextMenuChooseGeoD;
import geogebra.gui.ContextMenuGeoElementD;
import geogebra.gui.GuiManagerD;
import geogebra.gui.view.algebra.AlgebraControllerD;
import geogebra.gui.view.algebra.AlgebraViewD;
import geogebra.main.AppD;
import geogebra3D.App3D;
import geogebra3D.euclidianFor3D.EuclidianControllerFor3D;
import geogebra3D.euclidianFor3D.EuclidianViewFor3D;
import geogebra3D.gui.dialogs.DialogManager3D;
import geogebra3D.gui.layout.panels.EuclidianDockPanel3D;
import geogebra3D.gui.view.algebra.AlgebraView3D;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

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
	 * @param app application
	 */
	public GuiManager3D(AppD app) {
		super(app);
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		
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
		EuclidianDockPanel3D panel = new EuclidianDockPanel3D(getApp());
		getLayout().registerPanel(panel);
	}
	
	
	//////////////////////////////
	// ACTIONS
	//////////////////////////////
	
	@Override
	protected boolean initActions() {
		
		if (!super.initActions())
			return false;
		showAxes3DAction = new AbstractAction(getApp().getMenu("Axes"),
				(getApp()).getImageIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle axes
				((App3D) getApp()).toggleAxis3D();
				//getApp().getEuclidianView().repaint();
				getApp().storeUndoInfo();
				getApp().updateMenubar();
				
			}
		};

		showGrid3DAction = new AbstractAction(getApp().getMenu("Grid"),
				getApp().getImageIcon("grid.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle grid
				((App3D) getApp()).toggleGrid3D();
				//getApp().getEuclidianView().repaint();
				getApp().storeUndoInfo();
				getApp().updateMenubar();
				
			}
		};
		
		showPlaneAction = new AbstractAction(getApp().getMenu("Plane"),
				(getApp()).getImageIcon("plane.gif")) {
			private static final long serialVersionUID = 1L;

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
	
	
	
	//////////////////////////////
	// POPUP MENU
	//////////////////////////////
	
	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 */
	/*
	public void showDrawingPadPopup(Component invoker, Point p) {
		// clear highlighting and selections in views		
		app.getEuclidianView().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				app, p.x, p.y);
		popupMenu.show(invoker, p.x, p.y);
	}
	*/
	
	/**
	 * Displays the zoom menu at the position p in the coordinate space of
	 * euclidianView
	 * @param view view
	 * @param p zoom point
	 */
	public void showDrawingPadPopup3D(EuclidianViewInterfaceCommon view, geogebra.common.awt.GPoint p) {
		// clear highlighting and selections in views		
		((App3D) getApp()).getEuclidianView3D().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				getApp(), p.x, p.y);
		popupMenu.getWrappedPopup().show(((EuclidianViewND) view).getJPanel(), p.x, p.y);
	}
	
	
	
	/**
	 * Displays the popup menu for geo at the position p in the coordinate space
	 * of the component invoker
	 * @param selectedGeos first geos
	 * @param geos list of geos
	 * @param view view calling
	 * @param p place to show the popup menue
	 */
	@Override
	public void showPopupChooseGeo(ArrayList<GeoElement> selectedGeos,
			ArrayList<GeoElement> geos, EuclidianViewND view,
			geogebra.common.awt.GPoint p) {
		
		if (selectedGeos == null || selectedGeos.get(0) == null)
			return;

		// clear highlighting and selections in views
		getApp().getActiveEuclidianView().resetMode();
		
		Component invoker = view.getJPanel();

		Point screenPos = (invoker == null) ? new Point(0, 0) : invoker
				.getLocationOnScreen();
		screenPos.translate(p.x, p.y);


		ContextMenuGeoElementD popupMenu = new ContextMenuChooseGeoD(getApp(), view, selectedGeos, geos, screenPos, p);
		popupMenu.getWrappedPopup().show(invoker, p.x, p.y);


	}


	
	
	//////////////////////////////
	// ALGEBRA VIEW
	//////////////////////////////
	
	@Override
	protected AlgebraViewD newAlgebraView(AlgebraControllerD algc){
		return new AlgebraView3D(algc);
	}
	
	@Override
	protected EuclidianViewD newEuclidianView(boolean[] showAxis, boolean showGrid, int id){
		return new EuclidianViewFor3D(new EuclidianControllerFor3D(kernel), showAxis, showGrid, id, null);
	}
	
	
	
	
	//////////////////////////////
	// 3D VIEW
	//////////////////////////////
	
	@Override
	public void attachView(int viewId) {
		
		switch (viewId) {
		case App.VIEW_EUCLIDIAN3D:
			App.debug("TODO: attach 3D view?");
			break;
		default: 
			/*
			if (viewId>=App.VIEW_EUCLIDIAN_FOR_PLANE_START && viewId<=App.VIEW_EUCLIDIAN_FOR_PLANE_END)
				App.debug("TODO: attach view for plane?");
			else
			*/
				super.attachView(viewId);
			break;
		}
	}
	
	/*
	@Override
	protected PropertiesViewD newPropertiesViewD(AppD appD){
		return new PropertiesView3D(appD);
	}
	*/
}
