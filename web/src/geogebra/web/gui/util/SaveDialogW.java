package geogebra.web.gui.util;

import geogebra.common.gui.util.SelectionTable;
import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LogOutEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.GeoGebraTubeUser;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Material.MaterialType;
import geogebra.common.move.ggtapi.models.Material.Provider;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.LocalizationW;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.BrowseResources;
import geogebra.web.gui.dialog.DialogBoxW;
import geogebra.web.main.FileManager;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.move.googledrive.operations.GoogleDriveOperationW;
import geogebra.web.util.SaveCallback;

import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveDialogW extends DialogBoxW implements PopupMenuHandler, EventRenderable {

	private final int INDEX_PRIVATE = 0;
	private final int INDEX_SHARED = 1;
	private final int INDEX_PUBLIC = 2;
	
	protected AppW app;
	VerticalPanel p;
	protected TextBox title;
	StandardButton cancel;
	StandardButton save;
		
	private Label titleLabel;
	private final int MIN_TITLE_LENGTH = 4;
	Runnable runAfterSave;
	SaveCallback saveCallback;
	private PopupMenuButton providerPopup;
	private FlowPanel buttonPanel;
	private ListBox listBox;
	private MaterialCallback materialCallback;
	

	/**
	 * @param app AppW
	 * 
	 * Creates a new GeoGebraFileChooser Window
	 */
	public SaveDialogW(final App app) {
		super();
		this.app = (AppW) app;
		this.addStyleName("GeoGebraFileChooser");
		this.add(p = new VerticalPanel());
		this.setGlassEnabled(true);
		this.saveCallback = new SaveCallback(this.app);

		addTitelPanel();
		addButtonPanel();
		initMaterialCB();
		this.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(final CloseEvent<PopupPanel> event) {
				app.setDefaultCursor();
				cancel.setEnabled(true);
				title.setEnabled(true);
				((AppW) app).closePopups();
			}
		});
		this.addDomHandler(new ClickHandler(){

			@Override
            public void onClick(ClickEvent event) {
				((AppW) app).closePopups();
	            
            }}, ClickEvent.getType());
		app.getLoginOperation().getView().add(this);
	}

	private void initMaterialCB() {
		this.materialCallback = new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse) {
				if (parseResponse.size() == 1) {
					final Material newMat = parseResponse.get(0); 
					app.getKernel().getConstruction().setTitle(title.getText());
					app.setUniqueId(Integer.toString(newMat.getId()));
					//last synchronization is equal to last modified 
					app.setSyncStamp(newMat.getModified());
					newMat.setThumbnail(app.getEuclidianView1().getCanvasBase64WithTypeString());
					saveCallback.onSaved(newMat, false);
					if (runAfterSave != null) {
						runAfterSave.run();
					}
				}
				else {
					saveCallback.onError();
				}
				resetCallback();
			}
			
			@Override
			public void onError(final Throwable exception) {
				saveCallback.onError();
				resetCallback();
				((GuiManagerW) app.getGuiManager()).openFilePicker();
			}
		};
    }

	private void addTitelPanel() {
		final HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.titleLabel = new Label(app.getPlain("Title") + ": ");
		titlePanel.add(this.titleLabel);
		titlePanel.add(title = new TextBox());
		title.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && save.isEnabled()) {
					onSave();
				}
				else if (title.getText().length() < MIN_TITLE_LENGTH) {
					save.setEnabled(false);
				} else {
					save.setEnabled(true);
				}
			}
		});

		titlePanel.addStyleName("titlePanel");
		p.add(titlePanel);
		this.getCaption().setText(app.getMenu("Save"));
	}

	private void addButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");
		buttonPanel.add(cancel = new StandardButton(app.getMenu("Cancel")));
		buttonPanel.add(save = new StandardButton(app.getMenu("Save")));
		setAvailableProviders();
		//ImageOrText[] data, Integer rows, Integer columns, GDimensionW iconSize, geogebra.common.gui.util.SelectionTable mode
		
		save.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				onSave();
			}
		});

		cancel.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick() {
				app.setDefaultCursor();
				hide();
			}
		});
		
		p.add(buttonPanel);
	}
	
	private void setAvailableProviders(){
		ImageResource[] providerImages = new ImageResource[3];
		providerImages[0] = BrowseResources.INSTANCE.location_tube();
		int providerCount = 1;
		GeoGebraTubeUser user = app.getLoginOperation().getModel().getLoggedInUser();
		if(user != null && user.hasGoogleDrive()){
			providerImages[providerCount++] = BrowseResources.INSTANCE.location_drive();
		}
		if(user != null && user.hasOneDrive()){
			providerImages[providerCount++] = BrowseResources.INSTANCE.location_skydrive();
		}
		if(providerPopup != null){
			buttonPanel.remove(providerPopup);
		}
		providerPopup = new PopupMenuButton(app, ImageOrText.convert(providerImages, 24),1,providerCount,new GDimensionW(32,32),SelectionTable.MODE_IMAGE);
		providerPopup.addPopupHandler(this);
		providerPopup.getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
		providerPopup.getElement().getStyle().setLeft(10, Unit.PX);
		providerPopup.setSelectedIndex(app.getFileManager().getFileProvider() == Provider.GOOGLE ? 1 : 0);
		
		listBox = new ListBox();
		listBox.addStyleName("visibility");
		listBox.addItem(app.getMenu("Private"));
		listBox.addItem(app.getMenu("Shared"));
		listBox.addItem(app.getMenu("Public"));
		listBox.setItemSelected(INDEX_PRIVATE, true);
		
		buttonPanel.add(providerPopup);
		buttonPanel.add(listBox);
	}
	
	/**
	 * if user is offline, save local.
	 * </br>if user is online and 
	 * </br>- has chosen GOOGLE, {@code uploadToDrive}
	 * </br>- material was already saved as PUBLIC or SHARED, than only update (API)
	 * </br>- material is new or was private, than link to GGT
	 */
	protected void onSave() {
		if (isOffline()) {
			saveLocal();
		} else if (app.getFileManager().getFileProvider() == Provider.GOOGLE) {
			uploadToDrive();
		} else {
			if (app.getActiveMaterial() == null) {
				app.setActiveMaterial(new Material(0, MaterialType.ggb));
			}
			switch (listBox.getSelectedIndex()) {
			case 0:
				savePrivate();
				break;
			case 1:
				saveShared();
				break;
			case 2:
				savePublic();
				break;
			default: 
				savePrivate();
				break;
			}
		}
	}

	private void savePublic() {
		if (isAlreadyPublicOrShared()) {
			app.getActiveMaterial().setVisibility("O");
			uploadToGgt();
		} else {
			//link to GGT
			app.getActiveMaterial().setVisibility("O");
			app.uploadToGeoGebraTube(); //TODO overwrite for TOUCH!
			this.hide();
		}
	}

    private void saveShared() {
	    if (isAlreadyPublicOrShared()) {
	    	app.getActiveMaterial().setVisibility("S");
	    	uploadToGgt();
	    } else {
	    	//link to GGT
	    	app.getActiveMaterial().setVisibility("S");
	    	app.uploadSharedToGgt(); //TODO overwrite for TOUCH!
	    	this.hide();
	    }
    }

    private void savePrivate() {
	    app.getActiveMaterial().setVisibility("P");
	    uploadToGgt();
    }

	/**
	 * @return true if user is offline
	 */
	protected boolean isOffline() {
		return !app.getNetworkOperation().isOnline();
	}
	
	private void saveLocal() {
	    ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Saving"), false);
	    if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
	    	app.resetUniqueId();
	    	app.setLocalID(-1);
	    }
	    app.getKernel().getConstruction().setTitle(this.title.getText());
	    ((FileManager)app.getFileManager()).saveFile(new SaveCallback(app) {
	    	@Override
	    	public void onSaved(final Material mat, final boolean isLocal) {
	    		super.onSaved(mat, isLocal);
	    		if (runAfterSave != null) {
	    			runAfterSave.run();
	    			resetCallback();
	    		}
	    	}
	    });
	    
		hide();
    }

	/**
	 * @return true if material was already public or shared
	 */
    private boolean isAlreadyPublicOrShared() {
	    return app.getActiveMaterial().getVisibility().equals("O") ||
				app.getActiveMaterial().getVisibility().equals("S");
    }

	/**
	 * Handles the upload of the file and closes the dialog.
	 * If there are sync-problems with a file, a new one is generated on ggt.
	 */
	private void uploadToGgt() {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Saving"), false);
		if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
			app.resetUniqueId();
			doUploadToGgt();
		} else if (app.getUniqueId() == null) {
			doUploadToGgt();
		}
		else {
			handleSync();
		}
		hide();
	}

	private void uploadToDrive() {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Saving"), false);
		app.getGoogleDriveOperation().afterLogin(new Runnable(){

			@Override
			public void run() {
				doUploadToDrive();
			}

		});
    }

	/**
	 * GoogleDrive upload
	 */
	void doUploadToDrive() {
		String saveName = this.title.getText();
		if(!saveName.endsWith(".ggb")){
			app.getKernel().getConstruction().setTitle(saveName);
			saveName += ".ggb";
		} else {
			app.getKernel().getConstruction().setTitle(saveName.substring(0,saveName.length()-4));
		}
		JavaScriptObject callback = ((GoogleDriveOperationW) app.getGoogleDriveOperation()).getPutFileCallback(saveName, "GeoGebra");
		app.getGgbApi().getBase64(true, callback);
    }

	private void handleSync() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getItem(Integer.parseInt(app.getUniqueId()), new MaterialCallback(){

			@Override
			public void onLoaded(final List<Material> parseResponse) {
				if (parseResponse.size() == 1) {
					if (parseResponse.get(0).getModified() > app.getSyncStamp()) {
						app.resetUniqueId();
						ToolTipManagerW.sharedInstance().showBottomMessage(((LocalizationW) app.getLocalization()).getMenu("SeveralVersionsOfA", parseResponse.get(0).getTitle()), false);
					}
					doUploadToGgt();
				} else {
					// if the file was deleted meanwhile (parseResponse.size() == 0)
					app.resetUniqueId();
					doUploadToGgt();
				}
			}
			
			@Override
            public void onError(final Throwable exception) {
				//TODO show correct message
				app.showError("Error");
		    }
		});
	}
	
	/**
	 * does the upload of the actual opened file to GeoGebraTube
	 */
	void doUploadToGgt() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadMaterial(app, this.title.getText(), this.materialCallback);
	}
	
	@Override
	public void show(){
		super.show();
		setTitle();
		if (isOffline()) {
			this.providerPopup.setVisible(false);
			this.listBox.setVisible(false);
		} else {
			this.providerPopup.setVisible(true);
			this.listBox.setVisible(true);
			if (app.getActiveMaterial() != null) {
				if (app.getActiveMaterial().getVisibility().equals("O")) {
					this.listBox.setSelectedIndex(INDEX_PUBLIC);
				} else if (app.getActiveMaterial().getVisibility().equals("S")) {
					this.listBox.setSelectedIndex(INDEX_SHARED);
				} else {
					this.listBox.setSelectedIndex(INDEX_PRIVATE);
				}
			} else {
				this.listBox.setSelectedIndex(INDEX_PRIVATE);
			}
	}
		
		if (this.title.getText().length() < MIN_TITLE_LENGTH) {
			this.save.setEnabled(false);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        public void execute () {
	        	title.setFocus(true);
	        }
	   });
	}

	private void setTitle() {
		String consTitle = app.getKernel().getConstruction().getTitle();
		
		if (consTitle != null) {
			if (consTitle.startsWith(FileManager.FILE_PREFIX)) {
				consTitle = getTitleOnly(consTitle);
			}
			this.title.setText(consTitle);
		} else {
			this.title.setText("");
		}
	}
	
	private String getTitleOnly(String key) {
	    return key.substring(key.indexOf("_", key.indexOf("_")+1)+1);
    }
	
	public void setLabels() {
		this.getCaption().setText(app.getMenu("Save"));
		this.titleLabel.setText(app.getPlain("Title") + ": ");
		this.cancel.setText(app.getMenu("Cancel"));
		this.save.setText(app.getMenu("Save"));
		this.listBox.setItemText(INDEX_PRIVATE, app.getMenu("Private"));
		this.listBox.setItemText(INDEX_SHARED, app.getMenu("Shared"));
		this.listBox.setItemText(INDEX_PUBLIC, app.getMenu("Public"));
	}

	/**
	 * set callback to run after file was saved (e.g. new / edit)
	 * @param callback Runnable
	 */
	public void setCallback(final Runnable callback) {
	    this.runAfterSave = callback;
    }
	
	/**
	 * reset callback
	 */
	protected void resetCallback() {
		this.runAfterSave = null;
	}

	@Override
    public void fireActionPerformed(PopupMenuButton actionButton) {
	    if(actionButton.getSelectedIndex() == 1){
	    	if (!isOffline()) {
		    	listBox.setVisible(false);
	    	}
	    	app.getFileManager().setFileProvider(geogebra.common.move.ggtapi.models.Material.Provider.GOOGLE);
	    }else{
	    	app.getFileManager().setFileProvider(geogebra.common.move.ggtapi.models.Material.Provider.TUBE);
	    	if(!isOffline()) {
		    	listBox.setVisible(true);
	    	}
	    }
	    providerPopup.getMyPopup().hide();
    }

	@Override
    public void renderEvent(BaseEvent event) {
	    if(event instanceof LoginEvent || event instanceof LogOutEvent){
	    	this.setAvailableProviders();
	    }
    }
}
