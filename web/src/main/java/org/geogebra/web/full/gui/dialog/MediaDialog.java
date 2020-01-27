package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Audio / video dialog.
 *
 * @author Zbynek
 */
public abstract class MediaDialog extends OptionDialog {

	/** http prefix */
	private static final String HTTP = "http://";
	/** https prefix */
	private static final String HTTPS = "https://";

	protected AppW appW;

	protected MediaInputPanel mediaInputPanel;

	/**
	 * @param root
	 *            popup root
	 * @param app
	 *            app
	 */
	public MediaDialog(Panel root, AppW app) {
		super(root, app);
		this.appW = app;
		initGui();
	}

	protected void initGui() {
		FlowPanel mainPanel = new FlowPanel();

		mediaInputPanel = new MediaInputPanel(appW, this,
				app.getLocalization().getMenu("Link"), true);
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
		String value = isHTTPSOnly() ? url.replaceFirst(HTTP, "") : url;

		if (!url.startsWith(HTTPS) && !url.startsWith("data:")) {
			value = HTTPS + value;
		}
		return value;
	}

	/**
	 *
	 * @return if accepted URLs are HTTPS only or not.
	 */
	private static boolean isHTTPSOnly() {
		return true;
	}

	@Override
	public void hide() {
		appW.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	protected void onMediaElementCreated(GeoElement geoElement) {
		getApplication().getActiveEuclidianView()
				.getEuclidianController().selectAndShowBoundingBox(geoElement);
	}
}
