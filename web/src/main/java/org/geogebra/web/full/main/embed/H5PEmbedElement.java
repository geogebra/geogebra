package org.geogebra.web.full.main.embed;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.h5pviewer.H5P;
import org.geogebra.web.html5.util.h5pviewer.H5PPaths;
import org.gwtproject.timer.client.Timer;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLIFrameElement;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class H5PEmbedElement extends EmbedElement {
	private static final int H5P_INITIAL_HEIGHT = 150;
	private final Widget widget;
	private final GeoEmbed geoEmbed;
	private final int embedId;
	public static final int DEFAULT_WIDTH = 600;
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
		widget.addStyleName("h5pEmbed");
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
			Js.asPropertyMap(DomGlobal.window).set("myh5p", h5P);
			EmbedManager embedManager = app.getEmbedManager();
			if (embedManager != null) {
				embedManager.onLoaded(geoEmbed, this::update);
			}
			initializeSizingTimer();
			return null;
		});
	}

	private void initializeSizingTimer() {
		final HTMLIFrameElement frame = getFrame();
		// the resize event from H5P is not fired by the initial resize
		// use a timer to wait for height change instead
		Timer t = new Timer() {
			@Override
			public void run() {
				if (frame != null && frame.contentWindow != null
						&& frame.offsetHeight != H5P_INITIAL_HEIGHT) {
					geoEmbed.setSize(geoEmbed.getWidth(),
							Math.min(frame.offsetHeight, geoEmbed.getHeight()));
					geoEmbed.updateRepaint();
					cancel();
				}
			}
		};
		t.scheduleRepeating(100);
	}

	private void update() {
		double w = widget.getOffsetWidth();
		double h = widget.getOffsetHeight() ;
		double initialRatio = h / w;
		geoEmbed.setSize(DEFAULT_WIDTH, initialRatio * DEFAULT_WIDTH);
		geoEmbed.initPosition(euclidianController.getView());
		app.storeUndoInfo();
		DrawableND drawable = euclidianController.getView().getDrawableFor(geoEmbed);
		if (drawable != null) {
			drawable.update();
			euclidianController.selectAndShowSelectionUI(geoEmbed);
		}
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

	@Override
	public void setSize(int contentWidth, int contentHeight) {
		HTMLIFrameElement frame = getFrame();
		if (frame != null && frame.contentWindow != null) {
			H5PApi api = Js.uncheckedCast(Js.asPropertyMap(frame.contentWindow).get("H5P"));
			// We make the inside of the iframe non-scrollable;
			// there is a scrollbar for the whole widget
			frame.contentDocument.documentElement.style.overflow = "hidden";
			if (api != null) {
				api.instances.getAt(0).trigger("resize");
			}
		}
	}

	private HTMLIFrameElement getFrame() {
		Element iframe = Dom.querySelectorForElement(widget.getElement(), "iframe");
		if (iframe != null) {
			return Js.uncheckedCast(iframe);
		}
		return null;
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
