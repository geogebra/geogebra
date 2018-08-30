package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.media.GeoGebraURLParser;
import org.geogebra.common.media.MediaURLParser;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

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
		getCaption().setText(appW.getLocalization().getMenu("Web"));
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

		if (GeoGebraURLParser.isGeoGebraURL(url)) {
			getGeoGebraTubeAPI().getItem(
					GeoGebraURLParser.getIDfromURL(url),
					new MaterialCallbackI() {

						@Override
						public void onLoaded(List<Material> result,
								ArrayList<Chapter> meta) {
							app.getEmbedManager()
									.embed(result.get(0).getBase64());
						}

						@Override
						public void onError(Throwable exception) {
							// TODO Auto-generated method stub
						}
					});
		} else {
			GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
			ge.setUrl(url);
			ge.setAppName("extension");
			ge.initPosition(app.getActiveEuclidianView());
			ge.setEmbedId(app.getEmbedManager().nextID());
			ge.setLabel(null);
		}

		app.setMode(EuclidianConstants.MODE_MOVE, ModeSetter.DOCK_PANEL);
		hide();
	}

	private GeoGebraTubeAPI getGeoGebraTubeAPI() {
		return new GeoGebraTubeAPIW(((AppW) app).getClientInfo(),
				false,
				((AppW) app).getArticleElement());
	}
}
