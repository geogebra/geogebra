package org.geogebra.web.full.gui;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.geogebra.common.main.Localization;
import org.geogebra.web.full.css.TextStyles;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

public class WhatsNewDialog extends DialogBoxW implements FastClickHandler {

	public WhatsNewDialog(AppW app, String whatsNewMessage) {
		super(false, true, null, app.getPanel(), app);
		setupView(app.getLocalization(), whatsNewMessage);
	}

	private void setupView(Localization localization, String whatsNewMessage) {
		Widget view = createView(localization, whatsNewMessage);
		add(view);
	}

	private Widget createView(Localization localization, String whatsNewMessage) {
		VerticalPanel panel = new VerticalPanel();
		Widget title = createTitle(localization);
		Widget flowPanel = createFlowPanel(localization, whatsNewMessage);
		Widget okButton = createOkButton(localization);
		panel.add(title);
		panel.add(flowPanel);
		panel.add(okButton);
		return panel;
	}

	private Widget createTitle(Localization localization) {
		Label title = new Label();
		title.setText(localization.getMenu("WhatsNew"));
		title.addStyleName(TextStyles.HEADLINE_6);
		return title;
	}

	private Widget createFlowPanel(Localization localization, String whatsNewMessage) {
		FlowPanel flowPanel = new FlowPanel();
		Widget message = createMessage(whatsNewMessage);
		Widget readMore = createReadMore(localization);
		flowPanel.add(message);
		flowPanel.add(readMore);
		return flowPanel;
	}

	private Widget createMessage(String whatsNewMessage) {
		InlineLabel message = new InlineLabel();
		message.setText(whatsNewMessage);
		message.addStyleName(TextStyles.SUBTITLE_1);
		return message;
	}

	private Widget createReadMore(Localization localization) {
		Anchor link = new Anchor();
		link.setText(localization.getMenu("ReadMore"));
		link.setHref("www.geogebra.org/m/");
		link.setStyleName(TextStyles.SUBTITLE_1_LINK);
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
