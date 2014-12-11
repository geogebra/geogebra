package geogebra.phone.gui.view.material;

import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractView;
import geogebra.phone.gui.view.HeaderPanel;
import geogebra.phone.gui.view.StyleBar;
import geogebra.phone.gui.view.ViewPanel;
import geogebra.web.css.GuiResources;

import com.google.gwt.resources.client.ImageResource;

public class MaterialView extends AbstractView {

	public MaterialView(AppW app) {
		super(app);
	}

	@Override
	protected ViewPanel createViewPanel() {
		return new MaterialViewPanel(app);
	}

	@Override
	protected HeaderPanel createHeaderPanel() {
		return new MaterialHeaderPanel(app);
	}

	@Override
	protected ImageResource createViewIcon() {
		return GuiResources.INSTANCE.browseView();
	}
	
	@Override
	public StyleBar createStyleBar() {
		return null;
	}
}
