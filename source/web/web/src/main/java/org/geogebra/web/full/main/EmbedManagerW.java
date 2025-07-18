package org.geogebra.web.full.main;

import static org.geogebra.common.GeoGebraConstants.SUITE_APPCODE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.CheckForNull;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.euclidian.draw.DrawVideo;
import org.geogebra.common.euclidian.draw.DrawWidget;
import org.geogebra.common.exam.ExamType;
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
import org.geogebra.common.plugin.ActionType;
import org.geogebra.common.plugin.Event;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.plugin.ScriptType;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.applet.GeoGebraFrameFull;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.full.html5.Sandbox;
import org.geogebra.web.full.main.embed.CalcEmbedElement;
import org.geogebra.web.full.main.embed.CustomEmbedElement;
import org.geogebra.web.full.main.embed.EmbedElement;
import org.geogebra.web.full.main.embed.EmbedResolver;
import org.geogebra.web.full.main.embed.GraspableEmbedElement;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.ScriptManagerW;
import org.geogebra.web.html5.util.AppletParameters;
import org.geogebra.web.html5.util.ArchiveEntry;
import org.geogebra.web.html5.util.GeoGebraElement;
import org.gwtproject.dom.client.Element;
import org.gwtproject.dom.client.Style;
import org.gwtproject.dom.style.shared.Unit;
import org.gwtproject.event.dom.client.DomEvent;
import org.gwtproject.event.dom.client.DragStartEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Frame;
import org.gwtproject.user.client.ui.Widget;

import elemental2.core.Global;
import elemental2.promise.Promise;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Creates, deletes and resizes embedded applets.
 *
 * @author Zbynek
 *
 */
public class EmbedManagerW implements EmbedManager, EventRenderable, ActionExecutor {

	private final AppWFull app;
	private final HashMap<DrawWidget, EmbedElement> widgets = new HashMap<>();
	// cache for undo: index by embed ID, drawables will change on reload
	private final HashMap<Integer, EmbedElement> cache = new HashMap<>();

	private int counter;
	private final HashMap<Integer, String> content = new HashMap<>();
	private final HashMap<Integer, String> urls = new HashMap<>();
	private final Set<DrawEmbed> pendingAddition = new HashSet<>();
	private final HashMap<String, EmbedResolver> customEmbedResolvers = new HashMap<>();
	private List<GeoEmbed> unresolvedEmbeds = new ArrayList<>();

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
		if (widgets.get(drawEmbed) != null || pendingAddition.contains(drawEmbed)) {
			return;
		}
		int embedID = drawEmbed.getEmbedID();
		counter = Math.max(counter, embedID + 1);
		String appName = drawEmbed.getGeoEmbed().getAppName();
		if ("extension".equals(appName) || "external".equals(appName)) {
			if (drawEmbed.getGeoEmbed().hasExternalProtocol()) {
				pendingAddition.add(drawEmbed);
				GeoEmbed customEmbed = drawEmbed.getGeoEmbed();
				if (hasResolverForType(customEmbed.getExternalType())) {
					resolveEmbed(customEmbed).then(content -> {
						addExtension(drawEmbed, content);
						return null;
					});
				} else {
					unresolvedEmbeds.add(customEmbed);
				}
			} else {
				addExtension(drawEmbed, "");
			}
			if (content.get(embedID) != null) {
				widgets.get(drawEmbed)
						.setContent(content.get(embedID));
			}
		} else {
			addEmbed(drawEmbed);
		}
	}

	@Override
	public void setLayer(DrawWidget embed, int layer) {
		Element element = null;
		if (embed instanceof DrawVideo) {
			if (!app.getVideoManager().hasPlayer((DrawVideo) embed)) {
				return;
			}
			element = app.getVideoManager().getElement((DrawVideo) embed);
		} else if (widgets.get(embed) != null) {
			element = widgets.get(embed).getGreatParent().getElement();
		}
		if (element != null && element.hasClassName("background")) {
			element.getStyle().setZIndex(layer);
		}
	}

	private void addEmbed(DrawEmbed drawEmbed) {
		EmbedElement element;
		if (cache.containsKey(drawEmbed.getEmbedID())) {
			element = cache.get(drawEmbed.getEmbedID());
			element.setVisible(true);
		} else {
			element = createCalcEmbed(drawEmbed);
		}
		widgets.put(drawEmbed, element);
		cache.remove(drawEmbed.getEmbedID());
	}

	private CalcEmbedElement createCalcEmbed(DrawEmbed drawEmbed) {
		ExamType examType = ExamType.byName(drawEmbed.getGeoEmbed().getAppName());
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
				.setAttribute("featureSet", app.getAppletParameters().getParamFeatureSet())
				.setAttribute("borderColor", "#CCC");
		if (examType != null) {
			parameters.setAttribute("appName", SUITE_APPCODE)
					.setAttribute("featureSet", examType.name().toLowerCase(Locale.ROOT));
		}
		for (Entry<String, String> entry: drawEmbed.getGeoEmbed().getSettings()) {
			parameters.setAttribute(entry.getKey(), entry.getValue());
		}
		String fileName = urls.get(drawEmbed.getEmbedID());
		if (fileName != null) {
			parameters.setAttribute("filename", fileName);
		}
		fr.setComputedWidth(parameters.getDataParamWidth());
		fr.setComputedHeight(parameters.getDataParamHeight());
		fr.setOnLoadCallback(exportedApi -> {
			Map<String, Object> jsonArgument = new HashMap<>();
			jsonArgument.put("api", exportedApi);
			jsonArgument.put("loadedWithFile", fileName != null);
			app.dispatchEvent(new Event(EventType.EMBED_LOADED, drawEmbed.getGeoEmbed())
					.setJsonArgument(jsonArgument));
		});
		String jsonContent = content.get(drawEmbed.getEmbedID());
		if (SUITE_APPCODE.equals(drawEmbed.getGeoEmbed().getAppName())) {
			parameters.setAttribute("preventFocus", "true");
		}
		fr.runAsyncAfterSplash();
		fr.getApp().getKernel().getConstruction().getLabelManager().setMultiuserSuffix(
				app.getKernel().getConstruction().getLabelManager().getMultiuserSuffix());

		CalcEmbedElement element = new CalcEmbedElement(fr, this, drawEmbed.getEmbedID());
		addDragHandler(Js.uncheckedCast(fr.getElement()));

		element.setJsEnabled(isJsEnabled(), isJsRunningEnabled());
		AppWFull appEmbedded = fr.getApp();
		if (fileName != null) {
			appEmbedded.registerOpenFileListener(
					getListener(drawEmbed, parameters, appEmbedded));
			appEmbedded.getEventDispatcher().disableListeners();
		} else if (jsonContent != null) {
			boolean oldWidget = hasWidgetWithId(drawEmbed.getEmbedID());
			appEmbedded.getGgbApi().setFileJSON(
					Global.JSON.parse(jsonContent));
			if (oldWidget) {
				drawEmbed.getGeoEmbed().setEmbedId(nextID());
			}
		}
		return element;
	}

	private void addDragHandler(elemental2.dom.Element element) {
		Style evPanelStyle = ((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getCanvasElement().getParentElement().getStyle();

		element.addEventListener("dragstart", (event) ->
				evPanelStyle.setProperty("pointerEvents", "none"));

		element.addEventListener("dragend", (event) ->
				evPanelStyle.setProperty("pointerEvents", "initial"));
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
		// do NOT block pointerup here, it is registered on window because of capturing
		Dom.addEventListener(container.getElement(), "pointerdown",
				elemental2.dom.Event::stopPropagation);
	}

	private void addExtension(DrawEmbed drawEmbed, String content) {
		pendingAddition.remove(drawEmbed);
		Widget parentPanel = createParentPanel(drawEmbed, content);
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
		} else if (geoEmbed.hasExternalProtocol()) {
			return new CustomEmbedElement(parentPanel);
		} else {
			return new EmbedElement(parentPanel);
		}
	}

	private static Widget createParentPanel(DrawEmbed embed, String content) {

		GeoEmbed ge = embed.getGeoEmbed();

		if (ge.hasExternalProtocol()) {
			FlowPanel container = createContainer(embed, "custom-embed");
			container.addDomHandler(DomEvent::preventDefault, DragStartEvent.getType());
			container.getElement().setInnerHTML(content);
			return container;
		}

		String url = ge.getURL();
		if (url != null && url.contains("graspablemath.com")) {
			return createContainer(embed, "gm-div");
		}

		Frame frame = new Frame();
		frame.setUrl(url);
		frame.getElement().setAttribute("sandbox", Sandbox.embeds());
		return frame;
	}

	private static FlowPanel createContainer(DrawEmbed embed, String idPrefix) {
		FlowPanel panel = new FlowPanel();
		String id = idPrefix + embed.getEmbedID();
		panel.getElement().setId(id);
		panel.getElement().addClassName("gwt-Frame");
		return panel;
	}

	private static OpenFileListener getListener(final DrawEmbed drawEmbed,
			final AppletParameters parameters, final AppWFull fr) {
		return () -> {
			drawEmbed.getGeoEmbed()
					.setAppName(parameters.getDataParamAppName());
			fr.getEventDispatcher().enableListeners();
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
			// above the object canvas (50) and below MOW toolbar (51)
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
		for (Entry<String, ArchiveEntry> entry : ((GgbFile) archive).entrySet()) {
			if (entry.getKey().startsWith("embed")) {
				try {
					int id = Integer.parseInt(entry.getKey().split("[_.]")[1]);
					setContent(id, entry.getValue().string); // always JSON
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
		urls.put(id, material.getFileName());
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
	}

	private void showAndSelect(final GeoEmbed ge) {
		ge.setLabel(null);
		app.storeUndoInfo();
		app.invokeLater(() -> app.getActiveEuclidianView().getEuclidianController()
				.selectAndShowSelectionUI(ge));
	}

	@Override
	public void drawPreview(GGraphics2D g2, DrawEmbed drawEmbed,
			int width, int height, double angle) {
		EmbedElement widget = widgets.get(drawEmbed);
		if (widget != null) {
			widget.drawPreview(g2, width, height, angle);
		}
	}

	/**
	 * Store undo action in undo manager
	 * 
	 * @param id
	 *            embed ID
	 */
	public void createUndoAction(int id) {
		String[] args = new String[]{String.valueOf(id)};
		app.getUndoManager().storeUndoableAction(ActionType.REDO,
				args, ActionType.UNDO, args);
	}

	private void executeAction(ActionType action, int embedId) {
		restoreEmbeds();
		for (Entry<DrawWidget, EmbedElement> entry: widgets.entrySet()) {
			if (entry.getKey().getEmbedID() == embedId) {
				entry.getValue().executeAction(action);
			}
		}
	}

	@Override
	public void executeAction(ActionType action) {
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
	public void initAppEmbed(GeoEmbed geoEmbed) {
		geoEmbed.setEmbedId(nextID());
		geoEmbed.attr("showToolBar", true);
		geoEmbed.attr("showAlgebraInput", true);
		geoEmbed.attr("allowStyleBar", true);
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
			e.getValue().setJsEnabled(isJsEnabled(), isJsRunningEnabled());
		}
	}

	private boolean isJsEnabled() {
		return !app.isMebis()
				|| app.getLoginOperation().isTeacherLoggedIn();
	}

	private boolean isJsRunningEnabled() {
		return app.getEventDispatcher().isDisabled(ScriptType.JAVASCRIPT);
	}

	@Override
	public boolean executeAction(ActionType action, String... args) {
		if (action == ActionType.UNDO || action == ActionType.REDO) {
			embeddedAction(action, args[0]);
			return true;
		}
		return false;
	}

	private void embeddedAction(ActionType action, String id) {
		try {
			int embedId = Integer.parseInt(id);
			executeAction(action, embedId);
		} catch (RuntimeException e) {
			Log.warn("No undo possible for embed " + id);
		}
	}

	@Override
	public void setContentSync(String label, String url) {
		GeoElement el = app.getKernel().lookupLabel(label);
		if (el instanceof GeoEmbed) {
			DrawableND de = app.getActiveEuclidianView().getDrawableFor(el);
			int embedID = ((GeoEmbed) el).getEmbedID();
			if (de instanceof DrawWidget && widgets.get(de) != null) {
				widgets.get(de).setContent(url);
			} else {
				urls.put(embedID, url);
			}
		}
	}

	@Override
	public void sendCommand(GeoEmbed chart, String cmd) {
		doIfCalcEmbed(chart, element -> element.sendCommand(cmd));
	}

	@Override
	public void setGraphAxis(GeoEmbed chart, int axis, double crossing) {
		doIfCalcEmbed(chart, element -> element.setGraphAxis(axis, crossing));
	}

	/**
	 * @param chart chart embed
	 * @param consumer consumer
	 */
	public void doIfCalcEmbed(GeoEmbed chart, Consumer<CalcEmbedElement> consumer) {
		DrawableND drawChart = app.getActiveEuclidianView().getDrawableFor(chart);
		EmbedElement el = widgets.get(drawChart);
		if (el == null) {
			el = cache.get(chart.getEmbedID());
		}
		if (el instanceof CalcEmbedElement) {
			consumer.accept((CalcEmbedElement) el);
		}
	}

	@Override
	public @CheckForNull App getEmbedApp(GeoEmbed embed) {
		DrawableND drawChart = app.getActiveEuclidianView().getDrawableFor(embed);
		if (drawChart == null) {
			return null;
		}
		CalcEmbedElement el = (CalcEmbedElement) widgets.get(drawChart);
		if (el == null) {
			el = (CalcEmbedElement) cache.get(embed.getEmbedID());
		}
		return el.getFrame().getApp();
	}

	@Override
	public void addCalcWithPreselectedApp(String appName, String subApp) {
		final GeoEmbed ge = new GeoEmbed(app.getKernel().getConstruction());
		ge.setAppName(appName);
		EuclidianView view = app.getActiveEuclidianView();
		ge.initDefaultPosition(view);
		initAppEmbed(ge);
		ge.attr("subApp", subApp);
		ge.setLabel(null);
		app.storeUndoInfo();
		app.invokeLater(() -> {
			view.getEuclidianController().selectAndShowSelectionUI(ge);
			ge.setBackground(false);
			view.update(ge); // force painting in the foreground
		});
	}

	@Override
	public void registerEmbedResolver(String type, Object callback) {
		EmbedResolver embedResolver = Js.uncheckedCast(callback);
		if (embedResolver != null) {
			customEmbedResolvers.put(type, embedResolver);
			onEmbedResolverRegistered(type, embedResolver);
		} else {
			Log.warn("Embed resolver should be a Promise");
		}
	}

	private void onEmbedResolverRegistered(String type, EmbedResolver resolver) {
		unresolvedEmbeds.stream().filter(embed -> embed.isTypeOf(type))
				.forEach((embed) -> resolveAndAdd(embed, resolver));
		unresolvedEmbeds = unresolvedEmbeds.stream().filter(embed -> !embed.isTypeOf(type))
				.collect(Collectors.toList());
	}

	private void resolveAndAdd(GeoEmbed embed, EmbedResolver resolver) {
		resolver.resolve(embed.getExternalId()).then(content -> {
			EuclidianView view = app.getActiveEuclidianView();
			DrawEmbed drawEmbed = (DrawEmbed) view.getDrawableFor(embed);
			if (drawEmbed != null) {
				addExtension(drawEmbed, content);
				CustomEmbedElement embedElement =
						(CustomEmbedElement) widgets.get(drawEmbed);
				embedElement.setInnerHTML(content);
			} else {
				embedCustomElement(embed);
			}

			view.update(embed);
			view.repaintView();
			return null;
		});
	}

	@Override
	public boolean insertEmbed(String type, String id) {
		GeoEmbed embed =
				new GeoEmbed(app.getKernel().getConstruction());
		embed.setExternalProtocol(type, id);
		embed.setEmbedId(nextID());
		if (!hasResolverForType(type)) {
			unresolvedEmbeds.add(embed);
			return false;
		}
		resolveEmbed(embed);
		return true;
	}

	private boolean hasResolverForType(String type) {
		return customEmbedResolvers.containsKey(type);
	}

	private Promise<String> resolveEmbed(GeoEmbed embed) {
		EmbedResolver resolver = customEmbedResolvers.get(embed.getExternalType());
		Promise<String> promise = resolver.resolve(embed.getExternalId());
		return promise.then((content) -> {
			embedCustomElement(embed);
			return Promise.resolve(content);
		});
	}

	private void embedCustomElement(GeoEmbed ge) {
		EuclidianView view = app.getActiveEuclidianView();
		if (!ge.hasLocation()) {
			ge.initDefaultPosition(view);
		}
		app.invokeLater(() -> {
			if (!ge.isLabelSet()) {
				showAndSelect(ge);
			}
			view.update(ge);
			view.repaintView();
		});
	}
}
