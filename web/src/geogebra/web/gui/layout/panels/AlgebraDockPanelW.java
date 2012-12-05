package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.euclidian.event.HasOffsets;
import geogebra.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class AlgebraDockPanelW extends ResizeComposite /* DockPanelW */ {

	private App application;

	ScrollPanel algebrap;
	SimplePanel simplep;
	AlgebraViewW aview = null;

	public AlgebraDockPanelW() {
		super();
		initWidget(algebrap = new ScrollPanel());//temporarily
		algebrap.setSize("100%", "100%");
		algebrap.setAlwaysShowScrollBars(false);
	}

	public void setAlgebraView(AlgebraViewW av) {
		if (av != aview) {
			if (aview != null && simplep != null) {
				simplep.remove(aview);
				algebrap.remove(simplep);
			}

			simplep = new SimplePanel(aview = av);
			algebrap.add(simplep);
		}
	}

	public ScrollPanel getAbsolutePanel() {
	    return algebrap;
    }

	public void onResize() {
		if(application.getActiveEuclidianView().getEuclidianController() instanceof HasOffsets)
			((HasOffsets)application.getActiveEuclidianView().getEuclidianController()).updateOffsets();
		App.debug("resized");
    }

	public void attachApp(App app) {
		if (application != app) {
			application = app;
			setAlgebraView((AlgebraViewW)application.getAlgebraView());
		}
	}
}
