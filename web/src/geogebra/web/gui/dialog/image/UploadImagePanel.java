package geogebra.web.gui.dialog.image;

import geogebra.html5.main.AppW;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

public class UploadImagePanel extends VerticalPanel {
	
	private String fileData;
	private String fileName;

	private FileUpload uploadImageBtn;
	private Image previewImg;
	private ImageInputDialog dialog;
	
	private AppW app;
	
	public UploadImagePanel(ImageInputDialog dialog, AppW app) {
		this.app = app;
		this.dialog = dialog;
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
			previewImg = new Image(fileData);
			previewImg.setHeight("155px");
			previewImg.setWidth("213px");
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

}
