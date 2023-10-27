package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.full.main.EmbedFactory;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.user.client.ui.Label;

public class H5PInputDialog extends ComponentDialog {

	private MediaInputPanel mediaInputPanel;
	private EmbedFactory embedFactory;

	/**
	 * h5p tool dialog constructor
	 * @param app - see {@link AppW}
	 */
	public H5PInputDialog(AppW app) {
		super(app, new DialogData("H5P", "Cancel", "Insert"),
				false, true);
		addStyleName("mediaDialog");
		addStyleName("H5P");
		buildContent();
		embedFactory = new EmbedFactory(app, mediaInputPanel);
		embedFactory.setHideDialogCallback(this::hide);
	}

	private void buildContent() {
		Label helpTxt = BaseWidgetFactory.INSTANCE.newSecondaryText(
				app.getLocalization().getMenu("H5PDialog.InsertHelpTxt"), "helpTxt");
		addDialogContent(helpTxt);

		mediaInputPanel = new MediaInputPanel((AppW) app, this, "Link", true);
		mediaInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));
		addDialogContent(mediaInputPanel);

		setPosBtnDisabled(true);
	}

	@Override
	public void onPositiveAction() {
		if (app.getGuiManager() != null) {
			embedFactory.addEmbed();
		}
	}

	@Override
	public void show() {
		super.show();
		if (mediaInputPanel != null) {
			mediaInputPanel.focusDeferred();
		}
	}

	@Override
	public void hide() {
		super.hide();
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}
