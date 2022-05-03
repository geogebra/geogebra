package org.geogebra.web.full.main;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.EmbedManager;
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
import org.geogebra.web.full.gui.dialog.MediaDialog;
import org.geogebra.web.full.gui.dialog.MediaInputPanel;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.MarvlURLChecker;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;

import elemental2.dom.DomGlobal;

public class EmbedFactory implements AsyncOperation<URLStatus>, MaterialCallbackI {
	private URLChecker urlChecker;
	final AppW app;
	private Runnable hideDialogCallback;
	private MediaInputPanel mediaInputPanel;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 * @param mediaInputPanel input text field
	 */
	public EmbedFactory(AppW app, MediaInputPanel mediaInputPanel) {
		this.app = app;
		this.mediaInputPanel = mediaInputPanel;
		initURLChecker();
	}

	/**
	 * @return url checker
	 */
	public URLChecker getUrlChecker() {
		return urlChecker;
	}

	private void initURLChecker() {
		if (DomGlobal.location.host != null
				&& DomGlobal.location.host.contains("geogebra")) {
			urlChecker = new EmbedURLChecker(app.getAppletParameters().getParamBackendURL());
		} else {
			urlChecker = new MarvlURLChecker();
		}
	}

	/**
	 * Adds the GeoEmbed instance.
	 */
	public void addEmbed() {
		String input = mediaInputPanel.getInput();
		mediaInputPanel.resetError();
		String url = extractURL(input);
		if (!input.startsWith("<")) {
			mediaInputPanel.setText(url);
		}
		String materialId = getGeoGebraMaterialId(url);
		if (!StringUtil.empty(materialId)) {
			getGeoGebraTubeAPI().getItem(materialId, this);
		} else {
			urlChecker.check(MediaURLParser.toEmbeddableUrl(url), this);
		}
	}

	private static String extractURL(String input) {
		if (input.startsWith("<")) {
			Element el = DOM.createDiv();
			el.setInnerHTML(input);
			NodeList<Element> frames = el.getElementsByTagName("iframe");
			if (frames.getLength() > 0) {
				return MediaDialog.addProtocol(frames.getItem(0).getAttribute("src"));
			}
		}
		return MediaDialog.addProtocol(input);
	}

	private GeoGebraTubeAPI getGeoGebraTubeAPI() {
		return new GeoGebraTubeAPIW(app.getClientInfo(),
				false, app.getAppletParameters());
	}

	private String getGeoGebraMaterialId(String url) {
		if (GeoGebraURLParser.isGeoGebraURL(url)) {
			return GeoGebraURLParser.getIDfromURL(url);
		}
		return null;
	}

	private GeoElement createAndShowEmbeddedElement(String url) {
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setUrl(url);
		ge.setAppName("extension");
		ge.initDefaultPosition(app.getActiveEuclidianView());
		EmbedManager embedManager = app.getEmbedManager();
		if (embedManager != null) {
			ge.setEmbedId(embedManager.nextID());
		}
		ge.setLabel(null);
		app.storeUndoInfo();

		return ge;
	}

	private void showEmptyEmbeddedElement() {
		createAndShowEmbeddedElement(extractURL(mediaInputPanel.getInput()));
	}

	private void embedGeoGebraAndHide(Material material) {
		EmbedManager embedManager = app.getEmbedManager();
		if (embedManager != null) {
			embedManager.embed(material);
			app.storeUndoInfo();
		}

		runHideCallback();
	}

	@Override
	public void callback(URLStatus obj) {
		if (obj.getErrorKey() == null) {
			GeoElement geo = createAndShowEmbeddedElement(obj.getUrl());
			runHideCallback();
			app.getActiveEuclidianView()
					.getEuclidianController().selectAndShowSelectionUI(geo);
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
		runHideCallback();
	}

	public void setHideDialogCallback(Runnable callback) {
		hideDialogCallback = callback;
	}

	/**
	 * hide dialog and set defined mode
	 */
	public void runHideCallback() {
		if (hideDialogCallback != null) {
			hideDialogCallback.run();
		}
	}
}
