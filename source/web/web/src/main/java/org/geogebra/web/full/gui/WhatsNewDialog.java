/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.gui;

import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.Anchor;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.InlineLabel;
import org.gwtproject.user.client.ui.Widget;

/**
 * A Dialog that describes what has changed recently in the app.
 */
public class WhatsNewDialog extends ComponentDialog {
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
		addStyleName("whatsNewDialog");
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.addStyleName("messagePanel");
		Widget message = createMessage(whatsNewMessage);
		Widget readMore = createReadMore(localization, readMoreLink);
		contentPanel.add(message);
		contentPanel.add(readMore);
		addDialogContent(contentPanel);
	}

	private Widget createMessage(String whatsNewMessage) {
		InlineLabel message = new InlineLabel();
		message.setText(whatsNewMessage);
		message.addStyleName("message");
		return message;
	}

	private Widget createReadMore(Localization localization, final String readMoreLink) {
		Anchor link = new Anchor();
		link.setText(localization.getMenu("ReadMore"));
		link.addStyleName("link");
		link.addDomHandler(event -> app.showURLinBrowser(readMoreLink), ClickEvent.getType());
		return link;
	}
}