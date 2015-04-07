package org.geogebra.web.phone.gui.view.euclidian;

import org.geogebra.web.html5.euclidian.EuclidianViewW;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.phone.gui.view.AbstractView;
import org.geogebra.web.phone.gui.view.HeaderPanel;
import org.geogebra.web.phone.gui.view.StyleBar;
import org.geogebra.web.phone.gui.view.ViewPanel;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.euclidian.EuclidianStyleBarW;

import com.google.gwt.resources.client.ImageResource;

public class EuclidianView extends AbstractView {
	
	private EuclidianViewW euclidianView;

	public EuclidianView(AppW app) {
		super(app);
		euclidianView = app.getEuclidianView1();
	}

	@Override
	protected ViewPanel createViewPanel() {
		return new EuclidianViewPanel(app, euclidianView);
	}

	@Override
	protected ImageResource createViewIcon() {
		return GuiResources.INSTANCE.graphicsView();
	}

	@Override
	protected HeaderPanel createHeaderPanel() {
		return new EuclidianHeaderPanel(app);
	}

	@Override
	public StyleBar createStyleBar() {
		return new EuclidianStyleBar(
		        (EuclidianStyleBarW) euclidianView.getStyleBar());
	}
}
