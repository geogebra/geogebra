package org.geogebra.web.full.gui.dialog.image;

import java.util.ArrayList;
import java.util.Arrays;

import org.geogebra.web.full.css.MaterialDesignResources;
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
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;

import elemental2.dom.File;
import elemental2.dom.FileList;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

public class ImageDialog extends ComponentDialog implements WebcamDialogInterface {
	private FileUpload uploadImage;
	private String fileData;
	private String fileName;
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
		uploadImage = new FileUpload();
		addChangeHandler(uploadImage.getElement());

		InfoErrorData uploadData = new InfoErrorData(null, "ImageDialog.UploadImageMsg",
				"ImageDialog.Browse");
		ComponentInfoErrorPanel uploadPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				uploadData, MaterialDesignResources.INSTANCE.upload(),
				() -> uploadImage.click());
		TabData uploadTab = new TabData("Upload", uploadPanel);

		cameraPanel = new FlowPanel();
		cameraPanel.addStyleName("cameraPanel");
		webcamInputPanel = new WebCamInputPanel((AppW) app, this);
		webcamInputPanel.startVideo();
		cameraPanel.add(webcamInputPanel);

		captureBtn = new StandardButton(
				MaterialDesignResources.INSTANCE.camera_white(), null, 24);
		captureBtn.setStyleName("mowFloatingButton");
		captureBtn.addFastClickHandler(source -> {
			String dataURL = webcamInputPanel.getImageDataURL();
			String name = "webcam";
			if (dataURL != null) {
				((AppW) app).imageDropHappened(name, dataURL);
			}
			hide();
		});
		cameraPanel.add(captureBtn);
		TabData cameraTab = new TabData("Camera", cameraPanel);

		tab = new ComponentTab(new ArrayList<>(Arrays.asList(uploadTab, cameraTab)),
				app.getLocalization());
		addDialogContent(tab);
		webcamInputPanel.startVideo();
	}

	private FlowPanel getErrorPanel() {
		InfoErrorData cameraData = new InfoErrorData("Webcam.Denied.Caption",
				"Webcam.Denied.Message", null);
		ComponentInfoErrorPanel cameraPanel = new ComponentInfoErrorPanel(app.getLocalization(),
				cameraData, MaterialDesignResources.INSTANCE.no_camera(), null);
		return cameraPanel;
	}

	/**
	 * @param el - Element
	 */
	public void addChangeHandler(Element el) {
		el.setAttribute("accept", "image/*");
		HTMLInputElement input = Js.uncheckedCast(el);
		input.addEventListener("change", event -> {
			File fileToHandle = null;
			FileList files = input.files;
			if (files.length > 0) {
				for (int i = 0, j = files.length; i < j; ++i) {
					if (files.item(i).type.startsWith("image")) {
						fileToHandle = files.item(i);
						break;
					}
				}
			}
			if (fileToHandle != null) {
				FileReader reader = new FileReader();
				String fileName = fileToHandle.name;
				reader.onloadend = (ev) -> {
					if (reader.readyState == FileReader.DONE) {
						String fileStr = reader.result.asString();
						fileSelected(fileStr, fileName);
					}
					return null;
				};
				reader.readAsDataURL(fileToHandle);
			}
		});
	}

	private void fileSelected(String fData, String fName) {
		this.fileData = fData;
		this.fileName = fName;
		((AppW) app).imageDropHappened(fileName, fileData);
		hide();
	}

	@Override
	public void resize() {

	}

	@Override
	public void showAndResize() {

	}

	@Override
	public void onCameraSuccess() {
		cameraPanel.removeStyleName("error");
		cameraPanel.clear();
		cameraPanel.add(webcamInputPanel);
		webcamInputPanel.startVideo();
		cameraPanel.add(captureBtn);
	}

	@Override
	public void onCameraError() {
		cameraPanel.addStyleName("error");
		cameraPanel.clear();
		cameraPanel.add(this::getErrorPanel);
	}

	@Override
	public void show() {
		super.show();
		Scheduler.get().scheduleDeferred(() -> tab.switchToTab(0));
	}

	@Override
	public void hide() {
		webcamInputPanel.stopVideo();
		super.hide();
	}
}
