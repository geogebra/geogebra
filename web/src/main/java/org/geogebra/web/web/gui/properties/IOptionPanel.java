package org.geogebra.web.web.gui.properties;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;

import com.google.gwt.user.client.ui.Widget;

public interface IOptionPanel extends SetLabels {
	Object updatePanel(Object[] geos);
	Widget getWidget(); 
	void setWidget(Widget widget);

	OptionsModel getModel();
}
