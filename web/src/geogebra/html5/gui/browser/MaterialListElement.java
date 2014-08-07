package geogebra.html5.gui.browser;

import geogebra.common.kernel.commands.CmdGetTime;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.util.Unicode;
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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GUI Element showing a Material as search Result
 * 
 * @author Matthias Meisinger
 * 
 */
public class MaterialListElement extends FlowPanel implements ResizeListener {
	private SimplePanel image;
	private VerticalPanel infos;
	protected VerticalPanel links;
	private final VerticalPanel centeredContent;
	protected HorizontalPanel confirmDeletePanel;
	private StandardButton confirm;
	private StandardButton cancel;
	private StandardButton deleteButton;
	private Label title, date, sharedBy;
	private final Material material;
	protected final AppWeb app;
	protected boolean isSelected = false;
	protected boolean isLocalFile;

	//TODO: Translate Insert Worksheet and Edit
	private final StandardButton viewButton;
	private final StandardButton editButton;

	/**
	 * 
	 * @param m {@link Material}
	 * @param app {@link AppWeb}
	 */
    public MaterialListElement(final Material m, final AppWeb app) {
		viewButton = new StandardButton(
				BrowseResources.INSTANCE.document_viewer(), "");
		editButton = new StandardButton(
				BrowseResources.INSTANCE.document_edit(), "");
		this.app = app;
		this.material = m;
		this.isLocalFile = m.getId() <= 0 ? true : false;
		this.setStyleName("materialListElement");

		this.centeredContent = new VerticalPanel();
		this.centeredContent.setStyleName("centeredContent");
		
		initButtons();
		initMaterialInfos();
		
		this.add(this.centeredContent);
		this.add(this.links);

		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				event.preventDefault();
				materialSelected();
			}
		}, ClickEvent.getType());
		setLabels();
	}

    private void materialSelected() {
		if (this.isSelected) {
			if (this.isLocalFile) {
				onEdit();
			} else {
				onOpen();
			}
		} else {
			markSelected();
		}
	}

    private void initMaterialInfos() {
		this.image = new SimplePanel();
		this.image.addStyleName("fileImage");
		this.infos = new VerticalPanel();
		this.infos.setStyleName("fileDescription");

		this.title = new Label(this.material.getTitle());
		this.title.setStyleName("fileTitle");
		this.infos.add(this.title);

		this.add(this.image);

		String thumb = this.material.getThumbnail();
		if (thumb != null && thumb.length() > 0) {
			if (!this.isLocalFile) {
				thumb = Browser.normalizeURL(thumb);
			}
			this.image.getElement().getStyle().setBackgroundImage("url(" + thumb + ")");
		} else {
			this.image
			        .getElement()
			        .getStyle()
			        .setBackgroundImage(
			                "url("
			                        + AppResources.INSTANCE.geogebra64()
			                                .getSafeUri().asString() + ")");
		}
		if(this.material.getType() == Material.MaterialType.book){
			final Label deco = new Label();
			deco.setStyleName("bookDecoration");
			this.image.add(deco);
		}
		// no shared Panel for local files
		if (!this.isLocalFile) {
			this.sharedBy = new Label(this.material.getAuthor());
			this.sharedBy.setStyleName("sharedPanel");
			this.infos.add(this.sharedBy);
		}
		
		final String format = this.app.getLocalization().isRightToLeftReadingOrder() ? "\\Y "
		        + Unicode.LeftToRightMark
		        + "\\F"
		        + Unicode.LeftToRightMark
		        + " \\j"
		        : "\\j \\F \\Y";

		this.date = new Label(CmdGetTime.buildLocalizedDate(format,
		        this.material.getDate(), this.app.getLocalization()));
		this.infos.add(this.date);
		this.centeredContent.add(this.infos);
	}

	/**
	 * Initializes the panel of the buttons 'edit' and 'open'
	 */
    protected void initButtons() {
		this.links = new VerticalPanel();
		this.links.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.links.setStyleName("fileLinks");
		this.links.setVisible(false);
		
		addArrowPanel();

		initEditButton();
		if (this.isLocalFile) {
			initDeleteButton();
		} else {
			initViewButton();
		}
	}

	protected void addArrowPanel() {
	    final FlowPanel arrowPanel = new FlowPanel();
		final Image arrow = new Image(BrowseResources.INSTANCE.arrow_submenu());
		arrowPanel.add(arrow);
		arrowPanel.setStyleName("arrowPanel");
		this.links.add(arrowPanel);
    }
	
    protected void initDeleteButton() {
		this.deleteButton = new StandardButton(BrowseResources.INSTANCE.document_delete());
		this.links.add(this.deleteButton);
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
		this.links.setVisible(false);
		this.infos.setVisible(false);
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
		this.centeredContent.add(this.confirmDeletePanel);
	}

	void onConfirmDelete() {
		((AppW) this.app).getFileManager().delete(this.material);
	}

	void onCancel() {
		this.infos.setVisible(true);
		markUnSelected();
	}

	
	
	/**
	 * adds a {@link FastClickHandler}
	 */
	protected void initEditButton() {
		this.links.add(this.editButton);
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
	private void onEdit() {
		/* TODO */
		markUnSelected();
		if(!this.isLocalFile){
			if(material.getType() == MaterialType.book){
				((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getBookItems(material.getId(), new MaterialCallback(){

					@Override
		            public void onLoaded(final List<Material> response) {
						//FIXME don't use browseGUI here!
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

	/**
	 * adds a {@link FastClickHandler}
	 */
	protected void initViewButton() {
		this.links.add(this.viewButton);
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
		//FIXME don't use browseGUI here!
		this.isSelected = true;
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().unselectMaterials();
		this.addStyleName("selected");
		this.links.setVisible(true);
		((GuiManagerW) app.getGuiManager()).getBrowseGUI().rememberSelected(this);
	}

	/**
	 * removes the selection 
	 */
	public void markUnSelected() {
		this.isSelected = false;
		this.removeStyleName("selected");
		this.links.setVisible(false);
		if(this.isLocalFile) {
			this.confirmDeletePanel.setVisible(false);
		}
	}

	/**
	 * 
	 */
	public void setLabels() {
		this.viewButton.setText(app.getMenu(getInsertWorksheetTitle(material)));
		this.editButton.setText(app.getLocalization().getPlain("Open"));
		if (this.isLocalFile) {
			this.deleteButton.setText(app.getLocalization().getCommand("Delete"));
		}
	}

	/**
	 * 
	 * @return the {@link Material}
	 */
	public Material getMaterial() {
		return this.material;
	}
	
	@Override
	public void onResize() {
		//FIXME do we need this?
//		if (((GuiManagerW) app.getGuiManager()).getBrowseGUI().getOffsetWidth() < 780) {
//			this.image.addStyleName("scaleImage");
//		}
//		else {
//			this.image.removeStyleName("scaleImage");
//		}
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
}