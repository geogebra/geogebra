package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.common.move.events.BaseEvent;
import geogebra.common.move.views.EventRenderable;
import geogebra.html5.gui.FastClickHandler;
import geogebra.html5.gui.StandardButton;
import geogebra.html5.main.GgbAPIW;
import geogebra.html5.move.ggtapi.models.GeoGebraTubeAPIW;
import geogebra.web.gui.dialog.DialogManagerW;
import geogebra.web.main.AppW;

import com.google.gwt.core.client.JavaScriptObject;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SaveDialogW extends DialogBox implements EventRenderable {

	protected App app;
	VerticalPanel p;
	protected TextBox title;
	StandardButton cancel;
	StandardButton save;
	
	RadioButton materialPrivate;
	RadioButton materialShared;
	RadioButton materialPublic;
	
	Anchor downloadButton;
	
	private Label titleLabel;
	private final int MIN_TITLE_LENGTH = 4;

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
		addRadioButtons();
		addButtonPanel();
		
		this.addCloseHandler(new CloseHandler<PopupPanel>() {

			public void onClose(CloseEvent<PopupPanel> event) {
				app.setDefaultCursor();
				cancel.setEnabled(true);
				title.setEnabled(true);
			}
		});
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
	}

	private void addRadioButtons() {
		FlowPanel radioButtonPanel = new FlowPanel();
		radioButtonPanel.setStyleName("radioButtonPanel");
		
		//TODO translate
	    this.materialPrivate = new RadioButton("Material", "Privat");
	    this.materialShared = new RadioButton("Material", "Shared");
	    this.materialPublic = new RadioButton("Material", "Public");
	    
	    radioButtonPanel.add(this.materialPrivate);
	    radioButtonPanel.add(this.materialShared);
	    radioButtonPanel.add(this.materialPublic);
	    
	    this.materialPrivate.setValue(true);
	    
	    p.add(radioButtonPanel);
    }

	private void addButtonPanel() {
		FlowPanel buttonPanel = new FlowPanel();
		buttonPanel.addStyleName("buttonPanel");
		buttonPanel.add(cancel = new StandardButton(app.getMenu("Cancel")));
		buttonPanel.add(save = new StandardButton(app.getMenu("Save")));
		save.setEnabled(false);
		
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
			((DialogManagerW) app.getDialogManager()).showLogInDialog();
		} else {
			upload();
		}
	}


	/**
	 * Handles the upload of the file and closes the dialog
	 */
	void upload() {
	    if (this.materialPrivate.getValue()) {
	    	((GeoGebraTubeAPIW) app.getLoginOperation().getGeoGebraTubeAPI()).uploadMaterial((AppW) app, this.title.getText());
	    } else {
	    	((AppW) app).uploadToGeoGebraTube();
	    }
	    hide();
    }
	
	

	@Override
	public void show(){
		super.show();

		System.out.println("show");
		setTitle();
		if (this.title.getText().length() < MIN_TITLE_LENGTH) {
			this.save.setEnabled(false);
		}
		this.materialPrivate.setValue(true);
		this.title.setFocus(true);
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
		// TODO Auto-generated method stub

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
}
