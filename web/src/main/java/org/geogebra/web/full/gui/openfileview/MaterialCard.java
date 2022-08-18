package org.geogebra.web.full.gui.openfileview;

import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.CardInfoPanel;
import org.geogebra.web.full.gui.browser.MaterialCardController;
import org.geogebra.web.html5.gui.util.LayoutUtilW;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Material card
 */
public class MaterialCard extends FlowPanel implements MaterialCardI {
	private AppW app;
	// image of material
	private FlowPanel imgPanel;
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
		return app.getLoginOperation().getGeoGebraTubeAPI().owns(getMaterial());
	}

	private String getCardAuthor() {
		return "".equals(getMaterial().getAuthor())
				&& getMaterial().getCreator() != null
				? getMaterial().getCreator().getDisplayname()
				: getMaterial().getAuthor();
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

	@Override
	public void rename(String text) {
		String oldTitle = infoPanel.getCardId();
		infoPanel.setCardId(text);
		controller.rename(text, this, oldTitle);
	}

	@Override
	public void copy() {
		controller.copy();
	}

	@Override
	public void onDelete() {
		DialogData data = new DialogData(null, "Cancel", "Delete");
		ComponentDialog removeDialog = new RemoveDialog(app, data, this);
		removeDialog.show();
		removeDialog.setOnPositiveAction(this::onConfirmDelete);
	}

	@Override
	public String getCardTitle() {
		return getMaterial().getTitle();
	}

	@Override
	public String getMaterialID() {
		return getMaterial().getSharingKeyOrId();
	}

	@Override
	public void updateVisibility(Material material) {
		String visibility = material.getVisibility();
		if (material.isSharedWithGroup()) {
			visibility = "S";
		}
		NoDragImage visibiltyImg;
		Label visibilityTxt;
		if (material.isMultiuser()) {
			visibiltyImg = getMultiuserIcon();
			if (isOwnMaterial()) {
				visibilityTxt = new Label(app.getLocalization().getMenu("Collaborative"));
			} else {
				visibilityTxt = new Label(getCardAuthor());
			}
		} else {
			switch (visibility) {
			case "P":
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
			default:
				if (isOwnMaterial()) {
					visibiltyImg = new NoDragImage(
							MaterialDesignResources.INSTANCE.mow_card_public(), 24);
					visibilityTxt = new Label(app.getLocalization().getMenu("Public"));
				} else {
					visibiltyImg = null;
					visibilityTxt = new Label(getCardAuthor());
				}
				break;
			}
		}
		infoPanelContent.clear();
		if (visibiltyImg != null) {
			infoPanelContent.setStyleName("visibilityPanel");
			infoPanelContent
					.add(LayoutUtilW.panelRow(visibiltyImg, visibilityTxt));
		} else {
			infoPanelContent.setStyleName("cardAuthor");
			infoPanelContent.add(visibilityTxt);
		}
	}

	private NoDragImage getMultiuserIcon() {
		return new NoDragImage(
				MaterialDesignResources.INSTANCE.mow_card_multiuser(), 24);
	}

	public void setLabels() {
		updateVisibility(getMaterial());
	}
}