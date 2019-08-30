package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.media.GeoGebraURLParser;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.operations.URLChecker;
import org.geogebra.common.move.ggtapi.operations.URLStatus;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;

/**
 * @author csilla
 *
 */
public class EmbedInputDialog extends MediaDialog
		implements AsyncOperation<URLStatus> {

	private URLChecker urlChecker;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	EmbedInputDialog(AppW app, URLChecker urlChecker) {
		super(app.getPanel(), app);
		this.urlChecker = urlChecker;
		updateInfo();
	}

	private void updateInfo() {
		if (urlChecker != null && !urlChecker.hasFrameOptionCheck()) {
			showInfo(app.getLocalization().getMenu("EmbedFrameWarning"));
		}
	}

	/**
	 * set button labels and dialog title
	 */
	@Override
	public void setLabels() {
		super.setLabels();
		// dialog title
		getCaption().setText(appW.getLocalization().getMenu("Web"));
		updateInfo();
	}

	@Override
	protected void processInput() {
		if (appW.getGuiManager() != null) {
			String input = getInput();
			addEmbed(input);
		}
	}

	/**
	 * Adds the GeoEmbed instance.
	 *
	 * @param input
	 *            embed URL or code
	 */
	private void addEmbed(String input) {
		resetError();
		String url = extractURL(input);
		if (!input.startsWith("<")) {
			inputField.getTextComponent().setText(url);
		}
		if (GeoGebraURLParser.isGeoGebraURL(url)) {
			getGeoGebraTubeAPI().getItem(
					GeoGebraURLParser.getIDfromURL(url),
					new MaterialCallbackI() {

						@Override
						public void onLoaded(List<Material> result,
								ArrayList<Chapter> meta) {
							String base64 = result.get(0).getBase64();
							embedGeoGebraAndHide(base64);
						}

						@Override
						public void onError(Throwable exception) {
							showEmptyEmbeddedElement();
							hide();
						}
					});
		} else {
			urlChecker.check(url.replace("+", "%2B"), this);
		}
	}

	private void showEmptyEmbeddedElement() {
		showEmbeddedElement("");
	}

	private void showEmbeddedElement(String url) {
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setUrl(url);
		ge.setAppName("extension");
		ge.initPosition(app.getActiveEuclidianView());
		ge.setEmbedId(app.getEmbedManager().nextID());
		ge.setLabel(null);
		app.storeUndoInfo();
	}

	private void embedGeoGebraAndHide(String base64) {
		getApplication().getEmbedManager().embed(base64);
		app.storeUndoInfo();
		hide();
	}

	@Override
	public void hide() {
		super.hide();
		appW.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}

	private static String extractURL(String input) {
		if (input.startsWith("<")) {
			Element el = DOM.createDiv();
			el.setInnerHTML(input);
			NodeList<Element> frames = el.getElementsByTagName("iframe");
			if (frames.getLength() > 0) {
				return addProtocol(frames.getItem(0).getAttribute("src"));
			}
		}
		return addProtocol(input);
	}

	private GeoGebraTubeAPI getGeoGebraTubeAPI() {
		return new GeoGebraTubeAPIW(((AppW) app).getClientInfo(),
				false, ((AppW) app).getArticleElement());
	}

	@Override
	public void callback(URLStatus obj) {
		if (obj.getErrorKey() == null) {
			showEmbeddedElement(obj.getUrl());
			hide();
		} else {
			showError(obj.getErrorKey());
		}
	}

}
