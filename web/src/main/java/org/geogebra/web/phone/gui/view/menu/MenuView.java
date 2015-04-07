package org.geogebra.web.phone.gui.view.menu;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.AbstractView;
import org.geogebra.web.phone.gui.view.HeaderPanel;
import org.geogebra.web.phone.gui.view.StyleBar;
import org.geogebra.web.phone.gui.view.ViewPanel;
import org.geogebra.web.web.css.GuiResources;

import com.google.gwt.resources.client.ImageResource;

public class MenuView extends AbstractView {

	private MenuViewPanel menuViewPanel;

	public MenuView(AppW app) {
		super(app);
		this.menuViewPanel = (MenuViewPanel) createViewPanel();
	}

	@Override
	protected ViewPanel createViewPanel() {
		return new MenuViewPanel(app);
	}

	@Override
	protected ImageResource createViewIcon() {
		return GuiResources.INSTANCE.options();
	}

	@Override
	protected HeaderPanel createHeaderPanel() {
		return null;
	}

	@Override
	protected StyleBar createStyleBar() {
		return null;
	}
}
