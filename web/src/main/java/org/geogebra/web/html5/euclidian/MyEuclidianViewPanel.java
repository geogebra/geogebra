package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;

public class MyEuclidianViewPanel extends AbsolutePanel implements
        EuclidianPanelWAbstract {

	protected Canvas canvas;
	private EuclidianView ev;

	public MyEuclidianViewPanel(EuclidianView ev) {
		super();
		this.ev = ev;
		createCanvas();
		canvas.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		canvas.getElement().getStyle().setZIndex(0);
		add(canvas);

	}

	/**
	 * create the canvas
	 */
	protected void createCanvas() {
		canvas = Canvas.createIfSupported();
	}

	public AbsolutePanel getAbsolutePanel() {
		return this;
	}

	public Panel getEuclidianPanel() {
		return this;
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public EuclidianView getEuclidianView() {

		return ev;
	}

	public void onResize() {
		// ev.setCoordinateSpaceSizeDirectly(100, 100);
	}

	public void deferredOnResize() {
	}

	public void updateNavigationBar() {
		// TODO Auto-generated method stub

	}

}