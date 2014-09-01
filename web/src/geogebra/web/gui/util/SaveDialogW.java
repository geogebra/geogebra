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
import geogebra.html5.main.AppWeb;
import geogebra.html5.main.GgbAPIW;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.html5.move.ggtapi.models.MaterialCallback;
import geogebra.web.gui.GuiManagerW;
import geogebra.web.main.AppW;

import java.util.List;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Anchor;
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
	
	Anchor downloadButton;
	
	private Label titleLabel;
	private final int MIN_TITLE_LENGTH = 4;
	private boolean uploadWaiting;
	protected Callback<String, Throwable> cb;

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
		
		this.downloadButton = new Anchor();
		this.downloadButton.setStyleName("downloadButton");
		this.downloadButton.getElement().setAttribute("download", "geogebra.ggb");
		
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
	
	private boolean isLoggedIn() {
		return this.app.getLoginOperation().isLoggedIn();
	}

	/**
	 * 
	 */
	protected void onSave() {
		if (!isLoggedIn()) {
			this.uploadWaiting = true;
			((AppW)app).getLAF().getSignInButton(app).login();
		} else {
			upload();
		}
	}

	/**
	 * Handles the upload of the file and closes the dialog
	 */
	void upload() {
		//TODO - wait for translation:
		ToolTipManagerW.sharedInstance().showBottomInfoToolTip("<html>" + StringUtil.toHTMLString(app.getMenu("Save")) + "</html>", "");

		if (!this.title.getText().equals(app.getKernel().getConstruction().getTitle())) {
			((AppWeb) app).resetUniqueId();
		}
		((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadMaterial((AppW) app, this.title.getText(), new MaterialCallback() {

			@Override
			public void onLoaded(List<Material> parseResponse) {
				if (parseResponse.size() == 1) {
					app.getKernel().getConstruction().setTitle(title.getText());
					app.setUniqueId(Integer.toString(parseResponse.get(0).getId()));
					app.setSaved();
					((GuiManagerW) app.getGuiManager()).getBrowseGUI().loadFeatured();
					if (cb != null) {
						cb.onSuccess("Success");
						resetCallback();
					}
				}
			}
			
			@Override
			public void onError(Throwable exception) {
				cb.onFailure(exception);
				resetCallback();
			}
		});
		hide();
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
//	        	NativeEvent event = Document.get().createFocusEvent();
//	    		title.onBrowserEvent(Event.as(event));
	        }
	   });
	}

	public void saveSuccess(String fName, String desc) {
		((AppW) app).refreshCurrentFileDescriptors(fName,desc);
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

	public void openFilePicker() {
		JavaScriptObject callback = getDownloadCallback(this.downloadButton.getElement());
		((GgbAPIW) this.app.getGgbApi()).getGGB(true, callback);
    }

	private native JavaScriptObject getDownloadCallback(Element downloadButton) /*-{
		var _this = this;
	    return function(ggbZip) {
	    	var URL = $wnd.URL || $wnd.webkitURL;
	    	var ggburl = URL.createObjectURL(ggbZip);
	    	downloadButton.setAttribute("href", ggburl);
	    	if ($wnd.navigator.msSaveBlob) {
	    		$wnd.navigator.msSaveBlob(ggbZip, "geogebra.ggb");
	    	} else {
	    		downloadButton.click();
	    	}
	    }
    }-*/;
	
	public void setLabels() {
		this.getCaption().setText(app.getMenu("Save"));
		this.titleLabel.setText(app.getPlain("Title") + ": ");
		this.cancel.setText(app.getMenu("Cancel"));
		this.save.setText(app.getMenu("Save"));
	}

	public void setCallback(Callback<String, Throwable> callback) {
	    this.cb = callback;
    }
	
	protected void resetCallback() {
		this.cb = null;
	}
	
}
