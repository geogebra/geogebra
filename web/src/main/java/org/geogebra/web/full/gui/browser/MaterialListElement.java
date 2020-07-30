package org.geogebra.web.full.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.GuiManagerW;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.openfileview.MaterialCardI;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.shared.ggtapi.models.MaterialCallback;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * GUI Element showing a Material as search Result
 *
 * @author Matthias Meisinger
 *
 */
@SuppressWarnings("javadoc")
public class MaterialListElement extends FlowPanel
		implements MaterialListElementI, MaterialCardI {

	public enum State {
		Default, Selected, Disabled;
	}

	private FlowPanel materialElementContent;
	private FlowPanel background;
	protected FlowPanel infoPanel;
	protected boolean localMaterial;
	protected boolean ownMaterial;
	protected Label title;
	protected Label sharedBy;
	protected TextBox renameTitleBox;
	protected final AppW app;
	protected final Localization loc;
	protected final GuiManagerW guiManager;
	protected State state = State.Default;
	AsyncOperation<Boolean> editMaterial;

	protected StandardButton viewButton;
	protected StandardButton editButton;
	protected StandardButton renameButton;

	private FlowPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private StandardButton deleteButton;
	private StandardButton favoriteButton;

	private ShowDetailsListener showListener;

	private MaterialCardController controller;

	/**
	 *
	 * @param m
	 *            {@link Material}
	 * @param app
	 *            {@link AppW}
	 * @param isLocal
	 *            boolean
	 */
	public MaterialListElement(final Material m, final AppW app,
			boolean isLocal) {
		this.app = app;
		controller = new MaterialCardController(app);
		this.loc = app.getLocalization();
		this.guiManager = (GuiManagerW) app.getGuiManager();
		this.setMaterialSimple(m);
		this.localMaterial = isLocal;
		this.ownMaterial = app.getLoginOperation().getGeoGebraTubeAPI().owns(m);
		this.setStyleName("materialListElement");
		this.addStyleName("default");
		if (!isLocal) {
			// this.material.setSyncStamp(-1);
		}
		this.editMaterial = activeMaterial -> onEdit();
		initMaterialInfos();

		this.materialElementContent = new FlowPanel();
		this.materialElementContent.addStyleName("materialElementContent");
		this.add(this.materialElementContent);

		addPreviewPicture();
		addInfoPanel();
		showDetails(false);

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				if (state == State.Disabled) {
					return;
				} else if (state == State.Default) {
					if (app.getConfig().isSimpleMaterialPicker()) {
						onEdit();
					} else {
						markSelected();
					}
					event.stopPropagation();
				} else {
					event.stopPropagation();
				}
			}
		}, ClickEvent.getType());

		setLabels();
	}

	protected void initMaterialInfos() {
		if (!isLocal()) {
			this.title = new Label(this.getMaterial().getTitle());
		} else {
			String key = this.getMaterial().getTitle();
			this.title = new Label(extractTitle(key));
		}
		this.sharedBy = new Label(this.getMaterial().getAuthor());
		this.sharedBy.setStyleName("sharedPanel");
		this.title.addStyleName("fileTitle");
	}

	private void addInfoPanel() {
		this.infoPanel = new FlowPanel();
		this.infoPanel.setStyleName("infoPanel");

		addTextInfo();
		addSeperator();
		addOptions();

		this.materialElementContent.add(this.infoPanel);
	}

	protected void addTextInfo() {
		this.infoPanel.add(this.title);
		if (ownMaterial) {
			initRenameTextBox();
		}
		if (!isLocal()) {
			this.infoPanel.add(this.sharedBy);
		}
	}

	protected void addOptions() {
		if (ownMaterial && !isLocal()) {
			addEditButton();
			addViewButton();
			addRenameButton();
			addDeleteButton();
		} else if (isLocal()) {
			addEditButton();
			addRenameButton();
			addDeleteButton();
		} else {
			addEditButton();
			addViewButton();
		}
	}

	private void addRenameButton() {
		this.renameButton = new StandardButton(
				BrowseResources.INSTANCE.document_rename(), "", 20, app);
		this.infoPanel.add(this.renameButton);
		this.renameButton.addFastClickHandler(source -> onRename());
	}

	void onRename() {
		this.renameTitleBox.setVisible(true);
		this.renameTitleBox.setText(this.title.getText());
		this.title.setVisible(false);
		this.renameTitleBox.setSelectionRange(0,
				this.renameTitleBox.getText().length());
		this.renameTitleBox.setFocus(true);
	}

	@Override
	public void rename(String text) {
		if (text.length() < 1
				|| text.equals(this.title.getText())) { // no changes
			this.title.setVisible(true);
			this.renameTitleBox.setVisible(false);
			return;
		}
		final String oldTitle = this.title.getText();
		this.title.setText(text);
		this.renameTitleBox.setVisible(false);
		this.title.setVisible(true);

		controller.rename(text, this, oldTitle);
	}

	private void initRenameTextBox() {
		this.renameTitleBox = new GTextBox();
		this.renameTitleBox.addStyleName("renameBox");
		this.infoPanel.add(this.renameTitleBox);
		this.renameTitleBox.setVisible(false);
		this.renameTitleBox.addBlurHandler(event -> rename(renameTitleBox.getText()));

		this.renameTitleBox.addKeyDownHandler(event -> {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				renameTitleBox.setFocus(false);
			}
		});
	}

	private void addSeperator() {
		FlowPanel separator = new FlowPanel();
		separator.setStyleName("Separator");
		this.infoPanel.add(separator);
	}

	private void addPreviewPicture() {
		SimplePanel previewPicturePanel = new SimplePanel();
		previewPicturePanel.addStyleName("fileImage");
		previewPicturePanel.addDomHandler(event -> {
			if (state == State.Selected) {
				openDefault();
			} else if (state == State.Disabled) {
				return;
			} else {
				if (app.getConfig().isSimpleMaterialPicker()) {
					onEdit();
				} else {
					markSelected();
				}
				event.stopPropagation();
			}
		}, ClickEvent.getType());

		background = new FlowPanel();
		background.setStyleName("background");

		setPictureAsBackground();

		previewPicturePanel.add(background);
		this.materialElementContent.add(previewPicturePanel);

		addSyncDecoration();
	}

	private void addSyncDecoration() {
		if (this.getMaterial().getType() == Material.MaterialType.book) {
			final Label deco = new Label();
			deco.setStyleName("bookDecoration");
			background.add(deco);
		}
		if ((this.app.getFileManager().shouldKeep(this.getMaterial().getId())
				|| this.app.has(Feature.LOCALSTORAGE_FILES))
				&& this.getMaterial().getType() != MaterialType.book
				&& this.getMaterial().getSyncStamp() > 0 && this.getMaterial()
						.getModified() <= this.getMaterial().getSyncStamp()) {
			final Label deco = new Label();
			deco.setStyleName("syncDecoration");
			background.add(deco);
		}
		if (this.app.getLoginOperation().isLoggedIn()) {
			addFavoriteButton();
		}

	}

	private void setPictureAsBackground() {
		final String thumb = this.getMaterial().getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			background.getElement().getStyle().setBackgroundImage(
					"url(" + Browser.normalizeURL(thumb) + ")");
		} else {
			background.getElement().getStyle().setBackgroundImage("url("
					+ AppResources.INSTANCE.geogebra64().getSafeUri().asString()
					+ ")");
		}
	}

	private void addDeleteButton() {
		this.deleteButton = new StandardButton(
				BrowseResources.INSTANCE.document_delete(), app);
		this.infoPanel.add(this.deleteButton);
		this.deleteButton.addFastClickHandler(source -> onDelete());
		initConfirmDeletePanel();
	}

	private void addFavoriteButton() {
		this.favoriteButton = new StandardButton(
				this.getMaterial().isFavorite() ? BrowseResources.INSTANCE.favorite()
						: BrowseResources.INSTANCE.not_favorite(),
				app);
		this.favoriteButton.addStyleName("ggbFavorite");
		this.background.add(this.favoriteButton);
		this.favoriteButton.addFastClickHandler(source -> onFavorite());
	}

	void onFavorite() {
		app.getLoginOperation().getGeoGebraTubeAPI()
				.favorite(this.getMaterial().getId(), !this.getMaterial().isFavorite());
		this.getMaterial().setFavorite(!this.getMaterial().isFavorite());
		updateFavoriteText();
		if (this.getMaterial().isFavorite()) {
			if (app.getFileManager().shouldKeep(this.getMaterial().getId())) {
				this.app.getFileManager().getFromTube(this.getMaterial().getId(),
						this.getMaterial().isFromAnotherDevice());
			}
		} else if (this.getMaterial().isFromAnotherDevice()) {
			this.app.getFileManager().delete(this.getMaterial(), true,
					this.controller.getDeleteCallback());
		}
	}

	@Override
	public void onDelete() {
		this.deleteButton.addStyleName("deleteActive");
		if (this.editButton != null) {
			this.editButton.setVisible(false);
		}
		if (this.viewButton != null) {
			this.viewButton.setVisible(false);
		}
		if (this.renameButton != null) {
			this.renameButton.setVisible(false);
		}
		this.confirmDeletePanel.setVisible(true);
		this.deleteButton
				.setIcon(BrowseResources.INSTANCE.document_delete_active());
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.loc.getMenu("Delete"), app);
		this.confirm.addStyleName("gwt-Button");
		this.confirm.addStyleName("deleteButton");
		this.confirm.addFastClickHandler(source -> onConfirmDelete());
		this.cancel = new StandardButton(this.loc.getMenu("Cancel"), app);
		this.cancel.addStyleName("cancelButton");
		this.cancel.addStyleName("gwt-Button");
		this.cancel.addStyleName("minor");
		this.cancel.addFastClickHandler(source -> onCancel());

		this.confirmDeletePanel = new FlowPanel();
		this.confirmDeletePanel.add(this.cancel);
		this.confirmDeletePanel.add(this.confirm);
		this.confirmDeletePanel.setStyleName("confirmDelete");
		this.confirmDeletePanel.setVisible(false);
		this.infoPanel.add(this.confirmDeletePanel);
	}

	@Override
	public void remove() {
		app.getGuiManager().getBrowseView()
				.removeMaterial(this.getMaterial());
	}

	void onCancel() {
		showDetails(true);
	}

	/**
	 *
	 */
	protected void openDefault() {
		if (ownMaterial) {
			guiManager.getBrowseView().closeAndSave(editMaterial);
		} else {
			onView();
		}
	}

	protected void addEditButton() {
		this.editButton = new StandardButton(
				BrowseResources.INSTANCE.document_edit(), "", 20, app);
		this.infoPanel.add(this.editButton);
		this.editButton.addFastClickHandler(
				source -> guiManager.getBrowseView().closeAndSave(editMaterial));
	}

	/**
	 *
	 */
	protected void onEdit() {
		if (!localMaterial) {
			Log.debug(getMaterial().getType().toString());
			if (getMaterial().getType() == MaterialType.book) {
				((GeoGebraTubeAPIW) app.getLoginOperation()
						.getGeoGebraTubeAPI()).getBookItems(getMaterial().getId(),
								new MaterialCallback() {

									@Override
									public void onLoaded(
											final List<Material> response,
											final ArrayList<Chapter> chapters) {
										guiManager.getBrowseView()
												.clearMaterials();
										guiManager.getBrowseView()
												.onSearchResults(response,
														chapters);
									}
								});
				return;
			}
			final long synced = getMaterial().getSyncStamp();
			if (getMaterial().getType() == MaterialType.ws) {
				((GeoGebraTubeAPIW) app.getLoginOperation()
						.getGeoGebraTubeAPI()).getWorksheetItems(
								getMaterial().getId(), new MaterialCallback() {

									@Override
									public void onLoaded(
											final List<Material> response,
											ArrayList<Chapter> meta) {
										if (response.size() != 1 || StringUtil
												.empty(getMaterial().getBase64())) {
											Browser.openWindow(
													getMaterial().getEditUrl());
										} else {
											setMaterialSimple(response.get(0));
											getMaterial().setSyncStamp(synced);
											app.getGgbApi().setBase64(
													getMaterial().getBase64());
											app.setActiveMaterial(getMaterial());
										}
									}
								});
				return;
			}
			if (!app.isUnbundled()) {
				ToolTipManagerW.sharedInstance()
					.showBottomMessage(loc.getMenu("Loading"), false, app);
			}
			controller.loadOnlineFile();
		} else {
			if (!app.isUnbundled()) {
				ToolTipManagerW.sharedInstance()
					.showBottomMessage(loc.getMenu("Loading"), false, app);
			}
			if (!this.app.getFileManager().hasBase64(this.getMaterial())) {
				controller.loadOnlineFile();
			} else {
				this.app.getFileManager().openMaterial(this.getMaterial());
				this.app.setActiveMaterial(getMaterial());
			}
		}
	}

	protected void addViewButton() {
		this.viewButton = new StandardButton(
				BrowseResources.INSTANCE.document_view(), "", 20, app);
		this.viewButton.addStyleName("viewButton");
		this.infoPanel.add(this.viewButton);
		this.viewButton.addFastClickHandler(source -> onView());
	}

	/**
	 * marks the material as selected and disables the other materials
	 */
	protected void markSelected() {
		this.guiManager.getBrowseView().disableMaterials();
		this.guiManager.getBrowseView().rememberSelected(this);
		this.state = State.Selected;
		this.removeStyleName("unselected");
		this.removeStyleName("default");
		this.addStyleName("selected");
		showDetails(true);
	}

	/**
	 * sets the default style
	 */
	public void setDefaultStyle() {
		this.state = State.Default;
		this.removeStyleName("selected");
		this.removeStyleName("unselected");
		this.addStyleName("default");
		showDetails(false);
	}

	/**
	 * Disables the material.
	 */
	public void disableMaterial() {
		this.state = State.Disabled;
		this.addStyleName("unselected");
		this.removeStyleName("selected");
		this.removeStyleName("default");
	}

	/**
	 *
	 */
	public void setLabels() {
		if (this.deleteButton != null) {
			this.deleteButton.setText(loc.getMenu("Delete"));
		}
		if (this.cancel != null) {
			this.cancel.setText(this.loc.getMenu("Cancel"));
		}
		if (this.confirm != null) {
			this.confirm.setText(this.loc.getMenu("Delete"));
		}
		if (this.editButton != null) {
			this.editButton.setText(loc.getMenu("Edit"));
		}
		if (this.viewButton != null) {
			this.viewButton
					.setText(loc.getMenu(getInsertWorksheetTitle(getMaterial())));
		}
		if (this.renameButton != null) {
			this.renameButton.setText(loc.getMenu("Rename"));
		}
	}

	private void updateFavoriteText() {
		this.favoriteButton.setIcon(
				getMaterial().isFavorite() ? BrowseResources.INSTANCE.favorite()
						: BrowseResources.INSTANCE.not_favorite());
	}

	/**
	 *
	 * @return the {@link Material}
	 */
	public Material getMaterial() {
		return controller.getMaterial();
	}

	protected void showDetails(final boolean show) {
		if (ownMaterial && !isLocal()) {
			this.sharedBy.setVisible(true);
			if (viewButton != null) {
				this.viewButton.setVisible(show);
			}
			this.deleteButton.setVisible(show);
			this.deleteButton.removeStyleName("deleteActive");
			this.deleteButton
					.setIcon(BrowseResources.INSTANCE.document_delete());
			this.confirmDeletePanel.setVisible(false);
			this.renameButton.setVisible(show);
			this.editButton.setVisible(show);
		} else if (isLocal()) {
			this.deleteButton.setVisible(show);
			this.deleteButton.removeStyleName("deleteActive");
			this.deleteButton
					.setIcon(BrowseResources.INSTANCE.document_delete());
			this.confirmDeletePanel.setVisible(false);
			this.renameButton.setVisible(show);
			this.editButton.setVisible(show);
		} else {
			this.sharedBy.setVisible(true);
			if (viewButton != null) {
				this.viewButton.setVisible(show);
			}
			this.editButton.setVisible(show);
		}

		if (show) {
			this.infoPanel.addStyleName("detailed");
			showListener.onShowDetails(materialElementContent);
		} else {
			this.infoPanel.removeStyleName("detailed");
		}
	}

	/***
	 * Depends on LAF
	 *
	 * @param m
	 *            material
	 * @return action name (translation key)
	 **/
	public String getInsertWorksheetTitle(final Material m) {
		return "ViewMaterial";
	}

	/**
	 * Opens GeoGebraTube material in a new window (overwritten for tablet app,
	 * smart widget)
	 */
	protected void onView() {
		this.guiManager.getBrowseView().setMaterialsDefaultStyle();
		Browser.openWindow(getMaterial().getURL());
	}

	/**
	 * Sets the material and updates UI.
	 *
	 * @param mat
	 *            material
	 */
	public void setMaterial(Material mat) {
		this.setMaterialSimple(mat);

		if (isLocal()) {
			String key = mat.getTitle();
			this.title.setText(extractTitle(key));
		} else {
			this.title.setText(this.getMaterial().getTitle());
		}
		if (!app.getLoginOperation().getGeoGebraTubeAPI().owns(mat)) {
			this.sharedBy.setText(this.getMaterial().getAuthor());
		}
		this.background.clear();
		setPictureAsBackground();
		addSyncDecoration();
	}

	public void setShowDetailsListener(ShowDetailsListener listener) {
		this.showListener = listener;
	}

	private static String extractTitle(String key) {
		return key.substring(key.indexOf("_", key.indexOf("_") + 1) + 1);
	}

	/**
	 * @return true if this material is saved local
	 */
	public boolean isLocal() {
		return this.getMaterial().getId() <= 0;
	}

	/**
	 * @return true if this material belongs to the signed in user
	 */
	public boolean isOwn() {
		return this.ownMaterial;
	}

	protected void setMaterialSimple(Material material) {
		controller.setMaterial(material);
	}

	/**
	 * Actually delete the file.
	 */
	protected void onConfirmDelete() {
		controller.onConfirmDelete(this);
	}

	@Override
	public void setMaterialTitle(String oldTitle) {
		title.setText(oldTitle);
	}

	@Override
	public void copy() {
		controller.copy();
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
		// only needed for new cards
	}
}