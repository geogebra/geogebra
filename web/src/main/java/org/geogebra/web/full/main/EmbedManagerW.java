package org.geogebra.web.full.main;

import java.util.HashMap;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
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
	private HashMap<DrawEmbed, GeoGebraFrameBoth> widgets = new HashMap<>();

	/**
	 * @param app
	 *            application
	 */
	public EmbedManagerW(AppWFull app) {
		this.app = app;
	}

	@Override
	public void add(DrawEmbed drawEmbed) {
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(
				(AppletFactory) GWT.create(AppletFactory.class),
				app.getLAF(), app.getDevice(), false);

		fr.ae = new TestArticleElement("", "graphing");
		fr.setComputedWidth(fr.ae.getDataParamWidth());
		fr.setComputedHeight(fr.ae.getDataParamHeight());
		fr.ae.attr("showToolbar", "true").attr("scaleContainerClass",
				"embedContainer").attr("allowUpscale", "true");
		fr.runAsyncAfterSplash();
		DockPanelW panel = ((DockManagerW) app.getGuiManager().getLayout()
				.getDockManager()).getPanel(App.VIEW_EUCLIDIAN);

		FlowPanel scaler = new FlowPanel();
		scaler.add(fr);
		((TestArticleElement) fr.ae).setParentElement(scaler.getElement());
		FlowPanel container = new FlowPanel();
		container.add(scaler);
		container.getElement().addClassName("embedContainer");
		((EuclidianDockPanelW) panel).getEuclidianPanel().add(container);
		Style style = container.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setZIndex(51); // above the oject canvas (50) and below MOW
								// toolbar (51)
		widgets.put(drawEmbed, fr);
	}

	@Override
	public void update(DrawEmbed drawEmbed) {
		GeoGebraFrameBoth frame = widgets.get(drawEmbed);
		Style style = frame.getParent().getParent().getElement().getStyle();
		style.setTop(drawEmbed.getTop(), Unit.PX);
		style.setLeft(drawEmbed.getView().toScreenCoordXd(-5), Unit.PX);
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

}
