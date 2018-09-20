package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.util.debug.Log;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;
import org.geogebra.web.html5.euclidian.EnvironmentStyleW;
import org.geogebra.web.html5.euclidian.IsEuclidianController;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * panel for postioning widgets in Graphics
 *
 */
public class GBoxW extends GBox {

	private SimplePanel impl;
	private EuclidianController ec;

	/**
	 * @param ec
	 *            euclidian controller
	 */
	public GBoxW(EuclidianController ec) {
		this.ec = ec;
		impl = new SimplePanel();
		// impl.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		impl.getElement().getStyle().setZIndex(50);
	}

	/**
	 * @param box
	 *            box
	 * @return underlying panel
	 */
	public static SimplePanel getImpl(GBox box) {
		if (!(box instanceof GBoxW)) {
			return null;
		}
		return ((GBoxW) box).impl;
	}

	@Override
	public void add(AutoCompleteTextField textField) {
		impl.setWidget((AutoCompleteTextFieldW) textField);
	}

	@Override
	public void setVisible(boolean isVisible) {
		impl.setVisible(isVisible);
	}

	@Override
	public void setBounds(GRectangle rect) {
		impl.setWidth(rect.getWidth() + "");
		impl.setHeight(rect.getHeight() + "");

		if (impl.getParent() instanceof AbsolutePanel) {
			((AbsolutePanel) (impl.getParent())).setWidgetPosition(impl,
					(int) rect.getMinX(), (int) rect.getMinY());
		}
	}

	@Override
	public GRectangle getBounds() {
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

		return new Rectangle(left, top, impl.getOffsetWidth(),
				impl.getOffsetHeight());
	}

	@Override
	public void revalidate() {
		// not needed in Web
	}

	@Override
	public boolean isVisible() {
		return impl.isVisible();
	}

}
