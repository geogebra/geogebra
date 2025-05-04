package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.gwtproject.user.client.ui.Widget;

/**
 * UI delegate for OptionsModel.
 */
public interface IOptionPanel extends SetLabels {
	/**
	 * Update the panel.
	 * @param geos construction elements
	 * @return success
	 */
	Object updatePanel(Object[] geos);

	/**
	 * @return widget containing the setting elements
	 */
	Widget getWidget();

	/**
	 * @return underlying model
	 */
	OptionsModel getModel();
}
