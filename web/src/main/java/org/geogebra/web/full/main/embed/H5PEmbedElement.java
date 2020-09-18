package org.geogebra.web.full.main.embed;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.util.h5pviewer.H5P;
import org.geogebra.web.html5.util.h5pviewer.H5PPaths;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class H5PEmbedElement extends EmbedElement {
	private final Widget widget;
	private final GeoEmbed geoEmbed;
	public static final int BOTTOM_BAR = 48;
	private final int embedId;
	public static final int SCALE = 3;
	private final App app;
	private final EuclidianController euclidianController;
	private String url;

	/**
	 * @param widget UI widget
	 */
	public H5PEmbedElement(Widget widget, GeoEmbed geoEmbed) {
		super(widget);
		this.widget = widget;
		this.geoEmbed = geoEmbed;
		embedId = geoEmbed.getEmbedID();
		app = geoEmbed.getApp();
		euclidianController = app.getActiveEuclidianView().getEuclidianController();
		load();
	}

	@Override
	public void setContent(String url) {
		this.url = url;

		if (H5PLoader.isLoaded()) {
			render();
		}
	}

	private void render() {
		Element element = widget.getElement();
		if (element == null) {
			return;
		}

		H5P h5P = new H5P(Js.cast(element), url,
				getOptions(), getDisplayOptions());
		h5P.then(p -> {
			update();
			geoEmbed.initPosition(euclidianController.getView());
			app.storeUndoInfo();
			euclidianController.getView().getDrawableFor(geoEmbed).update();
			euclidianController.selectAndShowSelectionUI(geoEmbed);
			return null;
		});
	}

	private void update() {
		double w = widget.getOffsetWidth();
		double h = widget.getOffsetHeight() ;
		double initialRatio = h / w;
		double initialHeight = SCALE * initialRatio * w + BOTTOM_BAR;
		geoEmbed.setSize(SCALE * w, initialHeight);
	}

	private JsPropertyMap<Object> getOptions() {
		JsPropertyMap<Object> options = JsPropertyMap.of();
		options.set("id", "embed" + embedId);
		options.set("frameJs", H5PPaths.FRAME_JS);
		options.set("frameCss", H5PPaths.FRAME_CSS);
		return options;
	}

	private static JsPropertyMap<Object> getDisplayOptions() {
		return JsPropertyMap.of();
	}

	/**
	 *
	 * @return embed associated with the H5P content.
	 */
	public GeoEmbed getGeoEmbed() {
		return geoEmbed;
	}

	private void load() {
		if (H5PLoader.isLoaded()) {
			return;
		}
		H5PLoader.INSTANCE.load(this::render);
	}
}
