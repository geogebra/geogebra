package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.euclidian.event.HasOffsets;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AlgebraDockPanelW extends DockPanelW {

	private App application;

	ScrollPanel algebrap;
	SimplePanel simplep;
	AlgebraViewW aview = null;

	public AlgebraDockPanelW() {
		super(
				App.VIEW_ALGEBRA,	// view id 
				"AlgebraWindow", 			// view title phrase
				null,						// toolbar string
				true,						// style bar?
				2, 							// menu order
				'A'							// menu shortcut
			);		
	}

	protected Widget loadComponent() {
		algebrap = new ScrollPanel();//temporarily
		algebrap.setSize("100%", "100%");
		algebrap.setAlwaysShowScrollBars(false);
		return algebrap;
	}

	protected Widget loadStyleBar() {
		return new SimplePanel();
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
		if (application != null) {
			if(application.getActiveEuclidianView().getEuclidianController() instanceof HasOffsets)
				((HasOffsets)application.getActiveEuclidianView().getEuclidianController()).updateOffsets();
		}
		App.debug("resized");
    }

	public void attachApp(App app) {
		super.attachApp(app);
		if (application != app) {
			application = app;
			setAlgebraView((AlgebraViewW)application.getAlgebraView());
		}
	}

	@Override
    public void showView(boolean b) {
	    // TODO Auto-generated method stub	    
    }
}
