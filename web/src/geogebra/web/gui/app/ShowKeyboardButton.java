package geogebra.web.gui.app;

import geogebra.web.css.GuiResources;
import geogebra.web.gui.NoDragImage;
import geogebra.web.util.keyboard.UpdateKeyBoardListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A PopupPanel in the bottom left corner of the application which represents a
 * button to open the {@link OnScreenKeyBoard}
 */
public class ShowKeyboardButton extends PopupPanel {
	
	private final int HEIGHT = 33;
	private Widget parent;

	/**
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param textField
	 *            {@link Widget}
	 * @param parent
	 *            {@link Element}
	 */
	public ShowKeyboardButton(final UpdateKeyBoardListener listener,
			final Widget textField, Widget parent) {

		this.parent = parent;
		this.addStyleName("openKeyboardButton");
		NoDragImage showKeyboard = new NoDragImage(GuiResources.INSTANCE
		        .keyboard_show().getSafeUri().asString());
		this.add(showKeyboard);
		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				listener.doShowKeyBoard(true, textField);
			}
		}, ClickEvent.getType());

	}

	/**
	 * 
	 * @param show
	 *            {@code true} to show the button to open the OnScreenKeyboard
	 * @param textField
	 *            {@link Widget} to set as AutoHidePartner
	 */
	public void show(boolean show, Widget textField) {
		if (textField != null) {
			addAutoHidePartner(textField.getElement());
		}

		if (show && parent.isVisible()) {
			updatePosition();
			show();
		} else {
			hide();
		}

	}

	/**
	 * updates the popup position
	 */
	public void updatePosition() {
		this.setPopupPosition(parent.getAbsoluteLeft(),
				parent.getOffsetHeight()
				+ parent.getAbsoluteTop() - this.HEIGHT);
	}
}
