package geogebra.web.gui.app;

import geogebra.web.css.GuiResources;
import geogebra.web.gui.NoDragImage;
import geogebra.web.util.keyboard.UpdateKeyBoardListener;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

public class ShowKeyboardButton extends PopupPanel {
	public ShowKeyboardButton(final UpdateKeyBoardListener listener,
	        final Widget textField) {
		new PopupPanel();
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
		setAutoHideEnabled(true);
		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				listener.doShowKeyBoard(true, textField);
				hide();
			}
		}, ClickEvent.getType());
	}

	public void show(boolean show, Widget textField) {
		if (textField != null) {
			addAutoHidePartner(textField.getElement());
		}

		if (show) {
			show();
		} else {
			hide();
		}

	}
}
