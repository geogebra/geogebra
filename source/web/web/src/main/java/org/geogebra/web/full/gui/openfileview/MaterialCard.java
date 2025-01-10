package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.CardInfoPanel;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;

/**
 * Material card
 */
public class MaterialCard extends FlowPanel implements MaterialCardI {
	private AppW app;
	// image of material
	private MaterialImagePanel imgPanel;
	// material information
	private CardInfoPanel infoPanel;
	private ContextMenuButtonMaterialCard moreBtn;
	private FlowPanel infoPanelContent;
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
		imgPanel = new MaterialImagePanel(getMaterial());
		this.add(imgPanel);
		// panel containing the info regarding the material

		moreBtn = new ContextMenuButtonMaterialCard(app, getMaterial(), this);
		// panel for visibility state
		infoPanelContent = new FlowPanel();
		updateVisibility(getMaterial());
		infoPanel = new CardInfoPanel(getMaterial().getTitle(), infoPanelContent);

		infoPanel.add(moreBtn);
		this.add(infoPanel);
	}

	private boolean isOwnMaterial() {
		return app.getLoginOperation().getResourcesAPI().owns(getMaterial());
	}

	private String getCardAuthor() {
		return getMaterial().getCreator() != null
				? getMaterial().getCreator().getDisplayName()
				: "";
	}

	/**
	 * @return represented material
	 */
	Material getMaterial() {
		return controller.getMaterial();
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

	/**
	 * Change name on card and rename via API
	 * @param text new name
	 */
	public void rename(String text) {
		String oldTitle = infoPanel.getCardId();
		infoPanel.setCardId(text);
		controller.rename(text, this, oldTitle);
	}

	/**
	 * Call API to copy the material.
	 */
	public void copy() {
		controller.copy();
	}

	@Override
	public void onDelete() {
		controller.showDeleteConfirmDialog(this);
	}

	@Override
	public MaterialCardController getController() {
		return controller;
	}

	@Override
	public String getCardTitle() {
		return getMaterial().getTitle();
	}

	/**
	 * @param material
	 *            material
	 */
	public void updateVisibility(Material material) {
		MaterialDesignResources res = MaterialDesignResources.INSTANCE;
		String visibility = material.getVisibility();
		if (material.isSharedWithGroup()) {
			visibility = "S";
		}
		NoDragImage visibilityImg;
		String visibilityTxt;
		if (material.isMultiuser()) {
			visibilityImg = getMultiuserIcon();
			if (isOwnMaterial()) {
				visibilityTxt = app.getLocalization().getMenu("Collaborative");
			} else {
				visibilityTxt = getCardAuthor();
			}
		} else if (!isOwnMaterial()) {
			visibilityImg = null;
			visibilityTxt = getCardAuthor();
		} else {
			switch (visibility) {
			case "P":
				visibilityImg = new NoDragImage(res.mow_card_private(), 24);
				visibilityTxt = app.getLocalization().getMenu("Private");
				break;
			case "S":
				if (app.isMebis()) {
					visibilityImg = new NoDragImage(res.mow_card_shared(), 24);
				} else {
					visibilityImg = new NoDragImage(res.resource_card_shared(), 24);
				}
				visibilityTxt = app.getLocalization().getMenu("Shared");
				break;
			case "O":
			default:
				visibilityImg = new NoDragImage(res.mow_card_public(), 24);
				visibilityTxt = app.getLocalization().getMenu("Public");
				break;
			}
		}

		infoPanelContent.clear();
		Label visibilityLbl = BaseWidgetFactory.INSTANCE.newSecondaryText(visibilityTxt);

		if (visibilityImg != null) {
			infoPanelContent.setStyleName("visibilityPanel");
			infoPanelContent
					.add(LayoutUtilW.panelRow(visibilityImg, visibilityLbl));
		} else {
			infoPanelContent.setStyleName("cardAuthor");
			infoPanelContent.add(visibilityLbl);
		}
	}

	private NoDragImage getMultiuserIcon() {
		return new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_card_multiuser(), 24);
	}

	public void setLabels() {
		updateVisibility(getMaterial());
	}

	public void setThumbnail(Material mat) {
		imgPanel.setBackground(mat);
	}
}