package org.geogebra.web.full.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.euclidian.draw.DrawWidget;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OpenFileListener;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.html5.Sandbox;
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
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
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
	private HashMap<DrawWidget, EmbedElement> widgets = new HashMap<>();
	// cache for undo: index by embed ID, drawables will change on reload
	private HashMap<Integer, EmbedElement> cache = new HashMap<>();

	private int counter;
	private HashMap<Integer, String> content = new HashMap<>();
	private HashMap<Integer, String> base64 = new HashMap<>();

	/**
	 * @param app
	 *            application
	 */
	EmbedManagerW(AppWFull app) {
		this.app = app;
		this.counter = 0;
	}

	@Override
	public void add(final DrawEmbed drawEmbed) {
		if (widgets.get(drawEmbed) != null) {
			return;
		}
		if ("extension".equals(drawEmbed.getGeoEmbed().getAppName())) {
			addExtension(drawEmbed);
			if (content.get(drawEmbed.getEmbedID()) != null) {
				widgets.get(drawEmbed)
						.setContent(content.get(drawEmbed.getEmbedID()));
			}
		} else {
			addCalcEmbed(drawEmbed);
		}
	}

	@Override
	public void setLayer(DrawWidget embed, int layer) {
		Element element;
		if (embed instanceof DrawVideo) {
			if (!app.getVideoManager().hasPlayer((DrawVideo) embed)) {
				return;
			}
			element = app.getVideoManager().getElement((DrawVideo) embed);
		} else {
			element = widgets.get(embed).getGreatParent().getElement();
		}
		if (element.hasClassName("background")) {
			element.getStyle().setZIndex(layer);
		}
	}

	private void addCalcEmbed(DrawEmbed drawEmbed) {
		CalcEmbedElement element = getCalcEmbed(drawEmbed);
		widgets.put(drawEmbed, element);
	}

	private CalcEmbedElement getCalcEmbed(DrawEmbed drawEmbed) {
		CalcEmbedElement element;
		if (cache.containsKey(drawEmbed.getEmbedID())) {
			element = (CalcEmbedElement) cache.get(drawEmbed.getEmbedID());
			element.setVisible(true);
		} else {
			element = createCalcEmbed(drawEmbed);
		}
		return element;
	}

	private CalcEmbedElement createCalcEmbed(DrawEmbed drawEmbed) {
		TestArticleElement parameters = new TestArticleElement("", "graphing");
		GeoGebraFrameFull fr = new GeoGebraFrameFull(
				(AppletFactory) GWT.create(AppletFactory.class), app.getLAF(),
				app.getDevice(), parameters);

		parameters
				.attr("scaleContainerClass", "embedContainer")
				.attr("allowUpscale", "true")
				.attr("width", drawEmbed.getGeoEmbed().getContentWidth() + "")
				.attr("height", drawEmbed.getGeoEmbed().getContentHeight() + "")
				.attr("appName", drawEmbed.getGeoEmbed().getAppName())
				.attr("borderColor", "#CCC");
		for (Entry<String, String> entry: drawEmbed.getGeoEmbed().getSettings()) {
			parameters.attr(entry.getKey(), entry.getValue());
		}
		String currentBase64 = base64.get(drawEmbed.getEmbedID());
		if (currentBase64 != null) {
			parameters.attr("appName", "auto")
					.attr("ggbBase64", currentBase64);
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
		CalcEmbedElement element = new CalcEmbedElement(fr, this, drawEmbed.getEmbedID());
		if (currentBase64 != null) {
			fr.getApp().registerOpenFileListener(
					getListener(drawEmbed, parameters));
		} else if (content.get(drawEmbed.getEmbedID()) != null) {
			boolean oldWidget = hasWidgetWithId(drawEmbed.getEmbedID());
			fr.getApp().getGgbApi().setFileJSON(
					JSON.parse(content.get(drawEmbed.getEmbedID())));
			if (oldWidget) {
				drawEmbed.getGeoEmbed().setEmbedId(nextID());
			}
		}
		return element;
	}

	private boolean hasWidgetWithId(int embedId) {
		for (DrawWidget drawable: widgets.keySet()) {
			if (drawable.getEmbedID() == embedId) {
				return true;
			}
		}
		return false;
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

		EmbedElement old = cache.get(drawEmbed.getEmbedID());
		if (old == null) {
			EmbedElement value = drawEmbed.getGeoEmbed().isGraspableMath()
					? new GraspableEmbedElement(parentPanel, this)
					: new EmbedElement(parentPanel);
			widgets.put(drawEmbed, value);
			value.addListeners(drawEmbed.getEmbedID());
		} else {
			old.setVisible(true);
			widgets.put(drawEmbed, old);
			cache.remove(drawEmbed.getEmbedID());
			// the cached widget is in correct state
			content.remove(drawEmbed.getEmbedID());
		}
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
		frame.getElement().setAttribute("sandbox", Sandbox.embeds());
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
		style.setProperty("transformOrigin", "0 0");
		style.setProperty("transform", "rotate(" + drawEmbed.getGeoElement().getAngle() + "rad)");
		if (drawEmbed.getWidth() > 0) {
			embedElement.getGreatParent().setSize(
					drawEmbed.getWidth() + "px",
					drawEmbed.getHeight() + "px");
			// above the oject canvas (50) and below MOW toolbar (51)
			toggleBackground(embedElement, drawEmbed);
			int contentWidth = drawEmbed.getGeoEmbed().getContentWidth();
			int contentHeight = drawEmbed.getGeoEmbed().getContentHeight();
			embedElement.setSize(contentWidth, contentHeight);
		}
	}

	private void toggleBackground(EmbedElement frame,
			DrawWidget drawEmbed) {
		boolean background = drawEmbed.isBackground();
		Dom.toggleClass(frame.getGreatParent(), "background",
				background);
		if (!background) {
			app.getMaskWidgets().masksToForeground();
			frame.getGreatParent().getElement().getStyle().clearZIndex();
		}
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
		for (Entry<DrawWidget, EmbedElement> entry : widgets.entrySet()) {
			cache.put(entry.getKey().getEmbedID(),
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
		List<Integer> entries = new ArrayList<>();
		for (Entry<Integer, EmbedElement> entry : cache.entrySet()) {
			GeoElement geoEmbed = findById(entry.getKey());
			DrawEmbed drawEmbed = (DrawEmbed) app.getActiveEuclidianView()
					.getDrawableFor(geoEmbed);
			if (drawEmbed != null) {
				EmbedElement frame = entry.getValue();
				widgets.put(drawEmbed, frame);
				frame.setVisible(true);
				entries.add(entry.getKey());
			}
		}
		for (Integer entry: entries) {
			cache.remove(entry);
		}
	}

	private GeoElement findById(Integer key) {
		Set<GeoElement> set = app.getKernel().getConstruction()
				.getGeoSetConstructionOrder();
		for (GeoElement geo : set) {
			if (geo instanceof GeoEmbed
					&& ((GeoEmbed) geo).getEmbedID() == key) {
				return geo;
			}
		}
		return null;
	}

	private static void removeFrame(EmbedElement frame) {
		frame.getGreatParent().removeFromParent();
		frame.getGreatParent().getElement().removeFromParent();
	}

	@Override
	public void remove(DrawEmbed draw) {
		EmbedElement frame = widgets.get(draw);
		draw.getGeoEmbed().setBackground(true);
		toggleBackground(frame, draw);
		frame.setVisible(false);
		widgets.remove(draw);
		cache.put(draw.getEmbedID(), frame);
	}

	@Override
	public void persist() {
		for (Entry<DrawWidget, EmbedElement> e : widgets.entrySet()) {
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
		for (GeoElement geo : cons.getGeoSetConstructionOrder()) {
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
					int id = Integer.parseInt(entry.getKey().split("[_.]")[1]);
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
		for (Entry<DrawWidget, EmbedElement> e : widgets.entrySet()) {
			e.getKey().setBackground(true);
			toggleBackground(e.getValue(), e.getKey());
		}
	}

	@Override
	public void play(GeoEmbed lastVideo) {
		EuclidianView ev = app.getActiveEuclidianView();
		DrawableND de = ev.getDrawableFor(lastVideo);
		if (de instanceof DrawEmbed) {
			lastVideo.setBackground(false);
			toggleBackground(widgets.get(de), (DrawEmbed) de);
		}
	}

	@Override
	public void embed(Material material) {
		int id = nextID();
		base64.put(id, material.getBase64());
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setContentWidth(material.getWidth());
		ge.setContentHeight(material.getHeight());
		ge.attr("showToolBar", material.getShowToolbar() || material.getShowMenu());
		ge.attr("showMenuBar", material.getShowMenu());
		ge.attr("allowStyleBar", material.getAllowStylebar());
		ge.attr("showAlgebraInput", material.getShowInputbar());
		ge.setEmbedId(id);
		showAndSelect(ge);
	}

	private void showAndSelect(final GeoEmbed ge) {
		ge.initPosition(app.getActiveEuclidianView());
		ge.setLabel(null);
		app.storeUndoInfo();
		app.invokeLater(new Runnable() {

			@Override
			public void run() {
				app.getActiveEuclidianView().getEuclidianController()
						.selectAndShowSelectionUI(ge);
			}
		});
	}

	@Override
	public MyImage getPreview(DrawEmbed drawEmbed) {
		SVGResource resource = getSvgPlaceholder(drawEmbed);

		return new MyImageW(ImageManagerW.getInternalImage(
				resource), true);

	}

	private SVGResource getSvgPlaceholder(DrawEmbed drawEmbed) {
		switch (drawEmbed.getGeoEmbed().getAppName()) {
			case "graphing":
				return SvgPerspectiveResources.INSTANCE.menu_icon_algebra_transparent();
			case "cas":
				return SvgPerspectiveResources.INSTANCE.menu_icon_cas_transparent();
			default: return ToolbarSvgResourcesSync.INSTANCE.mode_extension();
		}
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
		for (Entry<DrawWidget, EmbedElement> entry : widgets.entrySet()) {
			if (entry.getKey().getEmbedID() == embedId) {
				entry.getValue().executeAction(action);
			}
		}
	}

	@Override
	public void executeAction(EventType action) {
		restoreEmbeds();
		for (Entry<DrawWidget, EmbedElement> entry : widgets.entrySet()) {
			entry.getValue().executeAction(action);
		}
	}

	@Override
	public void openGraspableMTool() {
		openTool("https://graspablemath.com");
	}

	@Override
	public void initAppEmbed(GeoEmbed ge) {
		ge.setEmbedId(nextID());
		ge.attr("showToolBar", true);
		ge.attr("showAlgebraInput", true);
		ge.attr("allowStyleBar", true);
	}

	private void openTool(String URL) {
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setUrl(URL);
		ge.setAppName("extension");
		ge.setEmbedId(nextID());
		showAndSelect(ge);
	}

	/**
	 * @return script manager of the top level app
	 */
	public ScriptManagerW getScriptManager() {
		return (ScriptManagerW) app.getScriptManager();
	}

	/**
	 *
	 * @return the APIs of the embedded calculators.
	 */
	JavaScriptObject getEmbeddedCalculators() {
		JavaScriptObject jso = JavaScriptObject.createObject();

		for (Entry<DrawWidget, EmbedElement> entry : widgets.entrySet()) {
			EmbedElement embedElement = entry.getValue();
			if (embedElement instanceof CalcEmbedElement) {
				JavaScriptObject api = ((CalcEmbedElement) embedElement)
						.getApi();
				pushApisIntoNativeEntry(
						entry.getKey().getGeoElement().getLabelSimple(), api,
						jso);
			}
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
