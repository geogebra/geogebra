package org.geogebra.web.full.gui;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Dialog that describes what has changed recently in the app.
 */
public class WhatsNewDialog extends DialogBoxW implements FastClickHandler {

	private static final String DIALOG_STYLE_NAME = "whatsNewDialog";
	private static final String TITLE_STYLE_NAME = "title";
	private static final String MESSAGE_PANEL_STYLE_NAME = "messagePanel";
	private static final String MESSAGE_STYLE_NAME = "message";
	private static final String LINK_STYLE_NAME = "link";

	/**
	 * Create a new WhatsNewDialog.
	 * @param app the app
	 * @param whatsNewMessage the message to show
	 * @param readMoreLink the link to read more about the changes
	 */
	public WhatsNewDialog(AppW app, String whatsNewMessage, String readMoreLink) {
		super(false, true, null, app.getPanel(), app);
		setupView(app.getLocalization(), whatsNewMessage, readMoreLink);
	}

	private void setupView(Localization localization, String whatsNewMessage,
						   String readMoreLink) {
		addStyleName(DIALOG_STYLE_NAME);
		Widget view = createView(localization, whatsNewMessage, readMoreLink);
		add(view);
	}

	private Widget createView(Localization localization, String whatsNewMessage,
							  String readMoreLink) {
		VerticalPanel panel = new VerticalPanel();
		Widget title = createTitle(localization);
		Widget flowPanel = createFlowPanel(localization, whatsNewMessage, readMoreLink);
		Widget okButton = createOkButton(localization);
		panel.add(title);
		panel.add(flowPanel);
		panel.add(okButton);
		return panel;
	}

	private Widget createTitle(Localization localization) {
		Label title = new Label();
		title.setText(localization.getMenu("WhatsNew"));
		title.addStyleName(TITLE_STYLE_NAME);
		return title;
	}

	private Widget createFlowPanel(Localization localization, String whatsNewMessage,
								   String readMoreLink) {
		FlowPanel flowPanel = new FlowPanel();
		flowPanel.addStyleName(MESSAGE_PANEL_STYLE_NAME);
		Widget message = createMessage(whatsNewMessage);
		Widget readMore = createReadMore(localization, readMoreLink);
		flowPanel.add(message);
		flowPanel.add(readMore);
		return flowPanel;
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
		link.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				app.showURLinBrowser(readMoreLink);
			}
		}, ClickEvent.getType());

		return link;
	}

	private Panel createOkButton(Localization localization) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(DIALOG_BUTTON_PANEL_STYLE_NAME);
		StandardButton okButton = new StandardButton(
				localization.getMenu("OK"), null);
		okButton.addFastClickHandler(this);
		panel.add(okButton);
		return panel;
	}

	@Override
	public void onClick(Widget source) {
		hide();
	}
}
