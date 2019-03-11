package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.common.main.App;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;

public class ImageInputDialog extends UploadImageDialog implements WebcamDialogInterface {

	private static final int PREVIEW_HEIGHT = 155;
	private static final int PREVIEW_WIDTH = 213;

	private WebCamInputPanel webcamPanel;
	private Label webcamLabel;

	public ImageInputDialog(App app) {
		super((AppW) app, PREVIEW_WIDTH, PREVIEW_HEIGHT);
	}

	@Override
	protected void initGUI() {
		super.initGUI();
		addStyleName("camera");
		if (webcamSupported()) {
			listPanel.add(webcamLabel = new Label(""));
		}
	}

	protected boolean webcamSupported() {
		return Browser.supportsWebcam();
	}

	@Override
	protected void initActions() {
		super.initActions();
		if (webcamLabel != null) {
			webcamLabel.addClickHandler(this);
		}
	}

	@Override
	public void setLabels() {
		super.setLabels();
		if (webcamLabel != null) {
			webcamLabel.setText(appw.getLocalization().getMenu("Webcam"));
		}
	}

	@Override
	protected void uploadClicked() {
		super.uploadClicked();
		if (webcamLabel != null) {
			webcamLabel.removeStyleDependentName("highlighted");
			if (webcamPanel != null) {
				webcamPanel.stopVideo();
				webcamPanel.clear();
				webcamPanel = null;
			}
		}
		setPreviewDimensions();
		imageUnavailable();
	}

	protected void webcamClicked() {
		webcamLabel.addStyleDependentName("highlighted");
		upload.removeStyleDependentName("highlighted");
		defaultToUpload = false;
		if (webcamPanel == null) {
			webcamPanel = new WebCamInputPanel(appw, this);
		} else {
			webcamPanel.startVideo();
		}

		inputPanel.setWidget(webcamPanel);
		imageAvailable();
	}

	@Override
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
			if (location != null && !location.isLabelSet()) {
				location.setLabel(null);
	    	}
			appw.imageDropHappened(name, data);
	    	hide();
	    } else if (source == cancelBtn) {
	      	appw.getImageManager().setPreventAuxImage(false);
			appw.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
					ModeSetter.TOOLBAR);
	  	   	hide();
	    } else if (source == upload) {
	    	uploadClicked();
	    	centerAndResize(0);
	    } else if (webcamLabel != null && source == webcamLabel) {
	    	webcamClicked();
	    }
	}

	@Override
	public void hide() {
		super.hide();
		this.defaultToUpload = true;
		if (this.uploadImagePanel != null) {
			this.uploadImagePanel.resetPreview();
		}
		if (this.webcamPanel != null) {
			this.webcamPanel.stopVideo();
		}
	}

	@Override
	public void resize() {
		Style style = inputPanel.getElement().getStyle();
		style.clearWidth();
		style.clearHeight();
	}

	private void setPreviewDimensions() {
		inputPanel.setHeight(PREVIEW_HEIGHT + "px");
		inputPanel.setWidth(PREVIEW_WIDTH + "px");
	}

	@Override
	public void onCameraSuccess() {
		imageAvailable();
	}

	@Override
	public void onCameraError() {
		imageUnavailable();
	}

	@Override
	public void showAndResize() {
		resize();
		center();
		show();
	}
}
