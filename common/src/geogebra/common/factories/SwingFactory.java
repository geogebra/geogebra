package geogebra.common.factories;

import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianController;
import geogebra.common.gui.inputfield.AutoCompleteTextField;
import geogebra.common.javax.swing.AbstractJComboBox;
import geogebra.common.javax.swing.GBox;
import geogebra.common.javax.swing.GLabel;
import geogebra.common.javax.swing.GPanel;
import geogebra.common.main.App;

public abstract class SwingFactory {
	private static SwingFactory prototype = null;

	// TODO: find another place for this function
	public abstract AutoCompleteTextField newAutoCompleteTextField(int length,
			App application, Drawable drawTextField);

	public abstract GLabel newJLabel(String string, boolean bool);

	public abstract AbstractJComboBox newJComboBox(App app, int ev);

	public abstract GBox createHorizontalBox(EuclidianController style);

	public abstract GPanel newGPanel();

	/**
	 * @return might return null. Use App.getSwingFactory()
	 */
	public static SwingFactory getPrototype() {
		return prototype;
	}

	public static void setPrototype(SwingFactory ret) {
		prototype = ret;

	}

}
