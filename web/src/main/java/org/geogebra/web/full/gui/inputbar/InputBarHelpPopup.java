package org.geogebra.web.full.gui.inputbar;

import org.geogebra.web.html5.gui.GPopupPanel;
import org.geogebra.web.html5.gui.HasKeyboardPopup;
import org.geogebra.web.html5.gui.inputfield.AutoCompleteW;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Widget;

/**
 * A popup panel, which holds the {@link InputBarHelpPanelW}
 *
 */
public class InputBarHelpPopup extends GPopupPanel implements HasKeyboardPopup {

	private ToggleButton toggleButton;

	/**
	 * @param app
	 *            {@link AppW}
	 * @param field
	 *            input field
	 * @param className
	 *            CSS class
	 */
	public InputBarHelpPopup(AppW app, AutoCompleteW field, String className) {
		super(app.getPanel(), app);
		this.addStyleName(className);
		this.setAutoHideEnabled(true);
		((InputBarHelpPanelW) app.getGuiManager().getInputHelpPanel()).setInputField(field);
		this.add((Widget) app.getGuiManager().getInputHelpPanel());
		this.addStyleName("GeoGebraPopup");
		if (app.isUnbundled()) {
			addStyleName("matDesign");
		}
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
		setButtonValue(false);
	}

	@Override
	public void show() {
		super.show();
		setButtonValue(true);
	}

	/**
	 *
	 */
	private void setButtonValue(boolean value) {
		if (this.toggleButton != null) {
			this.toggleButton.setSelected(value);
		}
	}

	/**
	 * set the toggleButton to change automatic his style, if this popup is
	 * shown or hidden.
	 *
	 * @param btnHelpToggle
	 *            {@link ToggleButton}
	 */
	public void setBtnHelpToggle(ToggleButton btnHelpToggle) {
		this.toggleButton = btnHelpToggle;
	}
}
