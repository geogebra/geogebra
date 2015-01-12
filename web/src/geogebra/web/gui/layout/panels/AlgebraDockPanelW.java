package geogebra.web.gui.layout.panels;

import geogebra.common.main.App;
import geogebra.web.gui.inputbar.AlgebraInputW;
import geogebra.web.gui.layout.DockPanelW;
import geogebra.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AlgebraDockPanelW extends DockPanelW {

	ScrollPanel algebrap;
	SimplePanel simplep;
	AlgebraViewW aview = null;
	AlgebraInputW inputPanel = new AlgebraInputW();

	public AlgebraDockPanelW() {
		super(
				App.VIEW_ALGEBRA,	// view id 
				"AlgebraWindow", 			// view title phrase
				null,						// toolbar string
				true,						// style bar?
				2, 							// menu order
				'A'							// menu shortcut
			);
		setViewImage(getResources().styleBar_algebraView());
	}

	@Override
	protected Widget loadComponent() {
		if (algebrap == null) {
			algebrap = new ScrollPanel();//temporarily
			algebrap.setSize("100%", "100%");
			algebrap.setAlwaysShowScrollBars(false);
		}
		if (app != null) {
			// force loading the algebra view,
			// as loadComponent should only load when needed
			setAlgebraView((AlgebraViewW)app.getAlgebraView());
			inputPanel.init(app);
			aview.setInputPanel(inputPanel);
		}
		return algebrap;
	}

	@Override
	protected Widget loadStyleBar() {
		return aview.getStyleBar();
	}

	public void setAlgebraView(AlgebraViewW av) {
		if (av != aview) {
			if (aview != null && simplep != null) {
				simplep.remove(aview);
				algebrap.remove(simplep);
			}

			simplep = new SimplePanel(aview = av);
			algebrap.add(simplep);
			simplep.addStyleName("algebraSimpleP");
			algebrap.addStyleName("algebraPanel");	
		}
	}

	public ScrollPanel getAbsolutePanel() {
	    return algebrap;
    }

	@Override
	public void onResize() {
		// noting to do here
    }

	@Override
    public void showView(boolean b) {
	    // TODO Auto-generated method stub	    
    }
	
	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_algebra();
	}

	/**
	 * scrolls to the bottom of the panel
	 */
	public void scrollToBottom(){
		this.algebrap.scrollToBottom();
	}

}
