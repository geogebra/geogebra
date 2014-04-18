package geogebra.web.gui.properties;

import geogebra.common.gui.SetLabels;

import com.google.gwt.user.client.ui.Widget;

public interface IOptionPanel extends SetLabels {
	boolean update(Object[] geos);
	Widget getWidget(); 
	void setWidget(Widget widget);
}
