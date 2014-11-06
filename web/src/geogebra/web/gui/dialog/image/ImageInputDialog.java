package geogebra.web.gui.dialog.image;

import geogebra.common.main.App;
import geogebra.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class ImageInputDialog extends UploadImageDialog implements ClickHandler {
	
	private static final int PREVIEW_HEIGHT = 155;
	private static final int PREVIEW_WIDTH = 213;
	
	private WebCamInputPanel webcamPanel;	
	private Label webcam;
	
	public ImageInputDialog(App app) {
		super((AppW) app, PREVIEW_WIDTH, PREVIEW_HEIGHT);
	}
	
	protected void initGUI() {
		super.initGUI();
		listPanel.add(webcam = new Label(""));		
	}
	
	protected void initActions() {
		super.initActions();
		webcam.addClickHandler(this);
	}
	
	public void setLabels() {
		super.setLabels();
		webcam.setText(app.getMenu("Webcam"));
	}
	
	
	protected void uploadClicked() {
		super.uploadClicked();
		webcam.removeStyleDependentName("highlighted");
		if (webcamPanel != null) {
			webcamPanel.stopVideo();
    		webcamPanel.clear();
    		webcamPanel = null;
		}
    	imageUnavailable();
	}
	
	private void webcamClicked() {
		webcam.addStyleDependentName("highlighted");
		upload.removeStyleDependentName("highlighted");
    	webcamPanel = new WebCamInputPanel(app);
    	inputPanel.setWidget(webcamPanel);
    	imageAvailable();
	}
	
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == insertBtn) {
	    	String data;
	    	String name;
	    	if (webcamPanel == null) { // file upload
	    		data = uploadImagePanel.getImageDataURL();
	    		name = uploadImagePanel.getFileName();
	    	} else { // webcam
	    		data = webcamPanel.getImageDataURL();
	    		name = "webcam";
	    	}
	    	if (!loc.isLabelSet()) {
	    		loc.setLabel(null);
	    	}
	    	app.imageDropHappened(name, data, "", loc);
	    	hide();
	    } else if (source == cancelBtn) {
	    	hide();
	    } else if (source == upload) {
	    	uploadClicked();
	    } else if (source == webcam) {
	    	webcamClicked();
	    }
	}
	
	@Override
	public void hide() {
		super.hide();
		if (this.uploadImagePanel != null) {
			this.uploadImagePanel.resetPreview();
		}
	}
}
