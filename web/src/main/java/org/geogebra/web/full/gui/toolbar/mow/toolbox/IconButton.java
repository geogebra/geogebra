package org.geogebra.web.full.gui.toolbar.mow.toolbox;

import org.geogebra.web.full.css.ToolbarSvgResources;
import org.geogebra.web.full.gui.app.GGWToolBar;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;

public class IconButton extends StandardButton {

	public IconButton(int mode, AppW appW) {
		super(GGWToolBar.getImageURLNotMacro(ToolbarSvgResources.INSTANCE, mode, appW),
				appW.getToolName(mode), 24);
		addStyleName("iconButton");
	}

	public IconButton(ResourcePrototype icon, String ariaLabel) {
		super(icon, 24);
		addStyleName("iconButton");
		AriaHelper.setLabel(this, ariaLabel);
	}

	public IconButton(SVGResource icon, String ariaLabel, String dataTitle, String dataTest) {
		this(icon, ariaLabel);
		AriaHelper.setTitle(this, dataTitle);
		AriaHelper.setDataTest(this, dataTest);
	}

	public void setDisabled(boolean isDisabled) {
		Dom.toggleClass(this, "disabled", isDisabled);
	}

	public void setActive(boolean isActive) {
		AriaHelper.setPressedState(this, isActive);
		Dom.toggleClass(this, "active", isActive);
	}

	public boolean isActive() {
		return getElement().hasClassName("active");
	}
}
