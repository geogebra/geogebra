package org.geogebra.web.web.gui.inputbar;

import com.google.gwt.user.client.ui.UIObject;

public interface HasHelpButton {

	void updateIcons(boolean b);

	String getCommand();

	UIObject getHelpToggle();

}
