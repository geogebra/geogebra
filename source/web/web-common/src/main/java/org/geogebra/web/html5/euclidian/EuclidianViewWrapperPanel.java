/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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