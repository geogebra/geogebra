package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author csilla
 *
 */
public class MaterialCard extends FlowPanel {
	private AppW app;
	// image of material
	private FlowPanel imgPanel;
	// material information
	private FlowPanel infoPanel;
	private Label cardTitle;
	private Label cardAuthor;

	/**
	 * @param m
	 *            material
	 * @param app
	 *            see {@link AppW}
	 */
	public MaterialCard(final Material m, final AppW app) {
		this.app = app;
		initGui(m);
	}

	private void initGui(Material m) {
		this.setStyleName("materialCard");
		imgPanel = new FlowPanel();
		imgPanel.setStyleName("cardImgPanel");
		setBackgroundImgPanel(m);
		this.add(imgPanel);
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("cardInfoPanel");
		cardTitle = new Label(m.getTitle());
		cardTitle.setStyleName("cardTitle");
		cardAuthor = new Label(m.getAuthor());
		cardAuthor.setStyleName("cardAuthor");
		infoPanel.add(cardTitle);
		infoPanel.add(cardAuthor);
		this.add(infoPanel);
	}

	private void setBackgroundImgPanel(Material m) {
		final String thumb = m.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			imgPanel.getElement().getStyle().setBackgroundImage(
					"url(" + Browser.normalizeURL(thumb) + ")");
		} else {
			imgPanel.getElement().getStyle().setBackgroundImage("url("
					+ AppResources.INSTANCE.geogebra64().getSafeUri().asString()
					+ ")");
		}
	}

}
