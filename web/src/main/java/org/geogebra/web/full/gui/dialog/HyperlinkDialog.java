package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.himamis.retex.editor.share.util.Unicode;

public class HyperlinkDialog extends OptionDialog {

	private final String hyperlinkText;
	private MediaInputPanel textInputPanel;
	private MediaInputPanel linkInputPanel;

	private HasTextFormat formatter;

	/**
	 * Dialog for inserting hyperlink into an inline text
	 */
	public HyperlinkDialog(AppW app, HasTextFormat formatter) {
		super(app.getPanel(), app);
		this.formatter = formatter;

		textInputPanel = new MediaInputPanel(app, this,	"Text", false);
		linkInputPanel = new MediaInputPanel(app, this, "Link", true);

		linkInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));
		updateButtonLabels("OK");

		hyperlinkText = formatter.getHyperlinkRangeText().replace('\n', Unicode.ZERO_WIDTH_SPACE);
		textInputPanel.setText(hyperlinkText);
		linkInputPanel.setText(formatter.getHyperLinkURL());

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
		String link = linkInputPanel.getInput();
		if (StringUtil.empty(link)) {
			return;
		}

		String url = normalizeUrl(link);

		String text = textInputPanel.inputField.getText();
		if (text.equals(hyperlinkText)	&& !StringUtil.empty(formatter.getHyperLinkURL())) {
			formatter.setHyperlinkUrl(url);
		} else {
			formatter.insertHyperlink(url,	text.replace(Unicode.ZERO_WIDTH_SPACE, '\n'));
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
