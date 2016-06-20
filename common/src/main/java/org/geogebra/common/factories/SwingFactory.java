package org.geogebra.common.factories;

import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.javax.swing.GBox;
import org.geogebra.common.main.App;

public abstract class SwingFactory {
	private static SwingFactory prototype = null;

	// TODO: find another place for this function
	public abstract AutoCompleteTextField newAutoCompleteTextField(int length,
			App application, Drawable drawTextField);

	public abstract GBox createHorizontalBox(EuclidianController style);

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
