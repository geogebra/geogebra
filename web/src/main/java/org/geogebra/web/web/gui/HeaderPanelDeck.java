package org.geogebra.web.web.gui;

import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.main.HasAppletProperties;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HeaderPanel;

public interface HeaderPanelDeck extends HasAppletProperties {

	void hideBrowser(MyHeaderPanel myHeaderPanel);

	ToolBarInterface getToolbar();

	void setMenuHeight(boolean b);

	void showBrowser(HeaderPanel bg);

	Element getElement();

}
