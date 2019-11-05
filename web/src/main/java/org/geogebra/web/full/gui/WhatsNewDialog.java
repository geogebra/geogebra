package org.geogebra.web.full.gui;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

/**
 * A Dialog that describes what has changed recently in the app.
 */
public class WhatsNewDialog extends DialogBoxW implements FastClickHandler {

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

	private void setupView(Localization localization, String whatsNewMessage, String readMoreLink) {
		addStyleName("whatsNewDialog");
		Widget view = createView(localization, whatsNewMessage, readMoreLink);
		add(view);
	}

	private Widget createView(Localization localization, String whatsNewMessage, String readMoreLink) {
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
		title.addStyleName("title");
		return title;
	}

	private Widget createFlowPanel(Localization localization, String whatsNewMessage, String readMoreLink) {
		FlowPanel flowPanel = new FlowPanel();
		Widget message = createMessage(whatsNewMessage);
		Widget readMore = createReadMore(localization, readMoreLink);
		flowPanel.add(message);
		flowPanel.add(readMore);
		return flowPanel;
	}

	private Widget createMessage(String whatsNewMessage) {
		InlineLabel message = new InlineLabel();
		message.setText(whatsNewMessage);
		message.addStyleName("message");
		return message;
	}

	private Widget createReadMore(Localization localization, String readMoreLink) {
		Anchor link = new Anchor();
		link.setText(localization.getMenu("ReadMore"));
		link.addStyleName("link");
		link.setHref("www.geogebra.org/m/");
		return link;
	}

	private Panel createOkButton(Localization localization) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName("DialogButtonPanel");
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
