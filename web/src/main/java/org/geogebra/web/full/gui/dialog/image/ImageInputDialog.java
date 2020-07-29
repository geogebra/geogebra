package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;

public class ImageInputDialog extends UploadImageDialog
		implements WebcamDialogInterface, ClickHandler {
	private static final int PREVIEW_HEIGHT = 155;
	private static final int PREVIEW_WIDTH = 213;

	private WebCamInputPanel webcamPanel;
	private Label webcamLabel;

	/**
	 * constructor
	 * @param app see {@link AppW}
	 */
	public ImageInputDialog(AppW app) {
		super(app, PREVIEW_WIDTH, PREVIEW_HEIGHT);
		setOnNegativeAction(() -> {
			app.getImageManager().setPreventAuxImage(false);
			app.getGuiManager().setMode(EuclidianConstants.MODE_MOVE,
					ModeSetter.TOOLBAR);
		});
		setOnPositiveAction(this::positiveAction);
	}

	private void positiveAction() {
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
		((AppW) app).imageDropHappened(name, data);
	}

	@Override
	protected void buildContent() {
		super.buildContent();
		addStyleName("camera");
		if (webcamSupported()) {
			listPanel.add(webcamLabel = new Label(app.getLocalization().getMenu("Webcam")));
			webcamLabel.addClickHandler(this);
		}
	}

	protected boolean webcamSupported() {
		return Browser.supportsWebcam();
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
			webcamPanel = new WebCamInputPanel((AppW) app, this);
		} else {
			webcamPanel.startVideo();
		}

		inputPanel.setWidget(webcamPanel);
		imageAvailable();
	}

	@Override
	public void onClick(ClickEvent event) {
		Object source = event.getSource();
		if (source == upload) {
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