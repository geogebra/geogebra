package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
	private ContextMenuButtonMaterialCard moreBtn;
	private FlowPanel visibilityPanel;
	private VisibilityState visibility;
	private Material material;

	/**
	 * @author csilla
	 *
	 */
	public enum VisibilityState {
		/**
		 * private material
		 */
		Private,
		/**
		 * shared by link
		 */
		Link,
		/**
		 * group material
		 */
		Group;
	}

	/**
	 * @param m
	 *            material
	 * @param app
	 *            see {@link AppW}
	 */
	public MaterialCard(final Material m, final AppW app) {
		this.app = app;
		this.material = m;
		initGui();
		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				// TODO open material
			}
		}, ClickEvent.getType());
	}

	private void initGui() {
		this.setStyleName("materialCard");
		// panel containing the preview image of material
		imgPanel = new FlowPanel();
		imgPanel.setStyleName("cardImgPanel");
		setBackgroundImgPanel(material);
		this.add(imgPanel);
		// panel containing the info regarding the material
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("cardInfoPanel");
		cardTitle = new Label(material.getTitle());
		cardTitle.setStyleName("cardTitle");
		cardAuthor = new Label(material.getAuthor());
		cardAuthor.setStyleName("cardAuthor");
		moreBtn = new ContextMenuButtonMaterialCard(app, material);
		// panel for visibility state
		visibilityPanel = new FlowPanel();
		visibilityPanel.setStyleName("visibilityPanel");
		NoDragImage visibiltyImg = new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_card_public(), 24);
		Label visibilityTxt = new Label(
				app.getLocalization().getMenu("Public"));
		visibility = VisibilityState.Group;
		switch (visibility) {
		case Private:
			visibiltyImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.mow_card_private(), 24);
			visibilityTxt = new Label(app.getLocalization().getMenu("Private"));
			break;
		case Link:
			visibiltyImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.mow_card_link(), 24);
			visibilityTxt = new Label(app.getLocalization().getMenu("Link"));
			break;
		// TODO group has to be handled since is not coming from material
		case Group:
			visibiltyImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.mow_card_group(), 24);
			visibilityTxt = new Label(app.getLocalization().getMenu("Group"));
			break;
		default:
			break;
		}
		visibilityPanel
				.add(LayoutUtilW.panelRowIndent(visibiltyImg, visibilityTxt));
		// build info panel
		infoPanel.add(cardTitle);
		infoPanel.add(app.getLoginOperation().owns(material) ? visibilityPanel
				: cardAuthor);
		infoPanel.add(moreBtn);
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
