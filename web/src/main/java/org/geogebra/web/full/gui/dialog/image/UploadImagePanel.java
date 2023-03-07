package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.laf.LoadSpinner;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.ui.FileUpload;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.VerticalPanel;

import elemental2.dom.File;
import elemental2.dom.FileList;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

/**
 * panel for uploading images contains preview image
 *
 */
public class UploadImagePanel extends VerticalPanel {
	private String fileData;
	private String fileName;

	private final int previewHeight;
	private final int previewWidth;

	private FileUpload uploadImageBtn;
	private Image previewImg;

	private final UploadImageDialog dialog;

	/**
	 * @param uploadImageDialog
	 *            dialog containing image upload panel
	 * @param previewWidth
	 *            width of preview image
	 * @param previewHeight
	 *            height of preview image
	 */
	public UploadImagePanel(AppW app, UploadImageDialog uploadImageDialog,
			int previewWidth, int previewHeight) {
		this.dialog = uploadImageDialog;
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
	    initGUI(app);
    }

	private void initGUI(AppW app) {
		add(uploadImageBtn = getUploadButton(app, this::fileSelected));
	}

	/**
	 * @param callback
	 *            Upload callback
	 * @return upload button
	 */
	public static FileUpload getUploadButton(AppW app, UploadImageCallback callback) {
		FileUpload upload = new FileUpload();
		Element el = upload.getElement();
		el.setAttribute("accept", "image/*");
		HTMLInputElement input = Js.uncheckedCast(el);
		app.getAppletFrame().add(upload); // needs to be in DOM to work for iPad Chrome
		input.addEventListener("change", event -> {
			if (upload.getParent() == app.getAppletFrame()) {
				upload.removeFromParent();
			}
			LoadSpinner spinner = new LoadSpinner();
			app.getAppletFrame().add(spinner);
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
				reader.addEventListener("loadend", (ev) -> {
					Log.debug("Image " + fileName + " loaded");
					if (reader.readyState == FileReader.DONE) {
						String fileStr = reader.result.asString();
						callback.insertImage(fileName, fileStr);
						spinner.removeFromParent();
					}
				});
				reader.addEventListener("error", evt -> spinner.removeFromParent());
				reader.readAsDataURL(fileToHandle);
			}
		});
		return upload;
	}

	private void fileSelected(String fName, String fData) {
		this.fileName = fName;
		this.fileData = fData;
		if (previewImg == null) {
			try {
				previewImg = new Image(fileData);
				previewImg.setWidth(previewWidth + "px");
				previewImg.setHeight(previewHeight + "px");
				add(previewImg);
			} catch (Throwable e) {
				Log.debug("ImageProblem" + e.getMessage());
			}
		} else {
			previewImg.setUrl(fileData);
		}
		dialog.imageAvailable();
	}

	/**
	 * @return image data
	 */
	public String getImageDataURL() {
		return fileData;
	}
	
	/**
	 * @return file name
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * opens the file browser
	 */
	public void openFileBrowserDirectly() {
		uploadImageBtn.click();
	}
}