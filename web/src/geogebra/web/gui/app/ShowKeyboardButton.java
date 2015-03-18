package geogebra.web.gui.app;

import geogebra.web.css.GuiResources;
import geogebra.web.gui.NoDragImage;
import geogebra.web.util.keyboard.OnScreenKeyBoard;
import geogebra.web.util.keyboard.UpdateKeyBoardListener;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A PopupPanel in the bottom left corner of the application which represents a
 * button to open the {@link OnScreenKeyBoard}
 */
public class ShowKeyboardButton extends PopupPanel {
	
	private final int HEIGHT = 33;
	private Element parent;

	/**
	 * @param listener
	 *            {@link UpdateKeyBoardListener}
	 * @param textField
	 *            {@link Widget}
	 * @param parent
	 *            {@link Element}
	 */
	public ShowKeyboardButton(final UpdateKeyBoardListener listener,
	        final Widget textField, Element parent) {

		this.parent = parent;
		this.addStyleName("openKeyboardButton");
		HorizontalPanel content = new HorizontalPanel();
		NoDragImage triangle = new NoDragImage(GuiResources.INSTANCE
		        .keyboard_triangleUp().getSafeUri().asString());
		triangle.addStyleName("arrowUp");
		content.add(triangle);
		NoDragImage showKeyboard = new NoDragImage(GuiResources.INSTANCE
		        .keyboard_show().getSafeUri().asString());
		content.add(showKeyboard);
		add(content);
		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				listener.doShowKeyBoard(true, textField);
				hide();
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

		if (show) {
			this.setPopupPosition(parent.getOffsetLeft(),
			        parent.getOffsetHeight() + parent.getOffsetTop()
			                - this.HEIGHT);
			show();
		}

	}

	/**
	 * updates the popup position
	 */
	public void updatePosition() {
		this.setPopupPosition(parent.getOffsetLeft(), parent.getOffsetHeight()
		        + parent.getOffsetTop() - this.HEIGHT);
	}
}
