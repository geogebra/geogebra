package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.gui.view.algebra.MathKeyboardListener;
import org.geogebra.web.web.gui.app.ShowKeyboardButton;
import org.geogebra.web.web.gui.layout.DockPanelW;
import org.geogebra.web.web.gui.view.algebra.AlgebraViewW;

import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AlgebraDockPanelW extends DockPanelW {

	ScrollPanel algebrap;
	SimplePanel simplep;
	AlgebraViewW aview = null;
	private ShowKeyboardButton keyboardButton;

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
			setAlgebraView((AlgebraViewW) app.getAlgebraView());
			aview.setInputPanel();
		}
		return algebrap;
	}

	@Override
	protected Widget loadStyleBar() {
		return aview.getStyleBar(true);
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
		// ignore super method


    }

	@Override
    public ResourcePrototype getIcon() {
		return getResources().menu_icon_algebra();
	}

	/**
	 * scrolls to the bottom of the panel
	 */
	public void scrollToBottom(){
		if (this.algebrap != null) {
			this.algebrap.scrollToBottom();
		}
	}

	@Override
	public MathKeyboardListener getKeyboardListener() {
		return ((AlgebraViewW) app.getAlgebraView()).getInputTreeItem();
	}


}
