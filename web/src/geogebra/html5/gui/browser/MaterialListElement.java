package geogebra.html5.gui.browser;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.html5.Browser;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.ResizeListener;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.AppWeb;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

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
	private FlowPanel infoPanel;

	protected FlowPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;

	private Label title, sharedBy;
	private TextBox renameTextBox;
	private final Material material;
	protected final AppWeb app;
	
	protected State state = State.Default;
	Runnable editMaterial;

	//TODO change from isLocal to isYourOwn
	protected boolean isLocalFile;

	private StandardButton viewButton;
	private StandardButton editButton;
	private StandardButton deleteButton;

	/**
	 * 
	 * @param m {@link Material}
	 * @param app {@link AppWeb}
	 */
	public MaterialListElement(final Material m, final AppWeb app) {
		this.app = app;
		this.material = m;
		this.isLocalFile = m.getId() <= 0 ? true : false;
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
		addOptions();

		this.materialElementContent.add(this.infoPanel);
	}

	private void addTextInfo() {
		this.infoPanel.add(this.title);
		if(!this.isLocalFile) {
			this.infoPanel.add(this.sharedBy);
		}
	}


	private void addOptions() {
		if (this.isLocalFile) {
			addEditButton();
			addDeleteButton();
		} else {
			addViewButton();
			addEditButton();
		}
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

		SimplePanel background = new SimplePanel();
		background.setStyleName("background");
		
		String thumb = this.material.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			if (!this.isLocalFile) {
				thumb = Browser.normalizeURL(thumb);
			}
			background.getElement().getStyle()
			.setBackgroundImage("url(" + thumb + ")");
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

	/**
	 * 
	 */
	void openDefault() {
		if (this.isLocalFile) {
			onEdit();
		} else {
			onView();
		}
	}

	private void initMaterialInfos() {
		this.title = new Label(this.material.getTitle());
		this.title.setStyleName("fileTitle");
				
		if (!this.isLocalFile) {
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
		}
	}

	protected void addDeleteButton() {
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
		this.viewButton.setVisible(false);
		this.editButton.setVisible(false);
		this.confirmDeletePanel.setVisible(true);
		this.sharedBy.setVisible(false);
		this.deleteButton.setIcon(BrowseResources.INSTANCE.document_delete_active());
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain("Delete"));
		this.confirm.addStyleName("deleteButton");
		this.confirm.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onConfirmDelete();
			}
		});
		this.cancel = new StandardButton(this.app.getLocalization().getPlain("Cancel"));
		this.cancel.addStyleName("cancelButton");
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
		((AppW) this.app).getFileManager().delete(this.material);
	}

	void onCancel() {
		showDetails(true);
	}

	private void addEditButton() {
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
	void onEdit() {
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().setMaterialsDefaultStyle();
		if(!this.isLocalFile){
			if(material.getType() == MaterialType.book){
				((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getBookItems(material.getId(), new MaterialCallback(){

					@Override
					public void onLoaded(final List<Material> response) {
						((GuiManagerW) app.getGuiManager()).getBrowseGUI().clearMaterials();
						((GuiManagerW) app.getGuiManager()).getBrowseGUI().onSearchResults(response);
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
		}else{
			((AppW) this.app).getFileManager().openMaterial(this.material, this.app);
		}
		closeBrowseView();
	}

	protected void closeBrowseView() {
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().close();
	}

	private void addViewButton() {
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
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().disableMaterials();
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().rememberSelected(this);
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
		if (this.isLocalFile) {
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

	private void showDetails(final boolean show) {
		if (this.isLocalFile) {
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
		//TODO tubeWindow for local files
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().setMaterialsDefaultStyle();
		if (!this.isLocalFile) {
			openTubeWindow(material.getURL());
//			openTubeWindow("http://tube-beta.geogebra.org/student/m123899?caller=tablet");
		}
		//		--> var wnd = window.open("about:blank", "", "_blank");
		//			wnd.document.write(html);
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