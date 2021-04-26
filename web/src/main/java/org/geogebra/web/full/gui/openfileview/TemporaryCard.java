package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.gui.CardInfoPanel;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Material card
 */
public class TemporaryCard extends FlowPanel implements MaterialCardI {
	private final AppW app;
	// image of material
	private FlowPanel imgPanel;
	private final MaterialCardController controller;

	/**
	 * @param m
	 *            material
	 * @param app
	 *            see {@link AppW}
	 */
	public TemporaryCard(final Material m, final AppW app) {
		this.app = app;
		controller = new MaterialCardController(app);
		controller.setMaterial(m);
		initGui();
		this.addDomHandler(event -> openMaterial(), ClickEvent.getType());
	}

	/**
	 * Open this material.
	 */
	protected void openMaterial() {
		Material material = controller.getMaterial();
		app.getGgbApi().setBase64(material.getBase64());
		controller.onOpenFile();
	}

	private void initGui() {
		this.setStyleName("materialCard");
		// panel containing the preview image of material
		imgPanel = new FlowPanel();
		imgPanel.setStyleName("cardImgPanel");
		setBackgroundImgPanel(getMaterial());
		this.add(imgPanel);
		// panel containing the info regarding the material

		// material information
		CardInfoPanel infoPanel = new CardInfoPanel(getMaterial().getTitle(), "");
		this.add(infoPanel);
	}

	/**
	 * @return represented material
	 */
	Material getMaterial() {
		return controller.getMaterial();
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

	@Override
	public void remove() {
		// not used
	}

	/**
	 * Actually delete the file.
	 */
	protected void onConfirmDelete() {
		// not used
	}

	@Override
	public void rename(String text) {
		// not used
	}

	@Override
	public void copy() {
		// not used
	}

	@Override
	public void onDelete() {
		// not used
	}

	@Override
	public String getCardTitle() {
		return getMaterial().getTitle();
	}

	@Override
	public void setShare(String groupID, boolean shared,
			AsyncOperation<Boolean> callback) {
		// not used
	}

	@Override
	public String getMaterialID() {
		return ""; // not used
	}

	@Override
	public void updateVisibility(String visibility) {
		// not used
	}

	public void setLabels() {
		// not used
	}
}