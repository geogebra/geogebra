package geogebra.common.javax.swing;

import geogebra.common.awt.Dimension;
import geogebra.common.awt.Rectangle;
import geogebra.common.gui.inputfield.AutoCompleteTextField;

public abstract class Box {

	public abstract void add(JLabel label);

	public abstract void add(AutoCompleteTextField textField);

	public abstract void setVisible(boolean isVisible);

	public abstract void setBounds(Rectangle labelRectangle);

	public abstract Dimension getPreferredSize();

	public abstract Rectangle getBounds();

	//public abstract Box createHorizontalBox();

}
