package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Used for plot panel and for 3D
 *
 */
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

	@Override
	public AbsolutePanel getAbsolutePanel() {
		return this;
	}

	@Override
	public Panel getEuclidianPanel() {
		return this;
	}

	@Override
	public Canvas getCanvas() {
		return canvas;
	}

	@Override
	public EuclidianView getEuclidianView() {

		return ev;
	}

	@Override
	public void onResize() {
		// no resizing
	}

	@Override
	public void deferredOnResize() {
		// no resizing
	}

	@Override
	public void updateNavigationBar() {
		// TODO Auto-generated method stub

	}

}