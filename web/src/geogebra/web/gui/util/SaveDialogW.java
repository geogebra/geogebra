package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.ggtapi.events.LoginEvent;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.views.EventRenderable;
import geogebra.common.util.StringUtil;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.gui.tooltip.ToolTipManagerW;
import geogebra.html5.main.AppW;
import geogebra.html5.util.SaveCallback;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.gui.browser.SignInButton;
import geogebra.web.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.move.ggtapi.models.MaterialCallback;

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

public class SaveDialogW extends DialogBox implements EventRenderable {

	protected App app;
	VerticalPanel p;
	protected TextBox title;
	StandardButton cancel;
	StandardButton save;
		
	private Label titleLabel;
	private final int MIN_TITLE_LENGTH = 4;
	private boolean uploadWaiting;
	protected SaveCallback cb;

	/**
	 * @param app AppW
	 * 
	 * Creates a new GeoGebraFileChooser Window
	 */
	public SaveDialogW(final App app) {
		super();
		this.app = app;
		this.addStyleName("GeoGebraFileChooser");
		this.add(p = new VerticalPanel());
		this.setGlassEnabled(true);

		addTitelPanel();
		addButtonPanel();
		
		this.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(CloseEvent<PopupPanel> event) {
				app.setDefaultCursor();
				cancel.setEnabled(true);
				title.setEnabled(true);
			}
		});
		app.getLoginOperation().getView().add(this);
	}

	private void addTitelPanel() {
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		this.titleLabel = new Label(app.getPlain("Title") + ": ");
		titlePanel.add(this.titleLabel);
		titlePanel.add(title = new TextBox());
		title.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
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
		FlowPanel buttonPanel = new FlowPanel();
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
			if (!isLoggedIn()) {
				this.uploadWaiting = true;
				((SignInButton)((AppW)app).getLAF().getSignInButton(app)).login();
			} else {
				upload();
			}
		}
	}
	
	protected boolean isOffline() {
		return !((AppW) app).getNetworkOperation().isOnline();
	}

	private boolean isLoggedIn() {
		return this.app.getLoginOperation().isLoggedIn();
	}
	
	private void saveLocal() {
	    ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString(app.getMenu("Saving")) + "</html>", false);
	    if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
	    	((AppW) app).resetUniqueId();
	    }
	    app.getKernel().getConstruction().setTitle(this.title.getText());
	    ((AppW) app).getFileManager().saveFile(new SaveCallback() {

	    	@Override
	        public void onError(String errorMessage) {
	    		ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString(app.getLocalization().getError("SaveFileFailed")) + "</html>", true);
	    		if (cb != null) {
					resetCallback();
				}
	    	}

	    	@Override
	        public void onSaved() {
	    		ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString(app.getMenu("SavedSuccessfully")) + "</html>", true);
	    		app.setSaved();
	    		//TODO don't call loadFeatured() only update saved file!!!
	    		((GuiManagerW) app.getGuiManager()).getBrowseGUI().loadFeatured();
	    		if (cb != null) {
					cb.onSaved();
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
		ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString(app.getMenu("Saving")) + "</html>", false);

		if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
			((AppW) app).resetUniqueId();
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
					if (parseResponse.get(0).getModified() > ((AppW) app).getSyncStamp()) {
						((AppW) app).resetUniqueId();
						ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString("Note that there are several versions of: " + parseResponse.get(0).getTitle()) + "</html>", true);
					}
					doUpload();
				} else {
					// if the file was deleted meanwhile (parseResponse.size() == 0)
					((AppW) app).resetUniqueId();
					doUpload();
				}
			}
			
			@Override
            public void onError(Throwable exception) {
			    ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString("Error") + "</html>", true);
		    }
		});
	}
	
	/**
	 * does the upload of the actual opened file to GeoGebraTube
	 */
	void doUpload() {
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadMaterial((AppW) app, this.title.getText(), new MaterialCallback() {

			@Override
			public void onLoaded(List<Material> parseResponse) {
				if (parseResponse.size() == 1) {
					ToolTipManagerW.sharedInstance().showBottomMessage("<html>" + StringUtil.toHTMLString(app.getMenu("SavedSuccessfully")) + "</html>", true);
					app.getKernel().getConstruction().setTitle(title.getText());
					app.setUniqueId(Integer.toString(parseResponse.get(0).getId()));
					app.setSaved();		
					//last synchronization is equal to last modified 
					((AppW) app).setSyncStamp(parseResponse.get(0).getModified());
					((GuiManagerW) app.getGuiManager()).getBrowseGUI().refreshMaterial(parseResponse.get(0), false);
					if (cb != null) {
						cb.onSaved();
						resetCallback();
					}
				}
				else {
					cb.onError(app.getLocalization().getError("SaveFileFailed"));
					resetCallback();
				}
			}
			
			@Override
			public void onError(Throwable exception) {
				cb.onError(app.getLocalization().getError("SaveFileFailed"));
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

	public void saveSuccess(String fName, String desc) {
		((AppW) app).getGoogleDriveOperation().refreshCurrentFileDescriptors(fName,desc);
	}

	private void setTitle() {
		String consTitle = app.getKernel().getConstruction().getTitle();
		if (consTitle != null) {
			this.title.setText(consTitle);
		} else {
			this.title.setText("");
		}
	}

	@Override
	public void renderEvent(BaseEvent event) {
		if(this.uploadWaiting && event instanceof LoginEvent && ((LoginEvent)event).isSuccessful()){
			this.uploadWaiting = false;
			upload();
		}

	}
	
	public void setLabels() {
		this.getCaption().setText(app.getMenu("Save"));
		this.titleLabel.setText(app.getPlain("Title") + ": ");
		this.cancel.setText(app.getMenu("Cancel"));
		this.save.setText(app.getMenu("Save"));
	}

	public void setCallback(SaveCallback callback) {
	    this.cb = callback;
    }
	
	protected void resetCallback() {
		this.cb = null;
	}
	
}
