package org.geogebra.web.full.main;

import org.geogebra.common.euclidian.EmbedManager;
import org.geogebra.common.euclidian.draw.DrawEmbed;
import org.geogebra.common.main.App;
import org.geogebra.web.full.gui.applet.AppletFactory;
import org.geogebra.web.full.gui.applet.GeoGebraFrameBoth;
import org.geogebra.web.full.gui.layout.DockManagerW;
import org.geogebra.web.html5.main.TestArticleElement;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;

public class EmbedManagerW implements EmbedManager {

	private AppWFull app;

	public EmbedManagerW(AppWFull app) {
		this.app = app;
	}

	public void add(DrawEmbed drawEmbed) {
		GeoGebraFrameBoth fr = new GeoGebraFrameBoth(
				(AppletFactory) GWT.create(AppletFactory.class),
				app.getLAF(), app.getDevice(), false);
		fr.ae = new TestArticleElement("", "graphing");
		fr.ae.setAttribute("toolbar", "true");
		fr.runAsyncAfterSplash();
		((DockManagerW) app.getGuiManager().getLayout().getDockManager())
				.getPanel(App.VIEW_EUCLIDIAN).getElement()
				.appendChild(fr.getElement());
		fr.getElement().getStyle().setPosition(Position.ABSOLUTE);
		fr.getElement().getStyle().setTop(100, Unit.PX);
		fr.getElement().getStyle().setLeft(100, Unit.PX);
		fr.getElement().getStyle().setZIndex(100);
	}

	public void update(DrawEmbed drawEmbed) {
		// TODO update position
	}

}
