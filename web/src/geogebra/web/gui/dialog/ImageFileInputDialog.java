package geogebra.web.gui.dialog;

import geogebra.web.main.Application;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FileUpload;

public class ImageFileInputDialog extends PopupPanel implements ClickHandler{

	protected Application app;

	protected FileUpload inputWidget;
	protected Button btCancel;

	public ImageFileInputDialog(Application app, boolean centering) {
	    super(false, true);
	    this.app = app;
	    createGUI();
	    setGlassEnabled(true);
	    center();
    }

	protected void createGUI() {

		inputWidget = new FileUpload();
		addGgbChangeHandler(inputWidget.getElement(), app);

		btCancel = new Button("Cancel");
		btCancel.getElement().getStyle().setMargin(3, Style.Unit.PX);
		btCancel.addClickHandler(this);

		VerticalPanel centerPanel = new VerticalPanel();
		centerPanel.add(inputWidget);
		centerPanel.add(btCancel);

		setWidget(centerPanel);
	}

	public native void addGgbChangeHandler(Element el, Application appl) /*-{
		var dialog = this;
		el.onchange = function(event) {
			var files = this.files;
			if (files.length) {
				var fileTypes = /^image.*$/;
				for (var i = 0, j = files.length; i < j; ++i) {
					if (!files[i].type.match(fileTypes)) {
						continue;
					}
					var fileToHandle = files[i];
					//code copied from Application
					var reader = new FileReader();
					reader.onloadend = function(ev) {
						if (reader.readyState === reader.DONE) {
							var reader2 = new FileReader();
							var base64result = reader.result;
							reader2.onloadend = function(eev) {
								if (reader2.readyState === reader2.DONE) {
									var fileStr = base64result;
									var fileStr2 = reader2.result;
									var fileName = fileToHandle.name;
									var coordx = 0;//e.offsetX ? e.offsetX : e.layerX;
									var coordy = 0;//e.offsetY ? e.offsetY : e.layerY;
									appl.@geogebra.web.main.Application::imageDropHappened(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)(fileName, fileStr, fileStr2, coordx, coordy);
									dialog.@geogebra.web.gui.dialog.ImageFileInputDialog::hide()();
								}
							}
							reader2.readAsBinaryString(fileToHandle);
						}
					};
					reader.readAsDataURL(fileToHandle);
					break;
				} 
			}
		};
	}-*/;

	public void onClick(ClickEvent event) {
	    if (event.getSource() == btCancel) {
	    	hide();
	    }
    }
}
