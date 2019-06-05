package org.geogebra.web.html5.gui.accessibility;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

public interface AccessibleWidget {

	List<? extends Widget> getControl();

	public void update();

}
