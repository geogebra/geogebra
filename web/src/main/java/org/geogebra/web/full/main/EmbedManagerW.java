package org.geogebra.web.full.main;

import java.util.HashMap;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.full.gui.layout.DockPanelW;
import org.geogebra.web.full.gui.layout.panels.EuclidianDockPanelW;
import org.geogebra.web.html5.gui.util.ClickEndHandler;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.TestArticleElement;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;

public class EmbedManagerW implements EmbedManager {

	private AppWFull app;
	private HashMap<DrawEmbed, GeoGebraFrameBoth> widgets = new HashMap<>();

	public EmbedManagerW(AppWFull app) {
		this.app = app;
	}

	@Override
	public void add(DrawEmbed drawEmbed) {
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(
				(AppletFactory) GWT.create(AppletFactory.class),
				app.getLAF(), app.getDevice(), false);
		fr.ae = new TestArticleElement("", "graphing");
		fr.ae.attr("showToolbar", "true");
		fr.runAsyncAfterSplash();
		DockPanelW panel = ((DockManagerW) app.getGuiManager().getLayout()
				.getDockManager()).getPanel(App.VIEW_EUCLIDIAN);
		((EuclidianDockPanelW) panel).getAbsolutePanel().add(fr);
		Style style = fr.getElement().getStyle();
		style.setPosition(Position.ABSOLUTE);
		style.setTop(100, Unit.PX);
		style.setLeft(100, Unit.PX);
		style.setZIndex(100);
		ClickStartHandler.initDefaults(fr, false, true);
		ClickEndHandler.init(fr, new ClickEndHandler(false, true) {

			@Override
			public void onClickEnd(int x, int y, PointerEventType type) {
				// just prevent default
			}
		});
		fr.addDomHandler(new MouseWheelHandler() {

			public void onMouseWheel(MouseWheelEvent event) {
				event.stopPropagation();

			}
		}, MouseWheelEvent.getType());
		widgets.put(drawEmbed, fr);
	}

	@Override
	public void update(DrawEmbed drawEmbed) {
		Style style = widgets.get(drawEmbed).getElement().getStyle();
		style.setTop(drawEmbed.getView().toScreenCoordYd(5), Unit.PX);
		style.setLeft(drawEmbed.getView().toScreenCoordXd(-5), Unit.PX);
	}

}
