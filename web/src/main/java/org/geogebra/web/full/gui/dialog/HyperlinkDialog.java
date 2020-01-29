package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.text.InlineTextController;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;

public class HyperlinkDialog extends OptionDialog {

	private final String hyperlinkText;
	private MediaInputPanel textInputPanel;
	private MediaInputPanel linkInputPanel;

	private InlineTextController inlineText;

	/**
	 * Dialog for inserting hyperlink into an inline text
	 */
	public HyperlinkDialog(AppW app, InlineTextController inlineText) {
		super(app.getPanel(), app);
		this.inlineText = inlineText;

		textInputPanel = new MediaInputPanel(app, this,
				app.getLocalization().getMenu("Text"), false);
		linkInputPanel = new MediaInputPanel(app, this,
				app.getLocalization().getMenu("Link"), true);

		linkInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));
		updateButtonLabels("OK");

		hyperlinkText = inlineText.getHyperlinkRangeText();
		if (StringUtil.empty(hyperlinkText)) {
			textInputPanel.setText(inlineText.getSelectedText());
		} else {
			textInputPanel.setText(hyperlinkText);
		}
		linkInputPanel.setText(inlineText.getHyperlinkUrl());

		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(textInputPanel);
		mainPanel.add(linkInputPanel);
		mainPanel.add(getButtonPanel());
		add(mainPanel);

		addStyleName("GeoGebraPopup");
		addStyleName("mediaDialog");
		addStyleName("mebis");

		linkInputPanel.focusDeferred();
	}

	@Override
	protected void processInput() {
		String url = normalizeUrl(linkInputPanel.getInput());

		String text = textInputPanel.inputField.getText();
		if (text.equals(hyperlinkText)
				&& !StringUtil.empty(inlineText.getHyperlinkUrl())) {
			inlineText.setHyperlinkUrl(url);
		} else {
			inlineText.insertHyperlink(url,	text);
		}
		hide();
	}

	private static String normalizeUrl(String url) {
		if (url.startsWith(GeoGebraConstants.HTTP) || url.startsWith(GeoGebraConstants.HTTPS)) {
			return url;
		}

		return GeoGebraConstants.HTTPS + url;
	}

	@Override
	public void hide() {
		super.hide();
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}
