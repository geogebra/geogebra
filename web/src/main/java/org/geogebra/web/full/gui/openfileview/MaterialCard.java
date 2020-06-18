package org.geogebra.web.full.gui.openfileview;

import java.util.List;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.DialogBoxW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author csilla
 *
 */
public class MaterialCard extends FlowPanel implements MaterialCardI {
	private AppW app;
	// image of material
	private FlowPanel imgPanel;
	// material information
	private FlowPanel infoPanel;
	private Label cardTitle;
	private Label cardAuthor;
	private ContextMenuButtonMaterialCard moreBtn;
	private FlowPanel visibilityPanel;
	private MaterialCardController controller;

	/**
	 * @param m
	 *            material
	 * @param app
	 *            see {@link AppW}
	 */
	public MaterialCard(final Material m, final AppW app) {
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
		app.getGuiManager().getBrowseView().closeAndSave(obj -> controller.loadOnlineFile());
	}

	private void initGui() {
		this.setStyleName("materialCard");
		// panel containing the preview image of material
		imgPanel = new FlowPanel();
		imgPanel.setStyleName("cardImgPanel");
		setBackgroundImgPanel(getMaterial());
		this.add(imgPanel);
		// panel containing the info regarding the material
		infoPanel = new FlowPanel();
		infoPanel.setStyleName("cardInfoPanel");
		cardTitle = new Label(getMaterial().getTitle());
		cardTitle.setStyleName("cardTitle");
		cardAuthor = new Label("".equals(getMaterial().getAuthor())
				&& getMaterial().getCreator() != null
				? getMaterial().getCreator().getDisplayname()
				: getMaterial().getAuthor());
		cardAuthor.setStyleName("cardAuthor");
		moreBtn = new ContextMenuButtonMaterialCard(app, getMaterial(), this);
		// panel for visibility state
		visibilityPanel = new FlowPanel();
		visibilityPanel.setStyleName("visibilityPanel");
		updateVisibility(getMaterial().getVisibility());

		// build info panel
		infoPanel.add(cardTitle);
		infoPanel.add(
				app.getLoginOperation().getGeoGebraTubeAPI().owns(getMaterial())
						? visibilityPanel
				: cardAuthor);
		infoPanel.add(moreBtn);
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
		removeFromParent();
	}

	/**
	 * Actually delete the file.
	 */
	protected void onConfirmDelete() {
		controller.onConfirmDelete(this);
	}

	@Override
	public void rename(String text) {
		String oldTitle = cardTitle.getText();
		cardTitle.setText(text);
		controller.rename(text, this, oldTitle);
	}

	@Override
	public void setMaterialTitle(String title) {
		cardTitle.setText(title);
	}

	@Override
	public void copy() {
		controller.copy();
	}

	@Override
	public void onDelete() {
		DialogBoxW removeDialog = new RemoveDialog(app.getPanel(), app, this);
		removeDialog.center();
	}

	@Override
	public String getMaterialTitle() {
		return getMaterial().getTitle();
	}

	@Override
	public void setShare(String groupID, boolean shared,
			AsyncOperation<Boolean> callback) {
		controller.setShare(groupID, shared, callback);
	}

	@Override
	public String getMaterialID() {
		return getMaterial().getSharingKeyOrId();
	}

	@Override
	public void updateVisibility(String visibility) {
		NoDragImage visibiltyImg = new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_card_public(), 24);
		Label visibilityTxt = new Label(
				app.getLocalization().getMenu("Public"));
		switch (visibility) {
		case "P":
			app.getLoginOperation().getGeoGebraTubeAPI()
					.getGroups(getMaterial().getSharingKeyOrId(),
							this::showSharedIcon);
			visibiltyImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.mow_card_private(), 24);
			visibilityTxt = new Label(app.getLocalization().getMenu("Private"));
			break;
		case "S":
			visibiltyImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.mow_card_shared(), 24);
			visibilityTxt = new Label(app.getLocalization().getMenu("Shared"));
			break;
		case "O":
			visibiltyImg = new NoDragImage(
					MaterialDesignResources.INSTANCE.mow_card_public(), 24);
			visibilityTxt = new Label(app.getLocalization().getMenu("Public"));
			break;
		default:
			break;
		}
		visibilityPanel.clear();
		visibilityPanel
				.add(LayoutUtilW.panelRowIndent(visibiltyImg, visibilityTxt));
	}

	private void showSharedIcon(List<String> strings) {
		if (strings != null && !strings.isEmpty()) {
			updateVisibility("S");
		}
	}

	public void setLabels() {
		updateVisibility(getMaterial().getVisibility());
	}
}
