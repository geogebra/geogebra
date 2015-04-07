package org.geogebra.web.web.gui.dialog.image;

import org.geogebra.common.main.App;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UploadImagePanel extends VerticalPanel {
	
	private String fileData;
	private String fileName;

	int previewHeight;
	int previewWidth;
	
	/** used to reset the uploadImageBtn */
	private FormPanel panel;
	private FileUpload uploadImageBtn;
	private Image previewImg;

	private UploadImageDialog dialog;
	
	private AppW app;
	
	public UploadImagePanel(UploadImageDialog uploadImageDialog, AppW app, int previewWidth, int previewHeight) {
		this.app = app;
		this.dialog = uploadImageDialog;
		this.previewWidth = previewWidth;
		this.previewHeight = previewHeight;
	    initGUI();
	    initActions();
    }
	
	private void initGUI() {
		panel = new FormPanel();
		panel.add(uploadImageBtn = new FileUpload()); 
		add(panel);
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
						panel.@org.geogebra.web.web.gui.dialog.image.UploadImagePanel::fileSelected(Ljava/lang/String;Ljava/lang/String;)(fileStr, fileName);
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
			try{
			previewImg = new Image(fileData);
			previewImg.setWidth(previewWidth + "px");
			previewImg.setHeight(previewHeight + "px");
			add(previewImg);
			}catch(Throwable e){
				App.debug("ImageProblem"+e.getMessage());
			}
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
			this.previewImg = null;
			panel.reset();
		}
	}

}
