package org.geogebra.web.html5.euclidian;

import org.geogebra.common.euclidian.EuclidianView;
import org.gwtproject.canvas.client.Canvas;
import org.gwtproject.dom.style.shared.Position;
import org.gwtproject.user.client.ui.AbsolutePanel;
import org.gwtproject.user.client.ui.Panel;

/**
 * Used for plot panel and for 3D
 *
 */
public class EuclidianViewWrapperPanel extends AbsolutePanel implements
        EuclidianPanelWAbstract {
	/** canvas */
	private Canvas canvas;
	private EuclidianView ev;

	/**
	 * @param ev
	 *            view wrapped in this panel
	 */
	public EuclidianViewWrapperPanel(EuclidianView ev) {
		super();
		this.ev = ev;
		canvas = createCanvas();
		if (canvas != null) {
			canvas.getElement().getStyle()
					.setPosition(Position.RELATIVE);
			canvas.getElement().getStyle().setZIndex(0);
			add(canvas);
		}
	}

	/**
	 * create the canvas
	 * 
	 * @return Canvas widget
	 */
	protected Canvas createCanvas() {
		return Canvas.createIfSupported();
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

	@Override
	public void reset() {
		// not needed
	}

	@Override
	public void enableZoomPanelEvents(boolean enable) {
		// not needed
	}

}