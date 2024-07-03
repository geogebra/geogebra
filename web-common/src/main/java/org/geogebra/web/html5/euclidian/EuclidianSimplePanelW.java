package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.core.client.Scheduler;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.dom.style.shared.Overflow;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.Panel;
import org.gwtproject.user.client.ui.RequiresResize;

/**
 * Euclidian panel for WebSimple
 *
 */
public class EuclidianSimplePanelW extends AbsolutePanel implements
        EuclidianPanelWAbstract, RequiresResize {

	private AppW app;
	private int oldHeight = 0;
	private int oldWidth = 0;

	private Canvas eview1 = null;
	private final ScheduledCommand onResizeCmd = this::onResize;

	/**
	 * This constructor is used by the applet
	 * 
	 * @param application
	 *            application
	 */
	public EuclidianSimplePanelW(AppW application) {
		super();
		loadComponent();
		app = application;
		getElement().setAttribute("role", "application");
	}

	private void loadComponent() {
		eview1 = Canvas.createIfSupported();
		eview1.getElement().getStyle().setPosition(Position.RELATIVE);
		eview1.getElement().getStyle().setZIndex(0);
		eview1.setWidth("1px");
		eview1.setHeight("1px");
		eview1.setCoordinateSpaceHeight(1);
		eview1.setCoordinateSpaceWidth(1);
		getElement().getStyle().setOverflow(Overflow.VISIBLE);
		add(eview1);

	}

	@Override
	public Canvas getCanvas() {
		return eview1;
	}

	@Override
	public Panel getEuclidianPanel() {
		return this;
	}

	/**
	 * @param app1
	 *            application
	 */
	public void attachApp(App app1) {
		this.app = (AppW) app1;
	}

	@Override
	public AbsolutePanel getAbsolutePanel() {
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
	public void onResize() {

		// This is probably not needed, but what if yes?

		if (app != null) {
			int h = getOffsetHeight();
			int w = getOffsetWidth();

			// exit if new size cannot be determined
			if (h <= 0 || w <= 0) {
				return;
			}

			if (h != oldHeight || w != oldWidth) {
				app.ggwGraphicsViewDimChanged(w, h);
				oldHeight = h;
				oldWidth = w;
			}
		}
	}

	@Override
	public void deferredOnResize() {

		// There is probably no need for deferred call here, but what if yes?

		Scheduler.get().scheduleDeferred(onResizeCmd);
		// onResize();
	}

	@Override
	public void updateNavigationBar() {
		// no navigation
	}

	@Override
	public void reset() {
		oldWidth = 0;
		oldHeight = 0;
	}

	@Override
	public void enableZoomPanelEvents(boolean enable) {
		if (app.getZoomPanel() != null) {
			app.getZoomPanel().setStyleName("pointerEventsNoneWhenDragging", !enable);
		}
	}

}
