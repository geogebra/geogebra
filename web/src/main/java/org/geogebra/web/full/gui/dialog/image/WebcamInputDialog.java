package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.kernel.ModeSetter;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;
import org.geogebra.web.shared.components.ComponentDialog;
import org.geogebra.web.shared.components.DialogData;

import com.google.gwt.event.logical.shared.ResizeEvent;
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
	public void onResize(ResizeEvent resizeEvent) {
		resize();
	}

	@Override
	public void resize() {
		if (!isShowing()) {
			return;
		}
		double width = webcamInputPanel.getVideoWidth();
		double height = webcamInputPanel.getVideoHeight();
		double ratio = height / width;
		if (app.getHeight() < app.getWidth()) {
			height = app.getHeight() / 2.5;
			width = height / ratio;
			if (width < 250) {
				width = 250;
				height = width * ratio;
			}
		} else {
			width = Math.max(250, app.getWidth() / 2.5);
			height = width * ratio;
		}
		webcamInputPanel.getParent().setHeight(height + "px");
		webcamInputPanel.getParent().setWidth(width + "px");
		center();
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

	@Override
	public void center() {
		super.center();
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
	public void showAndResize() {
		show();
		resize();
		center();
	}
}