package org.geogebra.common.jre.headless;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;

public class GBoxC extends GBox {

	public GBoxC(EuclidianController ec) {

	}

	@Override
	public void add(AutoCompleteTextField textField) {

	}

	@Override
	public void setVisible(boolean isVisible) {

	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public void setBounds(GRectangle labelRectangle) {

	}

	@Override
	public GRectangle getBounds() {
		return null;
	}

	@Override
	public void revalidate() {

	}
}
