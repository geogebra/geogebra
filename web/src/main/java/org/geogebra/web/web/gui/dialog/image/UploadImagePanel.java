package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.util.debug.Log;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * panel for uploading images contains preview image
 *
 */
public class UploadImagePanel extends VerticalPanel {
	
	private String fileData;
	private String fileName;

	private int previewHeight;
	private int previewWidth;
	
	/** used to reset the uploadImageBtn */
	private FormPanel panel;
	private FileUpload uploadImageBtn;
	private Image previewImg;

	private UploadImageDialog dialog;
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
	public native void addChangeHandler(Element el) /*-{
		var panel = this;
		el.setAttribute("accept", "image/*");
		el.onchange = function(event) {
			var fileToHandle = null;
			var files = this.files;
			if (files.length) {
				var fileTypes = /^image.*$/;
				for (var i = 0, j = files.length; i < j; ++i) {
					if (!files[i].type.match(fileTypes)) {
						continue;
					}
					fileToHandle = files[i];
					break;
				}
			}
			if (fileToHandle != null) {
				var reader = new FileReader();
				var fileName = fileToHandle.name;
				reader.onloadend = function(ev) {
					if (reader.readyState === reader.DONE) {
						var fileStr = reader.result;
						panel.@org.geogebra.web.web.gui.dialog.image.UploadImagePanel::fileSelected(Ljava/lang/String;Ljava/lang/String;)(fileStr, fileName);
					}
				};
				reader.readAsDataURL(fileToHandle);
			}
		}
	}-*/;

	private void fileSelected(String fData, String fName) {
		this.fileData = fData;
		this.fileName = fName;
		if (previewImg == null) {
			try{
			previewImg = new Image(fileData);
			previewImg.setWidth(previewWidth + "px");
			previewImg.setHeight(previewHeight + "px");
			add(previewImg);
			}catch(Throwable e){
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
