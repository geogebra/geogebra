package org.geogebra.web.full.main;

import java.util.HashMap;
import java.util.Map.Entry;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.main.App;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.html5.main.GgbFile;
import org.geogebra.web.html5.main.TestArticleElement;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
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
	private HashMap<Integer, GeoGebraFrameBoth> widgets = new HashMap<>();

	private int counter;
	private HashMap<Integer, String> base64 = new HashMap<>();

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
		fr.ae.attr("showToolbar", "true").attr("scaleContainerClass",
				"embedContainer").attr("allowUpscale", "true");
		fr.runAsyncAfterSplash();
		DockPanelW panel = ((DockManagerW) app.getGuiManager().getLayout()
				.getDockManager()).getPanel(App.VIEW_EUCLIDIAN);

		FlowPanel scaler = new FlowPanel();
		scaler.add(fr);
		parameters.setParentElement(scaler.getElement());
		FlowPanel container = new FlowPanel();
		container.add(scaler);
		container.getElement().addClassName("embedContainer");
		((EuclidianDockPanelW) panel).getEuclidianPanel().add(container);
		Style style = container.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setZIndex(51); // above the oject canvas (50) and below MOW
								// toolbar (51)
		if (base64.get(drawEmbed.getEmbedID()) != null) {
			fr.getApplication().getGgbApi()
					.setBase64(base64.get(drawEmbed.getEmbedID()));
		}
		// fr.getApplication().registerOpenFileListener(new OpenFileListener() {
		//
		// public void onOpenFile() {
		// update(drawEmbed);
		// }
		// });
		widgets.put(drawEmbed.getEmbedID(), fr);
	}

	@Override
	public void update(DrawEmbed drawEmbed) {
		GeoGebraFrameBoth frame = widgets.get(drawEmbed.getEmbedID());
		Style style = frame.getParent().getParent().getElement().getStyle();
		style.setTop(drawEmbed.getTop(), Unit.PX);
		style.setLeft(drawEmbed.getLeft(), Unit.PX);
		frame.getParent().getParent().setSize(
				Math.abs(drawEmbed.getRight() - drawEmbed.getLeft()) + "px",
				Math.abs(drawEmbed.getTop() - drawEmbed.getBottom()) + "px");
		frame.getElement().getStyle().setWidth(800, Unit.PX);
		frame.getElement().getStyle().setHeight(600, Unit.PX);
		frame.getApplication().checkScaleContainer();
	}

	@Override
	public void removeAll() {
		for (GeoGebraFrameBoth frame : widgets.values()) {
			frame.removeFromParent();
			frame.getElement().removeFromParent();
		}
		widgets.clear();
	}

	public int nextID() {
		return counter++;
	}

	/**
	 * Add base64 of embedded files into an archive
	 * 
	 * @param archiveContent
	 *            archive
	 */
	public void writeEmbeds(GgbFile archiveContent) {
		for (Entry<Integer, GeoGebraFrameBoth> e : widgets.entrySet()) {
			archiveContent.put("embed_" + e.getKey() + "_base64.txt",
					e.getValue().getApplication().getGgbApi().getBase64());
		}
	}

	/**
	 * Load all embeds for a slide
	 * 
	 * @param archive
	 *            slide
	 */
	public void loadBase64(GgbFile archive) {
		for (Entry<String, String> entry : archive.entrySet()) {
			if (entry.getKey().startsWith("embed")) {
				try {
					int id = Integer.parseInt(entry.getKey().split("_")[1]);
					counter = Math.max(counter, id + 1);
					base64.put(id,
						entry.getValue());
				} catch (RuntimeException e) {
					Log.warn("Problem loading embed " + entry.getKey());
				}
			}
		}	
	}
}
