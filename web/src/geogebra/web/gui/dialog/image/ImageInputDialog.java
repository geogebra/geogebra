package geogebra.web.gui.dialog.image;

import geogebra.common.kernel.geos.GeoPoint;
import geogebra.html5.main.AppW;
import geogebra.web.gui.util.VerticalSeparator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ImageInputDialog extends DialogBox implements ClickHandler {
	
	private HorizontalPanel mainPanel;
	
	private VerticalPanel listPanel;
	private VerticalPanel imagePanel;
	private FlowPanel bottomPanel;
	
	private SimplePanel inputPanel;
	private WebCamInputPanel webcamPanel;
	private UploadImagePanel uploadImagePanel;

	private AppW app;
	private GeoPoint loc;
	
	private Button insertBtn;
	private Button cancelBtn;
	
	private Label upload;
	private Label webcam;
	
	public ImageInputDialog(AppW app, GeoPoint loc) {
		this.app = app;
		this.loc = loc;
		initGUI();
		initActions();
		setLabels();
		center();
		uploadClicked();
	}
	
	private void initGUI() {
		add(mainPanel = new HorizontalPanel());
		
		mainPanel.add(listPanel = new VerticalPanel());
		listPanel.add(upload = new Label(""));
		listPanel.add(webcam = new Label(""));
		listPanel.setSpacing(10);
		
		mainPanel.add(new VerticalSeparator(200));
		mainPanel.setSpacing(5);
		mainPanel.add(imagePanel = new VerticalPanel());
		
		imagePanel.add(inputPanel = new SimplePanel());
		inputPanel.setHeight("180px");
		inputPanel.setWidth("240px");
		
		uploadImagePanel = new UploadImagePanel(this, app);
		imagePanel.add(bottomPanel = new FlowPanel()); 
		
		bottomPanel.add(insertBtn = new Button(""));
		bottomPanel.add(cancelBtn = new Button(""));
		insertBtn.setEnabled(false);
		
		bottomPanel.setStyleName("DialogButtonPanel");
		addStyleName("GeoGebraPopup");
		setGlassEnabled(true);
	}
	
	private void initActions() {
		insertBtn.addClickHandler(this);
		cancelBtn.addClickHandler(this);
		upload.addClickHandler(this);
		webcam.addClickHandler(this);
	}
	
	public void setLabels() {
		getCaption().setText(app.getMenu("Image"));
		upload.setText(app.getMenu("File"));
		webcam.setText(app.getMenu("Webcam"));
		insertBtn.setText(app.getPlain("OK"));
		cancelBtn.setText(app.getMenu("Cancel"));
	}
	
	public void imageAvailable() {
		insertBtn.setEnabled(true);
	}

	public void imageUnavailable() {
		insertBtn.setEnabled(false);
	}
	
	private void uploadClicked() {
		upload.addStyleDependentName("highlighted");
		webcam.removeStyleDependentName("highlighted");
		inputPanel.setWidget(uploadImagePanel);
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
}
