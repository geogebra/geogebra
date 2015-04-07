package org.geogebra.web.web.gui.properties;

import org.geogebra.common.gui.SetLabels;

import com.google.gwt.user.client.ui.Widget;

public interface IOptionPanel extends SetLabels {
	boolean update(Object[] geos);
	Widget getWidget(); 
	void setWidget(Widget widget);
}
