package org.geogebra.web.full.gui.dialog.image;

import org.geogebra.common.util.debug.Log;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	
	/** used to reset the uploadImageBtn */
	private FormPanel panel;
	private FileUpload uploadImageBtn;
	private Image previewImg;

	private final UploadImageDialog dialog;
	private UploadImageWithoutDialog uploadImageWithoutDialog;

	/**
	 * @param uploadImageDialog
	 *            dialog containing image upload panel
	 * @param previewWidth
	 *            width of preview image
	 * @param previewHeight
	 *            height of preview image
	 */
	public UploadImagePanel(UploadImageDialog uploadImageDialog,
			int previewWidth, int previewHeight) {
		this.dialog = uploadImageDialog;
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
	    initGUI();
	    initActions();
    }

	/**
	 * @param uploadImageWithoutDialog
	 *            enables file upload without dialog
	 */
	public UploadImagePanel(UploadImageWithoutDialog uploadImageWithoutDialog) {
		this(null, 0, 0);
		this.uploadImageWithoutDialog = uploadImageWithoutDialog;
	}

	private void initGUI() {
		panel = new FormPanel();
		panel.add(uploadImageBtn = new FileUpload()); 
		add(panel);
	}

	private void initActions() {
		addChangeHandler(uploadImageBtn.getElement());
	}

	/**
	 * @param el
	 *            Element
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
		if (dialog == null) {
			uploadImageWithoutDialog.insertImage();
		} else {
			dialog.imageAvailable();
		}
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
	 * reset the preview image
	 */
	public void resetPreview() {
		if (this.previewImg != null) {
			this.remove(this.previewImg);
			this.previewImg = null;
			panel.reset();
		}
	}

	/**
	 * opens the file browser
	 */
	public void openFileBrowserDirectly() {
		uploadImageBtn.click();
	}
}