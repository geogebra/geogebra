package org.geogebra.web.full.gui.dialog.image;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.webcam.WebcamDialogInterface;
import org.geogebra.web.shared.components.dialog.ComponentDialog;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.geogebra.web.shared.components.infoError.ComponentInfoErrorPanel;
import org.geogebra.web.shared.components.infoError.InfoErrorData;
import org.geogebra.web.shared.components.tab.ComponentTab;
import org.geogebra.web.shared.components.tab.TabData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;

public class ImageDialog extends ComponentDialog implements WebcamDialogInterface {
	private FlowPanel cameraPanel;
	private WebCamInputPanel webcamInputPanel;
	private StandardButton captureBtn;
	private ComponentTab tab;

	/**
	 * base dialog constructor
	 * @param app - see {@link AppW}
	 * @param dialogData - contains trans keys for title and buttons
	 */
	public ImageDialog(AppW app, DialogData dialogData) {
		super(app, dialogData, false, true);
		addStyleName("imageDialog");
		buildGUI();
	}

	private void buildGUI() {
		FileUpload uploadImage = UploadImagePanel.getUploadButton((AppW) app,
				(fileName, content) -> {
					((AppW) app).imageDropHappened(fileName, content);
					hide();
				});

		InfoErrorData uploadData = new InfoErrorData(null, "ImageDialog.UploadImageMsg",
				"ImageDialog.Browse");
		ComponentInfoErrorPanel uploadPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				uploadData, MaterialDesignResources.INSTANCE.upload(),
				uploadImage::click);
		TabData uploadTab = new TabData("Upload", uploadPanel);

		cameraPanel = new FlowPanel();
		loadCameraPanel();
		TabData cameraTab = new TabData("Camera", cameraPanel);

		tab = new ComponentTab(new ArrayList<>(Arrays.asList(uploadTab, cameraTab)),
				app.getLocalization());
		addDialogContent(tab);
		webcamInputPanel.startVideo();
	}

	private FlowPanel getErrorPanel(String title, String msg) {
		InfoErrorData cameraData = new InfoErrorData(title, msg, null);
		ComponentInfoErrorPanel cameraPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				cameraData, MaterialDesignResources.INSTANCE.no_camera(), null);
		return cameraPanel;
	}

	private void loadCameraPanel() {
		if (webcamInputPanel == null) {
			webcamInputPanel = new WebCamInputPanel((AppW) app, this);
		}

		if (captureBtn == null) {
			initCaptureBtn();
		}

		cameraPanel.clear();
		cameraPanel.setStyleName("cameraPanel");
		cameraPanel.add(webcamInputPanel);
		cameraPanel.add(captureBtn);
	}

	private void initCaptureBtn() {
		captureBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.camera_white(), null, 24);
		captureBtn.setStyleName("mowFloatingButton");
		AriaHelper.setTitle(captureBtn, app.getLocalization().getMenu("ImageDialog.Capture"));

		captureBtn.addFastClickHandler(source -> {
			String dataURL = webcamInputPanel.getImageDataURL();
			String name = "webcam";
			if (dataURL != null) {
				((AppW) app).imageDropHappened(name, dataURL);
			}
			hide();
		});
	}

	@Override
	public void resize() {
		onResize();
	}

	@Override
	public void showAndResize() {
		super.onResize();
		super.show();
	}

	@Override
	public void onCameraSuccess() {
		loadCameraPanel();
	}

	@Override
	public void onCameraError(String title, String msg) {
		cameraPanel.addStyleName("error");
		cameraPanel.clear();
		cameraPanel.add(() -> getErrorPanel(title, msg));
	}

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(() -> tab.switchToTab(0));
	}

	@Override
	public void hide() {
		if (this.webcamInputPanel != null) {
			this.webcamInputPanel.stopVideo();
		}
		super.hide();
	}

	@Override
	public void onResize() {
		super.onResize();
		tab.onResize();
		if (!cameraPanel.getStyleName().contains("error")) {
			cameraPanel.getElement().getStyle().setHeight(webcamInputPanel.getOffsetHeight() + 28,
					Style.Unit.PX);
		}
	}
}
