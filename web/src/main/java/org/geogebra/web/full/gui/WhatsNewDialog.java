package org.geogebra.web.full.gui;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Dialog that describes what has changed recently in the app.
 */
public class WhatsNewDialog extends ComponentDialog {
	private static final String DIALOG_STYLE_NAME = "whatsNewDialog";
	private static final String MESSAGE_PANEL_STYLE_NAME = "messagePanel";
	private static final String MESSAGE_STYLE_NAME = "message";
	private static final String LINK_STYLE_NAME = "link";

	/**
	 * Create a new WhatsNewDialog.
	 * @param app the app
	 * @param data dialog transkeys
	 * @param whatsNewMessage the message to show
	 * @param readMoreLink the link to read more about the changes
	 */
	public WhatsNewDialog(AppW app, DialogData data, String whatsNewMessage, String readMoreLink) {
		super(app, data, false, true);
		buildContent(app.getLocalization(), whatsNewMessage, readMoreLink);
	}

	private void buildContent(Localization localization, String whatsNewMessage,
						   String readMoreLink) {
		addStyleName(DIALOG_STYLE_NAME);
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.addStyleName(MESSAGE_PANEL_STYLE_NAME);
		Widget message = createMessage(whatsNewMessage);
		Widget readMore = createReadMore(localization, readMoreLink);
		contentPanel.add(message);
		contentPanel.add(readMore);
		addDialogContent(contentPanel);
	}

	private Widget createMessage(String whatsNewMessage) {
		InlineLabel message = new InlineLabel();
		message.setText(whatsNewMessage);
		message.addStyleName(MESSAGE_STYLE_NAME);
		return message;
	}

	private Widget createReadMore(Localization localization, final String readMoreLink) {
		Anchor link = new Anchor();
		link.setText(localization.getMenu("ReadMore"));
		link.addStyleName(LINK_STYLE_NAME);
		link.addDomHandler(event -> app.showURLinBrowser(readMoreLink), ClickEvent.getType());
		return link;
	}
}