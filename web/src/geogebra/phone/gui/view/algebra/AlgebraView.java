package geogebra.phone.gui.view.algebra;

import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractView;
import geogebra.phone.gui.view.HeaderPanel;
import geogebra.phone.gui.view.StyleBar;
import geogebra.phone.gui.view.ViewPanel;
import geogebra.web.css.GuiResources;

import com.google.gwt.resources.client.ImageResource;

public class AlgebraView extends AbstractView {

	public AlgebraView(AppW app) {
		super(app);
	}

	@Override
	protected ImageResource createViewIcon() {
		return GuiResources.INSTANCE.algebraView();
	}

	@Override
	protected ViewPanel createViewPanel() {
		return new AlgebraViewPanel(app);
	}

	@Override
	protected HeaderPanel createHeaderPanel() {
		return null;
	}

	@Override
	public StyleBar createStyleBar() {
		return null;
	}

}
