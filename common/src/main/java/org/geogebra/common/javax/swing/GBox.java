package org.geogebra.common.javax.swing;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;

public abstract class GBox {

	public abstract void add(AutoCompleteTextField textField);

	public abstract void setVisible(boolean isVisible);

	public abstract void setBounds(GRectangle labelRectangle);

	public abstract GRectangle getBounds();


	public abstract void revalidate();
}
