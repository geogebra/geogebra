package geogebra.web.gui;

import geogebra.web.css.GuiResources;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

public class SplashDialog extends PopupPanel {

	public SplashDialog() {
	    HTML svg = new HTML(GuiResources.INSTANCE.ggb4Splash().getText());
	    add(svg);
    }

}
