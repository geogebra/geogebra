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
import geogebra.web.gui.images.AppResources;
import geogebra.web.main.AppW;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
	private SimplePanel image;
	private FlowPanel infoPanel;

	protected HorizontalPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private final StandardButton showMore;
	private final StandardButton showLess;

	private Label title, sharedBy;
	private TextBox renameTextBox;
	private final Material material;
	protected final AppWeb app;
	protected boolean isSelected = false;
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

		this.showMore = new StandardButton(BrowseResources.INSTANCE.dots_options());
		this.showLess = new StandardButton(BrowseResources.INSTANCE.arrow_submenu_up());

		initMaterialInfos();

		addPreviewPicture();
		addInfoPanel();

		showDetails(false);

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				markSelected();
			}
		}, ClickEvent.getType());

		setLabels();
	}

	private void addInfoPanel() {
		this.infoPanel = new FlowPanel();
		this.infoPanel.setStyleName("infoPanel");

		addTextInfo();
		addOptions();

		this.add(this.infoPanel);
	}

	private void addTextInfo() {
		final FlowPanel firstLine = new FlowPanel();
		firstLine.setStyleName("firstLine");
		firstLine.add(this.title);

		this.showMore.addStyleName("showMoreButton");
		this.showMore.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				showMore();
			}
		});
		firstLine.add(this.showMore);
		this.infoPanel.add(firstLine);

		if(!this.isLocalFile) {
			this.infoPanel.add(this.sharedBy);
		}
	}


	private void addOptions() {
		addEditButton();
		if (this.isLocalFile) {
			addDeleteButton();
		} else {
			addViewButton();
		}
		this.infoPanel.add(this.showLess);
		this.showLess.addStyleName("showLessButton");
		this.showLess.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				//TODO event.stopPropagation
				//				if(CancelEventTimer.cancelMouseEvent()) {
				//					return;
				//				}
				showLess();
			}
		});
	}

	private void addPreviewPicture() {

		this.image = new SimplePanel();
		this.image.addStyleName("fileImage");
		this.image.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				markSelected();
				openDefault();
			}
		}, ClickEvent.getType());

		String thumb = this.material.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			if (!this.isLocalFile) {
				thumb = Browser.normalizeURL(thumb);
			}
			this.image.getElement().getStyle()
			.setBackgroundImage("url(" + thumb + ")");
		} else {
			this.image
			.getElement()
			.getStyle()
			.setBackgroundImage(
					"url("
							+ AppResources.INSTANCE.geogebra64()
							.getSafeUri().asString() + ")");
		}
		this.add(this.image);

		if(this.material.getType() == Material.MaterialType.book){
			final Label deco = new Label();
			deco.setStyleName("bookDecoration");
			this.image.add(deco);
		}
	}

	void openDefault() {
		if (this.isLocalFile) {
			onEdit();
		} else {
			onOpen();
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
		this.deleteButton.addStyleName("delete");
		this.deleteButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onDelete();
			}
		});
		initConfirmDeletePanel();
	}

	void onDelete() {		
		this.confirmDeletePanel.setVisible(true);
		this.deleteButton.setVisible(false);
	}

	private void initConfirmDeletePanel() {
		this.confirm = new StandardButton(this.app.getLocalization().getPlain("Delete"));
		this.confirm.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onConfirmDelete();
			}
		});
		this.cancel = new StandardButton(this.app.getLocalization().getPlain("Cancel"));
		this.cancel.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onCancel();
			}
		});

		this.confirmDeletePanel = new HorizontalPanel();
		this.confirmDeletePanel.add(this.confirm);
		this.confirmDeletePanel.add(this.cancel);
		this.confirmDeletePanel.setStyleName("confirmDelete");
		this.confirmDeletePanel.setVisible(false);
		this.infoPanel.add(this.confirmDeletePanel);
	}

	void onConfirmDelete() {
		((AppW) this.app).getFileManager().delete(this.material);
	}

	void onCancel() {
		this.confirmDeletePanel.setVisible(false);
		this.deleteButton.setVisible(true);
	}

	private void addEditButton() {
		this.editButton = new StandardButton(BrowseResources.INSTANCE.document_edit(), "");
		this.infoPanel.add(this.editButton);
		this.editButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick() {
				onEdit();
			}
		});
	}

	/**
	 * 
	 */
	void onEdit() {
		markUnSelected();
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
		app.setUnsaved();
		closeBrowseView();
	}

	protected void closeBrowseView() {
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().close();
	}

	private void addViewButton() {
		this.viewButton = new StandardButton(BrowseResources.INSTANCE.document_viewer(), "");
		this.infoPanel.add(this.viewButton);
		this.viewButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onOpen();
			}
		});
	}

	/**
	 * marks the material as selected
	 */
	protected void markSelected() {
		this.isSelected = true;
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().unselectMaterials();
		this.addStyleName("selected");
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().rememberSelected(this);
	}

	/**
	 * removes the selection 
	 */
	public void markUnSelected() {
		this.isSelected = false;
		this.removeStyleName("selected");
		showLess();
	}

	/**
	 * 
	 */
	public void setLabels() {
		this.editButton.setText(app.getLocalization().getPlain("Open"));
		if (this.isLocalFile) {
			this.deleteButton.setText(app.getLocalization().getCommand("Delete"));
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
		} else {
			this.viewButton.setVisible(show);
		}
		if (show) {
			this.showLess.setVisible(true);
			this.showMore.setVisible(false);
		} else {
			this.showLess.setVisible(false);
			this.showMore.setVisible(true);
		}
		this.editButton.setVisible(show);		
	}

	void showMore() {
		markSelected();
		this.addStyleName("showDetails");
		this.infoPanel.addStyleName("detailed");

		showDetails(true);
	}

	void showLess() {
		this.removeStyleName("showDetails");
		this.infoPanel.removeStyleName("detailed");
		if(this.isLocalFile) {
			this.confirmDeletePanel.setVisible(false);
		}

		showDetails(false);
	}

	/*** LAF dependent methods **/

	public String getInsertWorksheetTitle(final Material m) {
		return "View";
	}

	/**
	 * Opens GeoGebraTube material in a new window (overwritten for tablet app, smart widget)
	 */
	protected void onOpen() {
		//TODO tubeWindow for local files
		markUnSelected();
		if (!this.isLocalFile) {
			openTubeWindow(material.getURL());
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