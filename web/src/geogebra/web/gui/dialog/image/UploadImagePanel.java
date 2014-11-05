package geogebra.web.gui.dialog.image;

import geogebra.html5.main.AppW;
import geogebra.web.gui.NoDragImage;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UploadImagePanel extends VerticalPanel {
	
	private String fileData;
	private String fileName;

	String previewHeight;
	String previewWidth;
	
	private FileUpload uploadImageBtn;
	private Image previewImg;

	private UploadImageDialog dialog;
	
	private AppW app;
	
	public UploadImagePanel(UploadImageDialog uploadImageDialog, AppW app, String previewWidth, String previewHeight) {
		this.app = app;
		this.dialog = uploadImageDialog;
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
	    initGUI();
	    initActions();
    }
	
	private void initGUI() {
		add(uploadImageBtn = new FileUpload());
	}
	
	private void initActions() {
		addChangeHandler(uploadImageBtn.getElement());
	}
	
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
						panel.@geogebra.web.gui.dialog.image.UploadImagePanel::fileSelected(Ljava/lang/String;Ljava/lang/String;)(fileStr, fileName);
					}
				};
				reader.readAsDataURL(fileToHandle);
			}
		}
	}-*/;
	
	private void fileSelected(String fileData, String fileName) {
		this.fileData = fileData;
		this.fileName = fileName;
		if (previewImg == null) {
			previewImg = new NoDragImage(fileData);
			previewImg.setHeight(previewHeight);
			previewImg.setWidth(previewWidth);
			add(previewImg);
		} else {
			previewImg.setUrl(fileData);
		}
		dialog.imageAvailable();
	}

	public String getImageDataURL() {
		return fileData;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void resetPreview() {
		if (this.previewImg != null) {
			this.remove(this.previewImg);
		}
	}

}
