package org.geogebra.web.full.gui.dialog;

import com.google.gwt.user.client.ui.FlowPanel;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.euclidian.draw.DrawInlineText;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.main.AppW;

public class HyperlinkDialog extends OptionDialog {

	private MediaInputPanel textInputPanel;
	private MediaInputPanel linkInputPanel;

	private DrawInlineText inlineText;

	/**
	 * Dialog for inserting hyperlink into an inline text
	 */
	public HyperlinkDialog(AppW app, DrawInlineText inlineText) {
		super(app.getPanel(), app);
		this.inlineText = inlineText;

		FlowPanel mainPanel = new FlowPanel();

		textInputPanel = new MediaInputPanel(app, this,
				app.getLocalization().getMenu("Text"), false);
		linkInputPanel = new MediaInputPanel(app, this,
				app.getLocalization().getMenu("Link"), true);

		linkInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));
		updateButtonLabels("OK");

		textInputPanel.setText(inlineText.getSelectedText());

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
		inlineText.insertHyperlink(linkInputPanel.getInput(),
				textInputPanel.inputField.getText());
		hide();
	}

	@Override
	public void hide() {
		super.hide();
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}
