package org.geogebra.keyboard.web;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.awt.GColor;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.keyboard.base.KeyboardType;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.ToggleButton;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class KeyboardSwitcher extends FlowPanel {

	private TabbedKeyboard tabbedkeyboard;

	private FlowPanel contents;
	private Map<KeyboardType, SwitcherButton> switches;
	private ToggleButton moreButton;

	public class SwitcherButton extends Button {

		private KeyPanelBase keyboard;

		/**
		 * Create a new SwitcherButton
		 *
		 * @param label label
		 * @param keyboard keyboard
		 */
		public SwitcherButton(String label, KeyPanelBase keyboard) {
			super(label);
			this.keyboard = keyboard;

			ClickStartHandler.init(this, new ClickStartHandler(true, true) {

				@Override
				public void onClickStart(int x, int y, PointerEventType type) {
					select();
				}
			});
		}

		public KeyPanelBase getKeyboard() {
			return keyboard;
		}

		/**
		 * Makes the keyboard visible and selects the button.
		 */
		public void select() {
			tabbedkeyboard.hideKeyboards();
			unselectAll();
			keyboard.setVisible(true);
			setSelected(this, true);
		}
	}

	/**
	 * @param tabbedkeyboard
	 *            keyboard
	 */
	public KeyboardSwitcher(TabbedKeyboard tabbedkeyboard) {
		this.tabbedkeyboard = tabbedkeyboard;
		addStyleName("KeyboardSwitcher");
		setup();
	}

	protected void setup() {
		addCloseButton();
		contents = new FlowPanel();
		contents.addStyleName("switcherContents");
		add(contents);
		switches = new HashMap<>();
	}

	protected SwitcherButton addSwitch(KeyPanelBase keyboard, KeyboardType type, String string) {
		SwitcherButton btn = new SwitcherButton(string, keyboard);
		switches.put(type, btn);
		contents.add(btn);
		return btn;
	}

	protected void setSelected(Button btn, boolean value) {
		if (value) {
			btn.addStyleName("selected");
		} else {
			btn.removeStyleName("selected");
		}
	}

	protected boolean isSelected(Button btn) {
		return btn.getStyleName().contains("selected");
	}

	protected void unselectAll() {
		for (Widget btn : switches.values()) {
			btn.removeStyleName("selected");
		}
	}

	protected void addCloseButton() {
		ToggleButton closeButton = new ToggleButton(KeyboardResources.INSTANCE
				.keyboard_close_black(), GeoGebraColorConstants.GEOGEBRA_ACCENT) {
			@Override
			public void setFocus(boolean focused) {
				// Do not focus the button
			}
		};
		closeButton.removeStyleName("MyToggleButton");
		closeButton.getElement().setAttribute("aria-label",
				tabbedkeyboard.locale.getMenu("Close"));
		closeButton.addStyleName("closeTabbedKeyboardButton");
		closeButton.getElement().setAttribute("data-test", "closeKeyboardButton");
		ClickStartHandler.init(closeButton, new ClickStartHandler() {

			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				tabbedkeyboard.closeButtonClicked();
				DOM.setCapture(null);
			}
		});
		add(closeButton);
	}

	protected final void addMoreButton() {
		if (moreButton == null) {
			createMoreButton();
		}
		contents.add(moreButton);
	}

	private void createMoreButton() {
		moreButton = new ToggleButton(KeyboardResources.INSTANCE.keyboard_more(),
				KeyboardResources.INSTANCE.keyboard_more());
		moreButton.setMouseOverHandler(() -> switchIcon(true));
		moreButton.setMouseOutHandler(() -> switchIcon(moreButton.isSelected()));
		moreButton.getElement().setAttribute("aria-label",
				tabbedkeyboard.locale.getMenu("Commands"));

		moreButton.removeStyleName("MyToggleButton");
		moreButton.addStyleName("moreKeyboardButton");

		moreButton.addFastClickHandler((source) -> tabbedkeyboard.toggleHelp(moreButton
						.getAbsoluteLeft() + moreButton.getOffsetWidth(),
				moreButton.getAbsoluteTop()));
	}

	/**
	 * switch img on open/close popup
	 * @param isActive - popup is open
	 */
	public void switchIcon(boolean isActive) {
		Dom.toggleClass(this, "noOpacity", isActive);
		moreButton.setIcon(((SVGResource) moreButton.getIcon()).withFill(isActive
				? GeoGebraColorConstants.GEOGEBRA_ACCENT.toString()
				: GColor.BLACK.toString()));
	}

	protected void reset() {
		if (moreButton != null) {
			moreButton.setSelected(false);
		}
	}

	/**
	 * @param keyboardType
	 *            keyboard type
	 */
	protected void select(KeyboardType keyboardType) {
		if (keyboardType == KeyboardType.GREEK) {
			tabbedkeyboard.hideKeyboards();
			tabbedkeyboard.getKeyboard(keyboardType).setVisible(true);
		} else {
			switches.get(keyboardType).select();
		}
	}

	public FlowPanel getContent() {
		return contents;
	}

	/**
	 * Shows the More button
	 */
	public void showMoreButton() {
		if (moreButton != null) {
			moreButton.setVisible(true);
		}
	}

	/**
	 * Hides the More button
	 */
	public void hideMoreButton() {
		if (moreButton != null) {
			moreButton.setVisible(false);
		}
	}
}
