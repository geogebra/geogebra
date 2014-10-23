package geogebra.web.gui.inputbar;

import geogebra.html5.main.AppW;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * A popup panel, which holds the {@link InputBarHelpPanelW}
 *
 */
public class InputBarHelpPopup extends PopupPanel {

	private ToggleButton toggleButton;

	/**
	 * @param app {@link AppW}
	 */
	public InputBarHelpPopup(AppW app) {
		super();
		this.addStyleName("helpPopup");
		this.setAutoHideEnabled(true);
		this.add((Widget) app.getGuiManager().getInputHelpPanel());
		this.addStyleName("GeoGebraPopup");
	}

	/**
	 * Hides the popup and detaches it from the page. This has no effect if it is
	 * not currently showing.
	 *
	 * @param autoClosed the value that will be passed to
	 *          {@link CloseHandler#onClose(CloseEvent)} when the popup is closed
	 */
	@Override
	public void hide(boolean autoClosed) {
		super.hide(autoClosed);
		toggleButton(false);
	}

	@Override
	public void show() {
		super.show();
		toggleButton(true);
	}

	/**
	 * 
	 */
	private void toggleButton(boolean value) {
		if (this.toggleButton != null) {
			this.toggleButton.setValue(value);
		}
	}

	/**
	 * set the toggleButton to change automatic his style, if this popup is shown or hidden.
	 * @param btnHelpToggle {@link ToggleButton}
	 */
	public void setBtnHelpToggle(ToggleButton btnHelpToggle) {
		this.toggleButton = btnHelpToggle;
	}
}
