package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;

import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Audio / video / embed dialog.
 */

public abstract class MediaDialog extends ComponentDialog {

	protected MediaInputPanel mediaInputPanel;

	/**
	 * @param app
	 *            app
	 * @param dialogTitle
	 * 			  title of dialog
	 */

	public MediaDialog(AppW app, String dialogTitle) {
		super(app, new DialogData(dialogTitle, "Cancel", "Insert"),
				false, true);
		addStyleName("mediaDialog");
		addStyleName(dialogTitle);
		addStyleName("mebis");
		buildContent();
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();

		mediaInputPanel = new MediaInputPanel((AppW) app, this, "Link", true);
		mediaInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));

		contentPanel.add(mediaInputPanel);
		addDialogContent(contentPanel);

		mediaInputPanel.focusDeferred();
		setPosBtnDisabled(true);
	}

	/**
	 * @return url with https prefix
	 */
	protected String getUrlWithProtocol() {
		return addProtocol(mediaInputPanel.getInput());
	}

	/**
	 * @param url
	 *            url that may or may not include a protocol
	 * @return URL including protocol
	 */
	protected static String addProtocol(String url) {
		String value = url.replaceFirst(GeoGebraConstants.HTTP, "");

		if (!url.startsWith(GeoGebraConstants.HTTPS) && !url.startsWith("data:")) {
			value = GeoGebraConstants.HTTPS + value;
		}
		return value;
	}

	@Override
	public void hide() {
		app.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	protected void onMediaElementCreated(GeoElement geoElement) {
		getApplication().getActiveEuclidianView()
				.getEuclidianController().selectAndShowSelectionUI(geoElement);
	}
}
