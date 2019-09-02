package org.geogebra.web.html5.euclidian;

import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.web.html5.gui.util.MathKeyboardListener;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.IsWidget;

public interface InputBoxWidget extends IsWidget {
	/**
	 * Attach the symbolic editor to the specified input box for editing it.
	 *
	 * @param geoInputBox
	 *            GeoInputBox to edit.
	 *
	 * @param bounds
	 *            place to attach the editor to.
	 * @param parent
	 *            parent panel
	 */
	public void attach(GeoInputBox geoInputBox, GRectangle bounds,
			AbsolutePanel parent);

	/**
	 * @return keyboard listener
	 */
	public MathKeyboardListener getKeyboardListener();
}
