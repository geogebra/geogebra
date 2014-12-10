package geogebra.phone.gui.view.euclidian;

import geogebra.html5.euclidian.EuclidianViewW;
import geogebra.html5.main.AppW;
import geogebra.phone.gui.view.AbstractView;
import geogebra.phone.gui.view.HeaderPanel;
import geogebra.phone.gui.view.StyleBarPanel;
import geogebra.phone.gui.view.ViewPanel;
import geogebra.web.css.GuiResources;

import com.google.gwt.resources.client.ImageResource;

public class EuclidianView extends AbstractView {
	
	private EuclidianViewW euclidianView;

	public EuclidianView(AppW app) {
		super(app);
		euclidianView = app.getEuclidianView1();
	}

	@Override
	protected ViewPanel createViewPanel() {
		return new EuclidianViewPanel(app);
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
	public StyleBarPanel createStyleBarPanel() {
		return new EuclidianStyleBarPanel(euclidianView.getStyleBar());
	}
}
