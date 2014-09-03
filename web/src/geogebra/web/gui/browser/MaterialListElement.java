package geogebra.web.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.util.StringUtil;
import geogebra.html5.Browser;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.AppW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
public class MaterialListElement extends FlowPanel implements ResizeListener {
	
	public enum State {
		Default, Selected, Disabled;
	}
	
	private final int MAX_TITLE_HEIGHT = 40;
	private FlowPanel materialElementContent;
	private SimplePanel previewPicturePanel;
	protected FlowPanel infoPanel;
	protected boolean isLocal;
	protected boolean isOwnMaterial;

	protected Label title;
	protected Label sharedBy;
	private TextBox renameTextBox;
	protected final Material material;
	protected final AppW app;
	protected final GuiManagerW guiManager;
	
	protected State state = State.Default;
	Runnable editMaterial;

	protected StandardButton viewButton;
	protected StandardButton editButton;

	private FlowPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private StandardButton deleteButton;

	

	/**
	 * 
	 * @param m {@link Material}
	 * @param app {@link AppW}
	 */
	public MaterialListElement(final Material m, final AppW app, boolean isLocal) {
		this.app = (AppW) app;
		this.guiManager = (GuiManagerW) app.getGuiManager();
		this.material = m;
		this.isLocal = isLocal;
		this.isOwnMaterial = m.getAuthor().equals(app.getLoginOperation().getUserName());
		this.setStyleName("materialListElement");
		this.addStyleName("default");
		this.editMaterial = new Runnable() {
			
			@Override
			public void run() {
				onEdit();
			}
		};
		initMaterialInfos();
		
		materialElementContent = new FlowPanel();
		this.materialElementContent.addStyleName("materialElementContent");
		this.add(materialElementContent);
		
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
		if(!isLocal) {
			this.infoPanel.add(this.sharedBy);
		}
	}

	protected void addOptions() {
		if (isOwnMaterial && !isLocal) {
			addEditButton();
			addViewButton();
			addDeleteButton();
		} else if (isLocal) {
			addEditButton();
			addDeleteButton();
		} else {
			addEditButton();
			addViewButton();
		}
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

		final SimplePanel background = new SimplePanel();
		background.setStyleName("background");
		
		final String thumb = this.material.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			setPicture(background, thumb);
		} else {
			background
			.getElement()
			.getStyle()
			.setBackgroundImage(
					"url("
							+ AppResources.INSTANCE.geogebra64()
							.getSafeUri().asString() + ")");
		}
		
		this.previewPicturePanel.add(background);
		this.materialElementContent.add(this.previewPicturePanel);

		if(this.material.getType() == Material.MaterialType.book){
			final Label deco = new Label();
			deco.setStyleName("bookDecoration");
			background.add(deco);
		}
	}

	protected void setPicture(final SimplePanel background, String thumb) {
		 if (!isLocal) {
			 background.getElement().getStyle().setBackgroundImage("url(" + Browser.normalizeURL(thumb) + ")");
		} else {
			background.getElement().getStyle().setBackgroundImage("url(" + thumb + ")");
		}
    }

	private void addDeleteButton() {
		this.deleteButton = new StandardButton(BrowseResources.INSTANCE.document_delete());
		this.infoPanel.add(this.deleteButton);
		this.deleteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onDelete();
			}
		});
		initConfirmDeletePanel();
	}
	
	
	void onDelete() {
		this.deleteButton.addStyleName("deleteActive");
		if (!isLocal) {
			this.viewButton.setVisible(false);
		}
		this.editButton.setVisible(false);
		this.confirmDeletePanel.setVisible(true);
		this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete_active());
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain("Delete"));
		this.confirm.addStyleName("gwt-Button");
		this.confirm.addStyleName("deleteButton");
		this.confirm.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onConfirmDelete();
			}
		});
		this.cancel = new StandardButton(this.app.getLocalization().getPlain("Cancel"));
		this.cancel.addStyleName("cancelButton");
		this.cancel.addStyleName("gwt-Button");
		this.cancel.addStyleName("minor");
		this.cancel.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
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
		if (this.isLocal) {
			this.app.getFileManager().delete(this.material);
		} else {
			remove();
			((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).deleteMaterial(this.app, this.material, new MaterialCallback() {

				@Override
				public void onLoaded(List<Material> parseResponse) {
					ToolTipManagerW.sharedInstance().showBottomInfoToolTip("<html>" + StringUtil.toHTMLString("deleted") + "</html>", "");
				}
				
				@Override
				public void onError(Throwable exception) {
					ToolTipManagerW.sharedInstance().showBottomInfoToolTip("<html>" + StringUtil.toHTMLString("oops") + "</html>", "");
				}
			});
		}
		
	}

	protected void remove() {
		((BrowseGUI) app.getGuiManager().getBrowseGUI()).setMaterialsDefaultStyle();
        ((BrowseGUI) app.getGuiManager().getBrowseGUI()).removeFromLocalList(MaterialListElement.this.material);
    }
	
	void onCancel() {
		showDetails(true);
	}
	

	/**
	 * 
	 */
	protected void openDefault() {
		if (isLocal) {
			((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().setCallback(editMaterial);
			((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().showIfNeeded();
		} else {
			onView();
		}
	}

	protected void initMaterialInfos() {
		this.title = new Label(this.material.getTitle());
		this.title.setStyleName("fileTitle");
				
		if (!isLocal) {
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
		}
	}

	protected void addEditButton() {
		this.editButton = new StandardButton(BrowseResources.INSTANCE.document_edit(), "");
		this.infoPanel.add(this.editButton);
		this.editButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().setCallback(editMaterial);
				((DialogManagerW) app.getDialogManager()).getSaveUnsavedDialog().showIfNeeded();
			}
		});
	}

	/**
	 * 
	 */
	protected void onEdit() {
		this.guiManager.getBrowseGUI().setMaterialsDefaultStyle();
		if (!isLocal) {
			if(material.getType() == MaterialType.book){
				((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getBookItems(material.getId(), new MaterialCallback(){

					@Override
					public void onLoaded(final List<Material> response) {
						guiManager.getBrowseGUI().clearMaterials();
						guiManager.getBrowseGUI().onSearchResults(response);
					}
				});
				return;
			}
			((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getItem(material.getId(), new MaterialCallback(){

				@Override
				public void onLoaded(final List<Material> parseResponse) {
					app.getGgbApi().setBase64(parseResponse.get(0).getBase64());
				}
			});
		} else {
			this.app.getFileManager().openMaterial(this.material);
		}
		closeBrowseView();
		
	}

	protected void closeBrowseView() {
		this.guiManager.getBrowseGUI().close();
	}

	protected void addViewButton() {
		this.viewButton = new StandardButton(BrowseResources.INSTANCE.document_view(), "");
		this.viewButton.addStyleName("viewButton");
		this.infoPanel.add(this.viewButton);
		this.viewButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onView();
			}
		});
	}
	
	/**
	 * marks the material as selected and disables the other materials
	 */
	protected void markSelected() {
		this.guiManager.getBrowseGUI().disableMaterials();
		this.guiManager.getBrowseGUI().rememberSelected(this);
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
		this.editButton.setText(app.getLocalization().getMenu("Edit"));
		if (isOwnMaterial && !isLocal) {
			this.deleteButton.setText(app.getLocalization().getCommand("Delete"));
			this.cancel.setText(this.app.getLocalization().getPlain("Cancel"));
			this.confirm.setText(this.app.getLocalization().getPlain("Delete"));
			this.viewButton.setText(app.getMenu(getInsertWorksheetTitle(material)));
		} else if (isLocal) {
			this.deleteButton.setText(app.getLocalization().getCommand("Delete"));
			this.cancel.setText(this.app.getLocalization().getPlain("Cancel"));
			this.confirm.setText(this.app.getLocalization().getPlain("Delete"));
		} else {
			this.viewButton.setText(app.getMenu(getInsertWorksheetTitle(material)));
		}
	}

	/**
	 * 
	 * @return the {@link Material}
	 */
	public Material getMaterial() {
		return this.material;
	}

	protected void showDetails(final boolean show) {
		if (isOwnMaterial && !isLocal) {
			this.sharedBy.setVisible(true);
			this.viewButton.setVisible(show);
			this.deleteButton.setVisible(show);
			this.deleteButton.removeStyleName("deleteActive");
			this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete());
			this.confirmDeletePanel.setVisible(false);
		} else if (isLocal) {
			this.deleteButton.setVisible(show);
			this.deleteButton.removeStyleName("deleteActive");
			this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete());
			this.confirmDeletePanel.setVisible(false);
		} else {
			this.sharedBy.setVisible(true);
			this.viewButton.setVisible(show);
		}
		this.editButton.setVisible(show);
		
		if (show) {
			this.infoPanel.addStyleName("detailed");
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
		this.guiManager.getBrowseGUI().setMaterialsDefaultStyle();
		openTubeWindow(material.getURL());
	}

	/**
	 * Opens GeoGebraTube material in a new window
	 * @param id material id
	 */
	private native void openTubeWindow(String url)/*-{
		$wnd.open(url);
	}-*/;

	@Override
	public void onResize() {
		// TODO Auto-generated method stub

	}
}