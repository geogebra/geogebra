package org.geogebra.web.web.gui.browser;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.common.move.ggtapi.models.Chapter;
import org.geogebra.common.move.ggtapi.models.Material;
import org.geogebra.common.move.ggtapi.models.Material.MaterialType;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.textbox.GTextBox;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.view.browser.MaterialListElementI;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.gui.GuiManagerW;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.util.SaveDialogW;
import org.geogebra.web.web.gui.util.StandardButton;
import org.geogebra.web.web.move.ggtapi.models.GeoGebraTubeAPIW;
import org.geogebra.web.web.move.ggtapi.models.MaterialCallback;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends FlowPanel implements
        MaterialListElementI {
	
	public enum State {
		Default, Selected, Disabled;
	}
	
	private final int MAX_TITLE_HEIGHT = 40;
	private FlowPanel materialElementContent;
	private SimplePanel previewPicturePanel;
	private FlowPanel background;
	protected FlowPanel infoPanel;
	protected boolean isLocal;
	protected boolean isOwnMaterial;
	protected Label title;
	protected Label sharedBy;
	private TextBox renameTitleBox;
	protected Material material;
	protected final AppW app;
	protected final GuiManagerW guiManager;
	protected State state = State.Default;
	Runnable editMaterial;

	protected StandardButton viewButton;
	protected StandardButton editButton;
	protected StandardButton renameButton;

	private FlowPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private StandardButton deleteButton;
	private StandardButton favoriteButton;

	private ShowDetailsListener showListener;
	private Runnable deleteCallback = new Runnable() {

		@Override
		public void run() {
			App.debug("DELETE finished");
			MaterialListElement.this.app.getGuiManager().getBrowseView()
			        .setMaterialsDefaultStyle();
		}

	};

	/**
	 * 
	 * @param m {@link Material}
	 * @param app {@link AppW}
	 * @param isLocal boolean
	 */
	public MaterialListElement(final Material m, final AppW app, boolean isLocal) {
		this.app = app;
		this.guiManager = (GuiManagerW) app.getGuiManager();
		this.material = m;
		this.isLocal = isLocal;
		this.isOwnMaterial = app.getLoginOperation().owns(m);
		this.setStyleName("materialListElement");
		this.addStyleName("default");
		if (!isLocal) {
			// this.material.setSyncStamp(-1);
		}
		this.editMaterial = new Runnable() {
			
			@Override
			public void run() {
				onEdit();
			}
		};
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
					markSelected();
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
			this.title = new Label(this.material.getTitle());
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
		} else {
			String key = this.material.getTitle();
			this.title = new Label(extractTitle(key));
		}
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
		if (isOwnMaterial) {
			initRenameTextBox();
		}
		if (!isLocal()) {
			this.infoPanel.add(this.sharedBy);
		}
	}
	
	protected void addOptions() {
		if (isOwnMaterial && !isLocal()) {
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
		this.renameButton = new StandardButton(BrowseResources.INSTANCE.document_rename(), "", 20);
		this.infoPanel.add(this.renameButton);
		this.renameButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onRename();
			}
		});
	}

	void onRename() {
		this.renameTitleBox.setVisible(true);
		this.renameTitleBox.setText(this.title.getText());
		this.title.setVisible(false);
		this.renameTitleBox.setSelectionRange(0, this.renameTitleBox.getText().length());
		this.renameTitleBox.setFocus(true);
	}
	
	void doRename() {
		if (this.renameTitleBox.getText().length() < 1
		        ||
				this.renameTitleBox.getText().equals(this.title.getText())) { //no changes
			this.title.setVisible(true);
			this.renameTitleBox.setVisible(false);
			return;
		}
		final String oldTitle = this.title.getText();
		this.title.setText(this.renameTitleBox.getText());
		this.renameTitleBox.setVisible(false);
		this.title.setVisible(true);
		

		if (app.getNetworkOperation().isOnline() && this.material.getId() > 0) {

			this.material.setTitle(this.title.getText());
			((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadRenameMaterial(this.app, this.material, new MaterialCallback() {
				
				@Override
				                public void onLoaded(
				                        List<Material> parseResponse,
				                        ArrayList<Chapter> meta) {
					if (parseResponse.size() != 1) {
						app.showError(app.getLocalization().getError("RenameFailed"));
						title.setText(oldTitle);
					                } else {
						                App.debug("RENAME local");
						                material.setModified(parseResponse.get(
						                        0).getModified());
						                material.setSyncStamp(parseResponse
						                        .get(0)
						                        .getModified());
						                if (material.getLocalID() <= 0) {
							                return;
						                }
						                App.debug("RENAME CALLBACK" + oldTitle
						                        + "->" + title.getText());
						                material.setTitle(oldTitle);
						                app.getFileManager().rename(
						                        title.getText(),
						                                material, null);
					}
				}
			});
		} else {
			this.material.setModified(Math.max(
			        SaveDialogW.getCurrentTimestamp(app),
			        material.getSyncStamp() + 1));
			this.app.getFileManager().rename(this.title.getText(),
			        this.material, null);
		}
	}
	
	private void initRenameTextBox() {
		this.renameTitleBox = new GTextBox();
		this.renameTitleBox.addStyleName("renameBox");
		this.infoPanel.add(this.renameTitleBox);
		this.renameTitleBox.setVisible(false);
		this.renameTitleBox.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				doRename();
			}
		});
		
		this.renameTitleBox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					renameTitleBox.setFocus(false);
				}
			}
		});
	}
	
	private void addSeperator() {
		FlowPanel separator = new FlowPanel();
		separator.setStyleName("Separator");
		this.infoPanel.add(separator);
	}

	private void addPreviewPicture() {
		this.previewPicturePanel = new SimplePanel();
		this.previewPicturePanel.addStyleName("fileImage");
		this.previewPicturePanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if (state == State.Selected) {
					openDefault();
				} else if (state == State.Disabled) {
					return;
				} else {
					markSelected();
					event.stopPropagation();
				}
			}
		}, ClickEvent.getType());

		background = new FlowPanel();
		background.setStyleName("background");
		
		setPictureAsBackground();
		
		this.previewPicturePanel.add(background);
		this.materialElementContent.add(this.previewPicturePanel);

		addSyncDecoration();
	}

	private void addSyncDecoration() {
		if(this.material.getType() == Material.MaterialType.book){
			final Label deco = new Label();
			deco.setStyleName("bookDecoration");
			background.add(deco);
		}
		if ((this.app.getFileManager().shouldKeep(this.material.getId()) || this.app
				.has(Feature.LOCALSTORAGE_FILES))
		        && this.material.getType() != MaterialType.book
		        && this.material.getSyncStamp() > 0
		        && this.material.getModified() <= this.material.getSyncStamp()) {
			final Label deco = new Label();
			deco.setStyleName("syncDecoration");
			background.add(deco);
		}
		if (this.app.getLoginOperation().isLoggedIn()) {
			addFavoriteButton();
		}

	}

	private void setPictureAsBackground() {
	    final String thumb = this.material.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			background
			        .getElement()
			        .getStyle()
			        .setBackgroundImage(
			                "url(" + Browser.normalizeURL(thumb) + ")");
		} else {
			background
			.getElement()
			.getStyle()
			.setBackgroundImage(
					"url("
							+ AppResources.INSTANCE.geogebra64()
							.getSafeUri().asString() + ")");
		}
    }

	private void addDeleteButton() {
		this.deleteButton = new StandardButton(BrowseResources.INSTANCE.document_delete());
		this.infoPanel.add(this.deleteButton);
		this.deleteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onDelete();
			}
		});
		initConfirmDeletePanel();
	}
	
	private void addFavoriteButton() {
		this.favoriteButton = new StandardButton(
		        this.material.isFavorite() ? BrowseResources.INSTANCE
		                .favorite() : BrowseResources.INSTANCE
		                .not_favorite());
		this.favoriteButton.addStyleName("ggbFavorite");
		this.background.add(this.favoriteButton);
		this.favoriteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onFavorite();
			}
		});
	}

	void onFavorite(){
		app.getLoginOperation().getGeoGebraTubeAPI()
		        .favorite(this.material.getId(), !this.material.isFavorite());
		this.material.setFavorite(!this.material.isFavorite());
		updateFavoriteText();
		if (this.material.isFavorite()) {
			if (app.getFileManager().shouldKeep(this.material.getId())) {
				this.app.getFileManager().getFromTube(this.material.getId(),
				        this.material.isFromAnotherDevice());
			}
		} else if (this.material.isFromAnotherDevice()) {
			this.app.getFileManager().delete(this.material, true,
			        this.deleteCallback);
		}
	}
	void onDelete() {
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
		this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete_active());
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain("Delete"));
		this.confirm.addStyleName("gwt-Button");
		this.confirm.addStyleName("deleteButton");
		this.confirm.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onConfirmDelete();
			}
		});
		this.cancel = new StandardButton(this.app.getLocalization().getPlain("Cancel"));
		this.cancel.addStyleName("cancelButton");
		this.cancel.addStyleName("gwt-Button");
		this.cancel.addStyleName("minor");
		this.cancel.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onCancel();
			}
		});

		this.confirmDeletePanel = new FlowPanel();
		this.confirmDeletePanel.add(this.cancel);
		this.confirmDeletePanel.add(this.confirm);
		this.confirmDeletePanel.setStyleName("confirmDelete");
		this.confirmDeletePanel.setVisible(false);
		this.infoPanel.add(this.confirmDeletePanel);
	}

	void onConfirmDelete() {
		this.setVisible(false);
		setAllMaterialsDefault();
		final Material toDelete = this.material;


		if (app.getNetworkOperation().isOnline() && toDelete.getId() > 0) {
			((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
			        .deleteMaterial(this.app, toDelete, new MaterialCallback() {

				@Override
				        public void onLoaded(List<Material> parseResponse,
				                ArrayList<Chapter> meta) {
					        App.debug("DELETE local");
					remove();
					        MaterialListElement.this.app.getFileManager()
					                .delete(toDelete,
					                        true,
					                        MaterialListElement.this.deleteCallback);
				}

				@Override
				public void onError(Throwable exception) {
					        App.debug("DELETE backup");
					        MaterialListElement.this.app.getFileManager()
					                .delete(toDelete,
					                        false,
					                        MaterialListElement.this.deleteCallback);
					setVisible(true);
					app.showError(app.getMenu("DeleteFailed"));
				}
			});
		} else {
			App.debug("DELETE permanent");
			this.app.getFileManager().delete(toDelete, toDelete.getId() <= 0,
			        this.deleteCallback);
		}
		
	}

	protected void remove() {
		app.getGuiManager().getBrowseView()
		        .removeMaterial(MaterialListElement.this.material);
    }
	
	protected void setAllMaterialsDefault() {
		app.getGuiManager().getBrowseView().setMaterialsDefaultStyle();
	}
	
	void onCancel() {
		showDetails(true);
	}
	

	/**
	 * 
	 */
	protected void openDefault() {
		if (isOwnMaterial) {
			((DialogManagerW) app.getDialogManager()).getSaveDialog().showIfNeeded(editMaterial);
		} else {
			onView();
		}
	}

	protected void addEditButton() {
		this.editButton = new StandardButton(BrowseResources.INSTANCE.document_edit(), "", 20);
		this.infoPanel.add(this.editButton);
		this.editButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				app.checkSaved(editMaterial);
			}
		});
	}

	/**
	 * 
	 */
	protected void onEdit() {
		if (!isLocal) {
			if(material.getType() == MaterialType.book){
				((GeoGebraTubeAPIW) app.getLoginOperation()
				        .getGeoGebraTubeAPI()).getBookItems(material.getId(),
				        new MaterialCallback() {

					        @Override
					        public void onLoaded(final List<Material> response,
					                final ArrayList<Chapter> chapters) {
						        guiManager.getBrowseView().clearMaterials();
						        guiManager.getBrowseView().onSearchResults(
						                response, chapters);
					        }
				        });
				return;
			}
			final long synced = material.getSyncStamp();
			if (material.getType() == MaterialType.ws) {
				((GeoGebraTubeAPIW) app.getLoginOperation()
				        .getGeoGebraTubeAPI()).getWorksheetItems(
				        material.getId(),
				        new MaterialCallback() {

					        @Override
					        public void onLoaded(final List<Material> response,
					                ArrayList<Chapter> meta) {
						        if (response.size() != 1) {
						        guiManager.getBrowseView().clearMaterials();
						        guiManager.getBrowseView().onSearchResults(
							                response, null);
						        } else {
							        material = response.get(0);
							        material.setSyncStamp(synced);
							        app.getGgbApi().setBase64(
							                material.getBase64());
							        app.setActiveMaterial(material);
						        }
					        }
				        });
				return;
			}
			ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Loading"), false);

			loadMaterialAsGGB();
		} else {
			ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Loading"), false);
			if (this.material.getBase64() == null
					|| this.material.getBase64().length() == 0) {
				loadMaterialAsGGB();
			} else {
				this.app.getFileManager().openMaterial(this.material);
			}
		}
	}

	private void loadMaterialAsGGB() {
		final long synced = material.getSyncStamp();
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
				.getItem(material.getId() + "", new MaterialCallback() {

					@Override
					public void onLoaded(final List<Material> parseResponse,
							ArrayList<Chapter> meta) {
						if (parseResponse.size() == 1) {
							material = parseResponse.get(0);
							material.setSyncStamp(synced);
							if (material.getType() == MaterialType.csv) {
								app.openCSVbase64(material.getBase64());
							} else {
								app.getGgbApi().setBase64(material.getBase64());
							}
							app.setActiveMaterial(material);
						} else {
							app.showError(app.getLocalization().getError(
									"LoadFileFailed"));
						}
					}

					@Override
					public void onError(Throwable error) {
						app.showError(app.getLocalization().getError(
								"LoadFileFailed"));
					}
				});

	}

	protected void closeBrowseView() {
		this.guiManager.getBrowseView().close();
	}

	protected void addViewButton() {
		this.viewButton = new StandardButton(BrowseResources.INSTANCE.document_view(), "", 20);
		this.viewButton.addStyleName("viewButton");
		this.infoPanel.add(this.viewButton);
		this.viewButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onView();
			}
		});
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
		if(this.deleteButton != null){
				this.deleteButton.setText(app.getLocalization().getPlain("Delete"));
		}
		if(this.cancel != null){
				this.cancel.setText(this.app.getLocalization().getPlain("Cancel"));
		}
		if(this.confirm != null){
				this.confirm.setText(this.app.getLocalization().getPlain("Delete"));
		}
		if(this.editButton != null){
			this.editButton.setText(app.getLocalization().getMenu("Edit"));
		}
		if(this.viewButton != null){
			this.viewButton.setText(app.getMenu(getInsertWorksheetTitle(material)));
		}
		if (this.renameButton != null) {
			this.renameButton.setText(app.getMenu("Rename"));
		}

	}

	private void updateFavoriteText() {
		this.favoriteButton
		        .setIcon(material.isFavorite() ? BrowseResources.INSTANCE
		                .favorite() : BrowseResources.INSTANCE.not_favorite());
	}

	/**
	 * 
	 * @return the {@link Material}
	 */
	public Material getMaterial() {
		return this.material;
	}

	protected void showDetails(final boolean show) {
		/*
		 * if (this.favoriteButton != null) {
		 * this.favoriteButton.setVisible(show); }
		 */
		if (isOwnMaterial && !isLocal()) {
			this.sharedBy.setVisible(true);
			this.viewButton.setVisible(show);
			this.deleteButton.setVisible(show);
			this.deleteButton.removeStyleName("deleteActive");
			this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete());
			this.confirmDeletePanel.setVisible(false);
			this.renameButton.setVisible(show);
			this.editButton.setVisible(show);
		} else if (isLocal()) {
			this.deleteButton.setVisible(show);
			this.deleteButton.removeStyleName("deleteActive");
			this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete());
			this.confirmDeletePanel.setVisible(false);
			this.renameButton.setVisible(show);
			this.editButton.setVisible(show);
		} else {
			this.sharedBy.setVisible(true);
			this.viewButton.setVisible(show);
			this.editButton.setVisible(show);
		}
		
		
		if (show) {
			this.infoPanel.addStyleName("detailed");
			showListener.onShowDetails(materialElementContent);
		} else {
			this.infoPanel.removeStyleName("detailed");
		}
	}
	
	/*** LAF dependent methods **/

	public String getInsertWorksheetTitle(final Material m) {
		return "View";
	}

	/**
	 * Opens GeoGebraTube material in a new window (overwritten for tablet app, smart widget)
	 */
	protected void onView() {
		this.guiManager.getBrowseView().setMaterialsDefaultStyle();
		openTubeWindow(material.getURL());
	}

	/**
	 * Opens GeoGebraTube material in a new window
	 * @param id material id
	 */
	private native void openTubeWindow(String url)/*-{
		$wnd.open(url);
	}-*/;

	public void setMaterial(Material mat) {
		this.material = mat;

		if (isLocal()) {
			String key = mat.getTitle();
			this.title.setText(extractTitle(key));
		} else {
			this.title.setText(this.material.getTitle());
		}
		if (!app.getLoginOperation().owns(mat)) {
			this.sharedBy.setText(this.material.getAuthor());
		}
		this.background.clear();
		setPictureAsBackground();
		addSyncDecoration();
    }

	public void setShowDetailsListener(ShowDetailsListener listener){
		this.showListener = listener;
	}

	private String extractTitle(String key) {
	    return key.substring(key.indexOf("_", key.indexOf("_")+1)+1);
    }

	/**
	 * @return true if this material is saved local
	 */
	public boolean isLocal() {
		return this.material.getId() <= 0;
	}

	/**
	 * @return true if this material belongs to the signed in user
	 */
	public boolean isOwn() {
		return this.isOwnMaterial;
	}
}