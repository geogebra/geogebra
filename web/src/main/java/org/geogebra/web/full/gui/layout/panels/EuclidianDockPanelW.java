package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianDockPanelW extends EuclidianDockPanelWAbstract
		implements EuclidianPanelWAbstract {

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null; // static foreground
	Canvas eviewBg = null; // static background
	private boolean doubleCanvas = false;

	/**
	 * This constructor is used by the Application
	 * and by the other constructor
	 * 
	 * @param stylebar
	 *            (is there stylebar?)
	 * @param doubleCanvas
	 *            if application uses double canvas or not.
	 */
	public EuclidianDockPanelW(boolean stylebar, boolean doubleCanvas) {
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

		this.doubleCanvas = doubleCanvas;
		if (stylebar) {
			component = loadComponent();
		} else {
			component = loadComponent();
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
		this(stylebar, application.has(Feature.MOW_DOUBLE_CANVAS));
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
		if (Browser.isiOS() && app.has(Feature.VOICEOVER_APPLETS)) {
			new VoiceoverTabber(app, getCanvas()).add(euclidianpanel);
		}
	}
	
	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			if (doubleCanvas) {
				eviewBg = Canvas.createIfSupported();
				if (eviewBg != null) {
					eviewBg.addStyleName("mowBackground");
					eview1.addStyleName("mowForeground");
				}
				addCanvas(eviewBg);
				addCanvas(eview1);
				euclidianpanel.addStyleName("mowDoubleCanvas");
			} else {
				addCanvas(eview1);
			}
			// if (Browser.isiOS() && app.has(Feature.VOICEOVER_APPLETS)) {
			// // new VoiceoverTabber(app, getCanvas()).add(euclidianpanel);
			// }
		}
		return euclidianpanel;
	}

	private void addCanvas(Canvas c) {
		if (c != null) {
		// c.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		// c.getElement().getStyle().setZIndex(0);
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
	public Canvas getBackgroundCanvas() {
		return eviewBg;
	}

	@Override
	public EuclidianPanel getEuclidianPanel() {
		return euclidianpanel;
	}

	public void remove(Widget w) {
		euclidianpanel.remove(w);
	}

	public EuclidianDockPanelW getEuclidianView1Wrapper() {
		return this;
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
		if (app != null && app.has(Feature.DYNAMIC_STYLEBAR)) {
			return MaterialDesignResources.INSTANCE.gear();
		}
		return getResources().styleBar_graphicsView();
	}
}
