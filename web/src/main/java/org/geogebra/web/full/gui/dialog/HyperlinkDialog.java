package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;
import com.himamis.retex.editor.share.util.Unicode;

public class HyperlinkDialog extends ComponentDialog {
	private String hyperlinkText;
	private MediaInputPanel textInputPanel;
	private MediaInputPanel linkInputPanel;

	private HasTextFormat formatter;

	/**
	 * Dialog for inserting hyperlink into an inline text
	 *
	 * @param app see {@link AppW}
	 * @param data dialog transkeys
	 * @param formatter text formatter
	 *
	 */
	public HyperlinkDialog(AppW app, DialogData data, HasTextFormat formatter) {
		super(app, data, false, true);
		this.formatter = formatter;

		addStyleName("mediaDialog");
		addStyleName("hyperLink");
		addStyleName("mebis");

		buildContent(app);
		setOnPositiveAction(this::processInput);
	}

	private void buildContent(AppW app) {
		textInputPanel = new MediaInputPanel(app, this,	"Text", false);
		linkInputPanel = new MediaInputPanel(app, this, "Link", true);

		linkInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));

		hyperlinkText = formatter.getHyperlinkRangeText().replace('\n', Unicode.ZERO_WIDTH_SPACE);
		textInputPanel.setText(hyperlinkText);
		linkInputPanel.setText(formatter.getHyperLinkURL());

		FlowPanel contentPanel = new FlowPanel();
		contentPanel.add(textInputPanel);
		contentPanel.add(linkInputPanel);
		addDialogContent(contentPanel);

		linkInputPanel.focusDeferred();
	}

	private void processInput() {
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