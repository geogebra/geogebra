package org.geogebra.web.full.gui.layout.panels;

import org.geogebra.common.euclidian.EuclidianStyleBar;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.client.Style;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.Widget;

/**
 * This class may be redundant since EuclidianDockPanelW, but GeoGebra Desktop
 * also uses two different classes for similar purposes, so its behaviour was
 * imitated here.
 * 
 * @author arpad
 */

public class Euclidian2DockPanelW extends EuclidianDockPanelWAbstract
		implements EuclidianPanelWAbstract {

	EuclidianStyleBar espanel;
	EuclidianPanel euclidianpanel;

	Canvas eview1 = null; // static foreground
	private int idx;

	/**
	 * @param stylebar
	 *            allow stylebar?
	 * @param idx
	 *            index for app.getEuclidianView2(idx)
	 */
	public Euclidian2DockPanelW(boolean stylebar, int idx) {
		super(App.VIEW_EUCLIDIAN2, null, stylebar, false);

		this.idx = idx;
		// copied here from loadComponent
		setEmbeddedSize(300);

		// this should execute when DockPanelW.register is not called
		if (!stylebar) {
			buildDockPanel();
		}
	}

	@Override
	protected Widget loadComponent() {
		if (euclidianpanel == null) {
			euclidianpanel = new EuclidianPanel(this);
			eview1 = Canvas.createIfSupported();
			eview1.getElement().getStyle().setPosition(Style.Position.RELATIVE);
			eview1.getElement().getStyle().setZIndex(0);
			euclidianpanel.getAbsolutePanel().add(eview1);
		}

		// Euclidian2DockPanelW.loadComponent will be called lazy,
		// so it is this place where EuclidianView 2 should be inited
		// in EuclidianDockPanelW, EuclidianView is created automatically
		if (app != null) {
			app.getEuclidianView2(1);
		}

		return euclidianpanel;
	}

	@Override
	public SVGResource getViewIcon() {
		return getResources().styleBar_graphics2View();
	}

	@Override
	protected Widget loadStyleBar() {
		if (espanel == null) {
			espanel = app.getEuclidianView2(idx).getStyleBar();
		}

		return (Widget) espanel;
	}

	@Override
	public Canvas getCanvas() {
		return eview1;
	}

	public void remove(Widget w) {
		euclidianpanel.remove(w);
	}

	public Euclidian2DockPanelW getEuclidianView2Wrapper() {
		return this;
	}

	@Override
	public EuclidianPanel getEuclidianPanel() {
		return euclidianpanel;
	}

	@Override
	public EuclidianView getEuclidianView() {
		if (app != null && app.hasEuclidianView2(idx)) {
			return app.getEuclidianView2(idx);
		}
		return null;
	}

	@Override
	public ResourcePrototype getIcon() {
		return getResources().menu_icon_graphics2();
	}

	@Override
	public void calculateEnvironment() {
		if (app.hasEuclidianView2EitherShowingOrNot(1)) {
			app.getEuclidianView2(1).getEuclidianController()
					.calculateEnvironment();
		}
	}

	@Override
	public void resizeView(int width, int height) {
		app.ggwGraphicsView2DimChanged(width, height);
	}

}
