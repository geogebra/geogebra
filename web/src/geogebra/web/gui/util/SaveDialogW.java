package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.web.main.FileManager;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;
import geogebra.web.util.SaveCallback;

import java.util.List;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveDialogW extends DialogBox {

	protected AppW app;
	VerticalPanel p;
	protected TextBox title;
	StandardButton cancel;
	StandardButton save;
		
	private Label titleLabel;
	private final int MIN_TITLE_LENGTH = 4;
	Runnable runAfterSave;
	SaveCallback saveCallback;
	

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
		
		this.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(final CloseEvent<PopupPanel> event) {
				app.setDefaultCursor();
				cancel.setEnabled(true);
				title.setEnabled(true);
			}
		});
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
		final FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");
		buttonPanel.add(cancel = new StandardButton(app.getMenu("Cancel")));
		buttonPanel.add(save = new StandardButton(app.getMenu("Save")));
		
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
	
	/**
	 * 
	 */
	protected void onSave() {
		if (isOffline()) {
			saveLocal();
		} else {
			upload();
		}
	}
	
	/**
	 * @return true if user is offline
	 */
	protected boolean isOffline() {
		return !app.getNetworkOperation().isOnline();
	}

	private boolean isLoggedIn() {
		return this.app.getLoginOperation().isLoggedIn();
	}
	
	private void saveLocal() {
	    ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Saving"), false);
	    if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
	    	app.resetUniqueId();
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
	 * Handles the upload of the file and closes the dialog.
	 * If there are sync-problems with a file, a new one is generated on ggt.
	 */
	void upload() {
		ToolTipManagerW.sharedInstance().showBottomMessage(app.getMenu("Saving"), false);

		if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
			app.resetUniqueId();
			doUpload();
		} else if (app.getUniqueId() == null) {
			doUpload();
		}
		else {
			handleSync();
		}
		hide();
	}

	private void handleSync() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).getItem(Integer.parseInt(app.getUniqueId()), new MaterialCallback(){

			@Override
			public void onLoaded(final List<Material> parseResponse) {
				if (parseResponse.size() == 1) {
					if (parseResponse.get(0).getModified() > app.getSyncStamp()) {
						app.resetUniqueId();
						//TODO Translation needed - wrong implementation
						ToolTipManagerW.sharedInstance().showBottomMessage("Note that there are several versions of: " + parseResponse.get(0).getTitle(), true);
					}
					doUpload();
				} else {
					// if the file was deleted meanwhile (parseResponse.size() == 0)
					app.resetUniqueId();
					doUpload();
				}
			}
			
			@Override
            public void onError(final Throwable exception) {
				//TODO show correct message
			    ToolTipManagerW.sharedInstance().showBottomMessage("Error", true);
		    }
		});
	}
	
	/**
	 * does the upload of the actual opened file to GeoGebraTube
	 */
	void doUpload() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadMaterial(app, this.title.getText(), new MaterialCallback() {

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
			}
		});
	}
	
	@Override
	public void show(){
		super.show();
		setTitle();
		if (this.title.getText().length() < MIN_TITLE_LENGTH) {
			this.save.setEnabled(false);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
	        public void execute () {
	        	title.setFocus(true);
	        }
	   });
	}

	public void saveSuccess(final String fName, final String desc) {
		if(app.getGoogleDriveOperation()!=null){
			app.getGoogleDriveOperation().refreshCurrentFileDescriptors(fName,desc);
		}
	}

	private void setTitle() {
		final String consTitle = app.getKernel().getConstruction().getTitle();
		if (consTitle != null) {
			this.title.setText(consTitle);
		} else {
			this.title.setText("");
		}
	}
	
	public void setLabels() {
		this.getCaption().setText(app.getMenu("Save"));
		this.titleLabel.setText(app.getPlain("Title") + ": ");
		this.cancel.setText(app.getMenu("Cancel"));
		this.save.setText(app.getMenu("Save"));
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
	
}
