package org.geogebra.web.web.gui;

import org.geogebra.web.html5.gui.ToolBarInterface;
import org.geogebra.web.html5.main.HasAppletProperties;

public interface HeaderPanelDeck extends HasAppletProperties {

	void hideBrowser(MyHeaderPanel myHeaderPanel);

	ToolBarInterface getToolbar();

}
