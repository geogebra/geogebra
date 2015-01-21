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
import geogebra.common.util.debug.Log;
import geogebra.html5.awt.GDimensionW;
import geogebra.html5.gui.FastButton;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.textbox.GTextBox;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.main.LocalizationW;
import geogebra.html5.main.StringHandler;
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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SaveDialogW extends DialogBoxW implements PopupMenuHandler, EventRenderable {
	
	private enum Visibility {
		Private(0), Shared(1), Public(2);
		
		private int index;
		private String token;
		
		Visibility(int index) {
			this.index = index;
			this.token = createToken();
		}
		
		int getIndex() {
			return this.index;
		}
		
		String getToken() {
			return this.token;
		}
		
		private String createToken() {
			if (this.index == 2) {
				return "O";
			} else if (this.index == 1) {
				return "S";
			} else {
				return "P";
			}
		}
	}

	private final String GGT_EDIT_URL = "http://tube.geogebra.org/material/edit/id/";
	protected AppW app;
	FlowPanel contentPanel;
	VerticalPanel p;
	protected TextBox title;
	StandardButton dontSaveButton;
	StandardButton saveButton;
	FastButton cancelButton;
		
	private Label titleLabel;
	private final int MIN_TITLE_LENGTH = 1;
	Runnable runAfterSave;
	SaveCallback saveCallback;
	private PopupMenuButton providerPopup;
	private FlowPanel buttonPanel;
	private ListBox listBox;

	// private MaterialCallback materialCB;
	

	/**
	 * @param app AppW
	 * 
	 * Creates a new GeoGebraFileChooser Window
	 */
	public SaveDialogW(final App app) {
		super();
		this.app = (AppW) app;
		this.addStyleName("GeoGebraFileChooser");
		this.setGlassEnabled(true);
		this.saveCallback = new SaveCallback(this.app);
		this.contentPanel = new FlowPanel();
		this.add(this.contentPanel);
		
		this.getCaption().setText(app.getMenu("Save"));
		this.p = new VerticalPanel();
		this.p.add(getTitelPanel());
		this.p.add(getButtonPanel());
		this.contentPanel.add(p);
		
		addCancelButton();
		
		this.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(final CloseEvent<PopupPanel> event) {
				app.setDefaultCursor();
				dontSaveButton.setEnabled(true);
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
	
	MaterialCallback initMaterialCB(final String base64, final String url) {
		return new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse) {
				if (parseResponse.size() == 1) {
					handleMaterialCallback(parseResponse.get(0));
					runAfterSaveCallback();

					Material newMat = parseResponse.get(0);
					newMat.setSyncStamp(newMat.getModified());
					app.setTubeId(newMat.getId());
					if (url != null) {
						Window.open(url, "_blank", "");
					}
					app.setSyncStamp(parseResponse.get(0).getModified());
					saveLocalIfNeeded(parseResponse.get(0).getModified());
				}
				else {
					saveCallback.onError();
					resetCallback();
					saveLocalIfNeeded(getCurrentTimestamp(app));
				}

				hide();
			}



			@Override
			public void onError(final Throwable exception) {
				Log.error("SAVE Error" + exception.getMessage());
				saveCallback.onError();
				resetCallback();
				((GuiManagerW) app.getGuiManager()).openFilePicker();
				saveLocalIfNeeded(getCurrentTimestamp(app));
				hide();
			}

			private void saveLocalIfNeeded(long modified) {
				if (app.getFileManager().shouldKeep(0)) {
					app.getKernel().getConstruction().setTitle(title.getText());
					((FileManager) app.getFileManager()).saveFile(base64,
					        modified,
					        new SaveCallback(app) {
						        @Override
						        public void onSaved(final Material mat,
						                final boolean isLocal) {
							        super.onSaved(mat, isLocal);
						        }
					        });
				}
			}
		};
    }
	
	public static long getCurrentTimestamp(AppW app) {
		return Math.max(System.currentTimeMillis() / 1000,
		        app.getSyncStamp() + 1);
	}

	/**
	 * @param newMat 
	 */
    void handleMaterialCallback(Material newMat) {
        app.getKernel().getConstruction().setTitle(title.getText());
		app.setTubeId(newMat.getId());
        //last synchronization is equal to last modified 
        app.setSyncStamp(newMat.getModified());
        newMat.setThumbnail(app.getEuclidianView1().getCanvasBase64WithTypeString());
        saveCallback.onSaved(newMat, false);
    }
	
	
	private HorizontalPanel getTitelPanel() {
		final HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.titleLabel = new Label(app.getPlain("Title") + ": ");
		titlePanel.add(this.titleLabel);
		titlePanel.add(title = new GTextBox());
		title.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(final KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER && saveButton.isEnabled()) {
					onSave();
				}
				else if (title.getText().length() < MIN_TITLE_LENGTH) {
					saveButton.setEnabled(false);
				} else {
					saveButton.setEnabled(true);
				}
			}
		});

		titlePanel.addStyleName("titlePanel");
		return titlePanel;
	}

	private FlowPanel getButtonPanel() {
		buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");
		buttonPanel.add(dontSaveButton = new StandardButton(app.getMenu("DontSave")));
		buttonPanel.add(saveButton = new StandardButton(app.getMenu("Save")));
		saveButton.addStyleName("saveButton");
		setAvailableProviders();
		//ImageOrText[] data, Integer rows, Integer columns, GDimensionW iconSize, geogebra.common.gui.util.SelectionTable mode
		
		saveButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onSave();
			}
		});

		dontSaveButton.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				onDontSave();
			}
		});
		
		return buttonPanel;
	}
	
	private void addCancelButton() {
		SimplePanel cancel = new SimplePanel();
		this.cancelButton = new StandardButton(BrowseResources.INSTANCE.dialog_cancel());
		this.cancelButton.addStyleName("cancelSaveButton");
		this.cancelButton.addFastClickHandler(new FastClickHandler() {
			@Override
			public void onClick(Widget source) {
				onCancel();
			}
		});
		
		cancel.add(this.cancelButton);
		contentPanel.add(cancel);
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
		
		listBox = new ListBox();
		listBox.addStyleName("visibility");
		listBox.addItem(app.getMenu("Private"));
		listBox.addItem(app.getMenu("Shared"));
		listBox.addItem(app.getMenu("Public"));
		listBox.setItemSelected(Visibility.Private.getIndex(), true);
		if(app.getLAF().externalDriveSupported()){
			providerPopup.addPopupHandler(this);
			providerPopup.setSelectedIndex(app.getFileManager().getFileProvider() == Provider.GOOGLE ? 1 : 0);
		}
		providerPopup.getElement().getStyle().setPosition(com.google.gwt.dom.client.Style.Position.ABSOLUTE);
		providerPopup.getElement().getStyle().setLeft(10, Unit.PX);
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
		if (app.isOffline() || !app.getLoginOperation().isLoggedIn()) {
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
	
	/**
	 * sets the application as "saved" and closes the dialog
	 */
	protected void onDontSave() {
		app.setSaved();
		hide();
		runAfterSaveCallback();
	}
	
	/**
	 * closes the dialog
	 */
	protected void onCancel() {
		hide();
	}

	private void savePublic() {
		if (isAlreadyPublicOrShared()) {
			app.getActiveMaterial().setVisibility(Visibility.Public.getToken());
			uploadToGgt(null);
		} else {
			savePrivateFirst(Visibility.Public.getToken());
		}
	}

    private void saveShared() {
	    if (isAlreadyPublicOrShared()) {
	    	app.getActiveMaterial().setVisibility(Visibility.Shared.getToken());
			uploadToGgt(null);
	    } else {
	    	savePrivateFirst(Visibility.Shared.getToken());
	    }
    }

    private void savePrivate() {
	    app.getActiveMaterial().setVisibility(Visibility.Private.getToken());
		uploadToGgt(null);
    }

	private void saveLocal() {
	    ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Saving"), false);
	    if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
	    	app.resetUniqueId();
	    	app.setLocalID(-1);
	    }
	    app.getKernel().getConstruction().setTitle(this.title.getText());
	    app.getGgbApi().getBase64(true, new StringHandler(){

			@Override
            public void handle(String s) {
				((FileManager) app.getFileManager()).saveFile(s,
				        getCurrentTimestamp(app),
				        new SaveCallback(app) {
			    	@Override
			    	public void onSaved(final Material mat, final boolean isLocal) {
			    		super.onSaved(mat, isLocal);
			    		runAfterSaveCallback();
			    	}
			    });
				hide();
            }});
	    
	    
	    
		
    }

	/**
	 * saves the file to ggt as "private", than opens the "Edit" page from ggt in a new window
	 * 
	 * @param visibility
	 */
	private void savePrivateFirst(final String visibility) {

		uploadToGgt(GGT_EDIT_URL + app.getTubeId() + "?visibility="
		        + visibility);

	}
	
	
	/**
	 * @return true if material was already public or shared
	 */
    private boolean isAlreadyPublicOrShared() {
	    return app.getActiveMaterial().getVisibility().equals(Visibility.Public.getToken()) ||
				app.getActiveMaterial().getVisibility().equals(Visibility.Shared.getToken());
    }

	/**
	 * Handles the upload of the file and closes the dialog.
	 * If there are sync-problems with a file, a new one is generated on ggt.
	 */
	private void uploadToGgt(final String url) {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Saving"), false);
		app.getGgbApi().getBase64(true, new StringHandler(){

			@Override
            public void handle(String base64) {
				if (!SaveDialogW.this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
					App.debug("SAVE filename changed");
					app.setTubeId(0);
					doUploadToGgt(base64, initMaterialCB(base64, url));
				} else if (app.getTubeId() == 0) {
					App.debug("SAVE had no Tube ID");
					doUploadToGgt(base64, initMaterialCB(base64, url));
				}
				else {
					handleSync(base64, initMaterialCB(base64, url));
				}

	            
            }
			
		});
		
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

	void handleSync(final String base64, final MaterialCallback materialCallback) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI())
		        .getItem(app.getTubeId() + "", new MaterialCallback() {

			@Override
			public void onLoaded(final List<Material> parseResponse) {
				if (parseResponse.size() == 1) {
					if (parseResponse.get(0).getModified() > app.getSyncStamp()) {
						        App.debug("SAVE MULTIPLE"
						                + parseResponse.get(0).getModified()
						                + ":" + app.getSyncStamp());
						        app.setTubeId(0);
						        ToolTipManagerW
						                .sharedInstance()
						                .showBottomMessage(
						                        ((LocalizationW) app
						                                .getLocalization())
						                                .getPlain(
						                                        "SeveralVersionsOfA",
						                                        parseResponse
						                                                .get(0)
						                                                .getTitle()),
						                        false);
					}
					doUploadToGgt(base64, materialCallback);
				} else {
					// if the file was deleted meanwhile (parseResponse.size() == 0)
					app.resetUniqueId();
					doUploadToGgt(base64, materialCallback);
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
	 * @param materialCallback {@link MaterialCallback}
	 */
	void doUploadToGgt(String base64, MaterialCallback materialCallback) {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadMaterial(app, this.title.getText(),base64, materialCallback);
	}
	
	@Override
	public void show(){
		super.show();
		setTitle();
		if (app.isOffline()) {
			this.providerPopup.setVisible(false);
			this.listBox.setVisible(false);
		} else {
			this.providerPopup.setVisible(true);
			this.providerPopup.setSelectedIndex(0);
			app.getFileManager().setFileProvider(geogebra.common.move.ggtapi.models.Material.Provider.TUBE);
			this.listBox.setVisible(true);
			if (app.getActiveMaterial() != null) {
				if (app.getActiveMaterial().getVisibility().equals(Visibility.Public.getToken())) {
					this.listBox.setSelectedIndex(Visibility.Public.getIndex());
				} else if (app.getActiveMaterial().getVisibility().equals(Visibility.Shared.getToken())) {
					this.listBox.setSelectedIndex(Visibility.Shared.getIndex());
				} else {
					this.listBox.setSelectedIndex(Visibility.Private.getIndex());
				}
			} else {
				this.listBox.setSelectedIndex(Visibility.Private.getIndex());
			}
		}

		if (this.title.getText().length() < MIN_TITLE_LENGTH) {
			this.saveButton.setEnabled(false);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			public void execute () {
				title.setFocus(true);
			}
		});
	}
	
	/**
	 * shows the {@link SaveDialogW} if there are unsaved changes
	 * before editing another file or creating a new one
	 * 
	 * @param newConstruction {@link Runnable}
	 */
	public void showIfNeeded(Runnable newConstruction) {
		runAfterSave = newConstruction;
		if (!app.isSaved()) {
			center();
		} else {
			runAfterSaveCallback();
		}
	}

	private void setTitle() {
		String consTitle = app.getKernel().getConstruction().getTitle();
		if (consTitle != null && !consTitle.equals("")) {
			if (consTitle.startsWith(FileManager.FILE_PREFIX)) {
				consTitle = getTitleOnly(consTitle);
			}
			this.title.setText(consTitle);
		} else {
			this.title.setText(app.getMenu("Untitled"));
			this.title.setSelectionRange(0, this.title.getText().length());
		}
	}
	
	private String getTitleOnly(String key) {
	    return key.substring(key.indexOf("_", key.indexOf("_")+1)+1);
    }
	
	public void setLabels() {
		this.getCaption().setText(app.getMenu("Save"));
		this.titleLabel.setText(app.getPlain("Title") + ": ");
		this.dontSaveButton.setText(app.getMenu("DontSave"));
		this.saveButton.setText(app.getMenu("Save"));
		this.listBox.setItemText(Visibility.Private.getIndex(), app.getMenu("Private"));
		this.listBox.setItemText(Visibility.Shared.getIndex(), app.getMenu("Shared"));
		this.listBox.setItemText(Visibility.Public.getIndex(), app.getMenu("Public"));
	}

	/**
	 * runs the callback
	 */
	public void runAfterSaveCallback() {
		if (runAfterSave != null) {
			runAfterSave.run();
			resetCallback();
		}
	}
	
	/**
	 * resets the callback
	 */
	void resetCallback() {
		this.runAfterSave = null;
	}
	
	@Override
    public void fireActionPerformed(PopupMenuButton actionButton) {
	    if(actionButton.getSelectedIndex() == 1){
	    	if (!app.isOffline()) {
		    	listBox.setVisible(false);
	    	}
	    	app.getFileManager().setFileProvider(geogebra.common.move.ggtapi.models.Material.Provider.GOOGLE);
	    }else{
	    	app.getFileManager().setFileProvider(geogebra.common.move.ggtapi.models.Material.Provider.TUBE);
	    	if(!app.isOffline()) {
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
