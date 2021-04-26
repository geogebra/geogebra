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
import org.geogebra.common.main.undo.ActionExecutor;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.ToolbarSvgResourcesSync;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.html5.Sandbox;
import org.geogebra.web.full.main.embed.CalcEmbedElement;
import org.geogebra.web.full.main.embed.EmbedElement;
import org.geogebra.web.full.main.embed.GraspableEmbedElement;
import org.geogebra.web.full.main.embed.H5PEmbedElement;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import elemental2.core.Global;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Creates, deletes and resizes embedded applets.
 *
 * @author Zbynek
 *
 */
public class EmbedManagerW implements EmbedManager, EventRenderable, ActionExecutor {

	private AppWFull app;
	private HashMap<DrawWidget, EmbedElement> widgets = new HashMap<>();
	// cache for undo: index by embed ID, drawables will change on reload
	private HashMap<Integer, EmbedElement> cache = new HashMap<>();

	private int counter;
	private HashMap<Integer, String> content = new HashMap<>();
	private HashMap<Integer, String> base64 = new HashMap<>();
	private final HashMap<GeoElement, Runnable> errorHandlers = new HashMap<>();

	/**
	 * @param app
	 *            application
	 */
	EmbedManagerW(AppWFull app) {
		this.app = app;
		this.counter = 0;
		app.getLoginOperation().getView().add(this);
		app.getUndoManager().addActionExecutor(this);
	}

	@Override
	public void add(final DrawEmbed drawEmbed) {
		if (widgets.get(drawEmbed) != null) {
			return;
		}
		int embedID = drawEmbed.getEmbedID();
		counter = Math.max(counter, embedID + 1);
		String appName = drawEmbed.getGeoEmbed().getAppName();
		if ("extension".equals(appName)) {
			addExtension(drawEmbed);
			if (content.get(embedID) != null) {
				widgets.get(drawEmbed)
						.setContent(content.get(embedID));
			}
		} else {
			addEmbed(drawEmbed);
		}
	}

	private H5PEmbedElement createH5PEmbed(DrawEmbed drawEmbed) {
		int embedID = drawEmbed.getEmbedID();
		FlowPanel container = createH5PContainer(embedID);
		addWidgetToCache(drawEmbed, container);
		widgets.get(drawEmbed).setContent(drawEmbed.getGeoEmbed().getURL());
		return (H5PEmbedElement) widgets.get(drawEmbed);
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

	private void addEmbed(DrawEmbed drawEmbed) {
		EmbedElement element;
		if (cache.containsKey(drawEmbed.getEmbedID())) {
			element = cache.get(drawEmbed.getEmbedID());
			element.setVisible(true);
		} else {
			if ("h5p".equals(drawEmbed.getGeoEmbed().getAppName())) {
				element = createH5PEmbed(drawEmbed);
			} else {
				element = createCalcEmbed(drawEmbed);
			}
		}
		widgets.put(drawEmbed, element);
		cache.remove(drawEmbed.getEmbedID());
	}

	private CalcEmbedElement createCalcEmbed(DrawEmbed drawEmbed) {
		FlowPanel scaler = new FlowPanel();
		addToGraphics(scaler);

		FlowPanel parent = new FlowPanel();
		scaler.add(parent);

		AppletParameters parameters = new AppletParameters("graphing");
		GeoGebraFrameFull fr = new GeoGebraFrameFull(
				app.getAppletFrame().getAppletFactory(), app.getLAF(),
				app.getDevice(), GeoGebraElement.as(parent.getElement()), parameters);
		scaler.add(fr);

		parameters.setAttribute("scaleContainerClass", "embedContainer")
				.setAttribute("allowUpscale", "true")
				.setAttribute("width", drawEmbed.getGeoEmbed().getContentWidth() + "")
				.setAttribute("height", drawEmbed.getGeoEmbed().getContentHeight() + "")
				.setAttribute("appName", drawEmbed.getGeoEmbed().getAppName())
				.setAttribute("borderColor", "#CCC");
		for (Entry<String, String> entry: drawEmbed.getGeoEmbed().getSettings()) {
			parameters.setAttribute(entry.getKey(), entry.getValue());
		}
		String currentBase64 = base64.get(drawEmbed.getEmbedID());
		if (currentBase64 != null) {
			parameters.setAttribute("ggbBase64", currentBase64);
		}
		fr.setComputedWidth(parameters.getDataParamWidth());
		fr.setComputedHeight(parameters.getDataParamHeight());
		fr.runAsyncAfterSplash();

		CalcEmbedElement element = new CalcEmbedElement(fr, this, drawEmbed.getEmbedID());
		addDragHandler(Js.uncheckedCast(fr.getElement()));

		element.setJsEnabled(isJsEnabled());
		AppWFull appEmbedded = fr.getApp();
		if (currentBase64 != null) {
			appEmbedded.registerOpenFileListener(
					getListener(drawEmbed, parameters, appEmbedded));
			appEmbedded.getScriptManager().disableListeners();
		} else if (content.get(drawEmbed.getEmbedID()) != null) {
			boolean oldWidget = hasWidgetWithId(drawEmbed.getEmbedID());
			appEmbedded.getGgbApi().setFileJSON(
					Global.JSON.parse(content.get(drawEmbed.getEmbedID())));
			if (oldWidget) {
				drawEmbed.getGeoEmbed().setEmbedId(nextID());
			}
		}
		return element;
	}

	private void addDragHandler(elemental2.dom.Element element) {
		Style evPanelStyle = ((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getCanvasElement().getParentElement().getStyle();

		element.addEventListener("dragstart", (event) -> {
			evPanelStyle.setProperty("pointerEvents", "none");
		});

		element.addEventListener("dragend", (event) -> {
			evPanelStyle.setProperty("pointerEvents", "initial");
		});
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
		addWidgetToCache(drawEmbed, parentPanel);
	}

	private void addWidgetToCache(DrawEmbed drawEmbed, Widget parentPanel) {
		FlowPanel scaler = new FlowPanel();
		scaler.add(parentPanel);
		scaler.setHeight("100%");
		addToGraphics(scaler);

		EmbedElement old = cache.get(drawEmbed.getEmbedID());
		if (old == null) {
			EmbedElement embed = createEmbedElement(drawEmbed, parentPanel);
			widgets.put(drawEmbed, embed);
			embed.addListeners(drawEmbed.getEmbedID());
		} else {
			old.setVisible(true);
			widgets.put(drawEmbed, old);
			cache.remove(drawEmbed.getEmbedID());
			// the cached widget is in correct state
			content.remove(drawEmbed.getEmbedID());
		}
	}

	private EmbedElement createEmbedElement(DrawEmbed drawEmbed, Widget parentPanel) {
		GeoEmbed geoEmbed = drawEmbed.getGeoEmbed();
		if (geoEmbed.isGraspableMath()) {
			return new GraspableEmbedElement(parentPanel, this);
		} else if (geoEmbed.isH5P()) {
			return new H5PEmbedElement(parentPanel, geoEmbed);
		} else {
			return new EmbedElement(parentPanel);
		}
	}

	private static Widget createParentPanel(DrawEmbed embed) {

		GeoEmbed ge = embed.getGeoEmbed();

		if ("h5p".equals(ge.getAppName())) {
			return createH5PContainer(embed.getEmbedID());
		}

		String url = ge.getURL();

		if (url != null && url.contains("graspablemath.com")) {
			return createGraspableMathContainer(embed);
		}

		Frame frame = new Frame();
		frame.setUrl(url);
		frame.getElement().setAttribute("sandbox", Sandbox.embeds());
		return frame;
	}

	private static FlowPanel createGraspableMathContainer(DrawEmbed embed) {
		FlowPanel panel = new FlowPanel();
		String id = "gm-div" + embed.getEmbedID();
		panel.getElement().setId(id);
		panel.getElement().addClassName("gwt-Frame");
		return panel;
	}

	private static FlowPanel createH5PContainer(int embedID) {
		FlowPanel container = new FlowPanel();
		String id = "h5p-content" + embedID;
		container.addStyleName("h5pEmbed");
		container.getElement().setId(id);
		return container;
	}

	private static OpenFileListener getListener(final DrawEmbed drawEmbed,
			final AppletParameters parameters, final AppWFull fr) {
		return () -> {
			drawEmbed.getGeoEmbed()
					.setAppName(parameters.getDataParamAppName());
			fr.getScriptManager().enableListeners();
			return true;
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
					(int) drawEmbed.getWidth() + "px",
					(int) drawEmbed.getHeight() + "px");
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

	/**
	 * Get the embed element with a given id
	 * @param id embed id to find
	 * @return GeoEmbed, if found, null otherwise
	 */
	public GeoElement findById(int id) {
		Set<GeoElement> set = app.getKernel().getConstruction()
				.getGeoSetConstructionOrder();
		for (GeoElement geo : set) {
			if (geo instanceof GeoEmbed
					&& ((GeoEmbed) geo).getEmbedID() == id) {
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
					setContent(id, entry.getValue());
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
		ge.setSize(material.getWidth(), material.getHeight());
		ge.setContentWidth(material.getWidth());
		ge.setContentHeight(material.getHeight());
		ge.setAppName(StringUtil.empty(material.getAppName()) ? "auto" : material.getAppName());
		ge.attr("showToolBar", material.getShowToolbar() || material.getShowMenu());
		ge.attr("showMenuBar", material.getShowMenu());
		ge.attr("allowStyleBar", material.getAllowStylebar());
		ge.attr("showAlgebraInput", material.getShowInputbar());
		ge.setEmbedId(id);
		ge.initPosition(app.getActiveEuclidianView());
		showAndSelect(ge);
		app.dispatchEvent(new Event(EventType.EMBEDDED_CONTENT_CHANGED, ge, material.getBase64()));
	}

	private void showAndSelect(final GeoEmbed ge) {
		ge.setLabel(null);
		app.storeUndoInfo();
		app.invokeLater(() -> app.getActiveEuclidianView().getEuclidianController()
				.selectAndShowSelectionUI(ge));
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
		app.getUndoManager().storeAction(EventType.EMBEDDED_STORE_UNDO,
				String.valueOf(id));
	}

	private void executeAction(EventType action, int embedId) {
		restoreEmbeds();
		for (Entry<DrawWidget, EmbedElement> entry: widgets.entrySet()) {
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
		GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setUrl("https://graspablemath.com");
		ge.setAppName("extension");
		ge.setEmbedId(nextID());
		ge.initDefaultPosition(app.getActiveEuclidianView());
		showAndSelect(ge);
	}

	@Override
	public GeoEmbed openH5PTool(Runnable onError) {
		int embedId = nextID();
		GeoEmbed geoEmbed = new GeoEmbed(app.getKernel().getConstruction());
		geoEmbed.setEmbedId(embedId);
		geoEmbed.setAppName("h5p");
		geoEmbed.setSize(600, 300);
		geoEmbed.initPosition(app.getActiveEuclidianView());
		geoEmbed.setLabel(null);
		errorHandlers.put(geoEmbed, onError);
		return geoEmbed;
	}

	@Override
	public void initAppEmbed(GeoEmbed ge) {
		ge.setEmbedId(nextID());
		ge.attr("showToolBar", true);
		ge.attr("showAlgebraInput", true);
		ge.attr("allowStyleBar", true);
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
	JsPropertyMap<Object> getEmbeddedCalculators(boolean includeGraspableMath) {
		JsPropertyMap<Object> jso = JsPropertyMap.of();

		for (Entry<DrawWidget, EmbedElement> entry : widgets.entrySet()) {
			Object api = entry.getValue().getApi();
			if (api != null && (includeGraspableMath
					|| entry.getValue() instanceof CalcEmbedElement)) {
				jso.set(entry.getKey().getGeoElement().getLabelSimple(), api);
			}
		}

		return jso;
	}

	@Override
	public String getContent(int embedID) {
		return content.get(embedID);
	}

	@Override
	public void setContent(int id, String content) {
		counter = Math.max(counter, id + 1);
		this.content.put(id, content);
	}

	@Override
	public void renderEvent(BaseEvent event) {
		for (Entry<DrawWidget, EmbedElement> e : widgets.entrySet()) {
			e.getValue().setJsEnabled(isJsEnabled());
		}
	}

	private boolean isJsEnabled() {
		return !app.isMebis()
				|| app.getLoginOperation().isTeacherLoggedIn();
	}

	@Override
	public boolean executeAction(EventType action, String[] args) {
		if (action == EventType.EMBEDDED_STORE_UNDO) {
			embeddedAction(EventType.REDO, args[0]);
			return true;
		}
		return false;
	}

	@Override
	public boolean undoAction(EventType action, String... args) {
		if (action == EventType.EMBEDDED_STORE_UNDO) {
			embeddedAction(EventType.UNDO, args[0]);
			return true;
		}
		return false;
	}

	@Override
	public void embeddedAction(EventType action, String id) {
		try {
			int embedId = Integer.parseInt(id);
			executeAction(action, embedId);
		} catch (RuntimeException e) {
			Log.warn("No undo possible for embed " + id);
		}
	}

	@Override
	public void setContentSync(String label, String contentBase64) {
		GeoElement el = app.getKernel().lookupLabel(label);
		if (el instanceof GeoEmbed) {
			DrawableND de = app.getActiveEuclidianView().getDrawableFor(el);
			int embedID = ((GeoEmbed) el).getEmbedID();
			if (de instanceof DrawWidget && widgets.get(de) != null) {
				widgets.get(de).setContent(contentBase64);
			} else {
				base64.put(embedID, contentBase64);
			}
		}
	}

	@Override
	public void onError(GeoEmbed geoEmbed) {
		Runnable handler = errorHandlers.get(geoEmbed);
		if (handler != null) {
			handler.run();
		}
	}
}
