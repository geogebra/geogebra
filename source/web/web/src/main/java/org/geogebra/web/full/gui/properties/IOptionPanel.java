package org.geogebra.web.full.gui.properties;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.gwtproject.user.client.ui.Widget;

/**
 * UI delegate for OptionsModel.
 */
public interface IOptionPanel extends SetLabels {
	Object updatePanel(Object[] geos);

	Widget getWidget();

	OptionsModel getModel();
}
