package geogebra3D.gui;

import geogebra.euclidian.EuclidianView;
import geogebra.gui.GuiManager;
import geogebra.gui.InputDialog;
import geogebra.gui.InputDialogCirclePointRadius;
import geogebra.gui.OptionsDialog;
import geogebra.gui.GuiManager.NumberInputHandler;
import geogebra.gui.layout.Layout;
import geogebra.gui.layout.panels.Euclidian2DockPanel;
import geogebra.gui.layout.panels.EuclidianDockPanel;
import geogebra.gui.toolbar.Toolbar;
import geogebra.gui.view.algebra.AlgebraController;
import geogebra.gui.view.algebra.AlgebraView;
import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoPoint;
import geogebra.kernel.kernelND.GeoDirectionND;
import geogebra.kernel.kernelND.GeoPointND;
import geogebra.main.Application;
import geogebra3D.Application3D;
import geogebra3D.euclidianForPlane.EuclidianViewForPlane;
import geogebra3D.gui.layout.panels.EuclidianDockPanel3D;
import geogebra3D.gui.view.algebra.AlgebraView3D;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Extending DefaultGuiManager class for 3D
 * 
 * @author matthieu
 *
 */
public class GuiManager3D extends GuiManager {

	
	private AbstractAction showAxes3DAction, showGrid3DAction, showPlaneAction;
	
	/** 
	 * default constructor
	 * @param app
	 */
	public GuiManager3D(Application app) {
		super(app);
		javax.swing.JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	}
	
	protected void createLayout(){
		setLayout(new Layout());
	}
	
	/**
	 * Add 3D euclidian view to layout.
	 */
	protected void initLayoutPanels() {
		super.initLayoutPanels();
		EuclidianDockPanel3D panel = new EuclidianDockPanel3D(app);
		getLayout().registerPanel(panel);
	}
	
	
	//////////////////////////////
	// ACTIONS
	//////////////////////////////
	
	
	protected boolean initActions() {
		
		if (!super.initActions())
			return false;
		showAxes3DAction = new AbstractAction(app.getMenu("Axes"),
				app.getImageIcon("axes.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle axes
				((Application3D) app).toggleAxis3D();
				//app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};

		showGrid3DAction = new AbstractAction(app.getMenu("Grid"),
				app.getImageIcon("grid.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle grid
				((Application3D) app).toggleGrid3D();
				//app.getEuclidianView().repaint();
				app.storeUndoInfo();
				app.updateMenubar();
				
			}
		};
		
		showPlaneAction = new AbstractAction(app.getMenu("Plane"),
				app.getImageIcon("plane.gif")) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				// toggle plane
				((Application3D) app).togglePlane();
				app.storeUndoInfo();
				app.updateMenubar();
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
	 */
	public void showDrawingPadPopup3D(Component invoker, Point p) {
		// clear highlighting and selections in views		
		((Application3D) app).getEuclidianView3D().resetMode();
		
		// menu for drawing pane context menu
		ContextMenuGraphicsWindow3D popupMenu = new ContextMenuGraphicsWindow3D(
				app, p.x, p.y);
		popupMenu.show(invoker, p.x, p.y);
	}
	
	

	
	//////////////////////////////
	// ALGEBRA VIEW
	//////////////////////////////
	
	protected AlgebraView newAlgebraView(AlgebraController algc){
		return new AlgebraView3D(algc);
	}
	
	
	//////////////////////////////
	// INPUT HANDLERS
	//////////////////////////////
	
	/**
	 * @param title 
	 * @param geoPoint 
	 * @param forAxis 
	 * 
	 */
	public void showNumberInputDialogCirclePointDirectionRadius(String title, GeoPointND geoPoint, GeoDirectionND forAxis) {

		NumberInputHandler handler = new NumberInputHandler();
		InputDialog id = new InputDialogCirclePointDirectionRadius(app, title, handler, geoPoint, forAxis, kernel);
		id.setVisible(true);

	}
	
	public void showNumberInputDialogCirclePointRadius(String title, GeoPointND geoPoint1,  EuclidianView view) {

		if (((GeoElement) geoPoint1).isGeoElement3D() || (view instanceof EuclidianViewForPlane))
			//create a circle parallel to plane containing the view
			showNumberInputDialogCirclePointDirectionRadius(title, geoPoint1, view.getDirection());
		else
			//create 2D circle
			super.showNumberInputDialogCirclePointRadius(title, geoPoint1, view);
		
	}
	
	
	/**
	 * 
	 * @param title
	 * @param geoPoint
	 */
	public void showNumberInputDialogSpherePointRadius(String title, GeoPointND geoPoint) {

		NumberInputHandler handler = new NumberInputHandler();
		InputDialog id = new InputDialogSpherePointRadius(app, title, handler, geoPoint, kernel);
		id.setVisible(true);

	}
	
	

	protected OptionsDialog newOptionsDialog(){
		return new OptionsDialog3D(app);
	}


}
