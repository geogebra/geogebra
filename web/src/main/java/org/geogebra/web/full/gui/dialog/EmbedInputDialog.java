package org.geogebra.web.full.gui.dialog;

import org.geogebra.web.full.main.EmbedFactory;
import org.geogebra.web.html5.main.AppW;

/** embed dialog
 */
public class EmbedInputDialog extends MediaDialog {
	private EmbedFactory embedFactory;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	EmbedInputDialog(AppW app) {
		super(app, "Web");
		embedFactory = new EmbedFactory(app, this, mediaInputPanel);
		updateInfo();
	}

	@Override
	public void buildContent() {
		super.buildContent();
		addInfoLabel();
	}

	private void addInfoLabel() {
		mediaInputPanel.addInfoLabel();
	}

	private void updateInfo() {
		if (embedFactory.getUrlChecker() != null
				&& !embedFactory.getUrlChecker().hasFrameOptionCheck()) {
			mediaInputPanel.showInfo(app.getLocalization().getMenu("EmbedFrameWarning"));
		}
	}

	@Override
	public void onPositiveAction() {
		if (app.getGuiManager() != null) {
			embedFactory.addEmbed();
		}
	}
}