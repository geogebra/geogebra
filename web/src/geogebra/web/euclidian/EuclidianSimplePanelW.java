package geogebra.web.euclidian;

import geogebra.common.euclidian.EuclidianView;
import geogebra.common.main.App;
import geogebra.web.main.AppW;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class EuclidianSimplePanelW extends AbsolutePanel implements EuclidianPanelWAbstract {

	AppW app;
	int oldHeight = 0;
	int oldWidth = 0;

	Canvas eview1 = null;// static foreground

	/**
	 * This constructor is used by the Application
	 * and by the other constructor
	 * 
	 * @param stylebar (is there stylebar?)
	 */
	public EuclidianSimplePanelW(boolean stylebar) {
		super();

		loadComponent();
	}

	/**
	 * This constructor is used by the applet
	 * @param application
	 * @param stylebar
	 */
	public EuclidianSimplePanelW(AppW application, boolean stylebar) {
		this(stylebar);
		app = application;
	}

	protected Widget loadComponent() {
		eview1 = Canvas.createIfSupported();
		eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		eview1.getElement().getStyle().setZIndex(0);
		add(eview1);

		return this;
	}
	
	public Canvas getCanvas() {
	    return eview1;
    }

	public Panel getEuclidianPanel() {
	    return this;
    }

	public void attachApp(App app) {
		this.app = (AppW)app;
	}

	public EuclidianSimplePanelW getEuclidianView1Wrapper() {
		return this;
	}

	public AbsolutePanel getAbsolutePanel() {
		return this;
	}

	public EuclidianView getEuclidianView() {
		if (app != null)
			return app.getEuclidianView1();
		return null;
	}

	public void onResize() {
		// no call
    }

	public void deferredOnResize() {
		// no call
	}

	public void updateNavigationBar() { }
}
