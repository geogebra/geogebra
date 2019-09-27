package org.geogebra.test.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;

public class GBoxC extends GBox {

	@Override
	public void add(AutoCompleteTextField textField) {
		// not needed for tests
	}

	@Override
	public void setVisible(boolean isVisible) {
		// not needed for tests
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setBounds(GRectangle labelRectangle) {
		// not needed for tests
	}

	@Override
	public GRectangle getBounds() {
		return null;
	}

	@Override
	public void revalidate() {
		// not needed for tests
	}
}
