package org.geogebra.web.html5.javax.swing;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.AbstractJComboBox;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.javax.swing.GLabel;
import org.geogebra.common.main.App;
import org.geogebra.ggbjdk.java.awt.geom.Rectangle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class GBoxW extends org.geogebra.common.javax.swing.GBox {

	private HorizontalPanel impl;
	private EuclidianController style;

	public GBoxW(EuclidianController style) {
		this.style = style;
		impl = new HorizontalPanel();
		impl.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	}

	public static HorizontalPanel getImpl(GBox box) {
		if (!(box instanceof GBoxW)) {
			return null;
		}
		return ((GBoxW) box).impl;
	}

	@Override
	public void add(GLabel label) {
		impl.add(GLabelW.getImpl((GLabelW) label));

	}

	@Override
	public void add(AutoCompleteTextField textField) {
		impl.add((org.geogebra.web.html5.gui.inputfield.AutoCompleteTextFieldW) textField);

	}

	@Override
	public void setVisible(boolean isVisible) {
		impl.setVisible(isVisible);
	}

	@Override
	public void setBounds(GRectangle rect) {
		App.debug("implementation needed - just finishing"); // TODO
		impl.setWidth(rect.getWidth() + "");
		impl.setHeight(rect.getHeight() + "");

		if (impl.getParent() instanceof AbsolutePanel) {
			((AbsolutePanel) (impl.getParent())).setWidgetPosition(impl,
			        (int) rect.getMinX(), (int) rect.getMinY());
		}

	}

	@Override
	public GDimension getPreferredSize() {
		return new org.geogebra.web.html5.awt.GDimensionW(impl.getOffsetWidth(),
		        impl.getOffsetHeight());
	}

	@Override
	public GRectangle getBounds() {
		int left = impl.getAbsoluteLeft();
		int top = impl.getAbsoluteTop();

		if (impl.getParent() != null) {
			left -= impl.getParent().getAbsoluteLeft();
			top -= impl.getParent().getAbsoluteTop();
		}

		if (style != null && style.getEnvironmentStyle() != null) {
			left = (int) (left * (1 / style.getEnvironmentStyle().getScaleX()));
			top = (int) (top * (1 / style.getEnvironmentStyle().getScaleY()));
		} else {
			App.debug("ec null");
		}

		return new Rectangle(left, top, impl.getOffsetWidth(),
		        impl.getOffsetHeight());
	}

	@Override
	public void validate() {
	}

	@Override
	public void add(AbstractJComboBox comboBox) {
		impl.add(GComboBoxW.getImpl(comboBox));
	}

	@Override
	public void revalidate() {
		// TODO Auto-generated method stub

	}

}
