package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW extends EuclidianDockPanelWAbstract
		implements EuclidianPanelWAbstract {

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null; // static foreground

	/**
	 * This constructor is used by the Application
	 * and by the other constructor
	 * 
	 * @param stylebar
	 *            (is there stylebar?)
	 */
	public EuclidianDockPanelW(boolean stylebar) {
		super(
				App.VIEW_EUCLIDIAN,	// view id 
				"DrawingPad", 				// view title
				null,
				stylebar, // style bar?
				true, // zoom panel?
				5,							// menu order
				'1' // ctrl-shift-1
			);

		//TODO: temporary fix to make applets work until
		// dockpanels works for applets
		component = loadComponent();
		if (!stylebar) {
			buildDockPanel();
		}
	}
	
	/**
	 * This constructor is used by the applet
	 * 
	 * @param application
	 *            application
	 * @param stylebar
	 *            whether to use stylebar
	 */
	public EuclidianDockPanelW(AppW application, boolean stylebar) {
		this(stylebar);
		attachApp(application);
	}

	/**
	 * @param application
	 *            application
	 */
	public void attachApp(AppW application) {
		app = application;

		// GuiManager can be null at the startup of the application,
		// but then the addNavigationBar method will be called explicitly.
		if (app.getGuiManager() != null
				&& app.showConsProtNavigation(App.VIEW_EUCLIDIAN)) {
			addNavigationBar();
		}
	}

	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			TestHarness.setAttr(eview1, "euclidianView");
			addCanvas(eview1);
		}
		return euclidianpanel;
	}

	private void addCanvas(Canvas c) {
		if (c != null) {
			euclidianpanel.getAbsolutePanel().add(c);
		}
	}

	@Override
	protected Widget loadStyleBar() {
		if (espanel == null) {
			espanel = app.getEuclidianView1().getStyleBar();
		}
		return (Widget) espanel;
	}

	@Override
	public Canvas getCanvas() {
		return eview1;
	}

	@Override
	public EuclidianPanel getEuclidianPanel() {
		return euclidianpanel;
	}

	/**
	 * @param w
	 *            widget to be removed
	 */
	public void remove(Widget w) {
		euclidianpanel.remove(w);
	}

	@Override
	public EuclidianView getEuclidianView() {
		if (app != null) {
			return app.getEuclidianView1();
		}
		return null;
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_graphics();
	}

	@Override
	public void calculateEnvironment() {
		app.getEuclidianView1().getEuclidianController().calculateEnvironment();
	}

	@Override
	public void resizeView(int width, int height) {
		app.ggwGraphicsViewDimChanged(width, height);
	}

	@Override
	protected ResourcePrototype getViewIcon() {
		return getResources().styleBar_graphicsView();
	}
}
