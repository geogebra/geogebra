package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Input Dialog for Webcam / Document Camera
 */
public class WebcamInputDialog extends ComponentDialog
		implements WebcamDialogInterface {
	private WebCamInputPanel webcamInputPanel;

	/**
	 * @param app
	 *            application
	 * @param data
	 * 			  dialog transkeys
	 */
	public WebcamInputDialog(AppW app, DialogData data) {
		super(app, data, false, true);
		addStyleName("camera");
		buildContent();
		if (Browser.isMobile()) {
			this.setAutoHideEnabled(true);
		}
	}

	@Override
	public void onPositiveAction() {
		String dataURL = webcamInputPanel.getImageDataURL();
		String name = "webcam";
		if (dataURL != null && !webcamInputPanel.isStreamEmpty()) {
			((AppW) app).imageDropHappened(name, dataURL);
		}
	}

	private void buildContent() {
		FlowPanel contentPanel = new FlowPanel();
		contentPanel.setStyleName("mowCameraSimplePanel");
		webcamInputPanel = new WebCamInputPanel((AppW) app, this);
		contentPanel.add(webcamInputPanel);
		addDialogContent(contentPanel);
	}

	@Override
	public void hide() {
		if (this.webcamInputPanel != null) {
			this.webcamInputPanel.stopVideo();
		}
		((AppW) app).getImageManager().setPreventAuxImage(false);
		((AppW) app).getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
		super.hide();
	}

	@Override
	public void hide(boolean autoClosed, boolean setFocus) {
		super.hide(autoClosed, setFocus);
		app.getGuiManager().setMode(EuclidianConstants.MODE_SELECT_MOW,
				ModeSetter.TOOLBAR);
	}

	/**
	 * starts the video
	 */
	public void startVideo() {
		webcamInputPanel.startVideo();
	}

	@Override
	public void onCameraSuccess() {
		// not used
	}

	@Override
	public void onCameraError() {
		// not used
	}

	@Override
	public void resize() {
		// nothing to do here
	}

	@Override
	public void showAndResize() {
		show();
		center();
	}
}