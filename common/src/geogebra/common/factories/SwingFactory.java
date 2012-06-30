package geogebra.common.factories;

import geogebra.common.euclidian.Drawable;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.GBox;
import geogebra.common.javax.swing.AbstractJComboBox;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.main.AbstractApplication;

public abstract class SwingFactory {
	public static SwingFactory prototype = null;

	// TODO: find another place for this function
	public abstract AutoCompleteTextField newAutoCompleteTextField(int length,
			AbstractApplication application, Drawable drawTextField);

	public abstract GLabel newJLabel(String string);
	
	public abstract AbstractJComboBox newJComboBox();

	public abstract GBox createHorizontalBox();

}
