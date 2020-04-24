package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.full.main.AppWFull;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Audio / video dialog.
 *
 * @author Zbynek
 */
public abstract class MediaDialog extends OptionDialog {

	protected AppWFull appW;
	protected MediaInputPanel mediaInputPanel;

	/**
	 * @param root
	 *            popup root
	 * @param app
	 *            app
	 */
	public MediaDialog(Panel root, AppWFull app) {
		super(root, app);
		this.appW = app;
		initGui();
	}

	protected void initGui() {
		FlowPanel mainPanel = new FlowPanel();

		mediaInputPanel = new MediaInputPanel(appW, this, "Link", true);
		mediaInputPanel.addPlaceholder(app.getLocalization().getMenu("pasteLink"));

		// add panels
		add(mainPanel);
		mainPanel.add(mediaInputPanel);
		mainPanel.add(getButtonPanel());
		setLabels();

		// style
		addStyleName("GeoGebraPopup");
		addStyleName("mediaDialog");
		addStyleName("mebis");

		mediaInputPanel.focusDeferred();
	}

	/**
	 * set button labels
	 */
	public void setLabels() {
		updateButtonLabels("Insert");
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
		appW.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	protected void onMediaElementCreated(GeoElement geoElement) {
		getApplication().getActiveEuclidianView()
				.getEuclidianController().selectAndShowSelectionUI(geoElement);
	}
}
