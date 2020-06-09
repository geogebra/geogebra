package org.geogebra.web.full.gui.dialog;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.media.EmbedURLChecker;
import org.geogebra.common.media.GeoGebraURLParser;
import org.geogebra.common.media.MediaURLParser;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.GeoGebraTubeAPI;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.operations.URLChecker;
import org.geogebra.common.move.ggtapi.operations.URLStatus;
import org.geogebra.common.move.ggtapi.requests.MaterialCallbackI;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.MarvlURLChecker;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

/**
 * @author csilla
 *
 */
public class EmbedInputDialog extends MediaDialog
		implements AsyncOperation<URLStatus>, MaterialCallbackI {

	private URLChecker urlChecker;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	EmbedInputDialog(AppWFull app) {
		super(app.getPanel(), app);
		if (Window.Location.getHost() != null
				&& Window.Location.getHost().contains("geogebra")) {
			urlChecker = new EmbedURLChecker(app.getArticleElement().getParamBackendURL());
		} else {
			urlChecker = new MarvlURLChecker();
		}

		mediaInputPanel.addInfoLabel();
		updateInfo();
	}

	private void updateInfo() {
		if (urlChecker != null && !urlChecker.hasFrameOptionCheck()) {
			mediaInputPanel.showInfo(app.getLocalization().getMenu("EmbedFrameWarning"));
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
			String input = mediaInputPanel.getInput();
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
		mediaInputPanel.resetError();
		String url = extractURL(input);
		if (!input.startsWith("<")) {
			mediaInputPanel.inputField.getTextComponent().setText(url);
		}
		String materialId = getGeoGebraMaterialId(url);
		if (!StringUtil.empty(materialId)) {
			getGeoGebraTubeAPI().getItem(materialId, this);
		} else {
			urlChecker.check(MediaURLParser.toEmbeddableUrl(url), this);
		}
	}

	private String getGeoGebraMaterialId(String url) {
		if (GeoGebraURLParser.isGeoGebraURL(url)) {
			return GeoGebraURLParser.getIDfromURL(url);
		}
		return null;
	}

	private void showEmptyEmbeddedElement() {
		createAndShowEmbeddedElement("");
	}

	private GeoElement createAndShowEmbeddedElement(String url) {
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setUrl(url);
		ge.setAppName("extension");
		ge.initPosition(app.getActiveEuclidianView());
		EmbedManager embedManager = appW.getEmbedManager();
		if (embedManager != null) {
			ge.setEmbedId(embedManager.nextID());
		}
		ge.setLabel(null);
		app.storeUndoInfo();

		return ge;
	}

	private void embedGeoGebraAndHide(Material material) {
		EmbedManager embedManager = appW.getEmbedManager();
		if (embedManager != null) {
			embedManager.embed(material);
			appW.storeUndoInfo();
		}

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
			GeoElement geo = createAndShowEmbeddedElement(obj.getUrl());
			hide();
			onMediaElementCreated(geo);
		} else {
			mediaInputPanel.showError(obj.getErrorKey());
		}
	}

	@Override
	public void onLoaded(List<Material> result, ArrayList<Chapter> meta) {
		if (result.size() < 1) {
			onError(null);
		} else {
			embedGeoGebraAndHide(result.get(0));
		}
	}

	@Override
	public void onError(Throwable exception) {
		showEmptyEmbeddedElement();
		hide();
	}
}
