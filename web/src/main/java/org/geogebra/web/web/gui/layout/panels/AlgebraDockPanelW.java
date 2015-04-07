package org.geogebra.web.web.gui.layout.panels;

import org.geogebra.common.main.App;
import org.geogebra.web.web.gui.app.ShowKeyboardButton;
import org.geogebra.web.web.gui.inputbar.AlgebraInputW;
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
	AlgebraInputW inputPanel = new AlgebraInputW();
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
		// ignore super method


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

	/**
	 * set a ShowKeyBoardButton that will be updated, if this panel is resized
	 * 
	 * @param button
	 *            the button to be updated
	 */
	public void setKeyBoardButton(ShowKeyboardButton button) {
		this.keyboardButton = button;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		// hide the keyboard-button, when the view is closed
		if (keyboardButton != null && !visible) {
			keyboardButton.hide();
		}
	}
}
