package org.geogebra.web.full.main;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.geogebra.common.euclidian.DrawableND;
import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.io.file.ZipFile;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoEmbed;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.TestArticleElement;
import org.geogebra.web.html5.util.Dom;
import org.geogebra.web.html5.util.JSON;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Creates, deletes and resizes embedded applets.
 * 
 * @author Zbynek
 *
 */
public class EmbedManagerW implements EmbedManager {

	private AppWFull app;
	private HashMap<DrawEmbed, GeoGebraFrameBoth> widgets = new HashMap<>();

	private int counter;
	private HashMap<Integer, String> content = new HashMap<>();

	/**
	 * @param app
	 *            application
	 */
	public EmbedManagerW(AppWFull app) {
		this.app = app;
		this.counter = 0;
	}

	@Override
	public void add(final DrawEmbed drawEmbed) {
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(
				(AppletFactory) GWT.create(AppletFactory.class),
				app.getLAF(), app.getDevice(), false);
		TestArticleElement parameters = new TestArticleElement("", "graphing");
		fr.ae = parameters;
		fr.setComputedWidth(fr.ae.getDataParamWidth());
		fr.setComputedHeight(fr.ae.getDataParamHeight());
		fr.ae.attr("showToolBar", "true")
				.attr("scaleContainerClass",
				"embedContainer").attr("allowUpscale", "true")
				.attr("showAlgebraInput", "true");
		fr.runAsyncAfterSplash();
		DockPanelW panel = ((DockManagerW) app.getGuiManager().getLayout()
				.getDockManager()).getPanel(App.VIEW_EUCLIDIAN);

		FlowPanel scaler = new FlowPanel();
		scaler.add(fr);
		parameters.setParentElement(scaler.getElement());
		FlowPanel container = new FlowPanel();
		container.add(scaler);
		container.getElement().addClassName("embedContainer");
		container.getElement().addClassName("mowWidget");
		((EuclidianDockPanelW) panel).getEuclidianPanel().add(container);


		if (content.get(drawEmbed.getEmbedID()) != null) {
			fr.getApplication().getGgbApi().setFileJSON(
					JSON.parse(content.get(drawEmbed.getEmbedID())));
		}
		// fr.getApplication().registerOpenFileListener(new OpenFileListener() {
		//
		// public void onOpenFile() {
		// update(drawEmbed);
		// }
		// });
		widgets.put(drawEmbed, fr);
	}

	@Override
	public void update(DrawEmbed drawEmbed) {
		GeoGebraFrameBoth frame = widgets.get(drawEmbed);
		Style style = frame.getParent().getParent().getElement().getStyle();
		style.setTop(drawEmbed.getTop(), Unit.PX);
		style.setLeft(drawEmbed.getLeft(), Unit.PX);
		frame.getParent().getParent().setSize(
				Math.abs(drawEmbed.getWidth()) + "px",
				Math.abs(drawEmbed.getHeight()) + "px");
		// above the oject canvas (50) and below MOW toolbar (51)
		toggleBackground(frame, drawEmbed);
		frame.getApplication().getGgbApi()
				.setSize((int) drawEmbed.getGeoEmbed().getContentWidth(),
						(int) drawEmbed.getGeoEmbed().getContentHeight());
		frame.getElement().getStyle()
				.setWidth((int) drawEmbed.getGeoEmbed().getContentWidth(),
						Unit.PX);
		frame.getElement().getStyle()
				.setHeight((int) drawEmbed.getGeoEmbed().getContentHeight(),
						Unit.PX);
		frame.getApplication().checkScaleContainer();
	}

	private static void toggleBackground(GeoGebraFrameBoth frame,
			DrawEmbed drawEmbed) {
		Dom.toggleClass(frame.getParent().getParent(), "background",
				drawEmbed.isBackground());
	}

	@Override
	public void removeAll() {
		for (GeoGebraFrameBoth frame : widgets.values()) {
			removeFrame(frame);
		}
		widgets.clear();
	}

	private static void removeFrame(GeoGebraFrameBoth frame) {
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
		for (Entry<DrawEmbed, GeoGebraFrameBoth> e : widgets.entrySet()) {
			content.put(e.getKey().getEmbedID(), getContent(e.getValue()));
		}
	}

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
				((GgbFile) archiveContent).put("embed_" + id + ".json",
						content.get(id));
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

	public void backgroundAll() {
		for (Entry<DrawEmbed, GeoGebraFrameBoth> e : widgets.entrySet()) {
			e.getKey().setBackground(true);
			toggleBackground(e.getValue(), e.getKey());
		}

	}

	public void play(GeoEmbed lastVideo) {
		DrawableND de = app.getActiveEuclidianView()
				.getDrawableFor(lastVideo);
		if (de instanceof DrawEmbed) {
			((DrawEmbed) de).setBackground(false);
			toggleBackground(widgets.get(de), (DrawEmbed) de);
		}

	}
}
