package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.full.main.EmbedFactory;
import org.geogebra.web.html5.main.AppW;

/** embed dialog
 */
public class EmbedInputDialog extends MediaDialog {
	private final EmbedFactory embedFactory;

	/** Creates dialog for embed input
	 * @param app see {@link AppW}
	 */
	EmbedInputDialog(AppW app) {
		super(app, "Web");
		embedFactory = new EmbedFactory(app, mediaInputPanel);
		embedFactory.setHideDialogCallback(this::hide);
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

	@Override
	public void hide() {
		super.hide();
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}
}