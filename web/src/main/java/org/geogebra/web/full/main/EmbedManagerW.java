package org.geogebra.web.full.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.main.embed.CalcEmbedElement;
import org.geogebra.web.full.main.embed.EmbedElement;
import org.geogebra.web.full.main.embed.GraspableEmbedElement;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

/**
 * Creates, deletes and resizes embedded applets.
 *
 * @author Zbynek
 *
 */
public class EmbedManagerW implements EmbedManager {

	private AppWFull app;
	private HashMap<DrawEmbed, EmbedElement> widgets = new HashMap<>();
	// cache for undo: index by label, drawables will change on reload
	private HashMap<String, EmbedElement> cache = new HashMap<>();

	private int counter;
	private HashMap<Integer, String> content = new HashMap<>();
	private HashMap<Integer, String> base64 = new HashMap<>();
	private HashMap<String, JavaScriptObject> apis = new HashMap<>();
	private MyImage preview;

	/**
	 * @param app
	 *            application
	 */
	EmbedManagerW(AppWFull app) {
		this.app = app;
		this.counter = 0;
		preview = new MyImageW(ImageManagerW.getInternalImage(
				MaterialDesignResources.INSTANCE.graphing()), true);
	}

	@Override
	public void add(final DrawEmbed drawEmbed) {
		if ("extension".equals(drawEmbed.getGeoEmbed().getAppName())) {
			addExtension(drawEmbed);
			if (content.get(drawEmbed.getEmbedID()) != null) {
				widgets.get(drawEmbed)
						.setContent(content.get(drawEmbed.getEmbedID()));
			}
			return;
		}
		TestArticleElement parameters = new TestArticleElement("", "graphing");
		GeoGebraFrameFull fr = new GeoGebraFrameFull(
				(AppletFactory) GWT.create(AppletFactory.class), app.getLAF(),
				app.getDevice(), parameters);

		parameters.attr("showToolBar", "true")
				.attr("scaleContainerClass", "embedContainer")
				.attr("allowUpscale", "true").attr("showAlgebraInput", "true")
				.attr("width", drawEmbed.getGeoEmbed().getContentWidth() + "")
				.attr("height", drawEmbed.getGeoEmbed().getContentHeight() + "")
				.attr("appName", drawEmbed.getGeoEmbed().getAppName())
				.attr("allowStyleBar", "true");
		String currentBase64 = base64.get(drawEmbed.getEmbedID());
		if (currentBase64 != null) {
			parameters.attr("appName", "auto").attr("ggbBase64", currentBase64);
		}
		fr.setComputedWidth(parameters.getDataParamWidth()
				- parameters.getBorderThickness());
		fr.setComputedHeight(parameters.getDataParamHeight()
				- parameters.getBorderThickness());
		fr.runAsyncAfterSplash();

		FlowPanel scaler = new FlowPanel();
		scaler.add(fr);
		parameters.setParentElement(scaler.getElement());

		addToGraphics(scaler);
		CalcEmbedElement calcEmbedElement = new CalcEmbedElement(fr);
		if (currentBase64 != null) {
			fr.getApplication().registerOpenFileListener(
					getListener(drawEmbed, parameters));
		} else if (content.get(drawEmbed.getEmbedID()) != null) {
			fr.getApplication().getGgbApi().setFileJSON(
					JSON.parse(content.get(drawEmbed.getEmbedID())));
		}

		widgets.put(drawEmbed, calcEmbedElement);
		addApi(getAPILabel(drawEmbed), fr);
	}

	private void addApi(String label, GeoGebraFrameFull frame) {
		ScriptManagerW sm = (ScriptManagerW) frame.getApplication().getScriptManager();
		apis.put(label, sm.getApi());
	}

	private static String getAPILabel(DrawEmbed drawEmbed) {
		return drawEmbed.getGeoEmbed().getLabelSimple();
	}

	private void addToGraphics(FlowPanel scaler) {
		FlowPanel container = new FlowPanel();
		container.add(scaler);
		container.getElement().addClassName("embedContainer");
		container.getElement().addClassName("mowWidget");
		DockPanelW panel = app.getGuiManager().getLayout().getDockManager()
				.getPanel(App.VIEW_EUCLIDIAN);
		((EuclidianDockPanelW) panel).getEuclidianPanel().add(container);
	}

	private void addExtension(DrawEmbed drawEmbed) {
		Widget parentPanel = createParentPanel(drawEmbed);
		FlowPanel scaler = new FlowPanel();
		scaler.add(parentPanel);
		scaler.setHeight("100%");
		addToGraphics(scaler);

		String url = drawEmbed.getGeoEmbed().getURL();
		EmbedElement value = url.contains("graspablemath.com")
				? new GraspableEmbedElement(parentPanel, this)
				: new EmbedElement(parentPanel);
		widgets.put(drawEmbed, value);
		value.addListeners(drawEmbed.getEmbedID());
	}

	private static Widget createParentPanel(DrawEmbed embed) {
		String url = embed.getGeoEmbed().getURL();
		if (url.contains("graspablemath.com")) {
			FlowPanel panel = new FlowPanel();
			String id = "gm-div" + embed.getEmbedID();
			panel.getElement().setId(id);
			panel.getElement().addClassName("gwt-Frame");
			return panel;
		}
		Frame frame = new Frame();
		frame.setUrl(url);
		return frame;
	}

	private static OpenFileListener getListener(final DrawEmbed drawEmbed,
			final TestArticleElement parameters) {
		return new OpenFileListener() {

			@Override
			public boolean onOpenFile() {
				drawEmbed.getGeoEmbed()
						.setAppName(parameters.getDataParamAppName());
				return true;
			}
		};
	}

	@Override
	public void update(DrawEmbed drawEmbed) {
		EmbedElement embedElement = widgets.get(drawEmbed);
		if (embedElement == null) {
			return;
		}
		Style style = embedElement.getGreatParent().getElement().getStyle();
		style.setTop(drawEmbed.getTop(), Unit.PX);
		style.setLeft(drawEmbed.getLeft(), Unit.PX);
		embedElement.getGreatParent().setSize(
				Math.abs(drawEmbed.getWidth()) + "px",
				Math.abs(drawEmbed.getHeight()) + "px");
		// above the oject canvas (50) and below MOW toolbar (51)
		toggleBackground(embedElement, drawEmbed);
		int contentWidth = (int) drawEmbed.getGeoEmbed().getContentWidth();
		int contentHeight = (int) drawEmbed.getGeoEmbed().getContentHeight();
		embedElement.setSize(contentWidth, contentHeight);
	}

	private static void toggleBackground(EmbedElement frame,
			DrawEmbed drawEmbed) {
		Dom.toggleClass(frame.getGreatParent(), "background",
				drawEmbed.getGeoEmbed().isBackground());
	}

	@Override
	public void removeAll() {
		for (EmbedElement frame : widgets.values()) {
				removeFrame(frame);
		}
		widgets.clear();
	}

	@Override
	public void storeEmbeds() {
		for (Entry<DrawEmbed, EmbedElement> entry : widgets.entrySet()) {
			cache.put(entry.getKey().getGeoElement().getLabelSimple(),
					entry.getValue());
		}
		for (EmbedElement frame : widgets.values()) {
			frame.setVisible(false);
		}
		widgets.clear();
	}

	@Override
	public void clearStoredEmbeds() {
		for (EmbedElement frame : cache.values()) {
			removeFrame(frame);
		}
		cache.clear();
	}

	private void restoreEmbeds() {
		for (Entry<String, EmbedElement> entry : cache.entrySet()) {
			GeoElement geoEmbed = app.getKernel().lookupLabel(entry.getKey());
			DrawEmbed drawEmbed = (DrawEmbed) app.getActiveEuclidianView()
					.getDrawableFor(geoEmbed);
			EmbedElement frame = entry.getValue();
			widgets.put(drawEmbed, frame);
			frame.setVisible(true);
		}
		cache.clear();
	}

	private static void removeFrame(EmbedElement frame) {
		frame.getGreatParent().removeFromParent();
		frame.getGreatParent().getElement().removeFromParent();
	}

	@Override
	public void remove(DrawEmbed draw) {
		removeFrame(widgets.get(draw));
		widgets.remove(draw);
		apis.remove(getAPILabel(draw));
	}

	@Override
	public void persist() {
		for (Entry<DrawEmbed, EmbedElement> e : widgets.entrySet()) {
			String embedContent = e.getValue().getContentSync();
			if (embedContent != null) {
				content.put(e.getKey().getEmbedID(), embedContent);
			}
			// extensions have to update state asynchronously
		}
	}

	@Override
	public int nextID() {
		return counter++;
	}

	@Override
	public void writeEmbeds(Construction cons, ZipFile archiveContent) {
		persist();
		Iterator<GeoElement> it = cons.getGeoSetConstructionOrder().iterator();
		while (it.hasNext()) {
			GeoElement geo = it.next();
			if (geo instanceof GeoEmbed) {
				int id = ((GeoEmbed) geo).getEmbedID();
				String encoded = content.get(id);
				if (!StringUtil.empty(encoded)) {
					((GgbFile) archiveContent).put("embed_" + id + ".json",
							encoded);
				}
			}
		}
	}

	@Override
	public void loadEmbeds(ZipFile archive) {
		for (Entry<String, String> entry : ((GgbFile) archive).entrySet()) {
			if (entry.getKey().startsWith("embed")) {
				try {
					int id = Integer.parseInt(entry.getKey().split("_|\\.")[1]);
					counter = Math.max(counter, id + 1);
					content.put(id, entry.getValue());
				} catch (RuntimeException e) {
					Log.warn("Problem loading embed " + entry.getKey());
				}
			}
		}
	}

	@Override
	public void backgroundAll() {
		for (Entry<DrawEmbed, EmbedElement> e : widgets.entrySet()) {
			e.getKey().getGeoEmbed().setBackground(true);
			toggleBackground(e.getValue(), e.getKey());
		}
	}

	@Override
	public void play(GeoEmbed lastVideo) {
		DrawableND de = app.getActiveEuclidianView()
				.getDrawableFor(lastVideo);
		if (de instanceof DrawEmbed) {
			lastVideo.setBackground(false);
			toggleBackground(widgets.get(de), (DrawEmbed) de);
		}
	}

	@Override
	public void embed(String dataUrl) {
		int id = nextID();
		base64.put(id, dataUrl);
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setEmbedId(id);
		ge.initPosition(app.getActiveEuclidianView());
		ge.setLabel(null);
	}

	@Override
	public MyImage getPreview(DrawEmbed drawEmbed) {
		return preview;
	}

	/**
	 * Store undo action in undo manager
	 * 
	 * @param id
	 *            embed ID
	 */
	public void createUndoAction(int id) {
		app.getKernel().getConstruction().getUndoManager()
				.storeAction(EventType.EMBEDDED_STORE_UNDO,
				String.valueOf(id));
	}

	@Override
	public void executeAction(EventType action, int embedId) {
		restoreEmbeds();
		for (Entry<DrawEmbed, EmbedElement> entry : widgets.entrySet()) {
			if (entry.getKey().getEmbedID() == embedId) {
				entry.getValue().executeAction(action);
			}
		}
	}

	public AppWFull getApp() {
		return app;
	}

	/**
	 *
	 * @return the APIs of the embedded calculators.
	 */
	JavaScriptObject getEmbeddedCalculators() {
		JavaScriptObject jso = JavaScriptObject.createObject();

		for (Entry<String, JavaScriptObject> entry : apis.entrySet()) {
			pushApisIntoNativeEntry(entry.getKey(), entry.getValue(), jso);
		}
		return jso;
	}

	private static native void pushApisIntoNativeEntry(
			String embedName,
   			JavaScriptObject api,
			JavaScriptObject jso) /*-{
		jso[embedName] = api;
	}-*/;
}
