package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EnvironmentStyleW;
import org.geogebra.web.html5.euclidian.IsEuclidianController;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * panel for postioning widgets in Graphics
 *
 */
public class Positioner {

	private SimplePanel impl;
	private EuclidianController ec;

	/**
	 * @param ec
	 *            euclidian controller
	 * @param impl
	 *            positioned panel
	 */
	public Positioner(EuclidianController ec, SimplePanel impl) {
		this.ec = ec;
		this.impl = impl;
	}

	/**
	 * @param left
	 *            pixels from the left
	 * @param top
	 *            pixels from the top
	 */
	public void setPosition(double left, double top) {
		Widget parent = impl.getParent();
		if (parent instanceof AbsolutePanel) {
			((AbsolutePanel) parent).setWidgetPosition(impl,
					(int) left, (int) top);
		}
	}

	/**
	 * @return top left corner
	 */
	public GPoint getPosition() {
		int left = impl.getAbsoluteLeft();
		int top = impl.getAbsoluteTop();

		if (impl.getParent() != null) {
			left -= impl.getParent().getAbsoluteLeft();
			top -= impl.getParent().getAbsoluteTop();
		}
		EnvironmentStyleW evs = ec == null ? null
				: ((IsEuclidianController) ec).getEnvironmentStyle();
		if (evs != null) {
			left = (int) (left * (1 / evs.getScaleX()));
			top = (int) (top * (1 / evs.getScaleY()));
		} else {
			Log.debug("ec null");
		}

		return new GPoint(left, top);
	}

}
