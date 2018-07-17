package org.geogebra.web.full.gui.dialog;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.media.MediaURLParser;
import org.geogebra.web.html5.main.AppW;

/**
 * @author csilla
 *
 */
public class EmbedInputDialog extends MediaDialog {

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public EmbedInputDialog(AppW app) {
		super(app.getPanel(), app);
	}

	/**
	 * set button labels and dialog title
	 */
	@Override
	public void setLabels() {
		super.setLabels();
		// dialog title
		getCaption().setText(appW.getLocalization().getMenu("Audio"));
	}

	@Override
	protected void processInput() {
		if (appW.getGuiManager() != null) {
			String url = getUrlWithProtocol();
			inputField.getTextComponent().setText(url);
			addEmbed(MediaURLParser.getEmbedURL(url));
		}
	}

	/**
	 * Adds the GeoEmbed instance.
	 * 
	 * @param url
	 *            embed URL
	 */
	void addEmbed(String url) {
		resetError();
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setUrl(url);
		ge.setAppName("extension");
		ge.initPosition(app.getActiveEuclidianView());
		ge.setEmbedId(app.getEmbedManager().nextID());
		ge.setLabel(null);
		app.setMode(EuclidianConstants.MODE_MOVE, ModeSetter.DOCK_PANEL);
		hide();
	}
}
