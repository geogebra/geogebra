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
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.ImageManagerW;
import org.geogebra.web.html5.util.JSON;

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
	private HashMap<DrawEmbed, Widget> widgets = new HashMap<>();

	private int counter;
	private HashMap<Integer, String> content = new HashMap<>();
	private HashMap<Integer, String> base64 = new HashMap<>();
	private MyImage preview;

	/**
	 * @param app
	 *            application
	 */
	public EmbedManagerW(AppWFull app) {
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
				setContent(widgets.get(drawEmbed).getElement(),
						content.get(drawEmbed.getEmbedID()));
			}
			return;
		}
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(
				(AppletFactory) GWT.create(AppletFactory.class),
				app.getLAF(), app.getDevice(), false);
		TestArticleElement parameters = new TestArticleElement("",
				"graphing");
		fr.articleElement = parameters;
		parameters.attr("showToolBar", "true")
				.attr("scaleContainerClass", "embedContainer")
				.attr("allowUpscale", "true")
				.attr("showAlgebraInput", "true")
				.attr("width", drawEmbed.getGeoEmbed().getContentWidth() + "")
				.attr("height",
						drawEmbed.getGeoEmbed().getContentHeight() + "")
				.attr("appName", drawEmbed.getGeoEmbed().getAppName())
				.attr("allowStyleBar", "true");
		String currentBase64 = base64.get(drawEmbed.getEmbedID());
		if (currentBase64 != null) {
			parameters.attr("appName", "auto").attr("ggbBase64", currentBase64);

		}
		fr.setComputedWidth(
				parameters.getDataParamWidth()
						- parameters.getBorderThickness());
		fr.setComputedHeight(
				parameters.getDataParamHeight()
						- parameters.getBorderThickness());
		fr.runAsyncAfterSplash();

		FlowPanel scaler = new FlowPanel();
		scaler.add(fr);
		parameters.setParentElement(scaler.getElement());

		addToGraphics(scaler);
		if (currentBase64 != null) {
			fr.getApplication().registerOpenFileListener(
					getListener(drawEmbed, parameters));
		} else if (content.get(drawEmbed.getEmbedID()) != null) {
			fr.getApplication().getGgbApi().setFileJSON(
					JSON.parse(content.get(drawEmbed.getEmbedID())));
		}

		widgets.put(drawEmbed, fr);
	}

	private native void setContent(Element element, String string) /*-{
		$wnd.setTimeout(function() {
			element.contentWindow.postMessage({
				command : 'loadFromJSON',
				gmm_id : 3,
				args : {
					json : string
				}
			}, 'https://graspablemath.com');
		}, 5000);

	}-*/;

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
		Frame html = new Frame();
		FlowPanel scaler = new FlowPanel();
		String id = "gm-div" + drawEmbed.getEmbedID();
		html.getElement().setId(id);
		scaler.add(html);
		scaler.setHeight("100%");
		addToGraphics(scaler);

		html.setUrl(drawEmbed.getGeoEmbed().getURL());
		widgets.put(drawEmbed, html);
		addListeners(html.getElement(), drawEmbed.getEmbedID());
	}

	/**
	 * @param embedID
	 *            embed ID
	 * @param embedContent
	 *            JSON encoded content
	 */
	protected void storeContent(int embedID, String embedContent) {
		this.content.put(embedID, embedContent);
	}

	private native void addListeners(Element element, int id) /*-{
		$wnd.setTimeout(function() {
			element.contentWindow.postMessage({
				command : 'listen',
				gmm_id : 1,
				eventType : 'undoable-action'
			}, 'https://graspablemath.com');
		}, 5000);
		var that = this;
		window
				.addEventListener(
						'message',
						function(msg) {
							if (msg.data && msg.data.is_event) {
								element.contentWindow.postMessage({
									command : 'getAsJSON',
									gmm_id : 2
								}, 'https://graspablemath.com');
							} else {
								$wnd.console.log(msg);
								if (msg.data && msg.data.gmm_id == 2) {
									$wnd.console.log("store", msg.data.result);
									that.@org.geogebra.web.full.main.EmbedManagerW::storeContent(ILjava/lang/String;)(id,msg.data.result);
								}
							}
						});
	}-*/;

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
		Widget frame = widgets.get(drawEmbed);
		Style style = frame.getParent().getParent().getElement().getStyle();
		style.setTop(drawEmbed.getTop(), Unit.PX);
		style.setLeft(drawEmbed.getLeft(), Unit.PX);
		frame.getParent().getParent().setSize(
				Math.abs(drawEmbed.getWidth()) + "px",
				Math.abs(drawEmbed.getHeight()) + "px");
		// above the oject canvas (50) and below MOW toolbar (51)
		toggleBackground(frame, drawEmbed);
		if ("extension".equals(drawEmbed.getGeoEmbed().getAppName())) {
			updateExtension();
			return;
		}
		((GeoGebraFrameBoth) frame).getApplication().getGgbApi().setSize(
				(int) drawEmbed.getGeoEmbed().getContentWidth(),
				(int) drawEmbed.getGeoEmbed().getContentHeight());
		frame.getElement().getStyle()
				.setWidth((int) drawEmbed.getGeoEmbed().getContentWidth() - 2,
						Unit.PX);
		frame.getElement().getStyle()
				.setHeight((int) drawEmbed.getGeoEmbed().getContentHeight() - 2,
						Unit.PX);
		((GeoGebraFrameBoth) frame).getApplication().checkScaleContainer();
	}

	private void updateExtension() {
		// TODO Auto-generated method stub

	}

	private static void toggleBackground(Widget frame,
			DrawEmbed drawEmbed) {
		Dom.toggleClass(frame.getParent().getParent(), "background",
				drawEmbed.getGeoEmbed().isBackground());
	}

	@Override
	public void removeAll() {
		for (Widget frame : widgets.values()) {
			removeFrame(frame);
		}
		widgets.clear();
	}

	private static void removeFrame(Widget frame) {
		frame.getParent().getParent().removeFromParent();
		frame.getParent().getParent().getElement().removeFromParent();
	}

	@Override
	public void remove(DrawEmbed draw) {
		removeFrame(widgets.get(draw));
		widgets.remove(draw);
	}

	@Override
	public void persist() {
		for (Entry<DrawEmbed, Widget> e : widgets.entrySet()) {
			if (e.getValue() instanceof GeoGebraFrameBoth) {
				content.put(e.getKey().getEmbedID(),
						getContent((GeoGebraFrameBoth) e.getValue()));
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

	private static String getContent(GeoGebraFrameBoth value) {

		return JSON.stringify(
				value.getApplication().getGgbApi().getFileJSON(false));

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
		for (Entry<DrawEmbed, Widget> e : widgets.entrySet()) {
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

}
